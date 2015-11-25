package com.sap.smartbi.devops.hcp.services;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.sap.smartbi.devops.hcp.internal.models.html5.descriptor.ApplicationDescriptorImpl;
import com.sap.smartbi.devops.hcp.internal.models.html5.descriptor.ApplicationRouteImpl;
import com.sap.smartbi.devops.hcp.internal.models.html5.descriptor.ApplicationRouteTargetImpl;
import com.sap.smartbi.devops.hcp.internal.models.html5.descriptor.ApplicationSecurityConstraintImpl;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationDescriptor;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRoute;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTarget;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTargetType;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationSecurityConstraint;

public class Html5ApplicationDescriptorService {
	public ApplicationDescriptor createDescriptor()
	{
		return new ApplicationDescriptorImpl();
	}
	
	public ApplicationRoute createRoute(final String path, final ApplicationRouteTarget target) {
		if (path == null) {
			throw new NullPointerException("\"path\" is null");
		}
		
		if (path.length() == 0) {
			throw new IllegalArgumentException("\"path\" is empty");
		}
		
		if (target == null) {
			throw new NullPointerException("\"target\" is null");
		}
		
		ApplicationRouteImpl route = new ApplicationRouteImpl();
		
		route.setPath(path);
		route.setTarget(target);
		
		return route;
	}

	public ApplicationRouteTarget createRouteTarget(final String name, final ApplicationRouteTargetType type) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}
		
		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}
		
		if (type == null) {
			throw new NullPointerException("\"type\" is null");
		}
		
		ApplicationRouteTargetImpl target = new ApplicationRouteTargetImpl();
		
		target.setName(name);
		target.setType(type);
		
		return target;
	}

	public ApplicationSecurityConstraint createSecurityConstraint(final String permissionName, final String[] protectedPaths) {
		if (permissionName == null) {
			throw new NullPointerException("\"permissionName\" is null");
		}
		
		if (permissionName.length() == 0) {
			throw new IllegalArgumentException("\"permissionName\" is empty");
		}
		
		if (protectedPaths == null) {
			throw new NullPointerException("\"protectedPaths\" is null");
		}
		
		if (protectedPaths.length == 0) {
			throw new IllegalArgumentException("\"protectedPaths\" is empty");
		}

		ApplicationSecurityConstraintImpl constraint = new ApplicationSecurityConstraintImpl();
		
		constraint.setPermissionName(permissionName);
		constraint.setProtectedPaths(protectedPaths);
		
		return constraint;
	}
	
	public void getApplicationDescriptor(final ApplicationDescriptor descriptor, final OutputStream stream) throws JAXBException {
		if (descriptor == null) {
			throw new NullPointerException("\"descriptor\" is null");
		}
		
		if (stream == null) {
			throw new NullPointerException("\"stream\" is null");
		}
		
		Map<String, Object> properties = new HashMap<String, Object>(2);
		
		properties.put(JAXBContextProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
		properties.put(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
		
		JAXBContext context = JAXBContext.newInstance(ApplicationDescriptorImpl.class);
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		marshaller.marshal(descriptor, stream);
	}
}
