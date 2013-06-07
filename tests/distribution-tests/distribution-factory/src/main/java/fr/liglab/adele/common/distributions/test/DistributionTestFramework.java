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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.felix.framework.Felix;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
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

    @Override
    public <A> A adapt(Class<A> type) {
        return super.adapt(type);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public long getBundleId() {
        return super.getBundleId();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public long getLastModified() {
        return super.getLastModified();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int getPersistentState() {
        return super.getPersistentState();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setPersistentStateInactive() {
        super.setPersistentStateInactive();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setPersistentStateActive() {
        super.setPersistentStateActive();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setPersistentStateUninstalled() {
        super.setPersistentStateUninstalled();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(Object obj) {
        return super.hasPermission(obj);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
	 * @throws BundleException
	 * @see org.osgi.framework.launch.Framework#init()
	 */
	public void init() throws BundleException {
		super.init();
		AutoProcessor.process(felixConfiguration, getBundleContext());
	}

    /**
     * This method starts the framework instance, which will transition the
     * framework from start level 0 to its active start level as specified in
     * its configuration properties (1 by default). If the <tt>init()</tt> was
     * not explicitly invoked before calling this method, then it will be
     * implicitly invoked before starting the framework.
     *
     * @throws org.osgi.framework.BundleException
     *          if any error occurs.
     */
    @Override
    public void start() throws BundleException {
        super.start();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void start(int options) throws BundleException {
        super.start(options);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * This method asynchronously shuts down the framework, it must be called at the
     * end of a session in order to shutdown all active bundles.
     */
    @Override
    public void stop() throws BundleException {
        super.stop();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void stop(int options) throws BundleException {
        super.stop(options);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * This method will cause the calling thread to block until the framework
     * shuts down.
     *
     * @param timeout A timeout value.
     * @throws InterruptedException If the thread was interrupted.
     */
    @Override
    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        return super.waitForStop(timeout);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void uninstall() throws BundleException {
        super.uninstall();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void update() throws BundleException {
        super.update();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void update(InputStream is) throws BundleException {
        super.update(is);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public <S> Set<ServiceReference<S>> getHooks(Class<S> hookClass) {
        return super.getHooks(hookClass);    //To change body of overridden methods use File | Settings | File Templates.
    }


}
