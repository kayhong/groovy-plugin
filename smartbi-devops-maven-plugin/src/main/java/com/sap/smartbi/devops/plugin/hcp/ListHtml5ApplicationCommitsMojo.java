package com.sap.smartbi.devops.plugin.hcp;

import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.sap.smartbi.devops.hcp.models.Html5ApplicationCommit;
import com.sap.smartbi.devops.hcp.services.Html5ApplicationService;

@Mojo(name = "list-html5-application-commits")
public final class ListHtml5ApplicationCommitsMojo extends
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
						.format("Retrieving HTML5 application commits:\n\taccount: %1$s\n\thost: %2$s\n\tuser: %3$s\n",
								this.account, this.host, this.userName));

		try {
			Collection<Html5ApplicationCommit> commits = service
					.listApplicationCommits(this.name);

			this.getLog().info(
					"Successfully retrieved HTML5 application commits\n");

			for (Html5ApplicationCommit commit : commits) {
				this.getLog()
						.info(String
								.format("\tID: %1$s\n\tabbreviated ID: %2$s\n\tmessage: %3$s\n\tURL: %4$s\n\tauthor: %5$s\n\tcommitter: %6$s\n\tactive: %7$s\n",
										commit.getId(),
										commit.getAbbreviatedId(),
										commit.getMessage(), commit.getUrl(),
										commit.getAuthor(),
										commit.getCommitter(),
										commit.getIsActive()));
			}
		} catch (Throwable e) {
			this.getLog()
					.error("Failed to retrieve HTML5 application commits.");

			throw new MojoExecutionException(
					"An error occured while retrieving HTML5 application commits",
					e);
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
