package osgi.enroute.examples.scheduler.examples;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.examples.scheduler.application.SchedulerApplication;
import osgi.enroute.scheduler.api.CronJob;

/**
 * An example of a component that has a pre-defined property controlling its
 * cron schedule.
 */
@Component(enabled = false, property = CronJob.CRON + "=1-30/2 * * * * ?")
public class FixedCronScheduleComponent implements CronJob<Object> {

	public static int id;
	private SchedulerApplication app;
	private int fixedId;

	@Override
	public void run(Object data) throws Exception {
		System.out.println("Fixed Fired Cron Component ");
		app.fire(fixedId);
	}

	@Reference
	void setApp(SchedulerApplication app) {
		this.app = app;
		this.fixedId = id;
	}

}
