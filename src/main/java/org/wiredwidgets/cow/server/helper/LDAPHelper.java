/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.helper;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.jbpm.task.identity.LDAPUserGroupCallbackImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wiredwidgets.cow.server.api.service.User;

/**
 *
 * @author FITZPATRICK
 */
public class LDAPHelper {

    private static Logger log = Logger.getLogger(LDAPHelper.class);
    
    @Value("${ldap.host}") String LDAP_HOST;
    @Value("${ldap.port}") int LDAP_PORT;
    @Value("${ldap.role.context}") String LDAP_ROLE_CONTEXT;
    @Value("${ldap.user.context}") String LDAP_USER_CONTEXT;
    @Value("${ldap.admin}") String LDAP_ADMIN;
    @Value("${ldap.admin.password}") String LDAP_ADMIN_PASSWORD;
    
    public List<String> getLDAPGroups() {
        List<String> ldapGroups = new ArrayList<String>();
        
        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT);

            String baseDN = LDAP_ROLE_CONTEXT;
            String filter = "(&(objectClass=organizationalRole))";

            SearchResult searchResult = null;

            if (lc.isConnected()) {
                searchResult = lc.search(baseDN, SearchScope.ONE, filter);
            }

            List<SearchResultEntry> results = searchResult.getSearchEntries();

            for (SearchResultEntry e : results) {
                ldapGroups.add(e.getAttributeValue("cn"));
            }

            lc.close();
        } catch (LDAPException e) {
            log.error(e);
        }

        return ldapGroups;
    }

    public List<String> getLDAPUsers() {
        List<String> ldapUsers = new ArrayList<String>();

        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT);

            String baseDN = LDAP_USER_CONTEXT;
            String filter = "(&(objectClass=inetOrgPerson))";

            SearchResult searchResult = null;

            if (lc.isConnected()) {
                searchResult = lc.search(baseDN, SearchScope.ONE, filter);
            }

            List<SearchResultEntry> results = searchResult.getSearchEntries();

            for (SearchResultEntry e : results) {
                ldapUsers.add(e.getAttributeValue("uid"));
            }
            
            lc.close();
        } catch (LDAPException e) {
            log.error(e);
        }

        return ldapUsers;
    }
    
   /* public List<String> getUsersGroups(String user) {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserGroupCallbackImpl.BIND_USER, LDAP_USER_CONTEXT);
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, LDAP_USER_CONTEXT);
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, LDAP_ROLE_CONTEXT);
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_CTX, LDAP_ROLE_CONTEXT);
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(roleOccupant={0})");
        //properties.setProperty("ldap.user.id.dn", "true");
        properties.setProperty("java.naming.provider.url", "ldap://" + LDAP_HOST + ":" + Integer.toString(LDAP_PORT) + "/");

        LDAPUserGroupCallbackImpl ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);
        
        return ldapUserGroupCallback.getGroupsForUser(user, null, null);
    }*/
    
    public String createGroup(String groupName){
        String[] ldifLines =
            {
                    "dn: cn=" + groupName + "," + LDAP_ROLE_CONTEXT,
                    "objectclass: organizationalRole",
                    "cn: " + groupName
            };
        
        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT, LDAP_ADMIN, LDAP_ADMIN_PASSWORD);

            if (lc.isConnected()) {
                lc.add(new AddRequest(ldifLines));
            }

            lc.close();
            return groupName + " successfully created.";
        } catch (LDAPException e) {
           log.error(e);
        } catch (LDIFException e){
            log.error(e);
        }

        return groupName + " creation unsuccessful.";
    }
    
    public void createUser(User user){
        final String[] ldifLines =
            {
                    "dn: uid=" + user.getId() + "," + LDAP_USER_CONTEXT,
                    "objectclass: inetOrgPerson",
                    "cn: " + user.getFirstName() + " " + user.getLastName(),
                    "sn: " + user.getId(),
                    "uid: " + user.getId(),
                    "mail: " + user.getEmail()
            };
        
        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT, LDAP_ADMIN, LDAP_ADMIN_PASSWORD);

            if (lc.isConnected()) {
                lc.add(new AddRequest(ldifLines));
            }

            lc.close();
        } catch (LDAPException e) {
            log.error(e);
        } catch (LDIFException e){
            log.error(e);
        }
    }
    
    public boolean deleteGroup(String groupName){
        
        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT, LDAP_ADMIN, LDAP_ADMIN_PASSWORD);

            if (lc.isConnected()) {
                lc.delete(new DeleteRequest("cn=" + groupName + "," + LDAP_ROLE_CONTEXT));
            }

            lc.close();
            return true;
        } catch (LDAPException e) {
           log.error(e);
        }

        return false;
    }
    
    public boolean deleteUser(String userId){
        
        try {
            LDAPConnection lc = new LDAPConnection(LDAP_HOST, LDAP_PORT, LDAP_ADMIN, LDAP_ADMIN_PASSWORD);

            if (lc.isConnected()) {
                lc.delete(new DeleteRequest("uid=" + userId + "," + LDAP_USER_CONTEXT));
            }

            lc.close();
            return true;
        } catch (LDAPException e) {
           log.error(e);
        }

        return false;
    }
}
