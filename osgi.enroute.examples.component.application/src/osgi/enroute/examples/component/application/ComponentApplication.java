package osgi.enroute.examples.component.application;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

@AngularWebResource
@BootstrapWebResource
@WebServerExtender
@Component(name="osgi.enroute.examples.component")
public class ComponentApplication implements REST {

	public String getUpper(RESTRequest rq, String string) {
		return string.toUpperCase();
	}

}
