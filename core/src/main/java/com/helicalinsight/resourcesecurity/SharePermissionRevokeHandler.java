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

import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.ListIterator;

public class SharePermissionRevokeHandler {

    public Security.Share revokePermissions(Security.Share share, JSONObject revokeJson) {
        if (share == null) {
            return illegalState("Neither with roles/userss. Aborting.");
        }

        Security.Roles roles = share.getRoles();
        Security.Users users = share.getUsers();
        updateRoles(share, revokeJson, roles);
        updateUsers(share, revokeJson, users);
        return share;
    }

    private Security.Share illegalState(String message) {
        throw new IllegalStateException("Can't revoke permissions as the selected resource " +
                "is not shared with requested information. Cause: " + message);
    }


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

    private void malformedJson() {
        throw new MalformedJsonException("Parameter share has one or more malformed array(s)" +
                "." + "Only id or name is expected but not both.");
    }
}