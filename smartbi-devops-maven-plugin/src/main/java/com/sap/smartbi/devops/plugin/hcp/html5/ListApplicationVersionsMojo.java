package com.sap.smartbi.devops.plugin.hcp.html5;

import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.html5.Version;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "list-html5-application-versions")
public final class ListApplicationVersionsMojo extends AbstractApplicationMojo {

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

	@Parameter(property = USER_PROPERTY, required = true)
	private String user;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.dispatcher, this.host, this.account, this.user,
				this.password, this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Retrieving HTML5 application versions:\n\taccount: %1$s\n\tdispatcher: %2$s\n\thost: %3$s\n\tuser: %4$s\n",
								this.account, this.dispatcher, this.host,
								this.user));

		try {
			Collection<Version> versions = service
					.listApplicationVersions(this.application);

			this.getLog().info(
					"Successfully retrieved HTML5 application versions\n");

			for (Version version : versions) {
				this.getLog().info(
						String.format("\tversion: %1$s\n\tcommit ID: %2$s\n",
								version.getVersion(), version.getCommitId()));
			}
		} catch (RuntimeException e) {
			this.getLog().error(
					"Failed to retrieve HTML5 application versions.");

			throw new MojoExecutionException(
					"An error occured while retrieving HTML5 application versions",
					e);
		}
	}

	@Override
	public void setAccount(final String account) {
		this.account = account;
	}

	public void setApplication(final String application) {
		this.application = application;
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
	public void setUser(final String userName) {
		this.user = userName;
	}
}
