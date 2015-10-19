package osgi.enroute.examples.test;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * 
 */

public class ExamplesTest {

    private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
    
    /*
     * 
     */
    @Test
    public void testExamples() throws Exception {
    	Assert.assertNotNull(context);
    }
}
