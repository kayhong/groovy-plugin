package com.sap.smartbi.devops.hcp.internal.models.html5.descriptor;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTarget;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTargetType;

public final class ApplicationRouteTargetImpl implements
		ApplicationRouteTarget {
	
	private String name;
	private URI path;
	private ApplicationRouteTargetType type;
	private String version;

	@Override
	@XmlElement(name = "name")
	public String getName() {
		return this.name;
	}

	@Override
	@XmlElement(name = "entryPath")
	public URI getPath() {
		return this.path;
	}

	@Override
	@XmlElement(name = "type")
	public ApplicationRouteTargetType getType() {
		return this.type;
	}

	@Override
	@XmlElement(name = "version", required = false)
	public String getVersion() {
		return this.version;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setPath(final URI path) {
		this.path = path;
	}

	public void setType(ApplicationRouteTargetType type) {
		this.type = type;
	}

	@Override
	public void setVersion(final String version) {
		this.version = version;
	}

}
