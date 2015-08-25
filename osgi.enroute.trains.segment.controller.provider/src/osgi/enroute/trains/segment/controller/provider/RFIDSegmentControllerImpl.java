package osgi.enroute.trains.segment.controller.provider;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.promise.Promise;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.controller.api.RFIDSegmentController;
import osgi.enroute.trains.hardware.api.HardwareController;

@Component(name = ControllerConfig.LOCATOR)
@Designate(ocd = ControllerConfig.class, factory=true)
public class RFIDSegmentControllerImpl extends AbstractSegmentController implements RFIDSegmentController {
    @Activate
    public void activate(Map<String, Object> cfg) throws Exception {
        super.init(Segment.Type.LOCATOR, cfg);
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    protected void addHardwareController(HardwareController hwc) {
        super.addHardwareController(hwc);
    }
    
    protected void removeHardwareController(HardwareController hwc) {
        super.removeHardwareController(hwc);
    }

    @Override
    public String lastRFID() {
        return getController().getLastTag(index);
    }

    @Override
    public Promise<String> nextRFID() {
        return getController().getNextTag(index);
    }

}
