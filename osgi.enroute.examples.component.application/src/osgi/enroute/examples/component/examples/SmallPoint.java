package osgi.enroute.examples.component.examples;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component
public class SmallPoint {

	@Activate
	void activate() {
		System.out.println("Hello Lustrous Point!");
	}
	
}
