package osgi.enroute.trains.train.manager.example.provider;

import osgi.enroute.trains.cloud.api.TrackForTrain;
import osgi.enroute.trains.train.api.TrainController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * 
 */
@Component(name = "osgi.enroute.trains.train.manager.example",
	immediate=true,
	provide=Object.class,
	properties={"osgi.command.scope=trains","osgi.command.function=move"})
public class ExampleTrainManagerImpl {

	private TrackForTrain trackManager;
	
	private TrainController train;
	
	@Activate
	public void activate(){
		// TODO where does the TrainManager get his name from?
		// notify Track Manager
		trackManager.registerTrain("Train1", "Train");

		// turn the train light on 
		train.light(true);
		// start moving on activation
		train.move(50);
	}
	
	@Deactivate
	public void deactivate(){
		// stop when deactivated
		train.move(0);
	}
	
	@Reference
	public void setTrainController(TrainController t){
		this.train = t;
	}

	@Reference
	public void setTrackManager(TrackForTrain t){
		this.trackManager = t;
	}

	// make train move from gogo shell command
	public void move(int directionAndSpeed){
		this.train.move(directionAndSpeed);
	}
}
