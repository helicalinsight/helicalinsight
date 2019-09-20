/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourceloader.rules.impl;

import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 15-05-2015.
 *
 * @author Rajasekhar
 */
public final class EFWVFRule implements IResourceRule {

    private EFWVFRule() {
    }


    public static IResourceRule getInstance() {
        return EFWVFRuleHolder.INSTANCE;
    }

    @Override
    public boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        //As such no rules. Not even visibility
        return true;
    }


    @Override
    public Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path,
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
            foldersMap.put("description", fileAsJson.getString("description"));
        } else {
            foldersMap.put("description", name);
        }

        JSONArray charts = fileAsJson.getJSONArray("Charts");

        JSONArray array = new JSONArray();
        for (int chartNumber = 0; chartNumber < charts.size(); chartNumber++) {
            JSONObject chart = charts.getJSONObject(chartNumber);

            JSONObject aChart = new JSONObject();
            String chartName;
            JSONObject properties = chart.getJSONObject("prop");
            if (properties.has("name")) {
                chartName = properties.getString("name");
            } else {
                chartName = "VF Chart";
            }
            aChart.accumulate("name", chartName);
            aChart.accumulate("vf_id", chart.getInt("@id"));
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
