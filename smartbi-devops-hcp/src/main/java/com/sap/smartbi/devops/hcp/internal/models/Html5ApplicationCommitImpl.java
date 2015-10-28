package com.sap.smartbi.devops.hcp.internal.models;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.Html5ApplicationCommit;

public final class Html5ApplicationCommitImpl implements Html5ApplicationCommit {

	private String abbreviatedId;
	private String author;
	private String committer;
	private String id;
	private boolean isActive;
	private String message;
	private String version;
	private URI url;
	
	@XmlElement(name = "abrevCommitId")
	@Override
	public String getAbbreviatedId() {
		return this.abbreviatedId;
	}

	@XmlElement(name = "author")
	@Override
	public String getAuthor() {
		return this.author;
	}

	@XmlElement(name = "committer")
	@Override
	public String getCommitter() {
		return this.committer;
	}

	@XmlElement(name = "commitId")
	@Override
	public String getId() {
		return this.id;
	}

	@XmlElement(name = "active")
	@Override
	public boolean getIsActive() {
		return this.isActive;
	}

	@XmlElement(name = "commitMessage")
	@Override
	public String getMessage() {
		return this.message;
	}

	@XmlElement(name = "version")
	@Override
	public String getVersion() {
		return this.version;
	}

	@XmlElement(name = "url")
	@Override
	public URI getUrl() {
		return this.url;
	}

	public void setAbbreviatedId(final String abbreviatedId) {
		this.abbreviatedId = abbreviatedId;
	}
	
	public void setAuthor(final String author) {
		this.author = author;
	}
	
	public void setCommitter(final String committer) {
		this.committer = committer;
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setMessage(final String message) {
		this.message = message;
	}
	
	public void setVersion(final String version) {
		this.version = version;
	}
	
	public void setUrl(final URI url) {
		this.url = url;
	}
}
