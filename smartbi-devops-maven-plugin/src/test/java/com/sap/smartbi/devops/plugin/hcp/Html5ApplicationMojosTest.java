package com.sap.smartbi.devops.plugin.hcp;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public final class Html5ApplicationMojosTest {
	@Parameter
	public String goal;

	@Rule
	public MojoRule rule = new MojoRule();

	@Rule
	public TestResources resources = new TestResources();

	@Parameters
	public static Collection<Object[]> data() {

		Object[][] data = new Object[][] { { "list-html5-applications" },
				{ "create-html5-application" }, { "list-html5-applications" },
				{ "get-html5-application" },
				{ "list-html5-application-commits" },
				{ "list-html5-application-versions" },
				{ "import-html5-application-content" },
				{ "list-html5-application-commits" },
				{ "list-html5-application-versions" },
				{ "activate-html5-application-version" },
				{ "get-html5-application" }, { "start-html5-application" },
				{ "get-html5-application" }, { "restart-html5-application" },
				{ "get-html5-application" }, { "stop-html5-application" },
				{ "get-html5-application" }, { "delete-html5-application" },
				{ "list-html5-applications" } };

		return Arrays.asList(data);
	}

	@Test
	public void testHtml5ApplicationMojos() throws Exception {
		File directory = this.resources.getBasedir(this.goal);

		File pom = new File(directory, "pom.xml");

		Assert.assertNotNull(pom);
		Assert.assertTrue(pom.exists());

		Mojo mojo = this.rule.lookupMojo(this.goal, pom);

		Assert.assertNotNull(mojo);

		mojo.execute();
	}
}
