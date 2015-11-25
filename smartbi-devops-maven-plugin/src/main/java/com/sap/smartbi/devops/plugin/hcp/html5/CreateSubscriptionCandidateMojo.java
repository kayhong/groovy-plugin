package com.sap.smartbi.devops.plugin.hcp.html5;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "create-html5-subscription-candidate")
public final class CreateSubscriptionCandidateMojo extends
		AbstractApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String application;

	@Parameter(property = CONSUMER_ACCOUNT_PROPERTY, required = true)
	private String consumerAccount;

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
						.format("Creating subscription candidate for HTML5 application:\n\tapplication: %1$s\n\taccount: %2$s\n\tconsumer account: %3$s\n\thost: %4$s\n\tuser: %5$s\n",
								this.application, this.account,
								this.consumerAccount, this.host, this.user));

		try {
			if (service.createSubscriptionCandidate(this.application,
					this.consumerAccount)) {
				this.getLog()
						.info("Successfully created subscription candidate for HTML5 application\n");
			} else {
				this.getLog()
						.info("Could not create subscription candidate for HTML5 application\n");
			}
		} catch (RuntimeException e) {
			this.getLog()
					.error("Failed to create subscription candidate for HTML5 application\n");

			throw new MojoExecutionException(
					String.format(
							"An error occured while creating a subscription candidate for application \"%1$s@%2$s\" from consumer account \"%3$s\"\n",
							this.application, this.account,
							this.consumerAccount), e);
		}
	}

	@Override
	public void setAccount(String account) {
		this.account = account;
	}

	public void setConsumerAccount(final String consumerAccount) {
		this.consumerAccount = consumerAccount;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	@Override
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	@Override
	public void setUser(String user) {
		this.user = user;

	}
}
