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

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.EventAdminSSEEndpoint;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.jsonrpc.api.JSONRPC;

@AngularWebResource.Require
@BootstrapWebResource.Require
@WebServerExtender.Require
@ConfigurerExtender.Require
@EventAdminSSEEndpoint.Require
@Component(property = JSONRPC.ENDPOINT
		+ "=osgi.enroute.examples.cm")
public class CmFacade implements JSONRPC  {
	private static final Configuration[] EMPTY = new Configuration[0];

	ConfigurationAdmin cm;

	private CmApplication ca;

	@Override
	public Object getDescriptor() throws Exception {
		return null;
	}

	/**
	 * 
	 * @param pid
	 * @param map
	 * @throws IOException
	 */

	public void saveConfiguration(String pid, Hashtable<String, Object> map)
			throws IOException {
		cm.getConfiguration(pid, "?").update(map);
	}

	public void removeConfiguration(String pid) throws IOException {
		cm.getConfiguration(pid, "?").delete();
	}

	public String createInstance(String factoryPid) throws IOException {
		return cm.createFactoryConfiguration(factoryPid, "?").getPid();
	}

	public Collection<Map<String, Object>> getConfigurations(String filter)
			throws IOException, InvalidSyntaxException {
		return getConfigurations0(filter).map((c) -> ca.toMap(c.getProperties()))
				.collect(Collectors.toList());
	}

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
