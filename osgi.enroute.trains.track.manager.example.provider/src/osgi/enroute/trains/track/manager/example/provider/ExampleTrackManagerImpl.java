package osgi.enroute.trains.track.manager.example.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Observation;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackForSegment;
import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.cloud.api.TrackInfo;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * 
 */
@Component(name = "osgi.enroute.trains.track.manager",
		provide={TrackForSegment.class, TrackForTrain.class, TrackInfo.class})
public class ExampleTrackManagerImpl implements TrackForSegment, TrackForTrain {
	static Logger logger = LoggerFactory.getLogger(ExampleTrackManagerImpl.class);

	
	private Map<String, Segment> segments = new HashMap<String, Segment>();
	private Map<String, String> trains = new HashMap<String, String>();
	private Map<String, Color> signals = new HashMap<String, Color>();
	private Map<String, Boolean> switches = new HashMap<String, Boolean>();
	private Map<String, String> locators = new HashMap<String, String>();

	private List<Observation> observations = new ArrayList<Observation>();
	
	private EventAdmin ea;
	private DTOs dtos;
	
	@Activate
	public void activate(Map<String, Object> config){
		// TODO read segments from configuration - for now just some hard coded ones
		
		// o = signal
		//             ------B-----o
		//    A ___o_ /              \______ A
		//            \______C_____o_/
		//           X1              X2
		
		// segment A
		Segment s1 = new Segment();
		segments.put("A-1", s1);
		s1.track = "A-1";
		s1.type = Segment.Type.CURVED;
		s1.sequence = 1;
		s1.length = 500;
		s1.to = new String[]{"A-2"};
		s1.from = new String[]{"X2"};
		s1.controller = 1; // rfid controller
		
		// signal A
		Segment s2 = new Segment();
		segments.put("A-2", s2);
		s2.track = "A-2";
		s2.type = Segment.Type.SIGNAL;
		s2.sequence = 2;
		s2.length = 0;
		s2.to = new String[]{"X1"};
		s2.from = new String[]{"A-1"};
		s2.controller = 2; // signal controller
		
		// switch X1
		Segment s3 = new Segment();
		segments.put("X1", s3);
		s3.track = "X1";
		s3.type = Segment.Type.SWITCH;
		s3.sequence = 0;
		s3.length = 50; // length of switch?
		s3.to = new String[]{"B-1","C-1"};
		s3.from = new String[]{"A-2"};
		s3.controller = 3; // switch controller
		
		// segment B
		Segment s4 = new Segment();
		segments.put("B-1", s4);
		s4.track = "B-1";
		s4.type = Segment.Type.STRAIGHT;
		s4.sequence = 1;
		s4.length = 500;
		s4.to = new String[]{"B-2"};
		s4.from = new String[]{"X1"};
		s4.controller = 4; // rfid controller
	
		// signal B
		Segment s5 = new Segment();
		segments.put("B-2", s5);
		s5.track = "B-2";
		s5.type = Segment.Type.SIGNAL;
		s5.sequence = 2;
		s5.length = 0;
		s5.to = new String[]{"X2"};
		s5.from = new String[]{"B-1"};
		s5.controller = 5; // signal controller
		
		// segment C
		Segment s6 = new Segment();
		segments.put("C-1", s6);
		s6.track = "C-1";
		s6.type = Segment.Type.STRAIGHT;
		s6.sequence = 1;
		s6.length = 500;
		s6.to = new String[]{"C-2"};
		s6.from = new String[]{"X1"};
		s6.controller = 6; // rfid controller
	
		// signal C
		Segment s7 = new Segment();
		segments.put("C-2", s7);
		s7.track = "C-2";
		s7.type = Segment.Type.SIGNAL;
		s7.sequence = 2;
		s7.length = 0;
		s7.to = new String[]{"X2"};
		s7.from = new String[]{"C-1"};
		s7.controller = 7; // signal controller
		
		// Switch X2
		Segment s8 = new Segment();
		segments.put("X2", s8);
		s8.track = "X2";
		s8.type = Segment.Type.SWITCH;
		s8.sequence = 0;
		s8.length = 50; // length of switch?
		s8.to = new String[]{"A-1"};
		s8.from = new String[]{"B-2","C-2"};
		s8.controller = 8; // switch controller
		
		// how to initialize this? Should the TrackController publish 
		// initial state to the TrackManager once it has a reference?
		locators.put("A-1", null);
		locators.put("B-1", null);
		locators.put("C-1", null);
		
		switches.put("X1", false);
		switches.put("X2", false);
		
		signals.put("A-2", Color.RED);
		signals.put("B-2", Color.RED);
		signals.put("C-2", Color.RED);
		
		
		trains.put("rfid1", "Train1");
	}
	
	@Override
	public Map<String, Segment> getSegments() {
		return segments;
	}

	@Override
	public List<String> getTrains() {
		List<String> t = new ArrayList<String>();
		t.addAll(trains.values());
		return t;
	}

	@Override
	public Map<String, Color> getSignals() {
		return signals;
	}

	@Override
	public Map<String, Boolean> getSwitches() {
		return switches;
	}

	@Override
	public Map<String, String> getLocators() {
		return locators;
	}

	@Override
	public List<Observation> getRecentObservations(long sinceId) {
		return observations.subList((int)sinceId, observations.size());
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
		System.out.println("Train "+id+" online!");
	}

	@Override
	public void locatedTrainAt(String rfid, String segment) {
		String train = trains.get(rfid);
		locators.put(segment, train);
		
		Observation o = new Observation();
		synchronized(observations){
			o.type = Observation.Type.LOCATED;
			o.train = train;
			o.segment = segment;
			o.id = observations.size();
			o.time = System.currentTimeMillis();
			
			observations.add(o);
		}

		event(o);
	}

	@Override
	public void switched(String segment, boolean alternative) {
		switches.put(segment, alternative);
		
		Observation o = new Observation();
		synchronized(observations){
			o.type = Observation.Type.SWITCH;
			o.segment = segment;
			o.id = observations.size();
			o.time = System.currentTimeMillis();
			
			observations.add(o);
		}

		event(o);
	}

	@Override
	public void signal(String segment, Color color) {
		signals.put(segment, color);
		
		Observation o = new Observation();
		synchronized(observations){
			o.type = Observation.Type.SIGNAL;
			o.segment = segment;
			o.signal = color;
			o.id = observations.size();
			o.time = System.currentTimeMillis();
			
			observations.add(o);
		}

		event(o);
	}

	private void event(Observation o){
		try {
			Event event = new Event(Observation.TOPIC, dtos.asMap(o));
			System.out.println("Post observation "+o);
			ea.postEvent(event);
		} catch(Exception e){
			logger.error("Error posting observation "+o, e);
		}
	}
	
	@Reference
	public void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	@Reference 
	public void setDTOs(DTOs dtos){
		this.dtos = dtos;
	}

	@Override
	public Set<String> getBlocked() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void blocked(String segment, String reason, boolean blocked) {
		// TODO Auto-generated method stub
		
	}
}
