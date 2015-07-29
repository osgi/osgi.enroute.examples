package osgi.enroute.trains.emulator.provider;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.trains.controller.api.SwitchSegmentController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.lib.converter.Converter;

@Component(name="osgi.enroute.trains.controller.switch",
	designateFactory = ControllerConfig.class,
	immediate=true)
public class EmulatedSwitch implements SwitchSegmentController {
	static Logger logger = LoggerFactory.getLogger(EmulatedSwitch.class);
	
	// controller.id
	private int id;
	
	// current switch state
	private boolean swtch = false;
	
	@Activate
	public void activate(Map<String, Object> map) throws Exception {
		ControllerConfig config = Converter.cnv(ControllerConfig.class, map);
		this.id = config.controller_id();
	}
	
	@Override
	public void swtch(boolean alternative) {
		if(this.swtch!=alternative){
			this.swtch = alternative;
			logger.info("Switch Controller "+id+" switched to "+alternative);
		}
	}

	@Override
	public boolean getSwitch() {
		return this.swtch;
	}
}
