package com.helicalinsight.admin.dao.impl;

import com.helicalinsight.admin.dao.ProfileDao;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.efw.utility.ApplicationUtilities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;


import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the ProfileDao interface and responsible for profile
 * activities like add, edit, delete, list all profile.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Repository
public class ProfileDaoImpl implements ProfileDao {

    private static final Logger logger = LoggerFactory.getLogger(ProfileDaoImpl.class);

    /**
     * @Autowired Spring's dependency injection facilities which instantiate the
     * hibernate session factory bean from spring container
     */

    @Autowired
    private SessionFactory session;
    
    @Autowired
    @Qualifier(value = "entityManager")
    private EntityManager em;

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
     *
     * @param Profile profile object
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
     * @param int user id
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
    	Profile profile=null;
    	try {
     	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Profile> cr=cb.createQuery(Profile.class);
    	Root<Profile> resource = cr.from(Profile.class);
    	cr.select(resource).where(cb.equal(resource.get("profile_name"), profileName),cb.equal(resource.get("user_id"), userId));
        profile=em.createQuery(cr).getSingleResult();
    	}
    	catch(Exception e) {
			if (e instanceof NoResultException)
				return null;
			logger.error("Exception", e);
    	}
    	return profile;
    }


    @Override
    public List<Profile> getProfileListByNameAndUserId(String profileName, int userId) {

		List<Profile> proList = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Profile> cr = cb.createQuery(Profile.class);
			Root<Profile> resource = cr.from(Profile.class);
			cr.select(resource).where(cb.equal(resource.get("profile_name"), profileName), cb.equal(resource.get("user_id"), userId));
			proList = em.createQuery(cr).getResultList();
		} catch (Exception e) {
			if (e instanceof NoResultException)
				return null;
			logger.error("Exception", e);
		}
		return proList;
    }
}
