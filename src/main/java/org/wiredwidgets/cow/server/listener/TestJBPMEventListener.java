/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.listener;

import org.apache.log4j.Logger;
import org.drools.event.process.*;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author FITZPATRICK
 */
public class TestJBPMEventListener implements ProcessEventListener{
	
	private static Logger log = Logger.getLogger(TestJBPMEventListener.class);
    
    @Override
    public void beforeProcessStarted(ProcessStartedEvent pse) {
        log.info("Process started: " + pse.getProcessInstance().getId());
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent pse) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent pce) {
    	// log.info("Process complete: " + pce.getProcessInstance().getId());
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent pce) {
        log.info(pce.getProcessInstance().getProcessName() + " COMPLETED!");
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent pnte) {
    	log.info("Node triggered: " + pnte.getNodeInstance().getNodeName());
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent pnte) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent pnle) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent pnle) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent pvce) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent pvce) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void getSessionProcessListener() {
        
    }

    public void setSessionProcessListener(StatefulKnowledgeSession kSession) {
        kSession.addEventListener(this);
    }
    
}
