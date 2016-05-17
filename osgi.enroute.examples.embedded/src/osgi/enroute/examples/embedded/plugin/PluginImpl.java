package osgi.enroute.examples.embedded.plugin;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.examples.embedded.app.App;
import osgi.enroute.examples.embedded.app.Plugin;

@Component(immediate=true)
public class PluginImpl implements Plugin {

	@Reference
	private App app;
	
	@Activate
	void activate() {
		app.say("OSGi enRoute from a plugin");
	}

	@Override
	public void doSomething() throws Exception {
		app.say("OSGi enRoute doing something");
	}
}
