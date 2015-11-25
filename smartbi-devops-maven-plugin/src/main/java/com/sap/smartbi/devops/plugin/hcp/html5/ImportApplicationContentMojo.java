package com.sap.smartbi.devops.plugin.hcp.html5;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "import-html5-application-content")
public final class ImportApplicationContentMojo extends
		AbstractApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = CONTENT_PROPERTY, required = true)
	private File content;

	@Parameter(property = HOST_PROPERTY, required = true)
	private String host;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String application;

	@Parameter(property = PASSWORD_PROPERTY)
	private String password;

	@Parameter(property = PROXY_HOST_PROPERTY)
	private String proxyHost;

	@Parameter(property = PROXY_PORT_PROPERTY)
	private int proxyPort;

	@Parameter(property = USER_PROPERTY, required = true)
	private String user;

	@Parameter(property = VERSION_PROPERTY, required = true)
	private String version;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.host, this.account, this.user, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Importing HTML5 application content:\n\tapplication: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n\tversion: %5$s\n\tcontent source:%6$s\n",
								this.application, this.account, this.host,
								this.user, this.version,
								this.content.getAbsolutePath()));

		try {
			service.importApplicationContent(this.application, this.version,
					this.content);

			this.getLog()
					.info(String
							.format("Successfully imported HTML5 application content\n\tname: %1$s\n\tversion: %2$s\n\tcontent source: %3$s\n",
									this.application, this.version,
									this.content.getAbsolutePath()));
		} catch (RuntimeException e) {
			this.getLog()
					.error(String
							.format("Failed to import HTML5 application content\n\tname: %1$s\n\tversion: %2$s\n\tcontent source: %3$s\n",
									this.application, this.version,
									this.content.getAbsolutePath()));

			throw new MojoExecutionException(
					String.format(
							"An error occured while importing the \"%1$s\" HTML5 application content \"%2$s\" from content source \"%3$s\"",
							this.application, this.version,
							this.content.getAbsolutePath()), e);
		} catch (IOException e) {
			this.getLog()
					.error(String
							.format("Failed to import HTML5 application content\n\tname: %1$s\n\tversion: %2$s\n\tcontent source: %3$s\n",
									this.application, this.version,
									this.content.getAbsolutePath()));

			throw new MojoExecutionException(
					String.format(
							"An error occured while importing the \"%1$s\" HTML5 application content \"%2$s\" from content source \"%3$s\"",
							this.application, this.version,
							this.content.getAbsolutePath()), e);
		}
	}

	@Override
	public void setAccount(final String account) {
		this.account = account;
	}

	public void setApplication(final String application) {
		this.application = application;
	}

	public void setContent(final File content) {
		this.content = content;
	}

	@Override
	public void setHost(String host) {
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

	public void setVersion(final String version) {
		this.version = version;
	}
}
