package osgi.enroute.examples.logging.application;

import java.io.Closeable;
import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.debug.api.Debug;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.scheduler.api.Scheduler;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "osgi.enroute.examples.logging", property = { Debug.COMMAND_SCOPE + "=logging", //
		Debug.COMMAND_FUNCTION + "=error", //
		Debug.COMMAND_FUNCTION + "=warning", //
		Debug.COMMAND_FUNCTION + "=info", //
		Debug.COMMAND_FUNCTION + "=debug", //
		Debug.COMMAND_FUNCTION + "=audit", //
}

)
public class LoggingApplication implements REST {
	static Logger		logger	= LoggerFactory.getLogger(LoggingApplication.class);

	@Reference
	Scheduler			scheduler;

	@Reference
	Logger				service;

	@Reference
	LogService			log;

	private Closeable	schedule;

	@Reference
	void setLogReader(LogReaderService reader) {
		reader.addLogListener(e -> {
			switch (e.getLevel()) {
			case LogService.LOG_DEBUG:
				System.out.println("DEBUG:::: " + e.getMessage());
				break;

			case LogService.LOG_INFO:
				System.out.println("INFO::::: " + e.getMessage());
				break;
			case LogService.LOG_WARNING:
				System.out.println("WARNING:: " + e.getMessage());
				break;
			case LogService.LOG_ERROR:
				System.out.println("ERROR:::: " + e.getMessage());
				break;
			}
		});
	}

	@Activate
	void activate() throws Exception {
		schedule = scheduler.schedule(() -> {
			log.log(LogService.LOG_INFO, "From scheduler");
		}, 10000, 10000);
	}

	@Deactivate
	void deactivate() throws IOException {
		schedule.close();
	}

	public String getUpper(String string) {
		service.warn("To Upper " + string);
		return string.toUpperCase();
	}

	
	public void error(String msg) {
		logger.error(msg);
	}

	public void warning(String msg) {
		logger.warn(msg);
	}

	public void debug(String msg) {
		logger.debug(msg);
	}

	public void info(String msg) {
		logger.info(msg);
	}
}
