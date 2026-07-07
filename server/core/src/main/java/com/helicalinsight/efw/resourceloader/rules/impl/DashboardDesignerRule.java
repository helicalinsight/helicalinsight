package com.helicalinsight.efw.resourceloader.rules.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

@Deprecated
public final class DashboardDesignerRule extends AbstractResourceRule implements IResourceRule {

    private DashboardDesignerRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return DashboardDesignRuleHolder.INSTANCE;
    }

    @NotNull
    public String toString() {
        return "DashboardDesignerRule";
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
    protected void addContentOfJson(@NotNull Map<String, String> foldersMap, @NotNull JsonObject fileAsJson,
                                    Iterator<?> keys) {
        foldersMap.put("title", fileAsJson.get("fileName").getAsString());
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private static class DashboardDesignRuleHolder {
        public static final IResourceRule INSTANCE = new DashboardDesignerRule();
    }
}
