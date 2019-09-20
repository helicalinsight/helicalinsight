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

import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.admin.utils.RolesAccessComponent;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ListIterator;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareServiceImpl implements IComponent {

    private final RolesAccessComponent rolesAccessComponent = ApplicationContextAccessor.getBean(RolesAccessComponent
            .class);

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        JSONObject model;
        model = new JSONObject();

        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();


        JSONObject provideJson;

        if (!formData.has("provide")) {
            //Can't do anything
            throw new IncompleteFormDataException("The parameter provide should be in the " + "form of a json object.");
        } else {
            provideJson = formData.getJSONObject("provide");
        }


        String id = null;//Id should not  be null
        if (provideJson.has("id")) {
            id = provideJson.getString("id");
        }

        if (id == null) {
            //Provide only super  users and roles and return.
            prepareModelWithUsersAndRoles(model, limitOffsetModel, provideJson);
            return model.toString();
        } else if ("all".equalsIgnoreCase(id)) {

            prepareModel(model, limitOffsetModel, provideJson);
        }

        return model.toString();
    }


    private void prepareModelWithUsersAndRoles(JSONObject model,
                                               LimitOffsetModel limitOffsetModel,
                                               JSONObject provideJson) {
        if (provideJson.has("provideRoles") && "true".equalsIgnoreCase(provideJson.getString("provideRoles"))) {
            RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
            JSONObject rolesJson = AdminUtils.getRoles(roleService);
            model.accumulate("allRoles", rolesJson);
        }

        if (provideJson.has("provideUsers") && "true".equalsIgnoreCase(provideJson.getString("provideUsers"))) {
            UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
            limitOffsetModel.setSearchPhrase(ApplicationProperties.getInstance().getNullValue());
            JSONObject usersJson = AdminUtils.getUsersJson(userService, limitOffsetModel);
            model.accumulate("allUsers", discardLoggedInUser(usersJson));
        }
    }


    public void prepareModel(JSONObject model, LimitOffsetModel limitOffsetModel,
                             JSONObject provideJson) {
        includeRoles(provideJson, model, rolesAccessComponent, limitOffsetModel);
        includeUsers(provideJson, model, limitOffsetModel);
    }

    private JSONObject discardLoggedInUser(JSONObject usersJson) {
        String userId = AuthenticationUtils.getUserId();
        Object allUsers = usersJson.get("users");
        if (allUsers instanceof JSONObject) {//Only one user
            usersJson.discard("users");
            usersJson.put("users", new JSONObject());
            return usersJson;
        } else {
            JSONArray users = (JSONArray) allUsers;
            ListIterator listIterator = users.listIterator();
            while (listIterator.hasNext()) {
                JSONObject user = JSONObject.fromObject(listIterator.next());
                if (user.getString("id").equals(userId)) {
                    listIterator.remove();
                }
            }
            usersJson.put("users", users);
            return usersJson;
        }
    }

    private void includeRoles(JSONObject provideJson, JSONObject model,
                              RolesAccessComponent rolesAccessComponent,
                              LimitOffsetModel limitOffsetModel) {
        if (provideJson.has("provideRoles") && "true".equalsIgnoreCase(provideJson.getString("provideRoles"))) {
            JSONObject rolesJson = rolesAccessComponent.allRoles(limitOffsetModel);
            model.accumulate("allRoles", rolesJson);
        }
    }

    private void includeUsers(JSONObject provideJson, JSONObject model,
                              LimitOffsetModel limitOffsetModel) {
        if (provideJson.has("provideUsers") && "true".equalsIgnoreCase(provideJson.getString("provideUsers"))) {
            UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
            JSONObject usersJson = AdminUtils.getUsersJson(userService, limitOffsetModel);
            model.accumulate("allUsers", discardLoggedInUser(usersJson));
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
