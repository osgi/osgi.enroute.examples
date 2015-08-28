package osgi.enroute.examples.component.application;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource( resource={"angular.js", "angular-resource.js", "angular-route.js"})
@RequireBootstrapWebResource( resource="css/bootstrap.css")
@RequireWebServerExtender
@Component(name="osgi.enroute.examples.component")
public class ComponentApplication implements REST {

	public String getUpper(RESTRequest rq, String string) {
		return string.toUpperCase();
	}

}
