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

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by author on 15-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareJsonValidator {


    private final Set<String> userIds = new HashSet<>();


    private final Set<String> userNames = new HashSet<>();


    private final Set<String> roleIds = new HashSet<>();


    private final Set<String> roleNames = new HashSet<>();


    public ShareJsonValidator() {
        //Set the json that has to be validated.

        ShareServiceImpl shareService = new ShareServiceImpl();

        JSONObject provideJson = new JSONObject();
        provideJson.accumulate("provideUsers", "true");

        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();

        JSONObject model = new JSONObject();

        setRoleNamesAndIds();


        shareService.prepareModel(model, limitOffsetModel, provideJson);
        setUserNamesAndIds(model);

    }

    private void setRoleNamesAndIds() {
        RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
        List<Role> allRoles = roleService.getAllRoles();

        for (Role role : allRoles) {
            this.roleIds.add(String.valueOf(role.getId()));
            this.roleNames.add(String.valueOf(role.getRole_name()));
        }
    }


    private void setUserNamesAndIds(JSONObject model) {
        JSONObject allUsers = model.getJSONObject("allUsers");
        Object usersJson = allUsers.get("users");
        if (usersJson instanceof JSONArray) {
            for (Object object : (JSONArray) usersJson) {
                JSONObject user = JSONObject.fromObject(object);
                if (user.has("id")) {
                    String userId = user.getString("id");
                    this.userIds.add(userId);
                }

                if (user.has("name")) {
                    String name = user.getString("name");
                    this.userNames.add(name);
                }
            }
        }
    }

    public void validate(JSONObject jsonObject) {
        validateRole(jsonObject);
        validateUser(jsonObject);
    }

    private void validateRole(JSONObject jsonObject) {
        if (jsonObject.has("role")) {
            JSONArray roles = jsonObject.getJSONArray("role");
            for (Object object : roles) {
                JSONObject json = JSONObject.fromObject(object);
                if (json.has("id")) {
                    String id = json.getString("id");
                    if (!(this.roleIds.contains(id))) {
                        throw new InvalidDataException("The requested role id " + id + " is not a" +
                                " valid id. Access Denied.");
                    }
                }

                if (json.has("name")) {
                    String name = json.getString("name");
                    if (!(this.roleNames.contains(name))) {
                        throw new InvalidDataException("The requested role name " + name + " is " +
                                "not a valid name. Access Denied.");
                    }
                }
            }
        }
    }

    private void validateUser(JSONObject jsonObject) {
        if (jsonObject.has("user")) {
            JSONArray users = jsonObject.getJSONArray("user");
            for (Object object : users) {
                JSONObject json = JSONObject.fromObject(object);
                if (json.has("id")) {
                    String id = json.getString("id");
                    if (!(this.userIds.contains(id))) {
                        throw new InvalidDataException("The requested user id " + id + " is not a" +
                                " valid id. Access Denied.");
                    }
                }

                if (json.has("name")) {
                    String name = json.getString("name");
                    if (!(this.userNames.contains(name))) {
                        throw new InvalidDataException("The requested user name " + name + " is " +
                                "not a valid name. Access Denied.");
                    }
                }
            }
        }
    }


}
