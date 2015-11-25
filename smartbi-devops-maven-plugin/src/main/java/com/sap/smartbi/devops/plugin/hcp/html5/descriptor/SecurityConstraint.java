package com.sap.smartbi.devops.plugin.hcp.html5.descriptor;


public final class SecurityConstraint {

	private String permissionName;
	private String[] protectedPaths;
	
	public String getPermissionName() {
		return this.permissionName;
	}
	
	public String[] getProtectedPaths() {
		return this.protectedPaths;
	}

	public void setPermissionName(final String permissionName) {
		this.permissionName = permissionName;
	}
	
	public void setProtectedPaths(final String[] paths) {
		this.protectedPaths = paths;
	}
}
