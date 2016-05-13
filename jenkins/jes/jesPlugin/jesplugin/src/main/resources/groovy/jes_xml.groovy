import jenkins.model.*
import hudson.model.*
import java.io.*
import groovy.xml.*
import groovy.util.*
import hudson.matrix.*
import hudson.FilePath



// current build
def build = build
// current job
def entryProject = build.getProject()

// aggregateStatus is a global variable for monitoring the final status of job execution chain
aggregateStatus = ""
// a global boolean variable
isTimeOut = false
// an iteration counter
iterationTimes = 0


git_backend_branch = ""
git_ui_branch = ""
git_qs_branch = ""

pbJobs = ""

parser = new XmlParser()


//my
def myJobName = jobName
def myBuildNumber = buildNumber
/*
    workspace of current job build
    outputFileFoler : where you generate the xml result
*/
workspace = build.workspace
outputFileFolder = workspace.toString() + "/target/xml"

/*
    getParemeters(build) function is used to get the List of parameters configured by user, if no parameters , return null
    the param passed should be the job build which is analyzed

*/
def getParameters(def build){
	def paramAction = build.getAction(ParametersAction)
	if(paramAction){
		def paramList = paramAction.getParameters()
		return paramList
	}
	else{
		return null
	}
}

/*
    function  isTheSameTime(project, upBuild) is used to be judge whether the project/job(1st param) has a build, the upStreamBuild of the build of this project/job is same with upBuild(2nd param) who triggerd this project/job 
              if one of the builds of project match upBuild, take this build out of this function to be used after, and return true. if not matched return false
*/
def isTheSameTime(def project, def upBuild){
	def runList = project.builds
	def iterator = runList.iterator()
	boolean blnIsTheSameBuild
	while(iterator.hasNext()){
		def build = iterator.next()
		def cause = build.getCause(Cause.UpstreamCause)
		if(cause){
			if(cause.getUpstreamRun().is(upBuild)){
				thisBuild = build
				blnIsTheSameBuild = true
				break
			}
			else{
				blnIsTheSameBuild = false
			}
		}
	}
	return blnIsTheSameBuild
}

/*
    aggregate Final Status of Job Execution Chain
    if There is any failure build, The aggregated is failur. If only unstable build, The aggregated is unstable.
    if No problems with all the builds, it is SUCCESS
*/
def getAggregateStatus(def result){
	if(this.aggregateStatus==""){
		this.aggregateStatus = result
	}
	else{
		if(result=="in Progress"){
			this.aggregateStatus = result
		}

		if(result=="FAILURE"&&this.aggregateStatus!="in Progress"){
			this.aggregateStatus = result
		}
		if(result=="UNSTABLE"&&this.aggregateStatus=="SUCCESS"){
			this.aggregateStatus = result
		}
	}
	listener.getLogger().println("The current Aggregate Status is +++ " + this.aggregateStatus)
}

/*
	
*/
def getBranchInfo(def paramName, def paramValue){

	switch(paramName) {
		case "git.backend.branch":
			this.git_backend_branch = paramValue
			break
		case "git.ui.branch":
			this.git_ui_branch = paramValue
			break
		case "git.qs.branch":
			this.git_qs_branch = paramValue
			break
	}
}

def addParams(def build, def node, def jobNode){
	if(getParameters(build)){
		def paramList = getParameters(build)
		for(def parameter in paramList){
			def paramName = parameter.getName()
			def paramValue = parameter.getValue().toString()

			getBranchInfo(paramName, paramValue)

			if(!node){
				node = jobNode.appendNode("parameters")
			}
			def parameterNode = node.appendNode("parameter")
			parameterNode.appendNode("name", paramName)
			parameterNode.appendNode("value", paramValue)
		}
	}
}

def addJobs(def node, def name, def url, def number, def result){
	node.appendNode("name", name)
	node.appendNode("number", number)
	node.appendNode("url", url)
	node.appendNode("status", result)
}

