package com.sap.smartbi.devops.plugin.hcp.html5;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.html5.Application;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "get-html5-application")
public final class GetApplicationMojo extends AbstractApplicationMojo {

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
						.format("Retrieving HTML5 application details:\n\tapplication: %1$s\n\taccount: %2$s\n\tdispatcher: %3$s\n\thost: %4$s\n\tuser: %5$s\n",
								this.application, this.account,
								this.dispatcher, this.host, this.user));

		try {
			Application application = service.getApplication(this.application);

			this.getLog()
					.info(String
							.format("Successfully retrieved HTML5 application details\n\tname: %1$s\n\tdisplay name: %2$s\n\tdescription: %3$s\n\tURL: %4$s\n\trepository: %5$s\n\tstatus: %6$s\n\tactive version: %7$s\n\tactive commit: %8$s\n\tstarted version: %9$s\n\tstarted commit: %10$s\n",
									application.getName(),
									application.getDisplayName(),
									application.getDescription(),
									application.getUrl(),
									application.getRepository(),
									application.getStatus(),
									application.getActiveVersion(),
									application.getActiveCommit(),
									application.getStartedVersion(),
									application.getStartedCommit()));
		} catch (RuntimeException e) {
			this.getLog()
					.error(String
							.format("Failed to retrieve the \"%1$s\" HTML5 application details.",
									this.application));

			throw new MojoExecutionException(
					String.format(
							"An error occured while retrieving the \"%1$s\" HTML5 application details",
							this.application), e);
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
