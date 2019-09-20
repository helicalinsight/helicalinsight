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

package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Interface class for User service which is implemented by concrete
 * UserDetailsServiceImpl class
 *
 * @author Muqtar Ahmed
 */

public interface UserService extends UserDetailsService {

    /**
     * Implementation service class method add the user persistent object in
     * database
     *
     * @param user persistent object
     */

    int addUser(User user);

    /**
     * Implementation service class method update the user persistent object
     *
     * @param user persistent object
     */

    void editUser(User user);

    /**
     * Implementation service class method delete persistent object user by user
     * id from database
     *
     * @param userId user id
     */

    void deleteUser(int userId);

    /**
     * Implementation service class method return the user object by user id
     * from database
     *
     * @param userId user id
     * @return user object
     */

    User findUser(int userId);


    /**
     * Implementation service class method return user object by user name from
     * database
     *
     * @param username user name
     * @return user object
     */

    User searchUserByname(String username);

    /**
     * Implementation service class method return user object by user name from database
     *
     * @param username user name
     * @return user object
     */

    User findUserByName(String username);

    /**
     * Implementation service class method return list of all user object from
     * database
     *
     * @return list of user objects
     */

    List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel);


    List<User> findUsers(LimitOffsetModel limitOffsetModel);


    User findByIdAndFetchRolesEagerly(int userId);

    List<User> searchUser(int offset, int limit, LimitOffsetModel limitOffsetModel);

    List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel);

}
