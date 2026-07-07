package com.helicalinsight.resourcesecurity;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.model.Organization;

import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class OrganizationLevelAccess implements IAccessLevel {

    private final String organizationId;

    private final List<Organization> sharedOrganizations;

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    public OrganizationLevelAccess(List<Organization> sharedOrganizations, ResourcePermissionLevelsHolder
            resourcePermissionLevelsHolder) {
        this.sharedOrganizations = sharedOrganizations;
        final String organizationId = AuthenticationUtils.getOrganizationId();
        if (organizationId == null) {
            this.organizationId = "superOrganization";
        } else {
            this.organizationId = organizationId;
        }
        this.resourcePermissionLevelsHolder = resourcePermissionLevelsHolder;
    }

    public int accessLevel() {
        for (Organization pojo : this.sharedOrganizations) {
            final String id = pojo.getOrganizationId();
            if (id == null) {
                if ("superOrganization".equals(this.organizationId)) {
                    return pojo.getPermissionLevel();
                }
            } else {
                if (this.organizationId.equals(id)) {
                    return pojo.getPermissionLevel();
                }
            }
        }
        //return this.resourcePermissionLevelsHolder.noAccessLevel();
        return -1;
    }
}
