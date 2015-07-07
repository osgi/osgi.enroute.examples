package osgi.enroute.trains.cloud.api;

import java.util.regex.Pattern;

/**
 * Defines the configuration data for a track
 */
public interface TrackConfiguration {
	String NAME_S = "[-a-zA-Z0-9_.]+";
	String NUMBER_S = "[0-9]+";

	Pattern SEGMENT_P = Pattern.compile("(?<name>" + NAME_S
			+ "):(?<type>(STRAIGHT|CURVED|SWITCH|SIGNAL|LOCATOR)):(?<speed>"
			+ NUMBER_S + "):(?<controller>" + NUMBER_S + "):(?<to>(" + NUMBER_S
			+ ")(:" + NUMBER_S + ")*)");

	/**
	 * The nice name for the configuration.
	 */
	String name();

	/**
	 * The segment definitions. The structure is a set of data separated by colons:
	 * 
	 * {@code name:type:length<mm>:speed<%>:controller:to...}
	 */
	String[] segments();

	/**
	 * A name and RFID code for a train
	 * 
	 * {@code name:rfid}
	 */
	String[] trains();

}
