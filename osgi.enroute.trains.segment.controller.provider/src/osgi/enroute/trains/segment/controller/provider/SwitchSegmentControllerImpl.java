package osgi.enroute.trains.segment.controller.provider;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.trains.controller.api.SwitchSegmentController;

@Component(name = "osgi.enroute.trains.segment.controller.switch")
public class SwitchSegmentControllerImpl implements SwitchSegmentController {

	@Override
	public void swtch(boolean alternative) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getSwitch() {
		// TODO Auto-generated method stub
		return false;
	}


}
