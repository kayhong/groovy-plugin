package com.sap.smartbi.devops.hcp.internal.models;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.Html5Application;

public final class Html5ApplicationImpl implements Html5Application {
	private String activeCommit;
	private String activeVersion;
	private String description;
	private String displayName;
	private boolean isActiveCommitCurrent;
	private String name;
	private String repository;
	private String repositoryDestination;
	private String startedCommit;
	private String startedVersion;
	private String status;
	private URI url;

	@XmlElement(name = "activeCommit")
	@Override
	public String getActiveCommit() {
		return this.activeCommit;
	}

	@XmlElement(name = "activeVersion")
	@Override
	public String getActiveVersion() {
		return this.activeVersion;
	}

	@XmlElement(name = "displayName")
	public String getDisplayName() {
		return this.displayName;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return this.description;
	}

	@XmlElement(name = "isActiveCommitCurrent")
	@Override
	public boolean getIsActiveCommitCurrent() {
		return this.isActiveCommitCurrent;
	}

	@XmlElement(name = "name", required = true)
	@Override
	public String getName() {
		return this.name;
	}
	
	@XmlElement(name = "repository")
	@Override
	public String getRepository() {
		return this.repository;
	}
	
	@XmlElement(name = "repositoryDestination")
	public String getRepositoryDestination() {
		return this.repositoryDestination;
	}
	
	@XmlElement(name = "startedCommit")
	@Override
	public String getStartedCommit() {
		return this.startedCommit;
	}

	@XmlElement(name = "startedVersion")
	@Override
	public String getStartedVersion() {
		return this.startedVersion;
	}

	@XmlElement(name = "status")
	@Override
	public String getStatus() {
		return this.status;
	}

	@XmlElement(name = "url")
	@Override
	public URI getUrl() {
		return this.url;
	}
	
	public void setActiveCommit(final String activeCommit) {
		this.activeCommit = activeCommit;
	}
	
	public void setActiveVersion(final String activeVersion) {
		this.activeVersion = activeVersion;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public void setIsActiveCommitCurrent(boolean current) {
		this.isActiveCommitCurrent = current;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public void setRepository(final String repository) {
		this.repository = repository;
	}

	public void setRepositoryDestination(final String repositoryDestination) {
		this.repositoryDestination = repositoryDestination;
	}
	
	public void setStartedCommit(final String startedCommit) {
		this.startedCommit = startedCommit;
	}
	
	public void setStartedVersion(final String startedVersion) {
		this.startedVersion = startedVersion;
	}
	
	public void setStatus(final String status) {
		this.status = status;
	}
		
	public void setUrl(final URI url) {
		this.url = url;
	}
}
