package osgi.enroute.trains.cloud.api;

import org.osgi.dto.DTO;

/**
 * Describes a segment of the track. Segments are all connected, switches can
 * connect to 2 other segments. Segments SWITCH, SIGNAL, and LOCATOR segment
 * have a controller. Segment lengths can be zero.
 * 
 */
public class Segment extends DTO {

	public enum Type {
		STRAIGHT, CURVED, SWITCH, SIGNAL, LOCATOR;
	}
	
	/**
	 * Type of the segment
	 */
 	public Type type;

	/**
 	* Name of the track.This is the name required to get permission 
 	* from the Track Manager. If this name starts with X then no admission is required. This 
 	* is generally for switches.
 	*/
 	public String track;
 	
	/**
	 * Sequence of the segment in the track
	 */
	public int sequence;

	/**
	 * Length of the segment in millimeters, can be zero. It an be zero because
	 * the relation between the controllers and the switches and the signals is
	 * not always 1:1 mappable to controllers. Therefore zero sized segments can
	 * be used to map correctly.
	 */
	public int length;

	/**
	 * Maximum speed in mm/s
	 */
	public int maxSpeed;

	/**
	 * The associated controller
	 */
	public int controller = -1;

	/**
	 * The next segment. Only a switch can have an additional to. The second to
	 * is the one selected if the switch is set to its alternative.
	 */
	public String to[];
	
	/**
	 * The previous segment. Only a switch can have an additional to. The second to
	 * is the one selected if the switch is set to its alternative.
	 */
	public String from[];
}
