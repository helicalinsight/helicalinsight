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

package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for User activity which is implemented by concrete
 * UserDaoImpl class
 *
 * @author Muqtar Ahmed
 */

public interface UserDao {


    /**
     * Implementation class method add the user persistent object in database
     *
     * @param user persistent object
     */

    public int addUser(User user);

    /**
     * Implementation class method update the user persistent object
     *
     * @param user persistent object
     */

    public void editUser(User user);

    /**
     * Implementation class method delete persistent object user by user id from
     * database
     *
     * @param userId user id
     */

    public void deleteUser(int userId);

    /**
     * Implementation class method return the user object by user id from
     * database
     *
     * @param userId user id
     * @return user object
     */

    public User findUser(int userId);

    /**
     * Implementation class method return user object by user name
     *
     * @param username user name
     * @return user object
     */

    public User findUserByNameNorgNull(String username);

    /**
     * Implementation class method return user object by user name from database
     *
     * @param username user name
     * @return user object
     */

    public User searchUserByName(String username);

    /**
     * Implementation class method return user object by user name
     *
     * @param username user name
     * @return user object
     */

    public User findUserByName(String username);

    /**
     * Implementation class method return list of all user object from database
     *
     * @return list of user objects
     */

    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel);


    /**
     * Implementation class method delete the users
     * argument  from database
     */


    public List<User> findUsers(LimitOffsetModel limitOffsetModel);


    public User findByIdAndFetchRolesEagerly(int userId);

    public List<User> searchUser(int offset, int limit, LimitOffsetModel limitOffsetModel);

    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel);

}
