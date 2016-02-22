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
package osgi.enroute.examples.led.controller.core.provider;

import static com.pi4j.io.gpio.PinState.HIGH;
import static com.pi4j.io.gpio.PinState.LOW;
import static com.pi4j.io.gpio.RaspiPin.GPIO_00;
import static com.pi4j.io.gpio.RaspiPin.GPIO_01;
import static com.pi4j.io.gpio.RaspiPin.GPIO_02;
import static com.pi4j.io.gpio.RaspiPin.GPIO_03;
import static com.pi4j.io.gpio.RaspiPin.GPIO_05;
import static com.pi4j.io.gpio.RaspiPin.GPIO_06;
import static com.pi4j.io.gpio.RaspiPin.GPIO_07;
import static com.pi4j.io.gpio.RaspiPin.GPIO_08;
import static com.pi4j.io.gpio.RaspiPin.GPIO_09;
import static com.pi4j.io.gpio.RaspiPin.GPIO_10;
import static com.pi4j.io.gpio.RaspiPin.GPIO_11;
import static com.pi4j.io.gpio.RaspiPin.GPIO_12;
import static com.pi4j.io.gpio.RaspiPin.GPIO_13;
import static com.pi4j.io.gpio.RaspiPin.GPIO_14;
import static com.pi4j.io.gpio.RaspiPin.GPIO_15;
import static com.pi4j.io.gpio.RaspiPin.GPIO_16;
import static com.pi4j.io.gpio.RaspiPin.GPIO_17;
import static com.pi4j.io.gpio.RaspiPin.GPIO_18;
import static com.pi4j.io.gpio.RaspiPin.GPIO_19;
import static com.pi4j.io.gpio.RaspiPin.GPIO_20;
import static com.pi4j.io.gpio.RaspiPin.GPIO_21;
import static com.pi4j.io.gpio.RaspiPin.GPIO_22;
import static com.pi4j.io.gpio.RaspiPin.GPIO_23;
import static com.pi4j.io.gpio.RaspiPin.GPIO_24;
import static com.pi4j.io.gpio.RaspiPin.GPIO_25;
import static com.pi4j.io.gpio.RaspiPin.GPIO_26;
import static com.pi4j.io.gpio.RaspiPin.GPIO_27;
import static com.pi4j.io.gpio.RaspiPin.GPIO_28;
import static com.pi4j.io.gpio.RaspiPin.GPIO_29;
import static java.util.Objects.nonNull;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static osgi.enroute.examples.led.controller.core.events.LEDEventConstants.OFF;
import static osgi.enroute.examples.led.controller.core.events.LEDEventConstants.ON;
import static osgi.enroute.examples.led.controller.core.events.LEDEventConstants.STATUS_OFF;
import static osgi.enroute.examples.led.controller.core.events.LEDEventConstants.STATUS_ON;
import static osgi.enroute.examples.led.controller.util.Utils.dictionaryToMap;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.annotations.Designate;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import osgi.enroute.examples.led.controller.core.api.ILedController;
import osgi.enroute.examples.led.controller.gpio.configurable.LedGpioConfiguration;

/**
 * Implementation of {@link ILedController} Service
 */
@Designate(ocd = LedGpioConfiguration.class)
@Component(name = "osgi.enroute.examples.led.controller.core")
public final class LedController implements ILedController, ConfigurationListener {

	/**
	 * Configuration Settings Holder
	 */
	private static Pin configurablePin;

	/**
	 * GPIO Configuration Service PID
	 */
	private static final String GPIO_CONF_PID = "osgi.enroute.examples.led.controller.core";

	/**
	 * Configuration Admin Service Reference
	 */
	@Reference
	private volatile ConfigurationAdmin configurationAdmin;

	/**
	 * Event to be fired
	 */
	private Event event;

	/**
	 * Scheduler Service Reference
	 */
	@Reference
	private volatile EventAdmin eventAdmin;

	/**
	 * GPIO Controller
	 */
	private GpioController gpio;

	/**
	 * GPIO Configuration
	 */
	private Configuration gpioConfiguration;

	/**
	 * Log Service Reference
	 */
	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	private volatile LogService logService;

	/**
	 * GPIO PIN
	 */
	private GpioPinDigitalOutput pin;

	/**
	 * Event Properties
	 */
	private final Dictionary<String, String> properties = new Hashtable<>();

	/**
	 * Activation Callback
	 */
	@Activate
	public void activate() throws IOException {
		try {
			this.gpio = GpioFactory.getInstance();
		} catch (final Throwable e) {
			this.logService.log(LOG_ERROR, "pi4j native library missing");
		}

		if (nonNull(this.configurationAdmin)) {
			this.gpioConfiguration = this.configurationAdmin.getConfiguration(GPIO_CONF_PID);
		}
	}

	/** {@inheritDoc}} */
	@Override
	public void blinkFor(final int seconds) {
		this.pin.blink(500, seconds * 1000);
	}

