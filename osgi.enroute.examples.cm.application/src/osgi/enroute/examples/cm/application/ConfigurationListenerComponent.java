package osgi.enroute.examples.cm.application;

import java.util.Map;

import org.osgi.dto.DTO;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.dto.api.DTOs;

@Component
public class ConfigurationListenerComponent implements ConfigurationListener {
	private static final String TOPIC = "osgi/enroute/examples/cm";

	private EventAdmin ea;
	private ConfigurationAdmin cm;

	private CmApplication ca;

	private DTOs dtos;

	public static class ConfigurationEventProperties extends DTO {
		public String pid;
		public String factoryPid;
		public Map<String, Object> properties;
	}

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		try {
			ConfigurationEventProperties cep = new ConfigurationEventProperties();
			cep.factoryPid = event.getFactoryPid();
			cep.pid = event.getPid();
			cep.properties = event.getType() == ConfigurationEvent.CM_DELETED ? null
					: ca.toMap(cm.getConfiguration(event.getPid())
							.getProperties());

			System.out.println("Sending event " + cep);
			ea.postEvent(new Event(TOPIC, dtos.asMap(cep)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Reference
	void setEventAdmin(EventAdmin ea) {
		this.ea = ea;
	}

	@Reference
	void setCm(ConfigurationAdmin cm) {
		this.cm = cm;
	}

	@Reference
	void setCmApplication(CmApplication ca) {
		this.ca = ca;
	}

	@Reference
	void setDTOs(DTOs dtos) {
		this.dtos = dtos;
	}

}
