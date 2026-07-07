package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;

import jakarta.validation.constraints.NotNull;

/**

 */
@Deprecated
public class CubeRule extends AbstractResourceRule implements IResourceRule {
    private CubeRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return CubeRuleHolder.INSTANCE;
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys) {
        foldersMap.put("title", fileAsJson.get("fileName").getAsString());
    }

    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        return validationResult(fileAsJson);
    }

    @Override
    public Map<String, String> getResourceMap(JsonObject fileAsJson, String extensionKey, String path, String name, String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    @Override

    public boolean isThreadSafeToCache() {
        return true;
    }

    @NotNull
    public String toString() {
        return "CubeRule";
    }

    private static class CubeRuleHolder {
        public static final IResourceRule INSTANCE = new CubeRule();
    }
}


