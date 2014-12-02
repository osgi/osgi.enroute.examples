package osgi.enroute.examples.cm.application;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * An Example Managed Service
 *
 * Using a component for this purpose is a bit bizarre since you get
 * configuration through the activate method in a component. However, this is
 * for demo purposes only.
 */
@Component(name = "singleton", property = "service.pid=singleton", configurationPolicy = ConfigurationPolicy.IGNORE)
public class ManagedServiceComponent implements ManagedService {

	@Override
	public void updated(Dictionary<String, ?> properties)
			throws ConfigurationException {
		System.out.println("Updated singleton " + properties);
	}

}
