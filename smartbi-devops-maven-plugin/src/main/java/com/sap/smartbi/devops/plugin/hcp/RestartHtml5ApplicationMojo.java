package com.sap.smartbi.devops.plugin.hcp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "restart-html5-application")
public final class RestartHtml5ApplicationMojo extends
		AbstractHtml5ApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = HOST_PROPERTY, required = true)
	private String host;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String name;

	@Parameter(property = PASSWORD_PROPERTY)
	private String password;

	@Parameter(property = PROXY_HOST_PROPERTY)
	private String proxyHost;

	@Parameter(property = PROXY_PORT_PROPERTY)
	private int proxyPort;

	@Parameter(property = USER_PROPERTY, required = true)
	private String userName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.host, this.account, this.userName, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Restarting HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n",
								this.name, this.account, this.host,
								this.userName));

		try {
			service.restartApplication(this.name);

			this.getLog()
					.info(String
							.format("Successfully restarted HTML5 application\n\tname: %1$s\n",
									this.name));

		} catch (Throwable e) {
			this.getLog()
					.error(String
							.format("Failed to restart the \"%1$s\" HTML5 application.",
									this.name));

			throw new MojoExecutionException(
					String.format(
							"An error occured while restarting the \"%1$s\" HTML5 application",
							this.name), e);
		}
	}

	@Override
	public void setAccount(final String account) {
		this.account = account;
	}

	public void setApplication(final String application) {
		this.name = application;
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
	public void setUserName(final String userName) {
		this.userName = userName;
	}
}
