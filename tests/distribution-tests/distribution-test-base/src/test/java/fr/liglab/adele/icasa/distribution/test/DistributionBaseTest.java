
package fr.liglab.adele.icasa.distribution.test;
 
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
  
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DistributionBaseTest {
 
	@Inject
	BundleContext context;
      
	  @Configuration
	    public Option[] config() {
		  	//Add junitBundles
	        return options(
	            junitBundles()
	            );
	    }
 
    @Test
    public void testBundlesValidity() {
    	waitForStability(context);
    }

    /**
     * Waits for stability:
     * <ul>
     * <li>all bundles are activated
     * <li>service count is stable
     * </ul>
     * If the stability can't be reached after a specified time,
     * the method throws a {@link IllegalStateException}.
     * @param context the bundle context
     * @throws IllegalStateException when the stability can't be reach after a several attempts.
     */
    private void waitForStability(BundleContext context) throws IllegalStateException {
        // Wait for bundle initialization.
        boolean bundleStability = getBundleStability(context);
        int count = 0;
        while (!bundleStability && count < 500) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // Interrupted
            }
            count++;
            bundleStability = getBundleStability(context);
        }

        if (count == 500) {
            System.err.println("Bundle stability isn't reached after 500 tries");
            throw new IllegalStateException("Cannot reach the bundle stability");
        }

        boolean serviceStability = false;
        count = 0;
        int count1 = 0;
        int count2 = 0;
        while (! serviceStability && count < 500) {
            try {
                ServiceReference[] refs = context.getServiceReferences((String) null, null);
                count1 = refs.length;
                Thread.sleep(500);
                refs = context.getServiceReferences((String) null, null);
                count2 = refs.length;
                serviceStability = count1 == count2;
            } catch (Exception e) {
                System.err.println(e);
                serviceStability = false;
                // Nothing to do, while recheck the condition
            }
            count++;
        }

        if (count == 500) {
            System.err.println("Service stability isn't reached after 500 tries (" + count1 + " != " + count2);
            throw new IllegalStateException("Cannot reach the service stability");
        }
    }

    /**
     * Are bundle stables.
     * @param bc the bundle context
     * @return <code>true</code> if every bundles are activated.
     */
    private boolean getBundleStability(BundleContext bc) {
        boolean stability = true;
        Bundle[] bundles = bc.getBundles();
        for (int i = 0; i < bundles.length; i++) {
        	int state = bundles[i].getState();
            stability = stability && ((state == Bundle.ACTIVE) || (state == Bundle.RESOLVED));
            if (!((state == Bundle.ACTIVE) || (state == Bundle.RESOLVED))){
            	System.err.println("Waiting to stability for: " + bundles[i].getSymbolicName());
            } else {
            	System.err.println("Correctly installed: " + bundles[i].getSymbolicName());
            }
        }
        return stability;
    }
}