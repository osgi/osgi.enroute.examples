package osgi.enroute.examples.cm.application;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.jsonrpc.api.JSONRPC;

/**
 * CM FACADE
 * 
 * This component provides the facade of the application over a JSON RPC
 * protocol. This implementation does not do the checking of the authority but
 * this is of course criminal in a real application. This class is directly
 * exposed to the outside world.
 */
@Component(property = JSONRPC.ENDPOINT + "=osgi.enroute.examples.cm")
public class CmFacade implements JSONRPC {
	private static final Configuration[] EMPTY = new Configuration[0];
	private ConfigurationAdmin cm;
	private CmApplication ca;

	/**
	 * Provides facade into to the caller.
	 */
	@Override
	public Object getDescriptor() throws Exception {
		return null;
	}

	/**
	 * Save a new configuration.
	 * 
	 * @param pid
	 *            the (instance) PID of the configuration
	 * @param map
	 *            the set of properties
	 */

	public void saveConfiguration(String pid, Hashtable<String, Object> map)
			throws IOException {
		cm.getConfiguration(pid, "?").update(map);
	}

	/**
	 * Remove a configuration.
	 * 
	 * @param pid
	 *            the (instance) PID of a configuration
	 */
	public void removeConfiguration(String pid) throws IOException {
		cm.getConfiguration(pid, "?").delete();
	}

	/**
	 * Create a new instance configuration for a given factoryPid, returning the
	 * instance PID.
	 * 
	 * @param factoryPid
	 *            the factory PID
	 * @return the instance PID
	 */
	public String createInstance(String factoryPid) throws IOException {
		return cm.createFactoryConfiguration(factoryPid, "?").getPid();
	}

	/**
	 * Return a list of configurations filtered by an OSGi Filter expression.
	 * 
	 * @param filter
	 *            the filter or null
	 * @return a collection of configurations.
	 */
	public Collection<Map<String, Object>> findConfigurations(String filter)
			throws IOException, InvalidSyntaxException {
		return getConfigurations0(filter).map(
				(c) -> ca.toMap(c.getProperties()))
				.collect(Collectors.toList());
	}

	/**
	 * Functions to call some examples
	 * @throws Throwable 
	 */

	public Object example(String example) throws Throwable {
		return ca.example(example);
	}

	public Object examples() throws Exception {
		return ca.examples();
	}

	/*
	 * Just handle the conversion from an array (potentially null) to a stream.
	 */
	private Stream<Configuration> getConfigurations0(String filter)
			throws IOException, InvalidSyntaxException {

		Configuration[] configurations = cm.listConfigurations(filter);
		if (configurations == null)
			configurations = EMPTY;

		return Arrays.stream(configurations);
	}

	@Reference
	void setCm(ConfigurationAdmin cm) {
		this.cm = cm;
	}

	@Reference
	void setCmApplication(CmApplication ca) {
		this.ca = ca;
	}
}
