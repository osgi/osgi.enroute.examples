package osgi.enroute.examples.cm.application;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * An example Configuration Plugin
 * 
 */
@Component(name = "plugin", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ConfigurationPluginComponent implements ConfigurationPlugin {

	private String msg;
	
	@Activate
	void activate(Map<String,Object> map) {
		msg = (String) map.get("msg");
	}
	@Override
	public void modifyConfiguration(ServiceReference<?> reference,
			Dictionary<String, Object> properties) {

		properties.put("for.service",
				reference.getProperty(Constants.SERVICE_ID));
		properties.put("msg", msg);
	}

}
