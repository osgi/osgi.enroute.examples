package osgi.enroute.trains.segment.controller.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.util.promise.Promise;

import osgi.enroute.trains.controller.api.RFIDSegmentController;

@Component(name = "osgi.enroute.trains.segment.controller.rfid")
public class RFIDSegmentControllerImpl implements RFIDSegmentController {

	@Override
	public String lastRFID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Promise<String> nextRFID() {
		// TODO Auto-generated method stub
		return null;
	}


}
