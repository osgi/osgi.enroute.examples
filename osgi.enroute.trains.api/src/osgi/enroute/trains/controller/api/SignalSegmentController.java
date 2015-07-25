package osgi.enroute.trains.controller.api;

import osgi.enroute.trains.cloud.api.Color;

/**
 * This controller controls a SIGNAL Segment
 */
public interface SignalSegmentController extends SegmentController {

	/**
	 * Set the signal to the given color
	 * @param color
	 */
	void signal(Color color);

	Color getSignal();
}
