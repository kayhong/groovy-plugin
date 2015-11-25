package com.sap.smartbi.devops.hcp.models.html5.descriptor;

import java.net.URI;

public interface ApplicationRouteTarget {

	String getName();
	
	URI getPath();
	
	ApplicationRouteTargetType getType();
	
	String getVersion();
	
	void setPath(final URI path);
	
	// TODO type (destination | service | application)
	void setVersion(final String version);
}
