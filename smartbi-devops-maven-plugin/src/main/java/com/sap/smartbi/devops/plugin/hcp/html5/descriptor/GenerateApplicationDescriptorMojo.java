package com.sap.smartbi.devops.plugin.hcp.html5.descriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationDescriptor;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRoute;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTarget;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationRouteTargetType;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.ApplicationSecurityConstraint;
import com.sap.smartbi.devops.hcp.models.html5.descriptor.AuthenticationMethod;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationDescriptorService;

@Mojo(name = "generate-html5-application-descriptor")
public final class GenerateApplicationDescriptorMojo extends AbstractMojo {

	private final static String AUTHENTICATION_METHOD_PROPERTY = "authenticationMethod";
	private final static String DESCRIPTOR = "descriptor";
	private final static String LOGOUT_PATH_PROPERTY = "logoutPath";
	private final static String REDIRECT_WELCOME_PATH_PROPERTY = "redirectWelcomePath";
	private final static String ROUTES_PROPERTY = "routes";
	private final static String SECURITY_CONSTRAINTS_PROPERTY = "securityConstraints";
	private final static String WELCOME_PATH_PROPERTY = "welcomePath";

	@Parameter(property = AUTHENTICATION_METHOD_PROPERTY, required = true, defaultValue = "saml")
	private String authenticationMethod;

	@Parameter(property = DESCRIPTOR, required = true)
	private File descriptor;

	@Parameter(property = LOGOUT_PATH_PROPERTY)
	private String logoutPath;

	@Parameter(property = REDIRECT_WELCOME_PATH_PROPERTY, defaultValue = "false")
	private boolean redirectWelcomePath;

	@Parameter(property = ROUTES_PROPERTY, required = false)
	private Route[] routes;

	@Parameter(property = SECURITY_CONSTRAINTS_PROPERTY, required = false)
	private SecurityConstraint[] securityConstraints;

	@Parameter(property = WELCOME_PATH_PROPERTY)
	private String welcomePath;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		this.getLog().info("Generating HTML5 application descriptor");

		try (FileOutputStream stream = new FileOutputStream(this.descriptor)) {
			Html5ApplicationDescriptorService service = new Html5ApplicationDescriptorService();

			ApplicationDescriptor descriptor = service.createDescriptor();

			AuthenticationMethod method = null;

			switch (this.authenticationMethod) {
			case "none":
				method = AuthenticationMethod.NONE;

				break;

			case "saml":
				method = AuthenticationMethod.SAML;
				
				break;

			default:
				throw new IllegalArgumentException(String.format(
						"\"%1$s\" is not a valid authentication method",
						this.authenticationMethod));
			}

			assert method != null : "method should not be null";

			descriptor.setAuthenticationMethod(method);
			descriptor.setLogoutUri(URI.create(this.logoutPath));
			descriptor.setWelcomeUri(URI.create(this.welcomePath));

			if (this.routes != null) {
				List<ApplicationRoute> newRoutes = new ArrayList<ApplicationRoute>();

				for (Route route : this.routes) {
					Target target = route.getTarget();

					if (target == null) {
						continue;
					}

					ApplicationRouteTargetType targetType = null;

					switch (target.getType()) {
					case "application":
						targetType = ApplicationRouteTargetType.APPLICATION;

						break;

					case "destination":
						targetType = ApplicationRouteTargetType.DESTINATION;

						break;

					case "service":
						targetType = ApplicationRouteTargetType.SERVICE;

						break;

					default:
						throw new IllegalArgumentException(
								String.format(
										"\"%1$s\" is not a valid target type for target named \"%2$s\"",
										target.getName()));
					}

					assert targetType != null : "targetType should not be null";

					ApplicationRouteTarget newTarget = service
							.createRouteTarget(target.getName(), targetType);

					newTarget.setPath(URI.create(target.getPath()));
					newTarget.setVersion(target.getVersion());

					ApplicationRoute newRoute = service.createRoute(
							route.getPath(), newTarget);

					newRoute.setDescription(route.getDescription());

					newRoutes.add(newRoute);
				}

				if (newRoutes.size() > 0) {
					descriptor.setRoutes(newRoutes
							.toArray(new ApplicationRoute[] {}));
				}
			}

			if (this.securityConstraints != null) {
				List<ApplicationSecurityConstraint> newConstraints = new ArrayList<ApplicationSecurityConstraint>();

				for (SecurityConstraint constraint : this.securityConstraints) {
					ApplicationSecurityConstraint newConstraint = service
							.createSecurityConstraint(
									constraint.getPermissionName(),
									constraint.getProtectedPaths());

					newConstraints.add(newConstraint);
				}

				if (newConstraints.size() > 0) {
					descriptor
							.setSecurityConstraints(newConstraints
									.toArray(new ApplicationSecurityConstraint[] {}));
				}
			}

			service.getApplicationDescriptor(descriptor, stream);

			this.getLog().info(
					"Successfully generated HTML5 application descriptor");

			try (InputStreamReader reader = new FileReader(this.descriptor)) {
				char[] buffer = new char[4096];
				
				StringBuilder builder = new StringBuilder();

				while (true) {
					int count = reader.read(buffer, 0, buffer.length);
					if (count == -1) {
						break;
					}

					builder.append(buffer, 0, count);
				}
				
				this.getLog().info(builder.toString());
			}
		} catch (IOException e) {
			this.getLog().error(
					"Failed to generate HTML5 application descriptor");

			throw new MojoExecutionException(
					"An error occured while generating HTML5 application descriptor",
					e);
		} catch (JAXBException e) {
			this.getLog().error(
					"Failed to generate HTML5 application descriptor");

			throw new MojoExecutionException(
					"An error occured while generating HTML5 application descriptor",
					e);
		} catch (RuntimeException e) {
			this.getLog().error(
					"Failed to generate HTML5 application descriptor");

			throw new MojoExecutionException(
					"An error occured while generating HTML5 application descriptor",
					e);
		}
	}
}
