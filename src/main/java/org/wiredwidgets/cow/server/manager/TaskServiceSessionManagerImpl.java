/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author FITZPATRICK
 */
@Transactional
public class TaskServiceSessionManagerImpl implements TaskServiceSessionManager {

    TaskService jbpmTaskService;
    TaskServiceSession jbpmTaskServiceSession;
    HashMap userGroups;
    List groups;

    @Override
    public void init() {
        jbpmTaskServiceSession = jbpmTaskService.createSession();
        addUserGroupToSession();
    }

    @Override
    public TaskService getjbpmTaskService() {
        return jbpmTaskService;
    }

    @Override
    public void setjbpmTaskService(TaskService jbpmTaskService) {
        this.jbpmTaskService = jbpmTaskService;
    }

    @Override
    public HashMap getuserGroups() {
        return userGroups;
    }

    @Override
    public void setuserGroups(HashMap userGroups) {
        this.userGroups = userGroups;
    }
    
    @Override
    public List getgroups(){
        return groups;
    }
    
    @Override
    public void setgroups(List groups){
        this.groups = groups;
    }

    private void addUserGroupToSession() {
        List<String> allGroups = getAllGroups();
        for (String group : allGroups) {
            jbpmTaskServiceSession.addGroup(new Group(group));
            groups.add(group);
        }

        for (String username : getDefaultUsers()) {
            jbpmTaskServiceSession.addUser(new User(username));
            if (username != "Administrator") {

                List<String> groupsForUser = getUserGroups(username);
                for (String group : groupsForUser) {
                    if (userGroups.containsKey(username)) {
                        ((List) userGroups.get(username)).add(group);
                    } else {
                        List<String> values = new ArrayList<String>();
                        values.add(group);
                        userGroups.put(username, values);
                    }
                }
            }
        }
    }

    private String[] getDefaultUsers() {
        return new String[]{"shawn", "lew", "Administrator"};
    }

    private List<String> getUserGroups(String username) {
        if (!username.equals("Administrator")) {
            List<String> groupsForUser = new ArrayList<String>();
            groupsForUser.add("group1");
            //groupsForUser.add("group2");
            return groupsForUser;
        } /*
         * else if (username.equals("Administrator")){ List<String> groups = new
         * ArrayList<String>(); groups.add("admin"); return groups;
        }
         */
        return null;
    }

    private List<String> getAllGroups() {
        List<String> allGroups = new ArrayList<String>();
        allGroups.add("group1");
        /*
         * groups.add("group2");
        groups.add("admin");
         */
        return allGroups;
    }
}
