package osgi.enroute.trains.segment.controller.provider;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;

import osgi.enroute.trains.cloud.api.Segment;
import osgi.enroute.trains.controller.api.SwitchSegmentController;
import osgi.enroute.trains.hardware.api.HardwareController;

@Component(name = ControllerConfig.SWITCH)
@Designate(ocd = ControllerConfig.class, factory = true)
public class SwitchSegmentControllerImpl extends AbstractSegmentController implements SwitchSegmentController {

    @Activate
    public void activate(Map<String, Object> cfg) throws Exception {
        super.init(Segment.Type.SWITCH, cfg);
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
    protected void addHardwareController(HardwareController hwc) {
        super.addHardwareController(hwc);
    }
    
    protected void removeHardwareController(HardwareController hwc) {
        super.removeHardwareController(hwc);
    }

    @Override
    public void swtch(boolean alternative) {
        getController().setSwitch(index, alternative);
    }

    @Override
    public boolean getSwitch() {
        return getController().getSwitch(index);
    }

}
