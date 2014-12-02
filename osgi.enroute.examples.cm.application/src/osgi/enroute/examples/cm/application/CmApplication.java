package osgi.enroute.examples.cm.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.dto.DTO;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.EventAdminSSEEndpoint;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.dto.api.DTOs;
import osgi.enroute.dto.api.TypeReference;

/**
 * CM Application.
 * 
 * This is the CM Example application main class. It is mainly there to require
 * the different components that are needed to run this app.
 * 
 * This application consists of the following classes:
 * <ul>
 * <li>{@link CmFacade} – Provides the facade to the application from the
 * outside world.
 * <li>{@link Configuration2EventAdmin} – Forwards events about CM to the Event
 * Admin (which forwards it to the browser).
 * <li>{@link ConfigurationListenerComponent} – An example listener.
 * <li>{@link ConfigurationPluginComponent} – An example plugin. It adds a new
 * key to each configuration before it is delivered. The new key contains the
 * service id of the receiving Managed Service (Factory).
 * <li>{@link ManagedServiceComponent} – An example Managed Service, just prints
 * out the configuration.
 * <li>{@link ManagedServiceFactoryComponent} – An example Managed Service
 * factory, just prints out the configurations it gets.
 * </ul>
 */

@AngularWebResource.Require
@BootstrapWebResource.Require
@WebServerExtender.Require
@ConfigurerExtender.Require
@EventAdminSSEEndpoint.Require
@Component(name = "osgi.enroute.examples.cm", service = { CmApplication.class,
		ConfigurationListener.class })
public class CmApplication implements ConfigurationListener {
	private static final String TOPIC = "osgi/enroute/examples/cm";
	private EventAdmin ea;
	private ConfigurationAdmin cm;
	private DTOs dtos;
	private Coordinator coordinator;

	/*
	 * A utility function to convert a dictonary to a map.
	 */
	<K, V> Map<K, V> toMap(Dictionary<K, V> properties, Map<K, V> map) {
		if (properties != null) {
			for (Enumeration<K> e = properties.keys(); e.hasMoreElements();) {
				K key = e.nextElement();
				map.put(key, properties.get(key));
			}
		}
		return map;
	}

	/*
	 * A utility function to convert a dictonary to a map.
	 */
	Map<String, Object> toMap(Dictionary<String, Object> properties) {
		return toMap(properties, new HashMap<>());
	}

	public static class ConfigurationEventProperties extends DTO {
		public String pid;
		public String factoryPid;
		public Map<String, Object> properties;
		public String location;
	}

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		try {
			ConfigurationEventProperties cep = new ConfigurationEventProperties();
			cep.factoryPid = event.getFactoryPid();
			cep.pid = event.getPid();
			if (ConfigurationEvent.CM_DELETED != event.getType()) {
				Configuration configuration = cm.getConfiguration(event
						.getPid());
				cep.location = configuration.getBundleLocation();
				Dictionary<String, Object> properties = configuration
						.getProperties();
				if (properties == null) {
					cep.properties = new HashMap<>();
				} else
					cep.properties = toMap(properties);
			}
			ea.postEvent(new Event(TOPIC, dtos.asMap(cep)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	

	/**
	 * This code shows how you can create a singleton configuration with 
	 * Configuration Admin.
	 */
	public void exampleSingleton() throws IOException {
		Configuration configuration = cm.getConfiguration("singleton", "?");
		Hashtable<String, Object> map = new Hashtable<String,Object>();
		map.put("msg", "Hello Singleton");
		configuration.update(map);
	}

	/**
	 * This code is the same as {@link #exampleSingleton()} but it creates
	 * a plugin. The plugin can inspect and modify properties before they
	 * are delivered to the target service. The plugin is not effective for
	 * Configuration Listeners.
	 */
	public void examplePlugin() throws IOException {
		Configuration configuration = cm.getConfiguration("plugin", "?");
		Hashtable<String, Object> map = new Hashtable<String,Object>();
		map.put("msg", "Hello Plugin");
		configuration.update(map);
	}

	/**
	 * This code is the same as {@link #exampleSingleton()} but it creates
	 * a Configuration Listener. A listener receives events from configuration
	 * admin.
	 */
	public void exampleListener() throws IOException {
		Configuration configuration = cm.getConfiguration("listener", "?");
		if ( configuration.getProperties() == null)
			configuration.update(new Hashtable<String,Object>());
	}

	/**
	 * This example shows how to create a factory configuration.
	 */
	public void exampleFactory() throws IOException {
		Configuration a = cm.createFactoryConfiguration("factory", "?");
		Hashtable<String, Object> map = new Hashtable<String,Object>();
		map.put("msg", "Hello Factory");
		a.update(map);
	}


	/**
	 * The following example shows how to parse a file and turn them
	 * in configurations. The method uses the coordinator so that 
	 * participating clients can update things at once.
	 *
	 */
	public static class Config {
		public String pid;
		public String factoryPid;
		public Hashtable<String,Object> properties;
	}
	
	void exampleCoordinator() throws Exception {
		try (InputStream in = CmApplication.class
				.getResourceAsStream("example.configs")) {

			Coordination coordination = coordinator.begin("example.1", 100000);
			try {
				List<Config> list = dtos.decoder(
						new TypeReference<List<Config>>() {
						}).get(in);

				for (Config config : list) {
					Configuration c;

					if (config.factoryPid != null)
						c = cm.createFactoryConfiguration(config.factoryPid,
								"?");
					else
						c = cm.getConfiguration(config.pid);

					c.update(config.properties);
				}
				coordination.end();
			} catch (Throwable t) {
				coordination.fail(t);
			}
		}
	}

	
	@Reference
	void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	@Reference
	void setEventAdmin(EventAdmin ea) {
		this.ea = ea;
	}

	@Reference
	void setCm(ConfigurationAdmin cm) {
		this.cm = cm;
	}

	@Reference
	void setDTOs(DTOs dtos) {
		this.dtos = dtos;
	}

}
