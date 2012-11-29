/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.manager;

import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.wiredwidgets.cow.server.callback.DefaultUserGroupCallbackImpl;

/**
 *
 * @author FITZPATRICK
 */
public class MinaTaskServerManager {
    MinaTaskServer minaTaskServer;
    DefaultUserGroupCallbackImpl userGroupCallback;
    
    public void init() {
        UserGroupCallbackManager.getInstance().setCallback(userGroupCallback);
        Thread thread = new Thread(minaTaskServer);
        thread.start();
    }
    
    public void getMinaTaskServer() {
        
    }

    public void setMinaTaskServer(MinaTaskServer minaTaskServer) {
        this.minaTaskServer = minaTaskServer;
    }
    
    public DefaultUserGroupCallbackImpl getUserGroupCallback(){
        return userGroupCallback;
    }
    
    public void setUserGroupCallback(DefaultUserGroupCallbackImpl userGroupCallback){
        this.userGroupCallback = userGroupCallback;
    }
}
