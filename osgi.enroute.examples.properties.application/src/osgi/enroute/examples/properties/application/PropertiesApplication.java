package osgi.enroute.examples.properties.application;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequirePagedownWebResource(resource="enmarkdown.js")
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

	public void postValue(String s, String b) {
		System.out.println(s);
	}
}
