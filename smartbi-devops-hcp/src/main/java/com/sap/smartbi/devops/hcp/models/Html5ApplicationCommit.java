package com.sap.smartbi.devops.hcp.models;

import java.net.URI;

public interface Html5ApplicationCommit {
	String getAbbreviatedId();
	
	String getAuthor();

	String getCommitter();

	String getId();
	
	boolean getIsActive();
	
	String getMessage();
	
	String getVersion();
	
	URI getUrl();
}
