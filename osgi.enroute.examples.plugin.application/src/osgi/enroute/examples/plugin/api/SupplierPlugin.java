package osgi.enroute.examples.plugin.api;

import java.util.Set;

public interface SupplierPlugin {
    Set<Product> findProducts( String query );
    boolean buy( String id);
    String getSupplierId();
}
