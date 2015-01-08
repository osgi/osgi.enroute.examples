package osgi.enroute.examples.backend.api;

import java.util.Collection;

/**
 * An backend is a store of data. It provides the Create/Read/Update/Delete
 * operations for some backend store.
 */
public interface Backend {
	String TYPE = "type";

	/**
	 * Save a blob
	 * 
	 * @param name
	 *            The name of the blob
	 * @param data
	 *            The actual data
	 * @return the meta data describing the details of the store
	 */
	MetaData save(String name, byte[] data) throws Exception;

	/**
	 * Read the blob with the given name.
	 * 
	 * @param name
	 *            The name of the resource
	 * @return the data
	 */
	byte[] read(String name) throws Exception;

	/**
	 * Return a list of the meta data of what the store has.
	 * 
	 * @return The list
	 */
	Collection<? extends MetaData> list() throws Exception;

	/**
	 * Delete a blob
	 * 
	 * @param name
	 *            The name of the blob
	 * @return true if deleted, false if could not be deleted (might not exist)
	 */
	boolean delete(String name) throws Exception;
}
