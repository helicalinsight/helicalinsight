package com.helicalinsight.efw.resourceloader.rules.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;

import jakarta.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Rajesh
 * Created by author on 4/9/2019.
 */
@Deprecated
public class DashboardRule extends AbstractResourceRule implements IResourceRule {
    private DashboardRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return DashboardRuleHolder.INSTANCE;
    }

    @NotNull
    public String toString() {
        return "DashboardRule";
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

    private static class DashboardRuleHolder {
        public static final IResourceRule INSTANCE = new DashboardRule();
    }
}
