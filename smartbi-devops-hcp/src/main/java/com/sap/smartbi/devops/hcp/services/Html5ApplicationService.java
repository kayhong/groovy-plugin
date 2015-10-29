package com.sap.smartbi.devops.hcp.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.sap.smartbi.devops.hcp.internal.models.DeleteApplicationError;
import com.sap.smartbi.devops.hcp.internal.models.Html5ApplicationCommitImpl;
import com.sap.smartbi.devops.hcp.internal.models.Html5ApplicationImpl;
import com.sap.smartbi.devops.hcp.internal.models.Html5ApplicationVersionImpl;
import com.sap.smartbi.devops.hcp.models.HcpConnectionInfo;
import com.sap.smartbi.devops.hcp.models.Html5Application;
import com.sap.smartbi.devops.hcp.models.Html5ApplicationCommit;
import com.sap.smartbi.devops.hcp.models.Html5ApplicationVersion;

public final class Html5ApplicationService {

	private static final String CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token";
	private static final String CSRF_TOKEN_FETCH_HEADER_VALUE = "Fetch";
	private static final String URI_TEMPLATE = "https://dispatcher.{host}/hcproxy/b/api/accounts/{account}";

	private final HcpConnectionInfo connectionInfo;
	private final Client client;
	private String csrfToken;

	private Html5ApplicationService(final HcpConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;

		ClientConfig config = new ClientConfig();

		config.connectorProvider(new ApacheConnectorProvider());

		URI proxyUri = this.connectionInfo.getProxyUri();

		if (proxyUri != null) {
			config.property(ClientProperties.PROXY_URI, proxyUri);
		}

		config.register(HttpAuthenticationFeature.basic(
				this.connectionInfo.getUserName(),
				this.connectionInfo.getPassword()));

		this.client = ClientBuilder.newClient(config);
	}

	public static Html5ApplicationService getInstance(
			final HcpConnectionInfo connectionInfo) {
		if (connectionInfo == null) {
			throw new NullPointerException("connectionInfo is null");
		}

		return new Html5ApplicationService(connectionInfo);
	}

	public void activateApplicationVersion(final String name, String version) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		if (version == null) {
			throw new NullPointerException("\"version\" is null");
		}

		if (version.length() == 0) {
			throw new IllegalArgumentException("\"version\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("versions")
				.path(version)
				.path("action")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.POST,
					Entity.entity("ACTIVATE", MediaType.APPLICATION_JSON));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public Html5Application createApplication(final String name) {
		return this.doCreateApplication(name, null, null, false);
	}

	public Html5Application createApplication(final String name,
			final String displayName) {
		return this.doCreateApplication(name, displayName, null, false);
	}

	public Html5Application createApplication(final String name,
			final String displayName, final String repository,
			boolean cloudRepository) {
		return this.doCreateApplication(name, displayName, repository,
				cloudRepository);
	}

	public void createApplicationVersion(final String name,
			final String version, final String commitId) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		if (version == null) {
			throw new NullPointerException("\"version\" is null");
		}

		if (version.length() == 0) {
			throw new IllegalArgumentException("\"version\" is empty");
		}

		if (commitId == null) {
			throw new NullPointerException("\"commitId\" is null");
		}

		if (commitId.length() == 0) {
			throw new IllegalArgumentException("\"commitId\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("versions")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.POST, Entity.entity(
					new Html5ApplicationVersionImpl(version, commitId),
					MediaType.APPLICATION_JSON));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public void deleteApplication(final String name) {
		this.doDeleteApplication(name, true, true);
	}

	public void deleteApplication(final String name, boolean deleteSubscriptions) {
		this.doDeleteApplication(name, true, deleteSubscriptions);
	}

	public void deleteApplication(final String name, boolean deleteRepository,
			boolean deleteSubscriptions) {
		this.doDeleteApplication(name, deleteRepository, deleteSubscriptions);
	}

	public Html5Application getApplication(final String name) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.GET);

