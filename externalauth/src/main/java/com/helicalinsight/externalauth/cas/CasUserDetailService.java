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

package com.helicalinsight.externalauth.cas;

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.service.impl.UserDetailsServiceImpl;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2/11/2016.
 *
 * @author Somen
 */
@Service
public class CasUserDetailService extends UserDetailsServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CasUserDetailService.class);

    @Autowired
    private RoleService roleService;


    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;
    @Autowired
    private ExternalAdminUsers externalAdminUsers;
    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String casUserName) throws UsernameNotFoundException, DataAccessException {
        String combinedUsername = casUserName;
        if (!externalAdminUsers.getAdminUsers().contains(casUserName)) {
            combinedUsername = combinedUsername;
        }
        UserDetails userDetails = null;
        try {
            userDetails = super.loadUserByUsername(combinedUsername);
        } catch (UsernameNotFoundException unfe) {
            logger.error("The external user is not found", unfe);
        } catch (Exception ignore) {
            logger.error("Unknown exception", ignore);
        }
        if (userDetails == null) {
            this.synchronize(casUserName);
        }
        logger.info(combinedUsername);
        return super.loadUserByUsername(combinedUsername);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void synchronize(String userName) {
        User user = new User();
        boolean isAdmin;

        Role userRole;
        isAdmin = externalAdminUsers.getAdminUsers().contains(userName);
        String roleUser;

        if (isAdmin) {
            roleUser = namesConfigurer.getRoleAdmin();
            userRole = roleService.findByName(roleUser);
        } else {
            roleUser = namesConfigurer.getRoleUser();
            userRole = roleService.findRoleByName(roleUser);
        }



        List<Role> userRoleList = new ArrayList<>();
        userRoleList.add(userRole);
        user.setRoles(userRoleList);
        user.setUsername(userName);
        user.setIsExternallyAuthenticated(true);
        user.setPassword("");
        user.setEnabled(true);
        userService.addUser(user);
        logger.info("User added");
    }
}
