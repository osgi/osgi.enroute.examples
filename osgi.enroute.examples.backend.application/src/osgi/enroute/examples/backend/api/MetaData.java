package osgi.enroute.examples.backend.api;

import org.osgi.dto.DTO;

/**
 * The backend metadata
 */
public class MetaData extends DTO {
	/**
	 * The name of the blob
	 */
	public String name;

	/**
	 * The size of the blob
	 */
	public long size;

	/**
	 * The epoch time when it was last modified.
	 */
	public long modified;
}
