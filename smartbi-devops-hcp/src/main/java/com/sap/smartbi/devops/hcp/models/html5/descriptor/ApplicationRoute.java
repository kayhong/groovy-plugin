package com.sap.smartbi.devops.hcp.models.html5.descriptor;

public interface ApplicationRoute {

	String getDescription();
	
	String getPath();
	
	ApplicationRouteTarget getTarget();
	
	void setDescription(final String description);
}
