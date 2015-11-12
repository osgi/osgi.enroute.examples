package osgi.enroute.examples.led.controller.application;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.example.mqtt.client.MQTTClient;
import osgi.enroute.example.mqtt.client.MQTTConsts;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

/**
 * MQTT Communication Service Component
 */
@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireEventAdminServerSentEventsWebResource
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "osgi.enroute.examples.led.controller")
public final class MQTTLedOperation {

	/**
	 * Event Admin Reference
	 */
	@Reference
	private volatile EventAdmin eventAdmin;

	/**
	 * MQTT Client Reference
	 */
	private MQTTClient mqttClient;

	/**
	 * Event Properties
	 */
	private final Dictionary<String, String> properties = new Hashtable<>();

	/**
	 * Activation Callback
	 */
	@Activate
	public void activate() {
		this.mqttClient = new MQTTClient(MQTTConsts.MQTT_SERVER.value());
		this.mqttClient.subscribe(MQTTConsts.LED_CHANNEL.value(), message -> {
			try {
				if ("on".equalsIgnoreCase(message)) {
					LEDController.on();
					final Event event = new Event(MQTTConsts.SWITCH_ON_EVENT.value(), this.properties);
					this.eventAdmin.postEvent(event);
					return;
				}
				if ("off".equalsIgnoreCase(message)) {
					LEDController.off();
					final Event event = new Event(MQTTConsts.SWITCH_OFF_EVENT.value(), this.properties);
					this.eventAdmin.postEvent(event);
					return;
				}
			} catch (final Exception e) {
				e.printStackTrace(System.out);
			}
		});
	}

	/**
	 * Deactivation Callback
	 */
	@Deactivate
	public void deactivate() {
		this.mqttClient.disconnect();
		this.mqttClient = null;
		this.mqttClient.unsubscribe(MQTTConsts.LED_CHANNEL.value());
	}

}
