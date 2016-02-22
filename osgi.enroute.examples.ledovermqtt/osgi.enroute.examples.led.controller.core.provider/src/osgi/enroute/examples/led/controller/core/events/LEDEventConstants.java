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
package osgi.enroute.examples.led.controller.core.events;

/**
 * Event Topics
 */
public enum LEDEventConstants {

	/**
	 * Switch OFF OSGi Event
	 */
	OFF("osgi/enroute/led/off"),

	/**
	 * ON OSGi Event
	 */
	ON("osgi/enroute/led/on"),

	/**
	 * Status OFF OSGi Event
	 */
	STATUS_OFF("osgi/enroute/led/status/off"),

	/**
	 * Status ON OSGi Event
	 */
	STATUS_ON("osgi/enroute/led/status/on");

	/**
	 * Event Topic namespace holder
	 */
	private final String namespace;

	/**
	 * Constructor
	 */
	LEDEventConstants(final String text) {
		this.namespace = text;
	}

	/**
	 * Returns topic namespace
	 */
	public String value() {
		return this.namespace;
	}

}
