package osgi.enroute.examples.cm.examples;

import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Example Configuration Listener. This listener will print out the events on
 * the console.
 */
@Component(name = "listener", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ConfigurationListenerExample implements ConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		System.out.println("Configuration Event "
				+ getType(event.getType())
				+ " "
				+ (event.getFactoryPid() != null ? event.getFactoryPid() + "::"
						: "") + event.getPid());
	}

	private String getType(int type) {
		switch (type) {
		case ConfigurationEvent.CM_DELETED:
			return "deleted";
		case ConfigurationEvent.CM_LOCATION_CHANGED:
			return "location changed";
		case ConfigurationEvent.CM_UPDATED:
			return "updated";
		default:
			return "?";
		}
	}

}
