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
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.model.User;

import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class UserLevelAccess implements IAccessLevel {

    private final String hisUserName;

    private final String hisId;

    private final List<User> sharedUsers;

    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    public UserLevelAccess(List<User> sharedUsers, ResourcePermissionLevelsHolder resourcePermissionLevelsHolder) {
        this.sharedUsers = sharedUsers;
        this.hisUserName = AuthenticationUtils.getUserName();
        this.hisId = AuthenticationUtils.getUserId();
        this.resourcePermissionLevelsHolder = resourcePermissionLevelsHolder;
    }

    public int accessLevel() {
        for (User user : this.sharedUsers) {
            String username = user.getUsername();
            String id = user.getUserId();

            if (username != null && username.equals(this.hisUserName)) {
                return user.getPermissionLevel();
            }

            if (id != null && id.equals(this.hisId)) {
                return user.getPermissionLevel();
            }
        }
        return resourcePermissionLevelsHolder.noAccessLevel();
    }
}