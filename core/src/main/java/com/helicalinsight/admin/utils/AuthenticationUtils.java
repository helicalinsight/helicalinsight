/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consists of a set of utility methods used by various rules related classes
 *
 * @author Rajasekhar
 */
public final class AuthenticationUtils {

    public static String getUserId() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Principal activeUser;
        if (principal instanceof Principal) {
            activeUser = (Principal) principal;
        } else {
            throw new EfwException("User session has expired. Please login.");
        }
        return String.valueOf(activeUser.getLoggedInUser().getId());
    }

    /**
     * Obtains the currently logged in <code>User</code>'s <code>Role</code> ids
     * from the <code>Principal</code>
     *
     * @return Returns a list of <code>Role</code> ids held by the currently logged
     * in user
     */
    public static List<String> getUserRolesIds() {
        List<String> idList = new ArrayList<>();
        Principal activeUser;
        try {
            activeUser = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception ex) {
            throw new EfwException("User session has expired. Please login.", ex);
        }
        List<Role> list = activeUser.getLoggedInUser().getRoles();
        int counter = 0;
        for (Role role : list) {
            if (role == null) {
                idList.add(counter, null);
                continue;
            }
            idList.add(counter, role.getId() + "");
            counter++;
        }
        return idList;
    }

    @SuppressWarnings("ALL")
    public static boolean isSuperAdmin() {
        boolean isSuperAdmin = false;
        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor.getBean
                (ApplicationDefaultUserAndRoleNamesConfigurer.class);
        List<String> userRoles = AuthenticationUtils.getUserRoles();
        if (userRoles.contains(namesConfigurer.getRoleAdmin())) {
            isSuperAdmin = true;
        }
        return isSuperAdmin;
    }

    public static List<String> getUserRoles() {
        List<String> idList = new ArrayList<>();
        Principal activeUser;
        try {
            activeUser = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception ex) {
            throw new EfwException("User session has expired. Please login.", ex);
        }

        List<Role> list = activeUser.getLoggedInUser().getRoles();
        int counter = 0;
        for (Role role : list) {
            if (role == null) {
                idList.add(counter, null);
                continue;
            }
            String roleId = role.getRole_name();
            idList.add(counter, roleId);
            counter++;
        }
        return idList;
    }

    /**
     * Obtains the currently logged in <code>User</code> credentials as a
     * <code>Principal</code> object.
     * <p/>
     *
     * @return Principal
     */
    public static Principal getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Principal) {
            return (Principal) principal;
        } else {
            throw new EfwException("User session has expired. Please login.");
        }
    }

    /**
     * Returns the currently logged in user's name
     *
     * @return A string
     */
    public static String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Principal activeUser;
        if (principal instanceof Principal) {
            activeUser = (Principal) principal;
        } else {
            throw new EfwException("User session has expired. Please login.");
        }
        return activeUser.getLoggedInUser().getUsername();
    }

    /**
     * Returns the currently logged in user's password
     *
     * @return A string
     */
    public static String getPassword() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Principal activeUser;
        if (principal instanceof Principal) {
            activeUser = (Principal) principal;
        } else {
            throw new EfwException("User session has expired. Please login.");
        }
        return activeUser.getLoggedInUser().getPassword();
    }


    public static Map<String, String> getRealNames(JSONObject fileContent) {


        Map<String, String> realNames = new HashMap<>();
        if (fileContent == null) {
            throw new IllegalArgumentException("Json can't be null");
        }

        String userId;
        userId = getUserId(fileContent);


        UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
        User userObject = userService.findUser(Integer.parseInt(userId));
        if (userObject == null) {
            throw new RuntimeException("The user id is not found");
        }

        userId = userObject.getUsername();


        String password = userObject.getPassword();

        realNames.put("username", userId);
        realNames.put("password", password);
        return realNames;
    }


    public static String getUserId(JSONObject fileContent) {

        Object unDeterminedObject = fileContent.get("security");
        if (unDeterminedObject instanceof JSONObject) {
            return ((JSONObject) unDeterminedObject).getString("createdBy");
        } else {
            return fileContent.getString("security").replace("[", "").replace("]", "").replace("\"", "");
        }
    }


}
