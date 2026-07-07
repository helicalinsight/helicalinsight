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
        // return resourcePermissionLevelsHolder.noAccessLevel();
        return -1;
    }
}