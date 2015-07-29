package osgi.enroute.trains.cloud.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * General track info for Gui, trains, and controllers.
 * 
 */
public interface TrackInfo {

	/**
	 * Get the general configuration
	 * 
	 * @return a map where the key is the segment name and the segment contains
	 *         the segment info.
	 */
	Map<String, Segment> getSegments();

	/**
	 * Get the list of trains
	 * 
	 * @return
	 */
	List<String> getTrains();

	/**
	 * Get the the current set of signals and their state
	 * 
	 * @return
	 */
	Map<String, Color> getSignals();

	/**
	 * Get the current set of switches and their state.
	 * 
	 * @return
	 */
	Map<String, Boolean> getSwitches();

	/**
	 * Get the blocked segments
	 * 
	 * @return
	 */
	Set<String> getBlocked();

	/**
	 * Get the current locators and their last seen RFID
	 * 
	 * @return
	 */
	Map<String, String> getLocators();

	/**
	 * Return a list of recent observations since the last time. If no
	 * observations are available then this call can block up to 1 minute.
	 * 
	 * @param sinceId the id that the caller last received or 0 for all
	 * @return
	 */
	List<Observation> getRecentObservations(long sinceId);

	/**
	 * Get the current assignment for the given train
	 * 
	 * @param train
	 * @return
	 */
	String getDestination(String train);
}
