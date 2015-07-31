package osgi.enroute.trains.emulator.provider;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.cloud.api.TrackInfo;
import osgi.enroute.trains.controller.api.SegmentController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * The emulator listens for trains moving and triggers Emulated RFIDS
 */
@Component(name = "osgi.enroute.trains.emulator", immediate=true)
public class Emulator {
	private static final int INTERVAL = 100; // interval in ms between emulator updates
	
	private TrackInfo track;
	
	private Map<Integer, RFIDTrigger> rfids = Collections.synchronizedMap(new HashMap<Integer, RFIDTrigger>());
	private Map<Train, TrainPosition> trains = Collections.synchronizedMap(new HashMap<Train, TrainPosition>());
	
	// represents the position of a train on a segment
	private class TrainPosition {
		public Segment segment;
		public float distance;
	}
	
	private Thread emulatorThread;
	private boolean running = false;
	
	@Activate
	public void activate(){
		running = true;
		emulatorThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(running){
					// let all trains move
					synchronized(trains){
						for(Entry<Train, TrainPosition> e : trains.entrySet()){
							Train train = e.getKey();
							TrainPosition position = e.getValue();

							// speed in mm/s
							int speed = train.getDirectionAndSpeed();
							
							position.distance += speed*INTERVAL/1000.f;
							
							if(speed > 0 && position.distance > position.segment.length){
								// go to next segment
								position.distance -= position.segment.length;
								Segment next = track.getSegments().get(position.segment.to[0]);
								// skip signals/switches as next segment
								while(isSwitch(next) || isSignal(next)){
									if(isSwitch(next)){
										// this is a switch, check how it is configured
										boolean swtch = track.getSwitches().get(next.track);
										
										// train needs to travel the switch length as well
										position.distance -= next.length;
										
										if(next.to.length>1){
											// track splits up
											next = track.getSegments().get(next.to[swtch ? 1 : 0]);
										} else {
											// track joins, check whether train comes from right track
											if(!next.from[swtch ? 1 : 0].startsWith(position.segment.track)){
												System.out.println(train.getName()+" CRASHED at switch "+next.track);
												
											} else {
												next = track.getSegments().get(next.to[0]);
											}
										}
										
									} else if(isSignal(next)){
										if(track.getSignals().get(next.track+"-"+next.sequence)==Color.RED){
											System.out.print(train.getName()+" PASSED RED SIGNAL at "+next.track+"-"+next.sequence);
										}
										next = track.getSegments().get(next.to[0]);
									}
								}
								position.segment = next;
								
								// trigger next segments rfid
								RFIDTrigger rfid = rfids.get(next.controller);
								if(rfid!=null){
									rfid.trigger(train.getRfid());
								}
								
							} else if(speed < 0 && position.distance < 0){
								// go to the previous segment
								position.distance += position.segment.length;
								Segment prev = track.getSegments().get(position.segment.from[0]);

								// skip signals/switches as prev segment
								while(isSwitch(prev) || isSignal(prev)){
									if(isSwitch(prev)){
										// this is a switch, check how it is configured
										boolean swtch = track.getSwitches().get(prev.track);
										
										if(prev.from.length>1){
											// track splits up
											prev = track.getSegments().get(prev.from[swtch ? 1 : 0]);
										} else {
											// track joins, check whether train comes from right track
											if(!prev.to[swtch ? 1 : 0].startsWith(position.segment.track)){
												System.out.println(train.getName()+" CRASHED at switch "+prev.track);
											}
											prev = track.getSegments().get(prev.from[0]);
										}
										
										// train needs to travel the switch length as well
										position.distance -= prev.length;
									} else if(isSignal(prev)){
										// bi-directional signals?
										if(track.getSignals().get(prev.track+"-"+prev.sequence)==Color.RED){
											System.out.print(train.getName()+" PASSED RED SIGNAL at "+prev.track+"-"+prev.sequence);
										}
										prev = track.getSegments().get(prev.from[0]);
									}
								}
								position.segment = prev;
								
								// trigger next segments rfid
								RFIDTrigger rfid = rfids.get(prev.controller);
								if(rfid!=null){
									rfid.trigger(train.getRfid());
								}
								
							}
							
							//System.out.println(train.getName()+" is at "+position.segment.track+"-"+position.segment.sequence+" (distance: "+position.distance+" mm)");

						}
					}
					

					// how frequently do an emulation step?
					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		emulatorThread.start();
	}
	
	@Deactivate
	public void deactivate(){
		running = false;
		emulatorThread.interrupt();
	}
	
	private boolean isSwitch(Segment s){
		if(s.track.startsWith("X")){
			return true;
		}
		return false;
	}
	
	private boolean isSignal(Segment s){
		if(s.length==0){
			return true;
		}
		return false;
	}
	
	@Reference
	public void setTrackInfo(TrackInfo track){
		this.track = track;
	}
	
	@Reference(type='*')
	public void addTrain(Train train){
		TrainPosition pos = new TrainPosition();
		// TODO what is the initial train position?
		// for now fix at A-1
		pos.segment = track.getSegments().get("A-1");
		pos.distance = 0;
	
		trains.put(train, pos);
		
		// already trigger the train being at that position
		RFIDTrigger rfid = rfids.get(pos.segment.controller);
		if(rfid!=null){
			rfid.trigger(train.getRfid());
		}
	}
	
	public void removeTrain(Train train){
		trains.remove(train);
	}
	
	@Reference(type='*')
	public void addRFIDTrigger(RFIDTrigger t, Map<String, Object> properties){
		int id = Integer.parseInt((String)properties.get(SegmentController.CONTROLLER_ID));
		rfids.put(id, t);
	}
	
	public void removeRFIDTrigger(RFIDTrigger t, Map<String, Object> properties){
		int id = Integer.parseInt((String)properties.get(SegmentController.CONTROLLER_ID));
		rfids.remove(id);
	}
	
	
}
