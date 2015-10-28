package com.sap.smartbi.devops.hcp.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.smartbi.devops.hcp.models.HcpConnectionInfo;
import com.sap.smartbi.devops.hcp.models.Html5Application;
import com.sap.smartbi.devops.hcp.models.Html5ApplicationCommit;
import com.sap.smartbi.devops.hcp.models.Html5ApplicationVersion;

public final class Html5ApplicationServiceTest {

	private static HcpConnectionInfo CONNECTION_INFO;

	@BeforeClass
	public static void loadHcpConnectionProperties() {
		Properties properties = new Properties();

		File file = new File("hcp-connection.properties");

		if (file.exists()) {
			try {
				try (FileInputStream stream = new FileInputStream(file)) {

					properties.load(stream);
				}
			} catch (IOException e) {
				// Nothing
			}
		}

		String host = properties.getProperty("host");
		String account = properties.getProperty("account");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		String proxyUri = properties.getProperty("proxy.uri");

		CONNECTION_INFO = new HcpConnectionInfo(host, account, user, password,
				proxyUri == null ? null : URI.create(proxyUri));
	}

	@Test
	@Ignore
	public void testActivateApplicationVersion() {
		// TODO
	}

	@Test(expected = NullPointerException.class)
	public void testActivateApplicationVersionNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.activateApplicationVersion(null, UUID.randomUUID().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActivateApplicationVersionEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.activateApplicationVersion("", UUID.randomUUID().toString());
	}

	@Test(expected = NullPointerException.class)
	public void testActivateApplicationVersionNullVersion() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.activateApplicationVersion(
						createSomehowUniqueApplicationName(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActivateApplicationVersionEmptyVersion() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.activateApplicationVersion(
						createSomehowUniqueApplicationName(), "");
	}

	@Test(expected = NullPointerException.class)
	public void testCreateApplicationVersionNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion(null, UUID.randomUUID().toString(),
						UUID.randomUUID().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationVersionEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion("", UUID.randomUUID().toString(),
						UUID.randomUUID().toString());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateApplicationVersionNullVersion() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion(UUID.randomUUID().toString(), null,
						UUID.randomUUID().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationVersionEmptyVersion() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion(UUID.randomUUID().toString(), "",
						UUID.randomUUID().toString());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateApplicationVersionNullCommitId() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion(UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationVersionEmptyCommitId() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationVersion(UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "");
	}

	@Test
	public void testGetInstance() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Assert.assertNotNull(service);
	}

	@Test
	public void testCreateApplicationString() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Assert.assertNotNull(application);
			Assert.assertEquals(name, application.getDisplayName());
			Assert.assertNull(application.getDescription());
			Assert.assertEquals(name, application.getName());

			String repository = application.getRepository();

			Assert.assertNotNull(repository);
			Assert.assertTrue(repository.length() > 0);
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test
	public void testCreateApplicationStringString() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();
			String displayName = UUID.randomUUID().toString();

			application = service.createApplication(name, displayName);

			Assert.assertNotNull(application);
			Assert.assertEquals(displayName, application.getDisplayName());
			Assert.assertNull(application.getDescription());
			Assert.assertEquals(name, application.getName());

			String repository = application.getRepository();

			Assert.assertNotNull(repository);
			Assert.assertTrue(repository.length() > 0);
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test
	@Ignore
	public void testCreateApplicationStringStringStringBoolean() {
		// TODO
	}

	@Test
	public void testDeleteApplicationString() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		String name = createSomehowUniqueApplicationName();

		Html5Application application = service.createApplication(name);

		Assert.assertNotNull(application);

		service.deleteApplication(application.getName());

		for (Html5Application current : service.listApplications()) {
			Assert.assertNotEquals(name, current.getName());
		}
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteApplicationStringNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteApplicationStringEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				"");
	}

