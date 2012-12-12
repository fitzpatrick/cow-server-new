/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.convert;

import static org.wiredwidgets.cow.server.transform.v2.bpmn20.Bpmn20ProcessBuilder.PROCESS_INSTANCE_NAME_PROPERTY;

import javax.xml.datatype.XMLGregorianCalendar;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.wiredwidgets.cow.server.api.service.ProcessInstance;
import static org.drools.runtime.process.ProcessInstance.*;

/**
 *
 * @author FITZPATRICK
 */
public class JbpmProcessInstanceLogToSc2ProcessInstance extends AbstractConverter implements Converter<ProcessInstanceLog, ProcessInstance>{
	
    @Autowired
    protected StatefulKnowledgeSession kSession;	

    @Override
    public ProcessInstance convert(ProcessInstanceLog source) {
        ProcessInstance target = new ProcessInstance();
        target.setProcessDefinitionId(source.getProcessId());
        target.setKey(source.getProcessId());

        // for compatibility with REST API, preserve the JBPM 4.x convention
        // where the process instance ID = processID + "." + id
        target.setId(source.getProcessId() + "." + Long.toString(source.getProcessInstanceId()));
        target.setStartTime(this.getConverter().convert(source.getStart(), XMLGregorianCalendar.class));
        target.setEndTime(this.getConverter().convert(source.getEnd(), XMLGregorianCalendar.class));
        
        switch (source.getStatus()) {
        	case STATE_ABORTED :
        		target.setState("aborted");
        		break;
        	case STATE_ACTIVE :
        		target.setState("active");
        		break;
        	case STATE_COMPLETED :
        		target.setState("completed");
        		break;
        	case STATE_PENDING :
        		target.setState("pending");
        		break;
        	case STATE_SUSPENDED :
        		target.setState("suspended");
        		break;
        }
        
        // process instance name
        WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession.getProcessInstance(source.getProcessInstanceId());
        if (pi != null){
            String processName = (String) pi.getVariable(PROCESS_INSTANCE_NAME_PROPERTY);
            target.setName(processName);
        }
                     
        return target;
    }
    
}
