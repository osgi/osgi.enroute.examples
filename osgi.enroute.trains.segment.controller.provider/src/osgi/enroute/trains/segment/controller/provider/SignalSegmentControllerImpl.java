package osgi.enroute.trains.segment.controller.provider;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.controller.api.SignalSegmentController;
import osgi.enroute.trains.hardware.api.HardwareController;

@Component(name = ControllerConfig.SIGNAL)
@Designate(ocd = ControllerConfig.class, factory=true)
public class SignalSegmentControllerImpl extends AbstractSegmentController implements SignalSegmentController {

    @Activate
    public void activate(Map<String, Object> cfg) throws Exception {
        super.init(Segment.Type.SIGNAL, cfg);
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    protected void addHardwareController(HardwareController hwc) {
        super.addHardwareController(hwc);
    }
    
    protected void removeHardwareController(HardwareController hwc) {
        super.removeHardwareController(hwc);
    }

    @Override
    public void signal(Color color) {
        getController().setSignal(index, color);
    }

    @Override
    public Color getSignal() {
        return getController().getSignal(index);
    }

}
