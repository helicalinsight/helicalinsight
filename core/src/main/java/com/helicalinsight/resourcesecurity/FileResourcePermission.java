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

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class FileResourcePermission implements IResourcePermission {

    //private static final Logger logger = LoggerFactory.getLogger(ResourcePermission.class);

    private final JSONObject fileAsJson;

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    public FileResourcePermission(JSONObject fileAsJson) {
        this.resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);
        this.fileAsJson = fileAsJson;
    }

    public int maximumPermissionLevelOnResource() {
        //If the file has no security tag
        boolean isOwner = false;
        boolean isSecurityTagPresent = this.fileAsJson.has("security");

        if (isSecurityTagPresent) {
            IResourceSecurityRule securityRule = ResourceSecurityRule.getInstance();
            isOwner = securityRule.validate(this.fileAsJson);
        }

        if (isOwner) {
            //Send owner's permission level
            return this.resourcePermissionLevelsHolder.ownerAccessLevel();
        } else {
            //Not owner. Check if it is shared. Else send public resource permission level
            boolean isShareTagPresent = ShareRuleHelper.isShareTagPresent(this.fileAsJson);
            if (!isSecurityTagPresent) {
                if (!isShareTagPresent) {
                    return this.resourcePermissionLevelsHolder.publicResourceAccessLevel();
                }
            } else {
                //Security is present but not shared.
                if (!isShareTagPresent) {
                    if (fileAsJson.has("absolutePath")) {
                        return SecurityUtils.maxInheritPermission(new File(fileAsJson.getString("absolutePath")));
                    } else
                        return this.resourcePermissionLevelsHolder.noAccessLevel();
                }
            }
        }

        final JSONObject shareJson = this.fileAsJson.getJSONObject("share");
        //User organization may be null
        final boolean isResourceSharedWithOtherRoles = ShareRuleHelper.isResourceSharedWithOtherRoles(shareJson);
        final boolean isResourceSharedWithUsers = ShareRuleHelper.isResourceSharedWithUsers(shareJson);

        List<Integer> permissions = new ArrayList<>();
        AccessLevelFactoryProducer factoryProducer = new AccessLevelFactoryProducer(shareJson);
        AbstractAccessLevel abstractAccessLevel = factoryProducer.getFactory();

        if (isResourceSharedWithOtherRoles) {
            permissions.add(abstractAccessLevel.accessLevel("roles").accessLevel());
        }

        if (isResourceSharedWithUsers) {
            permissions.add(abstractAccessLevel.accessLevel("users").accessLevel());
        }


        if (!permissions.isEmpty()) {
            Integer max = Collections.max(permissions);
            if (max == -1) {
                if (fileAsJson.has("absolutePath")) {
                    return SecurityUtils.maxInheritPermission(new File(fileAsJson.getString("absolutePath")));
                }
            }
            return max;
        }

        //Return no access level. Empty share tag.
        return this.resourcePermissionLevelsHolder.noAccessLevel();
    }


}
