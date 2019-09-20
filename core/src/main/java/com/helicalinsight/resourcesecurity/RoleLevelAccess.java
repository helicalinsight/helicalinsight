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

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.model.Role;

import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class RoleLevelAccess implements IAccessLevel {

    private final List<String> userRolesIds;

    private final List<String> userRoles;

    private final List<Role> sharedRoles;

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    public RoleLevelAccess(List<Role> sharedRoles, ResourcePermissionLevelsHolder resourcePermissionLevelsHolder) {
        this.sharedRoles = sharedRoles;
        this.userRolesIds = AuthenticationUtils.getUserRolesIds();
        this.userRoles = AuthenticationUtils.getUserRoles();
        this.resourcePermissionLevelsHolder = resourcePermissionLevelsHolder;
    }

    public int accessLevel() {
        for (Role role : this.sharedRoles) {
            String roleId = role.getRoleId();
            if (roleId != null && this.userRolesIds.contains(roleId)) {
                return role.getPermissionLevel();
            }

            String roleName = role.getRoleName();
            if (roleName != null && this.userRoles.contains(roleName)) {
                return role.getPermissionLevel();
            }
        }
        return resourcePermissionLevelsHolder.noAccessLevel();
    }
}
