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
        //return resourcePermissionLevelsHolder.noAccessLevel();
        return -1;
    }
}
