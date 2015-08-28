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

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.examples.backend.api.Backend;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

/**
 * The Backend Application is the application core. It maintains a lost of
 * backends by name and provides access to it.
 *
 */
@RequireAngularWebResource(resource = { "angular.js", "angular-route.js",
		"angular-resource.js" }, priority = 1000)
@RequirePagedownWebResource(resource = "enmarkdown.js")
@RequireBootstrapWebResource(resource = { "css/bootstrap.css" })
@RequireWebServerExtender
@RequireConfigurerExtender
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
