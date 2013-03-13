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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.util.Util;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

public class DistributionFrameworkFactory implements FrameworkFactory {

	private String BUNDLESDIR = "/bundle/";
	private String LOADDIR = "/load/";
	private String ROOTDIR = "./target/distribution/";
	 /**
     * Switch for specifying bundle directory.
    **/
    public static final String BUNDLE_DIR_SWITCH = "-b";

    /**
     * The property name used to specify whether the launcher should
     * install a shutdown hook.
    **/
    public static final String SHUTDOWN_HOOK_PROP = "felix.shutdown.hook";
    /**
     * The property name used to specify an URL to the system
     * property file.
    **/
    public static final String SYSTEM_PROPERTIES_PROP = "felix.system.properties";
    /**
     * The default name used for the system properties file.
    **/
    public static final String SYSTEM_PROPERTIES_FILE_VALUE = "system.properties";
    /**
     * The property name used to specify an URL to the configuration
     * property file to be used for the created the framework instance.
    **/
    public static final String CONFIG_PROPERTIES_PROP = "felix.config.properties";
    /**
     * The default name used for the configuration properties file.
    **/
    public static final String CONFIG_PROPERTIES_FILE_VALUE = "config.properties";
    /**
     * Name of the configuration directory.
     */
    public static final String CONFIG_DIRECTORY = "conf";

