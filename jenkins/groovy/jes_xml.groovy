import jenkins.model.*
import hudson.model.*
import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction;
import java.io.*
import groovy.xml.*
import groovy.util.*
import hudson.matrix.*
import hudson.FilePath


def build = Thread.currentThread().executable
def resolver = build.buildVariableResolver
def myJobName = resolver.resolve("jobName")
def myBuildNumber = Integer.parseInt(resolver.resolve("buildNumber"))

def aggregateStatus = "unknown"

def git_backend_branch = "unkown"
def git_ui_branch = "unknown"
def git_qs_branch = "unkown"

pbJobs = "unkown"

parser = new XmlParser()





println "Analyse job : " + myJobName
println "        #   : " + myBuildNumber




workspace = build.workspace.toString()
outputFileFolder = workspace + "/target"

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

def isTheSameTime(def project, def upBuild){
	def runMap = project.builds
	boolean blnIsTheSameBuild
	for(def o = 0; o < runMap.size(); o++){
		def cause = runMap.get(o).getCause(Cause.UpstreamCause)
		if(cause){
			if(cause.getUpstreamRun().is(upBuild)){
				thisBuild = runMap.get(o)
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

def getAggregateStatus(def result){

	if(result=="in Progress"){
		this.aggregateStatus = result
	}

	if(result=="FAILURE"&&this.aggregateStatus!="in Progress"){
		this.aggregateStatus = result
	}
	if(result=="UNSTABLE"&&this.aggregateStatus=="SUCCESS"){
		this.aggregateStatus = result
	}
	println "The current Aggregate Status is +++ " + this.aggregateStatus
}

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

def gatherProblems(def name, def url, def number, def status){
	if(this.pbJobs=="unkown"){
		if(status!="SUCCESS"){
			this.pbJobs += """<li><span>${name}</span>
			<span><a href='${url}' target='_blank'>${url}</a></span>
			<span> #${number}</span>
			<span> ${status}</span>
			</li>"""
		}
	}
	else{
		if(status!="SUCCESS"){
			this.pbJobs += """<li><span>${name}</span>
			<span><a href='${url}' target='_blank'>${url}</a></span>
			<span> #${number}</span>
			<span> ${status}</span>
			</li>"""
		}
	}
	

}










//method to find triggered SubJobs
def findSubJobs(def project, def upBuild, def node){
	try {
		def actionsList = upBuild.getActions(BuildInfoExporterAction)
		for(def i = 0; i < actionsList.size(); i++ ){
			def projectsList = actionsList.get(i).getTriggeredProjects()
			println "****************** below is triggered non-blocked sub projects"
			println projectsList

			for(def j = 0; j < projectsList.size(); j++){
				def subProject = projectsList.get(j)


				println isTheSameTime(subProject, upBuild)
				if(isTheSameTime(subProject, upBuild)){
					def theBuild = thisBuild
					def parametersNode = null
					println theBuild

					if(subProject instanceof MatrixProject){
						println "is MatrixProject"

						def name = subProject.name
						def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
						def number = theBuild.number
						def result = theBuild.result.toString()
						if(theBuild.isInProgress()){
							result = "in Progress"
						}
						println "After juge whether this build is in Progress" + result

						gatherProblems(name, url, number, result)

						getAggregateStatus(result)
						

						def jobNode = node.appendNode("job")
						jobNode.appendNode("name", name)
						jobNode.appendNode("number", number)
						jobNode.appendNode("url", url)
						jobNode.appendNode("status", result)

						addParams(theBuild, parametersNode, jobNode)



						findDownstream(subProject, theBuild, jobNode)

						def axisList = subProject.getAxes()

						for(def k = 0; k < axisList.size(); k++){
							def axis = axisList.get(k)
							def axisName = axis.getName()
							for(def l = 0; l < axis.size(); l++){
								def value = axis.value(l)
								def configJobName = "${name}/${axisName}=${value}"
								def configJob = Jenkins.instance.getItemByFullName(configJobName)


								println configJob
								println isTheSameTime(configJob, theBuild)
								println theBuild
								if(configJob.builds){

									if(isTheSameTime(configJob, theBuild)){
										def configBuild = thisBuild
										def configBuildNumber = configBuild.number
										def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
										def configResult = configBuild.result.toString()
										if(configBuild.isInProgress()){
											configResult = "in Progress"
										}

										println "After juge whether this build is in Progress" + configResult

										gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
										getAggregateStatus(configResult)
										

										parametersNode = null

										def configJobNode = jobNode.appendNode("job")
										configJobNode.appendNode("name", configJobName)
										configJobNode.appendNode("number", configBuildNumber)
										configJobNode.appendNode("url", configBuildUrl)
										configJobNode.appendNode("status", configResult)

										addParams(configBuild, parametersNode, configJobNode)



										findSubJobs(configJob, configBuild, configJobNode)
										findSubJobs(configBuild, configJobNode)

									}
									else{
										this.aggregateStatus = "in Progress"
									}

								}
							}

						}

					}
					else{
						if(subProject.builds){
							println isTheSameTime(subProject, upBuild)
							if(isTheSameTime(subProject, upBuild)){
								println "is a normal Project"


								theBuild = thisBuild

								def name = subProject.name
								def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
								def number = theBuild.number
								def result = theBuild.result.toString()
								if(theBuild.isInProgress()){
									result = "in Progress"
								}
								println "After juge whether this build is in Progress" + result

								gatherProblems(name, url, number, result)
								getAggregateStatus(result)
								

								def jobNode = node.appendNode("job")
								jobNode.appendNode("name", name)
								jobNode.appendNode("number", number)
								jobNode.appendNode("url", url)
								jobNode.appendNode("status", result)

								addParams(theBuild, parametersNode, jobNode)



								findSubJobs(subProject, theBuild, jobNode)
								findSubJobs(theBuild, jobNode)
								findDownstream(subProject, theBuild, jobNode)
							}
						}
					}
				}
				else{
					this.aggregateStatus = "in Progress"
				}
			}
		}
	}


	catch(NullPointerException e) {
		println e
		println "****There is no corresponding Project****"
	}
}






def findSubJobs(def upBuild, def node){
	try{
		def actionsList = upBuild.getActions(BuildInfoExporterAction)
		for(def i = 0; i < actionsList.size(); i++ ){
			println "** these are blocked sub Jobs**"
			def buildsList = actionsList.get(i).getTriggeredBuilds()

			for(def j = 0; j < buildsList.size(); j++){

				try{
					def parametersNode = null
					def subProject = buildsList.get(j).getProject()
					println subProject
					if(subProject instanceof MatrixProject){
						println "is Matrix"
						def name = subProject.name
						def url = buildsList.get(j).properties.get("envVars")["BUILD_URL"].toString()
						def number = buildsList.get(j).number
						def result = buildsList.get(j).result.toString()
						if(buildsList.get(j).isInProgress()){
							result = "in Progress"
						}
						println "After juge whether this build is in Progress" + result
						gatherProblems(name, url, number, result)
						getAggregateStatus(result)

						def jobNode = node.appendNode("job")
						jobNode.appendNode("name", name)
						jobNode.appendNode("number", number)
						jobNode.appendNode("url", url)
						jobNode.appendNode("status", result)

						addParams(buildsList.get(j), parametersNode, jobNode)



						findDownstream(subProject, buildsList.get(j), jobNode)


						def axisList = subProject.getAxes()

						for(def k = 0; k < axisList.size(); k++){
							def axis = axisList.get(k)
							def axisName = axis.getName()
							for(def l = 0; l < axis.size(); l++){
								def value = axis.value(l)
								println "axis value is ==========" + value
								def configJobName = "${name}/${axisName}=${value}"
								println "configJobName is =======" + configJobName
								def configJob = Jenkins.instance.getItemByFullName(configJobName)


								if(configJob.builds){

									if(isTheSameTime(configJob, buildsList.get(j))){
										parametersNode = null
										def configBuild = thisBuild
										def configBuildNumber = configBuild.number
										def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
										def configResult = configBuild.result.toString()
										if(configBuild.isInProgress()){
											configResult = "in Progress"
										}
										println "After juge whether this build is in Progress" + configResult
										gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
										getAggregateStatus(configResult)
										

										def configJobNode = jobNode.appendNode("job")
										configJobNode.appendNode("name", configJobName)
										configJobNode.appendNode("number", configBuildNumber)
										configJobNode.appendNode("url", configBuildUrl)
										configJobNode.appendNode("status", configResult)


										addParams(configBuild, parametersNode, configJobNode)


										findSubJobs(configJob, configBuild, configJobNode)
										findSubJobs(configBuild, configJobNode)
									}
									else{
										this.aggregateStatus = "in Progress"
									}

								}
							}
						}
					}
					else{
						println "is normal project"
						def name = subProject.name
						def url = buildsList.get(j).properties.get("envVars")["BUILD_URL"].toString()
						def number = buildsList.get(j).number
						def result = buildsList.get(j).result.toString()
						if(buildsList.get(j).isInProgress()){
							result = "in Progress"
						}
						println "After juge whether this build is in Progress" + result

						gatherProblems(name, url, number, result)
						getAggregateStatus(result)


						def jobNode = node.appendNode("job")
						jobNode.appendNode("name", name)
						jobNode.appendNode("number", number)
						jobNode.appendNode("url", url)
						jobNode.appendNode("status", result)

						addParams(buildsList.get(j), parametersNode, jobNode)


						findSubJobs(subProject, buildsList.get(j), jobNode)
						findSubJobs(buildsList.get(j), jobNode)
						findDownstream(subProject, buildsList.get(j), jobNode)
					}
				}
				catch(NullPointerException e){
					println "------------------No Project of the build" + buildsList.get(j)

				}
			}
		}

	}

	catch(NullPointerException e){
		println e
		println "**** No Project correspond the build***"
	}

}

//Method to find Downstream projects
def findDownstream(def project, def build, def node){
	try{
		def downstreamList = project.getDownstreamProjects()

		for(def n = 0; n < downstreamList.size(); n++){
			println "**** this is downstream Jobs"
			def dproject = downstreamList.get(n)
			println dproject
			println build
			println isTheSameTime(dproject, build)
			if(isTheSameTime(dproject, build)){
				def theBuild = thisBuild
				def parametersNode = null

				if(dproject instanceof MatrixProject){

					println "is  Matrix"

					def name = dproject.name
					def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
					def number = theBuild.number
					def result = theBuild.result.toString()
					if(theBuild.isInProgress()){
						result = "in Progress"
					}
					println "After juge whether this build is in Progress" + result

					gatherProblems(name, url, number, result)
					getAggregateStatus(result)					


					def jobNode = node.appendNode("job")
					jobNode.appendNode("name", name)
					jobNode.appendNode("number", number)
					jobNode.appendNode("url", url)
					jobNode.appendNode("status", result)

					addParams(theBuild, parametersNode, jobNode)


					findDownstream(dproject, theBuild, jobNode)
					def axisList = dproject.getAxes()
					for(def i = 0; i < axisList.size(); i++){
						def axis = axisList.get(i)
						def axisName = axis.getName()
						for(def j = 0; j < axis.size(); j++){
							def value = axis.value(j)
							def configJobName = "${name}/${axisName}=${value}"
							def configJob = Jenkins.instance.getItemByFullName(configJobName)


							if(configJob.builds){

								if(isTheSameTime(configJob, theBuild)){
									parametersNode = null
									def configBuild = thisBuild
									println configBuild
									def configBuildNumber = configBuild.number
									def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
									def configResult = configBuild.result.toString()
									if(configBuild.isInProgress()){
										configResult = "in Progress"							
									}
									println "After juge whether this build is in Progress" + configResult
									gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
									getAggregateStatus(configResult)
							

									def configJobNode = jobNode.appendNode("job")
									configJobNode.appendNode("name", configJobName)
									configJobNode.appendNode("number", configBuildNumber)
									configJobNode.appendNode("url", configBuildUrl)
									configJobNode.appendNode("status", configResult)

									addParams(configBuild, parametersNode, configJobNode)



									findSubJobs(configJob, configBuild, configJobNode)
									findSubJobs(configBuild, configJobNode)


								}
								else{
									this.aggregateStatus = "in Progress"
								}

							}
						}

					}

				}
				else{

					if(dproject.builds){
						println "is normal project"

						if(isTheSameTime(dproject, build)){

							theBuild = thisBuild
							def name = dproject.name
							def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
							def number = theBuild.number
							def result = theBuild.result.toString()
							if(theBuild.isInProgress()){
								result = "in Progress"
							}
							println "After juge whether this build is in Progress" + result

							gatherProblems(name, url, number, result)
							getAggregateStatus(result)

							


							def jobNode = node.appendNode("job")
							jobNode.appendNode("name", name)
							jobNode.appendNode("number", number)
							jobNode.appendNode("url", url)
							jobNode.appendNode("status", result)

							addParams(theBuild, parametersNode, jobNode)




							findSubJobs(dproject, theBuild, jobNode)
							findSubJobs(theBuild, jobNode)
							findDownstream(dproject, theBuild, jobNode)
						}
					}
				}
			}
			else{
				this.aggregateStatus = "in Progress"
			}
		}
	}
	catch(NullPointerException e){
		println e
		println "*** No Project correspond the build"
	}


}


def findProjectsTree(def cause, def rootProject, def theBuild){

	def rootRun = theBuild
	if(cause){
		for(; cause; ){
			rootRun = cause.getUpstreamRun()
			rootProject = rootRun.getProject()
			cause = cause.getUpstreamRun().getCause(Cause.UpstreamCause)
		}
	}

	if(build.workspace.isRemote()){
		def channel = build.workspace.channel
		fp = new FilePath(channel, outputFileFolder + "/jes.xml")
	}
	else{
		fp = new FilePath(new File(outputFileFolder + "/jes.xml"))
	}

	fp.write("", null)
	def writer = new StringWriter()
	def xmlMarkup = new MarkupBuilder(writer)

	def jobName = rootProject.name
	def buildUrl = rootRun.properties.get("envVars")["BUILD_URL"].toString()
	def buildNumber = rootRun.number
	def result = rootRun.result.toString()

	gatherProblems(jobName, buildUrl, buildNumber, result)

	this.aggregateStatus = result

	xmlMarkup.job{
		name(jobName)
		number(buildNumber)
		url(buildUrl)
		status(result)

	}



	fp.write(writer.toString(), null)


	def rootNode = this.parser.parseText(writer.toString())



	if(rootProject instanceof MatrixProject){
		def parametersNode = null
		println  "is Matrix"
		addParams(rootRun, parametersNode, rootNode)


		def axisList = rootProject.getAxes()
		findDownstream(rootProject, rootRun, rootNode)

		for(def i = 0; i < axisList.size(); i++){
			def axis = axisList.get(i)
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

						println "After juge whether this build is in Progress" + configResult

						gatherProblems(configJobName, configBuildUrl, configBuildNumber, configResult)
						getAggregateStatus(configResult)		



						def jobNode = rootNode.appendNode("job")
						jobNode.appendNode("name",configJobName)
						jobNode.appendNode("number", configBuildNumber)
						jobNode.appendNode("url", configBuildUrl)
						jobNode.appendNode("status", configResult)

						addParams(configBuild, parametersNode, jobNode)




						findSubJobs(configJob, configBuild, jobNode)
						findSubJobs(configJob, jobNode)
					}
					else{
						this.aggregateStatus = "in Progress"
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


}

try{
	def entryProject = Jenkins.instance.getItem(myJobName)
	def theBuild = entryProject.getBuildByNumber(myBuildNumber)
	def upstreamCause = theBuild.getCause(Cause.UpstreamCause)
	findProjectsTree(upstreamCause, entryProject, theBuild)
	def pa = new ParametersAction([
		new StringParameterValue("aggStatus", this.aggregateStatus)
		])
	build.addAction(pa)
	build.addAction(new ParametersAction([
		new StringParameterValue("git_ui_branch", this.git_ui_branch)
		]))
	build.addAction(new ParametersAction([
		new StringParameterValue("git_backend_branch", this.git_backend_branch)
		]))
	build.addAction(new ParametersAction([
		new StringParameterValue("git_qs_branch", this.git_qs_branch)
		]))
	build.addAction(new ParametersAction([
		new StringParameterValue("pbJobs", this.pbJobs)
		]))
}
catch(NullPointerException e){
	println "************************************"
	println e
	println "No Such Job in Jenkins or No This build correspond thie build Number"
	println "************************************"

}



