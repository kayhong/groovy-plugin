package com.sap.smartbi.devops.plugin.hcp.html5;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public final class GenerateApplicationDescriptorMojoTest {

	private final static String GOAL = "generate-html5-application-descriptor";
	@Rule
	public TestResources resources = new TestResources();
	
	@Rule
	public MojoRule rule = new MojoRule();
	
	@Test
	public void testGenerateHtml5ApplicationDescriptorMojo() throws Exception {
		File directory = this.resources.getBasedir(GOAL);

		File pom = new File(directory, "pom.xml");

		Assert.assertNotNull(pom);
		Assert.assertTrue(pom.exists());

		Mojo mojo = this.rule.lookupMojo(GOAL, pom);

		Assert.assertNotNull(mojo);

		mojo.execute();
	}
}
