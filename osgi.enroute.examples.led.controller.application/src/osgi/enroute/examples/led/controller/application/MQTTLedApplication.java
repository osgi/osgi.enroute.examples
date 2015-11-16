/*******************************************************************************
 * Copyright 2015 OSGi Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package osgi.enroute.examples.led.controller.application;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.nonNull;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static osgi.enroute.examples.led.controller.util.Utils.dictionaryToMap;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.annotations.Designate;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.examples.led.controller.application.configurable.LedConfiguration;
import osgi.enroute.examples.led.controller.core.api.ILedController;
import osgi.enroute.examples.led.controller.model.Settings;
import osgi.enroute.examples.led.controller.mqtt.api.IMqttClient;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

/**
 * LED Control with MQTT Communication Service Component
 */
@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireEventAdminServerSentEventsWebResource
@RequireWebServerExtender
@RequireConfigurerExtender
@Designate(ocd = LedConfiguration.class)
@Component(name = "osgi.enroute.examples.led.controller.application")
public final class MQTTLedApplication implements ManagedService, REST {

	/**
	 * Extended REST Request for PUT Verb
	 */
	private interface SettingsRequest extends RESTRequest {
		Settings _body();
	}

	/**
	 * GPIO Configuration Service PID
	 */
	private static final String GPIO_CONF_PID = "osgi.enroute.examples.led.controller.core";

	/**
	 * MQTT Topic Configuration Service PID
	 */
	private static final String LED_CONF_PID = "osgi.enroute.examples.led.controller.application";

	/**
	 * MQTT Configuration Service PID
	 */
	private static final String MQTT_CONF_PID = "osgi.enroute.examples.led.controller.mqtt";

	/**
	 * Configuration Admin Service Reference
	 */
	@Reference
	private volatile ConfigurationAdmin configurationAdmin;

	/**
	 * GPIO Configuration
	 */
	private Configuration gpioConfiguration;

	/**
	 * LED Configuration
	 */
	private Configuration ledConfiguration;

	/**
	 * LED Controller Service Reference
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	private volatile ILedController ledController;

	/**
	 * Log Service Reference
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	private volatile LogService logService;

	/**
	 * MQTT Client Reference
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	private volatile IMqttClient mqttClient;

	/**
	 * MQTT Configuration
	 */
	private Configuration mqttConfiguration;

	/**
	 * MQTT Subscription Channel
	 */
	private String subscriptionChannel;

	/**
	 * Activation Callback
	 */
	@Activate
	public void activate() throws IOException {
		if (nonNull(this.configurationAdmin)) {
			this.mqttConfiguration = this.configurationAdmin.getConfiguration(MQTT_CONF_PID);
			this.gpioConfiguration = this.configurationAdmin.getConfiguration(GPIO_CONF_PID);
			this.ledConfiguration = this.configurationAdmin.getConfiguration(LED_CONF_PID);
		}
		this.subscribeToChannel();
	}

	/**
	 * Component Deactivation Callback
	 */
	@Deactivate
	public void deactivate() {
		if (nonNull(this.subscriptionChannel) && nonNull(this.mqttClient)) {
			this.mqttClient.unsubscribe(this.subscriptionChannel);
		}
	}