	@Test
	@Ignore
	public void testDeleteApplicationStringBoolean() {
		// TODO
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteApplicationStringBooleanNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				null, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteApplicationStringBooleanEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				"", false);
	}

	@Test
	@Ignore
	public void testDeleteApplicationStringBooleanBoolean() {
		// TODO
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteApplicationStringBooleanBooleanNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				null, true, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteApplicationStringBooleanBooleanEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).deleteApplication(
				"", false, true);
	}

	@Test
	public void testGetApplication() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Collection<Html5Application> applications = service.listApplications();

		for (Html5Application application : applications) {
			Html5Application otherApplication = service
					.getApplication(application.getName());

			String name = otherApplication.getName();

			Assert.assertNotNull(name);
			Assert.assertTrue(name.length() > 0);

			String repository = otherApplication.getRepository();

			Assert.assertNotNull(repository);
			Assert.assertTrue(repository.length() > 0);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetApplicationNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).getApplication(
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetApplicationEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO).getApplication("");
	}

	@Test
	public void testImportApplicationContent() throws IOException {
		Path directory = Files.createTempDirectory("hcp");

		Path file1 = Files.createFile(directory.resolve("file1.txt"));

		try (FileWriter writer = new FileWriter(file1.toFile())) {
			writer.write("some content");
		}

		Path file2 = Files.createFile(directory.resolve("file2.txt"));

		try (FileWriter writer = new FileWriter(file2.toFile())) {
			writer.write("some other content");
		}

		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5Application application = null;

		try {
			application = service
					.createApplication(createSomehowUniqueApplicationName());

			service.importApplicationContent(application.getName(), UUID
					.randomUUID().toString(), directory.toFile());
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testImportApplicationContentNullName() throws IOException {
		File directory = File.createTempFile("dumb", null).toPath().getParent()
				.toFile();

		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.importApplicationContent(null, UUID.randomUUID().toString(),
						directory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testImportApplicationContentEmptyName() throws IOException {
		File directory = File.createTempFile("dumb", null).toPath().getParent()
				.toFile();

		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.importApplicationContent("", UUID.randomUUID().toString(),
						directory);
	}

	@Test(expected = NullPointerException.class)
	public void testImportApplicationContentNullVersion() throws IOException {
		File directory = File.createTempFile("dumb", null).toPath().getParent()
				.toFile();

		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.importApplicationContent(createSomehowUniqueApplicationName(),
						null, directory);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testImportApplicationContentEmptyVersion() throws IOException {
		File directory = File.createTempFile("dumb", null).toPath().getParent()
				.toFile();

		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.importApplicationContent(createSomehowUniqueApplicationName(),
						"", directory);
	}

	@Test(expected = NullPointerException.class)
	public void testImportApplicationContentNullDirectory() throws IOException {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.importApplicationContent(createSomehowUniqueApplicationName(),
						UUID.randomUUID().toString(), null);
	}

	@Test
	public void testListApplicationCommits() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5Application application = null;

		try {
			application = service
					.createApplication(createSomehowUniqueApplicationName());

			Collection<Html5ApplicationCommit> commits = service
					.listApplicationCommits(application.getName());

			Assert.assertNotNull(commits);
			Assert.assertEquals(1, commits.size());

			Html5ApplicationCommit commit = commits.iterator().next();

			Assert.assertNotNull(commit);

			String abbreviatedId = commit.getAbbreviatedId();

			Assert.assertNotNull(abbreviatedId);
			Assert.assertTrue(abbreviatedId.length() > 0);

			String author = commit.getAuthor();

			Assert.assertNotNull(author);
			Assert.assertTrue(author.length() > 0);

			String committer = commit.getCommitter();

			Assert.assertNotNull(committer);
			Assert.assertTrue(committer.length() > 0);

			String id = commit.getId();

			Assert.assertNotNull(id);
			Assert.assertTrue(id.length() > 0);

			// getIsActive()?

			String message = commit.getMessage();

			Assert.assertNotNull(message);
			Assert.assertTrue(message.length() > 0);

			// getVersion() may return null (not Git tag)

			Assert.assertNotNull(commit.getUrl());
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testListApplicationCommitsNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listApplicationCommits(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListApplicationCommitsEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listApplicationCommits("");
	}

	@Test
	public void testListApplications() {
		Collection<Html5Application> applications = Html5ApplicationService
				.getInstance(CONNECTION_INFO).listApplications();

		Assert.assertNotNull(applications);

		for (Html5Application application : applications) {
			String name = application.getName();

			Assert.assertNotNull(name);
			Assert.assertTrue(name.length() > 0);

			String displayName = application.getDisplayName();

			Assert.assertNotNull(displayName);
			Assert.assertTrue(displayName.length() > 0);

			String status = application.getStatus();

			Assert.assertNotNull(status);
			Assert.assertTrue(status.length() > 0);

			String repository = application.getRepository();

			Assert.assertNotNull(repository);
			Assert.assertTrue(repository.length() > 0);

			Assert.assertNotNull(application.getUrl());
		}
	}

	@Test
	public void testListApplicationVersions() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Collection<Html5ApplicationVersion> versions = service
					.listApplicationVersions(application.getName());

			Assert.assertNotNull(versions);
			Assert.assertEquals(0, versions.size());

			// TODO enrich test coverage
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testListApplicationVersionsNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listApplicationVersions(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListApplicationVersionsEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listApplicationVersions("");
	}

	@Test
	public void testRestartApplication() {
		// TODO restart requires an active version, but error cases should also
		// be tested (no active version)
	}

	@Test
	public void testStartApplication() {
		// TODO start requires an active version, but error cases should also be
		// tested (no active version)
	}

	@Test
	public void testStopApplication() {
		// TODO
	}

	private static String createSomehowUniqueApplicationName() {
		return ("a" + UUID.randomUUID().toString().replace("-", "")).substring(
				0, 29);
	}
}
