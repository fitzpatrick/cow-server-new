/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.callback;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.task.service.UserGroupCallback;

/**
 *
 * @author FITZPATRICK
 */
public class DefaultUserGroupCallbackImpl implements UserGroupCallback{

    @Override
    public boolean existsUser(String userId) {
        return true;
    }

    @Override
    public boolean existsGroup(String groupId) {
        return true;
    }

    @Override
    public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
        if(groupIds != null) {
            List<String> retList = new ArrayList<String>(groupIds);
            // merge all groups
            if(allExistingGroupIds != null) {
                for(String grp : allExistingGroupIds) {
                    if(!retList.contains(grp)) {
                        retList.add(grp);
                    }
                }
            } 

            return retList;
        } else {
            // return empty list by default
            return new ArrayList<String>();
        }
    }
    
}
