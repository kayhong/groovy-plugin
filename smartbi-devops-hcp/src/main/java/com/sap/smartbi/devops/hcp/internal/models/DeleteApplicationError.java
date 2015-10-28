package com.sap.smartbi.devops.hcp.internal.models;

import javax.xml.bind.annotation.XmlElement;

public final class DeleteApplicationError {

	private String code;
	private String message;
	
	@XmlElement
	public String getCode() {
		return this.code;
	}
	
	@XmlElement
	public String getMessage() {
		return this.message;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(final String message) {
		this.message = message;
	}
}
