# OSGi enRoute Embedding Example

This example project shows how to use a non-OSGi program together with an OSGi enRoute application.

## Why

There can be a number of reasons why you want to embed an OSGi Framework in OSGi: 

* In certain cases it is a lot of work to convert an existing application to OSGi because it contains so many hacks and is badly coupled. In those cases it can be a better route to convert the application in small steps. First embedding the framework can then be the first step. Over time, more and more parts of the main application can then be  moved into OSGi.
* An existing application needs a plugin model. Instead of inventing your own plugin model, adding an OSGi framework might be the better route.

## Caveats 

OSGi is a dynamic model. This very powerful model is very easy to use with Declarative Service components but it is a challenge from classic Java code.
 
## What

This example is a trivial application represented by the `App` class which is started by a `Main` class' `main` method. It has a `Plugin` interface that we would like to be implemented by an OSGi component. The `PluginImpl` class represents a typical plugin. It uses Declarative Services to provide a service that implements the `Plugin` interface.

## Background

Launching a framework is quite trivial because this has been standardized by the OSGi Alliance. However, maintaining the runtime configuration of the bundles that are necessary on that framework is not. OSGi is a component system that focuses on building systems out of reusable components. Assembling these component systems, however, does require tool support because we humans are awful in overseeing the myriad of requirements that the selected components have on the runtime. Provided that there is sufficient metadata, the computers can do a much better job. OSGi enRoute leverages the OSGi metadata to the hilt and Bndtools provide the tooling. This is the primary reason that in this example we leverage the bnd/Bndtools [launcher] architecture. This way we can have our cake, use a normal classic Java application, and eat it to, fully support the support in development from Bndtools for the embedded framework. 

So please careful consider your options. Though it looks significantly easier to just embed your framework as is, for example described by the [Apache Felix example][2], it is strongly advised to consider the bnd launcher instead. After you got the Felix example to work you could use [Apache Felix Fileinstall] but we can assure you that for larger applications this does not scale well. The Bndtools resolver, which helps you create a runtime configuration, will over time become your best friend.   

The bnd launcher is used from Bndtools to debug applications but also to generate a single JAR application that contains all its dependencies including framework and any setup properties. The launcher is prepared to also include a classic Java main application that assumes all its dependencies come from the classpath and are totally visible.  

The trick in this model is to call your static `main` method after or before the OSGi Framework is started. In this example we show how to do this as well as calling your classic code from a bundle and calling the services in the OSGi framework from your classic code.
 
## The Project

The project therefore contains the following parts:

* `osgi.enroute.examples.embedded.main` – The main program that is not an OSGi program. In this case it contains only a few packages but normally this will likely consist of many JARs. We create the main JAR with the `main.bnd` file. This bundle contains the following packages:
   * `osgi.enroute.examples.embedded.app` – The application, which also defines the Plugin API
   * `osgi.enroute.examples.embedded.main` – The main program of your application. I.e. with the static `void main(String[])` method.
   * `osgi.enroute.examples.embedded.osgi` – The adapter that links up the application and the OSGi Framework code.
* `osgi.enroute.examples.embedded.plugin` – This is the plugin JAR. The contents of this JAR are defined by the `plugin.bnd` file. This JAR only contains the `osgi.enroute.examples.embedded.plugin` package.
* `osgi.enroute.examples.embedded.bndrun` – A bndrun file defines a _runtime_. In this example we add the main code to the `-runpath`.
* `debug.bndrun` – A debugging bndrun file. This bndrun file inherits from the `osgi.enroute.examples.embedded.bndrun` file but then adds a number of debugging bundles like a shell, web console, and XRay.

## Runpath

In the `osgi.enroute.examples.embedded.bndrun` file we define the runtime environment. It can be used to define all the framework properties, which framework to use, the set of bundles to run, and also the bundles that should be on the class path. Since our application has to run on the classpath, that is where we need to be.

The [-runpath] instruction in the `osgi.enroute.examples.embedded.bndrun` file tells bnd what JARs should be on the classpath. Any JAR listed in here will automatically be put on the application's classpath. If you have an application with a lot of JARs, you want to list all your JAR's there. In general an entry on this list is a repo reference. If instead you want to put your application JARs in a directory, then you can refer to them with the `version=file` option:

	-runpath: \
		jars/my.app.jar;version=file, \
		jars/a.dependency.jar;version=file    

Since this can be cumbersome to maintain, you can also use a macro and store all your JARs in a directory:

	-runpath: \
		${replace;${lsr;jar};.+\\.jar;jar/$0\\;version=file}

