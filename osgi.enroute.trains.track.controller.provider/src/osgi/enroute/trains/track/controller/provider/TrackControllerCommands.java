package osgi.enroute.trains.track.controller.provider;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.trains.cloud.api.Color;
import osgi.enroute.trains.cloud.api.Command;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import aQute.lib.converter.Converter;

/**
 * Small component that offers GoGo commands that post Command
 * Events to EventAdmin to control (and test) the Track Controller
 */
@Component(provide=Object.class,
		properties={"osgi.command.scope=trains",
		"osgi.command.function=signal",
		"osgi.command.function=swtch"})
public class TrackControllerCommands {

	private EventAdmin ea;
	private DTOs dtos;
	
	public void signal(String segment, String color) throws Exception{
		Command c = new Command();
		c.type = Command.Type.SIGNAL;
		c.segment = segment;
		c.signal = Converter.cnv(Color.class, color);
		
		ea.postEvent(new Event(Command.TOPIC, dtos.asMap(c)));
	}
	
	public void swtch(String segment) throws Exception {
		Command c = new Command();
		c.type = Command.Type.SWITCH;
		c.segment = segment;
		
		ea.postEvent(new Event(Command.TOPIC, dtos.asMap(c)));
	}
	
	@Reference
	public void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	@Reference
	public void setDTOs(DTOs dtos){
		this.dtos = dtos;
	}
}
