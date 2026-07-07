package com.helicalinsight.resourcesecurity.maxims;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class MaximFactoryProducer {

    private final JsonObject shareJson;

    public MaximFactoryProducer(JsonObject shareJson) {
        this.shareJson = shareJson;
    }

    @NotNull
    public AbstractMaxim getFactory() {
        return new MaximFactory(this.shareJson);
    }
}
