package com.sap.smartbi.devops.hcp.models.html5.descriptor;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum AuthenticationMethod {
	@XmlEnumValue(value = "none")
	NONE,
	@XmlEnumValue(value = "saml")
	SAML
}
