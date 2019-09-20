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

package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is service layer of application and this class implements the
 * UserService interface of spring security This is main class for
 * authentication and authorization check for user
 * authentication and authorization check for user
 *
 * @author Muqtar Ahmed
 */

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    /**
     * Spring dependency injection facilities which instantiate the
     * UserDao bean from spring container
     */

    @Autowired
    private UserDao userDao;

    /**
     * @Autowired Spring's dependency injection facilities which instantiate bean from spring container
     */

    /**
     * This is overloaded method check for user authentication and authorization
     * and return the UserDetails Object if user is authenticated
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String combineUserName) throws UsernameNotFoundException,
            DataAccessException {
        Principal principal;
        User user;

        String[] split;
        String username;

        if (combineUserName.contains(":")) {
            split = combineUserName.split(":");
            username = split[0];
            user = findUserByName(username);
        } else {
            username = combineUserName;
            user = userDao.findUserByNameNorgNull(username);
        }

        if (user != null) {
            // Let's populate user roles
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getRole_name()));
            }
            principal = new Principal(username, user.getPassword(), user.isEnabled(), true, true, true,
                    authorities, user.getEmailAddress(), user.getProfile(), null, user);
            logger.info("User Authenticated");
            return principal;
        } else {
            throw new UsernameNotFoundException("User Not Found!!!");
        }
    }

    /**
     * this is override method responsible for transaction of loading user
     * object by name  and return the user object by it's user name
     *
     * @param username user name
     * @return user object
     */

    @Transactional
    public User findUserByName(String username) {
        return userDao.findUserByName(username);
    }

    /**
     * this is overloaded method responsible for transaction of saving user
     * object
     */

    @Transactional
    public int addUser(User user) {
        return userDao.addUser(user);
    }

    /**
     * this is override method responsible for transaction of updating user
     * object
     */

    @Transactional
    public void editUser(User user) {
        userDao.editUser(user);
    }

    /**
     * this is override method responsible for transaction of deleting the user
     * object by user id
     *
     * @param userId user id
     */

    @Transactional
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    /**
     * this is override method responsible for transaction of loading the user
     * object by it's user id and return the user object
     *
     * @param userId user id
     * @return user object
     */

    @Transactional
    public User findUser(int userId) {
        return userDao.findUser(userId);
    }

    /**
     * this is override method responsible for transaction of loading the user
     * object by user name
     *
     * @param username user name
     * @return user object
     */

    @Transactional
    public User findUserByNameNorgNull(String username) {
        return userDao.findUserByNameNorgNull(username);
    }

    /**
     * this is override method responsible for transaction of loading all
     * existing user objects and return the list of all existing user objects
     *
     * @return list of user objects
     */

    @Transactional
    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel) {
        return userDao.getAllUsers(offset, limit, limitOffsetModel);
    }

    /**
     * this is override method responsible for transaction of loading user
     * object and return the user object by user name
     *
     * @param username user name
     * @return user object
     */

    @Transactional
    public User searchUserByname(String username) {
        return userDao.searchUserByName(username);
    }


    @Transactional
    public User findByIdAndFetchRolesEagerly(int userId) {
        return userDao.findByIdAndFetchRolesEagerly(userId);
    }

    @Transactional
    public List<User> findUsers(LimitOffsetModel limitOffsetModel) {
        return userDao.findUsers(limitOffsetModel);
    }


    @Transactional
    public List<User> searchUser(int offset, int limit,
                                 LimitOffsetModel limitOffsetModel) {
        return userDao.searchUser(offset, limit, limitOffsetModel);
    }

    @Override
    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel) {
        return userDao.searchUserByRole(offset, limit, role, limitOffsetModel);
    }
}