	/* (non-Javadoc)
	 * @see org.osgi.framework.launch.FrameworkFactory#newFramework(java.util.Map)
	 */
	@Override
	public Framework newFramework(Map configuration) {
		// Load system properties.
		System.out.println("[INFO] initializing Distribution Test Framework");
		loadSystemProperties();

		// Read configuration properties.
		Map configProps = loadConfigProperties();

		// If no configuration properties were found, then create
		// an empty properties object.
		if (configProps == null)
		{
			System.err.println("No " + CONFIG_PROPERTIES_FILE_VALUE + " found.");
			configProps = new Properties();
		}


		// Copy framework properties from the system properties.
		copySystemProperties(configProps);
		configProps.putAll(configuration);
		configProps.put("felix.auto.deploy.action", "install, start");
		try {
			configProps.put("felix.fileinstall.dir", getLoadFolder());
		} catch (IOException e1) {

		}
		try {
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

	 public static void loadSystemProperties()
	    {
	        // The system properties file is either specified by a system
	        // property or it is in the same directory as the Felix JAR file.
	        // Try to load it from one of these places.

	        // See if the property URL was specified as a property.
	        URL propURL = null;
	        String custom = System.getProperty(SYSTEM_PROPERTIES_PROP);
	        if (custom != null)
	        {
	            try
	            {
	                propURL = new URL(custom);
	            }
	            catch (MalformedURLException ex)
	            {
	                System.err.print("Main: " + ex);
	                return;
	            }
	        }
	        else
	        {
	            // Determine where the configuration directory is by figuring
	            // out where felix.jar is located on the system class path.
	            File confDir = null;
	            String classpath = System.getProperty("java.class.path");
	            int index = classpath.toLowerCase().indexOf("felix.jar");
	            int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;
	            if (index >= start)
	            {
	                // Get the path of the felix.jar file.
	                String jarLocation = classpath.substring(start, index);
	                // Calculate the conf directory based on the parent
	                // directory of the felix.jar directory.
	                confDir = new File(
	                    new File(new File(jarLocation).getAbsolutePath()).getParent(),
	                    CONFIG_DIRECTORY);
	            }
	            else
	            {
	                // Can't figure it out so use the current directory as default.
	                confDir = new File(System.getProperty("user.dir"), CONFIG_DIRECTORY);
	            }

	            try
	            {
	                propURL = new File(confDir, SYSTEM_PROPERTIES_FILE_VALUE).toURL();
	            }
	            catch (MalformedURLException ex)
	            {
	                System.err.print("Main: " + ex);
	                return;
	            }
	        }

	        // Read the properties file.
	        Properties props = new Properties();
	        InputStream is = null;
	        try
	        {
	            is = propURL.openConnection().getInputStream();
	            props.load(is);
	            is.close();
	        }
	        catch (FileNotFoundException ex)
	        {
	            // Ignore file not found.
	        }
	        catch (Exception ex)
	        {
	            System.err.println(
	                "Main: Error loading system properties from " + propURL);
	            System.err.println("Main: " + ex);
	            try
	            {
	                if (is != null) is.close();
	            }
	            catch (IOException ex2)
	            {
	                // Nothing we can do.
	            }
	            return;
	        }

	        // Perform variable substitution on specified properties.
	        for (Enumeration e = props.propertyNames(); e.hasMoreElements(); )
	        {
	            String name = (String) e.nextElement();
	            System.setProperty(name,
	                Util.substVars(props.getProperty(name), name, null, null));
	        }
	    }

	    /**
	     * <p>
	     * Loads the configuration properties in the configuration property file
	     * associated with the framework installation; these properties
	     * are accessible to the framework and to bundles and are intended
	     * for configuration purposes. By default, the configuration property
	     * file is located in the <tt>conf/</tt> directory of the Felix
	     * installation directory and is called "<tt>config.properties</tt>".
	     * The installation directory of Felix is assumed to be the parent
	     * directory of the <tt>felix.jar</tt> file as found on the system class
	     * path property. The precise file from which to load configuration
	     * properties can be set by initializing the "<tt>felix.config.properties</tt>"
	     * system property to an arbitrary URL.
	     * </p>
	     * @return A <tt>Properties</tt> instance or <tt>null</tt> if there was an error.
	    **/
	    public static Map<String, String> loadConfigProperties()
	    {
	        // The config properties file is either specified by a system
	        // property or it is in the conf/ directory of the Felix
	        // installation directory.  Try to load it from one of these
	        // places.

	        // See if the property URL was specified as a property.
	        URL propURL = null;
	        String custom = System.getProperty(CONFIG_PROPERTIES_PROP);
	        if (custom != null)
	        {
	            try
	            {
	                propURL = new URL(custom);
	            }
	            catch (MalformedURLException ex)
	            {
	                System.err.print("Main: " + ex);
	                return null;
	            }
	        }
	        else
	        {
	            // Determine where the configuration directory is by figuring
	            // out where felix.jar is located on the system class path.
	            File confDir = null;
	            String classpath = System.getProperty("java.class.path");
	            int index = classpath.toLowerCase().indexOf("felix.jar");
	            int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;
	            if (index >= start)
	            {
	                // Get the path of the felix.jar file.
	                String jarLocation = classpath.substring(start, index);
	                // Calculate the conf directory based on the parent
	                // directory of the felix.jar directory.
	                confDir = new File(
	                    new File(new File(jarLocation).getAbsolutePath()).getParent(),
	                    CONFIG_DIRECTORY);
	            }
	            else
	            {
	                // Can't figure it out so use the current directory as default.
	                confDir = new File(System.getProperty("user.dir"), CONFIG_DIRECTORY);
	            }

	            try
	            {
	                propURL = new File(confDir, CONFIG_PROPERTIES_FILE_VALUE).toURL();
	            }
	            catch (MalformedURLException ex)
	            {
	                System.err.print("Main: " + ex);
	                return null;
	            }
	        }

	        // Read the properties file.
	        Properties props = new Properties();
	        InputStream is = null;
	        try
	        {
	            // Try to load config.properties.
	            is = propURL.openConnection().getInputStream();
	            props.load(is);
	            is.close();
	        }
	        catch (Exception ex)
	        {
	            // Try to close input stream if we have one.
	            try
	            {
	                if (is != null) is.close();
	            }
	            catch (IOException ex2)
	            {
	                // Nothing we can do.
	            }

	            return null;
	        }

	        // Perform variable substitution for system properties and
	        // convert to dictionary.
	        Map<String, String> map = new HashMap<String, String>();
	        for (Enumeration e = props.propertyNames(); e.hasMoreElements(); )
	        {
	            String name = (String) e.nextElement();
	            map.put(name,
	                Util.substVars(props.getProperty(name), name, null, props));
	        }

	        return map;
	    }

	    public static void copySystemProperties(Map configProps)
	    {
	        for (Enumeration e = System.getProperties().propertyNames();
	             e.hasMoreElements(); )
	        {
	            String key = (String) e.nextElement();
	            if (key.startsWith("felix.") || key.startsWith("org.osgi.framework."))
	            {
	                configProps.put(key, System.getProperty(key));
	            }
	        }
	    }
	
	}
