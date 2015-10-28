package com.sap.smartbi.devops.hcp.internal.models;

import com.sap.smartbi.devops.hcp.models.Html5ApplicationVersion;

public final class Html5ApplicationVersionImpl implements
		Html5ApplicationVersion {

	private String commitId;
	private String version;
	
	public Html5ApplicationVersionImpl() {
	}
	
	public Html5ApplicationVersionImpl(final String version, final String commitId) {
		assert version != null : "\"version\" should not be null";
		assert commitId != null : "\"commitId\" should not be null";

		this.commitId = commitId;
		this.version = version;
	}
	
	@Override
	public String getCommitId() {
		return this.commitId;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	public void setCommitId(final String commitId) {
		this.commitId = commitId;
	}
	
	public void setVersion(final String version) {
		this.version = version;
	}
}
