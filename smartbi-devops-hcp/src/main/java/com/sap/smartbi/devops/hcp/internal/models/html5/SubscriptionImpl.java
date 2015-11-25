package com.sap.smartbi.devops.hcp.internal.models.html5;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.html5.Subscription;

public final class SubscriptionImpl implements Subscription {

	private String activeVersion;
	private boolean createStickyCandidate;
	private String name;
	private String providerAccount;
	private String providerName;
	private String startedVersion;
	private String providerStatus;
	private URI uri;

	@Override
	@XmlElement(name = "activeVersion")
	public String getActiveVersion() {
		return this.activeVersion;
	}

	@XmlElement(name = "createStickyCandidate")
	public boolean getCreateStickyCandidate() {
		return this.createStickyCandidate;
	}

	@Override
	@XmlElement(name = "name")
	public String getName() {
		return this.name;
	}

	@Override
	@XmlElement(name = "providerAccount")
	public String getProviderAccount() {
		return this.providerAccount;
	}

	@Override
	@XmlElement(name = "providerName")
	public String getProviderName() {
		return this.providerName;
	}

	@Override
	@XmlElement(name = "status")
	public String getProviderStatus() {
		return this.providerStatus;
	}

	@Override
	@XmlElement(name = "startedVersion")
	public String getStartedVersion() {
		return this.startedVersion;
	}

	@Override
	@XmlElement(name = "url")
	public URI getUri() {
		return this.uri;
	}

	public void setActiveVersion(final String version) {
		this.activeVersion = version;
	}
	
	public void setCreateStickyCandidate(boolean createStickyCandidate) {
		this.createStickyCandidate = createStickyCandidate;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public void setProviderAccount(final String account) {
		this.providerAccount = account;
	}
	
	public void setProviderName(final String name) {
		this.providerName = name;
	}
	
	public void setProviderStatus(final String status) {
		this.providerStatus = status;
	}
	
	public void setStartedVersion(final String version) {
		this.startedVersion = version;
	}
	
	public void setUri(final URI uri) {
		this.uri = uri;
	}
}
