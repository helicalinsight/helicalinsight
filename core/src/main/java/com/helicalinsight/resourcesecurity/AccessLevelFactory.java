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

import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import net.sf.json.JSONObject;


/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class AccessLevelFactory extends AbstractAccessLevel {

    private final JSONObject shareJson;

    private final ResourcePermissionLevelsHolder levelsHolder = ApplicationContextAccessor.getBean
            (ResourcePermissionLevelsHolder.class);

    AccessLevelFactory(JSONObject shareJson) {
        this.shareJson = shareJson;
    }

    @Override
    IAccessLevel accessLevel(String type) {
        JSONObject jsonObject = this.shareJson.getJSONObject(type);
        switch (type) {
            case "roles":
                return new RoleLevelAccess(ShareRuleHelper.getSharedRoles(jsonObject), this.levelsHolder);
            case "users":
                return new UserLevelAccess(ShareRuleHelper.getSharedUsers(jsonObject), this.levelsHolder);

            default:
                throw new EfwException(String.format("The access level for type %s is not defined", type));
        }
    }
}
