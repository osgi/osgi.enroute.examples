# OSGI ENROUTE EXAMPLES PROXYCACHE PROVIDER

This example show how to make a servlet that provides proxy support for resources
coming from a Content Delivery Network (CDN). If the content is not present, it will
download it from the CDN.

The example implicitly shows how to make a servlet with the whiteboard model.

## Application

The example contains a small application that shows a web page on:

	http://localhost:8080/osgi.enroute.examples.proxycache/index.html

This example loads the bootstrap CSS file from a CDN. This CDN URI is set in `configuration/configuration.json`.


