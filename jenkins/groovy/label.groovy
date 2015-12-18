import jenkins.model.*
import hudson.model.*
  

// Modify all labels
  
/*
	def items = hudson.model.Hudson.instance.items
	for(item in items)

*/
  Hudson.instance.items.each { 
    job ->
  	
  //job.setAssignedLabel(Jenkins.instance.getLabel('test'))
  
  job.setAssignedLabel(new hudson.model.labels.LabelAtom(Label_Name))
}

