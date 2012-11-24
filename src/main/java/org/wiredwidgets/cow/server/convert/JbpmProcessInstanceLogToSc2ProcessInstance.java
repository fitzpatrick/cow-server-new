/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.convert;

import javax.xml.datatype.XMLGregorianCalendar;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.springframework.core.convert.converter.Converter;
import org.wiredwidgets.cow.server.api.service.ProcessInstance;

/**
 *
 * @author FITZPATRICK
 */
public class JbpmProcessInstanceLogToSc2ProcessInstance extends AbstractConverter implements Converter<ProcessInstanceLog, ProcessInstance>{

    @Override
    public ProcessInstance convert(ProcessInstanceLog source) {
        ProcessInstance target = new ProcessInstance();
        target.setProcessDefinitionId(source.getProcessId());
        target.setKey(source.getProcessId());
        target.setName(source.getProcessId());
        target.setId(Long.toString(source.getProcessInstanceId()));
        target.setStartTime(this.getConverter().convert(source.getStart(), XMLGregorianCalendar.class));
        target.setEndTime(this.getConverter().convert(source.getEnd(), XMLGregorianCalendar.class));
        
        return target;
    }
    
}
