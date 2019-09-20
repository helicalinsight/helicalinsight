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

package com.helicalinsight.resourcesecurity.maxims;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.resourcesecurity.ShareRuleHelper;
import com.helicalinsight.resourcesecurity.model.User;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class UserMaxim implements ISecurityMaxim {

    private final JSONObject users;

    public UserMaxim(JSONObject users) {
        this.users = users;
    }

    public boolean inspect() {
        String hisUserName = AuthenticationUtils.getUserName();
        String userId = AuthenticationUtils.getUserId();
        List<User> sharedUsers = ShareRuleHelper.getSharedUsers(this.users);
        //In the list of users, the same user should not be repeated
        return validateListOfUsers(sharedUsers) && isAccessible(hisUserName, userId, sharedUsers);
    }

    private boolean validateListOfUsers(List<User> sharedUsers) {
        List<String> users = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        for (User user : sharedUsers) {
            // If the list already consists of the user name or the user id then the xml has
            // duplicate elements
            String userId = user.getUserId();
            if (users.contains(userId)) {
                return false;
            } else {
                if (userId != null) {
                    users.add(userId);
                }
            }

            String username = user.getUsername();
            if (userNames.contains(username)) {
                return false;
            } else {
                if (username != null) {
                    userNames.add(username);
                }
            }
        }
        return true;
    }

    private boolean isAccessible(String hisUserName, String hisId, List<User> sharedUsers) {
        for (User user : sharedUsers) {
            String username = user.getUsername();
            String id = user.getUserId();

            if (username != null && username.equals(hisUserName)) {
                return true;
            }

            if (id != null && id.equals(hisId)) {
                return true;
            }
        }
        return false;
    }
}
