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
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the utility class for spring security controller.This
 * class methods get called from  spring security controller for specific model and these methods
 * connect
 * to DAO layer and prepare the data in form of json.
 *
 * @author Muqtar Ahmed
 * @version 1.2
 */
@SuppressWarnings("unused")
public class AdminUtils {

    public static final Logger logger = LoggerFactory.getLogger(AdminUtils.class);

    /**
     * This method is responsible for fetching the user detail from DB prepare the json of fetched
     * data and return json as string
     *
     * @param userService user service object
     * @return json as string
     */

    public static JSONObject getUsersJson(UserService userService,
                                          LimitOffsetModel limitOffsetModel) {
        List<User> listOfUsers = userService.findUsers(limitOffsetModel);
        if (listOfUsers == null) {
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();
            jsonObj.accumulate("users", array);
            jsonObj.accumulate("total", limitOffsetModel.getTotalCount());
            return jsonObj;
        } else {
            return createJsonObjectFromUserList(listOfUsers, limitOffsetModel);
        }
    }

    private static JSONObject createJsonObjectFromUserList(List<User> listOfUsers, LimitOffsetModel limitOffsetModel) {
        JSONObject userJsonObject = new JSONObject();
        JSONObject userJson;
        JSONArray rolesArray;
        JSONArray userProfileArray;
        int userCount = limitOffsetModel.getOffset();
        for (User user : listOfUsers) {
            userJson = new JSONObject();
            rolesArray = new JSONArray();
            userProfileArray = new JSONArray();
            userJson.accumulate("slno", "" + (++userCount));
            userJson.accumulate("id", user.getId());
            userJson.accumulate("name", user.getUsername());
            userJson.accumulate("email", user.getEmailAddress());
            userJson.accumulate("enabled", user.isEnabled());
            userJson.accumulate("organisation", "");
            userJson.accumulate("orgName", "NULL");
            if (user.getRoles() != null) {
                for (Role role : user.getRoles()) {
                    JSONObject rolesObj = new JSONObject();
                    rolesObj.accumulate("id", role.getId());
                    rolesObj.accumulate("role", role.getRole_name());
                    rolesArray.add(rolesObj);
                }
            }

            userJson.accumulate("roles", rolesArray);
            if (user.getProfile() != null) {
                for (Profile profile : user.getProfile()) {
                    JSONObject profileObj = new JSONObject();
                    profileObj.accumulate("id", profile.getId());
                    profileObj.accumulate("name", profile.getProfile_name());
                    profileObj.accumulate("value", profile.getProfile_value());
                    userProfileArray.add(profileObj);
                }
            }
            userJson.accumulate("profiles", userProfileArray);
            userJsonObject.accumulate("users", userJson);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User details json object is %s", userJsonObject.toString()));
        }
        userJsonObject.accumulate("total", limitOffsetModel.getTotalCount());
        return userJsonObject;
    }