def gatherProblems(def name, def url, def number, def status){

	if(status!="SUCCESS"&&!this.pbJobs.contains(url)){
		if(status=="FAILURE"){
			this.pbJobs += """<li>
				<span><a href='${url}' target='_blank'>${name}</a></span>
				<span> #${number}</span>
				<span style='font-weight:bold;color:red;'> ${status}</span>
				</li>"""
		}
		if(status=="UNSTABLE"){
			this.pbJobs += """<li>
				<span><a href='${url}' target='_blank'>${name}</a></span>
				<span> #${number}</span>
				<span style='font-weight:bold;color:orange;'> ${status}</span>
				</li>"""
		}
	}
}










//method to find triggered SubJobs, Which not block parent job build
def findSubJobs(def project, def upBuild, def node){
	try {
		def actionsList = upBuild.getActions(action.getClass())
		def iterator = actionsList.iterator()
		while(iterator.hasNext()){
			def projectsList = iterator.next().getTriggeredProjects()
			listener.getLogger().println("****************** below is triggered non-blocked sub projects")
			listener.getLogger().println(projectsList)
			def projectsIterator = projectsList.iterator()
			while(projectsIterator.hasNext()){
				def subProject = projectsIterator.next()

				listener.getLogger().println(isTheSameTime(subProject, upBuild))
				if(isTheSameTime(subProject, upBuild)){
					def theBuild = thisBuild
					def parametersNode = null
					listener.getLogger().println(theBuild)

					if(subProject.getClass()==matrixProject.getClass()){
						listener.getLogger().println("is MatrixProject")

						def name = subProject.name
						def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
						def number = theBuild.number
						def result = theBuild.result.toString()
						if(theBuild.isInProgress()){
							result = "in Progress"
						}
						listener.getLogger().println("After juge whether this build is in Progress" + result)

						gatherProblems(name, url, number, result)
						getAggregateStatus(result)


						def jobNode = node.appendNode("job")
						addJobs(jobNode, name, url, number, result)
						addParams(theBuild, parametersNode, jobNode)

						findDownstream(subProject, theBuild, jobNode)

						def axisList = subProject.getAxes()
						def axisIterator = axisList.iterator()
						while(axisIterator.hasNext()){
							def axis = axisIterator.next()
							def axisName = axis.getName()
							for(def i = 0; i < axis.size(); i++){
								def value = axis.value(i)
								def configJobName = "${name}/${axisName}=${value}"
								def configJob = Jenkins.instance.getItemByFullName(configJobName)

								listener.getLogger().println(configJob)
								listener.getLogger().println(isTheSameTime(configJob, theBuild))
								listener.getLogger().println(theBuild)
								if(configJob.builds){

									if(isTheSameTime(configJob, theBuild)){
										def configBuild = thisBuild
										def configBuildNumber = configBuild.number
										def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
										def configResult = configBuild.result.toString()
										if(configBuild.isInProgress()){
											configResult = "in Progress"
										}

										listener.getLogger().println("After juge whether this build is in Progress" + configResult)

										gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
										getAggregateStatus(configResult)
										parametersNode = null
										def configJobNode = jobNode.appendNode("job")
										addJobs(configJobNode, configJobName, configBuildUrl, configBuildNumber, configResult)
										addParams(configBuild, parametersNode, configJobNode)
										findSubJobs(configJob, configBuild, configJobNode)
										findSubJobs(configBuild, configJobNode)
									}
								}
							}
						}
					}
					else{
						if(subProject.builds){
							listener.getLogger().println(isTheSameTime(subProject, upBuild))
							if(isTheSameTime(subProject, upBuild)){
								listener.getLogger().println("is a normal Project")
								theBuild = thisBuild
								def name = subProject.name
								def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
								def number = theBuild.number
								def result = theBuild.result.toString()
								if(theBuild.isInProgress()){
									result = "in Progress"
								}
								listener.getLogger().println("After juge whether this build is in Progress" + result)

								gatherProblems(name, url, number, result)
								getAggregateStatus(result)

								def jobNode = node.appendNode("job")
								addJobs(jobNode, name, url, number, result)
								addParams(theBuild, parametersNode, jobNode)

								findSubJobs(subProject, theBuild, jobNode)
								findSubJobs(theBuild, jobNode)
								findDownstream(subProject, theBuild, jobNode)
							}
						}
					}
				}
			}
		}
	}

	catch(NullPointerException e) {
		listener.getLogger().println(e)
		listener.getLogger().println("****There is no corresponding Project****")
	}
}






