package osgi.enroute.trains.hardware.api;

import org.osgi.util.promise.Promise;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Segment;

/**
 * API to control OSGi demo hardware on a Raspberry Pi.
 * <p>
 * The hardware consists of:
 * <ul>
 * <li>One infra-red LED, connected to GPIO17, to emulate a Lego remote
 * controller.</li>
 * <li>Two multi-color LEDs, connected to GPIO??, to control signal lights.</li>
 * <li>Two RFID readers, connected to the SPI bus, to detect train position.
 * </li>
 * <li>Two Lego motors, connected via GPIO?? to an H-bridge motor driver, to
 * control track switches.</li>
 * </ul>
 */
public interface HardwareController {
    /**
     * The IR remote control supports 4 devices, each having a "red" and "blue"
     * channel. Typically, the train motor is connected to "red" and the train
     * light to "blue".
     */
    enum RcChannel {
        Red1, Blue1, Red2, Blue2, Red3, Blue3, Red4, Blue4
    };

    /** is IR Led present? */
    boolean irLedPresent();

    /**
     * Get id for specified segment name/type, or -1 if not configured. Id will
     * typically be 0 for the first device and 1 for the second device.
     */
    int idForSegment(String name, Segment.Type type);

    /**
     * set RC channel to specified level.
     * 
     * @param chan
     *            specifies RC channel
     * @param level
     *            For a train, level controls speed and direction: 1-7=forward,
     *            0=stop, -1-7=reverse. For a light, level controls brightness:
     *            0=off, 1=dim, 7=bright
     * @param timeout
     *            in ms, after which level will revert to 0 (stop) or -1 for infinite timeout.
     */
    void setRC(RcChannel chan, int level, long timeout);

    /** get RC channel level. */
    int getRC(RcChannel chan);

    /**
     * set signal light to specified color.
     * 
     * @param id
     *            0,1 to select signal
     */
    void setSignal(int id, Color color);

    /** get signal color. */
    Color getSignal(int id);

    /**
     * set switch to normal or alternative position.
     * 
     * @param id
     *            0,1 to select switch
     */
    void setSwitch(int id, boolean alt);

    /** get switch position. */
    boolean getSwitch(int id);

    /**
     * get last RFID tag.
     * 
     * @param id
     *            0,1 to select RFID reader
     */
    String getLastTag(int id);

    /**
     * get next RFID tag.
     * 
     * @param id
     *            0,1 to select RFID reader
     */
    Promise<String> getNextTag(int id);
}
