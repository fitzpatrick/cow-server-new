package org.wiredwidgets.cow.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * Loads all processes at system startup.
 * Note we cannot just use an init method in ProcessServiceImpl, as this would
 * bypass the transactional proxy.  Since we invoke the method here using
 * the interface, it will be properly transactional.
 * @author JKRANES
 *
 */
public class ProcessLoader {
	
	@Autowired
	ProcessService service;
	
	public void init() {
		
		// NOTE: this turned out to be problematic, as loading processes requires all of the 
		// BPMN code generation infrastructure to be fully initialized.  
		
		// For now, we will load all processes the first time we call the server
		// to execute a process.  Later we might want to make this into some kind of
		// scheduled task that runs after all of the Spring setup is complete	
		
		// service.loadAllProcesses();
	}

}
