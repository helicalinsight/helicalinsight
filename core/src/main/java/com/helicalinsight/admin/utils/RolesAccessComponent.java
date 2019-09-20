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

package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
@Component
public class RolesAccessComponent {

    public JSONObject allRoles(LimitOffsetModel limitOffsetModel) {

        RoleService roleService = ApplicationContextAccessor.getBean(RoleService.class);
        return AdminUtils.getRoles(roleService, limitOffsetModel);
    }

    /**
     * @return logged-in logged-in user user-id
     */
    public Integer getLoggedInUsersUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new EfwServiceException("User is not logged in");
        }
        Principal principal = (Principal) auth.getPrincipal();
        if (principal != null) {
            User loggedInUser = principal.getLoggedInUser();
            return loggedInUser.getId();

        } else {
            throw new EfwServiceException("User is not logged in");
        }
    }
}
