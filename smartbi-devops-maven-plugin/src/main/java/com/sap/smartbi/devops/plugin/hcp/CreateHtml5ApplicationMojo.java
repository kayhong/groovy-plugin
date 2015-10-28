package com.sap.smartbi.devops.plugin.hcp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.Html5Application;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "create-html5-application")
public final class CreateHtml5ApplicationMojo extends
		AbstractHtml5ApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = CLOUD_REPOSITORY_PROPERTY, defaultValue = "true")
	private boolean cloudRepository;

	@Parameter(property = DISPLAY_NAME_PROPERTY)
	private String displayName;

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

	@Parameter(property = REPOSITORY_PROPERTY)
	private String repository;

	@Parameter(property = USER_PROPERTY, required = true)
	private String userName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.host, this.account, this.userName, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Creating HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n",
								this.name, this.account, this.host,
								this.userName));

		try {
			Html5Application application = service.createApplication(this.name,
					this.displayName, this.repository, this.cloudRepository);

			this.getLog()
					.info(String
							.format("Successfully created HTML5 application:\n\tdisplay name: %1$s\n\tURL: %2$s\n\tGit repository: %3$s\n\tstatus: %4$s\n",
									application.getDisplayName(),
									application.getUrl(),
									application.getRepository(),
									application.getStatus()));
		} catch (RuntimeException e) {
			this.getLog().error(
					String.format(
							"Failed to create the \"%1$s\" HTML5 application.",
							this.name));

			throw new MojoExecutionException(
					String.format(
							"An error occured while creating the \"%1$s\" HTML5 application",
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

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
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
