package osgi.enroute.trains.application;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.EasseWebResource;
import osgi.enroute.capabilities.JsonrpcWebResource;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackInfo;

@AngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@BootstrapWebResource(resource="css/bootstrap.css")
@WebServerExtender
@ConfigurerExtender
@EasseWebResource
@JsonrpcWebResource
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
