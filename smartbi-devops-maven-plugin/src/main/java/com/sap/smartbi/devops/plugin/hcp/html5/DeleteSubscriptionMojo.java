package com.sap.smartbi.devops.plugin.hcp.html5;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "delete-html5-subscription")
public final class DeleteSubscriptionMojo extends AbstractApplicationMojo {

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
						.format("Unsubscribing from HTML5 application:\n\tsubscription name: %1$s\n\taccount: %2$s\n\thost: %3$s\n\tuser: %4$s\n",
								this.subscription, this.account, this.host,
								this.user));

		try {
			if (service.deleteSubscription(this.subscription)) {
				this.getLog().info(
						"Successfully unsubscribed from HTML5 application\n");
			} else {
				this.getLog().info(
						"Could not unsubscribe from HTML5 application\n");
			}
		} catch (RuntimeException e) {
			this.getLog()
					.info("Failed to unsubscribe from HTML5 application\n");

			throw new MojoExecutionException(
					String.format(
							"An error occured while unsubscribing \"%1$s\" from HTML5 application",
							this.subscription), e);
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

	public void setSubscription(final String subscription) {
		this.subscription = subscription;
	}

	@Override
	public void setUser(final String user) {
		this.user = user;
	}

}
