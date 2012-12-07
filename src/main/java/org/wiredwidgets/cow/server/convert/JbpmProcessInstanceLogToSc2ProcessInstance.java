/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.convert;

import static org.wiredwidgets.cow.server.transform.v2.bpmn20.Bpmn20ProcessBuilder.VARIABLES_PROPERTY;
import static org.wiredwidgets.cow.server.transform.v2.bpmn20.Bpmn20ProcessBuilder.PROCESS_INSTANCE_NAME_PROPERTY;

import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.wiredwidgets.cow.server.api.service.ProcessInstance;

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
        
        // process instance name
        WorkflowProcessInstance pi = (WorkflowProcessInstance) kSession.getProcessInstance(source.getProcessInstanceId());
        if (pi != null){
            String processName = (String) pi.getVariable(PROCESS_INSTANCE_NAME_PROPERTY);
            target.setName(processName);
        }
                     
        return target;
    }
    
}
