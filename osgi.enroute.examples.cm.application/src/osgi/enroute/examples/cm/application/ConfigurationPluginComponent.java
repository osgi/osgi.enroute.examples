package osgi.enroute.examples.cm.application;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(name = "plugin", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ConfigurationPluginComponent implements ConfigurationPlugin {

	@Override
	public void modifyConfiguration(ServiceReference<?> reference,
			Dictionary<String, Object> properties) {

		properties.put("for.service",
				reference.getProperty(Constants.SERVICE_ID));
	}

}
