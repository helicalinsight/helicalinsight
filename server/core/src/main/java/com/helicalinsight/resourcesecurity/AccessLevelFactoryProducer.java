package com.helicalinsight.resourcesecurity;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class AccessLevelFactoryProducer {

    private final JsonObject shareJson;

    AccessLevelFactoryProducer(JsonObject shareJson) {
        this.shareJson = shareJson;
    }

    @NotNull
    AbstractAccessLevel getFactory() {
        return new AccessLevelFactory(this.shareJson);
    }
}
