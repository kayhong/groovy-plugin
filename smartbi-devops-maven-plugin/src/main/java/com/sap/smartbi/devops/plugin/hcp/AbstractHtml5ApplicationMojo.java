package com.sap.smartbi.devops.plugin.hcp;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.sap.smartbi.devops.hcp.models.HcpConnectionInfo;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

public abstract class AbstractHtml5ApplicationMojo extends AbstractMojo
		implements IHcpMojo {
	
	protected final static String ACCOUNT_PROPERTY = "account";
	protected final static String APPLICATION_PROPERTY = "application";
	protected final static String CLOUD_REPOSITORY_PROPERTY = "cloudRepository";
	protected final static String CONTENT_PROPERTY = "content";
	protected final static String DELETE_REPOSITORY_PROPERTY = "deleteRepository";
	protected final static String DELETE_SUBSCRIPTIONS_PROPERTY = "deleteSubscriptions";
	protected final static String DISPLAY_NAME_PROPERTY = "displayName";
	protected final static String HOST_PROPERTY = "host";
	protected final static String PASSWORD_PROPERTY = "password";
	protected final static String PROXY_HOST_PROPERTY = "proxyHost";
	protected final static String PROXY_PORT_PROPERTY = "proxyPort";
	protected final static String REPOSITORY_PROPERTY = "repository";
	protected final static String USER_PROPERTY = "user";
	protected final static String VERSION_PROPERTY = "version";

	protected Html5ApplicationService createHtml5ApplicationService(
			final String host, final String account, final String userName,
			final String password, final String proxyHost, int proxyPort) throws MojoExecutionException {
		assert host != null : "\"host\" should not be null";
		assert account != null : "\"account\" should not be null";
		assert userName != null : "\"userName\" should not be null";

		URI proxyUri = null;

		if (proxyHost != null) {
			try {
				proxyUri = new URI(new StringBuilder("http://")
						.append(proxyHost).append(':').append(proxyPort)
						.toString());
			} catch (URISyntaxException e) {
				this.getLog().error("Could not build proxy URI");

				throw new MojoExecutionException("Invalid proxy URI", e);
			}
		}

		return Html5ApplicationService.getInstance(new HcpConnectionInfo(host, account, userName, password, proxyUri));
	}
}
