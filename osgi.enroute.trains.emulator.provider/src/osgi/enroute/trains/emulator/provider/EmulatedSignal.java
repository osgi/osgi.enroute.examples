package osgi.enroute.trains.emulator.provider;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.controller.api.SignalSegmentController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.lib.converter.Converter;

@Component(name="osgi.enroute.trains.controller.signal",
	designateFactory = ControllerConfig.class,
	immediate=true)
public class EmulatedSignal implements SignalSegmentController {
	static Logger logger = LoggerFactory.getLogger(EmulatedSignal.class);
	
	// controller.id
	private int id;
	
	// current Color of the signal
	private Color color = Color.RED;
	
	@Activate
	public void activate(Map<String, Object> map) throws Exception {
		ControllerConfig config = Converter.cnv(ControllerConfig.class, map);
		this.id = config.controller_id();
	}
	
	@Override
	public void signal(Color color) {
		this.color = color;
		logger.info("Signal Controller "+id+" set signal to "+color);
	}

	@Override
	public Color getSignal() {
		return this.color;
	}
}
