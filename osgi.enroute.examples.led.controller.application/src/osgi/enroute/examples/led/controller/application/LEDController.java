package osgi.enroute.examples.led.controller.application;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * This is used to operate on LED
 */
public final class LEDController {

	/**
	 * GPIO Controller
	 */
	private static final GpioController GPIO = GpioFactory.getInstance();

	/**
	 * GPIO Pin
	 */
	private static GpioPinDigitalOutput pin;

	/**
	 * Switches off the LED
	 */
	public static void off() throws InterruptedException {
		pin = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_01, "EnrouteLED", PinState.LOW);
		pin.low();
		release();
	}

	/**
	 * Switches on the LED
	 */
	public static void on() throws InterruptedException {
		pin = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_01, "EnrouteLED", PinState.HIGH);
		pin.high();
		release();
	}

	/**
	 * Stops all GPIO activity/threads by shutting down the GPIO controller
	 * (this method will forcefully shutdown all GPIO monitoring threads and
	 * scheduled tasks)
	 */
	private static void release() {
		GPIO.shutdown();
		GPIO.unprovisionPin(pin);
	}

	/**
	 * Constructor
	 */
	private LEDController() {
		// Not to be instantiated
	}

}
