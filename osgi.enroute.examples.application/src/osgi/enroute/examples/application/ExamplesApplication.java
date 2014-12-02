package osgi.enroute.examples.application;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

@AngularWebResource.Require
@BootstrapWebResource.Require
@WebServerExtender.Require
@Component(name="osgi.enroute.examples")
public class ExamplesApplication implements REST {

	public String getUpper(RESTRequest rq, String string) {
		return string.toUpperCase();
	}

}
