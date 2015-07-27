package osgi.enroute.trains.train.controller.provider;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.trains.train.api.TrainController;

/**
 * 
 */
@Component(name = "osgi.enroute.trains.train.controller")
public class TrainControllerImpl implements TrainController {

	@Override
	public void move(int directionAndSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void light(boolean on) {
		// TODO Auto-generated method stub
		
	}

}
