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

import com.helicalinsight.admin.dao.ProfileDao;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This class implements the ProfileDao interface and responsible for profile
 * activities like add, edit, delete, list all profile.
 *
 * @author Muqtar Ahmed
 */

@Repository
public class ProfileDaoImpl implements ProfileDao {

    private static final Logger logger = LoggerFactory.getLogger(ProfileDaoImpl.class);

    /**
     * '@Autowired' Spring's dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */

    @Autowired
    private SessionFactory session;

    /**
     * this is override method get the current session from session factory
     * object and save the profile object
     *
     * @param profile profile object
     */

    @Override
    public int add(Profile profile) {
        try {
            session.getCurrentSession().save(profile);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return profile.getId();

    }

    /**
     * this is override method get the current session from session factory
     * object and update the profile object
     * @param profile the profile object
     */

    @Override
    public void edit(Profile profile) {
        try {
            session.getCurrentSession().update(profile);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }

    /**
     * this is override method get the current session from session factory
     * object and delete the profile object by profile id
     *
     * @param profileId profile id
     */

    @Override
    public void delete(int profileId) {
        try {
            session.getCurrentSession().delete(getProfile(profileId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    /**
     * this is override method get the current session from session factory
     * object and return the profile object by profile id
     *
     * @param profileId profile id
     * @return profile object
     */

    @Override
    public Profile getProfile(int profileId) {
        Profile profile = null;
        try {
            profile = (Profile) session.getCurrentSession().get(Profile.class, profileId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return profile;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of all existing profile objects
     *
     * @return list of profile objects
     */

    @Override
    public List<Profile> getAllProfile() {
        List<Profile> profileList = null;
        try {
            profileList = ApplicationUtilities.castList(Profile.class, session.getCurrentSession().createQuery("from " +
                    "" + "Profile").list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return profileList;
    }

    /**
     * this is override method get the current session from session factory
     * object and return the list of profile objects by user id
     *
     * @param  userId the user id
     * @return list of profile objects
     */

    @Override
    public List<Profile> getUserProfile(int userId) {
        List<Profile> profileList = null;
        try {
            profileList = ApplicationUtilities.castList(Profile.class, session.getCurrentSession().createQuery("from " +
                    "Profile where " + "user_id=:userId").setParameter("userId", userId).list());
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return profileList;
    }

    @Override
    public Profile getProfileByNameAndUserId(String profileName, int userId) {
        Criteria criteria = session.getCurrentSession().createCriteria(Profile.class);
        Criterion name = Restrictions.eq("profile_name", profileName);
        Criterion user = Restrictions.eq("user_id", userId);
        LogicalExpression andExp = Restrictions.and(name, user);
        criteria.add(andExp);
        if (criteria.list().isEmpty()) {
            return null;
        }
        return (Profile) criteria.uniqueResult();
    }


    @Override
    public List<Profile> getProfileListByNameAndUserId(String profileName, int userId) {
        Criteria criteria = session.getCurrentSession().createCriteria(Profile.class);
        Criterion name = Restrictions.eq("profile_name", profileName);
        Criterion user = Restrictions.eq("user_id", userId);
        LogicalExpression andExp = Restrictions.and(name, user);
        criteria.add(andExp);
        if (criteria.list().isEmpty()) {
            return null;
        }
        return criteria.list();
    }

}
