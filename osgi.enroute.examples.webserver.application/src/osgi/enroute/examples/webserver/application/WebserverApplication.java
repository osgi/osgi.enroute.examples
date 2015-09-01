package osgi.enroute.examples.webserver.application;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.examples.webserver.webresource.RequireWebserverWebresource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebserverWebresource
@RequireWebServerExtender
@Component(name="osgi.enroute.examples.webserver")
public class WebserverApplication  {


}
