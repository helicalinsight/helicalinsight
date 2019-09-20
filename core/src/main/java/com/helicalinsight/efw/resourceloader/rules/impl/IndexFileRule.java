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
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Some business specific rules (like whether to allow the currently logged in
 * user should view the folder in question in the view or not) are applied on
 * the folders in the solution directory. For the same purpose this class
 * instance can be used.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
public final class IndexFileRule extends AbstractResourceRule implements IResourceRule {

    private static final Logger logger = LoggerFactory.getLogger(IndexFileRule.class);

    /**
     * For singleton structure
     */
    private IndexFileRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    public static IResourceRule getInstance() {
        return EfwFolderRuleHolder.INSTANCE;
    }

    /**
     * Pretty toString() of the class
     *
     * @return The class name itself
     */
    public String toString() {
        return "EFWFolderRuleValidator";
    }

    /**
     * Currently as it stands the folder is validated only if the the user
     * credentials in the json parameter are matching with the currently logged
     * in user
     *
     * @param fileAsJson The json of the file used for indicating the credentials of
     *                   the user
     * @return The result of user credentials matching or not.
     */
    @Override
    public boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        final JSONObject settings = JsonUtils.getSettingsJson();
        boolean isOwner = false;
        try {
            isOwner = AbstractResourceRule.isSecurityMatching(settings, fileAsJson);
        } catch (Exception ex) {
            logger.error("An exception has taken place. The stack trace is ", ex);
        }
        //Check if the folder is shared with other users, if it is shared it should be shown
        return isOwner || resultOfConfiguredRules(settings, fileAsJson);
    }

    @Override
    public Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name,
                                              String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        foldersMap.put("type", "folder");
        foldersMap.put("name", fileAsJson.getString("title"));

        foldersMap.put("options", new JSONObject().accumulate("selectable", "true").toString());
        if (fileAsJson.optBoolean("inherit")) {
            foldersMap.put("permissionLevel", fileAsJson.getString("permissionLevel"));
            foldersMap.put("inherit", "true");
        } else {
            foldersMap.put("permissionLevel", "" + SecurityUtils.whatIsMyPermission(fileAsJson));
        }
        return foldersMap;

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JSONObject fileAsJson, Iterator<?> keys) {

    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwFolderRuleHolder {
        public static final IResourceRule INSTANCE = new IndexFileRule();
    }
}