def findSubJobs(def upBuild, def node){
	try{
		def actionsList = upBuild.getActions(action.getClass())
		def actionIterator = actionsList.iterator()
		while(actionIterator.hasNext()){
			listener.getLogger().println("** these are blocked sub Jobs**")
			def buildsList = actionIterator.next().getTriggeredBuilds()
			def buildsIterator = buildsList.iterator()
			while(buildsIterator.hasNext()){
				try{
					def parametersNode = null
					def build = buildsIterator.next()
					def subProject = build.getProject()

					listener.getLogger().println(subProject)
					if(subProject.getClass()==matrixProject.getClass()){
						listener.getLogger().println("is Matrix")
						def name = subProject.name
						def url = build.properties.get("envVars")["BUILD_URL"].toString()
						def number = build.number
						def result = build.result.toString()
						if(build.isInProgress()){
							result = "in Progress"
						}
						listener.getLogger().println("After juge whether this build is in Progress" + result)
						gatherProblems(name, url, number, result)
						getAggregateStatus(result)

						def jobNode = node.appendNode("job")
						addJobs(jobNode, name, url, number, result)
						addParams(build, parametersNode, jobNode)

						findDownstream(subProject, build, jobNode)

						def axisList = subProject.getAxes()
						def axisIterator = axisList.iterator()

						while(axisIterator.hasNext()){
							def axis = axisIterator.next()
							def axisName = axis.getName()
							for(def i = 0; i < axis.size(); i++){
								def value = axis.value(i)
								listener.getLogger().println("axis value is ==========" + value)
								def configJobName = "${name}/${axisName}=${value}"
								listener.getLogger().println("configJobName is =======" + configJobName)
								def configJob = Jenkins.instance.getItemByFullName(configJobName)


								if(configJob.builds){

									if(isTheSameTime(configJob, build)){
										parametersNode = null
										def configBuild = thisBuild
										def configBuildNumber = configBuild.number
										def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
										def configResult = configBuild.result.toString()
										if(configBuild.isInProgress()){
											configResult = "in Progress"
										}
										listener.getLogger().println("After juge whether this build is in Progress" + configResult)
										gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
										getAggregateStatus(configResult)


										def configJobNode = jobNode.appendNode("job")
										addJobs(configJobNode, configJobName, configBuildUrl,configBuildNumber, configResult)

										addParams(configBuild, parametersNode, configJobNode)

										findSubJobs(configJob, configBuild, configJobNode)
										findSubJobs(configBuild, configJobNode)
									}

								}
							}
						}
					}
					else{
						listener.getLogger().println("is normal project")
						def name = subProject.name
						def url = build.properties.get("envVars")["BUILD_URL"].toString()
						def number = build.number
						def result = build.result.toString()
						if(build.isInProgress()){
							result = "in Progress"
						}
						listener.getLogger().println("After juge whether this build is in Progress" + result)

						gatherProblems(name, url, number, result)
						getAggregateStatus(result)


						def jobNode = node.appendNode("job")
						addJobs(jobNode, name, url, number, result)
						addParams(build, parametersNode, jobNode)


						findSubJobs(subProject, build, jobNode)
						findSubJobs(build, jobNode)
						findDownstream(subProject, build, jobNode)
					}
				}
				catch(NullPointerException e){
					listener.getLogger().println("------------------No Project of the build" + build)

				}
			}

		}

	}

	catch(NullPointerException e){
		listener.getLogger().println(e)
		listener.getLogger().println("**** No Project correspond the build***")
	}

}

