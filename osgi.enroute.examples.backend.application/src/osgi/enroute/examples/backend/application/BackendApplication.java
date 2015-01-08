package osgi.enroute.examples.backend.application;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.PagedownWebResource;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.examples.backend.api.Backend;

/**
 * The Backend Application is the application core. It maintains a lost of
 * backends by name and provides access to it.
 *
 */
@AngularWebResource(resource = { "angular.js", "angular-route.js",
		"angular-resource.js" }, priority = 1000)
@PagedownWebResource(resource = "enmarkdown.js")
@BootstrapWebResource(resource = { "css/bootstrap.css" })
@WebServerExtender
@ConfigurerExtender
@Component(name = "osgi.enroute.examples.backends", service = BackendApplication.class)
public class BackendApplication {
	private ConcurrentMap<String, Backend> backends = new ConcurrentHashMap<>();

	public Backend getBackend(String type) throws FileNotFoundException {
		return backends.get(type);
	}

	public Collection<String> getBackends() {
		return backends.keySet();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addBackend(Backend backend, Map<String, Object> map) {
		backends.put((String) map.get(Backend.TYPE), backend);
	}

	void removeBackend(Backend backend, Map<String, Object> map) {
		backends.remove((String) map.get(Backend.TYPE), backend);
	}

}
