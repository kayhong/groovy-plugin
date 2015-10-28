package com.sap.smartbi.devops.plugin.hcp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "stop-html5-application")
public final class StopHtml5ApplicationMojo extends
		AbstractHtml5ApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String application;

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
						.format("Stopping HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n",
								this.application, this.account, this.host,
								this.user));

		try {
			service.stopApplication(this.application);

			this.getLog()
					.info(String
							.format("Successfully stopped HTML5 application\n\tname: %1$s\n",
									this.application));

		} catch (RuntimeException e) {
			this.getLog().error(
					String.format(
							"Failed to stop the \"%1$s\" HTML5 application.",
							this.application));

			throw new MojoExecutionException(
					String.format(
							"An error occured while stopping the \"%1$s\" HTML5 application",
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
