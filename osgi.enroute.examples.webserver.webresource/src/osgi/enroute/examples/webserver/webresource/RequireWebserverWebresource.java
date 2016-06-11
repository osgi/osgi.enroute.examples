package osgi.enroute.examples.webserver.webresource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import aQute.bnd.annotation.headers.RequireCapability;
import osgi.enroute.namespace.WebResourceNamespace;

@RequireCapability(
		ns = WebResourceNamespace.NS, 
		filter = "(&(" + WebResourceNamespace.NS + "="
		+ WebserverResourceConstants.WEBSERVER_WEB_RESOURCE_PATH + ")${frange;" + WebserverResourceConstants.WEBSERVER_WEB_RESOURCE_VERSION
		+ "})")
@Retention(RetentionPolicy.CLASS)
public @interface RequireWebserverWebresource {
	String[] resource() default "enroute-logo-64.png";
}
