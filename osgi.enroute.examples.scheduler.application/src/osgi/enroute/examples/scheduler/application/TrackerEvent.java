package osgi.enroute.examples.scheduler.application;

/**
 * Events that the application can send to Event Admin
 */
public enum TrackerEvent {
	CREATED, TICKED, RESOLVED, FAILED, CANCELED, DELETED, TIMEOUT;
}
