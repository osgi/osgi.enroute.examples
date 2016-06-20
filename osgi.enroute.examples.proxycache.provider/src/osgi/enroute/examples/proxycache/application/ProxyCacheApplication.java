package osgi.enroute.examples.proxycache.application;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.http.capabilities.RequireHttpImplementation;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireConfigurerExtender
@RequireHttpImplementation
@RequireWebServerExtender
public class ProxyCacheApplication {

}
