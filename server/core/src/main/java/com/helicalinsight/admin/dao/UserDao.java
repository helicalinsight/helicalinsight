package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;

import java.util.List;

/**
 * Interface class for User activity which is implemented by concrete
 * UserDaoImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public interface UserDao {


    /**
     * Implementation class method add the user persistent object in database
     *
     * @param user persistent object
     */

    public int addUser(User user);

    /**
     * Implementation class method update the user persistent object
     *
     * @param user persistent object
     */

    public void editUser(User user);

    /**
     * Implementation class method delete persistent object user by user id from
     * database
     *
     * @param userId user id
     */

    public void deleteUser(int userId);

    /**
     * Implementation class method return the user object by user id from
     * database
     *
     * @param userId user id
     * @return user object
     */

    public User findUser(int userId);
    
    /**
     * Returns the user associated with given id , despite  the organization status.
     * 
     * @param userId
     * @return
     */
    public User getUser(int userId);

    /**
     * Implementation class method return user object by user name with null
     * organization from database
     *
     * @param username user name
     * @return user object
     */

    public User findUserByNameNorgNull(String username, boolean applyFilter);

    /**
     * Implementation class method return user object by user name from database
     *
     * @param username user name
     * @return user object
     */

    public User searchUserByname(String username);

    /**
     * Implementation class method return user object by user name and
     * organization id from database
     *
     * @param username user name
     * @param org_id   organization id
     * @return user object
     */

    public User findUserByNameNOrgId(String username, Integer org_id, boolean applyFilter);

    /**
     * Implementation class method return list of all user object from database
     *
     * @return list of user objects
     */

    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel, boolean applyFilter);

    /**
     * Implementation class method return list of all users belongs to
     * particular organisation by organization id from database
     *
     * @param org_id organization id
     * @return list of user objects
     */

    public List<User> getOrganizationUsers(int org_id, int offset, int limit, LimitOffsetModel limitOffsetModel);

    /**
     * Implementation class method delete the users of organisation belongs
     * argument org_id from database
     *
     * @param org_id organization id
     */

    public void getUserByOrgIdNdelete(Integer org_id);


    public List<User> findUsers(LimitOffsetModel limitOffsetModel);

    public List<User> findOrganisationUsers(Integer orgId, LimitOffsetModel limitOffsetModel);


    public User findByIdAndFetchRolesEagerly(int userId);

    public List<User> searchUserByOrgId(int offset, int limit, Integer searchPhrase, LimitOffsetModel limitOffsetModel);

    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel);

    void initializeData();

	List<User> getAllUsersOfOrganization(Integer orgId);
	
	User isUserAlreadyExisted(String uname, Integer orgId);
	
	Long findUserCount();

	void restoreUser(Integer userId);
}
