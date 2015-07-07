package osgi.enroute.trains.train.api;

/**
 * A Train Controller interfaces to the Lego IR interface.
 */
public interface TrainController {
	/**
	 * Control the motor. Positive is forward, negative is reverse, 0 is stop.
	 * 
	 * @param directionAndSpeed
	 */
	void move(int directionAndSpeed);

	/**
	 * Control the light on the train
	 * @param on
	 */
	void light(boolean on);
}
