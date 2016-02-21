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
package osgi.enroute.examples.led.controller.application.configurable;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Metatype Configuration for LED Controller
 */
@ObjectClassDefinition(name = "LED MQTT Topic Configuration")
public @interface LedConfiguration {

	/**
	 * MQTT Subscription Topic
	 */
	@AttributeDefinition(name = "MQTT Subscription Topic", description = "The subscription topic to be used to send messages for switching on/off the light")
	public String subscriptionTopic() default "osgi/enRoute/led/controller";

}
