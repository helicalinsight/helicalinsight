package com.helicalinsight.resourcesecurity.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by author on 20-05-2015.
 *
 * @author Rajasekhar
 */
public final class Organization {

    private String organizationId;

    private int permissionLevel;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

        Organization that = (Organization) other;

        //noinspection SimplifiableIfStatement
        if (permissionLevel != that.permissionLevel) return false;
        return !(organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null);

    }

    @Override
    public int hashCode() {
        int result = organizationId != null ? organizationId.hashCode() : 0;
        result = 31 * result + permissionLevel;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Organization{" +
                "organizationId='" + organizationId + '\'' +
                ", permissionLevel=" + permissionLevel +
                '}';
    }
}
