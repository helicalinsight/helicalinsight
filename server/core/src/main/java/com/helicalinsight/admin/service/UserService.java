package com.helicalinsight.admin.service;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Interface class for User service which is implemented by concrete
 * UserDetailsServiceImpl class
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public interface UserService extends UserDetailsService {

    /**
     * Implementation service class method add the user persistent object in
     * database
     *
     * @param user persistent object
     */

    int addUser(User user);

    /**
     * Implementation service class method update the user persistent object
     *
     * @param user persistent object
     */

    void editUser(User user);

    /**
     * Implementation service class method delete persistent object user by user
     * id from database
     *
     * @param userId user id
     */

    void deleteUser(int userId);

    /**
     * Implementation service class method return the user object by user id
     * from database
     *
     * @param userId user id
     * @return user object
     */

    User findUser(int userId);

    /**
     * Implementation service class method return user object by user name with
     * null organization from database
     *
     * @param username user name
     * @return user object
     */

    User findUserByNameNorgNull(String username);
    User findUserByNameNorgNull(String username, boolean applyFilter);

    /**
     * Implementation service class method return user object by user name from
     * database
     *
     * @param username user name
     * @return user object
     */

    User searchUserByname(String username);

    /**
     * Implementation service class method return user object by user name and
     * organisation id from database
     *
     * @param username user name
     * @param org_id   organization id
     * @return user object
     */

    User findUserByNameNOrgId(String username, Integer org_id);
    User findUserByNameNOrgId(String username, Integer org_id, boolean applyFilter);

    /**
     * Implementation service class method return list of all user object from
     * database
     *
     * @return list of user objects
     */

    List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel);
    List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel,boolean applyFilter);

    /**
     * Implementation service class method return list of all users belong's to
     * particular organization by organisation id from database
     *
     * @param org_id organization id
     * @return list of user objects
     */

    List<User> getOrganizationUsers(int org_id, int offset, int limit, LimitOffsetModel limitOffsetModel);

    /**
     * Implementation service class method delete the users of organization
     * belongs argument org_id from database
     *
     * @param org_id organization id
     */
    void getUserByOrgIdNdelete(Integer org_id);

    List<User> findUsers(LimitOffsetModel limitOffsetModel);

    List<User> findOrganisationUsers(Integer orgId, LimitOffsetModel limitOffsetModel);

    User findByIdAndFetchRolesEagerly(int userId);

    List<User> searchUserByOrgId(int offset, int limit, Integer searchPharse, LimitOffsetModel limitOffsetModel);

    List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel);

     void initializeData();

	List<User> getAllUsersOfOrganization(Integer orgId);
	User isUserAlreadyExisted(String uname,Integer orgId);
	User getUser(int userId);
	
	Long findUserCount();
	
	void restoreUser(Integer userId);

}
