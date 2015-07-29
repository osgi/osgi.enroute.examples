package osgi.enroute.trains.emulator.provider;

import java.util.Map;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.trains.controller.api.RFIDSegmentController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.lib.converter.Converter;

@Component(name="osgi.enroute.trains.controller.rfid",
	designateFactory = ControllerConfig.class,
	immediate=true)
public class EmulatedRFID implements RFIDSegmentController {
	static Logger logger = LoggerFactory.getLogger(EmulatedRFID.class);
	
	// controller.id
	private int id;
	
	// last RFID
	private String lastRFID = null;
	private Deferred<String> nextRFID = new Deferred<String>();
	
	@Activate
	public void activate(Map<String, Object> map) throws Exception {
		ControllerConfig config = Converter.cnv(ControllerConfig.class, map);
		this.id = config.controller_id();
	}
	
	// called when (emulated) train is detected
	public synchronized void trigger(String rfid){
		// resolve next
		nextRFID.resolve(rfid);
		// set lastRFID
		this.lastRFID = rfid;
		// create new deferred for next
		nextRFID = new Deferred<String>();
		
		logger.info("RFID Controller "+id+" was triggerd by "+rfid);
	}
	
	@Override
	public synchronized String lastRFID() {
		return lastRFID;
	}

	@Override
	public synchronized Promise<String> nextRFID() {
		return nextRFID.getPromise();
	}
}
