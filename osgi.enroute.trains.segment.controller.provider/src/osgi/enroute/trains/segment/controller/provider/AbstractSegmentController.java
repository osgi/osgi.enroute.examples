package osgi.enroute.trains.segment.controller.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.hardware.api.HardwareController;

public class AbstractSegmentController {
    // ENROUTE_PID is segment name (see application/configuration.json)
    private static final String ENROUTE_PID = "._osgi.enroute.pid";
    private static final String CONTROLLER_ID = "controller.id";
    static protected Logger logger = LoggerFactory.getLogger(AbstractSegmentController.class);
    protected List<HardwareController> hwcList = new ArrayList<>();
    protected String cid;
    protected String segment;
    protected Segment.Type type;
    protected int index;

    private HardwareController hwControl;
    
    protected void init(Segment.Type type, Map<String, Object> map) {
        this.cid = String.valueOf(map.get(CONTROLLER_ID));
        this.segment = String.valueOf(map.get(ENROUTE_PID));
        this.type = type;
        System.err.printf("SegmentContoller[%s] segment<%s> type<%s>\n", cid, segment, type);
    }

    /**
     * Find the HardwareController for the segment/type we are controlling.
     */
    protected HardwareController getController() {
        if (hwControl == null) {
            for (HardwareController hwc : hwcList) {
                if ((index = hwc.idForSegment(segment, type)) >= 0) {
                    hwControl = hwc;
                    return hwControl;
                }
            }
            String msg = String.format("No HardwareController configured for segment<%s> type<%s>", segment, type);
            warn(msg);
            logger.warn(msg);
            throw new UnsupportedOperationException(msg);
        }
        return hwControl;
    }

    protected void addHardwareController(HardwareController hwc) {
        hwcList.add(hwc);
    }

    protected void removeHardwareController(HardwareController hwc) {
        hwcList.remove(hwc);
    }
    
    void warn(String msg) {
        System.err.println(msg);
    }

}
