package com.sap.smartbi.devops.plugin.hcp.html5;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.internal.models.html5.RoleAssignment;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "create-html5-application-role-assignments")
public final class CreateApplicationRoleAssignmentsMojo extends
		AbstractApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String application;

	@Parameter(property = DISPATCHER_PROPERTY, required = true)
	private String dispatcher;

	@Parameter(property = HOST_PROPERTY, required = true)
	private String host;

	@Parameter(property = PASSWORD_PROPERTY)
	private String password;

	@Parameter(property = PROXY_HOST_PROPERTY)
	private String proxyHost;

	@Parameter(property = PROXY_PORT_PROPERTY)
	private int proxyPort;

	@Parameter(property = ROLE_ASSIGNMENTS_PROPERTY, required = true)
	private List<Assignment> assignments;

	@Parameter(property = USER_PROPERTY, required = true)
	private String user;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.dispatcher, this.host, this.account, this.user,
				this.password, this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Creating role assignments for HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\tdispatcher: %3$s\n\thost: %4$s\n\tuser: %5$s\n",
								this.application, this.account,
								this.dispatcher, this.host, this.user));

		ArrayList<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>(
				this.assignments.size());

		for (Assignment assignment : this.assignments) {
			RoleAssignment roleAssignment = new RoleAssignment();

			roleAssignment.setPermission(assignment.getPermission());
			roleAssignment.setRole(assignment.getRole());
			
			roleAssignments.add(roleAssignment);
		}

		try {
			service.createApplicationRoleAssignments(this.application,
					roleAssignments);

			this.getLog()
					.info("Successfully created role assignments for HTML5 application\n");

		} catch (RuntimeException e) {
			this.getLog()
					.error("Failed to create role assignments for HTML5 application\n");

			throw new MojoExecutionException(
					String.format(
							"An error occured while creating role assignments for application \"%1$s@%2$s\"\n",
							this.application, this.account), e);
		}
	}

	@Override
	public void setAccount(final String account) {
		this.account = account;
	}

	public void setAssignments(final List<Assignment> assignments) {
		this.assignments = assignments;
	}

	@Override
	public void setDispatcher(final String dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void setHost(final String host) {
		this.host = host;
	}

	@Override
	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public void setProxyHost(final String proxyHost) {
		this.proxyHost = proxyHost;
	}

	@Override
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	@Override
	public void setUser(final String user) {
		this.user = user;
	}

}
