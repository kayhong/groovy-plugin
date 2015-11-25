package com.sap.smartbi.devops.plugin.hcp.html5.descriptor;

public final class Target {

	private String name;
	private String path;
	private String type;
	private String version;
	
	public String getName() {
		return this.name;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getType() {
		return this.type;
	}

	public String getVersion() {
		return this.version;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setType(final String type) {
		this.type = type;
	}
	
	public void setVersion(final String version) {
		this.version = version;
	}
}
