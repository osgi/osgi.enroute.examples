# OSGi enRoute Example Backend App

${Bundle-Description}

## Backend App
### Problem

The problem is a classic dispatch problem. In this case we need to dispatch the operations on a BLOB (Binary Large Object). We need to support the classic create, read, update, delete, and list.(We used to call these CRUD applications but where is the list in CRUD?) Blobs have a name and must maintain meta data for their modified time, name, and size.

### Design

We use OSGi µservices to implement this dispatching. The backend's are implemented as a `Backend` µservice. A Backend Application collects these backends, their type is conveyed using a µservice property. The application provides itself as a service. It provides an API to find a backend by type name and can provide a list of registered backend. Then we also provide a REST interface that uses the application to get the types. The REST interface is implemented using the [OSGi enRoute REST API][1]. It provides the following URIs:

* `GET 	/rest/backend` – Return an array of types.
* `GET /rest/backend/:type` – Return a list of MetaData for the entries in the <type> backend.
* `GET /rest/backend/:type/:name` – Get the blob
* `PUT /rest/backend/:type/:name` – Put the blob 
* `DELETE /rest/backend/:type/:name` –  Delete the blob

### Packages

* `osgi.enroute.examples.backend.api` – Defines the Backend API and metadata
* `osgi.enroute.examples.backend.application` – Defines the Application and contains the REST calls implementation.
* `osgi.enroute.examples.backend.file.provider` – A simple provider of the Backend service using a file storage.
* `osgi.enroute.examples.backend.memory.provider` – A simple provider of the Backend service using in-memory storage (a Map).

The GUI Javascript code can be found in `web` and the resources that are needed can be found in `static/osgi.enroute.examples.backend`.

### GUI

The application also has a GUI. This GUI allows to create blobs and delete them while showing which blobs are available. To run the app, checkout the project on [githb][2] (osgi.enroute.examples.backend.application). Then select debug.bndrun in bndtools, resolve, and then click on the Debug icon. After the app is started you can go to:

http://localhost:8080/osgi.enroute.examples.backend

The GUI should be self explanatory. Don't forget to look at XRay. You can find XRay at:

http://localhost:8080/system/console/xray

## The Original Email Conversation

Hi OSGi devs and experts,

I've got a problem which I really want to solve in an elegant way but I
think I haven't found the appropriate pieces yet.

The problem is the following:

I want to create some abstraction for a little data management system
which should be connected to different data backends at the same time
(e.g. S3, Dropbox, local/network filesystem, ...).

Now let's consider a simple example of the logic involving the following
standard CRUD operations (because I want to publish that in a single
ReST endpoint):

* create (e.g. upload)
* read (get metadata or list objects/files/buckets/... or just download)
* update (e.g. re-upload or change metadata)
* delete

So, what I actually want is the following:

1.) For creating/uploading a new object, a specific data backend may be
specified via HTTP header or determined automatically (e.g. based on
expected size or some other metadata).

2.) For listing existing objects all service instances/data backends
shall be queried at the same time (in parallel) and results combined
into a single list.

3.) For retrieving object metadata, downloading a specific object,
modifying, deleting it or executing some other operation with a specific
object, the correct service instance/data backend is called.


So, for case 1 I would need some way to evaluate the contextual
information of the upcoming service call (in this case the HTTP header).
If that is not available, I'll have to look at some service instance
information that helps me figuring out where to put some object (DNS-SD
or ZeroConf keep popping up conceptually here in my head).

For case 2 I just need to actually execute the same call for each
available service instance (like a network broadcast).

For case 3 I need to know somehow (this could be done by a local object
identity/location mapping) which service instance is responsible (or
allowed to) manage a specific object.


Now, I could code all this inside the ReST layer. However, what I really
want is to make it abstract in a way that I can hook it into other
places as well.

So, the initial caller 'A' should just have code like this:

	private B b;
	//...
	
	myObjectId = b.create(newObject);
	//...
	
	List objects = b.list(filter, sort, paging);
	//...
	
	myObject = b.get(myObjectId);
	//...
	
	b.updateMetadata(myObjectMetadata);
	//...
	
	b.delete(myObjectId);


So that the ReST layer does not even have to know that there is more
than just one backend.

The magic should be done outside in a generic way if possible, so that I
could re-use it as it is for other types of services.


Does that idea sound familiar to someone? Has that been solved already
and I just missed something?

I first started with the idea of using an aggregating proxy plus a
FindHook and stuff, but this imposes the runtime issue that consumers
need to be refreshed which I want to avoid if possible.

The main problem I think here is to present a single service instance to
the consumer for which there actually exists no real service
registration as it is just some kind of invocation handler with a
specialized purpose (routing/broadcasting service requests).

I would be thankful for any ideas!

[1]: http://enroute.osgi.org/services/osgi.enroute.rest.api.html
[2]: https://github.com/osgi/osgi.enroute.examples
