package com.sap.smartbi.devops.plugin.hcp.html5;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.html5.Subscription;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "create-html5-subscription")
public final class CreateSubscriptionMojo extends AbstractApplicationMojo {

	@Parameter(property = ACCOUNT_PROPERTY, required = true)
	private String account;

	@Parameter(property = APPLICATION_PROPERTY, required = true)
	private String application;

	@Parameter(property = HOST_PROPERTY, required = true)
	private String host;

	@Parameter(property = PASSWORD_PROPERTY)
	private String password;

	@Parameter(property = PROVIDER_ACCOUNT_PROPERTY, required = true)
	private String providerAccount;

	@Parameter(property = PROXY_HOST_PROPERTY)
	private String proxyHost;

	@Parameter(property = PROXY_PORT_PROPERTY)
	private int proxyPort;

	@Parameter(property = SUBSCRIPTION_PROPERTY, required = true)
	private String subscription;

	@Parameter(property = USER_PROPERTY, required = true)
	private String user;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Html5ApplicationService service = this.createHtml5ApplicationService(
				this.host, this.account, this.user, this.password,
				this.proxyHost, this.proxyPort);

		this.getLog()
				.info(String
						.format("Subscribing to HTML5 application\n\tsubscription: %1$s\n\tapplication: %2$s\n\tprovider account: %3$s\n\taccount: %4$s\n\thost: %5$s\n\tuser: %6$s\n",
								this.subscription, this.application,
								this.providerAccount, this.account, this.host,
								this.user));

		try {
			Subscription subscription = service.createSubscription(
					this.subscription, this.providerAccount, this.application);

			this.getLog()
					.info(String
							.format("Successfully subscribed to HTML5 application:\n\tURL: %1$s\n",
									subscription.getUri()));
		} catch (RuntimeException e) {
			this.getLog()
					.error(String
							.format("Failed to subscribe to HTML5 application \"%1$s@%2$s\" from account \"%3$s\"\n",
									this.application, this.providerAccount,
									this.account));

			throw new MojoExecutionException(
					String.format(
							"An error occured while subscribing to application \"%1$s@%2$s\" from account \"%3$s\"",
							this.application, this.providerAccount,
							this.account), e);
		}
	}

	@Override
	public void setAccount(String account) {
		this.account = account;
	}

	public void setApplication(final String application) {
		this.application = application;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	public void setProviderAccount(final String account) {
		this.providerAccount = account;
	}

	@Override
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	@Override
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setSubscription(final String subscription) {
		this.subscription = subscription;
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

}
