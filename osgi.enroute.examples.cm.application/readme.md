# OSGi enRoute Configuration Admin Examples

This application demonstrates the usage of the Configuration Admin service. It minimally shows the contents of the current Configuration Admin service and updates the GUI dynamically whenever one of the configurations is updated. Additionally, you can delete, modify and add configurations. 

This application is an illustration for  the [OSGi enRoute CM service description][enroute.cm] service catalog entry. It is advised to first read this page the concepts of using Configuration Admin. After you understand these concepts, this application allows you to see it in action in a live system. You should run the application in debug mode in bndtools. This allows you to inspect the interaction between the different actors and modify the code to see what the consequences are. Though you can of course see actual configurations, this demo app is not a Configuration Admin app itself, it definitely takes a few shortcuts to keep the code simple.

Just a warning, we are showing the live Configuration Admin configurations. This application uses its a number of factory configurations, deleting or modifying those will likely break the application. Some of this data the example code is a bit tricky because we also need Configuration Admin for the normal application's operations. So be careful with deleting or modifying the initial configurations they are having the factory pids:

* osgi.web.jsonrpc
* osgi.eventadmin.sse
 
Feel free to copy from this code, it is all ASL 2.0.

## Starting

You can start the demo application by selecting the `osgi.enroute.examples.cm.bndrun` file and selecting _@/Debug As/OSGi Run Launcher_. After the application has started it should be available at [http://localhost:8080/osgi.enroute.examples.cm](http://localhost:8080/osgi.enroute.examples.cm).

## Examples

The example has a few components that implement the different roles of Configuration Admin. These components are by default off, but that can be activated by setting their configuration, which you can of course do through the application directly or use one of the example buttons.

* `ConfigurationListenerComponent` – (pid = `listener`) The listener component listens to configuration events and prints them out on the console.
* `ConfigurationPluginComponent` – (pid = `plugin`) The plugin component sets between the configuration and the updates to the Managed Service and Managed Service Factory services. It can add, delete, or modify properties on the fly.
* `ManagedServiceComponent` – (pid = `singleton`) The singleton component prints the `updated()` method's parameter.
* `ManagedServiceFactoryComponent` – (pid = `factory`) The factory component prints the `updated()` and `deleted()` method's parameters.

In the [Singleton view](index.html#/singleton) and [Factory view](index.html#/factory) (see buttons right top) you will see a number of buttons. These buttons are linked to the methods in the `Examples` class. These methods just perform an example scenario. Just hover over them to see a tooltip with their description.

## Exercises

* Add a component that triggers on a configuration
* Enable the properties to be updated
* Check that if you delete a configuration from Web Console it also updates the GUI
* Use the Configuration Plugin to encrypt properties

[enroute.cm]: http://enroute.osgi.org/services/org.osgi.service.cm.html