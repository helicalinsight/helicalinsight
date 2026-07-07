package com.helicalinsight.resourcesecurity.maxims;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ShareRuleHelper;
import com.helicalinsight.resourcesecurity.model.Organization;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class OrganizationMaxim implements ISecurityMaxim {

    private final JsonObject organizations;
    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = new ResourcePermissionLevelsHolder();
    public OrganizationMaxim(JsonObject organizations) {
        this.organizations = organizations;
    }

    public boolean inspect() {
        //Can be null
        String organizationId = AuthenticationUtils.getOrganizationId();
        if (organizationId == null) {
            organizationId = "superOrganization";
        }

        List<Organization> sharedOrganizations = ShareRuleHelper.getSharedOrganizations(this.organizations);
        return isAccessible(organizationId, sharedOrganizations);
    }

    private boolean isAccessible(@NotNull String organizationId, @NotNull List<Organization> sharedOrganizations) {
        for (Organization pojo : sharedOrganizations) {
            final String id = pojo.getOrganizationId();
            if (id == null) {
                if ("superOrganization".equals(organizationId)) {
                    return true;
                }
            } else {
                if (organizationId.equals(id) && isOrganizationPermittedVisible(pojo)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isOrganizationPermittedVisible(Organization org) {
        if (org.getPermissionLevel() == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        if (org.getPermissionLevel() == resourcePermissionLevelsHolder.executeAccessLevel()) {
            return false;
        }
        return true;
    }
}
