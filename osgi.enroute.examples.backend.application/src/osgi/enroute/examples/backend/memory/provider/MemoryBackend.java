package osgi.enroute.examples.backend.memory.provider;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.examples.backend.api.Backend;
import osgi.enroute.examples.backend.api.MetaData;

/**
 * Implementation of the backend API for an in memory store.
 */
@Component(property = Backend.TYPE + "=memory")
public class MemoryBackend implements Backend {
	private final ConcurrentMap<String, MetaDataImpl> memory = new ConcurrentHashMap<>();

	/*
	 * Reuse metadata to also contain the payload. We extend meta data so we can
	 * return them as MetaData objects.
	 */
	public static class MetaDataImpl extends MetaData {
		byte[] payload;
	}

	/*
	 * Save the object in memory, reuse old object if existed.
	 */
	@Override
	public MetaData save(String name, byte[] data) throws Exception {
		MetaDataImpl md = memory.computeIfAbsent(name,
				(s) -> new MetaDataImpl());
		md.name = name;
		md.payload = new byte[data.length];
		md.modified = System.currentTimeMillis();
		md.size = data.length;
		System.arraycopy(data, 0, md.payload, 0, md.payload.length);
		memory.put(name, md);
		return md;
	}

	/*
	 * Read the object.
	 */
	@Override
	public byte[] read(String name) throws Exception {
		MetaDataImpl mdi = memory.get(name);
		if (mdi == null)
			throw new FileNotFoundException("Memory provider can't find name "
					+ name);

		return mdi.payload;
	}

	/*
	 * The metadata happens to be our map's values!
	 */
	@Override
	public Collection<MetaDataImpl> list() throws Exception {
		return memory.values();
	}

	/*
	 * Delete the blob
	 */
	@Override
	public boolean delete(String name) throws Exception {
		return memory.remove(name) != null;
	}

}
