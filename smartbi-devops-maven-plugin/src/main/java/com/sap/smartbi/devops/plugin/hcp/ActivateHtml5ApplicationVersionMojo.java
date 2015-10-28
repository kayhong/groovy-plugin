package com.sap.smartbi.devops.plugin.hcp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "activate-html5-application-version")
public final class ActivateHtml5ApplicationVersionMojo extends
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

	@Parameter(property = VERSION_PROPERTY, required = true)
	private String version;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.host, this.account, this.userName, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Activating HTML5 application version:\n\tapplication: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n\tversion: %5$s\n",
								this.name, this.account, this.host,
								this.userName, this.version));

		try {
			service.activateApplicationVersion(this.name, this.version);

			this.getLog()
					.info(String
							.format("Successfully activated HTML5 application version\n\tname: %1$s\n\tversion: %2$s\n",
									this.name, this.version));

		} catch (Throwable e) {
			this.getLog()
					.error(String
							.format("Failed to activate the HTML5 application version\n\tname: %1$s\n\tversion: %2$s\n",
									this.name, this.version));

			throw new MojoExecutionException(
					String.format(
							"An error occured while activating the \"%1$s\" HTML5 application version \"%2$s\"",
							this.name, this.version), e);
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

	public void setVersion(final String version) {
		this.version = version;
	}
}
