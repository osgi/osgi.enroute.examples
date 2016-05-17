package osgi.enroute.examples.plugin.api;

import org.osgi.dto.DTO;

public class Product extends DTO {
	public String	supplier;
	public String	id;
	public String	name;
	public String	description;
	public long		price;
	public int		inStock;
	public int		deliveryTimeInDays;
}
