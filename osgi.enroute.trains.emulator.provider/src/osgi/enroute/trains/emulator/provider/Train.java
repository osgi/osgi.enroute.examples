package osgi.enroute.trains.emulator.provider;

public interface Train {

	String getName();
	
	String getRfid();
	
	int getDirectionAndSpeed();
	
}
