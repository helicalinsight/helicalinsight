package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
     * @param organizationId logged-in organizationId
     * @param userService    user service object
     * @return json as string
     */

    public static JSONObject getUsersJson(Integer organizationId, UserService userService,
                                          LimitOffsetModel limitOffsetModel) {
        List<User> listOfUsers;
        if (organizationId == null) {
            listOfUsers = userService.findUsers(limitOffsetModel);
        } else {
            listOfUsers = userService.findOrganisationUsers(organizationId, limitOffsetModel);
        }
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


        int userCount = limitOffsetModel.getOffset();
        for (User user : listOfUsers) {
            JSONObject userJson = setUserjson(user);
            userJson.accumulate("slno", "" + (++userCount));
            userJsonObject.accumulate("users", userJson);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("User details json object is %s", userJsonObject.toString()));
        }
        userJsonObject.accumulate("total", limitOffsetModel.getTotalCount());
        return userJsonObject;
    }

    @NotNull
    public static JSONObject setUserjson(User user) {
        JSONArray rolesArray = new JSONArray();
        JSONObject userJson = new JSONObject();
        JSONArray userProfileArray = new JSONArray();

        userJson.accumulate("id", user.getId());
        userJson.accumulate("name", user.getUsername());
        userJson.accumulate("email", user.getEmailAddress());
        userJson.accumulate("enabled", user.isEnabled());
        userJson.accumulate("isExternalUser", user.getIsExternallyAuthenticated());
        if (user.getOrganization() == null) {
            userJson.accumulate("organisation", "");
            userJson.accumulate("orgName", ApplicationProperties.getInstance().getNullValue());
        } else {
            userJson.accumulate("organisation", user.getOrganization().getId());
            userJson.accumulate("orgName", user.getOrganization().getOrg_name());
        }

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
        return userJson;
    }

    /**
     * This method is responsible for fetching organisation detail from DB prepare the json of
     * fetched
     * data and return json as string
     *
     * @param organizationService organisation service object
     * @return json as string
     */
    public static JSONObject getOrganizationJson(OrganizationService organizationService, Integer orgId,
                                                 LimitOffsetModel limitOffsetModel) {
        List<Organization> organizationList = new ArrayList<>();
        Organization userOrganization;
        if (orgId == null) {
            //organizationList = organizationService.getAllOrganization();
            organizationList = organizationService.findAllOrganization(limitOffsetModel);
        } else {
            //userOrganization = organizationService.getOrganization(orgId);
            userOrganization = organizationService.findOrganization(orgId, limitOffsetModel);
            limitOffsetModel.setTotalCount(userOrganization != null ? 1 : 0);
            organizationList.add(userOrganization);
        }

        return getOrganizationJson(organizationList, limitOffsetModel);
    }

    private static JSONObject getOrganizationJson(List<Organization> organizationList,
                                                  LimitOffsetModel limitOffsetModel) {
        JSONObject organizationJsonObject = new JSONObject();
        JSONArray organizationArray = new JSONArray();
        int count = limitOffsetModel.getOffset();
        if (organizationList != null) {
            for (Organization organization : organizationList) {
                JSONObject obj = getAnOrganization(organization);
                obj.accumulate("slno", "" + (++count));
                organizationArray.add(obj);
            }
        }

        organizationJsonObject.accumulate("organisations", organizationArray);
        organizationJsonObject.accumulate("total", limitOffsetModel.getTotalCount());
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("organization details json object is %s", organizationJsonObject.toString()));
        }
        return organizationJsonObject;
    }

    @NotNull
    public static JSONObject getAnOrganization(Organization organization) {
        JSONObject obj = new JSONObject();
        obj.accumulate("id", organization.getId());
        obj.accumulate("name", organization.getOrg_name());
        obj.accumulate("description", organization.getOrg_desc());
        return obj;
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

    /**
     * This method is responsible for fetching the role details from DB & prepare the Json of
     * fetched data and return json.
     *
     * @param currentUser logged-in user details
     * @param roleService role service object
     * @return json of specific organization roles
     */
    public static JSONObject getRolesJson(Principal currentUser, RoleService roleService) {
        List<Role> roleList;
        JSONObject roleJsonObject = new JSONObject();
        JSONArray roleJsonArray = new JSONArray();
        LimitOffsetModel pageCount = new LimitOffsetModel();
        Integer orgId = currentUser.getLoggedInUser().getOrg_id();
        if (orgId == null) {
            roleList = roleService.getAllRole(0, Integer.MAX_VALUE, pageCount);
        } else {
            roleList = roleService.getOrganizationRoles(orgId, pageCount);
        }

        for (Role roles : roleList) {
            JSONObject obj = new JSONObject();
            obj.accumulate("id", roles.getId());
            obj.accumulate("name", roles.getRole_name());
            if (roles.getOrganization() == null) {
                obj.accumulate("organisation", "");
            } else {
                obj.accumulate("organisation", roles.getOrganization().getId());
            }
            roleJsonArray.add(obj);
        }
        roleJsonObject.accumulate("total", pageCount.getTotalCount());
        roleJsonObject.accumulate("roles", roleJsonArray);
        return roleJsonObject;
    }

    public static JSONObject getSuperOrganizationRoles(RoleService roleService) {
        List<Role> roleList;
        JSONObject roleJsonObject = new JSONObject();
        JSONArray roleJsonArray = new JSONArray();
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
        //Needs to be set as searchPhrase has to be %%
        limitOffsetModel.setSearchPhrase(null);
        //Request only for super organization roles
        limitOffsetModel.setOnlySuperOrganization(true);

        roleList = roleService.getOrganizationRoles(null, limitOffsetModel);
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
     * This method is responsible for fetching the role detail for specific organisation from DB
     * prepare the json of fetched data and return json as string
     *
     * @param roleService role service object
     * @param orgId       organisation id for which role to be fetch
     * @return json as string
     */
    public static JSONObject getOrganizationRoles(RoleService roleService, Integer orgId,
                                                  LimitOffsetModel limitOffsetModel) {
        List<Role> rolesList;
        if (orgId == null) {
            rolesList = roleService.findAllRoles(limitOffsetModel);
        } else {
            rolesList = roleService.getOrganizationRoles(orgId, limitOffsetModel);
        }

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
                JSONObject jsonObject = getARole(role);
                   jsonObject.accumulate("slno", "" + (++counts));
                rolesJsonArray.add(jsonObject);
            }

            rolesJson.accumulate("total", limitOffsetModel.getTotalCount());
            rolesJson.accumulate("roles", rolesJsonArray);
            return rolesJson;
        }
    }

    @NotNull
    public static JSONObject getARole(Role role) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id", role.getId());
        jsonObject.accumulate("name", role.getRole_name());
        if (role.getOrganization() == null) {
            jsonObject.accumulate("organisation", "");
            jsonObject.accumulate("orgName", ApplicationProperties.getInstance().getNullValue());
        } else {
            jsonObject.accumulate("organisation", role.getOrganization().getId());
            jsonObject.accumulate("orgName", role.getOrganization().getOrg_name());
        }
        return jsonObject;
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
        List<Profile> profileList;
        if (currentUser.getLoggedInUser().getOrg_id() == null) {
            profileList = profileService.getAllProfile();
        } else {
            profileList = profileService.getUserProfile(currentUser.getLoggedInUser().getId());
        }
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
     * This method validate the form data for organisation if mandatory field is empty or null
     * throws the exception
     *
     * @param entityObj json object
     */
    public static void checkNullsForOrganization(JSONObject entityObj) {
        String name = entityObj.getString("name");
        String description = entityObj.getString("description");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("password", description);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }

    /**
     * This method check for duplicate role whether role which user want to add is already present
     * in DB depends on it return true or false
     *
     * @param roleService role service object
     * @param name        role name
     * @param orgId       organisation id
     * @return boolean value either true or false
     */
    public static boolean checkForDuplicateRole(RoleService roleService, String name, Integer orgId) {
        boolean isRoleExist = false;
        Role role;
        if (orgId == null) {
            role = roleService.findByName(name);
        } else {
            role = roleService.findRoleByNameNOrgId(name, orgId);
        }
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
     * @param orgId       organisation id
     * @return boolean value true or false
     */
    public static boolean checkForDuplicateUser(User user, UserService userService, String name, Integer orgId) {
        boolean isUserExist = false;
        User existUser;
        if (orgId == null) {
            existUser = userService.findUserByNameNorgNull(name);

        } else {
            existUser = userService.findUserByNameNOrgId(name, orgId);

        }
        if (existUser != null) {
            isUserExist = true;
        }
        return isUserExist;
    }

    /**
     * This method check for duplicate organisation whether organisation which user want to add is
     * already present
     * in DB depends on it return true or false
     *
     * @param organizationService organisation service object
     * @param organizationName    organisation name
     * @return boolean value true or false
     */
    public static boolean checkForDuplicateOrganization(OrganizationService organizationService,
                                                        String organizationName) {
        boolean doesOrgExists = false;
        Organization existOrganization;
        existOrganization = organizationService.getOrganization(organizationName);
        if (existOrganization != null) {
            doesOrgExists = true;
        }
        return doesOrgExists;
    }

    public static boolean isValidName(String name,String regex) {
    	return name.matches(regex);
    }
    
	public static boolean isValidEmail(String email) {
		if (StringUtils.isNotBlank(email) && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				throw new EfwServiceException("Invalid Email address");
		}
		return true;
	}
    
}
