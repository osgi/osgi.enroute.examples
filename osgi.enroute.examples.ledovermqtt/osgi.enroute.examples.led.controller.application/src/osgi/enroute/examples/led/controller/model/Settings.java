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
package osgi.enroute.examples.led.controller.model;

import org.osgi.dto.DTO;

/**
 * Represents a Settings Data Transfer Object
 */
public final class Settings extends DTO {

	/**
	 * MQTT Server Host
	 */
	public String host;

	/**
	 * MQTT Server Password
	 */
	public String password;

	/**
	 * GPIO PIN Number
	 */
	public String pin;

	/**
	 * MQTT Server Port
	 */
	public String port;

	/**
	 * MQTT Subscription Topic
	 */
	public String topic;

	/**
	 * MQTT Server Username
	 */
	public String username;

}
