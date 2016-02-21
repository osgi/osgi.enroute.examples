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
package osgi.enroute.examples.led.controller.gpio.configurable;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Metatype Configuration for LED GPIO Configuration
 */
@ObjectClassDefinition(name = "LED GPIO PIN Configuration")
public @interface LedGpioConfiguration {

	/**
	 * List of GPIO PIN Entries
	 */
	enum Pins {
		PIN00, PIN01, PIN02, PIN03, PIN04, PIN05, PIN06, PIN07, PIN08, PIN09, PIN10, PIN11, PIN12, PIN13, PIN14, PIN15, PIN16, PIN17, PIN18, PIN19, PIN20, PIN21, PIN22, PIN23, PIN24, PIN25, PIN26, PIN27, PIN28, PIN29;
	}

	/**
	 * GPIO PIN connected to LED
	 */
	@AttributeDefinition(name = "GPIO PIN", description = "Please Select the GPIO PIN Configured with the LED connected to Raspberry Pi")
	Pins GPIOpin();

}
