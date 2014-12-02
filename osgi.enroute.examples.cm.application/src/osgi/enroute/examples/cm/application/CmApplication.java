package osgi.enroute.examples.cm.application;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.EventAdminSSEEndpoint;
import osgi.enroute.capabilities.WebServerExtender;

@AngularWebResource.Require
@BootstrapWebResource.Require
@WebServerExtender.Require
@ConfigurerExtender.Require
@EventAdminSSEEndpoint.Require
@Component(name = "osgi.enroute.examples.cm", service = CmApplication.class)
public class CmApplication {
	ConfigurationAdmin cm;

	<K, V> Map<K, V> toMap(Dictionary<K, V> properties, Map<K, V> map) {
		if (properties != null) {
			for (Enumeration<K> e = properties.keys(); e.hasMoreElements();) {
				K key = e.nextElement();
				map.put(key, properties.get(key));
			}
		}
		return map;
	}

	Map<String, Object> toMap(Dictionary<String, Object> properties) {
		return toMap(properties, new HashMap<>());
	}

	@Reference
	void setCm(ConfigurationAdmin cm) {
		this.cm = cm;
	}

}
