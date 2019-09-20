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
