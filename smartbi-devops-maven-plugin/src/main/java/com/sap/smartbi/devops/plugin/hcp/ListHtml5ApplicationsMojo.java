package com.sap.smartbi.devops.plugin.hcp;

import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.Html5Application;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "list-html5-applications")
public final class ListHtml5ApplicationsMojo extends
		AbstractHtml5ApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

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
				this.host, this.account, this.user, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Retrieving HTML5 applications:\n\taccount: %1$s\n\thost: %2$s\n\tuser: %3$s\n",
								this.account, this.host, this.user));

		try {
			Collection<Html5Application> applications = service
					.listApplications();

			this.getLog().info("Successfully retrieved HTML5 applications\n");

			for (Html5Application application : applications) {
				this.getLog()
						.info(String
								.format("\tname: %1$s\n\tdisplay name: %2$s\n\tURL: %3$s\n\trepository: %4$s\n\tstatus: %5$s\n\tactive version: %6$s\n\tactive commit: %7$s\n\tstarted version: %8$s\n\tstarted commit: %9$s\n",
										application.getName(),
										application.getDisplayName(),
										application.getUrl(),
										application.getRepository(),
										application.getStatus(),
										application.getActiveVersion(),
										application.getActiveCommit(),
										application.getStartedVersion(),
										application.getStartedCommit()));
			}
		} catch (RuntimeException e) {
			this.getLog().error("Failed to retrieve HTML5 applications.");

			throw new MojoExecutionException(
					"An error occured while retrieving HTML5 applications", e);
		}
	}

	@Override
	public void setAccount(final String account) {
		this.account = account;
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