    /**
     * This method is responsible for fetching the profile detail from DB prepare the json of
     * fetched
     * data and return json as string
     *
     * @param profileService profile service object
     * @return json as string
     */
    public static JSONObject getProfile(ProfileService profileService) {
        JSONObject profileJsonObject = new JSONObject();
        JSONArray profileJsonArray = new JSONArray();
        List<Profile> profileList = profileService.getAllProfile();
        for (Profile profile : profileList) {
            JSONObject obj = new JSONObject();
            obj.accumulate("id", profile.getId());
            obj.accumulate("name", profile.getProfile_name());
            obj.accumulate("value", profile.getProfile_value());
            obj.accumulate("user", profile.getUser().getId());
            profileJsonArray.add(obj);
        }

        profileJsonObject.accumulate("profiles", profileJsonArray);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("profile details json object is %s", profileJsonObject.toString()));
        }
        return profileJsonObject;
    }

    public static JSONObject getRoles(RoleService roleService) {
        List<Role> roleList;
        JSONObject roleJsonObject = new JSONObject();
        JSONArray roleJsonArray = new JSONArray();
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
        limitOffsetModel.setSearchPhrase(null);


        roleList = roleService.getRoles(limitOffsetModel);
        int counter = 0;
        for (Role role : roleList) {
            JSONObject json = new JSONObject();
            json.accumulate("slno", counter);
            json.accumulate("id", role.getId());
            json.accumulate("name", role.getRole_name());
            json.accumulate("organisation", "");
            json.accumulate("orgName", ApplicationProperties.getInstance().getNullValue());
            roleJsonArray.add(json);
            counter++;
        }
        roleJsonObject.accumulate("total", limitOffsetModel.getTotalCount());
        roleJsonObject.accumulate("roles", roleJsonArray);
        return roleJsonObject;
    }

    /**
     * This method is responsible for fetching the role detail from DB
     * prepare the json of fetched data and return json as string
     *
     * @param roleService role service object
     * @return json as string
     */
    public static JSONObject getRoles(RoleService roleService,
                                      LimitOffsetModel limitOffsetModel) {
        List<Role> rolesList;
        rolesList = roleService.findAllRoles(limitOffsetModel);

        JSONObject rolesJson = new JSONObject();
        JSONArray rolesJsonArray = new JSONArray();

        if (rolesList == null) {
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();
            json.accumulate("users", array);
            return json;
        } else {
            int counts = limitOffsetModel.getOffset();
            for (Role role : rolesList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("slno", "" + (++counts));
                jsonObject.accumulate("id", role.getId());
                jsonObject.accumulate("name", role.getRole_name());
                jsonObject.accumulate("organisation", "");
                jsonObject.accumulate("orgName", "NULL");
                rolesJsonArray.add(jsonObject);
            }

            rolesJson.accumulate("total", limitOffsetModel.getTotalCount());
            rolesJson.accumulate("roles", rolesJsonArray);
            return rolesJson;
        }
    }

    /**
     * This method is responsible for fetching the profile detail from DB
     * prepare the json of fetched data and return json as string
     *
     * @param currentUser    logged-in user details
     * @param profileService profile service object
     * @return json as string
     */
    public static JSONObject getProfileJson(Principal currentUser, ProfileService profileService) {
        JSONObject profileJsonObject = new JSONObject();
        JSONArray profileJsonArray = new JSONArray();
        List<Profile> profileList = profileService.getAllProfile();

        for (Profile profile : profileList) {
            JSONObject profileObj = new JSONObject();
            profileObj.accumulate("id", profile.getId());
            profileObj.accumulate("name", profile.getProfile_name());
            profileObj.accumulate("value", profile.getProfile_value());
            profileJsonArray.add(profileObj);
        }
        profileJsonObject.accumulate("profile", profileJsonArray);
        return profileJsonObject;
    }

    /**
     * This method is responsible for fetching the profile detail for specific user from DB
     * prepare the json of fetched data and return json as string
     *
     * @param profileService profile service object
     * @param userId         user id
     * @return json as string
     */
    public static JSONObject getUserProfile(ProfileService profileService, Integer userId) {
        List<Profile> userProfile = profileService.getUserProfile(userId);

        JSONObject profileJosnObj = new JSONObject();
        JSONArray profileJsonArray = new JSONArray();
        for (Profile profile : userProfile) {
            JSONObject profileObj = new JSONObject();
            profileObj.accumulate("id", profile.getId());
            profileObj.accumulate("name", profile.getProfile_name());
            profileObj.accumulate("value", profile.getProfile_value());
            profileJsonArray.add(profileObj);
        }

        profileJosnObj.accumulate("profile", profileJsonArray);
        return profileJosnObj;
    }

    /**
     * This method validate the form data for user if mandatory field is empty or null throws the
     * exception
     *
     * @param entityObj json object
     */
    public static void checkNullsForUser(JSONObject entityObj) {
        String name = entityObj.getString("name");
        String password = entityObj.getString("password");
        String email = entityObj.getString("email");
        String enabled = entityObj.getString("enabled");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("password", password);
        parameters.put("email", email);
        parameters.put("enabled", enabled);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }

    /**
     * This method validate the form data for profile if mandatory field is empty or null throws
     * the exception
     *
     * @param entityObj json object
     */
    public static void checkNullsForProfile(JSONObject entityObj) {
        String name = entityObj.getString("name");
        String value = entityObj.getString("value");
        String profileUserId = entityObj.getString("id");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("enabled", value);
        parameters.put("enabled", profileUserId);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }


    /**
     * This method check for duplicate role whether role which user want to add is already present
     * in DB depends on it return true or false
     *
     * @param roleService role service object
     * @param name        role name
     * @return boolean value either true or false
     */
    public static boolean checkForDuplicateRole(RoleService roleService, String name) {
        boolean isRoleExist = false;
        Role role = roleService.findByName(name);

        if (role != null) {
            isRoleExist = true;
        }
        return isRoleExist;
    }

    /**
     * This method check for duplicate profile whether profile which user want to add is already
     * present
     * in DB depends on it return true or false
     *
     * @param profileService profile service object
     * @param profileName    profile name
     * @param userId         user id
     * @return boolean true or false
     */
    public static boolean checkForDuplicateProfile(ProfileService profileService, String profileName, int userId) {
        boolean isProfileExist = false;
        Profile existProfile;
        existProfile = profileService.getProfileByNameAndUserId(profileName, userId);
        if (existProfile != null) {
            isProfileExist = true;
        }
        return isProfileExist;
    }

    /**
     * This method check for duplicate user whether user which user want to add is already present
     * in DB depends on it return true or false
     *
     * @param user        user object
     * @param userService user service object
     * @param name        user name
     * @return boolean value true or false
     */
    public static boolean checkForDuplicateUser(User user, UserService userService, String name) {
        User existUser = userService.findUserByName(name);

        return existUser != null;
    }
}
