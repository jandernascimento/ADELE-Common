
package fr.liglab.adele.commons.distribution.test;


import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemTimeout;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.CoreOptions.when;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.Factory;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public abstract class AbstractDistributionBaseTest {

	
	protected int DEBUG_PORT = 9878;
	
	protected CompositeOption packDebugConfiguration() {
		CompositeOption debugConfig = new DefaultCompositeOption(
				when(isDebugModeOn())
				.useOptions(
						vmOption(String
								.format("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%d",
										DEBUG_PORT))
										));

		return debugConfig;
	}

	private static boolean isDebugModeOn() {
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();

		boolean debugModeOn = false;

		for (String string : arguments) {
			debugModeOn = string.indexOf("jdwp") != -1;
			if (debugModeOn)
				break;
		}

		return debugModeOn;
	} 

	/**
	 * Test configuration.
	 * @return
	 */
	public List<Option> config() {
		List<Option> config = new ArrayList<Option>();
		config.add(junitBundles());
		config.add(testBaseBundles());
		config.add(packDebugConfiguration());
		return config;
	}
	/**
	 * Add the needed bundles to test the platform.
	 * Mainly the bundle containing this class.
	 * @return
	 */
	protected CompositeOption testBaseBundles() {
		CompositeOption apamCoreConfig = new DefaultCompositeOption(
				mavenBundle().groupId("fr.liglab.adele.common")
				.artifactId("base.distribution.test").versionAsInProject());
		return apamCoreConfig;
	}

	@Configuration
	public Option[] configuration() {
		Option conf[] = config().toArray(new Option[0]);
		return conf;
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
	protected void waitForStability(BundleContext context) throws IllegalStateException {
        //Bundles and service stability is handled by the Framework.
        waitForiPojoFactoriesStability(context);
	}

	/**
	 * Waits for Factories stability:
	 * <ul>
	 * <li>service factories are valid
	 * </ul>
	 * If the stability can't be reached after a specified time,
	 * the method throws a {@link IllegalStateException}.
	 * @param context the bundle context
	 * @throws IllegalStateException when the stability can't be reach after a several attempts.
	 */
	protected void waitForiPojoFactoriesStability(BundleContext context) throws IllegalStateException {

		int count = 0;
		boolean serviceStability = false;
		int count1 = 0;
		int count2 = 0;
		while (! serviceStability && count < 500) {
			try {
				ServiceReference[] refs = context.getServiceReferences(Factory.class.getName(), null);
				count1 = refs.length;
				Thread.sleep(500);
				refs = context.getServiceReferences(Factory.class.getName(), "(factory.state="+Factory.VALID+")");
				count2 = refs.length;
				serviceStability = count1 == count2;
			} catch (Exception e) {
				System.err.println(e);
				serviceStability = false;
				// Nothing to do, while recheck the condition
			}
			count++;
		}

		if (count >= 500) {
			System.err.println("Service stability isn't reached after 500 tries (" + count1 + " != " + count2);
			throw new IllegalStateException("Cannot reach the service stability");
		}
	}


    protected <S> S getService(BundleContext context, Class clazz){
        ServiceReference<S> sr = context.getServiceReference(clazz);

        if (sr == null){
            return null;
        }
        S service = context.getService(sr);
        return service;
    }
}