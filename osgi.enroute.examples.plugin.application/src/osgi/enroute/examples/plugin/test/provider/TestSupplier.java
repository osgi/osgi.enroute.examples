package osgi.enroute.examples.plugin.test.provider;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.examples.plugin.api.Product;
import osgi.enroute.examples.plugin.api.SupplierPlugin;

@Component
public class TestSupplier implements SupplierPlugin {

	@Reference
	DTOs dtos;

	Product[] products;

	@Activate
	void activate() throws Exception {
		try {
			InputStream in = TestSupplier.class.getResourceAsStream("products.json");
			products = dtos.decoder(Product[].class).get(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<Product> findProducts(String query) {
		return Stream.of(products)//
				.filter(p -> p.description.toLowerCase().contains(query.toLowerCase()))//
				.collect(Collectors.toSet());
	}

	@Override
	public boolean buy(String id) {
		Optional<Product> product = Stream.of(products).filter(p -> p.id.equals(id)).findFirst();
		if (product.isPresent()) {
			Product p = product.get();

			// should of course happen atomically in a db ..

			if (p.inStock > 0) {
				p.inStock--;
				return true;
			}
		}
		return false;
	}

	@Override
	public String getSupplierId() {
		return "test";
	}

}
