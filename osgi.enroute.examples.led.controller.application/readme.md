# OSGi enRoute LED Controller Example

Example application to switch on/off or blink LED connected to Raspberry Pi GPIO Pins using MQTT Communication

##### Hardware Requirements:
1. LED Light
2. Resistor
3. Raspberry Pi (Any Model)

##### Bundles Used:
1. **osgi.enroute.examples.led.controller.application** - Example Application Project
2. **osgi.enroute.examples.led.controller.core.api** - LED Controller API
3. **osgi.enroute.examples.led.controller.core.provider** - LED Controller Service Implementation
4. **osgi.enroute.examples.led.controller.mqtt.api** - MQTT Communication API (Simplified Version)
5. **osgi.enroute.examples.led.controller.mqtt.provider** - MQTT Communication Service Implementation

##### HOW TO RUN
Before you start executing the following steps, it is advisable to configure the LED with GPIO Pin as mentioned the following image (although the image is an example). As soon as you connect the LED to the GPIO, you can start executing the following steps.

1. Run either **osgi.enroute.examples.led.controller.bndrun**  or **debug.run** (You might need to add **Google Guava Bundle** to your local Bndtools Repository) 
2. You can now access the application at **http://<IP Address of the Raspberry Pi>:8080**
3. Update the settings before you play with this tool
4. Now you can publish MQTT message (with **on** or **off** or **blink X** payload where **X** is any specific integer number) to the specified topic

##### LED Configuration:
The following image is an example of connecting **LED** to **GPIO PIN 1**.
 
![alt text](http://pi4j.com/images/gpio-control-example.png "LED Configuration")

