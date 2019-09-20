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

import com.helicalinsight.resourcesecurity.model.Role;
import com.helicalinsight.resourcesecurity.model.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by author on 13-01-2015.
 *
 * @author Rajasekhar
 */
public final class ShareRuleHelper {

    private static final Logger logger = LoggerFactory.getLogger(ShareRuleHelper.class);

    static boolean isShareTagPresent(JSONObject fileAsJson) {
        return fileAsJson.has("share");
    }

    static boolean isResourceSharedWithOtherRoles(JSONObject shareJson) {
        return shareJson.has("roles");
    }

    static boolean isResourceSharedWithUsers(JSONObject shareJson) {
        return shareJson.has("users");
    }


    public static List<Role> getSharedRoles(JSONObject listOfRolesJson) {
        List<Role> roleList = new ArrayList<>();
        try {
            roleList.add(getRolePojo(listOfRolesJson.getJSONObject("role")));
        } catch (JSONException ignore) {
            JSONArray rolesJson = listOfRolesJson.getJSONArray("role");
            Iterator<?> iterator = rolesJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                roleList.add(getRolePojo((JSONObject) iterator.next()));
            }
        }
        return roleList;
    }


    private static Role getRolePojo(JSONObject roleJson) {
        Role role = new Role();
        //Set name if available
        if (roleJson.has("@name")) {
            role.setRoleName(roleJson.getString("@name"));
        } else {
            role.setRoleName(null);
        }

        //Set id if available
        if (roleJson.has("@id")) {
            role.setRoleId(roleJson.getString("@id"));
        } else {
            role.setRoleId(null);
        }

        role.setPermissionLevel(getPermissionLevel(roleJson));
        return role;
    }

    private static int getPermissionLevel(JSONObject json) {
        int permissionLevel = -1;
        try {
            permissionLevel = Integer.parseInt(json.getString("#text"));
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("One of the file's permission level was not number.", ex);
            }
        }
        return permissionLevel;
    }


    public static List<User> getSharedUsers(JSONObject listOfUsersJson) {
        List<User> userList = new ArrayList<>();
        try {
            userList.add(getUserPojo(listOfUsersJson.getJSONObject("user")));
        } catch (JSONException ignore) {
            JSONArray usersJson = listOfUsersJson.getJSONArray("user");
            Iterator<?> iterator = usersJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                userList.add(getUserPojo((JSONObject) iterator.next()));
            }
        }
        return userList;
    }


    private static User getUserPojo(JSONObject userJson) {
        User user = new User();
        if (userJson.has("@name")) {
            user.setUsername(userJson.getString("@name"));
        } else {
            user.setUsername(null);
        }

        if (userJson.has("@id")) {
            user.setUserId(userJson.getString("@id"));
        } else {
            user.setUserId(null);
        }

        user.setPermissionLevel(getPermissionLevel(userJson));
        return user;
    }


}
