package com.helicalinsight.resourcesecurity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;

public class SharePermissionRevokeHandler {
	/**
	 * revokePermissions
	 * @deprecated
	 * This method is no longer acceptable
	 * <p>use SharePermissionRevokeHandler#revokePermissions(Security.Share share, JsonObject revokeJson)  instead</p>
	 * @param share
	 * @param revokeJson
	 * @return
	 */
    public Security.Share revokePermissions(Security.Share share, JSONObject revokeJson) {
        if (share == null) {
            return illegalState("Neither with roles/users/organizations. Aborting.");
        }

        Security.Organizations organizations = share.getOrganizations();
        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();

        updateOrganizations(share, revokeJson, organizations);

        updateRoles(share, revokeJson, roles);

        updateUsers(share, revokeJson, users);
        if (share.getRoles() == null && share.getUsers() == null && share.getOrganizations() == null) {
            return null;
        }
        return share;
    }
    /**
     * using gson
     * revokePermissions(Security.Share share, JsonObject revokeJson)
     * @param share
     * @param revokeJson
     * @return
     */
    public Security.Share revokePermissions(Security.Share share, JsonObject revokeJson) {
        if (share == null) {
            return illegalState("Neither with roles/users/organizations. Aborting.");
        }

        Security.Organizations organizations = share.getOrganizations();
        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();

        updateOrganizations(share, revokeJson, organizations);

        updateRoles(share, revokeJson, roles);

        updateUsers(share, revokeJson, users);
        if (share.getRoles() == null && share.getUsers() == null && share.getOrganizations() == null) {
            return null;
        }
        return share;
    }
    private Security.Share illegalState(String message) {
        throw new IllegalStateException("Can't revoke permissions as the selected resource " +
                "is not shared with requested information. Cause: " + message);
    }

    /**
     * updateOrganizations
     * This method no longer acceptable
     * <p>use {@link SharePermissionRevokeHandler#updateOrganizations( Security.Share share,  JsonObject shareWith,
                                      Security.Organizations organizations) } instead </p>
     * @param share
     * @param shareWith
     * @param organizations
     */
    @Deprecated
    private void updateOrganizations(@NotNull Security.Share share, @NotNull JSONObject shareWith,
                                     @Nullable Security.Organizations organizations) {
        List<Security.Organization> organizationsList;
        if (shareWith.has("organization")) {
            //Only super organization users can successfully complete this request.
            ShareTagUpdateHandler.checkWhetherAuthenticated();
            JSONArray organizationsArray = shareWith.getJSONArray("organization");
            if (organizations != null) {
                organizationsList = organizations.getOrganizations();
                if (organizationsList == null) {
                    illegalState("Not shared with organizations.");
                    return;
                }

                for (Object object : organizationsArray) {
                    JSONObject organizationJson = JSONObject.fromObject(object);
                    String permission;
                    String id = null;
                    try {
                        permission = organizationJson.getString("permission");

                        if (organizationJson.has("id")) {
                            id = organizationJson.getString("id");
                        }
                    } catch (Exception ex) {
                        throw new MalformedJsonException("Parameter share is malformed with " +
                                "organization array. Permission level is expected along with " +
                                "optional id.");
                    }

                    ListIterator<Security.Organization> listIterator = organizationsList.listIterator();
                    while (listIterator.hasNext()) {
                        Security.Organization organization = listIterator.next();
                        if (id != null && organization.getId().equals(id)) {
                            if (organization.getPermission().equals(permission)) {
                                listIterator.remove();
                            }
                        }/* Code commented as it is in if condition with valid check
                         else {
                            if (organization.getPermission().equals(permission)) {
                                listIterator.remove();
                            }
                        }*/
                    }
                }
                if (!organizationsList.isEmpty()) {
                    organizations.setOrganizations(organizationsList);
                    share.setOrganizations(organizations);
                } else {
                    share.setOrganizations(null);
                }
            } else {
                illegalState("Not shared with organizations.");
            }
        }
    }

