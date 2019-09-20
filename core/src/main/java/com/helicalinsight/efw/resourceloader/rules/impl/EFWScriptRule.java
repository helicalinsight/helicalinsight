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
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 15-05-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
public final class EFWScriptRule implements IResourceRule {

    private EFWScriptRule() {
    }


    public static IResourceRule getInstance() {
        return EFWScriptRuleHolder.INSTANCE;
    }

    @Override
    public boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        if (fileAsJson.has("visible")) {
            String visible = fileAsJson.getString("visible");
            return "true".equalsIgnoreCase(visible);
        }
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
        foldersMap.put("name", name);
        foldersMap.put("lastModified", lastModified);
        foldersMap.put("path", relativePath);
        JSONObject scriptJson = fileAsJson.getJSONObject("script");
        if (fileAsJson.has("@name")) {
            foldersMap.put("@name", fileAsJson.getString("@name"));
        }
        if (scriptJson.has("description")) {
            foldersMap.put("description", scriptJson.getString("description"));
        }
        if (scriptJson.has("group")) {
            foldersMap.put("group", scriptJson.getString("group"));
        }
        if (scriptJson.has("icon")) {
            foldersMap.put("icon", scriptJson.getString("icon"));
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