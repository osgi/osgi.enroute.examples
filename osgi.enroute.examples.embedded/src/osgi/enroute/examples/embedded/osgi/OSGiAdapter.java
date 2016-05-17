package osgi.enroute.examples.embedded.osgi;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import osgi.enroute.examples.embedded.main.Main;

public class OSGiAdapter implements BundleActivator {
	public static boolean		IMMEDIATE	= false;
	public static BundleContext	context;

	@Override
	public void start(BundleContext context) throws Exception {
		OSGiAdapter.context = context;
		Hashtable<String, String> map = new Hashtable<>();
		map.put("main.thread", "true");
		context.registerService(Callable.class, new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				ServiceReference<?> serviceReferences[] = context
						.getServiceReferences(null,"(launcher.arguments=*)");

				Main.main((String[]) serviceReferences[0].getProperty("launcher.arguments"));
				return 0;
			}

		}, map);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
