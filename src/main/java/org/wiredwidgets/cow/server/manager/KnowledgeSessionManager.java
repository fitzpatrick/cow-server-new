/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.manager;

import javax.persistence.EntityManagerFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.GenericCommandBasedWSHumanTaskHandler;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientConnector;
import org.jbpm.task.service.mina.AsyncMinaTaskClient;
import org.jbpm.task.utils.ContentMarshallerContext;

/**
 *
 * @author FITZPATRICK
 */
public class KnowledgeSessionManager {
    StatefulKnowledgeSession kSession;
    
    public void init() {
        
    }
    
    public StatefulKnowledgeSession getkSession() {
        return kSession;
    }
    
    public void setkSession(StatefulKnowledgeSession kSession) {
        this.kSession = kSession;
    }
}
