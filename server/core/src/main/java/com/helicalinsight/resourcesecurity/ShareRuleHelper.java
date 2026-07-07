package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.resourcesecurity.model.Organization;
import com.helicalinsight.resourcesecurity.model.Role;
import com.helicalinsight.resourcesecurity.model.User;
import org.jetbrains.annotations.NotNull;
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

    static boolean isShareTagPresent(@NotNull JsonObject fileAsJson) {
        return fileAsJson.has("share");
    }

    static boolean isResourceShareTagPresent(@NotNull HIResourceSecurityDB hiResourceSecurity) {
        return hiResourceSecurity!=null;
    }

    static boolean isResourceSharedWithOtherRoles(@NotNull JsonObject shareJson) {
        return shareJson.has("roles");
    }

    static boolean isResourceSharedWithUsers(@NotNull JsonObject shareJson) {
        return shareJson.has("users");
    }

    static boolean isResourceSharedWithOrganizations(@NotNull JsonObject shareJson) {
        return shareJson.has("organizations");
    }

    @NotNull
    public static List<Role> getSharedRoles(@NotNull JsonObject listOfRolesJson) {
        List<Role> roleList = new ArrayList<>();
        try {
            roleList.add(getRolePojo(listOfRolesJson.getAsJsonObject("role")));
        } catch (JsonSyntaxException ignore) {
            JsonArray rolesJson = listOfRolesJson.getAsJsonArray("role");
            Iterator<?> iterator = rolesJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                roleList.add(getRolePojo((JsonObject) iterator.next()));
            }
        }
        return roleList;
    }

    @NotNull
    private static Role getRolePojo(@NotNull JsonObject roleJson) {
        Role role = new Role();
        //Set name if available
        if (roleJson.has("name")) {
            role.setRoleName(roleJson.get("name").getAsString());
        } else {
            role.setRoleName(null);
        }

        //Set id if available
        if (roleJson.has("id")) {
            role.setRoleId(roleJson.get("id").getAsString());
        } else {
            role.setRoleId(null);
        }

        role.setPermissionLevel(getPermissionLevel(roleJson));
        return role;
    }

    private static int getPermissionLevel(@NotNull JsonObject json) {
        int permissionLevel = -1;
        try {
            permissionLevel = Integer.parseInt(json.get("").getAsString());
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("One of the file's permission level was not number.", ex);
            }
        }
        return permissionLevel;
    }

    @NotNull
    public static List<User> getSharedUsers(@NotNull JsonObject listOfUsersJson) {
        List<User> userList = new ArrayList<>();
        try {
            userList.add(getUserPojo(listOfUsersJson.getAsJsonObject("user")));
        } catch (JsonSyntaxException ignore) {
            JsonArray usersJson = listOfUsersJson.getAsJsonArray("user");
            Iterator<?> iterator = usersJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                userList.add(getUserPojo((JsonObject) iterator.next()));
            }
        }
        return userList;
    }

    @NotNull
    private static User getUserPojo(@NotNull JsonObject userJson) {
        User user = new User();
        if (userJson.has("name")) {
            user.setUsername(userJson.get("name").getAsString()
            		);
        } else {
            user.setUsername(null);
        }

        if (userJson.has("id")) {
            user.setUserId(userJson.get("id").getAsString());
        } else {
            user.setUserId(null);
        }

        user.setPermissionLevel(getPermissionLevel(userJson));
        return user;
    }

    @NotNull
    public static List<Organization> getSharedOrganizations(@NotNull JsonObject organizations) {
        List<Organization> organizationsList = new ArrayList<>();
        try {
            //Think there is only one organization tag.
            final Object object = organizations.get("organization");
            JsonObject organization = null;
            if (object instanceof JsonArray) {
                throw new JsonSyntaxException("There are multiple organization tags");
                //Oops. There are multiple organization tags
                //Go to catch block
            } else if (object instanceof JsonObject) {
                organization = (JsonObject) object;
            }
            if (organization == null) {
                //Found an organization tag with no id. So it's a string with just permission level
                addPojo((String) object, organizationsList);
            } else {
                organizationsList.add(getOrganizationPojo(organization));
            }
        } catch (JsonSyntaxException ignore) {
            JsonArray organizationsJson = organizations.getAsJsonArray("organization");
            Iterator<?> iterator = organizationsJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if (next instanceof JsonObject) {
                    organizationsList.add(getOrganizationPojo((JsonObject) next));
                    continue;
                }
                addPojo((String) next, organizationsList);
            }
        }
        return organizationsList;
    }

    private static void addPojo(final String accessLevel, @NotNull List<Organization> organizationsList) {
        Organization organization = new Organization();
        organization.setPermissionLevel(Integer.parseInt(accessLevel));
        organization.setOrganizationId(null);
        organizationsList.add(organization);
    }

    @NotNull
    private static Organization getOrganizationPojo(@NotNull JsonObject organization) {
        Organization organizationPojo = new Organization();
        organizationPojo.setOrganizationId(organization.get("id").getAsString());
        organizationPojo.setPermissionLevel(getPermissionLevel(organization));
        return organizationPojo;
    }
}
