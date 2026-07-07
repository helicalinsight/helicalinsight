package com.helicalinsight.resourcesecurity.maxims;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.ShareRuleHelper;
import com.helicalinsight.resourcesecurity.model.User;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public final class UserMaxim implements ISecurityMaxim {

    private final JsonObject users;
    private final ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = new ResourcePermissionLevelsHolder();
    public UserMaxim(JsonObject users) {
        this.users = users;
    }

    public boolean inspect() {
        String hisUserName = AuthenticationUtils.getUserName();
        String userId = AuthenticationUtils.getUserId();
        List<User> sharedUsers = ShareRuleHelper.getSharedUsers(this.users);
        //In the list of users, the same user should not be repeated
        return validateListOfUsers(sharedUsers) && isAccessible(hisUserName, userId, sharedUsers);
    }

    private boolean validateListOfUsers(@NotNull List<User> sharedUsers) {
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

    private boolean isAccessible(String hisUserName, String hisId, @NotNull List<User> sharedUsers) {
        for (User user : sharedUsers) {
            String username = user.getUsername();
            String id = user.getUserId();

            if (username != null && username.equals(hisUserName) && isUserPermittedVisible(user)) {
                return true;
            }

            if (id != null && id.equals(hisId) && isUserPermittedVisible(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserPermittedVisible(User user) {
        if (user.getPermissionLevel() == resourcePermissionLevelsHolder.noAccessLevel()) {
            return false;
        }
        if (user.getPermissionLevel() == resourcePermissionLevelsHolder.executeAccessLevel()) {
            return false;
        }
        return true;
    }
}