In this example, we generate the main program so we could use the standard bnd repo reference:

	-runpath: \
		osgi.enroute.examples.embedded.main

However, since you likely do not build your main application, we pretend that if also comes from the file system. We just use the JAR file from the `generated` directory since Bndtools always generates the JAR there after every change you make.

	-runpath: \
		generated/osgi.enroute.examples.embedded.main.jar;version=file 

## System Exports & Capabilities

The trickiest part of embedding an OSGi framework is to cross the class loader boundary. In OSGi, each _bundle_ is a module that has very strict walls. Many classic Java developers developed severe head traumas because they were not used to provide those walls with the proper respect and therefore ran head on into these walls.

In OSGi, you can only pass through those walls by explicitly exporting packages. When you're a bundle it is obviously quite simple, you add the Export-Package header to your manifest and that's all. How should this work when you want to `export` packages from your main code. Since you're not a bundle there is no manifest.

The OSGi specification has two framework properties for this reason:

* `org.osgi.framework.system.packages.extra` – This properties is basically an Export-Package header content, it has the same syntax. Any packages listed here are exported by the _system bundle_ on the OSGi side. There is also a `org.osgi.framework.system.packages` property but this is usually automatically set by the framework to the JVM's packages your running on. E.g. java.net, javax.*, etc. Since this list is not that simple to construct, it is better to use the `extra` version of the properties.
* `org.osgi.framework.system.capabilities.extra` – OSGi applications can also depend on other capabilities then packages. This property uses the Provide-Capability header and any capabilities listed in here are provided by the system bundle.

Believe us, maintaining these properties can be a total pita. It is one of the main reasons why it makes sense to use the bnd [launcher] because the bnd launcher will look at the manifest of the JAR's that are put on the -runpath and extract any Provide-Capability and Export-Package headers. It will automatically add the contents of these headers to the    `org.osgi.framework.system.packages.extra` and `org.osgi.framework.system.capabilities.extra` properties. Therefore, even though your JAR is not an OSGi bundle, it can use these headers on the classpath and they will be recognized by the bnd launcher.
 
## Imports

Unfortunately, with imports you're out of luck. It is impossible to import the packages from a bundle into your application withough extremely strongly discouraged class loader magic. So we won't go there because it also makes sense. Your application has a fixed class path and though we can use that from a bundle using the `-runpath`, we cannot expect the classic classpath to adjust to the OSGi world.  

Therefore, you can only communicate with the bundles in the OSGi Framework with classes that are on your classpath and _exported_ by the system bundle.

## The OSGi Adapter

The trick is now to get control in our `Main` program. The bnd launcher will get activated when you start the JAR that we can export from the bndrun files:

	java -jar  osgi.enroute.examples.embedded.jar
	
Since we're on the classpath when we place our application code on the `-runpath` we will be available. However, we still need to be called. The bnd launcher supports for this reason the Embedded-Activator header. This header must point to a class that implements the `BundleActivator` class, just like the Bundle-Activator header does. If this header is detected, it will instantiate the given class and call the `start` method. The passed Bundle Context is the OSGi Framework's system bundle's context.

There is a subtle issue. Main applications tend to get called on the _main thread_. In theory it should matter on which thread your application gets started. In practice, this is not true. One example is the Eclipse SWT running on MacOS, if you're not calling it on the main thread then you get a nasty error and your application quits.

The OSGi Adapter takes care of this. It registers a `Callable` service with a magic property. The launcher will then call this callable after it has initialized the framework. 

The only thing we now need for calling the `main` method are the command line arguments. The bnd launcher registers these with an Object and Launcher service with the service property `launcher.arguments`. We get the value of this property by getting the service reference and then the appropriate property.

## The App

In the Main program we interpret the arguments and start an App instance, representing the actual application. We register the App instance as an App service.

The App package also contains a Plugin service API. In the App we iterate every second over the Plugin services and call a method. It gets the service object, calls the method, and ungets the service.

## The Plugin

The Plugin Implementation is written as a Declarative Service. It is made immediate because the getting/ungetting of the App would otherwise always activate/deactivate it. It depends on the App service and when its work method `doSomething` is called, it calls a method on the App service, back to our application.


 
[launcher]: http://bnd.bndtools.org/chapters/300-launching.html
[2]: http://felix.apache.org/documentation/subprojects/apache-felix-framework/apache-felix-framework-launching-and-embedding.html
[Apache Felix Fileinstall]: http://felix.apache.org/documentation/subprojects/apache-felix-file-install.html
[-runpath]: http://bnd.bndtools.org/instructions/runpath.html