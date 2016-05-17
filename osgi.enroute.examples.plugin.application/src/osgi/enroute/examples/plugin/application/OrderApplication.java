package osgi.enroute.examples.plugin.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.examples.plugin.api.Product;
import osgi.enroute.examples.plugin.api.SupplierPlugin;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource = { "angular.js", "angular-resource.js", "angular-route.js" }, priority = 1000)
@RequireBootstrapWebResource(resource = "css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "osgi.enroute.examples.plugin")
public class OrderApplication implements REST {

	@Reference
	volatile List<SupplierPlugin> suppliers;

	public List<Product> getFind(String query) {
		return suppliers.stream().flatMap(supplier -> supplier.findProducts(query).stream())
				.sorted((a, b) -> Long.compare(a.price, b.price)).collect(Collectors.toList());
	}

	public boolean getBuy(String supplier, String productId) throws Exception {
		Optional<Boolean> result = suppliers.stream().filter(s -> s.getSupplierId().equals(supplier))
				.map(s -> s.buy(productId)).findFirst();

		return result.isPresent() && result.get();
	}
}
