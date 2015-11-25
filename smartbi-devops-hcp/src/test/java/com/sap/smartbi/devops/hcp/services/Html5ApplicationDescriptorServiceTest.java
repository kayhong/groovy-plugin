package com.sap.smartbi.devops.hcp.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import com.sap.smartbi.devops.hcp.internal.models.html5.descriptor.ApplicationRouteTargetImpl;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationDescriptor;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRoute;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTarget;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTargetType;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationSecurityConstraint;

public final class Html5ApplicationDescriptorServiceTest {

	@Test
	public void testCreateDescriptor() {
		Assert.assertNotNull(new Html5ApplicationDescriptorService().createDescriptor());
	}
	
	@Test
	public void testCreateRoute() {
		String path = UUID.randomUUID().toString();
		ApplicationRouteTarget target = new ApplicationRouteTargetImpl();
		
		ApplicationRoute route = new Html5ApplicationDescriptorService().createRoute(path, target); 
		
		Assert.assertNotNull(route);
		Assert.assertEquals(path, route.getPath());
		Assert.assertSame(target, route.getTarget());
		
	}
	@Test(expected = NullPointerException.class)
	public void testCreateRouteNullPath() {
		new Html5ApplicationDescriptorService().createRoute(null, new ApplicationRouteTargetImpl());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateRouteEmptyPath() {
		new Html5ApplicationDescriptorService().createRoute("", new ApplicationRouteTargetImpl());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateRouteNullTarget() {
		new Html5ApplicationDescriptorService().createRoute("some-path", null);
	}
	
	@Test
	public void testCreateRouteTarget() {
		Html5ApplicationDescriptorService service = new Html5ApplicationDescriptorService();
		String name = UUID.randomUUID().toString();
		
		for (ApplicationRouteTargetType type : ApplicationRouteTargetType.values()) {
			ApplicationRouteTarget target = service.createRouteTarget(name, type);
			
			Assert.assertNotNull(target);
			Assert.assertEquals(name, target.getName());
			Assert.assertEquals(type, target.getType());
		}
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateRouteTargetNullName() {
		new Html5ApplicationDescriptorService().createRouteTarget(null, ApplicationRouteTargetType.APPLICATION);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateRouteTargetEmptyName() {
		new Html5ApplicationDescriptorService().createRouteTarget("", ApplicationRouteTargetType.DESTINATION);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateRouteTargetNullType() {
		new Html5ApplicationDescriptorService().createRouteTarget("some name", null);
	}

	@Test
	public void testCreateSecurityConstraint() {
		String permissionName = UUID.randomUUID().toString();
		String[] paths = new String[] { "path1", "path2", "path3" };
		
		ApplicationSecurityConstraint constraint = new Html5ApplicationDescriptorService().createSecurityConstraint(permissionName, paths);
		
		Assert.assertNotNull(constraint);
		Assert.assertSame(permissionName, constraint.getPermissionName());
		Assert.assertSame(paths, constraint.getProtectedPaths());
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateSecurityConstraintNullPermissionName() {
		new Html5ApplicationDescriptorService().createSecurityConstraint(null, new String[] { "somePath" });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSecurityConstraintEmptyPermissionName() {
		new Html5ApplicationDescriptorService().createSecurityConstraint("", new String[] { "somePath" });
	}
	
	@Test(expected = NullPointerException.class)
	public void testCreateSecurityConstraintNullPaths() {
		new Html5ApplicationDescriptorService().createSecurityConstraint("some permission name", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSecurityConstraintEmptyPaths() {
		new Html5ApplicationDescriptorService().createSecurityConstraint("some permission name", new String[] { });
	}
	
	@Test
	public void testGetApplicationDescriptor() throws IOException, JAXBException {
		Html5ApplicationDescriptorService service = new Html5ApplicationDescriptorService();
		
		ApplicationDescriptor descriptor = service.createDescriptor();
		
		Assert.assertNull(descriptor.getAuthenticationMethod());
		Assert.assertNull(descriptor.getLogoutUri());
		Assert.assertFalse(descriptor.getRedirectWelcomePage());
		Assert.assertNull(descriptor.getSecurityConstraints());
		Assert.assertNull(descriptor.getWelcomeUri());
		
		ApplicationRouteTarget target = service.createRouteTarget("target #1", ApplicationRouteTargetType.DESTINATION);
		
		ApplicationRoute route = service.createRoute("/path1", target);
		
		ApplicationSecurityConstraint constraint = service.createSecurityConstraint("some permission name", new String[] { "some-path1", "some-path2" });

		descriptor.setRoutes(new ApplicationRoute[] { route });
		descriptor.setSecurityConstraints(new ApplicationSecurityConstraint[] { constraint });

		Path path = Files.createTempFile(null, null);
		
		try (FileOutputStream stream = new FileOutputStream(path.toFile())) {
			service.getApplicationDescriptor(descriptor, stream);
		}
	}
}
