package com.helicalinsight.admin.controller;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.ResponseUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Cipher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller handles the following activities
 * 01) Create Read Update Delete operations on User
 * 02) Create Read Update Delete operations on Organization
 * 03) Create Read Update Delete operations on Profile
 * 04) Create Read Update Delete operations on Roles
 *
 * @author Muqtar Ahmed
 * @author Somen
 * @version 1.0.2.0
 */
@SuppressWarnings("unused")
@Controller
@Component
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolesAccessComponent rolesAccessComponent;

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Autowired
    private ProfileService profileService;


    /**
     * This method maps the request to
     * 01) If the user has a valid session then the user goes to welcome.html
     * 02) If the user has no valid session, then the login screen is shown.
     *
     * @return The tiles mapping configuration <b>template-main</b> as th view
     */
    @RequestMapping(value = {"/index", "/"})
    public String defaultPage(HttpServletRequest request, HttpServletResponse response) {
        if (logger.isDebugEnabled()) {
            logger.debug("Root mapping is invoked. Returning tiles view template-main.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {

            return "loginBody";
        }
        else{
            boolean isAjax = ControllerUtils.isAjax(request);
            JSONObject result = new JSONObject();
            result.accumulate("message", "Login Success");
            try {
                if(isAjax) {
                    ControllerUtils.handleSuccess(response, isAjax, result.toString());
                }
                else{
                    return "loginBody";
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return  null;
        }
    }

    /**
     * When the user requests /login.html it redirects to the login page
     * If the user is already logged in then the respective welcome page is displayed
     * When the user is unable to login due to wrong credentials then the spring security
     * directs to the mapping /loginFailed, the user is shown respective error message
     * The error attribute is set to <b>true</b> in the error parameter of the session
     * This method redirects the user to login page when the user request /login.html
     *
     * @param request the  HttpSession object is obtained from the request
     * @return /admin.html or /hdi.html  or /welcome.html
     * @throws IOException
     */
    @RequestMapping(value = {"/login", "/loginFailed"}, method = {RequestMethod.GET})
    public View login(HttpServletRequest request) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Requested with /login or /loginFailed mapping");
        }

        Authentication activeAuthentication = SecurityContextHolder.getContext().getAuthentication();
        RedirectView view;

        HttpSession session = request.getSession(false);
        String redirectUrl = whenUserHasValidSession(activeAuthentication, session);

        if (redirectUrl == null) { 
        	redirectUrl = request.getContextPath()+"/index";
        }
        if (session != null) {
            session.setAttribute("error", "true");
        }

        view = new RedirectView(redirectUrl);
        view.setExposeModelAttributes(false);
        return view;
    }

    private String whenUserHasValidSession(Authentication authentication, HttpSession session) {
        String redirectUrl = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<String> userRoles = AuthenticationUtils.getUserRoles();
            if (userRoles.contains(this.namesConfigurer.getRoleAdmin())) {
                String roleAdmin = namesConfigurer.getRoleAdmin();
                session.setAttribute("superAdminRole", roleService.findRoleByNameNOrgId(roleAdmin, null));
                redirectUrl = "./admin";
            } else if (userRoles.contains(this.namesConfigurer.getRoleUser())) {
                redirectUrl = "./hi";
            } else {
                redirectUrl = "./welcome";
            }
        }
        return redirectUrl;
    }


    /**
     * This method maps the welcome page when the user is logged in successfully
     *
     * @return String <b>forward-page</b> which maps to the tiles-definition.xml
     */
    @RequestMapping(value = "/welcome", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String commonPage() {
        if (logger.isDebugEnabled()) {
            logger.debug("Returning forward-page");
        }
        return "Login Success";
    }


    /**
     * This service maps the admin home page.
     *
     * @return adminLoggedIn
     */
    @RequestMapping(value = {"/admin/home", "/admin"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String adminDefaultPage() {
        if (logger.isDebugEnabled()) {
            logger.debug("Returning adminLoggedIn");
        }
        return "adminLoggedIn";
    }

    /**
     * This service helps to return  all the roles contained in the organization
     *
     * @param orgId    The organization id for which the roles list has to be obtained. This may be null
     * @param request  This is only used to determine if the request was ajax call
     * @param response The json object  containing list of all the roles w.r.t to organization
     * @throws IOException
     */
    @RequestMapping(value = "/admin/roles/{id}", method = RequestMethod.GET)
    public void listRoles(@PathVariable("id") String orgId, HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            LimitOffsetModel limitOffsetModel = new LimitOffsetModel(request);
            JSONObject result = this.rolesAccessComponent.allRoles(orgId, limitOffsetModel);
            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * This service is responsible for performing user activity like creating,
     * updating and deleting user depends on action( add, update, delete)
     *
     * @param request  HTTP Request
     * @param response HTTPResponse
     * @throws IOException Exception
     */
    @RequestMapping(value = "/admin/users", method = {RequestMethod.POST, RequestMethod.GET})
    public void getUserService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        String action = request.getParameter("action");
        String formData = request.getParameter("formData");
        JSONObject formDataJson = JSONObject.fromObject(formData);
        try {
            Integer organizationId = rolesAccessComponent.getLoggedInUserOrgId();
            JSONObject result = new JSONObject();
            User user = new User();
            if (StringUtils.isEmpty(action)) {
                LimitOffsetModel limitOffsetModel = new LimitOffsetModel(request);
                result = AdminUtils.getUsersJson(organizationId, userService, limitOffsetModel);
            } else {
                String userId = request.getParameter("id");
                if ("add".equalsIgnoreCase(action)) {
                    result.accumulate("message", "User created successfully.");
                    Integer userIdCreated = crateNewUser(user, formDataJson, organizationId);
                    User newUser = userService.findUser(userIdCreated);
                    JSONObject userNew = AdminUtils.setUserjson(newUser);
                    result.accumulate("id", userIdCreated);
                    result.accumulate("data", userNew);
                } else if ("update".equalsIgnoreCase(action)) {
                    result.accumulate("message", updateUser(userId, formDataJson));
                } else if ("delete".equalsIgnoreCase(action)) {
                    result.accumulate("message", deleteUser(userId));
                }
                result = ResponseUtils.handleSuccessMassage(result);
            }
            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    private Integer crateNewUser(User user, JSONObject formDataJson, Integer organizationId) {
        String name = formDataJson.getString("name");
        String password = formDataJson.getString("password");
        String email = formDataJson.getString("email");
        String enabled = formDataJson.getString("enabled");
        String orgId = formDataJson.getString("organisation");
        Integer organization;
        if (organizationId != null) {
            organization = organizationId;
        } else if (orgId != null && orgId.trim().length() > 0) {
            organization = Integer.parseInt(orgId);
        } else {
            organization = null;
        }
        AdminUtils.checkNullsForUser(formDataJson);
        if (AdminUtils.checkForDuplicateUser(user, userService, name, organization)) {
            throw new OperationFailedException("User with the same name already exists");
        } else {
            Role userRole;
            String roleUser = this.namesConfigurer.getRoleUser();
            if (organization == null) {
                userRole = roleService.findByName(roleUser);
                user.setOrg_id(null);
            } else {
                userRole = roleService.findRoleByNameNOrgId(roleUser, organization);
                user.setOrg_id(organization);
            }

            if ("TRUE".equalsIgnoreCase(enabled)) {
                user.setEnabled(true);
            } else if ("FALSE".equalsIgnoreCase(enabled)) {
                user.setEnabled(false);
            }
            List<Role> userRoleList = new ArrayList<>();
            userRoleList.add(userRole);
            user.setRoles(userRoleList);
            user.setUsername(name);
            user.setPassword(CipherUtils.encrypt(password));
            user.setEmailAddress(email);
            return userService.addUser(user);
        }
    }

    private String updateUser(String userId, JSONObject formDataJson) {
        User user;
        Integer updateId = null;
        if (userId != null && userId.length() > 0) {
            updateId = Integer.parseInt(userId);
        }
        String password = formDataJson.getString("password");
        String email = formDataJson.getString("email");
        String enabled = formDataJson.getString("enabled");
        JSONArray roleId = formDataJson.optJSONArray("roleIds");

        if (updateId == null) {
            throw new FormValidationException("User Id is null. Data cannot be updated");
        }
        user = userService.findUser(updateId);
        if (user == null) {
            throw new OperationFailedException("The user does not exists");
        }
        if(!user.getIsExternallyAuthenticated()) {
            if ("".equalsIgnoreCase(password) || password.trim().length() < 0) {
                user.setPassword(CipherUtils.encrypt(CipherUtils.decrypt(user.getPassword())));
            } else {
                user.setPassword(CipherUtils.encrypt(CipherUtils.decrypt(password)));
            }
        }
        if ("TRUE".equalsIgnoreCase(enabled)) {
            user.setEnabled(true);
        } else if ("FALSE".equalsIgnoreCase(enabled)) {
            user.setEnabled(false);
        }
        user.setEmailAddress(email);
        String message = "";
        if (roleId != null && roleId.size() > 0) {
            List<Role> userRoleList = new ArrayList<>();
            for (int i = 0; i < roleId.size(); i++) {
                Role userRole = roleService.getRole(roleId.getInt(i));
                userRoleList.add(userRole);
            }
            user.setRoles(userRoleList);
        } else if (roleId != null) {
            throw new FormValidationException("User not updated, Please choose at least one role");
        }
        userService.editUser(user);
        message = "User updated successfully. " + message;
        return message;
    }

    private String deleteUser(String userId) {
        int currentUserId = rolesAccessComponent.getLoggedInUserId();
        int deleteId;
        if (userId != null && userId.length() > 0) {
            deleteId = Integer.parseInt(userId);
        } else {
            throw new FormValidationException("User id is null.");
        }
        if (currentUserId == deleteId) {
            throw new OperationFailedException("Cannot delete active user.");
        } else {
            userService.deleteUser(deleteId);
        }
        return "User deleted successfully";
    }

    /**
     * This service is responsible for profile relaed activity like creating,
     * updating and deleting profiles depends on action type
     *
     * @param request  This action and formData parameter is retrieved from request object
     * @param response The response contains json object depending on the action (add, update, delete)
     * @throws IOException Exception
     */
    @RequestMapping(value = "/admin/profiles", method = {RequestMethod.POST, RequestMethod.GET})
    public void getProfileService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        String action = request.getParameter("action");
        String formData = request.getParameter("formData");
        JSONObject formDataJson = JSONObject.fromObject(formData);
        JSONObject result = new JSONObject();
        Profile profileObj = new Profile();
        try {
            if (StringUtils.isEmpty(action)) {
                String userId = request.getParameter("userId");
                Integer uId;
                if (userId != null && userId.length() > 0) {
                    uId = Integer.parseInt(userId);
                } else {
                    throw new FormValidationException("User Id cannot be null");
                }
                result = AdminUtils.getUserProfile(profileService, uId);
            } else {
                String profileId = request.getParameter("id");
                if ("add".equalsIgnoreCase(action)) {
                    result.accumulate("message", "Profile added successfully.");
                    result.accumulate("id", addProfile(formDataJson, profileObj));
                } else if ("update".equalsIgnoreCase(action)) {

                    result.accumulate("message", updateProfile(formDataJson, profileId));
                } else if ("delete".equalsIgnoreCase(action)) {
                    result.accumulate("message", deleteProfile(profileId));
                }
                result = ResponseUtils.handleSuccessMassage(result);
            }
            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    private int addProfile(JSONObject formDataJson, Profile profileObj) {
        String name = formDataJson.getString("name");
        String value = formDataJson.getString("value");
        String profileUserId = formDataJson.getString("id");
        if (profileUserId == null || profileUserId.length() == 0) {
            throw new FormValidationException("User Id cannot be null");
        }
        int userId = Integer.parseInt(profileUserId);
        AdminUtils.checkNullsForProfile(formDataJson);
        if (AdminUtils.checkForDuplicateProfile(profileService, name, userId)) {
            throw new OperationFailedException("Profile already exists for the user");
        } else {
            profileObj.setProfile_name(name);
            profileObj.setProfile_value(value);
            profileObj.setUser_id(userId);
            return profileService.add(profileObj);
        }
    }

    @NotNull
    private String updateProfile(JSONObject formDataJson, String profileId) {
        Profile profileObj;
        String result;
        Integer profileUpdateId;
        if (profileId != null && profileId.length() > 0) {
            profileUpdateId = Integer.parseInt(profileId);
        } else {
            throw new FormValidationException("Profile Id cannot be null");
        }

        AdminUtils.checkNullsForProfile(formDataJson);

        String profileName = formDataJson.getString("name");

        String profileValue = formDataJson.getString("value");
        profileObj = profileService.getProfile(profileUpdateId);
        Integer userId = profileObj.getUser_id();
        String profile_name = profileObj.getProfile_name();
        if (!profile_name.equals(profileName)) {
            List<Profile> profileList = profileService.getProfileListByNameAndUserId(profileName, userId);
            if (AdminUtils.checkForDuplicateProfile(profileService, profileName, userId) && profileList != null &&
                    profileList.size() > 1) {
                throw new OperationFailedException("Profile already exists for the user");
            }
        }

        profileObj.setProfile_name(profileName);
        profileObj.setProfile_value(profileValue);
        profileService.edit(profileObj);
        result = "Profile updated successfully";
        return result;
    }

    @NotNull
    private String deleteProfile(String profileId) {
        Integer deleteProfile;
        if (profileId != null && profileId.length() > 0) {
            deleteProfile = Integer.parseInt(profileId);
        } else {
            throw new FormValidationException("Profile Id cannot be null");
        }
        profileService.delete(deleteProfile);
        return "Profile successfully deleted";
    }


    /**
     * This service is responsible for doing organisation activity like
     * creating, updating and deleting organisation depends on action type from
     * front end
     *
     * @param request  HTTPRequest contains the action(action parameter is optional). If action is provided it should
     *                 be add or update or delete
     * @param response HTTPResponse
     * @throws IOException Exception
     */
    @RequestMapping(value = "/admin/organisations", method = {RequestMethod.GET, RequestMethod.POST})
    public void getOrganizationService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        String action = request.getParameter("action");
        String formData = request.getParameter("formData");
        JSONObject formDataJson = JSONObject.fromObject(formData);
        Organization organizationObj = new Organization();
        Integer orgId = rolesAccessComponent.getLoggedInUserOrgId();
        JSONObject result = new JSONObject();
        try {
            if (StringUtils.isEmpty(action)) {
                result = whenActionIsEmpty(request, orgId);

            } else {
                String organizationId = request.getParameter("id");
                if ("add".equalsIgnoreCase(action)) {
                    AdminUtils.checkNullsForOrganization(formDataJson);
                    result.accumulate("message", "Organization added successfully");
                    int orgIdNew = addOrganization(organizationObj, formDataJson, orgId);
                    Organization organization = organizationService.findOrganization(orgIdNew, new LimitOffsetModel());
                    JSONObject anOrganization = AdminUtils.getAnOrganization(organization);
                    result.accumulate("id", orgIdNew);
                    result.accumulate("data",anOrganization);
                } else if ("update".equalsIgnoreCase(action)) {
                    result.accumulate("message", updateOrganization(formDataJson, organizationId, orgId));
                } else if ("delete".equalsIgnoreCase(action)) {
                    result.accumulate("message", deleteOrganization(organizationId, orgId));
                }
                result = ResponseUtils.handleSuccessMassage(result);
            }
            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    private JSONObject whenActionIsEmpty(HttpServletRequest request, Integer orgId) {
        JSONObject result;
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel(request);
        result = AdminUtils.getOrganizationJson(organizationService, orgId, limitOffsetModel);
        return result;
    }

    private int addOrganization(Organization organizationObj, JSONObject formDataJson, Integer orgId) {
        String name = formDataJson.getString("name");
        String description = formDataJson.getString("description");
        if (description.startsWith("\"null")) {
            description = description.replace("\"null\"", "null");
        }
        if (orgId == null) {
            if (AdminUtils.checkForDuplicateOrganization(organizationService, name)) {
                throw new OperationFailedException("Organization already exists");
            }
            organizationObj.setOrg_name(name);
            organizationObj.setOrg_desc(description);
            int id = organizationService.add(organizationObj);
            roleService.add(new Role(this.namesConfigurer.getRoleAdmin(), id));
            roleService.add(new Role(this.namesConfigurer.getRoleUser(), id));
            return id;
        } else {
            throw new OperationFailedException("You have no enough permission to add organization");
        }
    }

    @NotNull
    private String updateOrganization(JSONObject formDataJson, String organizationId, Integer orgId) {
        Organization organizationObj;
        String result;
        if (orgId == null) {
            Integer organizationUpdateId;
            if (organizationId != null && organizationId.length() > 0) {
                organizationUpdateId = Integer.parseInt(organizationId);
            } else {
                throw new FormValidationException("Organization id is null");
            }

            String organizationName = formDataJson.getString("name");
            String organizationDescription = formDataJson.getString("description");

            organizationObj = organizationService.getOrganization(organizationUpdateId);
            organizationObj.setOrg_name(organizationName);
            organizationObj.setOrg_desc(organizationDescription);
            organizationService.edit(organizationObj);

            result = "Organization updated successfully";
        } else {
            throw new OperationFailedException("You have no enough permission to edit " + "organization");

        }
        return result;
    }

    private String deleteOrganization(String organizationId, Integer orgId) {
        String result = "";
        Integer deleteOrganization;
        if (orgId == null) {
            if (organizationId != null && organizationId.length() > 0) {
                deleteOrganization = Integer.parseInt(organizationId);
                Organization organization =  organizationService.getOrganization(deleteOrganization);
                if(Boolean.TRUE.equals(organization.isDeleted())) {
	                userService.getUserByOrgIdNdelete(deleteOrganization);
	                try {
	                    roleService.deleteOrganization(deleteOrganization);
	                    organizationService.delete(deleteOrganization);
	                } catch (Exception me) {
	                    if (me instanceof ConstraintViolationException) {
	                        throw new OperationFailedException("Cannot delete organization. Active " +
	                                "user's organization may have role associated with this " +
	                                "organization");
	                    }
	                }
                }
                else {
                	organizationService.delete(deleteOrganization);
                }
                result = "Organization deleted successfully ";
            }
        } else {
            throw new OperationFailedException("You have no enough permission to delete " + "organization");
        }
        return result;
    }

    /**
     * This service is used to handle the Users Role related CRUD operations
     *
     * @param request  the request object has action and formData as the action can be add, edit or delete
     *                 the id parameter is required for editing and deleting operations.
     * @param response The response is a json object that contains status and message.
     * @throws IOException
     */

    @RequestMapping(value = "/admin/roles", method = {RequestMethod.POST, RequestMethod.GET})
    public void getRoleService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        String action = request.getParameter("action");
        String formData = request.getParameter("formData");
        JSONObject formDataJson = JSONObject.fromObject(formData);
        Role roleObj = new Role();
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel(request);
        JSONObject result = new JSONObject();
        Integer userOrgId = rolesAccessComponent.getLoggedInUserOrgId();
        try {
            if (StringUtils.isEmpty(action)) {
                String orgId = request.getParameter("id");
                result = searchRole(limitOffsetModel, userOrgId, orgId);
            } else {
                if ("add".equalsIgnoreCase(action)) {
                    result.accumulate("message", "Role added successfully");
                    String value = addNewRole(formDataJson, roleObj);
                    Role role = roleService.getRole(Integer.valueOf(value));
                    JSONObject aRole = AdminUtils.getARole(role);
                    result.accumulate("id", value);
                    result.accumulate("data",aRole);
                    String org = formDataJson.getString("organisation");
                    Integer orgId = Integer.valueOf(org);
                    if (orgId > 0) {
                        result.accumulate("orgName", organizationService.getOrganization(orgId).getOrg_name());
                    } else {
                        result.accumulate("orgName", ApplicationProperties.getInstance().getNullValue());
                    }


                } else if ("userRoles".equalsIgnoreCase(action)) {
                    String userId = request.getParameter("userId");
                    String[] roleId = request.getParameterValues("roleIds[]");
                    result.accumulate("message", updateRole(userId, roleId));
                } else if ("delete".equalsIgnoreCase(action)) {
                    result.accumulate("message", deleteRole(formDataJson, roleObj));
                }
                result = ResponseUtils.handleSuccessMassage(result);
            }
            ControllerUtils.handleSuccess(response, isAjax, result.toString());
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    private JSONObject searchRole(LimitOffsetModel limitOffsetModel, Integer userOrgId, String orgId) {
        Integer org_id;
        if (userOrgId != null) {
            org_id = userOrgId;
        } else {
            org_id = (orgId != null) ? Integer.parseInt(orgId) : null;
        }

        return AdminUtils.getOrganizationRoles(roleService, org_id, limitOffsetModel);
    }

    @NotNull
    private String addNewRole(JSONObject formDataJson, Role roleObj) {
        String result;
        String name = formDataJson.getString("name");
        String organisation = formDataJson.getString("organisation");
        if ((name == null) || ("".equalsIgnoreCase(name)) || (name.trim().length() < 0)) {
            throw new RequiredParameterIsNullException(String.format("The parameter %s is" +
                    " " + "null or empty." + " Invalid request.", name));
        } else {
            Integer orgId;
            if (organisation != null && organisation.trim().length() > 0) {
                orgId = Integer.parseInt(organisation);
                if (orgId < 0) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    Principal currentUser = (Principal) auth.getPrincipal();
                    orgId = currentUser.getLoggedInUser().getOrg_id();
                }
            } else {
                orgId = null;
            }


            if (AdminUtils.checkForDuplicateRole(roleService, name, orgId)) {
                throw new OperationFailedException("Role already exists");
            } else {

                roleObj.setOrg_id(orgId);
                roleObj.setRole_name(name);
                int id = roleService.add(roleObj);
                result = "" + id;
            }
        }
        return result;
    }

    @NotNull
    private String updateRole(String userId, String[] roleId) {
        String result;
        int length = 0;
        if (roleId != null) {
            length = roleId.length;

        }
        if (length == 0) {
            throw new RequiredParameterIsNullException("Please select  at least one  role");
        } else {
            Integer userUpdateId;
            if (userId != null && userId.trim().length() > 0) {
                userUpdateId = Integer.parseInt(userId);
            } else {
                throw new FormValidationException("Role Id cannot be null");
            }
            User user = userService.findUser(userUpdateId);
            List<Role> userRoleList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Role userRole = roleService.getRole(Integer.parseInt(roleId[i]));
                userRoleList.add(userRole);
                if (user != null) {
                    user.setRoles(userRoleList);
                } else {
                    throw new OperationFailedException("User does not exists");
                }
            }
            userService.editUser(user);
            result = "Role updated successfully";
        }
        return result;
    }

    @NotNull
    private String deleteRole(JSONObject formDataJson, Role roleObj) {
        String id = formDataJson.getString("id");
        if ((id == null) || ("".equalsIgnoreCase(id)) || (id.trim().length() <= 0)) {
            throw new RequiredParameterIsNullException(String.format("The parameter %s is" +
                    " " + "null or empty." + " Invalid request.", id));
        } else {
            int roleId = Integer.parseInt(id);
            if (AuthenticationUtils.getUserRolesIds().contains(id)) {
                logger.info("Attempted to delete the active role");
                throw new OperationFailedException("Active users role cannot be deleted");
            }
            roleObj.setId(roleId);
            roleService.delete(roleId);
            return "Role deleted successfully";
        }
    }


}