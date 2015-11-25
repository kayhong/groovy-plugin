package com.sap.smartbi.devops.plugin.hcp.html5.descriptor;

public final class Route {
	private String description;
	private String path;
	private Target target;
	
	public String getDescription() {
		return this.description;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public Target getTarget() {
		return this.target;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setPath(final String path) {
		this.path = path;
	}
	
	public void setTarget(final Target target) {
		this.target = target;
	}
}
