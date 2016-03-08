package com.sap.smartbi.devops.hcp.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.smartbi.devops.hcp.internal.models.html5.RoleAssignment;
import com.sap.smartbi.devops.hcp.models.html5.Application;
import com.sap.smartbi.devops.hcp.models.html5.Commit;
import com.sap.smartbi.devops.hcp.models.html5.HcpConnectionInfo;
import com.sap.smartbi.devops.hcp.models.html5.Subscription;
import com.sap.smartbi.devops.hcp.models.html5.Version;

public final class Html5ApplicationServiceTest {

	private static HcpConnectionInfo CONNECTION_INFO;
	private static HcpConnectionInfo CONSUMER_CONNECTION_INFO;

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

		String dispatcher = properties.getProperty("dispatcher");
		String host = properties.getProperty("host");
		String account = properties.getProperty("account");
		String consumerAccount = properties.getProperty("consumer.account");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		String proxyUri = properties.getProperty("proxy.uri");

		CONNECTION_INFO = new HcpConnectionInfo(dispatcher, host, account,
				user, password, proxyUri == null ? null : URI.create(proxyUri));

		CONSUMER_CONNECTION_INFO = new HcpConnectionInfo(dispatcher, host,
				consumerAccount, user, password, proxyUri == null ? null
						: URI.create(proxyUri));
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

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationRoleAssignmentsNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationRoleAssignments(null,
						new ArrayList<RoleAssignment>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationRoleAssignmentsEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationRoleAssignments("",
						new ArrayList<RoleAssignment>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateApplicationRoleAssignmentsNullAssignments() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createApplicationRoleAssignments(UUID.randomUUID().toString(),
						null);
	}

	@Test
	public void testCreateSubscription() {
		Html5ApplicationService service1 = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5ApplicationService service2 = Html5ApplicationService
				.getInstance(CONSUMER_CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();
			String providerName = createSomehowUniqueApplicationName();

			application = service1.createApplication(providerName);

			Assert.assertTrue(service1.createSubscriptionCandidate(
					providerName, CONSUMER_CONNECTION_INFO.getAccount()));

			Subscription subscription = service2.createSubscription(name,
					CONNECTION_INFO.getAccount(), providerName);

			Assert.assertNotNull(subscription);
			Assert.assertEquals(name, subscription.getName());
			Assert.assertEquals(CONNECTION_INFO.getAccount(),
					subscription.getProviderAccount());
			Assert.assertEquals(providerName, subscription.getProviderName());
			Assert.assertNotNull(subscription.getUri());
		} finally {
			if (application != null) {
				service1.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSubscriptionNullName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription(null, CONNECTION_INFO.getAccount(),
						createSomehowUniqueApplicationName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSubscriptionEmptyName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription("", CONNECTION_INFO.getAccount(),
						createSomehowUniqueApplicationName());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSubscriptionNullProviderAccount() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription(createSomehowUniqueApplicationName(), null,
						createSomehowUniqueApplicationName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSubscriptionEmptyProviderAccount() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription(createSomehowUniqueApplicationName(), "",
						createSomehowUniqueApplicationName());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSubscriptionNullProviderName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription(createSomehowUniqueApplicationName(),
						CONNECTION_INFO.getAccount(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSubscriptionEmptyProviderName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.createSubscription(createSomehowUniqueApplicationName(),
						CONNECTION_INFO.getAccount(), "");
	}

	@Test
	public void testCreateSubscriptionCandidate() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Assert.assertNotNull(application);
			Assert.assertEquals(name, application.getName());

			Assert.assertTrue(service.createSubscriptionCandidate(name,
					CONSUMER_CONNECTION_INFO.getAccount()));
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSubscriptionCandidateEmptyConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createSubscriptionCandidate(
						createSomehowUniqueApplicationName(), "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSubscriptionCandidateEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createSubscriptionCandidate("",
						CONSUMER_CONNECTION_INFO.getAccount());
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSubscriptionCandidateNullConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createSubscriptionCandidate(
						createSomehowUniqueApplicationName(), null);
	}

	@Test(expected = NullPointerException.class)
	public void testCreateSubscriptionCandidateNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.createSubscriptionCandidate(null,
						CONSUMER_CONNECTION_INFO.getAccount());
	}

	@Test
	public void testCreateApplicationString() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

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

		Application application = null;

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

		Application application = service.createApplication(name);

		Assert.assertNotNull(application);

		service.deleteApplication(application.getName());

		for (Application current : service.listApplications()) {
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
	public void testDeleteSubscription() {
		Html5ApplicationService service1 = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5ApplicationService service2 = Html5ApplicationService
				.getInstance(CONSUMER_CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();
			String providerName = createSomehowUniqueApplicationName();

			application = service1.createApplication(providerName);

			Assert.assertTrue(service1.createSubscriptionCandidate(
					providerName, CONSUMER_CONNECTION_INFO.getAccount()));

			Subscription subscription = service2.createSubscription(name,
					CONNECTION_INFO.getAccount(), providerName);

			Assert.assertNotNull(subscription);

			Assert.assertTrue(service2.deleteSubscription(name));
		} finally {
			if (application != null) {
				service1.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteSubscriptionNullName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.deleteSubscription(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteSubscriptionEmptyName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.deleteSubscription("");
	}

	@Test
	public void testDeleteSubscriptionCandidate() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Assert.assertNotNull(application);
			Assert.assertEquals(name, application.getName());

			Assert.assertTrue(service.createSubscriptionCandidate(name,
					CONSUMER_CONNECTION_INFO.getAccount()));

			Assert.assertTrue(service.deleteSubscriptionCandidate(
					application.getName(),
					CONSUMER_CONNECTION_INFO.getAccount()));
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteSubscriptionCandidateEmptyConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.deleteSubscriptionCandidate(
						createSomehowUniqueApplicationName(), "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteSubscriptionCandidateEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.deleteSubscriptionCandidate("",
						CONSUMER_CONNECTION_INFO.getAccount());
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteSubscriptionCandidateNullConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.deleteSubscriptionCandidate(
						createSomehowUniqueApplicationName(), null);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteSubscriptionCandidateNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.deleteSubscriptionCandidate(null,
						CONSUMER_CONNECTION_INFO.getAccount());
	}

	@Test
	public void testGetApplication() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Collection<Application> applications = service.listApplications();

		for (Application application : applications) {
			Application otherApplication = service.getApplication(application
					.getName());

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

	@Test
	public void testGetSubscription() {
		Html5ApplicationService service1 = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5ApplicationService service2 = Html5ApplicationService
				.getInstance(CONSUMER_CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();
			String providerName = createSomehowUniqueApplicationName();

			application = service1.createApplication(providerName);

			Assert.assertNotNull(application);

			Assert.assertTrue(service1.createSubscriptionCandidate(
					providerName, CONSUMER_CONNECTION_INFO.getAccount()));

			Subscription expectedSubscription = service2.createSubscription(
					name, CONNECTION_INFO.getAccount(), providerName);

			Assert.assertNotNull(expectedSubscription);

			Subscription subscription = service2.getSubscription(name);

			Assert.assertNotNull(subscription);

			Assert.assertEquals(expectedSubscription.getActiveVersion(),
					subscription.getActiveVersion());
			Assert.assertEquals(name, subscription.getName());
			Assert.assertEquals(CONNECTION_INFO.getAccount(),
					subscription.getProviderAccount());
			Assert.assertEquals(providerName, subscription.getProviderName());
			Assert.assertEquals(expectedSubscription.getProviderStatus(),
					subscription.getProviderStatus());
			Assert.assertEquals(expectedSubscription.getStartedVersion(),
					subscription.getStartedVersion());
			Assert.assertEquals(expectedSubscription.getUri(),
					subscription.getUri());
		} finally {
			if (application != null) {
				service1.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetSubscriptionNullName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.getSubscription(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSubscriptionEmptyName() {
		Html5ApplicationService.getInstance(CONSUMER_CONNECTION_INFO)
				.getSubscription("");
	}

	@Test
	public void testGetSubscriptionCandidate() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			service.createSubscriptionCandidate(name,
					CONSUMER_CONNECTION_INFO.getAccount());

			Assert.assertTrue(service.getSubscriptionCandidate(name,
					CONSUMER_CONNECTION_INFO.getAccount()));

		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testGetSubscriptionCandidateNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.getSubscriptionCandidate(null, UUID.randomUUID().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSubscriptionCandidateEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.getSubscriptionCandidate("", UUID.randomUUID().toString());
	}

	@Test(expected = NullPointerException.class)
	public void testGetSubscriptionCandidateNullConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.getSubscriptionCandidate(createSomehowUniqueApplicationName(),
						null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSubscriptionCandidateEmptyConsumerAccount() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.getSubscriptionCandidate(createSomehowUniqueApplicationName(),
						"");
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

		Path subDirectory = Files.createDirectory(directory
				.resolve("sub-directory"));

		Path file3 = Files.createFile(subDirectory.resolve("file3.txt"));

		try (FileWriter writer = new FileWriter(file3.toFile())) {
			writer.write("yet another content");
		}

		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

		try {
			application = service
					.createApplication(createSomehowUniqueApplicationName());

			Assert.assertTrue(service.importApplicationContent(
					application.getName(), UUID.randomUUID().toString(),
					directory.toFile()));
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

		Application application = null;

		try {
			application = service
					.createApplication(createSomehowUniqueApplicationName());

			Collection<Commit> commits = service
					.listApplicationCommits(application.getName());

			Assert.assertNotNull(commits);
			Assert.assertEquals(1, commits.size());

			Commit commit = commits.iterator().next();

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
		Collection<Application> applications = Html5ApplicationService
				.getInstance(CONNECTION_INFO).listApplications();

		Assert.assertNotNull(applications);

		for (Application application : applications) {
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

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Collection<Version> versions = service
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

	@Test
	public void testListSubscriptions() {

		Html5ApplicationService service1 = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Html5ApplicationService service2 = Html5ApplicationService
				.getInstance(CONSUMER_CONNECTION_INFO);

		Collection<Subscription> subscriptions = service2.listSubscriptions();

		Assert.assertNotNull(subscriptions);

		Subscription subscription = null;

		// "webide" application is always subscribed from account "sapwebide"
		Assert.assertEquals(1, subscriptions.size());

		subscription = subscriptions.toArray(new Subscription[] {})[0];

		Assert.assertEquals("webide", subscription.getName());
		Assert.assertEquals("sapwebide", subscription.getProviderAccount());
		Assert.assertEquals("webide", subscription.getProviderName());

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();
			String providerName = createSomehowUniqueApplicationName();

			application = service1.createApplication(providerName);

			Assert.assertNotNull(application);

			Assert.assertTrue(service1.createSubscriptionCandidate(
					providerName, CONSUMER_CONNECTION_INFO.getAccount()));

			subscription = service2.createSubscription(name,
					CONNECTION_INFO.getAccount(), providerName);

			Assert.assertNotNull(subscription);

			subscriptions = service2.listSubscriptions();

			Assert.assertNotNull(subscriptions);
			Assert.assertEquals(2, subscriptions.size());

			boolean found = false;

			for (Subscription current : subscriptions) {
				if (name.equals(current.getName())) {
					Assert.assertEquals(CONNECTION_INFO.getAccount(),
							current.getProviderAccount());
					Assert.assertEquals(providerName, current.getProviderName());
					Assert.assertNotNull(current.getUri());

					found = true;
				}
			}

			Assert.assertTrue(found);
		} finally {
			if (application != null) {
				service1.deleteApplication(application.getName());
			}
		}
	}

	@Test
	public void testListSusbcriptionCandidates() {
		Html5ApplicationService service = Html5ApplicationService
				.getInstance(CONNECTION_INFO);

		Application application = null;

		try {
			String name = createSomehowUniqueApplicationName();

			application = service.createApplication(name);

			Collection<String> consumerAccounts = service
					.listSubscriptionCandidates(name);

			Assert.assertNotNull(consumerAccounts);
			Assert.assertEquals(0, consumerAccounts.size());

			service.createSubscriptionCandidate(name,
					CONSUMER_CONNECTION_INFO.getAccount());

			consumerAccounts = service.listSubscriptionCandidates(name);

			Assert.assertNotNull(consumerAccounts);
			Assert.assertEquals(1, consumerAccounts.size());
			Assert.assertEquals(CONSUMER_CONNECTION_INFO.getAccount(),
					consumerAccounts.toArray(new String[] {})[0]);
		} finally {
			if (application != null) {
				service.deleteApplication(application.getName());
			}
		}
	}

	@Test(expected = NullPointerException.class)
	public void testListSubscriptionCandidatesNullName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listSubscriptionCandidates(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListSubscriptionCandidatesEmptyName() {
		Html5ApplicationService.getInstance(CONNECTION_INFO)
				.listSubscriptionCandidates("");
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
