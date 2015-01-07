package osgi.enroute.examples.component.examples;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

@Component
public class LogExampleComponent {
	private LogService log;
	
	@Activate
	void activate() {
		log.log(LogService.LOG_INFO, "Hello Lustrous Point!");
	}
	
	@Deactivate
	void deactivate() {
		log.log(LogService.LOG_INFO, "Goodbye!");
	}
	
	@Reference
	void setLog(LogService log) {
		this.log = log;
	}
}
