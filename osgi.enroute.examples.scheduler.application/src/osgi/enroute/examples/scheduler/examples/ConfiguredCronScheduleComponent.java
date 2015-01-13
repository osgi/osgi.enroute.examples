package osgi.enroute.examples.scheduler.examples;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.examples.scheduler.application.SchedulerApplication;
import osgi.enroute.scheduler.api.CronJob;

/**
 * This component is an example for a component that receives its schedule
 * through Configuration Admin. If the PID {@value PID} is set, it will creat
 * this component. The {@value CronJob#CRON} service property should be set with
 * a Cron schedule.
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = ConfiguredCronScheduleComponent.PID)
public class ConfiguredCronScheduleComponent implements
		CronJob<ConfiguredCronScheduleComponent.CronData> {

	public static final String PID = "cron.pid";

	/**
	 * The cron expression can be preceded by properties. These properties can
	 * be mapped to this interface. An instance of this interface is then passed
	 * to the {@link CronJob}.
	 */
	public interface CronData {
		/**
		 * A message. For example:
		 * 
		 * <pre>
		 *   message=foo
		 *   * * * * * ?
		 * </pre>
		 */
		String message();
	}

	/**
	 * Define the schema for the configuration data.
	 */
	public interface Configuration {
		/**
		 * The tracker id
		 */
		int id();

		/**
		 * The cron expression
		 */
		String cron();
	}

	private SchedulerApplication app;
	private DTOs dtos;
	private int id;
	private Configuration configuration;

	/*
	 * Activate
	 */
	@Activate
	void activate(Map<String, Object> map) throws Exception {
		configuration = dtos.convert(map).to(Configuration.class);
		this.id = configuration.id();

		assert id != 0 : "This component requires an id";
	}

	/*
	 * This method is called as indicated by the cron expression.
	 */
	@Override
	public void run(CronData data) throws Exception {
		System.out.println("Fired Cron Component " + id + " "
				+ configuration.cron());
		app.fire(id);
	}

	@Reference
	void setApp(SchedulerApplication app) {
		this.app = app;
	}

	@Reference
	public void setDtos(DTOs dtos) {
		this.dtos = dtos;
	}
}
