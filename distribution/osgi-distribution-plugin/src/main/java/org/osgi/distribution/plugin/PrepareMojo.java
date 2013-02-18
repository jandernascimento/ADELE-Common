package org.osgi.distribution.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

/**
 * Goal which prepares a distribution for the zip mojo.
 * 
 * @goal prepare
 * 
 * @phase prepare-package
 * @requiresDependencyResolution
 */
public class PrepareMojo extends AbstractMojo {

	protected String distribDirectory;

	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The Maven Session Object
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	protected MavenSession session;

	/**
	 * The Maven PluginManager Object
	 * 
	 * @component
	 * @required
	 */
	protected BuildPluginManager pluginManager;

	/**
	 * Maven plugin output metadata
	 * 
	 * @parameter expression=${output}
	 * @readonly
	 */
	protected OutputMetadata output;

	/**
	 * Execute that mojo.
	 */
	public void execute() throws MojoExecutionException {
		distribDirectory = project.getProperties().getProperty(
				"distribDirectory");
		manageDependencies();
	}

	/**
	 * gather dependencies as dom element.
	 * 
	 * @throws MojoExecutionException
	 */
	private void manageDependencies() throws MojoExecutionException {
		List<Dependency> dependencies = project.getDependencies();

		if (dependencies != null) {
			for (Object depObj : dependencies) {
				if (!(depObj instanceof Dependency))
					continue;

				Dependency dep = (Dependency) depObj;

				if (dep.getType().equals("osgi-distribution")) {
					copyDependency(dep);
					unzipOsgiDistribution(dep);
				} else {
					copyDependency(dep);
				}
			}
		}
	}

	/**
	 * Unzip an osgi distribution dependency into distribution folder.
	 * 
	 * @param dep
	 * @throws MojoExecutionException
	 */
	private void unzipOsgiDistribution(Dependency dep)
			throws MojoExecutionException {

		String zipFinalPathName = distribDirectory + File.separator
				+ dep.getArtifactId() + "-" + dep.getVersion() + ".zip";
		Xpp3Dom config = MojoExecutor.configuration(
				MojoExecutor.element("from", zipFinalPathName),
				MojoExecutor.element("to", distribDirectory));

		MojoExecutor.executeMojo(MojoExecutor.plugin("org.codehaus.mojo",
				"truezip-maven-plugin", "1.1"), MojoExecutor.goal("cp"),
				config, MojoExecutor.executionEnvironment(project, session,
						pluginManager));
	}

	/**
	 * Copy the dependency given as parameter into default folder, or given
	 * output folder.
	 * 
	 * @param dep
	 * @throws MojoExecutionException
	 */
	private void copyDependency(Dependency dep) throws MojoExecutionException {

		Xpp3Dom config = MojoExecutor.configuration(
				MojoExecutor.element("overWriteSnapshots", "true"),
				MojoExecutor.element("overWriteIfNewer", "true"));

		if (dep.getType().equals("osgi-distribution")) {
			config.addChild(MojoExecutor.element("outputDirectory",
					distribDirectory).toDom());
		} else {
			if (output != null) {
				// send to right place
				config.addChild(MojoExecutor.element("outputDirectory",
						distribDirectory + output.getDirectory()).toDom());
			} else {
				// default : send all dependencies to load folder
				config.addChild(MojoExecutor.element("outputDirectory",
						distribDirectory + "load").toDom());
			}
		}

		Xpp3Dom items = null;
		items = new Xpp3Dom("artifactItems");
		Xpp3Dom itemAsDom = new Xpp3Dom("artifactItem");
		itemAsDom.addChild(MojoExecutor.element("groupId", dep.getGroupId())
				.toDom());
		itemAsDom.addChild(MojoExecutor.element("artifactId",
				dep.getArtifactId()).toDom());
		itemAsDom.addChild(MojoExecutor.element("version", dep.getVersion())
				.toDom());
		itemAsDom.addChild(MojoExecutor.element("type", dep.getType()).toDom());
		if (output != null
				&& dep.getArtifactId().equals(output.getIncludesArtifactId())) {
			itemAsDom.addChild(MojoExecutor.element("destFileName",
					output.getOutputFileName()).toDom());
		}
		items.addChild(itemAsDom);
		config.addChild(items);

		MojoExecutor.executeMojo(
				MojoExecutor.plugin("org.apache.maven.plugins",
						"maven-dependency-plugin", "2.5.1"), MojoExecutor
						.goal("copy"), config, MojoExecutor
						.executionEnvironment(project, session, pluginManager));
	}

}
