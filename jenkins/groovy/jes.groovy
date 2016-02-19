    import jenkins.model.*
    import hudson.model.*
    import hudson.plugins.parameterizedtrigger.BuildInfoExporterAction;
    import java.io.*
    import groovy.xml.*
    import groovy.util.*



    def entryProject = Jenkins.instance.getItem("JobParent")
    def theBuild = entryProject.getBuildByNumber(121)
    def upstreamCause = theBuild.getCause(Cause.UpstreamCause)
    def build = Thread.currentThread().executable

    workspace = build.workspace.toString()

    def isTheSameTime(def project, def upBuild){
    	def runMap = project.builds
    	
    	b = false
    	for(i = 0; i < runMap.size(); i++){
    		def cause = runMap.get(i).getCause(Cause.UpstreamCause)
    		if(cause){
    			if(cause.getUpstreamRun() == upBuild){
    				thisBuild = runMap.get(i)
    				b = true				
    			}
    		}
    	}
    }




    //method to find triggered SubJobs 
    def findSubJobs(def project, def upBuild, def node){

    	def actionsList = upBuild.getActions(BuildInfoExporterAction)
    	for(def i = 0; i < actionsList.size(); i++ ){      

        def projectsList = actionsList.get(i).getTriggeredProjects() 

        for(def j = 0; j < projectsList.size(); j++){
         def subProject = projectsList.get(j)
         if(subProject.builds){
          isTheSameTime(subProject, upBuild)
          if(b){
           upBuild = thisBuild  					
         }
       }
       def name = subProject.name
       def url = upBuild.getUrl()
       def number = upBuild.number
       def result = upBuild.result.toString()

       def currentNode = node.appendNode(
         "subProject",
         [Job_Name: name, Build_Number: number, URL: url, Status: result],
         name)

       findSubJobs(subProject, upBuild, currentNode)
       findSubJobs(upBuild, node)
       findDownstream(subProject, upBuild, currentNode)

     }
    }
    }


    def findSubJobs(def upBuild, def node){
    	def actionsList = upBuild.getActions(BuildInfoExporterAction)
    	for(def i = 0; i < actionsList.size(); i++ ){
    		def buildsList = actionsList.get(i).getTriggeredBuilds()

    		for(def k = 0; k < buildsList.size(); k++){
         
         def subProject = buildsList.get(k).getProject()
         def name = subProject.name
         def url = buildsList.get(k).getUrl()
         def number = buildsList.get(k).number
         def result = buildsList.get(k).result.toString()
         
         def currentNode = node.appendNode(
           "subProject",
           [Job_Name: name, Build_Number: number, URL: url, Status: result],
           name)

         findSubJobs(subProject, buildsList.get(k), currentNode)
         findSubJobs(buildsList.get(k), currentNode)
         findDownstream(subProject, buildsList.get(k), currentNode)

       }

     }


    }

    //Method to find Downstream projects
    def findDownstream(def project, def build, def node){
    	
    	def downstreamList = project.getDownstreamProjects()

    	for(def n = 0; n < downstreamList.size(); n++){


    		def dproject = downstreamList.get(n)
    		if(dproject.builds){
         isTheSameTime(dproject, build)
         if(b){
          build = thisBuild      			
        }
      }

      def name = dproject.name
      def url = build.getUrl()
      def number = build.number
      def result = build.result.toString()

      def currentNode = node.appendNode(
       "DownstreamProject",
       [Job_Name: name, Build_Number: number, URL: url, Status: result],
       name)
      findDownstream(dproject, build, currentNode)
      findSubJobs(dproject, build, currentNode)
      findSubJobs(build, currentNode)
    }
    }


    def findProjectsTree(def cause, def rootProject, def theBuild){

    	//def rootprojectName = rootProject.getFullName()
    	if(cause){
    		for(; cause; ){				
    			rootRun = cause.getUpstreamRun()
          rootProject = rootRun.getProject()
          cause = cause.getUpstreamRun().getCause(Cause.UpstreamCause)
        }
      }
      else{
       rootRun = theBuild	
     }
     def file = new File(workspace + "\\jes.xml")
     def writer = new FileWriter(file)
     def xmlMarkup = new MarkupBuilder(writer)

     def name = rootProject.name
     def url = rootRun.getUrl()
     def number = rootRun.number
     def result = rootRun.result.toString()
     xmlMarkup.rootProject(Job_Name: name, Build_Number: number, URL: url, Status: result)

     def parser = new XmlParser()
     def node = parser.parse(file)

     
     
     

     findSubJobs(rootProject, rootRun, node)
     findSubJobs(rootRun, node)
     findDownstream(rootProject, rootRun, node)

     

     writer = new FileWriter(workspace + "\\jes.xml")
     XmlUtil.serialize(node, writer)

     
    }





    findProjectsTree(upstreamCause, entryProject, theBuild)






