package com.sap.smartbi.devops.hcp.models.html5.descriptor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum ApplicationRouteTargetType {
	@XmlEnumValue(value = "destination")
	DESTINATION,
	@XmlEnumValue(value = "service")
	SERVICE,
	@XmlEnumValue(value = "application")
	APPLICATION
}