    /**
     * using gson
     * updateOrganizations(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Organizations organizations) {
     * @param share
     * @param shareWith
     * @param organizations
     */
	private void updateOrganizations(@NotNull Security.Share share, @NotNull JsonObject shareWith,
			@Nullable Security.Organizations organizations) {
		List<Security.Organization> organizationsList;
		if (shareWith.has("organization")) {
//Only super organization users can successfully complete this request.
			ShareTagUpdateHandler.checkWhetherAuthenticated();
			JsonArray organizationsArray = shareWith.getAsJsonArray("organization");
			if (organizations != null) {
				organizationsList = organizations.getOrganizations();
				if (organizationsList == null) {
					illegalState("Not shared with organizations.");
					return;
				}

				for (Object object : organizationsArray) {
					JsonObject organizationJson = new Gson().fromJson(object.toString(),JsonObject.class);
					String permission;
					String id = null;
					try {
						permission = organizationJson.get("permission").getAsString();

						if (organizationJson.has("id")) {
							id = organizationJson.get("id").getAsString();
						}
					} catch (Exception ex) {
						throw new MalformedJsonException("Parameter share is malformed with "
								+ "organization array. Permission level is expected along with " + "optional id.");
					}

					ListIterator<Security.Organization> listIterator = organizationsList.listIterator();
					while (listIterator.hasNext()) {
						Security.Organization organization = listIterator.next();
						if (id != null && organization.getId().equals(id)) {
							if (organization.getPermission().equals(permission)) {
								listIterator.remove();
							}
						} /*
							 * Code commented as it is in if condition with valid check else { if
							 * (organization.getPermission().equals(permission)) { listIterator.remove(); }
							 * }
							 */
					}
				}
				if (!organizationsList.isEmpty()) {
					organizations.setOrganizations(organizationsList);
					share.setOrganizations(organizations);
				} else {
					share.setOrganizations(null);
				}
			} else {
				illegalState("Not shared with organizations.");
			}
		}
	}
    /**
     * updateRoles
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link SharePermissionRevokeHandler#updateRoles(Security.Share share, JsonObject shareWith, Security.Roles roles)} instead </p>
     * @param share
     * @param shareWith
     * @param roles
     */
	@Deprecated
    private void updateRoles(Security.Share share, JSONObject shareWith, Security.Roles roles) {
        List<Security.Role> rolesList;
        if (shareWith.has("role")) {
            JSONArray roleArray = shareWith.getJSONArray("role");
            if (roles != null) {
                rolesList = roles.getRoles();
                if (rolesList == null) {
                    illegalState("Not shared with roles.");
                    return;
                }

                for (Object object : roleArray) {
                    JSONObject roleJson = JSONObject.fromObject(object);
                    String permission;
                    String id = null;
                    String name = null;
                    try {
                        permission = roleJson.getString("permission");

                        if (roleJson.has("id")) {
                            id = roleJson.getString("id");
                        }

                        if (roleJson.has("name")) {
                            name = roleJson.getString("name");
                        }
                    } catch (Exception ex) {
                        throw new MalformedJsonException("Parameter share is malformed with " +
                                "role array. Permission level is expected along with " +
                                "id or name of the role.");
                    }

                    if ((id != null && name != null) || (id == null && name == null)) {
                        malformedJson();
                    }

                    ListIterator<Security.Role> listIterator = rolesList.listIterator();

                    while (listIterator.hasNext()) {
                        Security.Role role = listIterator.next();
                        if (id != null) {
                            if (role.getId().equals(id)) {
                                if (role.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        } else {
                            if (role.getName().equals(name)) {
                                if (role.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        }
                    }
                }

                if (!rolesList.isEmpty()) {
                    roles.setRoles(rolesList);
                    share.setRoles(roles);
                } else {
                    share.setRoles(null);
                }
            } else {
                illegalState("Not shared with roles.");
            }
        }
    }
	/**
	 * using gson
	 * updateRoles(Security.Share share, JsonObject shareWith, Security.Roles roles)
	 * @param share
	 * @param shareWith
	 * @param roles
	 */
	private void updateRoles(Security.Share share, JsonObject shareWith, Security.Roles roles) {
        List<Security.Role> rolesList;
        if (shareWith.has("role")) {
            JsonArray roleArray = shareWith.getAsJsonArray("role");
            if (roles != null) {
                rolesList = roles.getRoles();
                if (rolesList == null) {
                    illegalState("Not shared with roles.");
                    return;
                }

                for (Object object : roleArray) {
                    JsonObject roleJson = new Gson().fromJson(object.toString(),JsonObject.class);
                    String permission;
                    String id = null;
                    String name = null;
                    try {
                        permission = roleJson.get("permission").getAsString();

                        if (roleJson.has("id")) {
                            id = roleJson.get("id").getAsString();
                        }

                        if (roleJson.has("name")) {
                            name = roleJson.get("name").getAsString();
                        }
                    } catch (Exception ex) {
                        throw new MalformedJsonException("Parameter share is malformed with " +
                                "role array. Permission level is expected along with " +
                                "id or name of the role.");
                    }

                    if ((id != null && name != null) || (id == null && name == null)) {
                        malformedJson();
                    }

                    ListIterator<Security.Role> listIterator = rolesList.listIterator();

                    while (listIterator.hasNext()) {
                        Security.Role role = listIterator.next();
                        if (id != null) {
                            if (role.getId().equals(id)) {
                                if (role.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        } else {
                            if (role.getName().equals(name)) {
                                if (role.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        }
                    }
                }

                if (!rolesList.isEmpty()) {
                    roles.setRoles(rolesList);
                    share.setRoles(roles);
                } else {
                    share.setRoles(null);
                }
            } else {
                illegalState("Not shared with roles.");
            }
        }
    }

	/**
	 * updateUsers
	 * @deprecated
	 * This method is no longer acceptable
	 * <p>use {@link SharePermissionRevokeHandler#updateUsers(Security.Share share, JsonObject revokeJson, Security.Users users)} instead</p>
	 * @param share
	 * @param revokeJson
	 * @param users
	 */
	@Deprecated
    private void updateUsers(Security.Share share, JSONObject revokeJson, Security.Users users) {
        List<Security.User> usersList;
        if (revokeJson.has("user")) {
            JSONArray userArray = revokeJson.getJSONArray("user");
            if (users != null) {
                usersList = users.getUsers();
                if (usersList == null) {
                    illegalState("Not shared with users.");
                    return;
                }

                for (Object object : userArray) {
                    JSONObject userJson = JSONObject.fromObject(object);
                    String permission;
                    String id = null;
                    String name = null;
                    try {
                        permission = userJson.getString("permission");

                        if (userJson.has("id")) {
                            id = userJson.getString("id");
                        }

                        if (userJson.has("name")) {
                            name = userJson.getString("name");
                        }
                    } catch (Exception ex) {
                        throw new MalformedJsonException("Parameter share is malformed with " +
                                "user array. Permission level is expected along with " +
                                "id or name of the user.");
                    }

                    if ((id != null && name != null) || (id == null && name == null)) {
                        malformedJson();
                    }

                    ListIterator<Security.User> listIterator = usersList.listIterator();

                    while (listIterator.hasNext()) {
                        Security.User user = listIterator.next();
                        if (id != null) {
                            if (user.getId().equals(id)) {
                                if (user.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        } else {
                            if (user.getName().equals(name)) {
                                if (user.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        }
                    }
                }

                if (!usersList.isEmpty()) {
                    users.setUsers(usersList);
                    share.setUsers(users);
                } else {
                    share.setUsers(null);
                }
            } else {
                illegalState("Not shared with users.");
            }
        }
    }
	/**
	 * using gson
	 * updateUsers(Security.Share share, JsonObject revokeJson, Security.Users users)
	 * @param share
	 * @param revokeJson
	 * @param users
	 */
	private void updateUsers(Security.Share share, JsonObject revokeJson, Security.Users users) {
        List<Security.User> usersList;
        if (revokeJson.has("user")) {
            JsonArray userArray = revokeJson.getAsJsonArray("user");
            if (users != null) {
                usersList = users.getUsers();
                if (usersList == null) {
                    illegalState("Not shared with users.");
                    return;
                }

                for (Object object : userArray) {
                    JsonObject userJson = new Gson().fromJson(object.toString(),JsonObject.class);
                    String permission;
                    String id = null;
                    String name = null;
                    try {
                        permission = userJson.get("permission").getAsString();

                        if (userJson.has("id")) {
                            id = userJson.get("id").getAsString();
                        }

                        if (userJson.has("name")) {
                            name = userJson.get("name").getAsString();
                        }
                    } catch (Exception ex) {
                        throw new MalformedJsonException("Parameter share is malformed with " +
                                "user array. Permission level is expected along with " +
                                "id or name of the user.");
                    }

                    if ((id != null && name != null) || (id == null && name == null)) {
                        malformedJson();
                    }

                    ListIterator<Security.User> listIterator = usersList.listIterator();

                    while (listIterator.hasNext()) {
                        Security.User user = listIterator.next();
                        if (id != null) {
                            if (user.getId().equals(id)) {
                                if (user.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        } else {
                            if (user.getName().equals(name)) {
                                if (user.getPermission().equals(permission)) {
                                    listIterator.remove();
                                }
                            }
                        }
                    }
                }

                if (!usersList.isEmpty()) {
                    users.setUsers(usersList);
                    share.setUsers(users);
                } else {
                    share.setUsers(null);
                }
            } else {
                illegalState("Not shared with users.");
            }
        }
    }

    private void malformedJson() {
        throw new MalformedJsonException("Parameter share has one or more malformed array(s)" +
                "." + "Only id or name is expected but not both.");
    }
}