# OSGi enRoute REST Examples 

This project demonstrates the use of the [OSGi enRoute REST API][1]. This API allows you to write POJO code that is mapped to REST with JSON as payload. The project shows in the [RestApplication][6] class a number of examples that manipulate a history. All the examples are turned into a web page that allows you to execute them easily with a mouse click.

## How To Start

Check out this repository and load all projects in bndtools. (Do the [Quickstart Tutorial][2] for using enRoute workspaces.) Then in the `osgi.enroute.examples.rest.application`, select the `debug.bndrun` by double clicking it. After resolving it, click on the Debug button. This will start a web server on `http://localhost:8080`. Select the link. This shows a page with an entry for each example. You can click on the example, the result will then be displayed just below it.

## Feedback

You can discuss any issues on the [forum page][3], submit changes to [Github][4] or raise issues, or submit a PR on the [REST API page][5]. (There is a discussion list at the end of the description.)

[1]: http://enroute.osgi.org/services/osgi.enroute.rest.api.html
[2]: http://enroute.osgi.org/book/200-quick-start.html
[3]: http://enroute.osgi.org/forum.html
[4]: https://github.com/osgi/osgi.enroute.examples
[5]: http://enroute.osgi.org/services/osgi.enroute.rest.api.html
[6]: https://github.com/osgi/osgi.enroute.examples/blob/master/osgi.enroute.examples.rest.application/src/osgi/enroute/examples/rest/application/RestApplication.java
