package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.dao.ProfileDao;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is service layer of application and this class implements the
 * ProfileService interface and responsible for profile transactional activities
 * like add, edit, delete, list all organisation etc
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private static final String profileRegex= "^[A-Za-z0-9@,. _$-]+$";

    /**
     * @Autowired Spring's dependency injection facilities which instantiate the
     * profileDao bean from spring container
     */

    @Autowired
    ProfileDao profileDao;

    /**
     * this is overloaded method responsible for transaction of saving profile
     */

    @Transactional
    public int add(Profile profile) {
    	if(AdminUtils.isValidName(profile.getProfile_name(),profileRegex)) {
    		return profileDao.add(profile);
    	}
    	throw new EfwServiceException("Profile name can only use A-Z, a-z, @, $, 0-9,,, . and _ and spaces.");
    }

    /**
     * this is overloaded method responsible for transaction of updating profile
     */

    @Transactional
    public void edit(Profile profile) {
    	if(AdminUtils.isValidName(profile.getProfile_name(),profileRegex)) {
    		profileDao.edit(profile);
    	}
    	else {
    		throw new EfwServiceException("Profile name can only use A-Z, a-z, @, $, 0-9,,, . and _ and spaces.");
    	}
    }

    /**
     * this is overloaded method responsible for transaction of deleting profile
     * by id
     */

    @Transactional
    public void delete(int profileId) {
        profileDao.delete(profileId);
    }

    /**
     * this is overloaded method responsible for transaction of loading profile
     * and return the profile object
     *
     * @return profile object
     */

    @Transactional
    public Profile getProfile(int profileId) {
        return profileDao.getProfile(profileId);
    }

    /**
     * this is overloaded method responsible for transaction of loading all
     * existing profile and return list of profile objects
     *
     * @return list of profile objects
     */

    @Transactional
    public List<Profile> getAllProfile() {
        logger.debug("Returning from getAllProfile");
        return profileDao.getAllProfile();
    }

    /**
     * this is overloaded method responsible for transaction of loading profile
     * by id and return list of profile objects
     *
     * @return list of profile objects
     */

    @Transactional
    public List<Profile> getUserProfile(int userId) {
        return profileDao.getUserProfile(userId);
    }

    /**
     * this is overloaded method responsible for transaction of loading profile
     * by profile name and user id and return profile object
     *
     * @return profile object
     */

    @Transactional
    public Profile getProfileByNameAndUserId(String profileName, int userId) {
        return profileDao.getProfileByNameAndUserId(profileName, userId);
    }

    @Transactional
    public List<Profile> getProfileListByNameAndUserId(String profileName, int userId) {
        return profileDao.getProfileListByNameAndUserId(profileName, userId);
    }
}
