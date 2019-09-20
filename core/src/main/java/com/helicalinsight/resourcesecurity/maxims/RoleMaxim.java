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

package com.helicalinsight.resourcesecurity.maxims;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.resourcesecurity.ShareRuleHelper;
import com.helicalinsight.resourcesecurity.model.Role;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class RoleMaxim implements ISecurityMaxim {

    private final JSONObject roles;

    public RoleMaxim(JSONObject roles) {
        this.roles = roles;
    }

    public boolean inspect() {
        List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
        List<String> userRoles = AuthenticationUtils.getUserRoles();
        List<Role> sharedRoles = ShareRuleHelper.getSharedRoles(this.roles);
        return validateListOfRoles(sharedRoles) && isAccessible(userRolesIds, userRoles, sharedRoles);
    }

    private boolean validateListOfRoles(List<Role> sharedRoles) {
        List<String> roles = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        for (Role role : sharedRoles) {
            // If the list already consists of the role name or the role id then the xml has
            // duplicate elements
            String roleName = role.getRoleName();
            if (roleNames.contains(roleName)) {
                return false;
            } else {
                if (roleName != null) {
                    roleNames.add(roleName);
                }
            }

            String roleId = role.getRoleId();
            if (roles.contains(roleId)) {
                return false;
            } else {
                if (roleId != null) {
                    roles.add(roleId);
                }
            }
        }
        return true;
    }

    private boolean isAccessible(List<String> userRolesIds, List<String> userRoles,
                                 List<Role> sharedRoles) {
        for (Role role : sharedRoles) {
            String roleId = role.getRoleId();
            if (roleId != null && userRolesIds.contains(roleId)) {
                return true;
            }

            String roleName = role.getRoleName();
            if (roleName != null && userRoles.contains(roleName)) {
                return true;
            }
        }
        return false;
    }
}
