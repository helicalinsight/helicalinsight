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

import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class implements the UserDao interface and responsible for user
 * activities like add, edit, delete, list all user.
 *
 * @author Muqtar Ahmed
 */
@SuppressWarnings("unchecked")
@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    /**
     * Spring's dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public int addUser(User user) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the user object " + user);
            }
            sessionFactory.getCurrentSession().save(user);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user.getId();
    }

    /**
     * this is override method get the current session from session factory
     * object and update the user object
     *
     * @param user User object
     */

    @Override
    public void editUser(User user) {
        try {
            sessionFactory.getCurrentSession().update(user);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by user name
     *
     * @param username user name
     * @return user object
     */

    @Override
    public User findUserByNameNorgNull(String username) {
        User user = null;
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(User.class);
            Criterion name = Restrictions.eq("username", username);
            criteria.add(name);
            if (criteria.list().isEmpty()) {
                return null;
            }
            user = (User) criteria.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by it's user name
     *
     * @param username user name
     * @return user object
     */

    @Override
    public User findUserByName(String username) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        Criterion name = Restrictions.eq("username", username);
        criteria.add(name);
        if (criteria.list().isEmpty()) {
            return null;
        }
        return (User) criteria.uniqueResult();
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by user name
     *
     * @param username user name
     * @return user object
     */

    @Override
    public User searchUserByName(String username) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username));
        if (criteria.list().isEmpty()) {
            return null;

        }
        return (User) criteria.uniqueResult();

    }


    /**
     * this is override method get the current session from session factory
     * object and delete the user object by user id
     *
     * @param userId user id
     */

    @Override
    public void deleteUser(int userId) {
        try {
            sessionFactory.getCurrentSession().delete(findUser(userId));

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the user object by it's user id
     *
     * @param userId user id
     * @return user object
     */

    @Override
    public User findUser(int userId) {
        User user = null;
        try {
            user = (User) sessionFactory.getCurrentSession().get(User.class, userId);
            if (user == null) {
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return user;
    }


    @Override
    public User findByIdAndFetchRolesEagerly(int userId) {
        User rolesList = null;
        try {
            rolesList = (User) sessionFactory.getCurrentSession().createQuery("SELECT p FROM User p JOIN" +
                    " FETCH p.roles WHERE" + " p.id=:userId").setParameter("userId", userId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return rolesList;
    }

    @Override
    public List<User> findUsers(LimitOffsetModel limitOffsetModel) {
        String searchOn = limitOffsetModel.getSearchOn();
        String searchPhrase = limitOffsetModel.getSearchPhrase();
        Integer offset = limitOffsetModel.getOffset();
        Integer limit = limitOffsetModel.getLimit();

        if ((searchOn == null) || ("".equals(searchOn)) || (searchOn.trim().length() <= 0)) {
            return getAllUsers(offset, limit, limitOffsetModel);
        } else {
            List<User> userList = null;
            Query query = null;

            Session currentSession = sessionFactory.getCurrentSession();
            if ("email".equalsIgnoreCase(searchOn)) {
                query = currentSession.createQuery("from User user where " +
                        "emailAddress" +
                        " like :emailAddress ORDER BY " + "user.username ASC");
                query.setParameter("emailAddress", searchPhrase);
            } else if ("user".equalsIgnoreCase(searchOn)) {
                query = currentSession.createQuery("from User user where username" +
                        " like :username ORDER BY " +
                        "user.username ASC");
                query.setParameter("username", searchPhrase);
            } else if ("roles".equalsIgnoreCase(searchOn)) {
                query = currentSession.createQuery("select distinct user  from User" +
                        "                user  join user.roles r where " +
                        "r.role_name" +
                        " like :role_name ORDER BY " +
                        "user.username ASC");
                query.setParameter("role_name", searchPhrase);
            }
            if (query != null) {
                if (query.list().isEmpty()) {
                    limitOffsetModel.setTotalCount(0);
                    return null;
                } else {
                    limitOffsetModel.setTotalCount(query.list().size());
                }
                query.setFirstResult(offset);
                query.setMaxResults(limit);
                userList = query.list();
            }
            return userList;
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing user objects
     *
     * @return list of user objects
     */

    @Override
    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel) {
        List<User> userList = null;
        try {
            logger.info("This query get all user is being executed");
            Query query = sessionFactory.getCurrentSession().createQuery("from User " + "user ORDER BY " +
                    "user.username ASC");
            if (query.list().isEmpty()) {
                limitOffsetModel.setTotalCount(0);

            } else {
                limitOffsetModel.setTotalCount(query.list().size());
            }
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            userList = query.list();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return userList;
    }

    @Override
    public List<User> searchUser(int offset, int limit, LimitOffsetModel limitOffsetModel) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.addOrder(Order.asc("username"));
        if (criteria.list().isEmpty()) {
            limitOffsetModel.setTotalCount(0);
            return null;
        } else {
            limitOffsetModel.setTotalCount(criteria.list().size());
        }
        criteria.setFirstResult(offset);
        criteria.setMaxResults(limit);
        return criteria.list();
    }

    @Override
    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel) {
        return null;
    }


}