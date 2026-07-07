package com.helicalinsight.resourcesecurity.maxims;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ShareRuleHelper;
import com.helicalinsight.resourcesecurity.model.Role;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class RoleMaxim implements ISecurityMaxim {

    private final JsonObject roles;
    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = new ResourcePermissionLevelsHolder();

    public RoleMaxim(JsonObject roles) {
        this.roles = roles;
    }

    public boolean inspect() {
        List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
        List<String> userRoles = AuthenticationUtils.getUserRoles();
        List<Role> sharedRoles = ShareRuleHelper.getSharedRoles(this.roles);
        return validateListOfRoles(sharedRoles) && isAccessible(userRolesIds, userRoles, sharedRoles);
    }

    private boolean validateListOfRoles(@NotNull List<Role> sharedRoles) {
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

    private boolean isAccessible(@NotNull List<String> userRolesIds, @NotNull List<String> userRoles,
                                 @NotNull List<Role> sharedRoles) {
        for (Role role : sharedRoles) {
            String roleId = role.getRoleId();
            if (roleId != null && userRolesIds.contains(roleId) && isRolePermittedVisible(role)) {
                return true;
            }

            String roleName = role.getRoleName();
            if (roleName != null && userRoles.contains(roleName) && isRolePermittedVisible(role)) {
                return true;
            }
        }
        return false;
    }


    public boolean isRolePermittedVisible(Role role) {
        if (role.getPermissionLevel() == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        if (role.getPermissionLevel() == resourcePermissionLevelsHolder.executeAccessLevel()) {
            return false;
        }
        return true;
    }
}
