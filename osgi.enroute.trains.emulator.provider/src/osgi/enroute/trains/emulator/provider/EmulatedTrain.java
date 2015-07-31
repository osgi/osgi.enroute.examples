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

	private String name;
	private String rfid;
	
	private boolean light = false;
	
	private int directionAndSpeed = 0;
	
	@Activate
	public void activate(Map<String, Object> map) throws Exception {
		TrainConfig config = Converter.cnv(TrainConfig.class, map);
		this.name = config.name();
		this.rfid = config.rfid();
	}
	
	@Override
	public void move(int directionAndSpeed) {
		this.directionAndSpeed = directionAndSpeed;
		logger.info("Train "+name+" is moving "+directionAndSpeed);
	}

	@Override
	public void light(boolean on) {
		light = on ? true : false;
		
		if(light){
			logger.info("Train "+name+" turns his light on");
		} else {
			logger.info("Train "+name+" turns his light off");
		}
	}

	@Override
	public int getDirectionAndSpeed() {
		return directionAndSpeed;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getRfid(){
		return rfid;
	}
}
