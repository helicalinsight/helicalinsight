package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
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
 */
@Deprecated
public final class EFWVFRule implements IResourceRule {

    private EFWVFRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return EFWVFRuleHolder.INSTANCE;
    }

    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        //As such no rules. Not even visibility
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
        foldersMap.put("lastModified", lastModified);
        foldersMap.put("name", name);
        foldersMap.put("path", relativePath);
        if (fileAsJson.has("description")) {
            foldersMap.put("description", fileAsJson.get("description").getAsString());
        } else {
            foldersMap.put("description", name);
        }

        JsonArray charts = fileAsJson.getAsJsonArray("Charts");

        JsonArray array = new JsonArray();
        for (int chartNumber = 0; chartNumber < charts.size(); chartNumber++) {
            JsonObject chart = charts.get(chartNumber).getAsJsonObject();

            JsonObject aChart = new JsonObject();
            String chartName;
            JsonObject properties = chart.getAsJsonObject("prop");
            if (properties.has("name")) {
                chartName = properties.get("name").getAsString();
            } else {
                chartName = "VF Chart";
            }
            aChart.addProperty("name", chartName);
            aChart.addProperty("vf_id", chart.get("id").getAsInt());
            array.add(aChart);
        }

        foldersMap.put("charts", array.toString());

        return foldersMap;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private static class EFWVFRuleHolder {
        public static final IResourceRule INSTANCE = new EFWVFRule();
    }
}
