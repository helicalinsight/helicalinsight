package com.helicalinsight.resourcesecurity;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public class PermissionConstants {

    public List<String> allowedPermissions() {
        ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean
                (ResourcePermissionLevelsHolder.class);
        List<String> permissions = new ArrayList<>();
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.noAccessLevel()));
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.executeAccessLevel()));
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.readAccessLevel()));
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.readWriteAccessLevel()));
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.readWriteDeleteAccessLevel()));
        permissions.add(String.valueOf(resourcePermissionLevelsHolder.ownerAccessLevel()));
        return permissions;
    }
}
