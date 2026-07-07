package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.resourcedb.processor.model.Pair;
import com.helicalinsight.resourcesecurity.model.Organization;
import com.helicalinsight.resourcesecurity.model.Role;
import com.helicalinsight.resourcesecurity.model.User;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by author on 13-01-2015.
 *
 * @author Rajasekhar
 */
public final class ShareRuleHelperDB {

    private static final Logger logger = LoggerFactory.getLogger(ShareRuleHelperDB.class);

    static boolean isShareTagPresent(@NotNull Map<String,Object> resourceMap) {
        return resourceMap.containsKey("share");
    }

    static boolean isResourceShareTagPresent(@NotNull HIResourceSecurityDB hiResourceSecurity) {
        return hiResourceSecurity!=null;
    }

    static boolean isResourceSharedWithOtherRoles(Map<String,List<Pair>> shareMap) {
        return shareMap.containsKey("roles");
    }

    static boolean isResourceSharedWithUsers(Map<String,List<Pair>> shareMap) {
        return shareMap.containsKey("users");
    }

    static boolean isResourceSharedWithOrganizations(Map<String,List<Pair>> shareMap) {
        return shareMap.containsKey("organizations");
    }

    @NotNull
    public static List<Pair> getSharedRoles(Map<String,List<Pair>> shareMap) {
        List<Pair> rolePair=shareMap.get("roles");
        /*List<Role> roleList = new ArrayList<>();
        try {
            roleList.add(getRolePojo(shareMap.get("roles")));
        } catch (JSONException ignore) {
            JSONArray rolesJson = listOfRolesJson.getJSONArray("role");
            Iterator<?> iterator = rolesJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                roleList.add(getRolePojo((JSONObject) iterator.next()));
            }
        }*/
        return rolePair;
    }

    @NotNull
    private static Role getRolePojo(@NotNull JSONObject roleJson) {
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

    private static int getPermissionLevel(@NotNull JSONObject json) {
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

    @NotNull
    public static List<Pair> getSharedUsers(Map<String,List<Pair>> shareMap) {
        List<Pair> userPair=shareMap.get("users");
       /* List<User> userList = new ArrayList<>();
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
        return userList;*/
        return userPair;
    }

    @NotNull
    private static User getUserPojo(@NotNull JSONObject userJson) {
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

    @NotNull
    public static List<Pair> getSharedOrganizations(Map<String,List<Pair>> shareMap) {
        List<Pair> orgPair = shareMap.get("organizations");
       /* List<Organization> organizationsList = new ArrayList<>();
        try {
            //Think there is only one organization tag.
            final Object object = organizations.get("organization");
            JSONObject organization = null;
            if (object instanceof JSONArray) {
                throw new JSONException();
                //Oops. There are multiple organization tags
                //Go to catch block
            } else if (object instanceof JSONObject) {
                organization = (JSONObject) object;
            }
            if (organization == null) {
                //Found an organization tag with no id. So it's a string with just permission level
                addPojo((String) object, organizationsList);
            } else {
                organizationsList.add(getOrganizationPojo(organization));
            }
        } catch (JSONException ignore) {
            JSONArray organizationsJson = organizations.getJSONArray("organization");
            Iterator<?> iterator = organizationsJson.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if (next instanceof JSONObject) {
                    organizationsList.add(getOrganizationPojo((JSONObject) next));
                    continue;
                }
                addPojo((String) next, organizationsList);
            }
        }
        return organizationsList;*/
        return orgPair;
    }

    private static void addPojo(final String accessLevel, @NotNull List<Organization> organizationsList) {
        Organization organization = new Organization();
        organization.setPermissionLevel(Integer.parseInt(accessLevel));
        organization.setOrganizationId(null);
        organizationsList.add(organization);
    }

    @NotNull
    private static Organization getOrganizationPojo(@NotNull JSONObject organization) {
        Organization organizationPojo = new Organization();
        organizationPojo.setOrganizationId(organization.getString("@id"));
        organizationPojo.setPermissionLevel(getPermissionLevel(organization));
        return organizationPojo;
    }
}
