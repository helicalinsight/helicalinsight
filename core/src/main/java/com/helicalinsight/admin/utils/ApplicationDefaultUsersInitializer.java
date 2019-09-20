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

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates two users. One is admin and the another one is
 * user with admin and user roles respectively when application is being deployed(If the database
 * is empty). Sets the users passwords with the same name as that of the user.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
@Component
public class ApplicationDefaultUsersInitializer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDefaultUsersInitializer.class);

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Autowired
    @Qualifier(value = "roleServiceImpl")
    private RoleService roleService;

    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer applicationDefaultUserAndRoleNamesConfigurer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        int offset = 0;
        int limit = 1;

        List<User> allUsers = this.userService.getAllUsers(offset, limit, new LimitOffsetModel());
        if (allUsers == null || allUsers.isEmpty()) {
            initData();
        }
    }

    /**
     * This method creates two users with admin and user role
     */
    private void initData() {
        String roleUser = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUser();
        String roleAdmin = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdmin();
        String rolePhantom = this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantom();

        if (logger.isInfoEnabled()) {
            logger.info("Application initialization started. Creating 2 default users with " + "roles for the " +
                    "application.");
        }
        try {
            this.roleService.add(new Role(roleAdmin));
            this.roleService.add(new Role(roleUser));

            LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
            List<Role> allRoles = roleService.getAllRole(limitOffsetModel);

            User user = new User();
            String roleAdminName = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdminName();
            user.setUsername(roleAdminName);
            user.setPassword(roleAdminName);
            user.setEnabled(true);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdminEmail());
            user.setRoles(allRoles);
            this.userService.addUser(user);

            String[] split = rolePhantom.split(",");
            for(String s: split) {
                Role byName = this.roleService.findByName(s);
                if(byName==null)
                this.roleService.add(new Role(s));
            }

            Role userRole = this.roleService.findByName(roleUser);
            List<Role> userRoleList = new ArrayList<>();
            userRoleList.add(userRole);
            String roleUserName = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUserName();
            user.setUsername(roleUserName);
            user.setPassword(roleUserName);
            user.setEnabled(true);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUserEmail());
            user.setRoles(userRoleList);
            userService.addUser(user);


            List<Role> phantomRoleList = new ArrayList<>();
            for(String s: split) {
                Role phantomRole = this.roleService.findByName(s);
                phantomRoleList.add(phantomRole);
            }

            String rolePhantomName = this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantomName();
            user.setUsername(rolePhantomName);
            user.setPassword(rolePhantomName);
            user.setEnabled(true);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantomEmail());
            user.setRoles(phantomRoleList);
            userService.addUser(user);

        } catch (Exception ex) {
            logger.error("An exception occurred. ", ex);
        }
    }
}
