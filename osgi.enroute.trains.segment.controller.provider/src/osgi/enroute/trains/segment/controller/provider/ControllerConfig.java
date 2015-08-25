package osgi.enroute.trains.segment.controller.provider;

public interface ControllerConfig {
    static String LOCATOR = "osgi.enroute.trains.controller.rfid";
    static String SIGNAL = "osgi.enroute.trains.controller.signal";
    static String SWITCH = "osgi.enroute.trains.controller.switch";
	int controller_id();
}
