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

package com.helicalinsight.resourcesecurity;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 04-12-2014.
 * <p/>
 * <p/>
 * An instance of this class is obtained using setting.xml configuration(resourceSecurity).
 * Validates the
 * security tag details are matching or not in the file that is passed as a parameter to the
 * interface method.
 *
 * @author Rajasekhar
 * @see com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule
 */
public final class ResourceSecurityRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSecurityRule.class);

    /**
     * No need to create multiple instances. So private constructor
     */
    private ResourceSecurityRule() {
    }

    /**
     * The singleton instance of the class is returned
     *
     * @return An instance of <code>IResourceSecurityRule</code>
     */
    public static IResourceSecurityRule getInstance() {
        return ResourceSecurityRuleHolder.INSTANCE;
    }

    /**
     * Returns <code>true</code> if the file has matching security details. The credentials are
     * compared with the currently logged in user's credentials.
     *
     * @param fileAsJson The file under concern with extension from setting.xml
     * @return <code>true</code> if the file has matching security details.
     */
    @Override
    public boolean validate(JSONObject fileAsJson) {
        try {
            if (!"true".equalsIgnoreCase(fileAsJson.getString("visible"))) {
                return false;
            }
            Object unDeterminedObject = fileAsJson.get("security");
            if (unDeterminedObject instanceof JSONObject) {
                return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), (JSONObject) unDeterminedObject);
            } else {
                String security = fileAsJson.getString("security");
                return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), security);
            }
        } catch (JSONException ex) {
            logger.error("The resource is malformed. Setting.xml is configured to apply rules. " +
                    "But the resource does not support " + "the rules.", ex);
        }
        return false;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * A private static class for lazy initialization
     */
    private static class ResourceSecurityRuleHolder {
        private static final IResourceSecurityRule INSTANCE = new ResourceSecurityRule();
    }
}