	/**
	 * RESTful Web-service to retrieve settings
	 */
	public Settings getSettings(final RESTRequest request) {
		final Settings settings = new Settings();
		Dictionary<String, Object> properties = this.mqttConfiguration.getProperties();
		Map<String, Object> map;

		// Retrieving MQTT Settings
		if (properties != null) {
			map = dictionaryToMap(properties);

			if (map.containsKey("host")) {
				final Object mqttServer = map.get("host");
				if (nonNull(mqttServer) && !isNullOrEmpty(String.valueOf(mqttServer))) {
					settings.host = String.valueOf(mqttServer);
				}
			}

			if (map.containsKey("port")) {
				final Object mqttPort = map.get("port");
				if (nonNull(mqttPort) && !isNullOrEmpty(String.valueOf(mqttPort))) {
					settings.port = String.valueOf(mqttPort);
				}
			}

			if (map.containsKey("username")) {
				final Object mqttUsername = map.get("username");
				if (nonNull(mqttUsername) && !isNullOrEmpty(String.valueOf(mqttUsername))) {
					settings.username = String.valueOf(mqttUsername);
				}
			}

			if (map.containsKey("userPassword")) {
				final Object mqttPassword = map.get("userPassword");
				if (nonNull(mqttPassword) && !isNullOrEmpty(String.valueOf(mqttPassword))) {
					settings.password = String.valueOf(mqttPassword);
				}
			}
		}

		// Retrieving GPIO Settings
		properties = this.gpioConfiguration.getProperties();

		if (properties != null) {
			map = dictionaryToMap(properties);
			if (map.containsKey("GPIOpin")) {
				final Object gpioPin = map.get("GPIOpin");
				if (nonNull(gpioPin) && !isNullOrEmpty(String.valueOf(gpioPin))) {
					settings.pin = String.valueOf(gpioPin);
				}
			}
		}

		// Retrieving LED Settings
		properties = this.ledConfiguration.getProperties();
		if (properties != null) {
			map = dictionaryToMap(properties);
			if (map.containsKey("subscriptionTopic")) {
				final Object subscriptionTopic = map.get("subscriptionTopic");
				if (nonNull(subscriptionTopic) && !isNullOrEmpty(String.valueOf(subscriptionTopic))) {
					settings.topic = String.valueOf(subscriptionTopic);
				}
			}
		}
		return settings;
	}

	/**
	 * RESTful Web-service to store settings
	 */
	public Settings putSettings(final SettingsRequest request) throws IOException {
		final Settings settings = request._body();

		// Updating MQTT Configurations
		Dictionary<String, Object> properties = new Hashtable<>();
		if (nonNull(settings.host)) {
			properties.put("host", settings.host);
		}
		if (nonNull(settings.username)) {
			properties.put("username", settings.username);
		}
		if (nonNull(settings.port)) {
			properties.put("port", settings.port);
		}
		if (nonNull(settings.password)) {
			properties.put("userPassword", settings.password);
		}
		this.mqttConfiguration.update(properties);

		// Updating PIN Configurations
		properties = new Hashtable<>();
		if (nonNull(settings.pin)) {
			properties.put("GPIOpin", settings.pin);
			this.gpioConfiguration.update(properties);
		}

		// Updating MQTT Topic Configurations
		properties = new Hashtable<>();
		if (nonNull(settings.topic)) {
			properties.put("subscriptionTopic", settings.topic);
			this.ledConfiguration.update(properties);
		}

		return settings;
	}

	/**
	 * Subscribe to the desired channel
	 */
	private void subscribeToChannel() {
		if (!isNullOrEmpty(this.subscriptionChannel) && nonNull(this.mqttClient)) {
			final boolean isConnected = this.mqttClient.connect();
			if (isConnected) {
				this.mqttClient.subscribe(String.valueOf(this.subscriptionChannel), message -> {
					try {
						if ("on".equalsIgnoreCase(message)) {
							this.ledController.switchOn();
							return;
						}
						if ("off".equalsIgnoreCase(message)) {
							this.ledController.switchOff();
							return;
						}
						if ("blink".startsWith(message)) {
							final String noOfTimes = message.split(" ")[1];
							this.ledController.blink(Integer.valueOf(noOfTimes));
							return;
						}
					} catch (final Exception e) {
						this.logService.log(LOG_ERROR, e.getMessage());
					}
				});
			}
		}
	}

	/** {@inheritDoc}} */
	@Override
	public void updated(final Dictionary<String, ?> properties) throws ConfigurationException {
		final Map<String, ?> map = dictionaryToMap(properties);
		if (map.containsKey("subscriptionTopic")) {
			final Object topic = map.get("subscriptionTopic");
			if (nonNull(topic) && !isNullOrEmpty(String.valueOf(topic))) {
				this.subscriptionChannel = String.valueOf(topic);
				this.subscribeToChannel();
			}
		}
	}

}
