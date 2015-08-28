package osgi.enroute.examples.scheduler.application;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.examples.scheduler.application.SchedulerApplication.ExampleInfo;
import osgi.enroute.examples.scheduler.application.SchedulerApplication.Tracker;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

/**
 * This component is the facade for the REST interface. We accept a uri like
 * 
 * <pre>
 * PUT /rest/scheduler/:method-name?parameter=   start a tracker
 * DELETE /rest/scheduler/:id                    delete a tracker
 * GET /rest/scheduler/:id                       get the tracker
 * GET /rest/scheduler                           get example info
 * </pre>
 * <p>
 * This example should, but does not do it, implement authorization checking.
 */
@Component()
public class SchedulerRESTFacade implements REST {
	@Reference	private SchedulerApplication app;

	interface PutRequest extends RESTRequest {
		String parameter();
	}

	/*
	 * Start a command and create a tracker.
	 */
	public Tracker putScheduler(PutRequest rq, Object payload, String method) throws Exception {
		return app.createTracker(method, rq.parameter());
	}

	public Tracker deleteScheduler(RESTRequest rq, int id) throws Exception {
		return app.removeTracker(id);
	}

	public Tracker getScheduler(RESTRequest rq, int id) {
		return app.getTracker(id);
	}

	public Map<String, ExampleInfo> getScheduler(RESTRequest rq) {
		return app.examples();
	}

}
