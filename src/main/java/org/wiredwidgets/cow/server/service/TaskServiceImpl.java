/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.task.Content;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.api.service.HistoryActivity;
import org.wiredwidgets.cow.server.api.service.HistoryTask;
import org.wiredwidgets.cow.server.api.service.Participation;
import org.wiredwidgets.cow.server.api.service.Task;
import static org.wiredwidgets.cow.server.transform.v2.bpmn20.Bpmn20UserTaskNodeBuilder.*;
import static org.wiredwidgets.cow.server.transform.v2.bpmn20.Bpmn20DecisionUserTaskNodeBuilder.DECISION_VAR_NAME;

/**
 *
 * @author FITZPATRICK
 */
@Transactional
@Component
public class TaskServiceImpl extends AbstractCowServiceImpl implements TaskService {
	
	private static Logger log = Logger.getLogger(TaskServiceImpl.class);

    //private static TypeDescriptor JBPM_PARTICIPATION_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.api.task.Participation.class));
    private static TypeDescriptor COW_PARTICIPATION_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Participation.class));
    private static TypeDescriptor JBPM_TASK_SUMMARY_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.task.query.TaskSummary.class));
    private static TypeDescriptor JBPM_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.task.Task.class));
    private static TypeDescriptor COW_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Task.class));
    //private static TypeDescriptor JBPM_HISTORY_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.api.history.HistoryTask.class));
    private static TypeDescriptor COW_HISTORY_TASK_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(HistoryTask.class));
    //private static TypeDescriptor JBPM_HISTORY_ACTIVITY_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.jbpm.api.history.HistoryActivityInstance.class));
    private static TypeDescriptor COW_HISTORY_ACTIVITY_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(HistoryActivity.class));

    @Transactional(readOnly = true)
    @Override
    public List<Task> findPersonalTasks(String assignee) {
        List<TaskSummary> tempTasks = new ArrayList<TaskSummary>();
        List<TaskSummary> tasks = new ArrayList<TaskSummary>();
        List<String> groupsForUser = userGroups.get(assignee);
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Reserved);
        tempTasks.addAll(taskClient.getTasksAssignedAsPotentialOwnerByStatusByGroup(assignee, groupsForUser, status, "en-UK"));
        
        for (TaskSummary task : tempTasks){
            User u = task.getActualOwner();
            if (u.getId().equals(assignee)){
                tasks.add(task);
            }
        }
        
        return this.convertTaskSummarys(tasks);
    }

    @Override
    public String createAdHocTask(Task task) {
        org.jbpm.task.Task newTask = this.createOrUpdateTask(task);
        return Long.toString(newTask.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasks() {
        List<org.jbpm.task.Task> tasks = (List<org.jbpm.task.Task>)taskClient.query("select t from Task t where t.taskData.status in ('Created', 'Ready', 'Reserved', 'InProgress')", Integer.MAX_VALUE,0);
        return this.convertTasks(tasks);
    }

    @Transactional(readOnly = true)
    @Override
    public Task getTask(Long id) {
        org.jbpm.task.Task task = taskClient.getTask(id);
        return this.converter.convert(task, Task.class);
    }

    @Transactional(readOnly = true)
    @Override
    public HistoryTask getHistoryTask(String id) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void completeTask(Long id, String assignee, String outcome, Map<String, Object> variables) {
        log.debug(assignee + " starting task with ID: " + id);
        
        taskClient.start(id, assignee);
        org.jbpm.task.Task task = taskClient.getTask(id);
        Content inputContent = taskClient.getContent(task.getTaskData().getDocumentContentId());        
        Map<String, Object> inputMap = (Map<String, Object>) ContentMarshallerHelper.unmarshall(SerializablePlaceholderResolverStrategy.class.getName(), inputContent.getContent(), minaWorkItemHandler.getMarshallerContext(), null);
        
        for (Map.Entry<String, Object> entry : inputMap.entrySet()){
            log.debug(entry.getKey() + " = " + entry.getValue());
        }
        
        Map<String, Object> outputMap = new HashMap<String, Object>();
        
        // put Outcome into the outputMap 
        // The InputMap contains a variable that tells us what key to use
        if (inputMap.get(DECISION_VAR_NAME) != null) {
        	outputMap.put((String)inputMap.get(DECISION_VAR_NAME), outcome);
        }        
               
        Map<String, Object> outputVarsMap = new HashMap<String, Object>();       
        Map<String, Object> inputVarsMap = (Map<String, Object>) inputMap.get(TASK_INPUT_VARIABLES_NAME);
        
        if (inputVarsMap != null) {
        	// initialize the output map with the input values
        	log.debug("Copying input map: " + inputVarsMap);
        	outputVarsMap.putAll(inputVarsMap);
        }
                   
        if (variables != null && variables.size() > 0) {
        	log.debug("Adding variables: " + variables);
        	// update with any new or modified values
        	outputVarsMap.putAll(variables);
    	}
        
        if (outputVarsMap.size() > 0) {
        	log.debug("Adding map to output");
        	outputMap.put(TASK_OUTPUT_VARIABLES_NAME, outputVarsMap);   
        }
        
        ContentData contentData = ContentMarshallerHelper.marshal(outputMap, minaWorkItemHandler.getMarshallerContext(), null);      
        taskClient.complete(id, assignee, contentData);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllUnassignedTasks() {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Created); 
        status.add(Status.InProgress);
        status.add(Status.Ready);
        
        List<TaskSummary> tasks = taskClient.getTasksAssignedAsPotentialOwnerByStatusByGroup("Administrator", groups, status, "en-UK");
        
        return this.convertTaskSummarys(tasks);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findGroupTasks(String user) {
        // Need to add user groups to this
        List<String> groupsForUser = userGroups.get(user);
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Created); 
        status.add(Status.InProgress);
        status.add(Status.Ready);
        
        List<TaskSummary> tasks = taskClient.getTasksAssignedAsPotentialOwnerByStatusByGroup(user, groupsForUser, status, "en-UK");
        
        return this.convertTaskSummarys(tasks);
    }

    @Override
    public void takeTask(Long taskId, String userId) {
        List<String> groupsForUser = (List<String>) userGroups.get(userId);
        taskClient.claim(taskId, userId, groupsForUser);
    }
    
    @Override
    public void updateTask(Task task) {
        this.createOrUpdateTask(task);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasksByProcessInstance(Long id) {

        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAllTasksByProcessKey(Long id) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTaskParticipatingGroup(Long taskId, String groupId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTaskParticipatingUser(Long taskId, String userId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Participation> getTaskParticipations(Long taskId) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskParticipatingGroup(Long taskId, String groupId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskParticipatingUser(Long taskId, String userId, String type) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTaskAssignment(Long taskId) {

        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryTask> getHistoryTasks(String assignee, Date startDate, Date endDate) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryTask> getHistoryTasks(String processId) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<HistoryActivity> getHistoryActivities(String processInstanceId) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findOrphanedTasks() {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public Activity getWorkflowActivity(String processInstanceId, String key) {
        return null;//throw new UnsupportedOperationException("Not supported yet.");
    }

    private Date convert(XMLGregorianCalendar source) {
        return source.toGregorianCalendar().getTime();
    }

    /*
     * private List<Participation>
     * convertParticipations(List<org.jbpm.api.task.Participation> source) {
     * return (List<Participation>) converter.convert(source,
     * JBPM_PARTICIPATION_LIST, COW_PARTICIPATION_LIST); }
     */
    private List<Task> convertTaskSummarys(List<org.jbpm.task.query.TaskSummary> source) {
        return (List<Task>) converter.convert(source, JBPM_TASK_SUMMARY_LIST, COW_TASK_LIST);
    }
    
    private List<Task> convertTasks(List<org.jbpm.task.Task> source) {
        return (List<Task>) converter.convert(source, JBPM_TASK_LIST, COW_TASK_LIST);
    }

    /*
     * private List<HistoryTask>
     * convertHistoryTasks(List<org.jbpm.api.history.HistoryTask> source) {
     * return (List<HistoryTask>) this.converter.convert(source,
     * JBPM_HISTORY_TASK_LIST, COW_HISTORY_TASK_LIST); }
     *
     * private List<HistoryActivity>
     * convertHistoryActivities(List<org.jbpm.api.history.HistoryActivityInstance>
     * source) { return (List<HistoryActivity>) this.converter.convert(source,
     * JBPM_HISTORY_ACTIVITY_LIST, COW_HISTORY_ACTIVITY_LIST); }
     */
    //TODO: Check if you can update a task. Can you update task by just adding a task with the same ID?
    private org.jbpm.task.Task createOrUpdateTask(Task source) {

        org.jbpm.task.Task target;
        boolean newTask = false;
        if (source.getId() == null) {
            newTask = true;
            target = new org.jbpm.task.Task();
        } else {
            target = taskClient.getTask(Long.valueOf(source.getId()));
        }
        if (target == null) {
            return null;
        }
        if (source.getAssignee() != null) {
            PeopleAssignments pa = new PeopleAssignments();
            List<OrganizationalEntity> orgEnt = new ArrayList<OrganizationalEntity>();
            org.jbpm.task.User oe = new org.jbpm.task.User();
            oe.setId(source.getAssignee());
            pa.setTaskInitiator(oe);
            orgEnt.add(oe);
            pa.setPotentialOwners(orgEnt);
            target.setPeopleAssignments(pa);

        }
        if (source.getDescription() != null) {
            List<I18NText> desc = new ArrayList<I18NText>();
            desc.add(new I18NText("en-UK", source.getDescription()));
            target.setDescriptions(desc);
        }
        if (source.getDueDate() != null) {
            Deadlines deadlines = new Deadlines();
            List<Deadline> dls = new ArrayList<Deadline>();
            Deadline dl = new Deadline();
            dl.setDate(this.convert(source.getDueDate()));
            dls.add(dl);
            deadlines.setEndDeadlines(dls);
            target.setDeadlines(deadlines);
        }
        if (source.getName() != null) {
            List<I18NText> names = new ArrayList<I18NText>();
            names.add(new I18NText("en-UK", source.getName()));
            target.setNames(names);
        }
        if (source.getPriority() != null) {
            target.setPriority(source.getPriority());
        }

        TaskData td = new TaskData();
        target.setTaskData(td);
        /*
         * if (source.getProgress() != null) {
         * target.setProgress(source.getProgress()); }
         */

        // convert variables
        /*
         * if (source.getVariables() != null &&
         * source.getVariables().getVariables().size() > 0) { Map<String,
         * Object> variables = new HashMap<String, Object>(); for (Variable
         * variable : source.getVariables().getVariables()) {
         * variables.put(variable.getName(), variable.getValue()); }
         * this.taskService.setVariables(target.getId(), variables); }
         */

        if (newTask) {
            taskClient.addTask(target, null);
        }

        return target;
    }
}
