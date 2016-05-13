package jenkins.plugins.jes;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Recorder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;


import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import org.kohsuke.stapler.QueryParameter;
import groovy.lang.Binding;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import hudson.matrix.MatrixProject;
import hudson.remoting.VirtualChannel;

import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction.BuildReference;
import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction;
import javax.servlet.ServletException;

import java.io.*;
import java.net.*;

//import javax.xml.crypto.dsig.Transform;
//import javax.xml.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.lang.Exception;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link HelloWorldBuilder} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */

public class JesPostBuilder extends Recorder implements SimpleBuildStep {

	private final String jobName;
	private final int buildNumber;
	private final String xsltPath;
	private final int sleep;
	private final String outputFiles;

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public JesPostBuilder(String jobName, int buildNumber, String xsltPath, int sleep, String outputFiles) {
		this.jobName = jobName;
		this.buildNumber = buildNumber;
		this.xsltPath = xsltPath;
		this.sleep = sleep;
		this.outputFiles = outputFiles;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return BuildStepMonitor.NONE;
	}

	@Override
	public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener)
			throws IOException {
		// This is where you 'build' the project.

		if (sleep > 0) {
			listener.getLogger().println("Just wait for " + sleep + " s");
			try {
				Thread.sleep(sleep * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * Use GroovyShell to call groovy script from java class
		 *
		 * InputStream in =
		 * this.getClass().getResourceAsStream("/groovy/jes_xml.groovy");
		 * BufferedReader br = new BufferedReader( new InputStreamReader(in));
		 * 
		 * GroovyShell shell = new GroovyShell(); Script script =
		 * shell.parse(br); Binding binding = new Binding();
		 * binding.setVariable("jobName", jobName);
		 * binding.setVariable("buildNumber", buildNumber);
		 * binding.setVariable("xsltPath", xsltPath);
		 * binding.setVariable("listener", listener);
		 * script.setBinding(binding); script.run();
		 */

		/*
		 * create a BuildInfoExporterAction variable to be passed to groovy
		 */
		BuildReference buildRef = new BuildReference("reference");
		BuildInfoExporterAction action = new BuildInfoExporterAction(buildRef);

		// create a variable type of MatrixProject to be passed to groovy
		MatrixProject matrixProject = new MatrixProject("matrixProject");

		/*
		 * listener.getLogger().println("This action is object of this class");
		 * listener.getLogger().println(action.getClass());
		 */

		listener.getLogger().println("This java code will call groovy script");

		// Get the url of groovy source file
		URL groovyUrl = this.getClass().getResource("/groovy/jes_xml.groovy");
		listener.getLogger().println(groovyUrl);

		URL[] roots = { groovyUrl };

		Binding binding = new Binding();
		/*
		 * build is the current build of the current Project
		 * listener is used to get log print out in Jenkins
		 * jobName and buildNumber are input by user in the job config page
		 * action is BuildInfoExporterAction defined by parameterized-trigger plugin
		 * matrixProject is multi-configure jobs
		  * */
		binding.setVariable("build", build);
		binding.setVariable("listener", listener);
		binding.setVariable("jobName", jobName);
		binding.setVariable("buildNumber", buildNumber);
		binding.setVariable("action", action);		
		binding.setVariable("matrixProject", matrixProject);

		try {
			/*
			 *  call the "jes_xml.groovy" groovy script by GroovyScriptEngine proviede by groovy api
			 *  "jes_xml.groovy" provide the core function to traverse analyse and gather Job Execution Chain Information 
			 */
			GroovyScriptEngine gse = new GroovyScriptEngine(roots);
			gse.run("jes_xml.groovy", binding);
			listener.getLogger().println("==================================");
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			listener.getLogger().println("resource Exception : " + e.getMessage());
			listener.getLogger().println(e.getStackTrace());
		} catch (ScriptException e) {
			listener.getLogger().println("ScriptException : " + e.getMessage());
			listener.getLogger().println(e.getStackTrace());
		}

		try {
			/* 
			 * The code in this try block is to transform xml result to html result
			 * xml result if generated by jes_xml.groovy
			 * html will be generated by this java transformer
			 */
			TransformerFactory factory = TransformerFactory.newInstance();			
			Transformer transformer;			
			InputStream xslStream;
			
			/*
			 *	 File like object with remoting support.
			 *	 Unlike File, which always implies a file path on the current computer, 
			 *	 FilePath represents a file path on a specific agent or the master. 
			 *  Despite that, FilePath can be used much like File.
			 *  It exposes a bunch of operations , and when invoked against a file on a remote node, 
			 *  FilePath executes the necessary code remotely, thereby providing semi-transparent file operations.   
			 *  
			 */
			//xmlFp means the FilePath of xml result generated by jes_xml.groovy
			FilePath xmlFp = workspace.child("target/xml/jes.xml");
			listener.getLogger().println("whether the xml file exist? : " + xmlFp.exists());
			listener.getLogger().println("This is xml file name is : " + xmlFp.getName());
			
			/*
			 * Prepare xml source to be transform
			 * and declare some variables which will be initialized and used
			 * */
			InputStream xmlStream = xmlFp.read();
			StreamSource xmlSource = new StreamSource(xmlStream);
			VirtualChannel channel;
			FilePath fp;
			StreamResult htmlTarget;
			
			/*
			 * if xsltPaht is not input by the user in job config page
			 * the xmltransformer will use default xsl file which is put in src/main/resources/xslt/jes.xsl 
			 */
			if (xsltPath.isEmpty()) {
				xslStream = this.getClass().getResourceAsStream("/xslt/jes.xsl");
				transformer = factory.newTransformer(new StreamSource(xslStream));
				
				/*
				 * generate html result in specific file path in workspace.
				 * if outputFiles is not set by user in Job config page, it will use defaut path /target/html/jes.html
				 */
				if (workspace.isRemote()) {
					channel = workspace.getChannel();
					if (outputFiles.isEmpty())
						fp = new FilePath(channel, workspace + "/target/html/jes.html");
					else
						fp = new FilePath(channel, workspace + outputFiles);

					htmlTarget = new StreamResult(fp.write());
					transformer.transform(xmlSource, htmlTarget);

				} else {
					if (outputFiles.isEmpty())
						fp = new FilePath(new File(workspace + "/target/html/jes.html"));
					else
						fp = new FilePath(new File(workspace + "/" + outputFiles));

					htmlTarget = new StreamResult(fp.write());
					transformer.transform(xmlSource, htmlTarget);
				}
			} else {
				FilePath xsltFp;
				if (workspace.isRemote()) {
					channel = workspace.getChannel();
					xsltFp = new FilePath(channel, workspace + xsltPath);
					xslStream = xsltFp.read();
					transformer = factory.newTransformer(new StreamSource(xslStream));
					if (outputFiles.isEmpty())
						fp = new FilePath(channel, workspace + "/target/html/jes.html");
					else
						fp = new FilePath(channel, workspace + outputFiles);

					htmlTarget = new StreamResult(fp.write());
					transformer.transform(xmlSource, htmlTarget);
				} else {
					xsltFp = new FilePath(new File(workspace + "/" + xsltPath));
					xslStream = xsltFp.read();
					transformer = factory.newTransformer(new StreamSource(xslStream));
					if (outputFiles.isEmpty())
						fp = new FilePath(new File(workspace + "/target/html/jes.html"));
					else
						fp = new FilePath(new File(workspace + "/" + outputFiles));

					htmlTarget = new StreamResult(fp.write());
					transformer.transform(xmlSource, htmlTarget);
				}
			}

		} catch (Exception e) {
			listener.getLogger().println(e);
		}

	}

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * Descriptor for {@link HelloWorldBuilder}. Used as a singleton. The class
	 * is marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See
	 * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension // This indicates to Jenkins that this is an implementation of an
				// extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		/**
		 * To persist global configuration information, simply store it in a
		 * field and call save().
		 *
		 * <p>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private boolean useFrench;

		/**
		 * In order to load the persisted global configuration, you have to call
		 * load() in the constructor.
		 */
		public DescriptorImpl() {
			load();
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 *
		 * @param value
		 *            This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the
		 *         browser.
		 *         <p>
		 *         Note that returning {@link FormValidation#error(String)} does
		 *         not prevent the form from being saved. It just means that a
		 *         message will be displayed to the user.
		 */

		public FormValidation doCheckJobName(@QueryParameter String value) throws IOException, ServletException {			
			if (value.length() < 4)
				return FormValidation.warning("Analyze current job");
			return FormValidation.ok();
		}

		public FormValidation doCheckBuildNumber(@QueryParameter int value) throws IOException, ServletException {

			if (value <= 0)
				return FormValidation.warning("Analyze the last build");
			return FormValidation.ok();
		}

		public FormValidation doCheckXsltPath(@QueryParameter String value) throws IOException, ServletException {
			if (value.isEmpty())
				return FormValidation.warning("Use defaut xslt");
			return FormValidation.ok();
		}

		public FormValidation doCheckOutputFiles(@QueryParameter String value) throws IOException, ServletException {
			if (value.isEmpty())
				return FormValidation.warning("the html result file will be created at workspace/target/html/jes.html");
			return FormValidation.ok();
		}
		
		public FormValidation doCheckSleep(@QueryParameter int value) throws IOException, ServletException {
			if (value <= 0)
				return FormValidation.warning("execute imediately");
			return FormValidation.ok();
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project
			// types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		@Override
		public String getDisplayName() {
			return "Job Execution Chain";
		}

		/*
		 * @Override public boolean configure(StaplerRequest req, JSONObject
		 * formData) throws FormException { // To persist global configuration
		 * information, // set that to properties and call save(). useFrench =
		 * formData.getBoolean("useFrench"); // ^Can also use req.bindJSON(this,
		 * formData); // (easier when there are many fields; need set* methods
		 * for this, // like setUseFrench) save(); return super.configure(req,
		 * formData); }
		 */

		/*
		 * This method returns true if the global configuration says we should
		 * speak French.
		 *
		 * The method name is bit awkward because global.jelly calls this method
		 * to determine the initial state of the checkbox by the naming
		 * convention.
		 */
		
	}
}
