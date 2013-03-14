
package fr.liglab.adele.commons.distribution.base.test;
 
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
  
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DistributionBaseTest extends AbstractDistributionBaseTest{
 
	@Inject
	public BundleContext context;

	
	@Test
	public void testBundlesValidity() {
		waitForStability(context);
	}


}