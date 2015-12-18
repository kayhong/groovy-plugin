// this groovy creates 5 jobs called Old_jobN

import jenkins.model.*
import hudson.model.*

  
  //http://javadoc.jenkins-ci.org/hudson/model/FreeStyleProject.html    for Freestyleproject Class
def type = FreeStyleProject.DESCRIPTOR


  //a loop like **for(int i=1, i<5, i++)**in java
  for(i in 1..<5){
    
    def name = 'Old_job' + i	
     
    // http://javadoc.jenkins-ci.org/jenkins/model/Jenkins.html#createProject(hudson.model.TopLevelItemDescriptor, java.lang.String, boolean) ** the link for api createproject fonction
    
    
    //Jenkins.instance.createProject(type , name)   .instance to call the method getInstance.
    Jenkins.instance.createProject(type , name,true)
  }










  