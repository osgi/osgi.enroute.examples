package osgi.enroute.examples.component.examples;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.scheduler.api.Scheduler;

@Component
public class Ping {

	private EventAdmin admin;
	private Scheduler scheduler;
	private Closeable schedule;

	@Activate
	void activate() throws Exception {
		Event event = new Event("osgi/enroute/ping", new HashMap<>());
		schedule = scheduler.schedule(()-> admin.postEvent(event), 1000);
	}
	
	@Deactivate
	void deactivate() throws IOException {
		schedule.close();
	}

	@Reference
	void setEventAdmin(EventAdmin admin) {
		this.admin = admin;
	}

	@Reference
	void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
}
