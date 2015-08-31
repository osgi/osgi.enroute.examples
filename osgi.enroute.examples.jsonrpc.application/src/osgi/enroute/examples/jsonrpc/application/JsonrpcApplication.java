package osgi.enroute.examples.jsonrpc.application;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.jsonrpc.api.RequireJsonrpcWebResource;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequireJsonrpcWebResource
@RequirePagedownWebResource(resource = "enmarkdown.js")
@Component(name="osgi.enroute.examples.jsonrpc", property=JSONRPC.ENDPOINT + "=exampleEndpoint")
public class JsonrpcApplication implements JSONRPC {

	@interface Configuration {
		String message() default "Welcome!";
	}
	private Configuration config;
	
	@Activate void activate(Configuration config){
		this.config = config;
	}
	
	
	@Override
	public Object getDescriptor() throws Exception {
		return config.message();
	}
	
	
	public String toUpper(String string) {
		return string.toUpperCase();
	}


}
