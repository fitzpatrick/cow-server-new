package org.wiredwidgets.cow.server.manager;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.GenericHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;

public class WorkItemHandlerFactory {
		
	public static GenericHTWorkItemHandler createInstance(StatefulKnowledgeSession session) {
		GenericHTWorkItemHandler handler = new MinaHTWorkItemHandler(session);
		session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		return handler;
	}

}
