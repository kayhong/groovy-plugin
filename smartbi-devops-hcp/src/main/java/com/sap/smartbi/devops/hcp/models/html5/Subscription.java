package com.sap.smartbi.devops.hcp.models.html5;

import java.net.URI;

public interface Subscription {
	String getActiveVersion();
	
	String getName();
	
	String getProviderAccount();

	String getProviderName();
	
	String getProviderStatus();
	
	String getStartedVersion();
	
	URI getUri();
}
