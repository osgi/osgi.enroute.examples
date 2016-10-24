package osgi.enroute.examples.easse.application;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireEventAdminServerSentEventsWebResource
@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@Component(name="osgi.enroute.example.eventadminserversentevents")
public class EasseApplication implements REST {

	@Reference
	private EventAdmin eventAdmin;
	
	public void putTopic(Map<String,Object> properties) {
		Event event = new Event((String) properties.get("topic"), properties);
		eventAdmin.postEvent(event);
	}
}
