/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.manager;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 *
 * @author FITZPATRICK
 */
public class KnowledgeSessionFactory {
    @Autowired
    KnowledgeBase kBase;
    
    @Autowired
    EntityManagerFactory emf;
    
    @Autowired
    JpaTransactionManager txManager;
    
    @Autowired
    MinaHTWorkItemHandler minaWorkItemHandler;
    
    public StatefulKnowledgeSession createInstance(){
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        env.set(EnvironmentName.TRANSACTION_MANAGER, txManager);
        
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        
        StatefulKnowledgeSession kSession = JPAKnowledgeService.newStatefulKnowledgeSession(kBase, config, env);
        new JPAWorkingMemoryDbLogger(kSession);
        JPAProcessInstanceDbLog.setEnvironment(env);
        minaWorkItemHandler = new MinaHTWorkItemHandler(kSession);
        kSession.getWorkItemManager().registerWorkItemHandler("Human Task", minaWorkItemHandler);
        
        return kSession;
        /*
        
        System.out.println("KBASE IS " + kBase);
        System.out.println("EMF IS " + emf);
        
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
        kSession = JPAKnowledgeService.loadStatefulKnowledgeSession(4, kBase, null, env);*/
    }
}
