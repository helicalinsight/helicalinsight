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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by author on 16-01-2015.
 * Modified on 20-05-2015
 *
 * @author Rajasekhar
 */
public final class InheritShareRule implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(InheritShareRule.class);
    private static final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
    private static String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
    /**
     * Determines whether a file can be shown to the user based on the file
     * content
     *
     * @param fileAsJson The file under concern that is to be served to the view
     * @return <code>true</code> if the file can be shown to the user
     */

    private ResourcePermissionFactory factory = new ResourcePermissionFactory();

    private InheritShareRule() {
    }

    public static IResourceSecurityRule getInstance() {
        return InheritShareRuleHolder.INSTANCE;
    }


    public boolean validate(JSONObject fileAsJson) {
        String absolutePath = fileAsJson.getString("absolutePath");
        File file = new File(absolutePath);

        int parentPermission = SecurityUtils.maxInheritPermission(file);
        if (parentPermission == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        boolean shareTagPresent = ShareRuleHelper.isShareTagPresent(fileAsJson);
        if (!shareTagPresent && parentPermission > resourcePermissionLevelsHolder.noAccessLevel()) {
            fileAsJson.put("permissionLevel", parentPermission);
            fileAsJson.put("parentPermission", parentPermission);
            fileAsJson.put("inherit", "true");
            return true;
        } else {
            int myPermission = SecurityUtils.whatIsMyPermission(fileAsJson);
            if (myPermission == -1 && parentPermission >= resourcePermissionLevelsHolder.readAccessLevel()) {

                fileAsJson.put("permissionLevel", myPermission);
                fileAsJson.put("parentPermission", parentPermission);
                fileAsJson.put("inherit", "true");
                return true;
            }

            if (myPermission < resourcePermissionLevelsHolder.noAccessLevel()) {

                //fileAsJson.put("parentPermission", myPermission);
                return false;
            } else {
                fileAsJson.put("permissionLevel", myPermission);
                fileAsJson.put("parentPermission", parentPermission);
                fileAsJson.put("inherit", "true");

                return true;
            }
        }

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    /**
     * A private static class for lazy initialization
     */
    private static class InheritShareRuleHolder {
        public static final IResourceSecurityRule INSTANCE = new InheritShareRule();
    }
}