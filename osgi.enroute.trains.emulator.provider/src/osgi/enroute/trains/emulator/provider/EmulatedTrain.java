package osgi.enroute.trains.emulator.provider;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import osgi.enroute.trains.train.api.TrainController;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.lib.converter.Converter;

@Component(name="osgi.enroute.trains.train",
	designateFactory = TrainConfig.class,
	immediate=true)
public class EmulatedTrain implements TrainController, Train {
	static Logger logger = LoggerFactory.getLogger(EmulatedTrain.class);

	private String train;
	
	private boolean light = false;
	
	private int directionAndSpeed = 0;
	
	@Activate
	public void activate(Map<String, Object> map) throws Exception {
		TrainConfig config = Converter.cnv(TrainConfig.class, map);
		this.train = config.train_id();
	}
	
	@Override
	public void move(int directionAndSpeed) {
		this.directionAndSpeed = directionAndSpeed;
		logger.info("Train "+train+" is moving "+directionAndSpeed);
	}

	@Override
	public void light(boolean on) {
		light = on ? true : false;
		
		if(light){
			logger.info("Train "+train+" turns his light on");
		} else {
			logger.info("Train "+train+" turns his light off");
		}
	}

	@Override
	public int getDirectionAndSpeed() {
		return directionAndSpeed;
	}

	@Override
	public String getName() {
		return train;
	}

}
