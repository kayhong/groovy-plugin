package com.sap.smartbi.devops.hcp.models;

import java.net.URI;

public interface Html5Application {
	String getActiveCommit();

	String getActiveVersion();

	String getDisplayName();

	String getDescription();

	boolean getIsActiveCommitCurrent();

	String getName();

	String getRepository();

	String getStartedCommit();

	String getStartedVersion();

	String getStatus();

	URI getUrl();
}
