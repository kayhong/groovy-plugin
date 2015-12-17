import jenkins.model.*
import hudson.model.*


def previousNamePrefix="job"
def newNamePrefix="MyPrefix"
  
def names = Jenkins.instance.getJobNames()
  for(name in names){
    if(name.startsWith(previousNamePrefix)){
    	println "current name              = " + name
      	println "newNamePrefix             = " + newNamePrefix
        println "job will be renamed into  = " + newNamePrefix+name
      	Jenkins.instance.getItem(name).renameTo(newNamePrefix+name)             	     	
    }
  
  }
