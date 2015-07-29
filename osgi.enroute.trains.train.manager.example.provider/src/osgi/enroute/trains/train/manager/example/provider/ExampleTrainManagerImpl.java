package osgi.enroute.trains.train.manager.example.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * 
 */
@Component(name = "osgi.enroute.trains.train.manager.example")
public class ExampleTrainManagerImpl {

	private TrackForTrain trackManager;
	
	private Map<TrainController, TrainDriver> trains = Collections.synchronizedMap(new HashMap<TrainController, TrainDriver>()); 
		

	@Reference
	public void setTrackManager(TrackForTrain t){
		this.trackManager = t;
	}
	
	@Reference(type='*')
	public void addTrainController(TrainController t){
		TrainDriver driver = new TrainDriver(t);
		trains.put(t, driver);
		driver.start();
	}
	
	public void removeTrainController(TrainController t){
		TrainDriver driver = trains.get(t);
		if(driver!=null){
			driver.stop();
		}
	}
	
	/**
	 * Simple class that keeps on driving a train infinitely
	 * without requesting segment access
	 */
	private class TrainDriver {

		private final TrainController train;
		
		public TrainDriver(TrainController t){
			this.train = t;
		}
		
		public void start(){
			this.train.move(50); // 5 cm per second = 11 seconds per segment
		}
		
		public void stop(){
			train.move(0);
		}
	}
}
