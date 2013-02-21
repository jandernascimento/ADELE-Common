/**
 * 
 */
package org.osgi.distribution.plugin.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import org.apache.maven.it.util.cli.CommandLineException;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;

/**
 * @author tfqg0024
 * 
 */
public class CustomMavenLauncher {

	private final String mavenHome;

	private final String executable;

	public CustomMavenLauncher(String mavenHome) {
		this(mavenHome, false);
	}

	public CustomMavenLauncher(String mavenHome, boolean debugJvm) {
		this.mavenHome = mavenHome;

		String script = debugJvm ? "mvnDebug" : "mvn";

		if (mavenHome != null) {
			executable = new File(mavenHome, "bin/" + script).getPath();
		} else {
			executable = script;
		}
	}

	public int run(String[] cliArgs, Map envVars, String workingDirectory,
			File logFile) throws Exception {
		Commandline cmd = new Commandline();

		cmd.setExecutable(executable);

		if (mavenHome != null) {
			cmd.addEnvironment("M2_HOME", mavenHome);
		}

		if (envVars != null) {
			for (Object o : envVars.keySet()) {
				String key = (String) o;

				cmd.addEnvironment(key, (String) envVars.get(key));
			}
		}

		if (envVars == null || envVars.get("JAVA_HOME") == null) {
			cmd.addEnvironment("JAVA_HOME", System.getProperty("java.home"));
		}

		cmd.addEnvironment("MAVEN_TERMINATE_CMD", "on");

		cmd.setWorkingDirectory(workingDirectory);

		for (String cliArg : cliArgs) {
			cmd.createArgument().setValue(cliArg);
		}

		Writer logWriter = new FileWriter(logFile);

		StreamConsumer out = new WriterStreamConsumer(logWriter);

		StreamConsumer err = new WriterStreamConsumer(logWriter);
		
		System.out.println("Commande : " + cmd);
		for (String arg : cmd.getShell().getShellArgs()){
			System.out.println(arg);
		}
		System.out.println(cmd);
		System.out.println(cmd.getWorkingDirectory().getAbsolutePath());
		try {
			return CommandLineUtils.executeCommandLine(cmd, out, err);
		} catch (CommandLineException e) {
			throw new Exception("Failed to run Maven: "
					+ e.getMessage() + "\n" + cmd, e);
		} finally {
			logWriter.close();
		}
	}

	public int run(String[] cliArgs, String workingDirectory, File logFile)
			throws Exception {
		return run(cliArgs, Collections.EMPTY_MAP, workingDirectory, logFile);
	}
}