//Method to find Downstream projects
def findDownstream(def project, def build, def node){
	try{
		def downstreamList = project.getDownstreamProjects()
		def iterator = downstreamList.iterator()
		while(iterator.hasNext()){
			listener.getLogger().println("**** this is downstream Jobs")
			def dproject = iterator.next()

			listener.getLogger().println(dproject)
			listener.getLogger().println(build)
			listener.getLogger().println(isTheSameTime(dproject, build))
			if(isTheSameTime(dproject, build)){
				def theBuild = thisBuild
				def parametersNode = null

				if(dproject.getClass()==matrixProject.getClass()){

					listener.getLogger().println("is  Matrix")

					def name = dproject.name
					def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
					def number = theBuild.number
					def result = theBuild.result.toString()
					if(theBuild.isInProgress()){
						result = "in Progress"
					}
					listener.getLogger().println("After juge whether this build is in Progress" + result)

					gatherProblems(name, url, number, result)
					getAggregateStatus(result)


					def jobNode = node.appendNode("job")
					addJobs(jobNode, name, url, number, result)
					addParams(theBuild, parametersNode, jobNode)


					findDownstream(dproject, theBuild, jobNode)
					def axisList = dproject.getAxes()
					def axisIterator = axisList.iterator()
					while(axisIterator.hasNext()){
						def axis = axisIterator.next()
						def axisName = axis.getName()
						for(def i = 0; i < axis.size(); i++){
							def value = axis.value(i)
							def configJobName = "${name}/${axisName}=${value}"
							def configJob = Jenkins.instance.getItemByFullName(configJobName)


							if(configJob.builds){

								if(isTheSameTime(configJob, theBuild)){
									parametersNode = null
									def configBuild = thisBuild
									listener.getLogger().println(configBuild)
									def configBuildNumber = configBuild.number
									def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
									def configResult = configBuild.result.toString()
									if(configBuild.isInProgress()){
										configResult = "in Progress"
									}
									listener.getLogger().println("After juge whether this build is in Progress" + configResult)
									gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
									getAggregateStatus(configResult)


									def configJobNode = jobNode.appendNode("job")
									addJobs(configJobNode, configJobName, configBuildUrl, configBuildNumber, configResult)
									addParams(configBuild, parametersNode, configJobNode)

									findSubJobs(configJob, configBuild, configJobNode)
									findSubJobs(configBuild, configJobNode)
								}
							}
						}
					}
				}
				else{
					if(dproject.builds){
						listener.getLogger().println("is normal project")
						if(isTheSameTime(dproject, build)){
							theBuild = thisBuild
							def name = dproject.name
							def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
							def number = theBuild.number
							def result = theBuild.result.toString()
							if(theBuild.isInProgress()){
								result = "in Progress"
							}
							listener.getLogger().println("After juge whether this build is in Progress" + result)

							gatherProblems(name, url, number, result)
							getAggregateStatus(result)

							def jobNode = node.appendNode("job")
							addJobs(jobNode, name, url, number, result)
							addParams(theBuild, parametersNode, jobNode)

							findSubJobs(dproject, theBuild, jobNode)
							findSubJobs(theBuild, jobNode)
							findDownstream(dproject, theBuild, jobNode)
						}
					}
				}
			}
		}
	}
	catch(NullPointerException e){
		listener.getLogger().println(e)
		listener.getLogger().println("*** No Project correspond the build")
	}
}