	/** {@inheritDoc} */
	@Override
	public void configurationEvent(final ConfigurationEvent event) {
		if (GPIO_CONF_PID.equals(event.getPid())) {
			final Dictionary<String, ?> properties = this.gpioConfiguration.getProperties();
			this.extractConfiguration(properties);
			if (this.retrieveCurrentLedStatus() == HIGH) {
				this.event = new Event(STATUS_ON.value(), this.properties);
				if (nonNull(this.eventAdmin)) {
					this.eventAdmin.postEvent(this.event);
				}
			}
			if (this.retrieveCurrentLedStatus() == LOW) {
				this.event = new Event(STATUS_OFF.value(), this.properties);
				if (nonNull(this.eventAdmin)) {
					this.eventAdmin.postEvent(this.event);
				}
			}
		}

	}

	/**
	 * Extracts GPIO PIN Configuration
	 */
	private void extractConfiguration(final Dictionary<String, ?> properties) {
		final Map<String, ?> map = dictionaryToMap(properties);
		if (map.containsKey("GPIOpin")) {
			final Object value = map.get("GPIOpin");
			if (nonNull(value)) {
				final String pin = String.valueOf(value);
				switch (pin) {
				case "PIN00":
					configurablePin = GPIO_00;
					break;
				case "PIN01":
					configurablePin = GPIO_01;
					break;
				case "PIN02":
					configurablePin = GPIO_02;
					break;
				case "PIN03":
					configurablePin = GPIO_03;
					break;
				case "PIN04":
					configurablePin = GPIO_05;
					break;
				case "PIN06":
					configurablePin = GPIO_06;
					break;
				case "PIN07":
					configurablePin = GPIO_07;
					break;
				case "PIN08":
					configurablePin = GPIO_08;
					break;
				case "PIN09":
					configurablePin = GPIO_09;
					break;
				case "PIN10":
					configurablePin = GPIO_10;
					break;
				case "PIN11":
					configurablePin = GPIO_11;
					break;
				case "PIN12":
					configurablePin = GPIO_12;
					break;
				case "PIN13":
					configurablePin = GPIO_13;
					break;
				case "PIN14":
					configurablePin = GPIO_14;
					break;
				case "PIN15":
					configurablePin = GPIO_15;
					break;
				case "PIN16":
					configurablePin = GPIO_16;
					break;
				case "PIN17":
					configurablePin = GPIO_17;
					break;
				case "PIN18":
					configurablePin = GPIO_18;
					break;
				case "PIN19":
					configurablePin = GPIO_19;
					break;
				case "PIN20":
					configurablePin = GPIO_20;
					break;
				case "PIN21":
					configurablePin = GPIO_21;
					break;
				case "PIN22":
					configurablePin = GPIO_22;
					break;
				case "PIN23":
					configurablePin = GPIO_23;
					break;
				case "PIN24":
					configurablePin = GPIO_24;
					break;
				case "PIN25":
					configurablePin = GPIO_25;
					break;
				case "PIN26":
					configurablePin = GPIO_26;
					break;
				case "PIN27":
					configurablePin = GPIO_27;
					break;
				case "PIN28":
					configurablePin = GPIO_28;
					break;
				case "PIN29":
					configurablePin = GPIO_29;
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Stops all GPIO activity/threads by shutting down the GPIO controller
	 * (this method will forcefully shutdown all GPIO monitoring threads and
	 * scheduled tasks)
	 */
	@Deactivate
	public void release() {
		if (nonNull(this.gpio)) {
			this.gpio.shutdown();
			this.gpio.unprovisionPin(this.pin);
		}
	}

	/**
	 * Returns the current status of LED connected to the GPIO mentioned in the
	 * configuration
	 */
	private PinState retrieveCurrentLedStatus() {
		this.pin = this.gpio.provisionDigitalOutputPin(configurablePin);
		return this.pin.getState();
	}

	/** {@inheritDoc} */
	@Override
	public void switchOff() {
		if (nonNull(configurablePin)) {
			this.pin = this.gpio.provisionDigitalOutputPin(configurablePin, "enRouteLED", LOW);
		}
		if (nonNull(this.pin)) {
			this.pin.low();
			this.event = new Event(OFF.value(), this.properties);
			if (nonNull(this.eventAdmin)) {
				this.eventAdmin.postEvent(this.event);
			}
		}
		this.release();
	}

	/** {@inheritDoc} */
	@Override
	public void switchOn() {
		if (nonNull(configurablePin)) {
			this.pin = this.gpio.provisionDigitalOutputPin(configurablePin, "enRouteLED", HIGH);
		}

		if (nonNull(this.pin)) {
			this.pin.high();
			this.event = new Event(ON.value(), this.properties);
			if (nonNull(this.eventAdmin)) {
				this.eventAdmin.postEvent(this.event);
			}
		}
		this.release();
	}

}
