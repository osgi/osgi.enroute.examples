package osgi.enroute.examples.cm.application;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(name = "singleton", property = "service.pid=singleton", configurationPolicy = ConfigurationPolicy.IGNORE)
public class ManagedServiceComponent implements ManagedService {

	@Override
	public void updated(Dictionary<String, ?> properties)
			throws ConfigurationException {
		System.out.println("Updated singleton " + properties);
	}

}
