package osgi.enroute.trains.controller.api;

import org.osgi.util.promise.Promise;

/**
 * This controller controls a LOCATOR Segment
 */
public interface RFIDSegmentController extends SegmentController {

	/**
	 * Read an RFID. Resolves the promise when a new RFID is read.
	 * @return
	 */
	Promise<String> lastRFID();
}
