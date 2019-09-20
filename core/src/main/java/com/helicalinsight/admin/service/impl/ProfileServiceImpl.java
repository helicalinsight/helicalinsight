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

import com.helicalinsight.admin.dao.ProfileDao;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This is service layer of application and this class implements the
 * ProfileService interface and responsible for profile transactional activities
 * like add, edit, delete
 *
 * @author Muqtar Ahmed
 */

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

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
        return profileDao.add(profile);
    }

    /**
     * this is overloaded method responsible for transaction of updating profile
     */

    @Transactional
    public void edit(Profile profile) {
        profileDao.edit(profile);
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
