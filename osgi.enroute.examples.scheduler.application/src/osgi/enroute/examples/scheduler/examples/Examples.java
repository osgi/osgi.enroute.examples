package osgi.enroute.examples.scheduler.examples;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;

import osgi.enroute.examples.scheduler.application.Tooltip;
import osgi.enroute.scheduler.api.CancelException;
import osgi.enroute.scheduler.api.CancellablePromise;
import osgi.enroute.scheduler.api.CronJob;
import osgi.enroute.scheduler.api.Scheduler;
import osgi.enroute.scheduler.api.TimeoutException;

/**
 * This class shows many examples of what you can do with the scheduler.
 */
@Component(service = Examples.class)
public class Examples {
	// 2015-01-13T09:54:42.820Z
	static DateTimeFormatter ISODATE = DateTimeFormatter.ISO_DATE_TIME;

	/**
	 * Delay for 1 second with milliseconds using a Promise
	 */

	/**
	 * Delay for 1 second with milliseconds using a Callable
	 */

	@Tooltip(description = "Schedule a callback to resolve in ms", type = "number", deflt = "2000")
	public CancellablePromise<String> afterCallbackMs(int id, int timeout)
			throws Exception {

		CancellablePromise<String> promise = scheduler.after(() -> timeout
				+ " ms", timeout < 1000 ? 1000 : timeout);
		
		promise.then((p) -> {
			out.println("Finished afterWithCallback " + id);
			return null;
		}, failure);

		return promise;
	}

	/**
	 * Execute at a given time with a Promise
	 */

	@Tooltip(description = "Schedule a promise to resolve at a certain date-time", type = "datetime-local", deflt = "2015-01-13T09:54:42.820Z")
	public CancellablePromise<Instant> atWithPromise(int id, String parameter)
			throws Exception {
		
		LocalDateTime localDateTime = LocalDateTime.parse(parameter, ISODATE);
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
		Instant instant = zonedDateTime.toInstant();
		
		CancellablePromise<Instant> promise = scheduler.at(instant);
		String actual = parameter;

		promise.then((p) -> {
			out.println("Finished atWithPromise " + id + " " + actual);
			return null;
		}, failure);

		return promise;
	}

	/**
	 * Execute a periodic schedule using milliseconds
	 */

	@Tooltip(description = "Schedule by period. You can specify a comma separated lists of ms.", type = "text", deflt = "100, 200, 300, 400, 500")
	public CancellablePromise<?> schedulePeriodic(int id, long periods[],
			Callable<Void> callable) throws Exception {
		Closeable c = scheduler.schedule(
				() -> {

					out.println("Fired periodic " + id + " "
							+ Arrays.toString(periods));

					callable.call();

				}, periods[0], Arrays.stream(periods).skip(1).toArray());

		return new Closer<Object>(c);
	}

	/**
	 * Execute a cron schedule
	 */

	@Tooltip(description = "Schedule a callback with a cron expression.", deflt = "0-30/2 * * * * ?", type = "text")
	public CancellablePromise<?> scheduleCron(int id, String cronExpression,
			Callable<Void> callable) throws Exception {

		String actual = cronExpression == null ? "0-9/2 * * * * ?"
				: cronExpression;

		Closeable c = scheduler.schedule(() -> {

			out.println("Fired cron " + id + " " + actual);

			callable.call();

		}, actual);

		return new Closer<Object>(c);
	}

	/**
	 * Register a service with a cron schedule
	 */
	interface CronData {
		String message();
	}

