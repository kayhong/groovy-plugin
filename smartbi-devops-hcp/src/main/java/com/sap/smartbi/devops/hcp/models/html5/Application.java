package com.sap.smartbi.devops.hcp.models.html5;

import java.net.URI;

public interface Application {
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
