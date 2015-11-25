package com.sap.smartbi.devops.hcp.models.html5.descriptor;

public interface ApplicationSecurityConstraint {

	String getDescription();
	
	String[] getExcludedPaths();
	
	String getPermissionName();
	
	String[] getProtectedPaths();
	
	void setDescription(final String description);
	
	void setExcludedPaths(final String[] paths);
}
