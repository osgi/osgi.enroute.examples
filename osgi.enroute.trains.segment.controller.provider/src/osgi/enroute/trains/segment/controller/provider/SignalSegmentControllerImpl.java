package osgi.enroute.trains.segment.controller.provider;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.controller.api.SignalSegmentController;

@Component(name = "osgi.enroute.trains.segment.controller.signal")
public class SignalSegmentControllerImpl implements SignalSegmentController {

	@Override
	public void signal(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Color getSignal() {
		// TODO Auto-generated method stub
		return null;
	}

}
