package osgi.enroute.examples.component.examples;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property=EventConstants.EVENT_TOPIC+"=osgi/enroute/*")
public class EventListener implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		System.out.println("Event: " + event.getTopic());
	}
}
