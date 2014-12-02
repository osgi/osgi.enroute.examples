package osgi.enroute.examples.cm.application;

import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
/**
 * Example Configuration Listener
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ConfigurationListenerComponent implements ConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		System.out.println("Configuration Event " + getType(event.getType())
				+ event.getFactoryPid() + "::" + event.getPid());
	}

	private String getType(int type) {
		switch (type) {
		case ConfigurationEvent.CM_DELETED:
			return "deleted";
		case ConfigurationEvent.CM_LOCATION_CHANGED:
			return "location";
		case ConfigurationEvent.CM_UPDATED:
			return "updated";
		default:
			return "?";
		}
	}

}
