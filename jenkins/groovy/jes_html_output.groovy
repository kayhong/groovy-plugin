	import jenkins.model.*
	import hudson.model.*
	import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction;
	import java.io.*
	import groovy.xml.*
	import groovy.util.*
	import hudson.matrix.*



	def entryProject = Jenkins.instance.getItem("JobParent")
	def theBuild = entryProject.getBuildByNumber(133)
	def upstreamCause = theBuild.getCause(Cause.UpstreamCause)
	def build = Thread.currentThread().executable

	workspace = build.workspace.toString()


	def getColor(def result){

		switch(result){
			case "SUCCESS" : 
			return "green"
			break
			case "FAILURE" :
			return "red"
			break
			default :
			return "orange"
			break

		}

	}

	def isTheSameTime(def project, def upBuild){
		def runMap = project.builds
		
		for(def o = 0; o < runMap.size(); o++){
			def cause = runMap.get(o).getCause(Cause.UpstreamCause)
			if(cause){
				if(cause.getUpstreamRun() == upBuild){
					thisBuild = runMap.get(o)
					return true				
				}
				else{
					return false
				}
			}
		}
	}




	//method to find triggered SubJobs 
	def findSubJobs(def project, def upBuild, def node){

		def actionsList = upBuild.getActions(BuildInfoExporterAction)
		def ul = null
		for(def i = 0; i < actionsList.size(); i++ ){
			def projectsList = actionsList.get(i).getTriggeredProjects() 
			println "****************** below is triggered non-blocked sub projects"
			println projectsList
			
			for(def j = 0; j < projectsList.size(); j++){
				def subProject = projectsList.get(j)
				println subProject
				println upBuild

				println isTheSameTime(subProject, upBuild)
				if(isTheSameTime(subProject, upBuild)){
					def theBuild = thisBuild
					println theBuild

					if(subProject instanceof MatrixProject){
						println "is MatrixProject"

						def name = subProject.name
						def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
						def number = theBuild.number
						def result = theBuild.result.toString()
						def color = getColor(result)

						if(!ul)
						ul = node.appendNode("ul", [style : "list-style-type:circle"])

						def li = ul.appendNode("li", [style : "border-style: solid; border-width: 1px"])
						li.appendNode("span", [style : "border-style: solid; border-width: 1px"], name)
						def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
						urlSpan.appendNode(
							"a",
							[href : url, target : "_blank"],
							"Job_Url"
							)
						li.appendNode(
							"span",
							[style : "border-style: solid; border-width: 1px"],
							"#" + number
							)
						li.appendNode(
							"span",
							[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
							result
							)

						findDownstream(subProject, theBuild, li)

						def axisList = subProject.getAxes()
						def configRootTag = null
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
										color = getColor(configResult)

										if(!configRootTag)
										configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

										li = configRootTag.appendNode("li", [style : "border-style: solid; border-width: 1px"])
										li.appendNode("span", [style : "border-style: solid; border-width: 1px"], configJobName)
										urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
										urlSpan.appendNode(
											"a",
											[href : configBuildUrl, target : "_blank"],
											"Job_Url"
											)
										li.appendNode(
											"span",
											[style : "border-style: solid; border-width: 1px"],
											"#" + configBuildNumber
											)
										li.appendNode(
											"span",
											[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
											configResult
											)


										findSubJobs(configJob, configBuild, li)
										findSubJobs(configBuild, li)

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
								def color = getColor(result)

								if(!ul)
								ul = node.appendNode("ul", [style : "list-style-type:circle"])

								def li = ul.appendNode(
									"li",
									[style : "border-style: solid; border-width: 1px"])

								li.appendNode(
									"span",
									[style : "border-style: solid; border-width: 1px"],
									name
									)
								def urlSpan = li.appendNode(
									"span",
									[style : "border-style: solid; border-width: 1px"]
									)
								urlSpan.appendNode(
									"a",
									[href : url, target : "_blank"],
									"Job_Url"
									)

								li.appendNode(
									"span",
									[style : "border-style: solid; border-width: 1px"],
									"#"+number
									)
								li.appendNode(
									"span",
									[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
									result
									)

								findSubJobs(subProject, theBuild, li)
								findSubJobs(theBuild, li)
								findDownstream(subProject, theBuild, li)  					
							}
						}
					}
				}
			}
		}
	}


	def findSubJobs(def upBuild, def node){
		def actionsList = upBuild.getActions(BuildInfoExporterAction)
		def ul = null
		for(def i = 0; i < actionsList.size(); i++ ){
			println "** these are blocked sub Jobs**"
			def buildsList = actionsList.get(i).getTriggeredBuilds()

			for(def j = 0; j < buildsList.size(); j++){

				def subProject = buildsList.get(j).getProject()
				println subProject

				if(subProject instanceof MatrixProject){
					println "is Matrix"
					def name = subProject.name
					def url = buildsList.get(j).properties.get("envVars")["BUILD_URL"].toString()
					def number = buildsList.get(j).number
					def result = buildsList.get(j).result.toString()
					def color = getColor(result)

					if(!ul)
					ul = node.appendNode("ul", [style : "list-style-type:circle"])

					def li = ul.appendNode("li", [style : "border-style: solid; border-width: 1px"])
					li.appendNode("span", [style : "border-style: solid; border-width: 1px"], name)
					def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
					urlSpan.appendNode(
						"a",
						[href : url, target : "_blank"],
						"Job_Url"
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px"],
						"#" + number
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
						result
						)

					findDownstream(subProject, buildsList.get(j), li)


					def axisList = subProject.getAxes()
					def configRootTag = null
					for(def k = 0; k < axisList.size(); k++){
						def axis = axisList.get(k)
						def axisName = axis.getName()
						for(def l = 0; l < axis.size(); l++){
							def value = axis.value(l)
							println "axis value is ==========" + value
							subProject.name
							def configJobName = "${name}/${axisName}=${value}"
							println "configJobName is =======" + configJobName
							def configJob = Jenkins.instance.getItemByFullName(configJobName)


							if(configJob.builds){
								
								if(isTheSameTime(configJob, buildsList.get(j))){
									def configBuild = thisBuild				
									def configBuildNumber = configBuild.number
									def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
									def configResult = configBuild.result.toString()
									color = getColor(configResult)
									
									if(!configRootTag)
									configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

									li = configRootTag.appendNode("li", [style : "border-style: solid; border-width: 1px"])
									li.appendNode("span", [style : "border-style: solid; border-width: 1px"], configJobName)
									urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
									urlSpan.appendNode(
										"a",
										[href : configBuildUrl, target : "_blank"],
										"Job_Url"
										)
									li.appendNode(
										"span",
										[style : "border-style: solid; border-width: 1px"],
										"#" + configBuildNumber
										)
									li.appendNode(
										"span",
										[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
										configResult
										)


									findSubJobs(configJob, configBuild, li)
									findSubJobs(configBuild, li)									
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
					def color = getColor(result)

					if(!ul)
					ul = node.appendNode("ul", [style : "list-style-type:circle"])

					def li = ul.appendNode(
						"li",
						[style : "border-style: solid; border-width: 1px"])
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px"],
						name
						)
					def urlSpan = li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px"]
						)
					urlSpan.appendNode(
						"a",
						[href : url, target : "_blank"],
						"Job_Url"
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px"],
						"#"+ number
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
						result
						)


					findSubJobs(subProject, buildsList.get(j), li)
					findSubJobs(buildsList.get(j), li)
					findDownstream(subProject, buildsList.get(j), li)
				}
			}
		}
	}

	//Method to find Downstream projects
	def findDownstream(def project, def build, def node){

		def downstreamList = project.getDownstreamProjects()
		def ul = null

		for(def n = 0; n < downstreamList.size(); n++){
			println "**** this is downstream Jobs"			
			def dproject = downstreamList.get(n)
			println dproject
			println build
			println isTheSameTime(dproject, build)
			if(isTheSameTime(dproject, build)){
				def theBuild = thisBuild
				
				if(dproject instanceof MatrixProject){
					println "is  Matrix"

					def name = dproject.name
					def url = theBuild.properties.get("envVars")["BUILD_URL"].toString()
					def number = theBuild.number
					def result = theBuild.result.toString()
					def color = getColor(result)

					if(!ul)
					ul = node.appendNode("ul", [style : "list-style-type:circle"])

					def li = ul.appendNode("li", [style : "border-style: solid; border-width: 1px"])
					li.appendNode("span", [style : "border-style: solid; border-width: 1px"], name)
					def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
					urlSpan.appendNode(
						"a",
						[href : url, target : "_blank"],
						"Job_Url"
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px"],
						"#" + number
						)
					li.appendNode(
						"span",
						[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
						result
						)

					findDownstream(dproject, theBuild, li)
					def axisList = dproject.getAxes()
					def configRootTag = null
					for(def i = 0; i < axisList.size(); i++){
						def axis = axisList.get(i)
						def axisName = axis.getName()					
						for(def j = 0; j < axis.size(); j++){
							print "This is the ${j} axis value"
							dproject.getFullName()
							def value = axis.value(j)
							println "axis value is ==========" + value
							def configJobName = "${name}/${axisName}=${value}"
							println "configJobName is =======" + configJobName
							def configJob = Jenkins.instance.getItemByFullName(configJobName)

							

							
							if(configJob.builds){

								if(isTheSameTime(configJob, theBuild)){
									def configBuild = thisBuild								
									println configBuild
									def configBuildNumber = configBuild.number
									def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
									def configResult = configBuild.result.toString()
									color = getColor(configResult)
									
									if(configRootTag)
									configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

									li = configRootTag.appendNode("li", [style : "border-style: solid; border-width: 1px"])
									li.appendNode("span", [style : "border-style: solid; border-width: 1px"], configJobName)
									urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
									urlSpan.appendNode(
										"a",
										[href : configBuildUrl, target : "_blank"],
										"Job_Url"
										)
									li.appendNode(
										"span",
										[style : "border-style: solid; border-width: 1px"],
										"#" + configBuildNumber
										)
									li.appendNode(
										"span",
										[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
										configResult
										)


									findSubJobs(configJob, configBuild, li)
									findSubJobs(configBuild, li)



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
							def color = getColor(result)

							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							def li = ul.appendNode(
								"li",
								[style : "border-style: solid; border-width: 1px"])
							li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px"],
								name
								)
							def urlSpan = li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px"]
								)
							urlSpan.appendNode(
								"a",
								[href : url, target : "_blank"],
								"Job_Url"
								)
							li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px"],
								"#"+number
								)
							li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
								result
								)

							findSubJobs(dproject, theBuild, li)
							findSubJobs(theBuild, li)
							findDownstream(dproject, theBuild, li)      			
						}
					}
				}
			}
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

		def file = new File(workspace + "\\jes.html")
		def writer = new FileWriter(file)
		def html = new MarkupBuilder(writer)

		def name = rootProject.name
		def url = rootRun.properties.get("envVars")["BUILD_URL"].toString()
		def number = rootRun.number
		def result = rootRun.result.toString()
		def color = getColor(result)

		html.html{
			body{
				ul(style : "list-style-type:circle"){
					li{
						span(style : "border-style: solid; border-width: 1px", name)
						span(style : "border-style: solid; border-width: 1px"){
							a(href : url, target : "_blank", "Job_Url")
						}
						span(style : "border-style: solid; border-width: 1px", "#" + number)
						span(style : "border-style: solid; border-width: 1px; background-color:${color}", result)
					}
				}
			}
		}

		def parser = new XmlParser()
		def rootNode = parser.parse(file)

		def node = rootNode.body.ul.li[0]


		if(rootProject instanceof MatrixProject){
			println  "is Matrix"			
			def axisList = rootProject.getAxes()
			def ul =null

			for(def i = 0; i < axisList.size(); i++){
				def axis = axisList.get(i)
				def axisName = axis.getName()
				for(def j = 0; j < axis.size(); j++){
					def value = axis.value(j)
					def configJobName = "${name}/${axisName}=${value}"
					def configJob = Jenkins.instance.getItemByFullName(configJobName)

					if(configJob.builds){

						if(isTheSameTime(configJob, rootRun)){
							rootRun = thisBuild

							def configBuildNumber = rootRun.number
							def configBuildUrl = rootRun.properties.get("envVars")["BUILD_URL"].toString()
							def configResult = rootRun.result.toString()
							color = color(configResult)
							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							def li = ul.appendNode("li", [style : "border-style: solid; border-width: 1px"])
							li.appendNode("span", [style : "border-style: solid; border-width: 1px"], configJobName)
							def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
							urlSpan.appendNode(
								"a",
								[href : configBuildUrl, target : "_blank"],
								"Job_Url"
								)
							li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px"],
								"#" + configBuildNumber
								)
							li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px; background-color:${color}"], 
								configResult
								)


							findDownstream(configJob, rootRun, li)
							findSubJobs(configJob, rootRun, li)
							findSubJobs(configJob, li)
						}
					}
				}

			}

		}
		else{
			findDownstream(rootProject, rootRun, node)
			findSubJobs(rootProject, rootRun, node)
			findSubJobs(rootRun, node)
		}







		writer = new FileWriter( workspace + "\\jes.html")
		XmlUtil.serialize(rootNode, writer)


	}





	findProjectsTree(upstreamCause, entryProject, theBuild)






