package osgi.enroute.trains.controller.api;

import org.osgi.util.promise.Promise;

/**
 * This controller controls a LOCATOR Segment
 */
public interface RFIDSegmentController extends SegmentController {

	/**
	 * Return the last seen RFID
	 */
	String lastRFID();
	
	/**
	 * Read an RFID. Resolves the promise when a new RFID is read.
	 * @return
	 */
	Promise<String> nextRFID();
}
