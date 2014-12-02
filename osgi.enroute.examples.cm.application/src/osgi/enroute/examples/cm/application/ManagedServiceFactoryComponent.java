package osgi.enroute.examples.cm.application;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * An Example Managed Service Factory
 *
 * Using a component for this purpose is a bit bizarre since you get
 * configuration through the activate method in a component. However, this is
 * for demo purposes only.
 */
@Component(name="factory", property="service.pid=factory", configurationPolicy=ConfigurationPolicy.IGNORE)
public class ManagedServiceFactoryComponent implements ManagedServiceFactory {
	
	@Override
	public String getName() {
		return "factory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties)
			throws ConfigurationException {
		System.out.println("Updated factory " + pid + " -> " + properties);		
	}

	@Override
	public void deleted(String pid) {
		System.out.println("Deleted factory " + pid );		
	}

	
}
