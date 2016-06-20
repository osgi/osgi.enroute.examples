package osgi.enroute.examples.proxycache.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import osgi.enroute.examples.proxycache.provider.ProxycacheImpl.Config;

/**
 * 
 */
@Designate(ocd = Config.class)
@Component(//
name = "osgi.enroute.examples.proxycache", //
property = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/cdn/*", //
service = Servlet.class, //
configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ProxycacheImpl extends HttpServlet {
	private static final long	serialVersionUID	= 1L;
	private File				data;
	private URI					base;
	private URI					remote;

	@ObjectClassDefinition
	@interface Config {
		String remote();
	}

	@Activate
	void activate(BundleContext context, Config config) throws URISyntaxException {
		this.data = context.getDataFile("cache");
		this.base = this.data.toURI();
		this.remote = new URI(config.remote());
	}

	public void doGet(HttpServletRequest rq, HttpServletResponse rsp) throws IOException, ServletException {
		try {
			String pathInfo = rq.getPathInfo();
			if (pathInfo == null)
				throw new FileNotFoundException();

			pathInfo = pathInfo.substring(1);
			URI uri = base.resolve(pathInfo);
			File file = new File(uri);
			Path path = file.toPath();

			if (!file.isFile()) {
				URI source = remote.resolve(pathInfo);

				file.getParentFile().mkdirs();
				Path temp = Files.createTempFile(path.getParent(), "proxy", ".tmp");
				Files.copy(source.toURL().openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
				Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
			}
			Files.copy(path, rsp.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
