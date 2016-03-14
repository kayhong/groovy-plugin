package com.sap.smartbi.devops.hcp.internal.models.html5;

import javax.xml.bind.annotation.XmlElement;

public final class RoleAssignment {

	private String description;
	private String permission;
	private String role;

	public RoleAssignment() {
	}

	@XmlElement(name = "description", required = false)
	public String getDescription() {
		return this.description;
	}
	
	@XmlElement(name = "permission")
	public String getPermission() {
		return this.permission;
	}
	
	@XmlElement(name = "role")
	public String getRole() {
		return this.role;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public void setPermission(final String permission) {
		this.permission = permission;
	}
	
	public void setRole(final String role) {
		this.role = role;
	}
}
