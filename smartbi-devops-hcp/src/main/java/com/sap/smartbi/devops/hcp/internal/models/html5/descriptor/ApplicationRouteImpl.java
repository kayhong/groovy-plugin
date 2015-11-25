package com.sap.smartbi.devops.hcp.internal.models.html5.descriptor;

import javax.xml.bind.annotation.XmlElement;

import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRoute;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTarget;

public final class ApplicationRouteImpl implements ApplicationRoute {

	private String description;
	private String path;
	private ApplicationRouteTarget target;
	
	@Override
	@XmlElement(name = "description", required = false)
	public String getDescription() {
		return this.description;
	}

	@Override
	@XmlElement(name = "path")
	public String getPath() {
		return this.path;
	}

	@Override
	@XmlElement(name = "target", type = ApplicationRouteTargetImpl.class)
	public ApplicationRouteTarget getTarget() {
		return this.target;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	public void setPath(final String path) {
		this.path = path;
	}
	
	public void setTarget(final ApplicationRouteTarget target) {
		this.target = target;
	}
}
