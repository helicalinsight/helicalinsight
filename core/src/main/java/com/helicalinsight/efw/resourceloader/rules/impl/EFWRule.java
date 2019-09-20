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
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * Different reports are represented using different types of files. For EFW
 * file type this class is used.
 *
 * @author Rajasekhar
 * @see com.helicalinsight.efw.resourceloader.rules.IResourceRule
 * @see com.helicalinsight.efw.resourceloader.rules.IRule
 */
@SuppressWarnings("unused")
public final class EFWRule extends AbstractResourceRule implements IResourceRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWRule.class);

    /**
     * For Singleton structure - A private constructor
     */
    private EFWRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */

    public static IResourceRule getInstance() {
        return EFWRuleHolder.INSTANCE;
    }

    /**
     * First validates the security tag details present in the file, which is a mandatory tag.
     * The file should also be visible to be returned true.
     * Then, validates the file in the following scenarios:
     * <p/>
     * 1.	If resource tag not is present it would be considered as a ‘Public’ resource.
     *
     * @param fileAsJson The file under concern
     * @return <code>true</code> if the file can be sent to the currently logged in user's view
     * @throws ImproperXMLConfigurationException      If resourceSecurity tag is not configured
     * @throws UnSupportedRuleImplementationException
     */
    public boolean validateFile(JSONObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        //For old code compatibility. When there were no security or share rules only visible was
        // the rule
        if (!"true".equalsIgnoreCase(fileAsJson.getString("visible"))) {
            return false;
        }
        final JSONObject settings = JsonUtils.getSettingsJson();
        boolean enabled = settings.has("security");
        if (!enabled) {
            return true;
        }

        boolean supportForOldVersions = checkSupportForOldVersions(fileAsJson, settings);
        boolean isOwner;

        if (fileAsJson.has("security")) {
            isOwner = isSecurityMatching(settings, fileAsJson);
        } else {
            isOwner = supportForOldVersions;
        }

        return isOwner || resultOfConfiguredRules(settings, fileAsJson);
    }

    private boolean checkSupportForOldVersions(JSONObject fileAsJson, final JSONObject settings) throws
            ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        JSONObject security = settings.getJSONObject("security");
        if (security.has("supportOldVersions")) {
            String supportOldVersions = security.getString("supportOldVersions");
            if (supportOldVersions.equalsIgnoreCase("true")) {
                if (logger.isInfoEnabled()) {
                    logger.info("Supporting old efw resources");
                }
                return true;
            }
        }
        return (isSecurityMatching(settings, fileAsJson));
    }

    /**
     * Pretty toString method
     *
     * @return The class name
     */

    public String toString() {
        return "EFWRuleValidator";
    }

    /**
     * Includes the resource under concern in the json being sent to the view
     *
     * @param fileAsJson   The file under concern
     * @param extensionKey The extension of the file type. The tag key and not the value
     * @return A map of the file content
     */
    public Map<String, String> getResourceMap(JSONObject fileAsJson, String extensionKey, String path, String name,
                                              String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    protected void addContentOfJson(Map<String, String> foldersMap, JSONObject fileAsJson,
                                    Iterator<?> keys) {
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (("security".equals(key)) || "share".equals(key)) {
                continue;
            }
            foldersMap.put(key.toLowerCase(), fileAsJson.getString(key));
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EFWRuleHolder {
        public static final IResourceRule INSTANCE = new EFWRule();
    }
}
