package osgi.enroute.iot.domotica.command;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class DomoticaCommand {

	@Activate
	void activate() {
		System.out.println("Hello World");
	}

	@Deactivate
	void deactivate() {
		System.out.println("Goodbye World");
	}
}
