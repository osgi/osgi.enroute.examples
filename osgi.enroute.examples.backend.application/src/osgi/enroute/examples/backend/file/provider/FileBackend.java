package osgi.enroute.examples.backend.file.provider;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import osgi.enroute.examples.backend.api.Backend;
import osgi.enroute.examples.backend.api.MetaData;

/**
 * Implementation of the {@link Backend} API for a file system
 */
@Component(property = Backend.TYPE + "=file")
public class FileBackend implements Backend {

	File root;

	@Activate
	void activate(BundleContext context) {
		root = context.getDataFile("backend-file");
		root.mkdirs();
	}

	/*
	 * Save a blob
	 */
	@Override
	public MetaData save(String name, byte[] data) throws Exception {
		File file = getFile(name);
		try (FileOutputStream fout = new FileOutputStream(file)) {
			fout.write(data);
		}
		return toMetaData(file);
	}

	/*
	 * Read a blob
	 */
	@Override
	public byte[] read(String name) throws Exception {
		File f = getFile(name);
		if (!f.isFile())
			return null;

		try (FileInputStream fout = new FileInputStream(getFile(name));
				DataInputStream din = new DataInputStream(fout)) {
			byte[] data = new byte[(int) f.length()];
			din.readFully(data);
			return data;
		}
	}

	/*
	 * List the metadata
	 */
	@Override
	public Collection<? extends MetaData> list() throws Exception {
		return Arrays.stream(root.listFiles()).map((file) -> {
			return toMetaData(file);
		}).collect(Collectors.toList());
	}

	/*
	 * Delete a blob
	 */
	@Override
	public boolean delete(String name) throws Exception {
		File f = getFile(name);
		if (!f.isFile())
			return false;

		return f.delete();
	}
	
	/*
	 * Helper to create metadata
	 */
	private MetaData toMetaData(File file) {
		MetaData md = new MetaData();
		md.name = file.getName();
		md.modified = file.lastModified();
		md.size = file.length();
		return md;
	}

	/*
	 * Helper to get the file from a name
	 */
	private File getFile(String name) {
		if (name.indexOf(File.separatorChar) >= 0)
			throw new IllegalArgumentException(
					"Name must not contain separators");

		return new File(root, name);
	}


}
