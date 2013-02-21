package org.osgi.distribution.plugin.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.Before;
import org.junit.Test;

public class SimpleTest {

	File testDir;

	// new File("src/test/resources/simple");

	@Before
	public void setUp() throws VerificationException, IOException {

		Verifier verifier;

		testDir = ResourceExtractor.simpleExtractResources(getClass(),
				"/simple");
		
		System.out.println(testDir.getAbsolutePath());
		
		verifier = new Verifier(testDir.getAbsolutePath());
		verifier.deleteArtifact(Helper.TEST_GROUP_ID, "test-simple",
				Helper.TEST_VERSION, "zip");

		verifier.executeGoal("clean");
	}

	@Test
	public void testInstall() throws Exception {

		testDir = ResourceExtractor.simpleExtractResources(getClass(),
				"/simple");
		Verifier verifier = new Verifier(testDir.getAbsolutePath());

		// System.out.println("basedir : "+verifier.getBasedir());
		 List<String> cliOptions = new ArrayList<String>();
		 cliOptions.add("-N");
		 verifier.setCliOptions(cliOptions);

		// System.out.println("before install");
		 verifier.executeGoal("install");
		// System.out.println("after install");

//		CustomMavenLauncher launcher = new CustomMavenLauncher(
//				findDefaultMavenHome(), false);
//
//		String[] cliArgs = { "clean", "install", "-X" };
//		int ret = launcher.run(cliArgs, testDir.getAbsolutePath(),
//				new File(testDir.getAbsolutePath(), "log.txt"));

//		Assert.assertEquals(0, ret);
		verifier.verifyErrorFreeLog();
		verifier.resetStreams();

		// Check existency
		File result = new File(testDir
				+ "/target/test-simple-0.0.1-SNAPSHOT.zip"); // Expected name
		Assert.assertTrue(result.exists());

		ZipFile zip = new ZipFile(result);
		ZipEntry zentry = zip.getEntry("felix.jar");
		Assert.assertNotNull(zentry);

	}

//	private String findDefaultMavenHome() throws VerificationException {
//		String defaultMavenHome = System.getProperty("maven.home");
//
//		if (defaultMavenHome == null) {
//			try {
//				Properties envVars = CommandLineUtils.getSystemEnvVars();
//				defaultMavenHome = envVars.getProperty("M2_HOME");
//			} catch (IOException e) {
//				throw new VerificationException(
//						"Cannot read system environment variables.", e);
//			}
//		}
//
//		if (defaultMavenHome == null) {
//			File f = new File(System.getProperty("user.home"), "m2");
//			if (new File(f, "bin/mvn").isFile()) {
//				defaultMavenHome = f.getAbsolutePath();
//			}
//		}
//		return defaultMavenHome;
//	}
}