package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.Profile;

import java.util.List;

/**
 * Interface class for User Profile service layer which is implemented by
 * concrete ProfileServiceImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
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
