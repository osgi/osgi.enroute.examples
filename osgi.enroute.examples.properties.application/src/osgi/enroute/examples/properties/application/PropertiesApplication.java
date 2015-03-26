package osgi.enroute.examples.properties.application;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

@AngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@BootstrapWebResource(resource="css/bootstrap.css")
@WebServerExtender
@ConfigurerExtender
@Component(name="osgi.enroute.examples.properties")
public class PropertiesApplication implements REST {
	Logger logger = LoggerFactory.getLogger(PropertiesApplication.class);
	
	BundleContext context;
	
	@Activate
	void activate(BundleContext ctx) {
		logger.info("Activated");
		this.context =ctx;
	}
	public String getProperty(RESTRequest rq, String string) {
		logger.info("getProperty " + string);
		System.out.println("Get " + string);
		return context.getProperty(string);
	}

	public Properties getProperty(RESTRequest rq) {
		return System.getProperties();
	}

}
