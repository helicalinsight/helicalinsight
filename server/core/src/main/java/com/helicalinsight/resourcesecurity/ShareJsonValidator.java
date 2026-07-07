package com.helicalinsight.resourcesecurity;

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by author on 15-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareJsonValidator {

    @NotNull
    private final Set<String> userIds = new HashSet<>();

    @NotNull
    private final Set<String> userNames = new HashSet<>();

    @NotNull
    private final Set<String> roleIds = new HashSet<>();

    @NotNull
    private final Set<String> roleNames = new HashSet<>();

    @NotNull
    private final Set<String> organizationIds = new HashSet<>();

    public ShareJsonValidator() {
        //Set the json that has to be validated.

        ShareServiceImpl shareService = new ShareServiceImpl();
        Integer loggedInUserOrganizationId = shareService.getLoggedInUserOrganizationId();

        JSONObject provideJson = new JSONObject();
        provideJson.accumulate("provideUsers", "true");

        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();

        boolean superOrganizationUser = AuthenticationUtils.isSuperOrganizationUser();
        JSONObject model = new JSONObject();

        setRoleNamesAndIds();

        if (superOrganizationUser) {
            setOrganizationIds(shareService, limitOffsetModel, model);

            shareService.prepareModel(model, limitOffsetModel, null, provideJson, null);
            setUserNamesAndIds(model);
        } else {
            shareService.prepareModel(model, limitOffsetModel, loggedInUserOrganizationId, provideJson,
                    loggedInUserOrganizationId + "");
            setUserNamesAndIds(model);
        }
    }

    private void setRoleNamesAndIds() {
        RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
        List<Role> allRoles = roleService.getAllRoles();

        for (Role role : allRoles) {
            this.roleIds.add(String.valueOf(role.getId()));
            this.roleNames.add(String.valueOf(role.getRole_name()));
        }
    }

    private void setOrganizationIds(ShareServiceImpl shareService, LimitOffsetModel limitOffsetModel,
                                    JSONObject model) {
        shareService.includeOrganizations(model, limitOffsetModel);
        final JSONObject organizations = model.getJSONObject("allOrganizations");
        Object allOrganizations = organizations.get("organisations");
        if (allOrganizations instanceof JSONArray) {
            for (Object object : (JSONArray) allOrganizations) {
                JSONObject organization = JSONObject.fromObject(object);
                if (organization.has("id")) {
                    String id = organization.getString("id");
                    this.organizationIds.add(id);
                }
            }
        }
    }

    private void setUserNamesAndIds(@NotNull JSONObject model) {
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
        validateOrganization(jsonObject);
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

    private void validateOrganization(JSONObject jsonObject) {
        if (jsonObject.has("organization")) {
            JSONArray organizations = jsonObject.getJSONArray("organization");
            for (Object object : organizations) {
                JSONObject json = JSONObject.fromObject(object);
                if (json.has("id")) {
                    String id = json.getString("id");
                    if (!(this.organizationIds.contains(id))) {
                        throw new InvalidDataException("The requested organization id " + id +
                                " does not belong to any organization. Access Denied.");
                    }
                }
            }
        }
    }
}
