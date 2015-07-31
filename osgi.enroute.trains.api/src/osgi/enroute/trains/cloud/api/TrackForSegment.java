package osgi.enroute.trains.cloud.api;

/**
 * The public Track interface for a Track Controller
 */
public interface TrackForSegment extends TrackInfo {

	/**
	 * Send when a train has been identified at a locator segment
	 * 
	 * @param rfid
	 *            The RFID for the train
	 * @param segment
	 *            The name of the segment
	 */
	void locatedTrainAt(String rfid, String segment);

	/**
	 * Indicate that a switch has been set
	 * 
	 * @param segment
	 *            the name of the switch segment
	 * @param alternative
	 *            defines the normal or alternative track
	 */
	void switched(String segment, boolean alternative);
	
	/**
	 * Indicate that a signal changed color
	 * 
	 * @param segment
	 *            the name of the signal segment
	 * @param color
	 *            the new signal color
	 */
	void signal(String segment, Color color);
	
	/**
	 * External indication that a segment is broken 
	 */
	
	void blocked(String segment, String reason, boolean blocked);
}
