package org.osgi.distribution.plugin;

import java.util.HashMap;
import java.util.Map;

public class OutputMetadata {
	
	private String includesArtifactId = null;
	private String directory = null;
	private String outputFileName = null;
//	Map<String, String> m_output = null;
	
	public OutputMetadata(){
//		m_output = new HashMap<String, String>();
		
	}

	public String getIncludesArtifactId() {
		return includesArtifactId;
	}

	public void setIncludesArtifactId(String includesArtifactId) {
		this.includesArtifactId = includesArtifactId;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

//	public Map<String, String> getM_output() {
//		return m_output;
//	}
//
//	public void setM_output(Map<String, String> m_output) {
//		this.m_output = m_output;
//	}
	
	

}
