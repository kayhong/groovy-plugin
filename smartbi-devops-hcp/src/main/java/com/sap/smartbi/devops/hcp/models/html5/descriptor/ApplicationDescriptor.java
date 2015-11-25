package com.sap.smartbi.devops.hcp.models.html5.descriptor;

import java.net.URI;

public interface ApplicationDescriptor {

	AuthenticationMethod getAuthenticationMethod();
	
	URI getLogoutUri();
	
	boolean getRedirectWelcomePage();
	
	ApplicationRoute[] getRoutes();
	
	ApplicationSecurityConstraint[] getSecurityConstraints();
	
	URI getWelcomeUri();
	
	void setAuthenticationMethod(final AuthenticationMethod method);

	void setLogoutUri(final URI uri);
	
	void setRedirectWelcomePage(boolean redirect);
	
	void setRoutes(final ApplicationRoute[] routes);
	
	void setSecurityConstraints(final ApplicationSecurityConstraint[] constraints);

	void setWelcomeUri(final URI uri);
}
