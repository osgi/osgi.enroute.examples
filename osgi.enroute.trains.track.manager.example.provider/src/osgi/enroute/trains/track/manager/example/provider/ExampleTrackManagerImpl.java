package osgi.enroute.trains.track.manager.example.provider;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackForSegment;
import osgi.enroute.trains.cloud.api.TrackForTrain;

/**
 * 
 */
@Component(name = "osgi.enroute.trains.track.manager.example")
public class ExampleTrackManagerImpl implements TrackForSegment, TrackForTrain {

	@Override
	public Map<String, Segment> getSegments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTrains() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Color> getSignals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> getSwitches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getLocators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Observation> getRecentObservations(long sinceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDestination(String train) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requestAccessTo(String train, String fromSegment,
			String toSegment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release(String train) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerTrain(String id, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void locatedTrainAt(String train, String segment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switched(String segment, boolean alternative) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signal(String segment, Color color) {
		// TODO Auto-generated method stub
		
	}

}
