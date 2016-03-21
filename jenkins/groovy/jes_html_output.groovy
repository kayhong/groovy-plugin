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


		def getColor(def result){

			switch(result){
				case "SUCCESS" :
				return "green"
				case "FAILURE" :
				return "red"
				case "in Progress" : 
				return "rgb(0,240,230)"
				default :
				return "orange"
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



	//method to find triggered SubJobs
	def findSubJobs(def project, def upBuild, def node){
		try {
			def actionsList = upBuild.getActions(BuildInfoExporterAction)
			def ul = null
			for(def i = 0; i < actionsList.size(); i++ ){
				def projectsList = actionsList.get(i).getTriggeredProjects()
				println "****************** below is triggered non-blocked sub projects"
				println projectsList

				for(def j = 0; j < projectsList.size(); j++){
					def subProject = projectsList.get(j)


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
							if(theBuild.isInProgress()){
								result = "in Progress"
							}
							def color = getColor(result)

							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							def li = ul.appendNode("li")
							li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], name)
							def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
							urlSpan.appendNode(
								"a",
								[href : url, target : "_blank"],
								url
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
											if(configBuild.isInProgress()){
												configResult = "in Progress"
											}
											color = getColor(configResult)

											if(!configRootTag)
											configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

											li = configRootTag.appendNode("li")
											li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], configJobName)
											urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
											urlSpan.appendNode(
												"a",
												[href : configBuildUrl, target : "_blank"],
												configBuildUrl
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
									if(theBuild.isInProgress()){
										result = "in Progress"
									}
									def color = getColor(result)

									if(!ul)
									ul = node.appendNode("ul", [style : "list-style-type:circle"])

									def li = ul.appendNode("li")

									li.appendNode(
										"span",
										[class: "colapse", style : "border-style: solid; border-width: 1px"],
										name
										)
									def urlSpan = li.appendNode(
										"span",
										[style : "border-style: solid; border-width: 1px"]
										)
									urlSpan.appendNode(
										"a",
										[href : url, target : "_blank"],
										url
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
					else{
						
                          node.span[3].replaceNode({
                          new groovy.util.Node(node,
								"span",
								[style : "border-style: solid; border-width: 1px; background-color:rgb(0,240,230)"],
								"in Progress")		
                          })
                      	
						
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
			def ul = null
			for(def i = 0; i < actionsList.size(); i++ ){
				println "** these are blocked sub Jobs**"
				def buildsList = actionsList.get(i).getTriggeredBuilds()

				for(def j = 0; j < buildsList.size(); j++){

					try{
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
							def color = getColor(result)

							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							def li = ul.appendNode("li")
							li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], name)
							def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
							urlSpan.appendNode(
								"a",
								[href : url, target : "_blank"],
								url
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
											if(configBuild.isInProgress()){
												configResult = "in Progress"
											}
											color = getColor(configResult)

											if(!configRootTag)
											configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

											li = configRootTag.appendNode("li")
											li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], configJobName)
											urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
											urlSpan.appendNode(
												"a",
												[href : configBuildUrl, target : "_blank"],
												configBuildUrl
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
							if(buildsList.get(j).isInProgress()){
								result = "in Progress"
							}
							def color = getColor(result)

							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							def li = ul.appendNode("li")
							li.appendNode(
								"span",
								[class: "colapse", style : "border-style: solid; border-width: 1px"],
								name
								)
							def urlSpan = li.appendNode(
								"span",
								[style : "border-style: solid; border-width: 1px"]
								)
							urlSpan.appendNode(
								"a",
								[href : url, target : "_blank"],
								url
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
						if(theBuild.isInProgress()){
							result = "in Progress"
						}
						def color = getColor(result)

						if(!ul)
						ul = node.appendNode("ul", [style : "list-style-type:circle"])

						def li = ul.appendNode("li")
						li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], name)
						def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
						urlSpan.appendNode(
							"a",
							[href : url, target : "_blank"],
							url
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
										if(configBuild.isInProgress()){
											configResult = "in Progress"
										}
										color = getColor(configResult)

										if(!configRootTag)
										configRootTag = li.appendNode("ul", [style : "list-style-type:circle"])

										li = configRootTag.appendNode("li")
										li.appendNode("span", [class: "colapse", style : "border-style: solid; border-width: 1px"], 
											configJobName)
										urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
										urlSpan.appendNode(
											"a",
											[href : configBuildUrl, target : "_blank"],
											configBuildUrl
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
								if(theBuild.isInProgress()){
									result = "in Progress"
								}
								def color = getColor(result)

								if(!ul)
								ul = node.appendNode("ul", [style : "list-style-type:circle"])

								def li = ul.appendNode("li")
								li.appendNode(
									"span",
									[class: "colapse", style : "border-style: solid; border-width: 1px"],
									name
									)
								def urlSpan = li.appendNode(
									"span",
									[style : "border-style: solid; border-width: 1px"]
									)
								urlSpan.appendNode(
									"a",
									[href : url, target : "_blank"],
									url
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
				else{
							
                  node.span[3].replaceNode({
                    new groovy.util.Node(null,
								"span",
								[style : "border-style: solid; border-width: 1px; background-color:blue"],
								"in Progress")	
                  	
                  }) 
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
			fp = new FilePath(channel, outputFileFolder + "/jes.html")

		}
		else{
			fp = new FilePath(new File(outputFileFolder + "/jes.html"))
		}

		fp.write("", null)



		def writer = new StringWriter()
		def html = new MarkupBuilder(writer)



		def name = rootProject.name
		def url = rootRun.properties.get("envVars")["BUILD_URL"].toString()
		def number = rootRun.number
		def result = rootRun.result.toString()
		if(rootRun.isInProgress()){
			result = "in Progress"
		}
		def color = getColor(result)
		html.html{
			head{				
				script(src : "https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js", "js")
				script(type : "text/javascript", ''' 
					window.onload = function() {
						$('.job_line ul').hide();
						$('.job_parent').find('ul').slideToggle();

						$('.colapse').click(function() {
							$(this).parent().find('ul').slideToggle();
							});
							}''')
				style(type : "text/css", '''
					.job_parent {
						margin-left: 1em;
						font-size: large;
						font-weight: bold;
						cursor: pointer;
					}
					.job_line {
						margin-left: 1em;
						margin-top: 0;
						margin-bottom: 1em;
						list-style-type: circle;
						font-size: medium;
						cursor: pointer;
					}

					''')

			}
		}
		def parser = new XmlParser()
		def rootNode = parser.parseText(writer.toString())

		def bodyNode = rootNode.appendNode("body")
		def div = bodyNode.appendNode("div",[style:"margin:auto"])
		div.appendNode("a", 
			[href:"http://dewdflhana1265.emea.global.corp.sap:8080/job/JES/lastCompletedBuild/rebuild/parameterized", 
			target: "_blank", style: "font-size: 30px; background-color:hsla(120,100%,25%,0.3); margin-left:40%"],
			"ReBuild")
		def ulNode = bodyNode.appendNode("ul",[class : "job_parent", style : "list-style-type:circle"])
		def li = ulNode.appendNode("li")
		li.appendNode("span", [class : "colapse", style : "border-style: solid; border-width: 1px"], name)
		def spanNode = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
		spanNode.appendNode("a",[href: url, target: "_blank"], url)
		li.appendNode("span", [style : "border-style: solid; border-width: 1px"],"#" + number)
		li.appendNode("span", [style : "border-style: solid; border-width: 1px; background-color:${color}"], result)





		fp.write(writer.toString(), null)

		

		def node = rootNode.body.ul.li[0]


		if(rootProject instanceof MatrixProject){
			println  "is Matrix"
			findDownstream(rootProject, rootRun, node)

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
							def configBuild = thisBuild

							def configBuildNumber = configBuild.number
							def configBuildUrl = configBuild.properties.get("envVars")["BUILD_URL"].toString()
							def configResult = configBuild.result.toString()
							if(configBuild.isInProgress()){
								configResult = "in Progress"
							}
							color = getColor(configResult)
							if(!ul)
							ul = node.appendNode("ul", [style : "list-style-type:circle"])

							li = ul.appendNode("li")
							li.appendNode("span", [class : "colapse", style : "border-style: solid; border-width: 1px"], configJobName)
							def urlSpan = li.appendNode("span", [style : "border-style: solid; border-width: 1px"])
							urlSpan.appendNode(
								"a",
								[href : configBuildUrl, target : "_blank"],
								configBuildUrl
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

		def content = XmlUtil.serialize(rootNode)
		fp.write(content, null)

	}
	try{
		def entryProject = Jenkins.instance.getItem(myJobName)
		def theBuild = entryProject.getBuildByNumber(myBuildNumber)
		def upstreamCause = theBuild.getCause(Cause.UpstreamCause)
		findProjectsTree(upstreamCause, entryProject, theBuild)

	}
	catch(NullPointerException e){
		println "************************************"
		println e
		println "No Such Job in Jenkins or No This build correspond thie build Number"
		println "************************************"

	}



