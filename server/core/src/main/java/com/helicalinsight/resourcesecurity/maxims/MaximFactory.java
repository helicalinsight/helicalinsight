package com.helicalinsight.resourcesecurity.maxims;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwException;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class MaximFactory extends AbstractMaxim {

    private final JsonObject shareJson;

    public MaximFactory(JsonObject shareJson) {
        this.shareJson = shareJson;
    }

    @NotNull
    @Override
    public ISecurityMaxim maxim(String type) {
        if ("roles".equals(type)) {
            return new RoleMaxim(this.shareJson.getAsJsonObject(type));
        } else if ("users".equals(type)) {
            return new UserMaxim(this.shareJson.getAsJsonObject(type));
        } else if ("organizations".equals(type)) {
            return new OrganizationMaxim(this.shareJson.getAsJsonObject(type));
        } else {
            throw new EfwException(String.format("The maxim %s is undefined.", type));
        }
    }
}
