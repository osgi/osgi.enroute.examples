package osgi.enroute.examples.cm.examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.dto.api.TypeReference;
import osgi.enroute.examples.cm.application.Tooltip;

/**
 * This class contains a number example scenarios that are executed when you hit
 * a corresponding button on the GUI. Just add public methods here and they are
 * available as an additional bundle. Add a {@link Tooltip} annotation and these
 * buttons even have a tooltip!
 */
@Component(service = Examples.class)
public class Examples {

	private ConfigurationAdmin cm;
	private Coordinator coordinator;
	private DTOs dtos;

	/**
	 * This code shows how you can create a singleton configuration with
	 * Configuration Admin. This will instantiate a
	 * {@link ManagedServiceExample}
	 */
	@Tooltip("Create a a configuration for pid='singleton', this activates the ManagedServiceExample component")
	public void singleton() throws IOException {
		Configuration configuration = cm.getConfiguration("singleton", "?");
		Hashtable<String, Object> map = new Hashtable<String, Object>();
		map.put("msg", "Hello Singleton");
		configuration.update(map);
	}

	/**
	 * This example shows how to create a factory configuration.
	 */
	@Tooltip("Create a a factory configuration for pid='factory', this activates the ManagedServiceFactory component")
	public void factory() throws IOException {
		Configuration a = cm.createFactoryConfiguration("factory", "?");
		Hashtable<String, Object> map = new Hashtable<String, Object>();
		map.put("msg", "Hello Factory");
		a.update(map);
	}

	/**
	 * This code is the same as {@link #exampleSingleton()} but it creates a
	 * plugin. The plugin can inspect and modify properties before they are
	 * delivered to the target service. The plugin is not effective for
	 * Configuration Listeners.
	 */
	@Tooltip("Create a a configuration for pid='plugin', this activates the PluginExample component")
	public void plugin() throws IOException {
		Configuration configuration = cm.getConfiguration("plugin", "?");
		Hashtable<String, Object> map = new Hashtable<String, Object>();
		map.put("msg", "Hello Plugin");
		configuration.update(map);
	}

	/**
	 * This code is the same as {@link #exampleSingleton()} but it creates a
	 * Configuration Listener. A listener receives events from configuration
	 * admin.
	 */
	@Tooltip("Create a a configuration for pid='listener', this activates the ConfigurationListenerExample component")
	public void listener() throws IOException {
		Configuration configuration = cm.getConfiguration("listener", "?");
		if (configuration.getProperties() == null)
			configuration.update(new Hashtable<String, Object>());
	}

	/**
	 * The following example shows how to parse a file and turn them in
	 * configurations. The method uses the coordinator so that participating
	 * clients can update things at once.
	 *
	 */

	/*
	 * A DTO for holding the configuration records in our examples.config file.
	 */
	public static class Config extends DTO {
		public String pid;
		public String factoryPid;
		public Hashtable<String, Object> properties;
	}

	@Tooltip("Reads a number of configuration from a resource 'example.configs' and installs them. The actions are enclosed by a Coordination")
	public void coordinator() throws Exception {
		try (InputStream in = Examples.class
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

	@Tooltip("Throws an Exception to see the error handling")
	public void exception() throws Exception {
		throw new Exception("Test Exception");
	}

	@Reference
	void setCm(ConfigurationAdmin cm) {
		this.cm = cm;
	}

	@Reference
	void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	@Reference
	void setDTOs(DTOs dtos) {
		this.dtos = dtos;
	}

}
