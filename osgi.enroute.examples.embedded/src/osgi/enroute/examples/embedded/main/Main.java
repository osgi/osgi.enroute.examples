package osgi.enroute.examples.embedded.main;

import osgi.enroute.examples.embedded.app.App;
import osgi.enroute.examples.embedded.osgi.OSGiAdapter;

public class Main  {

	private static App service;

	public static void main(String args[]) throws Exception {
		System.out.println("App 1.0");
		service = new App(args.length > 0 ? args[0] : "Hello: ");
		OSGiAdapter.context.registerService(App.class, service, null);

		service.run();
		
	}

}
