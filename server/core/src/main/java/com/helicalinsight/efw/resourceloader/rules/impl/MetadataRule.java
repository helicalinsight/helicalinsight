package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.Iterator;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;

/**
 * Created by author on 01-04-2015.
 *
 * @author Rajasekhar
 */
@Deprecated
public final class MetadataRule extends AbstractResourceRule implements IResourceRule {

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRule getInstance() {
        return MetadataRuleHolder.INSTANCE;
    }

    @Override
    protected void addContentOfJson(@NotNull Map<String, String> foldersMap, @NotNull JsonObject fileAsJson,
                                    Iterator<?> keys) {
        if (fileAsJson.has("fileName")) {
            foldersMap.put("title", fileAsJson.get("fileName").getAsString());
        } else {
            foldersMap.put("title", fileAsJson.getAsJsonObject("database").get("name").getAsString());
        }
    }

    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        return validationResult(fileAsJson);
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull JsonObject fileAsJson, String extensionKey, String path,
                                              String name, String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class MetadataRuleHolder {
        public static final IResourceRule INSTANCE = new MetadataRule();
    }
}