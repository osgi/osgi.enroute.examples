package osgi.enroute.trains.cloud.api;

import org.osgi.dto.DTO;

/**
 * Event class for sending out information about what's happening
 */
public class Observation extends DTO {
	public final static String TOPIC = "osgi/trains/observation";

	public enum Type {
		CHANGE, LOCATED, ASSIGNMENT, SIGNAL, SWITCH, BLOCKED
	}

	public Type type;
	public String segment;
	public String train;
	public Color signal;
	public String assignment;
	public long time;
	public long id;
	public String message;
}
