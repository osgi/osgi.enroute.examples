package osgi.enroute.examples.backend.application;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.examples.backend.api.Backend;
import osgi.enroute.examples.backend.api.MetaData;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

/**
 * The Backend REST API provides a REST interface into the Backend Application.
 * It provides the REST CRUD operations. The URI for this REST API has the
 * following syntax:
 * 
 * <pre>
 * 	/rest/backend                GET Return an array of types 
 *  /rest/backend/<type>         GET Return a list of MetaData 
 *                                   for the entries in the <type> 
 *                                   backend
 *  /rest/backend/<type>/<name>  GET Get the blob
 *                               PUT Put the blob 
 *                               DELETE Delete the blob
 * </pre>
 * 
 */
@Component
public class BackendREST implements REST {

	@Reference
	BackendApplication model;

	/*
	 * Defines how the body looks like
	 */
	interface PutRequest extends RESTRequest {
		String _body();
	}

	/**
	 * Used to return data with some of its metadata.
	 */
	public static class Data extends DTO {
		public String name;
		public String type;
		public String data;
	}

	/*
	 * This is the {@code PUT /rest/backend/<type>/<name>}. Get the backend and
	 * save the blob
	 */
	public MetaData putBackend(PutRequest pr, String type, String name)
			throws Exception {
		return getBackend(type).save(name, pr._body().getBytes("UTF-8"));
	}

	/*
	 * This is the {@code GET /rest/backend}. Get the list of type names.
	 */
	public Collection<String> getBackend(RESTRequest rq) {
		return model.getBackends();
	}

	/*
	 * This is the {@code GET /rest/backend/<type>}. Get the list of blob meta
	 * data for the given type.
	 */
	public Collection<? extends MetaData> getBackend(RESTRequest pr, String type)
			throws Exception {
		return getBackend(type).list();
	}

	/*
	 * This is the {@code GET /rest/backend<type>/<name>}. Get the blob data for
	 * the given type.
	 */
	public Data getBackend(RESTRequest pr, String type, String name)
			throws Exception {
		Backend backend = getBackend(type);

		byte[] data = backend.read(name);
		if (data == null)
			throw new FileNotFoundException("No such data for " + type + "/"
					+ name);

		Data result = new Data();
		result.name = name;
		result.type = type;
		result.data = new String(data, "UTF-8");
		return result;
	}

	/*
	 * This is the {@code DELETE /rest/backend<type>/<name>}. Delete the blob
	 */
	public boolean deleteBackend(RESTRequest r, String type, String name)
			throws Exception {
		return getBackend(type).delete(name);
	}

	/*
	 * Helper to get the backend.
	 */
	private Backend getBackend(String type) throws Exception {
		Backend b = model.getBackend(type);
		if (b == null)
			throw new FileNotFoundException("No such type " + type);

		return b;
	}
}
