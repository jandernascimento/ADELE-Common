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
package fr.liglab.adele.icasa.distributions.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.Felix;
import org.apache.felix.main.Main;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

public class DistributionFrameworkFactory implements FrameworkFactory {

	private String BUNDLESDIR = "/bundle/";
	private String LOADDIR = "/load/";
	private String ROOTDIR = "./target/distribution/";

	/* (non-Javadoc)
	 * @see org.osgi.framework.launch.FrameworkFactory#newFramework(java.util.Map)
	 */
	@Override
	public Framework newFramework(Map configuration) {
		// Load system properties.
		Main.loadSystemProperties();

		// Read configuration properties.
		Properties configProps = Main.loadConfigProperties();

		// If no configuration properties were found, then create
		// an empty properties object.
		if (configProps == null)
		{
			System.err.println("No " + Main.CONFIG_PROPERTIES_FILE_VALUE + " found.");
			configProps = new Properties();
		}


		// Copy framework properties from the system properties.
		Main.copySystemProperties(configProps);
		configProps.putAll(configuration);
		configProps.put("felix.auto.deploy.action", "install, start");
		try {
			configProps.put("felix.fileinstall.dir", getLoadFolder());
			configProps.put("felix.auto.deploy.dir", getBundleFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		DistributionTestFramework framework = new DistributionTestFramework(configProps);
		return framework;
	}


	/**
	 * @return
	 */
	private String getListOfBundles() {
		StringBuilder listOfBundles = new StringBuilder();
		String currentpath = null;
		try {
			currentpath =  new File("./target/distribution/").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Get the bundle folder.
		File folder = null;
		try {
			folder = new File(getBundleFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}

		File[] listOfFiles = folder.listFiles(); 

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String file = listOfFiles[i].getName();
				if (file.endsWith(".jar") && !file.contains("org.osgi.compendium")) { // It is added by pax exam. We skip to avoid test fail
					try {
						listOfBundles.append("file://");
						listOfBundles.append(listOfFiles[i].getCanonicalPath());
						listOfBundles.append(" ");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return listOfBundles.toString();
	}
	private String getBundleFolder() throws IOException{
		File bundle = new File(getRootDir() + BUNDLESDIR);
		if (bundle.exists() && bundle.isDirectory()) {
			return bundle.getCanonicalPath();
		}
		throw new IOException("Unable to locate bundle directory in: " + bundle.getName());
	}

	private String getLoadFolder() throws IOException{
		File bundle = new File(getRootDir() + LOADDIR);
		if (bundle.exists() && bundle.isDirectory()) {
			return bundle.getCanonicalPath();
		}

		throw new IOException("Unable to locate bundle directory in: " + bundle.getName());
	}

	private String getRootDir() throws IOException {
		File root = new File(ROOTDIR);
		File[] listOfFiles = root.listFiles(); 
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				return listOfFiles[i].getCanonicalPath(); //it must be only one directory.
			}
		}
		throw new IOException("Unable to locate root directory in: " + root.getName());
	}

	private class DistributionTestFramework implements Framework {

		Framework _fwk = null;

		Map felixConfiguration = new HashMap();

		private DistributionTestFramework(Map configuration){
			felixConfiguration.putAll(configuration);
			_fwk = new Felix(felixConfiguration);
		}
		/**
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#init()
		 */
		public void init() throws BundleException {
			_fwk.init();
			AutoProcessor.process(felixConfiguration, _fwk.getBundleContext());
		}
		/**
		 * @param timeout
		 * @return
		 * @throws InterruptedException
		 * @see org.osgi.framework.launch.Framework#waitForStop(long)
		 */
		public FrameworkEvent waitForStop(long timeout)
				throws InterruptedException {
			return _fwk.waitForStop(timeout);
		}
		/**
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#start()
		 */
		public void start() throws BundleException {
			_fwk.start();
		}
		/**
		 * @param options
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#start(int)
		 */
		public void start(int options) throws BundleException {
			_fwk.start(options);
		}
		/**
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#stop()
		 */
		public void stop() throws BundleException {
			_fwk.stop();
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getState()
		 */
		public int getState() {
			return _fwk.getState();
		}
		/**
		 * @param options
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#stop(int)
		 */
		public void stop(int options) throws BundleException {
			_fwk.stop(options);
		}
		/**
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#uninstall()
		 */
		public void uninstall() throws BundleException {
			_fwk.uninstall();
		}
		/**
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#update()
		 */
		public void update() throws BundleException {
			_fwk.update();
		}
		/**
		 * @param in
		 * @throws BundleException
		 * @see org.osgi.framework.launch.Framework#update(java.io.InputStream)
		 */
		public void update(InputStream in) throws BundleException {
			_fwk.update(in);
		}
		/**
		 * @return
		 * @see org.osgi.framework.launch.Framework#getBundleId()
		 */
		public long getBundleId() {
			return _fwk.getBundleId();
		}
		/**
		 * @return
		 * @see org.osgi.framework.launch.Framework#getLocation()
		 */
		public String getLocation() {
			return _fwk.getLocation();
		}
		/**
		 * @return
		 * @see org.osgi.framework.launch.Framework#getSymbolicName()
		 */
		public String getSymbolicName() {
			return _fwk.getSymbolicName();
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getHeaders()
		 */
		public Dictionary getHeaders() {
			return _fwk.getHeaders();
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getRegisteredServices()
		 */
		public ServiceReference[] getRegisteredServices() {
			return _fwk.getRegisteredServices();
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getServicesInUse()
		 */
		public ServiceReference[] getServicesInUse() {
			return _fwk.getServicesInUse();
		}
		/**
		 * @param permission
		 * @return
		 * @see org.osgi.framework.Bundle#hasPermission(java.lang.Object)
		 */
		public boolean hasPermission(Object permission) {
			return _fwk.hasPermission(permission);
		}
		/**
		 * @param name
		 * @return
		 * @see org.osgi.framework.Bundle#getResource(java.lang.String)
		 */
		public URL getResource(String name) {
			return _fwk.getResource(name);
		}
		/**
		 * @param locale
		 * @return
		 * @see org.osgi.framework.Bundle#getHeaders(java.lang.String)
		 */
		public Dictionary getHeaders(String locale) {
			return _fwk.getHeaders(locale);
		}
		/**
		 * @param name
		 * @return
		 * @throws ClassNotFoundException
		 * @see org.osgi.framework.Bundle#loadClass(java.lang.String)
		 */
		public Class loadClass(String name) throws ClassNotFoundException {
			return _fwk.loadClass(name);
		}
		/**
		 * @param name
		 * @return
		 * @throws IOException
		 * @see org.osgi.framework.Bundle#getResources(java.lang.String)
		 */
		public Enumeration getResources(String name) throws IOException {
			return _fwk.getResources(name);
		}
		/**
		 * @param path
		 * @return
		 * @see org.osgi.framework.Bundle#getEntryPaths(java.lang.String)
		 */
		public Enumeration getEntryPaths(String path) {
			return _fwk.getEntryPaths(path);
		}
		/**
		 * @param path
		 * @return
		 * @see org.osgi.framework.Bundle#getEntry(java.lang.String)
		 */
		public URL getEntry(String path) {
			return _fwk.getEntry(path);
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getLastModified()
		 */
		public long getLastModified() {
			return _fwk.getLastModified();
		}
		/**
		 * @param path
		 * @param filePattern
		 * @param recurse
		 * @return
		 * @see org.osgi.framework.Bundle#findEntries(java.lang.String, java.lang.String, boolean)
		 */
		public Enumeration findEntries(String path, String filePattern,
				boolean recurse) {
			return _fwk.findEntries(path, filePattern, recurse);
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getBundleContext()
		 */
		public BundleContext getBundleContext() {
			return _fwk.getBundleContext();
		}
		/**
		 * @param signersType
		 * @return
		 * @see org.osgi.framework.Bundle#getSignerCertificates(int)
		 */
		public Map getSignerCertificates(int signersType) {
			return _fwk.getSignerCertificates(signersType);
		}
		/**
		 * @return
		 * @see org.osgi.framework.Bundle#getVersion()
		 */
		public Version getVersion() {
			return _fwk.getVersion();
		}

	}
}
