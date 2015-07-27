package osgi.enroute.trains.controller.api;


/**
 * A controller controls a signal, a switch and an RFID reader.
 */
public interface SegmentController {
	
	/**
	 * Service property for identifying this controller
	 */
	String CONTROLLER_ID = "controller.id";

}
