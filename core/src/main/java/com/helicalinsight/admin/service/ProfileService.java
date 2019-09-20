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

import com.helicalinsight.admin.model.Profile;

import java.util.List;

/**
 * Interface class for User Profile service layer which is implemented by
 * concrete ProfileServiceImpl class
 *
 * @author Muqtar Ahmed
 */
public interface ProfileService {

    /**
     * Implementation service class method add the profile in the database
     *
     * @param profile profile object
     */

    public int add(Profile profile);

    /**
     * Implementation service class method update the profile in the database
     *
     * @param profile profile object
     */

    public void edit(Profile profile);

    /**
     * Implementation service class method delete the profile from the database
     * by profile id
     *
     * @param profileId profile id
     */

    public void delete(int profileId);

    /**
     * Implementation service class method return the profile object by profile
     * id from the database
     *
     * @param profileId profile id
     * @return profile object
     */

    public Profile getProfile(int profileId);

    /**
     * Implementation service class method return the list of all existing
     * profile object from database
     *
     * @return list of all existing profile objects
     */

    public List<Profile> getAllProfile();

    /**
     * Implementation service class method return the list of profile object by
     * user id from database
     *
     * @param userId user id
     * @return list of profile objects
     */

    public List<Profile> getUserProfile(int userId);


    /**
     * Implementation class method return the profile object by user id and
     * profile name from database
     *
     * @param profileName profile name
     * @param userId      user id
     * @return profile Object
     */

    public Profile getProfileByNameAndUserId(String profileName, int userId);


    public List<Profile> getProfileListByNameAndUserId(String profileName, int userId);
}
