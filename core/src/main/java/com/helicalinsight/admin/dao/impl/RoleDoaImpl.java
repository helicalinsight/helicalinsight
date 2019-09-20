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

package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class implements the RoleDao interface and responsible for role
 * activities like add, edit, delete, list all roles.
 *
 * @author Muqtar Ahmed
 */
@SuppressWarnings("unchecked")
@Repository
public class RoleDoaImpl implements RoleDao {

    private static final Logger logger = LoggerFactory.getLogger(RoleDoaImpl.class);

    /**
     * Springs dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */

    @Autowired
    SessionFactory sessionFactory;

    /**
     * this is override method get the current session from session factory
     * object and save the role object
     *
     * @param role role object
     */

    @Override
    public int add(Role role) {
        try {
            sessionFactory.getCurrentSession().save(role);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return role.getId();
    }

    /**
     * this is override method get the current session from session factory
     * object and update the role object
     *
     * @param role role object
     */

    @Override
    public void edit(Role role) {
        try {
            sessionFactory.getCurrentSession().update(role);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and delete the role object by role id
     *
     * @param roleId role id
     */

    @Override
    public void delete(int roleId) {
        try {
            sessionFactory.getCurrentSession().delete(getRole(roleId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the role object by role id
     *
     * @param roleId role id
     * @return role object
     */

    @Override
    public Role getRole(int roleId) {
        Role role = null;
        try {
            role = (Role) sessionFactory.getCurrentSession().get(Role.class, roleId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return role;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing roles
     *
     * @return list of role objects
     */


    @Override
    public List<Role> findAllRoles(int offset, int limit, String searchPhrase, String searchOn,
                                   LimitOffsetModel pageCount) {
        if ((searchOn == null) || ("".equalsIgnoreCase(searchOn)) || searchOn.trim().length() <= 0) {

            return getAllRole(offset, limit, pageCount);
        } else {
            List<Role> roleList;
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Role.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            if ("name".equalsIgnoreCase(searchOn)) {
                searchPhrase = "%" + searchPhrase + "%";
                criteria.add(Restrictions.like("role_name", searchPhrase));
            }

            if (criteria.list().isEmpty()) {
                pageCount.setTotalCount(0);
            } else {
                pageCount.setTotalCount(criteria.list().size());
            }
            criteria.setFirstResult(offset);
            criteria.setMaxResults(limit);
            roleList = criteria.list();
            return roleList;
        }
    }

    @Override
    public List<Role> getAllRole(int offset, int limit, LimitOffsetModel pageCount) {
        List<Role> roleList = null;
        try {
            roleList = ApplicationUtilities.castList(Role.class, sessionFactory.getCurrentSession().createQuery
                    ("from  Role order by role_name").setFirstResult(offset).setMaxResults(limit).list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (roleList != null) {
            pageCount.setTotalCount(roleList.size());
        }
        return roleList;
    }

    @Override
    public List<Role> getAllRole(LimitOffsetModel pageCount) {
        List<Role> roleList = null;
        try {
            roleList = ApplicationUtilities.castList(Role.class, sessionFactory.getCurrentSession().createQuery
                    ("from" + " Role").list());

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (roleList != null) {
            pageCount.setTotalCount(roleList.size());
        }
        return roleList;
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roleList = null;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("from Role");
            roleList = ApplicationUtilities.castList(Role.class, query.list());
        } catch (Exception ex) {
            logger.error("Exception", ex);
        }
        return roleList;
    }

    /**
     * This is override method get the current session from session factory
     * object and return only those role matches by role name
     * is is null
     *
     * @param role_name role name
     * @return role object
     */

    @Override
    public Role findByName(String role_name) {
        Role role = null;
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Role.class);
            Criterion roleName = Restrictions.eq("role_name", role_name);
            criteria.add(roleName);
            if (criteria.list().isEmpty()) {
                return null;
            }
            role = (Role) criteria.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return role;
    }


    /**
     * this is override method get the current session from session factory
     * object and return the role object matches by role name
     * id
     *
     * @param role_name role name
     * @return role object
     */

    @Override
    public Role findRoleByName(String role_name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Role.class);
        Criterion name = Restrictions.eq("role_name", role_name);
        criteria.add(name);
        if (criteria.list().isEmpty()) {
            return null;
        }
        return (Role) criteria.uniqueResult();
    }


    /**
     * Returns a list of roles.
     *
     * @return list of role objects
     */
    @Override
    public List<Role> getRoles(LimitOffsetModel limitOffsetModel) {
        String searchOn = limitOffsetModel.getSearchOn();
        Integer offset = limitOffsetModel.getOffset();
        Integer limit = limitOffsetModel.getLimit();
        String searchPhrase = limitOffsetModel.getSearchPhrase();

        if ((searchOn == null) || ("".equalsIgnoreCase(searchOn))) {
            searchOn = "name";
        }


        List<Role> roleList;

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Role.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if ("name".equalsIgnoreCase(searchOn)) {
            criteria.add(Restrictions.like("role_name", searchPhrase));

        }

        List list = criteria.list();
        if (list.isEmpty()) {
            limitOffsetModel.setTotalCount(0);
        } else {
            limitOffsetModel.setTotalCount(list.size());
        }
        criteria.setFirstResult(offset);
        criteria.setMaxResults(limit);
        roleList = list;
        return roleList;
    }

    @Override
    public List<Role> searchUserRoles(String roleName, int offset, int limit,
                                      LimitOffsetModel pageCount) {

        List<Role> roles = null;
        try {
            roleName = "%" + roleName + "%";
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Role.class);

            criteria.add(Restrictions.like("role_name", roleName));
            pageCount.setTotalCount(criteria.list().size());
            criteria.setFirstResult(offset);
            criteria.setMaxResults(limit);
            roles = criteria.list();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return roles;

    }

    @Override
    public Long getRoleCount() {
        return (Long) sessionFactory.openSession().createCriteria(Role.class).setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public boolean isRoleExists(int roleId) {
        return false;
    }
}
