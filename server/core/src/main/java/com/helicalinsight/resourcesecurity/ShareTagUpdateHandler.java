package com.helicalinsight.resourcesecurity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareTagUpdateHandler {

    private final List<String> permissions;

    public ShareTagUpdateHandler() {
        permissions = new PermissionConstants().allowedPermissions();
    }
    /**
     * updateShareTag
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link ShareTagUpdateHandler#updateShareTag( Security.Share share, JsonObject shareWith)}  instead</p>
     * @param share
     * @param shareWith
     * @return
     */
    @Deprecated
    @Nullable
    public Security.Share updateShareTag(@Nullable Security.Share share, @NotNull JSONObject shareWith) {
        if (share == null) {//Create a new one if the xml is empty
            share = ApplicationContextAccessor.getBean(Security.Share.class);
        }

        Security.Organizations organizations = share.getOrganizations();
        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();

        updateOrganizations(share, shareWith, organizations);

        updateRoles(share, shareWith, roles);

        updateUsers(share, shareWith, users);
        return share;
    }
    /**
     * using gson
     * pdateShareTag(@Nullable Security.Share share, @NotNull JsonObject shareWith)
     * @param share
     * @param shareWith
     * @return
     */
    @Nullable
    public Security.Share updateShareTag(@Nullable Security.Share share, @NotNull JsonObject shareWith) {
        if (share == null) {//Create a new one if the xml is empty
            share = ApplicationContextAccessor.getBean(Security.Share.class);
        }

        Security.Organizations organizations = share.getOrganizations();
        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();

        updateOrganizations(share, shareWith, organizations);

        updateRoles(share, shareWith, roles);

        updateUsers(share, shareWith, users);
        return share;
    }
    /**
     * updateOrganizations
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link ShareTagUpdateHandler#updateShareTag( Security.Share share, JsonObject shareWith,Security.Organizations organizations)}  instead</p>
     * @param share
     * @param shareWith
     * @param organizations
     * @return
     */
    @Deprecated
    private void updateOrganizations(@NotNull Security.Share share, @NotNull JSONObject shareWith,
                                     @Nullable Security.Organizations organizations) {
        List<Security.Organization> organizationsList;
        if (shareWith.has("organization")) {
            //Only super organization users can successfully complete this request.
            checkWhetherAuthenticated();
            JSONArray organizationsArray = shareWith.getJSONArray("organization");
            if (organizations != null) {
                organizationsList = organizations.getOrganizations();
                if (organizationsList == null) {
                    organizationsList = new ArrayList<>();
                }
                setValuesFromOrganizationJson(share, organizations, organizationsList, organizationsArray);
            } else {
                organizations = ApplicationContextAccessor.getBean(Security.Organizations.class);
                organizationsList = new ArrayList<>();
                setValuesFromOrganizationJson(share, organizations, organizationsList, organizationsArray);
            }
        }
    }
    /**
     * using gson
     * 
     * @param share
     * @param shareWith
     * @param organizations
     */
	private void updateOrganizations(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Organizations organizations) {
		List<Security.Organization> organizationsList;
		if (shareWith.has("organization")) {
			//Only super organization users can successfully complete this request.
			checkWhetherAuthenticated();
			JsonArray organizationsArray = shareWith.getAsJsonArray("organization");
			if (organizations != null) {
				organizationsList = organizations.getOrganizations();
				if (organizationsList == null) {
					organizationsList = new ArrayList<>();
				}
				setValuesFromOrganizationJson(share, organizations, organizationsList, organizationsArray);
			} else {
				organizations = ApplicationContextAccessor.getBean(Security.Organizations.class);
				organizationsList = new ArrayList<>();
				setValuesFromOrganizationJson(share, organizations, organizationsList, organizationsArray);
			}
		}
	}
	/**
	 * updateRoles
	 * @deprecated
	 * This method is no longer acceptable
	 * <p>use {@link ShareTagUpdateHandler#updateRoles( Security.Share share,  JsonObject shareWith,
                              Security.Roles roles)} instead</p>
	 * @param share
	 * @param shareWith
	 * @param roles
	 */
	@Deprecated
    private void updateRoles(@NotNull Security.Share share, @NotNull JSONObject shareWith,
                             @Nullable Security.Roles roles) {
        List<Security.Role> rolesList;
        if (shareWith.has("role")) {
            JSONArray roleArray = shareWith.getJSONArray("role");
            if (roles != null) {
                rolesList = roles.getRoles();
                if (rolesList == null) {
                    rolesList = new ArrayList<>();
                }
                setValuesFromRoleJson(share, roles, rolesList, roleArray);
            } else {
                roles = ApplicationContextAccessor.getBean(Security.Roles.class);
                rolesList = new ArrayList<>();
                setValuesFromRoleJson(share, roles, rolesList, roleArray);
            }
        }
    }
	/**
	 * using gson
	 * updateRoles(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Roles roles)
	 * @param share
	 * @param shareWith
	 * @param roles
	 */
	private void updateRoles(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Roles roles) {
		List<Security.Role> rolesList;
		if (shareWith.has("role")) {
			JsonArray roleArray = shareWith.getAsJsonArray("role");
			if (roles != null) {
				rolesList = roles.getRoles();
				if (rolesList == null) {
					rolesList = new ArrayList<>();
				}
				setValuesFromRoleJson(share, roles, rolesList, roleArray);
			} else {
				roles = ApplicationContextAccessor.getBean(Security.Roles.class);
				rolesList = new ArrayList<>();
				setValuesFromRoleJson(share, roles, rolesList, roleArray);
			}
		}
	}
	/**
	 * updateUsers
	 * @deprecated
	 * This method is no longer acceptable
	 * <p>use {@link ShareTagUpdateHandler#updateUsers( Security.Share share,  JsonObject shareWith,
                              Security.Users users) } instead </p>
	 * @param share
	 * @param shareWith
	 * @param users
	 */
    private void updateUsers(@NotNull Security.Share share, @NotNull JSONObject shareWith,
                             @Nullable Security.Users users) {
        List<Security.User> usersList;
        if (shareWith.has("user")) {
            JSONArray userArray = shareWith.getJSONArray("user");
            if (users != null) {
                usersList = users.getUsers();
                if (usersList == null) {
                    usersList = new ArrayList<>();
                }
                setValuesFromUserJson(share, users, usersList, userArray);
            } else {
                users = ApplicationContextAccessor.getBean(Security.Users.class);
                usersList = new ArrayList<>();
                setValuesFromUserJson(share, users, usersList, userArray);
            }
        }
    }
    /**
     * using gson
     * updateUsers(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Users users)
     * @param share
     * @param shareWith
     * @param users
     */
	private void updateUsers(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Users users) {
		List<Security.User> usersList;
		if (shareWith.has("user")) {
			JsonArray userArray = shareWith.getAsJsonArray("user");
			if (users != null) {
				usersList = users.getUsers();
				if (usersList == null) {
					usersList = new ArrayList<>();
				}
				setValuesFromUserJson(share, users, usersList, userArray);
			} else {
				users = ApplicationContextAccessor.getBean(Security.Users.class);
				usersList = new ArrayList<>();
				setValuesFromUserJson(share, users, usersList, userArray);
			}
		}
	}
    
    public static void checkWhetherAuthenticated() {
        Integer loggedInUserOrganizationId = AuthenticationUtils.getLoggedInUserOrganizationId();

        if (loggedInUserOrganizationId != null) {
            throw new AccessDeniedException("Access denied. Only super organization users " + "can share based on " +
                    "organization.");
        }
    }
    /**
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link ShareTagUpdateHandler#setValuesFromOrganizationJson(Security.Share share, Security.Organizations
            organizations, @NotNull List<Security.Organization> organizationsList,
                                               JsonArray organizationsArray)}  instead</p>
     * @param share
     * @param organizations
     * @param organizationsList
     * @param organizationsArray
     */
    @Deprecated
    private void setValuesFromOrganizationJson(@NotNull Security.Share share, @NotNull Security.Organizations
            organizations, @NotNull List<Security.Organization> organizationsList,
                                               @NotNull JSONArray organizationsArray) {
        Security.Organization organization;
        for (Object object : organizationsArray) {
            JSONObject organizationJson = JSONObject.fromObject(object);
            String permission;
            String id = null;
            try {
                permission = organizationJson.getString("permission");

                validatePermissionLevel(permission);

                if (organizationJson.has("id")) {
                    id = organizationJson.getString("id");
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with " +
                        "organization array. Permission level is expected along with " +
                        "optional id.");
            }

            organization = ApplicationContextAccessor.getBean(Security.Organization.class);
            for(Security.Organization item:organizationsList){
                if (item.getId().equals(id)){
                    organization=item;
                    break;
                }
            }
            if (id != null) {
                organization.setId(id);
            }

            organization.setPermission(permission);


            if (!organizationsList.contains(organization)) {
                organizationsList.add(organization);
            }
        }
        organizations.setOrganizations(organizationsList);
        share.setOrganizations(organizations);
    }
    /**
     * using gson
     * setValuesFromOrganizationJson(@NotNull Security.Share share, @NotNull Security.Organizations
            organizations, @NotNull List<Security.Organization> organizationsList,
                                               @NotNull JsonArray organizationsArray)
     * @param share
     * @param organizations
     * @param organizationsList
     * @param organizationsArray
     */
    private void setValuesFromOrganizationJson(@NotNull Security.Share share, @NotNull Security.Organizations
            organizations, @NotNull List<Security.Organization> organizationsList,
                                               @NotNull JsonArray organizationsArray) {
        Security.Organization organization;
        for (Object object : organizationsArray) {
            JsonObject organizationJson = new Gson().fromJson(object.toString(),JsonObject.class);
            String permission;
            String id = null;
            try {
                permission = organizationJson.get("permission").getAsString();

                validatePermissionLevel(permission);

                if (organizationJson.has("id")) {
                    id = organizationJson.get("id").getAsString();
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with " +
                        "organization array. Permission level is expected along with " +
                        "optional id.");
            }

            organization = ApplicationContextAccessor.getBean(Security.Organization.class);
            for(Security.Organization item:organizationsList){
                if (item.getId().equals(id)){
                    organization=item;
                    break;
                }
            }
            if (id != null) {
                organization.setId(id);
            }

            organization.setPermission(permission);


            if (!organizationsList.contains(organization)) {
                organizationsList.add(organization);
            }
        }
        organizations.setOrganizations(organizationsList);
        share.setOrganizations(organizations);
    }
    /**
     * setValuesFromRoleJson
     * @deprecated
     * This method is no longer acceptable
     * <p>use ShareTagUpdateHandler#setValuesFromRoleJson( Security.Share share,  Security.Roles roles,
                                        List<Security.Role> rolesList,  JsonArray roleArray) instead</p>
     * @param share
     * @param roles
     * @param rolesList
     * @param roleArray
     */
    @Deprecated
    private void setValuesFromRoleJson(@NotNull Security.Share share, @NotNull Security.Roles roles,
                                       @NotNull List<Security.Role> rolesList, @NotNull JSONArray roleArray) {
        Security.Role role;
        for (Object object : roleArray) {
            JSONObject roleJson = JSONObject.fromObject(object);
            String permission;
            String id = null;
            String name = null;
            if (roleJson.has("id") && roleJson.has("name")) {
                malformedJson();
            }

            try {
                permission = roleJson.getString("permission");

                validatePermissionLevel(permission);

                if (roleJson.has("id")) {
                    id = roleJson.getString("id");
                }
                if (roleJson.has("name")) {
                    name = roleJson.getString("name");
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with role" +
                        " array. Permission level is expected along with either " +
                        "name or id.");
            }

            if ((id != null && name != null) || (id == null && name == null)) {
                malformedJson();
            }

            role = ApplicationContextAccessor.getBean(Security.Role.class);
            for(Security.Role item:rolesList){
                if (item.getId().equals(id)){
                    role=item;
                    break;
                }
            }


            role.setPermission(permission);

            if (id != null) {
                role.setId(id);
            }

            if (name != null) {
                role.setName(name);
            }


            if (!rolesList.contains(role)) {
                rolesList.add(role);
            }
        }
        roles.setRoles(rolesList);
        share.setRoles(roles);
    }
    /**
     * using gson
     * setValuesFromRoleJson(@NotNull Security.Share share, @NotNull Security.Roles roles,
			@NotNull List<Security.Role> rolesList, @NotNull JsonArray roleArray)
     * @param share
     * @param roles
     * @param rolesList
     * @param roleArray
     */
	private void setValuesFromRoleJson(@NotNull Security.Share share, @NotNull Security.Roles roles,
			@NotNull List<Security.Role> rolesList, @NotNull JsonArray roleArray) {
		Security.Role role;
		for (Object object : roleArray) {
			JsonObject roleJson = new Gson().fromJson(object.toString(),JsonObject.class);
			String permission;
			String id = null;
			String name = null;
			if (roleJson.has("id") && roleJson.has("name")) {
				malformedJson();
			}

			try {
				permission = roleJson.get("permission").getAsString();

				validatePermissionLevel(permission);

				if (roleJson.has("id")) {
					id = roleJson.get("id").getAsString();
				}
				if (roleJson.has("name")) {
					name = roleJson.get("name").getAsString();
				}
			} catch (Exception ex) {
				throw new MalformedJsonException("Parameter share is malformed with role"
						+ " array. Permission level is expected along with either " + "name or id.");
			}

			if ((id != null && name != null) || (id == null && name == null)) {
				malformedJson();
			}

			role = ApplicationContextAccessor.getBean(Security.Role.class);
			for (Security.Role item : rolesList) {
				if (item.getId().equals(id)) {
					role = item;
					break;
				}
			}

			role.setPermission(permission);

			if (id != null) {
				role.setId(id);
			}

			if (name != null) {
				role.setName(name);
			}

			if (!rolesList.contains(role)) {
				rolesList.add(role);
			}
		}
		roles.setRoles(rolesList);
		share.setRoles(roles);
	}

	/**
	 * setValuesFromUserJson
	 * @deprecated
	 * This is no longer acceptable
	 * <p>use {@link ShareTagUpdateHandler#setValuesFromUserJson( Security.Share share, Security.Users users,
                                        List<Security.User> usersList,  JsonArray userArray)} instead</p>
	 * @param share
	 * @param users
	 * @param usersList
	 * @param userArray
	 */
    private void setValuesFromUserJson(@NotNull Security.Share share, @NotNull Security.Users users,
                                       @NotNull List<Security.User> usersList, @NotNull JSONArray userArray) {
        Security.User user;
        for (Object object : userArray) {
            JSONObject userJson = JSONObject.fromObject(object);
            String permission;
            String id = null;
            String name = null;
            if (userJson.has("id") && userJson.has("name")) {
                malformedJson();
            }

            try {
                permission = userJson.getString("permission");

                validatePermissionLevel(permission);

                if (userJson.has("id")) {
                    id = userJson.getString("id");
                }
                if (userJson.has("name")) {
                    name = userJson.getString("name");
                }
            } catch (Exception ex) {
                throw new MalformedJsonException("Parameter share is malformed with user" +
                        " array. Permission level is expected along with either " +
                        "name or id.");
            }

            if ((id != null && name != null) || (id == null && name == null)) {
                malformedJson();
            }

            user = ApplicationContextAccessor.getBean(Security.User.class);
            for(Security.User item:usersList){
                if(item.getId().equals(id)){
                    user=item;
                    break;
                }
            }

            user.setPermission(permission);

            if (id != null) {
                user.setId(id);
            }

            if (name != null) {
                user.setName(name);
            }


            if (!usersList.contains(user)) {
                usersList.add(user);
            }
        }
        users.setUsers(usersList);
        share.setUsers(users);
    }

    /**
     * using gson
     * setValuesFromUserJson(@NotNull Security.Share share, @NotNull Security.Users users,
			@NotNull List<Security.User> usersList, @NotNull JsonArray userArray)
     * @param share
     * @param users
     * @param usersList
     * @param userArray
     */
	private void setValuesFromUserJson(@NotNull Security.Share share, @NotNull Security.Users users,
			@NotNull List<Security.User> usersList, @NotNull JsonArray userArray) {
		Security.User user;
		for (Object object : userArray) {
			JsonObject userJson = new Gson().fromJson(object.toString(),JsonObject.class);
			String permission;
			String id = null;
			String name = null;
			if (userJson.has("id") && userJson.has("name")) {
				malformedJson();
			}

			try {
				permission = userJson.get("permission").getAsString();

				validatePermissionLevel(permission);

				if (userJson.has("id")) {
					id = userJson.get("id").getAsString();
				}
				if (userJson.has("name")) {
					name = userJson.get("name").getAsString();
				}
			} catch (Exception ex) {
				throw new MalformedJsonException("Parameter share is malformed with user"
						+ " array. Permission level is expected along with either " + "name or id.");
			}

			if ((id != null && name != null) || (id == null && name == null)) {
				malformedJson();
			}

			user = ApplicationContextAccessor.getBean(Security.User.class);
			for (Security.User item : usersList) {
				if (item.getId().equals(id)) {
					user = item;
					break;
				}
			}

			user.setPermission(permission);

			if (id != null) {
				user.setId(id);
			}

			if (name != null) {
				user.setName(name);
			}

			if (!usersList.contains(user)) {
				usersList.add(user);
			}
		}
		users.setUsers(usersList);
		share.setUsers(users);
	}

    private void validatePermissionLevel(String permission) {
        if (!this.permissions.contains(permission)) {
            throw new IllegalArgumentException("The permission level " + permission + " " +
                    "is not valid.");
        }
    }

    private void malformedJson() {
        throw new MalformedJsonException("Parameter share has one or more malformed array(s)" +
                "." + "Only id or name is expected but not both.");
    }
}
