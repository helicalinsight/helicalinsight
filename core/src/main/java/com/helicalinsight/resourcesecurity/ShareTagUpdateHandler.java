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

package com.helicalinsight.resourcesecurity;

import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareTagUpdateHandler {

    private final List<String> permissions;

    public ShareTagUpdateHandler() {
        permissions = new PermissionConstants().allowedPermissions();
    }


    public Security.Share updateShareTag(Security.Share share, JSONObject shareWith) {
        if (share == null) {//Create a new one if the xml is empty
            share = ApplicationContextAccessor.getBean(Security.Share.class);
        }

        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();


        updateRoles(share, shareWith, roles);

        updateUsers(share, shareWith, users);
        return share;
    }


    private void updateRoles(Security.Share share, JSONObject shareWith,
                             Security.Roles roles) {
        List<Security.Role> rolesList;
        if (shareWith.has("role")) {
            JSONArray roleArray = shareWith.getJSONArray("role");
            if (roles != null) {
                rolesList = roles.getRoles();
                if (rolesList == null) {
                    rolesList = new ArrayList<>();
                }
                setValuesFromRoleJson(share, roles, rolesList, roleArray);
            } else {
                roles = ApplicationContextAccessor.getBean(Security.Roles.class);
                rolesList = new ArrayList<>();
                setValuesFromRoleJson(share, roles, rolesList, roleArray);
            }
        }
    }

    private void updateUsers(Security.Share share, JSONObject shareWith,
                             Security.Users users) {
        List<Security.User> usersList;
        if (shareWith.has("user")) {
            JSONArray userArray = shareWith.getJSONArray("user");
            if (users != null) {
                usersList = users.getUsers();
                if (usersList == null) {
                    usersList = new ArrayList<>();
                }
                setValuesFromUserJson(share, users, usersList, userArray);
            } else {
                users = ApplicationContextAccessor.getBean(Security.Users.class);
                usersList = new ArrayList<>();
                setValuesFromUserJson(share, users, usersList, userArray);
            }
        }
    }


    private void setValuesFromRoleJson(Security.Share share, Security.Roles roles,
                                       List<Security.Role> rolesList, JSONArray roleArray) {
        Security.Role role;
        for (Object object : roleArray) {
            JSONObject roleJson = JSONObject.fromObject(object);
            String permission;
            String id = null;
            String name = null;
            if (roleJson.has("id") && roleJson.has("name")) {
                malformedJson();
            }

            try {
                permission = roleJson.getString("permission");

                validatePermissionLevel(permission);

                if (roleJson.has("id")) {
                    id = roleJson.getString("id");
                }
                if (roleJson.has("name")) {
                    name = roleJson.getString("name");
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with role" +
                        " array. Permission level is expected along with either " +
                        "name or id.");
            }

            if ((id != null && name != null) || (id == null && name == null)) {
                malformedJson();
            }

            role = ApplicationContextAccessor.getBean(Security.Role.class);
            role.setPermission(permission);

            if (id != null) {
                role.setId(id);
            }

            if (name != null) {
                role.setName(name);
            }

            if (!rolesList.contains(role)) {
                rolesList.add(role);
            }
        }
        roles.setRoles(rolesList);
        share.setRoles(roles);
    }

    private void setValuesFromUserJson(Security.Share share, Security.Users users,
                                       List<Security.User> usersList, JSONArray userArray) {
        Security.User user;
        for (Object object : userArray) {
            JSONObject userJson = JSONObject.fromObject(object);
            String permission;
            String id = null;
            String name = null;
            if (userJson.has("id") && userJson.has("name")) {
                malformedJson();
            }

            try {
                permission = userJson.getString("permission");

                validatePermissionLevel(permission);

                if (userJson.has("id")) {
                    id = userJson.getString("id");
                }
                if (userJson.has("name")) {
                    name = userJson.getString("name");
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with user" +
                        " array. Permission level is expected along with either " +
                        "name or id.");
            }

            if ((id != null && name != null) || (id == null && name == null)) {
                malformedJson();
            }

            user = ApplicationContextAccessor.getBean(Security.User.class);
            user.setPermission(permission);

            if (id != null) {
                user.setId(id);
            }

            if (name != null) {
                user.setName(name);
            }

            if (!usersList.contains(user)) {
                usersList.add(user);
            }
        }
        users.setUsers(usersList);
        share.setUsers(users);
    }

    private void validatePermissionLevel(String permission) {
        if (!this.permissions.contains(permission)) {
            throw new IllegalArgumentException("The permission level " + permission + " " +
                    "is not valid.");
        }
    }

    private void malformedJson() {
        throw new MalformedJsonException("Parameter share has one or more malformed array(s)" +
                "." + "Only id or name is expected but not both.");
    }
}
