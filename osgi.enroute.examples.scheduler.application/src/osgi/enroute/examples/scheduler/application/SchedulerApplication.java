package osgi.enroute.examples.scheduler.application;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.configurer.capabilities.RequireConfigurerExtender;
import osgi.enroute.dto.api.DTOs;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.examples.scheduler.examples.Examples;
import osgi.enroute.github.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.scheduler.api.CancelException;
import osgi.enroute.scheduler.api.CancellablePromise;
import osgi.enroute.scheduler.api.TimeoutException;
import osgi.enroute.stackexchange.pagedown.webresource.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

/**
 * This application demonstrates the use of the OSGi enRoute scheduler.
 * 
 */
@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequireEventAdminServerSentEventsWebResource
@RequirePagedownWebResource(resource = "enmarkdown.js")
@Component(name = "osgi.enroute.examples.scheduler", service = SchedulerApplication.class)
public class SchedulerApplication {
	static Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);
	public final static String TOPIC = "osgi/enroute/examples/scheduler";

	/*
	 * Maintains an executed command until it is canceled.
	 */
	public static class Tracker extends DTO {
		public int id;
		public long created = System.currentTimeMillis();
		public long modified;
		public List<Integer> ticks = new ArrayList<>();
		public String tooltip;
		public long ended;
		public String method;
		public String parameter;
		public Object value;
		public String failure;
		public TrackerEvent lastEvent;

		CancellablePromise<?> promise;
	}

	/*
	 * Maintains the information about a command
	 */
	public static class ExampleInfo extends DTO {
		public String description;
		public String type;
		public String deflt;
	}

	private Map<String, Method> examplesMap;
	private AtomicInteger n = new AtomicInteger(1000);
	@Reference
	private EventAdmin ea;
	@Reference
	private DTOs dtos;
	private Map<Integer, Tracker> trackers = new ConcurrentHashMap<>();
	private Examples examples;

	/*
	 * Execute a command and create tracker to allow it to be canceled.
	 */
	public Tracker createTracker(String method, String parameter) throws Exception {

		Method m = examplesMap.get(method);
		if (m == null)
			return null;

		Tooltip t = m.getAnnotation(Tooltip.class);
		if (t != null && ((parameter == null || parameter.isEmpty()))) {
			parameter = t.deflt();
		}

		Tracker tracker = new Tracker();

		tracker.id = n.getAndIncrement();
		tracker.method = method;
		tracker.parameter = parameter;

		trackers.put(tracker.id, tracker);

		//
		// We accept comma separated lists
		// so we need to turn those into
		// an array
		//

		Object source = parameter;
		if (parameter.indexOf(',') > 0)
			source = parameter.split("\\s*,\\s*");

		Object parm = dtos.convert(source).to(m.getGenericParameterTypes()[1]);

		//
		// Check if we have a command with a callable
		// or none. The callable will execute a tick
		//

		if (m.getParameterTypes().length == 3) {

			Callable<Void> tick = () -> {
				tick(tracker);
				return null;
			};
			tracker.promise = (CancellablePromise<?>) m.invoke(examples, tracker.id, parm, tick);

		} else {

			tracker.promise = (CancellablePromise<?>) m.invoke(examples, tracker.id, parm);

		}

		//
		// Register callbacks for any failure
		// or success
		//

		tracker.promise.then((x) -> {
			tracker.value = x.getValue();
			event(TrackerEvent.RESOLVED, tracker);
			return null;
		} , (x) -> {
			tracker.failure = x.getFailure().toString();

			//
			// Parse the timeout and cancelation
			// from general failure
			//

			if (x.getFailure() == TimeoutException.SINGLETON)
				event(TrackerEvent.TIMEOUT, tracker);
			else if (x.getFailure() == CancelException.SINGLETON)
				event(TrackerEvent.CANCELED, tracker);
			else
				event(TrackerEvent.FAILED, tracker);
		});

		event(TrackerEvent.CREATED, tracker);
		return tracker;
	}

	/*
	 * Create a tick for this tracker.
	 */
	void tick(Tracker tracker) throws Exception {
		int delta = (int) (tracker.modified = System.currentTimeMillis() - tracker.created);
		tracker.ticks.add(delta);
		event(TrackerEvent.TICKED, tracker);
	}

	/*
	 * Return the tracker for the give id
	 */
	public Tracker getTracker(int id) {
		return trackers.get(id);
	}

	/*
	 * Remove a tracker with a given id
	 */
	public Tracker removeTracker(int id) throws Exception {
		Tracker remove = trackers.remove(id);
		if (remove != null) {
			remove.promise.cancel();
			event(TrackerEvent.DELETED, remove);
		}
		return remove;
	}

	/*
	 * Create an event for Event Admin
	 */
	private void event(TrackerEvent subject, Tracker tracker) throws Exception {
		tracker.modified = System.currentTimeMillis();
		if (subject == TrackerEvent.CREATED && tracker.lastEvent != null)
			return;

		tracker.lastEvent = subject;
		Event event = new Event(TOPIC + "/" + subject, dtos.asMap(tracker));
		ea.postEvent(event);
	}

	/*
	 * Return the list of commands
	 */
	public Map<String, ExampleInfo> examples() {
		return examplesMap.values().stream().collect(Collectors.toMap((m) -> m.getName(), this::getExampleInfo));
	}

	/*
	 * Turn a command into some more information about the command
	 */
	private ExampleInfo getExampleInfo(Method m) {
		ExampleInfo ei = new ExampleInfo();
		Tooltip tooltip = m.getAnnotation(Tooltip.class);
		if (tooltip != null) {
			ei.deflt = tooltip.deflt();
			ei.type = tooltip.type();
			ei.description = tooltip.description();
		} else
			ei.description = m.getName();
		return ei;
	}

	/*
	 * Called to create an tick event on a give id.
	 */
	public void fire(int id) throws Exception {
		Tracker tracker = getTracker(id);
		if (tracker != null)
			tick(tracker);
		else
			logger.error("Unknown id " + id);
	}

	/////////////////////////////////////////////////////////////// housekeeping

	@Reference
	void setExample(Examples ex) {
		this.examples = ex;
		examplesMap = Arrays.stream(ex.getClass().getMethods()).filter(this::isExample)
				.collect(Collectors.toMap((m) -> m.getName(), (m) -> m));
	}

	boolean isExample(Method m) {
		boolean yes = !Modifier.isStatic(m.getModifiers()) && !Modifier.isAbstract(m.getModifiers())
				&& m.getDeclaringClass() != Object.class;
		if (!yes)
			return false;

		if (!CancellablePromise.class.isAssignableFrom(m.getReturnType()))
			return false;

		Class<?>[] types = m.getParameterTypes();
		if (types.length > 3)
			return false;

		switch (types.length) {
		case 3:
			if (!Callable.class.isAssignableFrom(types[2]))
				return false;

		case 2:

		case 1:
			if (int.class != types[0])
				return false;

		}
		return true;
	}

}
