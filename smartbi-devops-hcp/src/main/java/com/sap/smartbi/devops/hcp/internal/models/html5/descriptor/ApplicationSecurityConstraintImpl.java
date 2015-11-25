package com.sap.smartbi.devops.hcp.internal.models.html5.descriptor;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationSecurityConstraint;

public final class ApplicationSecurityConstraintImpl implements ApplicationSecurityConstraint {

	private String description;
	private String[] excludedPaths;
	private String permissionName;
	private String[] protectedPaths;
	
	@Override
	@XmlElement(name = "description", required = false)
	public String getDescription() {
		return this.description;
	}

	@Override
	@XmlElement(name = "excludedPaths", required = false)
	public String[] getExcludedPaths() {
		return this.excludedPaths;
	}

	@Override
	@XmlElement(name = "permission")
	public String getPermissionName() {
		return this.permissionName;
	}

	@Override
	@XmlElement(name = "protectedPaths", required = false)
	public String[] getProtectedPaths() {
		return this.protectedPaths;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setExcludedPaths(String[] paths) {
		this.excludedPaths = paths;
	}

	public void setPermissionName(String name) {
		this.permissionName = name;
	}

	public void setProtectedPaths(String[] paths) {
		this.protectedPaths = paths;
	}

}
