
package fr.liglab.adele.icasa.distribution.test;
 
import javax.inject.Inject;


import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
  
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
    	waitSomeTime(10000);
    	Bundle[] bundles = context.getBundles();
    	for (Bundle bundle: bundles){
    		System.out.println("bundle:" + bundle.getSymbolicName());
    		System.out.println("state:" + bundle.getState());
    		assertNotSame(Bundle.INSTALLED, bundle.getState());
    		assertNotSame(Bundle.STOP_TRANSIENT, bundle.getState());
    		assertNotSame(Bundle.STOPPING, bundle.getState());
    		assertNotSame(Bundle.UNINSTALLED, bundle.getState());
    	}
        
    }
    
	public static void waitSomeTime(int l) {
		try {
			Thread.sleep(l);//wait to be registered
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}