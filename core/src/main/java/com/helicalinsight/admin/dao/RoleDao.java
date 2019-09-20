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

import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for Role activity which is implemented by concrete
 * RoleDaoImpl class
 *
 * @author Muqtar
 */
public interface RoleDao {

    /**
     * Implementation class method add the role persistent object in database
     *
     * @param role persistent object
     */
    public int add(Role role);

    /**
     * Implementation class method update the role persistent object in database
     *
     * @param role persistent object
     */

    public void edit(Role role);

    /**
     * Implementation class method delete the role persistent object from
     * database
     *
     * @param roleId role id
     */

    public void delete(int roleId);

    /**
     * Implementation class method return the role object from database by role
     * id
     *
     * @param roleId role id
     * @return role object
     */

    public Role getRole(int roleId);

    /**
     * Implementation class method load return the list of role object from
     * database
     *
     * @return list of role objects
     */
    public List<Role> getAllRole(LimitOffsetModel pageCount);

    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount);

    /**
     * Implementation class method return the role object from database by role
     * name
     *
     * @param role_name role name
     * @return role object
     */
    public Role findByName(String role_name);

    /**
     * Implementation class method load the role object from database by role
     * name
     *
     * @param role_name role name
     * @return role object
     */

    public Role findRoleByName(String role_name);


    /**
     * Implementation class method return the list of role object from database
     *
     * @return list of role objects
     */

    public List<Role> getRoles(LimitOffsetModel pageCount);

    public List<Role> searchUserRoles(String roleName, int offset, int limit,
                                      LimitOffsetModel pageCount);

    public boolean isRoleExists(int roleId);

    public List<Role> findAllRoles(int offset, int limit, String searchPhrase, String searchOn,
                                   LimitOffsetModel pageCount);

    public Long getRoleCount();

    public List<Role> getAllRoles();
}
