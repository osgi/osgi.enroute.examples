package osgi.enroute.examples.webconsole.provider;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.osgi.service.component.annotations.Component;

/**
 * An absolute minimum Web Console plugin
 */
@Component(name = "osgi.enroute.examples.webconsole", service = Servlet.class, property = "felix.webconsole.label="
		+ WebconsoleImpl.PLUGIN)
public class WebconsoleImpl extends AbstractWebConsolePlugin {
	private static final long serialVersionUID = 1L;
	final static String PLUGIN = "min";

	@Override
	public String getLabel() {
		return PLUGIN;
	}

	@Override
	public String getTitle() {
		return "Minimum";
	}

	@Override
	protected void renderContent(HttpServletRequest rq, HttpServletResponse rsp) throws ServletException, IOException {
		rsp.getWriter().println("Hello World");
	}

	public URL getResource(String resource) {
		if (resource.equals("/" + PLUGIN))
			return null;

		resource = resource.replaceAll("/" + PLUGIN + "/", "");
		return getClass().getResource(resource);
	}
}
