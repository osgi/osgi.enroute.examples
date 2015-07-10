package osgi.enroute.trains.controller.api;

import org.osgi.util.promise.Promise;

import osgi.enroute.trains.cloud.api.Color;

/**
 * A controller conrols a signal, a switch and an RFID reader. This API provides
 * access to the underlying information
 */
public interface Controller {
	
	/**
	 * Service property for identifying this controller
	 */
	String CONTROLLER_ID = "controller.id";

	/**
	 * Set the signal to the given color
	 * @param color
	 */
	void signal(Color color);

	/**
	 * Set the switch to normal or the alternative
	 * @param alternative
	 */
	void swtch(boolean alternative);
	
	boolean getSwitch();
	

	/**
	 * Read an RFID. Resolves the promise when a new RFID is read.
	 * @return
	 */
	Promise<String> lastRFID();
}
