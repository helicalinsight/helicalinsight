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

import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.resourcesecurity.maxims.AbstractMaxim;
import com.helicalinsight.resourcesecurity.maxims.MaximFactoryProducer;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by author on 16-01-2015.
 * Modified on 20-05-2015
 *
 * @author Rajasekhar
 */
public final class ShareRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(ShareRule.class);

    private ShareRule() {
    }


    public static IResourceSecurityRule getInstance() {
        return ShareRuleHolder.INSTANCE;
    }

    /**
     * Determines whether a file can be shown to the user based on the file
     * content
     *
     * @param fileAsJson The file under concern that is to be served to the view
     * @return <code>true</code> if the file can be shown to the user
     */
    public boolean validate(JSONObject fileAsJson) {
        if (!ShareRuleHelper.isShareTagPresent(fileAsJson)) {
            return false;
        }

        final JSONObject shareJson;
        try {
            shareJson = fileAsJson.getJSONObject("share");
        } catch (JSONException badFileException) {
            if (logger.isWarnEnabled()) {
                logger.warn("Malformed Xml: ", badFileException);
            }
            return false;
        }

        try {
            final boolean isResourceSharedWithOtherRoles = ShareRuleHelper.isResourceSharedWithOtherRoles(shareJson);
            final boolean isResourceSharedWithUsers = ShareRuleHelper.isResourceSharedWithUsers(shareJson);


            if (!isResourceSharedWithOtherRoles && !isResourceSharedWithUsers) {
                return false;
            }

            MaximFactoryProducer factoryProducer = new MaximFactoryProducer(shareJson);
            final AbstractMaxim abstractMaxim = factoryProducer.getFactory();

            if (isResourceSharedWithOtherRoles) {
                if (abstractMaxim.maxim("roles").inspect()) {
                    return true;
                }
            }

            if (isResourceSharedWithUsers) {
                if (abstractMaxim.maxim("users").inspect()) {
                    return true;
                }
            }


        } catch (JSONException ex) {
            logger.error("The resource is malformed as it does not adhere to the " + "configuration in setting.xml.",
                    ex);
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
    private static class ShareRuleHolder {
        public static final IResourceSecurityRule INSTANCE = new ShareRule();
    }
}