def findProjectsTree(def cause, def rootProject, def theBuild){

	this.iterationTimes = this.iterationTimes + 1

	def rootRun = theBuild
	if(cause){
		for(; cause; ){
			rootRun = cause.getUpstreamRun()
			rootProject = rootRun.getProject()
			cause = cause.getUpstreamRun().getCause(Cause.UpstreamCause)
		}
	}

	if(workspace.isRemote()){
		def channel = workspace.channel
		fp = new FilePath(channel, outputFileFolder + "/jes.xml")
	}
	else{
		fp = new FilePath(new File(outputFileFolder + "/jes.xml"))
	}

	fp.write("", null)
	def writer = new StringWriter()
	def xmlMarkup = new MarkupBuilder(writer)

	def rootjobName = rootProject.name
	def rootbuildUrl = rootRun.properties.get("envVars")["BUILD_URL"].toString()
	def rootbuildNumber = rootRun.number
	def result = rootRun.result.toString()

	if(this.iterationTimes > 1){
		this.aggregateStatus = ""
		this.pbJobs = ""
		gatherProblems(rootjobName, rootbuildUrl, rootbuildNumber, result)
	}

	getAggregateStatus(result)

	xmlMarkup.job{
		name(rootjobName)
		number(rootbuildNumber)
		url(rootbuildUrl)
		status(result)

	}

	fp.write(writer.toString(), null)
	def rootNode = this.parser.parseText(writer.toString())

	if(rootProject.getClass()== matrixProject.getClass()){
		def parametersNode = null
		listener.getLogger().println("is Matrix")
		addParams(rootRun, parametersNode, rootNode)


		def axisList = rootProject.getAxes()
		findDownstream(rootProject, rootRun, rootNode)
		def axisIterator = axisList.iterator()
		while(axisIterator.hasNext()){
			def axis = axisIterator.next()
			def axisName = axis.getName()
			for(def j = 0; j < axis.size(); j++){
				def value = axis.value(j)
				def configJobName = "${jobName}/${axisName}=${value}"
				def configJob = Jenkins.instance.getItemByFullName(configJobName)


				if(configJob.builds){

					if(isTheSameTime(configJob, rootRun)){
						parametersNode = null
						def configBuild = thisBuild
						def configBuildNumber = configBuild.number
						def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
						def configResult = configBuild.result.toString()
						if(configBuild.isInProgress()){
							configResult = "in Progress"
						}

						listener.getLogger().println("After juge whether this build is in Progress" + configResult)

						gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
						getAggregateStatus(configResult)

						def jobNode = rootNode.appendNode("job")
						addJobs(jobNode, configJobName, configBuildUrl, configBuildNumber, configResult)

						addParams(configBuild, parametersNode, jobNode)

						findSubJobs(configJob, configBuild, jobNode)
						findSubJobs(configJob, jobNode)
					}
				}
			}

		}

	}
	else{
		def parametersNode = null
		addParams(rootRun, parametersNode, rootNode)


		findDownstream(rootProject, rootRun, rootNode)
		findSubJobs(rootProject, rootRun, rootNode)
		findSubJobs(rootRun, rootNode)
	}

	def content = XmlUtil.serialize(rootNode)
	fp.write(content, null)

	try{
		if(this.aggregateStatus=="in Progress"){
			if(this.iterationTimes > 30){
				isTimeOut = true
			}
			else{
				listener.getLogger().println("***Kang is good ,")
				listener.getLogger().println("***but I have to sleep for 60 s for another execution for this function")
				listener.getLogger().println("***see you next time")
				Thread.sleep(1000*60)
				findProjectsTree(cause, rootProject, theBuild)
			}

		}

        def stringPa = new ParametersAction([
                new StringParameterValue("aggStatus", this.aggregateStatus),new StringParameterValue("git_ui_branch", this.git_ui_branch),
                new StringParameterValue("git_backend_branch", this.git_backend_branch),new StringParameterValue("git_qs_branch", this.git_qs_branch),
                new StringParameterValue("pbJobs", this.pbJobs)
            ])
        Thread.currentThread().executable.addAction(stringPa)        
        def boolPa = new ParametersAction(new BooleanParameterValue("isTimeOut", isTimeOut))
        Thread.currentThread().executable.addAction(boolPa)				
    }
	catch(InterruptedException ex){
		Thread.currentThread().interrupt()
	}


}

try{



	if(!myJobName.isEmpty()){
		if(myBuildNumber<=0){
			entryProject = Jenkins.instance.getItem(myJobName)
			build = entryProject.getLastBuild()
			listener.getLogger().println("Analyse job : " + entryProject.getName())
			listener.getLogger().println( "        #   : " + build.getNumber())
		}else{
			entryProject = Jenkins.instance.getItem(myJobName)
			build = entryProject.getBuildByNumber(myBuildNumber)
			listener.getLogger().println("Analyse job : " + entryProject.getName())
			listener.getLogger().println( "        #   : " + build.getNumber())
		}
	}else if(myBuildNumber>0){
		build = entryProject.getBuildByNumber(myBuildNumber)		
	}
	
	listener.getLogger().println("Analyse job : " + entryProject.getName())
	listener.getLogger().println( "        #   : " + build.getNumber())
	def upstreamCause = build.getCause(Cause.UpstreamCause)
	findProjectsTree(upstreamCause, entryProject, build)
}
catch(NullPointerException e){
	listener.getLogger().println("************************************")
	listener.getLogger().println(e)
	listener.getLogger().println("No Such Job in Jenkins or No This build correspond thie build Number")
	listener.getLogger().println("************************************")

}



