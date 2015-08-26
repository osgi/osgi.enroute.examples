package osgi.enroute.trains.application;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.configurer.capabilities.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.github.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.jsonrpc.capabilities.RequireJsonrpcWebResource;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackInfo;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequireEventAdminServerSentEventsWebResource
@RequireJsonrpcWebResource
@Component(name="osgi.enroute.trains", property=JSONRPC.ENDPOINT + "=trains")
public class TrainsApplication implements JSONRPC {

	private TrackInfo ti;

	public Map<String, Segment> getSegments() {
		security();
		return ti.getSegments();
	}

	private void security() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getDescriptor() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Reference
	void setTrackInfo( TrackInfo ti) {
		this.ti = ti;
		
	}

}
