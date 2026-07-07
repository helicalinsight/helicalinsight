package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;


/**
 * Created by author on 15-05-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
@Deprecated
public final class EFWScriptRule implements IResourceRule {

    private EFWScriptRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return EFWScriptRuleHolder.INSTANCE;
    }

    @Override
    public boolean validateFile(@NotNull JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        if (fileAsJson.has("visible")) {
            String visible = fileAsJson.get("visible").getAsString();
            return "true".equalsIgnoreCase(visible);
        }
        return true;
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull JsonObject fileAsJson, String extensionKey, String path,
                                              String name, String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        String permission = RulesUtils.permissionLevelForNonVisibleFiles();
        foldersMap.put("permissionLevel", permission);

        String relativePath = ApplicationUtilities.getRelativeSolutionPath(path);

        foldersMap.put("title", FilenameUtils.removeExtension(name));
        foldersMap.put("type", "file");
        foldersMap.put("extension", extensionKey);
        foldersMap.put("name", name);
        foldersMap.put("lastModified", lastModified);
        foldersMap.put("path", relativePath);
        JsonObject scriptJson = fileAsJson.getAsJsonObject("script");
        if (fileAsJson.has("name")) {
            foldersMap.put("name", fileAsJson.get("name").getAsString());
        }
        if (scriptJson.has("description")) {
            foldersMap.put("description", scriptJson.get("description").getAsString());
        }
        if (scriptJson.has("group")) {
            foldersMap.put("group", scriptJson.get("group").getAsString());
        }
        if (scriptJson.has("icon")) {
            foldersMap.put("icon", scriptJson.get("icon").getAsString());
        }

        return foldersMap;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private static class EFWScriptRuleHolder {
        public static final IResourceRule INSTANCE = new EFWScriptRule();
    }
}