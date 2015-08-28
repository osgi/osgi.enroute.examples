package osgi.enroute.examples.cm.application;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.dto.DTO;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.dto.api.DTOs;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.examples.cm.examples.ConfigurationListenerExample;
import osgi.enroute.examples.cm.examples.ConfigurationPluginExample;
import osgi.enroute.examples.cm.examples.Examples;
import osgi.enroute.examples.cm.examples.ManagedServiceExample;
import osgi.enroute.examples.cm.examples.ManagedServiceFactoryExample;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.jsonrpc.api.RequireJsonrpcWebResource;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

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
 * <li>{@link ConfigurationListenerExample} – An example listener.
 * <li>{@link ConfigurationPluginExample} – An example plugin. It adds a new
 * key to each configuration before it is delivered. The new key contains the
 * service id of the receiving Managed Service (Factory).
 * <li>{@link ManagedServiceExample} – An example Managed Service, just prints
 * out the configuration.
 * <li>{@link ManagedServiceFactoryExample} – An example Managed Service
 * factory, just prints out the configurations it gets.
 * </ul>
 */

@RequireAngularWebResource(resource={"angular.js", "angular-route.js", "angular-resource.js"}, priority=1000)
@RequirePagedownWebResource(resource="enmarkdown.js")
@RequireBootstrapWebResource(resource={"css/bootstrap.css"})
@RequireWebServerExtender
@RequireConfigurerExtender
@RequireEventAdminServerSentEventsWebResource
@RequireJsonrpcWebResource
@Component(name = "osgi.enroute.examples.cm", service = { CmApplication.class,
		ConfigurationListener.class })
public class CmApplication implements ConfigurationListener {
	private static final String TOPIC = "osgi/enroute/examples/cm";
	@Reference
	private EventAdmin ea;
	@Reference
	private ConfigurationAdmin cm;
	@Reference
	private DTOs dtos;
	private Examples examples;
	private Map<String, Method> examplesMap;

	
	@Activate
	void activate() throws Exception {
		ConfigurationEventProperties cep = new ConfigurationEventProperties();
		cep.refresh=true;
		ea.postEvent(new Event(TOPIC, dtos.asMap(cep)));
		
	}
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

	/**
	 * 
	 */
	public static class ConfigurationEventProperties extends DTO {
		public boolean refresh;
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

	public Object example(String example) throws Throwable {
		Method m = examplesMap.get(example);
		if (m == null)
			throw new FileNotFoundException("No such example " + example);

		try {
			return m.invoke(examples);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getTargetException();
			throw cause;
		}
	}

	public Map<String, String> examples() {
		return examplesMap.values().stream()
				.collect(Collectors.toMap((m) -> m.getName(), (m) -> {
					Tooltip tooltip = m.getAnnotation(Tooltip.class);
					return tooltip == null ? m.getName() : tooltip.value();
				}));
	}

	@Reference
	void setExample(Examples ex) {
		this.examples = ex;
		examplesMap = Arrays
				.stream(ex.getClass().getMethods())
				.filter((m) -> !Modifier.isStatic(m.getModifiers())
						&& !Modifier.isAbstract(m.getModifiers())
						&& m.getDeclaringClass() != Object.class)
				.collect(Collectors.toMap((m) -> m.getName(), (m) -> m));
	}
}
