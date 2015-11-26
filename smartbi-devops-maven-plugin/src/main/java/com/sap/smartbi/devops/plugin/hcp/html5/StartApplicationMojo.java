package com.sap.smartbi.devops.plugin.hcp.html5;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "start-html5-application")
public final class StartApplicationMojo extends AbstractApplicationMojo {

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
						.format("Starting HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\tdispatcher: %3$s\n\thost: %4$s\n\tuser: %5$s\n",
								this.application, this.account,
								this.dispatcher, this.host, this.user));

		try {
			service.startApplication(this.application);

			this.getLog()
					.info(String
							.format("Successfully started HTML5 application\n\tname: %1$s\n",
									this.application));

		} catch (RuntimeException e) {
			this.getLog().error(
					String.format(
							"Failed to start the \"%1$s\" HTML5 application.",
							this.application));

			throw new MojoExecutionException(
					String.format(
							"An error occured while starting the \"%1$s\" HTML5 application",
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
