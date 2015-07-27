package osgi.enroute.trains.controller.api;


/**
 * This controller controls a SWITCH Segment
 */
public interface SwitchSegmentController extends SegmentController {

	/**
	 * Set the switch to normal or the alternative
	 * @param alternative
	 */
	void swtch(boolean alternative);
	
	boolean getSwitch();
}
