/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.convert;

import java.util.Arrays;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.jbpm.task.Content;
import org.jbpm.task.TaskData;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.wiredwidgets.cow.server.api.service.Task;
import org.wiredwidgets.cow.server.api.service.Variable;
import org.wiredwidgets.cow.server.api.service.Variables;

/**
 *
 * @author FITZPATRICK
 */
public class JbpmTaskToSc2Task extends AbstractConverter implements Converter<org.jbpm.task.Task, Task> {

    // NOTE: Autowiring does not work here!
    @Autowired
    org.jbpm.task.TaskService taskClient;
    
    @Autowired
    MinaHTWorkItemHandler minaWorkItemHandler;
    
    private static Logger log = Logger.getLogger(JbpmTaskSummaryToSc2Task.class);
    
    @Override
    public Task convert(org.jbpm.task.Task s) {
        Task target = new Task();

        if (s == null){
            return null;
        }
        TaskData source = s.getTaskData();
        
        //target.setDescription(source.getDescriptions().get(0).getText());

        if (source != null && source.getActualOwner() != null){
            target.setAssignee(source.getActualOwner().getId());
        }

        if (source != null && source.getCreatedOn() != null) {
            target.setCreateTime(this.getConverter().convert(source.getCreatedOn(), XMLGregorianCalendar.class));
        }

        if (source != null && source.getExpirationTime() != null) {
            target.setDueDate(this.getConverter().convert(source.getExpirationTime(), XMLGregorianCalendar.class));
        }
        
        target.setId(String.valueOf(s.getId()));
        target.setPriority(new Integer(s.getPriority()));
        
        // add task outcomes using the "Options" variable from the task
        org.jbpm.task.Task task = taskClient.getTask(s.getId());        
        Content content = taskClient.getContent(task.getTaskData().getDocumentContentId());      
        Map<String, Object> map = (Map<String, Object>) ContentMarshallerHelper.unmarshall(
        		"org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy", 
        		content.getContent(), 
        		minaWorkItemHandler.getMarshallerContext(), null);  
        
        if (map.containsKey("Options")){
            String[] options = ( (String) map.get("Options") ).split(",");
            target.getOutcomes().addAll(Arrays.asList(options));
        }
        
        // get ad-hoc variables from the "Content" map
        if (map.containsKey("Content")){
            Map<String, Object> contentMap = (Map<String, Object>) map.get("Content");
            if (contentMap != null) {
                    for (Map.Entry<String, Object> entry : contentMap.entrySet()) {
                            addVariable(target, entry.getKey(), entry.getValue());
                    }
            }
        }
        
        return target;
    }
    
    private void addVariable(Task task, String key, Object value) {
        Variable var = new Variable();
        var.setName(key);
        // Support strings only.  Other types will cause ClassCastException
        try {
            var.setValue((String)value);
        } catch (ClassCastException e) {
            var.setValue("Variable type " + value.getClass().getName() + " is not supported");
        }    	
        addVariable(task, var);
    }

    private void addVariable(Task task, Variable var) {
        if (task.getVariables() == null) {
            task.setVariables(new Variables());
        }
        task.getVariables().getVariables().add(var);
    }
    
    /*
     * Fix for COW-132
     * Tasks in parallel structures may cause sub-executions  with IDs in the form key.number.number.number
     * In this case we only want to look at the top level process execution.  
     */
    private String getTopLevelExecutionId(String executionId) {
        String[] parts = executionId.split("\\.");
        return parts[0] + "." + parts[1];
    }

}