			return response.readEntity(Html5ApplicationImpl.class);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public void importApplicationContent(final String name,
			final String version, File directory) throws IOException {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		if (version == null) {
			throw new NullPointerException("\"version\" is null");
		}

		if (version.length() == 0) {
			throw new IllegalArgumentException("\"version\" is empty");
		}

		if (directory == null) {
			throw new NullPointerException("\"directory\" is null");
		}

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
					"\"directory\" does not denote a valid directory");
		}

		File zipFile = File.createTempFile("hcp", null);
		zipFile.deleteOnExit();

		try (ZipOutputStream zipStream = new ZipOutputStream(
				new FileOutputStream(zipFile))) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					// TODO handle directories, too
				} else {
					String entryName = directory.toURI()
							.relativize(file.toURI()).getPath();

					zipStream.putNextEntry(new ZipEntry(entryName));

					Files.copy(file.toPath(), zipStream);
				}
			}
		}
		;

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("versions")
				.path(version)
				.path("content")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try (FileInputStream zipStream = new FileInputStream(zipFile)) {
			response = this.invokeService(uri, HttpMethod.PUT, Entity.entity(
					zipStream, new MediaType("application", "zip")));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public Collection<Html5ApplicationCommit> listApplicationCommits(
			final String name) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("commits")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.GET);

			Html5ApplicationCommit[] commits = response
					.readEntity(Html5ApplicationCommitImpl[].class);

			return Collections.unmodifiableCollection(Arrays.asList(commits));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public Collection<Html5Application> listApplications() {
		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.GET);

			Html5Application[] applications = response
					.readEntity(Html5ApplicationImpl[].class);

			return Collections.unmodifiableCollection(Arrays
					.asList(applications));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public Collection<Html5ApplicationVersion> listApplicationVersions(
			final String name) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("versions")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.GET);

			Html5ApplicationVersion[] versions = response
					.readEntity(Html5ApplicationVersionImpl[].class);

			return Collections.unmodifiableCollection(Arrays.asList(versions));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public void restartApplication(final String name) {
		this.doInvokeApplicationAction(name, "RESTART");
	}

	public void startApplication(final String name) {
		this.doInvokeApplicationAction(name, "START");
	}

	public void stopApplication(final String name) {
		this.doInvokeApplicationAction(name, "STOP");
	}

	private Html5Application doCreateApplication(final String name,
			final String displayName, final String repository,
			boolean cloudRepository) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Html5ApplicationImpl application = new Html5ApplicationImpl();

		application.setName(name);
		application.setDisplayName(displayName);
		application.setRepository(repository);

		if (repository != null) {
			if (!cloudRepository) {
				application.setRepositoryDestination("hcproxy-gerritinternal");
			}
		}

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.POST,
					Entity.entity(application, MediaType.APPLICATION_JSON));
		} finally {
			if (response != null) {
				response.close();
			}
		}

		if (response == null) {
			// TODO throw some significant exception!!!
			throw new RuntimeException("HTML5 application was not created");
		} else {
			uri = UriBuilder
					.fromUri(URI_TEMPLATE)
					.path("applications")
					.path(name)
					.build(this.connectionInfo.getHost(),
							this.connectionInfo.getAccount());

			try {
				response = this.invokeService(uri, HttpMethod.GET);

				return response.readEntity(Html5ApplicationImpl.class);
			} finally {
				if (response != null) {
					response.close();
				}
			}
		}
	}

	private void doDeleteApplication(final String name,
			boolean deleteRepository, boolean deleteSubscriptions) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.queryParam("withRepository", deleteRepository)
				.queryParam("withSubscriptions", deleteSubscriptions)
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.DELETE);

			Status status = Status.fromStatusCode(response.getStatus());

			switch (status) {
			case OK: {
				DeleteApplicationError error = response
						.readEntity(DeleteApplicationError.class);

				break;
			}

			case CREATED: {
				break;
			}

			default: {
				// TODO throw some meaningful exception
				break;
			}
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private void doInvokeApplicationAction(final String name,
			final String action) {
		if (name == null) {
			throw new NullPointerException("\"name\" is null");
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("\"name\" is empty");
		}

		switch (action) {
		case "RESTART":
		case "START":
		case "STOP":

			break;

		default: {
			assert false : "invalid application action name";

			break;
		}
		}

		assert this.connectionInfo != null : "\"this.connectionInfo\" should not be null";

		URI uri = UriBuilder
				.fromUri(URI_TEMPLATE)
				.path("applications")
				.path(name)
				.path("action")
				.build(this.connectionInfo.getHost(),
						this.connectionInfo.getAccount());

		Response response = null;

		try {
			response = this.invokeService(uri, HttpMethod.POST,
					Entity.entity(action, MediaType.APPLICATION_JSON));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	private Response invokeService(final URI uri, String method) {
		return this.invokeService(uri, method, null);
	}

	private Response invokeService(final URI uri, String method,
			final Entity<?> entity) {
		assert uri != null : "\"uri\" should not be null";
		assert method != null : "\"method\" should not be null";

		assert this.client != null : "\"this.client\" should not be null";

		Response response = null;

		if (this.csrfToken == null) {
			if (HttpMethod.GET.equals(method)) {
				response = this.client
						.target(uri)
						.request(MediaType.APPLICATION_JSON)
						.header(CSRF_TOKEN_HEADER_NAME,
								CSRF_TOKEN_FETCH_HEADER_VALUE).get();

				this.csrfToken = response
						.getHeaderString(CSRF_TOKEN_HEADER_NAME);
			} else {
				URI fakeUri = UriBuilder
						.fromUri(URI_TEMPLATE)
						.path("applications")
						.build(this.connectionInfo.getHost(),
								this.connectionInfo.getAccount());

				Response fakeResponse = null;

				try {
					fakeResponse = this.client
							.target(fakeUri)
							.request()
							.header(CSRF_TOKEN_HEADER_NAME,
									CSRF_TOKEN_FETCH_HEADER_VALUE).get();

					this.csrfToken = fakeResponse
							.getHeaderString(CSRF_TOKEN_HEADER_NAME);
				} finally {
					if (fakeResponse != null) {
						fakeResponse.close();
					}
				}

				response = this.client.target(uri)
						.request(MediaType.APPLICATION_JSON)
						.header(CSRF_TOKEN_HEADER_NAME, this.csrfToken)
						.method(method, entity);
			}
		} else {
			response = this.client.target(uri)
					.request(MediaType.APPLICATION_JSON)
					.header(CSRF_TOKEN_HEADER_NAME, this.csrfToken)
					.method(method, entity);
		}

		StatusType statusType = response.getStatusInfo();

		switch (statusType.getFamily()) {
		case CLIENT_ERROR:
		case SERVER_ERROR: {
			String details = null;

			try {
				details = response.readEntity(String.class);
			} catch (IllegalStateException e) {
				// Nothing
			}

			String message = null;

			if (details == null) {
				message = String.format(
						"%1$s: status code = %2$d, reason = \"%3$s\"",
						statusType.getFamily(), response.getStatus(),
						statusType.getReasonPhrase());
			} else {
				message = String.format(
						"%1$s: status code = %2$d, reason = \"%3$s\" (%4$s)",
						statusType.getFamily(), response.getStatus(),
						statusType.getReasonPhrase(), details);
			}

			// TODO improve error reporting!
			throw new RuntimeException(message);
		}

		case INFORMATIONAL:
		case REDIRECTION:
		case SUCCESSFUL: {
			break;
		}

		case OTHER:
		default: {
			assert false : "Unexpected Response.Status.Family enumeration value";

			break;
		}
		}

		return response;
	}
}
