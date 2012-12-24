/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wiredwidgets.cow.server.api.service.Group;
import org.wiredwidgets.cow.server.api.service.User;

/**
 *
 * @author FITZPATRICK
 */
@Transactional
@Component
public class UsersServiceImpl extends AbstractCowServiceImpl implements UsersService {

    private static TypeDescriptor JBPM_USER_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(User.class));
    private static TypeDescriptor JBPM_GROUP_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Group.class));
    private static TypeDescriptor COW_USER_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.wiredwidgets.cow.server.api.service.User.class));
    private static TypeDescriptor COW_GROUP_LIST = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(org.wiredwidgets.cow.server.api.service.Group.class));

    
    @Override
    public String createGroup(String groupName) {
        return "Not supported yet.";
    }

    @Override
    public void createOrUpdateUser(User user) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteUser(String id) {
        return false;//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional(readOnly = true)
    @Override
    public List<Group> findAllGroups() {
        List<Group> groupList = new ArrayList<Group>();
        for (String group: groups){
            Group temp = new Group();
            temp.setId(group);
            temp.setName(group);
            
            groupList.add(temp);
        }
        return groupList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<User>();
        List<String> users = new ArrayList<String>(userGroups.keySet());
        for (String user: users){
            User temp = new User();
            temp.setId(user);
            userList.add(temp);
        }
        return userList;
    }

    @Transactional(readOnly = true)
    @Override
    public Group findGroup(String id) {
        if (groups.contains(id)){
            Group group = new Group();
            group.setId(id);
            group.setName(id);
            return group;
        } 
        
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public User findUser(String id) {
        return new User();//throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteGroup(String id) {
        return false;//throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
