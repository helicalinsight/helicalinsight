package com.helicalinsight.resourcesecurity;

import com.helicalinsight.admin.service.OrganizationService;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;

import java.util.ListIterator;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareServiceImplDB implements IComponent {

    private final RolesAccessComponent rolesAccessComponent = ApplicationContextAccessor.getBean(RolesAccessComponent
            .class);

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        JSONObject model;
        model = new JSONObject();

        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();

        //Get logged in user organization
        Integer loggedInUserOrganizationId = getLoggedInUserOrganizationId();

        JSONObject provideJson;

        if (!formData.has("provide")) {
            //Can't do anything
            throw new IncompleteFormDataException("The parameter provide should be in the " + "form of a json object.");
        } else {
            provideJson = formData.getJSONObject("provide");
        }

        boolean superOrganizationUser = AuthenticationUtils.isSuperOrganizationUser();

        String id = null;//Id should always be null in request except for super organization user
        if (superOrganizationUser) {
            if (provideJson.has("id")) {
                id = provideJson.getString("id");
            }

            if (id == null) {
                //No need of all organizations details. Only Super organization details.
                //Provide only super organization users and roles and return.
                prepareModelWithSuperOrgUsersAndRoles(model, limitOffsetModel, provideJson);
                return model.toString();
            } else if ("all".equalsIgnoreCase(id)) {
                //Provide all organizations, users and roles and return.

                //Fixed 16-07-2015
                //Fixed to provide flexibility and reduce the load on server if
                //organizations are not required in case if only users or roles
                //are requested.
                if (provideJson.has("provideOrganizations") && "true".equalsIgnoreCase(provideJson.getString
                        ("provideOrganizations"))) {
                    includeOrganizations(model, limitOffsetModel);//All organizations.
                }
                prepareModel(model, limitOffsetModel, null, provideJson, null);
            } else {
                //For a specific organization. No need of all organizations. Only
                //specific roles and users. So pass the id.
                prepareModel(model, limitOffsetModel, null, provideJson, id);
            }

            return model.toString();
        } else {//Tenant ROLE_ADMIN or ROLE_USER
            if (provideJson.has("id")) {
                throw new AccessDeniedException("Requested with id. Organization level access is " +
                        "" + "denied. Only request for users and roles of your organization is " +
                        "granted.");
            }
            String orgId = loggedInUserOrganizationId + "";
            prepareModel(model, limitOffsetModel, loggedInUserOrganizationId, provideJson, orgId);
            return model.toString();
        }
    }

    public Integer getLoggedInUserOrganizationId() {
        return this.rolesAccessComponent.getLoggedInUserOrgId();
    }

    private void prepareModelWithSuperOrgUsersAndRoles(@NotNull JSONObject model,
                                                       @NotNull LimitOffsetModel limitOffsetModel,
                                                       @NotNull JSONObject provideJson) {
        if (provideJson.has("provideRoles") && "true".equalsIgnoreCase(provideJson.getString("provideRoles"))) {
            RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
            JSONObject rolesJson = AdminUtils.getSuperOrganizationRoles(roleService);
            model.accumulate("allRoles", rolesJson);
        }

        if (provideJson.has("provideUsers") && "true".equalsIgnoreCase(provideJson.getString("provideUsers"))) {
            UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
            limitOffsetModel.setSearchOn("organisation");
            limitOffsetModel.setSearchPhrase(ApplicationProperties.getInstance().getNullValue());
            limitOffsetModel.setOnlySuperOrganization(true);
            JSONObject usersJson = AdminUtils.getUsersJson(null, userService, limitOffsetModel);
            model.accumulate("allUsers", discardLoggedInUser(usersJson));
        }
    }

    public void includeOrganizations(@NotNull JSONObject model, LimitOffsetModel limitOffsetModel) {
        OrganizationService organizationService = ApplicationContextAccessor.getBean(OrganizationService.class);
        JSONObject organizationJson = AdminUtils.getOrganizationJson(organizationService, null, limitOffsetModel);

        model.accumulate("allOrganizations", organizationJson);
    }

    public void prepareModel(@NotNull JSONObject model, @NotNull LimitOffsetModel limitOffsetModel,
                             Integer loggedInUserOrganizationId, @NotNull JSONObject provideJson,
                             String requestedOrgId) {
        includeRoles(provideJson, model, rolesAccessComponent, requestedOrgId, limitOffsetModel);

        includeUsers(provideJson, model, loggedInUserOrganizationId, limitOffsetModel);
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

    private void includeRoles(@NotNull JSONObject provideJson, @NotNull JSONObject model,
                              @NotNull RolesAccessComponent rolesAccessComponent, String requestedOrgId,
                              @NotNull LimitOffsetModel limitOffsetModel) {
        if (provideJson.has("provideRoles") && "true".equalsIgnoreCase(provideJson.getString("provideRoles"))) {
            limitOffsetModel.setRequestedWithSpecificOrganization(true);
            JSONObject rolesJson = rolesAccessComponent.allRoles(requestedOrgId, limitOffsetModel);
            model.accumulate("allRoles", rolesJson);
        }
    }

    private void includeUsers(@NotNull JSONObject provideJson, @NotNull JSONObject model,
                              Integer loggedInUserOrganizationId, @NotNull LimitOffsetModel limitOffsetModel) {
        if (provideJson.has("provideUsers") && "true".equalsIgnoreCase(provideJson.getString("provideUsers"))) {
            UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
            limitOffsetModel.setRequestedWithSpecificOrganization(true);
            JSONObject usersJson = AdminUtils.getUsersJson(loggedInUserOrganizationId, userService, limitOffsetModel);
            model.accumulate("allUsers", discardLoggedInUser(usersJson));
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
