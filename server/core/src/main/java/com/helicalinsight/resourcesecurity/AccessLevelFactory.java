package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class AccessLevelFactory extends AbstractAccessLevel {

    private final JsonObject shareJson;

    private final ResourcePermissionLevelsHolder levelsHolder = ApplicationContextAccessor.getBean
            (ResourcePermissionLevelsHolder.class);

    AccessLevelFactory(JsonObject shareJson) {
        this.shareJson = shareJson;
    }

    @NotNull
    @Override
    IAccessLevel accessLevel(String type) {
        JsonObject jsonObject = this.shareJson.getAsJsonObject(type);
        switch (type) {
            case "roles":
                return new RoleLevelAccess(ShareRuleHelper.getSharedRoles(jsonObject), this.levelsHolder);
            case "users":
                return new UserLevelAccess(ShareRuleHelper.getSharedUsers(jsonObject), this.levelsHolder);
            case "organizations":
                return new OrganizationLevelAccess(ShareRuleHelper.getSharedOrganizations(jsonObject),
                        this.levelsHolder);
            default:
                throw new EfwException(String.format("The access level for type %s is not defined", type));
        }
    }
}
