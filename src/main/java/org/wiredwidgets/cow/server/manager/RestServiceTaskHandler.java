package org.wiredwidgets.cow.server.manager;

import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class RestServiceTaskHandler implements WorkItemHandler {
	
	private static Logger log = Logger.getLogger(RestServiceTaskHandler.class);

	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {	
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem item, WorkItemManager manager) {
		log.info("Work item: " + item.getName());
		
		for (Entry<String, Object> entry : item.getParameters().entrySet()) {
			log.info(entry.getKey() + ":" + entry.getValue());
		}
		
		
		manager.completeWorkItem(item.getId(), null);
		
		
	}

}
