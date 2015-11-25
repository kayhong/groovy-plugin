package com.sap.smartbi.devops.hcp.internal.models.html5.descriptor;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationDescriptor;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRoute;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationSecurityConstraint;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.AuthenticationMethod;

public final class ApplicationDescriptorImpl implements
		ApplicationDescriptor {

	private AuthenticationMethod authenticationMethod;
	private URI logoutUri;
	private boolean redirectWelcomePage;
	private ApplicationRoute[] routes;
	private URI welcomeUri;
	private ApplicationSecurityConstraint[] constraints;

	@Override
	@XmlElement(name = "authenticationMethod", required = false)
	public AuthenticationMethod getAuthenticationMethod() {
		return this.authenticationMethod;
	}

	@Override
	@XmlElement(name = "logoutPage", required = false)
	public URI getLogoutUri() {
		return this.logoutUri;
	}

	@Override
	@XmlElement(name = "sendWelcomeFileRedirect")
	public boolean getRedirectWelcomePage() {
		return this.redirectWelcomePage;
	}

	@Override
	@XmlElements({ @XmlElement(type = ApplicationRouteImpl.class) })
	public ApplicationRoute[] getRoutes() {
		return this.routes;
	}

	@Override
	@XmlElements({ @XmlElement(type = ApplicationSecurityConstraintImpl.class) })
	public ApplicationSecurityConstraint[] getSecurityConstraints() {
		return this.constraints;
	}

	@Override
	@XmlElement(name = "welcomeFile", required = false)
	public URI getWelcomeUri() {
		return this.welcomeUri;
	}

	@Override
	public void setAuthenticationMethod(AuthenticationMethod method) {
		this.authenticationMethod = method;
	}

	@Override
	public void setLogoutUri(final URI uri) {
		this.logoutUri = uri;
	}

	@Override
	public void setRedirectWelcomePage(boolean redirect) {
		this.redirectWelcomePage = redirect;
	}

	@Override
	public void setRoutes(ApplicationRoute[] routes) {
		this.routes = routes;
	}

	@Override
	public void setSecurityConstraints(
			final ApplicationSecurityConstraint[] constraints) {
		this.constraints = constraints;
	}

	@Override
	public void setWelcomeUri(URI uri) {
		this.welcomeUri = uri;
	}

}
