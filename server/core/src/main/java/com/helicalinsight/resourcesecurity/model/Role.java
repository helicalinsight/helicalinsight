package com.helicalinsight.resourcesecurity.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 20-05-2015.
 *
 * @author Rajasekhar
 */
public final class Role {

    private String roleId;

    private String roleName;

    private int permissionLevel;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Role role = (Role) other;

        if (permissionLevel != role.permissionLevel) return false;
        //noinspection SimplifiableIfStatement
        if (roleId != null ? !roleId.equals(role.roleId) : role.roleId != null) return false;
        return !(roleName != null ? !roleName.equals(role.roleName) : role.roleName != null);

    }

    @Override
    public int hashCode() {
        int result = roleId != null ? roleId.hashCode() : 0;
        result = 31 * result + (roleName != null ? roleName.hashCode() : 0);
        result = 31 * result + permissionLevel;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Role{" +
                "roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", permissionLevel=" + permissionLevel +
                '}';
    }
}
