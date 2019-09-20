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

import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is service layer of application and this class implements the
 * RoleService interface and responsible for role transactional activities like
 * add, edit, delete role
 *
 * @author Muqtar Ahmed
 */

@Service(value = "roleServiceImpl")
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * Spring dependency injection facilities which instantiate the
     * roleDao bean from spring container
     */
    @Autowired
    private RoleDao roleDao;

    /**
     * this is overloaded method responsible for transaction of saving role
     * object
     */

    @Transactional
    public int add(Role role) {
        logger.debug("adding role");
        return roleDao.add(role);
    }

    /**
     * this is overloaded method responsible for transaction of updating role
     * object
     */

    @Transactional
    public void edit(Role role) {
        roleDao.edit(role);
    }

    /**
     * this is overloaded method responsible for transaction of deleting role
     * object
     */

    @Transactional
    public void delete(int roleId) {
        roleDao.delete(roleId);
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by id and return role object
     *
     * @param roleId object
     */

    @Transactional
    public Role getRole(int roleId) {
        return roleDao.getRole(roleId);
    }

    /**
     * this is overloaded method responsible for transaction of loading all
     * existing roles and return the list of roles
     *
     * @return list of role objects
     */
    @Transactional
    public List<Role> getAllRole(LimitOffsetModel limitOffsetModel) {
        return roleDao.getAllRole(limitOffsetModel);
    }


    @Transactional
    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount) {
        return roleDao.getAllRole(offset, limit, pageCount);
    }

    @Transactional
    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by role name and return role object
     *
     * @param name name
     * @return role object
     */

    @Transactional
    public Role findByName(String name) {
        return roleDao.findByName(name);
    }

    /**
     * this is overloaded method responsible for transaction of loading role
     * object by role name return role object
     *
     * @param role_name role name
     * @return role object
     */

    @Transactional
    public Role findRoleByName(String role_name) {
        return roleDao.findRoleByName(role_name);
    }


    /**
     * this is overloaded method responsible for transaction of loading role
     * object and return list of role objects
     *
     * @return list of role object
     */

    @Transactional
    public List<Role> getRoles(LimitOffsetModel limitOffsetModel) {
        return roleDao.getRoles(limitOffsetModel);
    }

    @Transactional
    public List<Role> searchUserRoles(String roleName, int offset, int limit,
                                      LimitOffsetModel pageCount) {
        return roleDao.searchUserRoles(roleName, offset, limit, pageCount);
    }

    @Transactional
    public List<Role> findAllRoles(LimitOffsetModel pageCount) {
        return roleDao.findAllRoles(pageCount.getOffset(), pageCount.getLimit(), pageCount.getSearchPhrase(),
                pageCount.getSearchOn(), pageCount);
    }

}