	@Tooltip(description = "Register a service with a cron expression.", deflt = "30/2 * * * * ?", type = "text")
	public CancellablePromise<?> cronService(int id, String cronExpression,
			Callable<Void> callable) {

		//
		// If you use the generic version, don't use Lambdas
		CronJob<CronData> job = new CronJob<Examples.CronData>() {
			public void run(CronData cronData) throws Exception {
				out.println("Fired cron service " + id + cronData.message());
				callable.call();
			}
		};

		@SuppressWarnings("rawtypes")
		ServiceRegistration<CronJob> registration = context.registerService(
				CronJob.class, job, new Hashtable<String, Object>() {
					private static final long serialVersionUID = 1L;

					{
						put(CronJob.CRON, cronExpression);
					}
				});

		return new Closer<Object>(() -> registration.unregister());
	}

	/**
	 * Create a configuration that kicks of a component with a Cron schedule
	 * CronScheduleComponent
	 * 
	 * @throws IOException
	 */

	@Tooltip(description = "Create a configuration for the the ConfiguredCronScheduleComponent component. The service will stay registered until you cancel it.", deflt = "message=foo\n0/5 * * * * ?", type = "text")
	public CancellablePromise<?> cronComponent(int id, String cronExpression)
			throws IOException {
		Configuration config = cm
				.getConfiguration(ConfiguredCronScheduleComponent.PID);
		Hashtable<String, Object> d = new Hashtable<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put(CronJob.CRON, cronExpression);
				put("id", id);
			}
		};
		config.update(d);

		return new Closer<Object>(() -> config.delete());
	}

	/**
	 * Enable the FixedCronScheduleComponent service.
	 * 
	 * @throws Exception
	 * 
	 */

	@Tooltip(description = "Enable the FixedCronScheduleComponent component with a fixed 'cron' service property of 1-30/2 * * * * ?, firing every 2 second the first 30 seconds of the minute", deflt = "", type = "")
	public CancellablePromise<?> fixedSchedule(int id, String cronExpression)
			throws Exception {

		assert fsc.get() == null : "This is a singleton, so we can only have one";
		if (fsc.get() != null)
			throw new Exception("Already started");

		FixedCronScheduleComponent.id = id;
		componentContext.enableComponent(FixedCronScheduleComponent.class
				.getName());

		return new Closer<Object>(
				() -> componentContext
						.disableComponent(FixedCronScheduleComponent.class
								.getName()));
	}

	/**
	 * Make a promise that times out
	 */

	@Tooltip(description = "Create a Cancellable Promise that times out after a number of ms", deflt = "2000", type = "number")
	public CancellablePromise<Void> before(int id, int ms) {
		Deferred<Void> deferred = new Deferred<Void>();
		Promise<Void> promise = deferred.getPromise();

		CancellablePromise<Void> before = scheduler.before(promise,
				ms >= 1000 ? ms : 1000);

		before.then((p) -> {
			assert false : "Should never be resolved";
			return null;
		}, (p) -> {
			if (p.getFailure() == TimeoutException.SINGLETON) {
				out.println("Timed out " + id);
			} else if (p.getFailure() == CancelException.SINGLETON) {
				out.println("Got canceled " + id);
			} else
				assert false : "Should not happen since we never fail it";
		});

		return before;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// HELPER CODE
	// ////////////////////////////////////////////////////////////////////////////////

	@Reference
	private Scheduler scheduler;
	private PrintStream out = System.out;
	private Failure failure = (p) -> out.println("Failure");
	private BundleContext context;
	@Reference
	private ConfigurationAdmin cm;
	private ComponentContext componentContext;
	private AtomicReference<FixedCronScheduleComponent> fsc = new AtomicReference<>();


	@Activate
	void activate(ComponentContext cc) {
		context = cc.getBundleContext();
		componentContext = cc;
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, target = "(component.name=*FixedCronScheduleComponent)")
	void addFixed(CronJob<?> fsc) {
		if (fsc instanceof FixedCronScheduleComponent)
			this.fsc.set((FixedCronScheduleComponent) fsc);
	}

	void removeFixed(CronJob<?> fsc) {
		if (fsc instanceof FixedCronScheduleComponent)
			this.fsc.compareAndSet((FixedCronScheduleComponent) fsc, null);
	}
}
