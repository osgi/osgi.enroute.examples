package osgi.enroute.example.mqtt.client;

/**
 * MQTT Communication Related Constants
 */
public enum MQTTConsts {

	/**
	 * Subscription Channel
	 */
	LED_CHANNEL("osgi/enroute/led"),

	/**
	 * MQTT Server
	 */
	MQTT_SERVER("iot.eclipse.org"),

	/**
	 * LED Switch off Event Topic
	 */
	SWITCH_OFF_EVENT("led/off"),

	/**
	 * LED Switch on Event Topic
	 */
	SWITCH_ON_EVENT("led/on");

	/**
	 * The text holder
	 */
	private final String text;

	/**
	 * Constructor
	 */
	private MQTTConsts(final String text) {
		this.text = text;
	}

	/**
	 * Returns the text value
	 */
	public String value() {
		return this.text;
	}

}
