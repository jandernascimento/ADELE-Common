/*
 * Copyright Adele Team LIG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.liglab.adele.common.distributions.test;


import java.util.HashMap;
import java.util.Map;


import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;


/**
 * @author <a href="mailto:cilia-devel@lists.ligforge.imag.fr">Cilia Project
 *         Team</a>
 */
public class DistributionTestFramework extends Felix {

	Map felixConfiguration = new HashMap();

	protected DistributionTestFramework(Map configuration){
		super(configuration);
		felixConfiguration.putAll(configuration);
	}

    /**
	 * @throws BundleException
	 * @see org.osgi.framework.launch.Framework#init()
	 */
	public void init() throws BundleException {
		super.init();
		AutoProcessor.process(felixConfiguration, getBundleContext());
	}

    public void start() throws BundleException{
        super.start();
        waitForStability(getBundleContext());
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
        // Wait for bundle initialization.
        boolean bundleStability = getBundleStability(context);
        int count = 0;
        System.out.println("To reach stability");
        while (!bundleStability && count < 500) {
            System.out.println("Waiting for stability" + count);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Interrupted
            }
            count++;
            bundleStability = getBundleStability(context);
        }

        if (count >= 500) {
            System.err.println("Bundle stability isn't reached after 500 tries");
            showUnstableBundles(context);
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
                Thread.sleep(50);
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

        if (count >= 500) {
            System.err.println("Service stability isn't reached after 500 tries (" + count1 + " != " + count2);
            showUnstableBundles(context);
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
        }
        return stability;
    }
    /**
     * Are bundle stables.
     * @param bc the bundle context
     * @return <code>true</code> if every bundles are activated.
     */
    private boolean showUnstableBundles(BundleContext bc) {
        boolean stability = true;
        Bundle[] bundles = bc.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            int state = bundles[i].getState();
            stability = stability && ((state == Bundle.ACTIVE) || (state == Bundle.RESOLVED));
            if (!((state == Bundle.ACTIVE) || (state == Bundle.RESOLVED))){
                System.err.println("Waiting to stability for: " + bundles[i].getSymbolicName() +" : "+ state);
            }
        }
        return stability;
    }


}
