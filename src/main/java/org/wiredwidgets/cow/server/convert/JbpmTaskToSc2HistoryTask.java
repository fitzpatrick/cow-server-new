/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.convert;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jbpm.task.TaskData;
import org.springframework.core.convert.converter.Converter;
import org.wiredwidgets.cow.server.api.service.HistoryTask;

/**
 *
 * @author FITZPATRICK
 */
public class JbpmTaskToSc2HistoryTask extends AbstractConverter implements Converter<org.jbpm.task.Task, HistoryTask> {
 
    // private static Logger log = Logger.getLogger(JbpmTaskToSc2HistoryTask.class);
    
    @Override
    public HistoryTask convert(org.jbpm.task.Task s) {
        HistoryTask target = new HistoryTask();

        if (s == null){
            return null;
        }
        
        target.setId(String.valueOf(s.getId()));  
        
        TaskData td = s.getTaskData();
        if (td != null) {
        	if (td.getActualOwner() != null) {
        		target.setAssignee(td.getActualOwner().getId());
        	}
	        target.setCreateTime(convert(td.getCreatedOn()));
            target.setEndTime(convert(td.getCompletedOn()));
            target.setExecutionId(td.getProcessId() + "." + String.valueOf(td.getProcessInstanceId()));
        }    
        return target;
    }
   
    private XMLGregorianCalendar convert(Date date) {
    	return getConverter().convert(date, XMLGregorianCalendar.class);
    }
    
}
