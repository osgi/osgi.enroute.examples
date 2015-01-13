package osgi.enroute.examples.scheduler.application;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Describes information about an example
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tooltip {
	/**
	 * A description of the command
	 */
	String description();

	/**
	 * Either number, datetime-local, or text. Translated to the HTML input type
	 * attribute.
	 */
	String type();

	/**
	 * The default value for the parameter
	 */
	String deflt();
}
