package osgi.enroute.examples.embedded.app;

import java.util.Collection;

import org.osgi.framework.ServiceReference;

import osgi.enroute.examples.embedded.osgi.OSGiAdapter;

public class App {
	private String prefix;

	public App(String prefix) {
		this.prefix = prefix;
	}

	public void say(String msg) {
		System.out.println(prefix + msg);
	}

	public void run() throws Exception {
		while(true) {
			Thread.sleep(1000);
			
			Collection<ServiceReference<Plugin>> serviceReferences = OSGiAdapter.context.getServiceReferences(Plugin.class, null);
			for( ServiceReference<Plugin> sr : serviceReferences) {
				Plugin p = OSGiAdapter.context.getService(sr);
				try {
					p.doSomething();
				} finally {
					OSGiAdapter.context.ungetService(sr);
				}
			}
		}
	}
}
