package com.helicalinsight.admin.service.impl;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.exception.AuthenticationException;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is service layer of application and this class implements the
 * UserService interface of spring security This is main class for
 * authentication and authorization check for user
 * authentication and authorization check for user
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private static final String userNameRegex = "^[A-Za-z0-9_'\\u2014&\\-\\s.,$@+!/]+$";
    
    /**
     * @Autowired Spring's dependency injection facilities which instantiate the
     * UserDao bean from spring container
     */

    @Autowired
    private UserDao userDao;

    /**
     * @Autowired Spring's dependency injection facilities which instantiate the
     * OrganizationDao bean from spring container
     */

    @Autowired
    private OrganizationDao organizationDao;

    /**
     * This is overloaded method check for user authentication and authorization
     * and return the UserDetails Object if user is authenticated
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String combineUserName) throws UsernameNotFoundException,
            DataAccessException {
        Organization organization;
        Principal principal;
        User user;

        String[] split;
        String username;
        String organizationName = "";

        if (combineUserName.contains(":")) {
            split = combineUserName.split(":");
            username = split[0];
            organizationName = split[1];
            organization = organizationDao.getOrganization(organizationName);
            if(organization==null){
                throw  new AuthenticationException("The organization does not exists");
            }
            // int org_id = organization.getId();
            // user = findUserByNameNOrgId(username, Integer.toString(org_id));
            Integer org_id = organization.getId();
            user = findUserByNameNOrgId(username, org_id);
        } else {
            username = combineUserName;
            user = userDao.findUserByNameNorgNull(username,true);
        }

        if (user != null) {
            // Let's populate user roles
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getRole_name()));
            }
            String password = user.getPassword();
            if ("".equals(organizationName)) {
                principal = new Principal(username, password==null?"":CipherUtils.decrypt(password), user.isEnabled(), true, true, true,
                        authorities, user.getEmailAddress(), user.getProfile(), null, user);
            } else {
                // code for profile
                principal = new Principal(username, password==null?"":CipherUtils.decrypt(password), user.isEnabled(), true, true, true,
                        authorities, user.getEmailAddress(), user.getProfile(), user.getOrganization().getOrg_name(),
                        user);
            }
            logger.info("User Authenticated");
            return principal;
        } else {
            throw new UsernameNotFoundException("User Not Found!!!");
        }
    }

    /**
     * this is override method responsible for transaction of loading user
     * object by name and organization id and return the user object by it's
     * user name and organization
     *
     * @param username user name
     * @param org_id   organization id
     * @return user object
     */

    @Transactional
    public User findUserByNameNOrgId(String username, Integer org_id) {
        return userDao.findUserByNameNOrgId(username, org_id,true);
    }

    /**
     * this is overloaded method responsible for transaction of saving user
     * object
     */

    @Transactional
    public int addUser(User user) {
    	if(AdminUtils.isValidName(user.getUsername(),userNameRegex) 
    			&& AdminUtils.isValidEmail(user.getEmailAddress())) {
    		user.setDeleted(false);
    		return userDao.addUser(user);
    	}
		throw new EfwServiceException("Username can only use A-Z, a-z, 0-9, -, _,—, ', &, . , @, $, +, !, / and can have spaces.");
    }

    /**
     * this is override method responsible for transaction of updating user
     * object
     */

    @Transactional
    public void editUser(User user) {
    	if(AdminUtils.isValidName(user.getUsername(),userNameRegex)) {
    		userDao.editUser(user);
    	} else {
    		throw new EfwServiceException("Username can only use A-Z, a-z, 0-9, -, _,—, ', &, . , @, $, +, !, / and can have spaces.");
    	}
    }

    /**
     * this is override method responsible for transaction of deleting the user
     * object by user id
     *
     * @param userId user id
     */

    @Transactional
    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    /**
     * this is override method responsible for transaction of loading the user
     * object by it's user id and return the user object
     *
     * @param userId user id
     * @return user object
     */

    @Transactional
    public User findUser(int userId) {
        User user =  userDao.findUser(userId);
        if( null == user ) {
        	throw new EfwServiceException("User not found.");
        }
        return user;
    }

    /**
     * this is override method responsible for transaction of loading the user
     * object by user name and who's organisation is null
     *
     * @param username user name
     * @return user object
     */

    @Transactional
    public User findUserByNameNorgNull(String username) {
        return userDao.findUserByNameNorgNull(username,true);
    }

    /**
     * this is override method responsible for transaction of loading all
     * existing user objects and return the list of all existing user objects
     *
     * @return list of user objects
     */

    @Transactional
    public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel) {
        return userDao.getAllUsers(offset, limit, limitOffsetModel,false);
    }

    /**
     * this is override method responsible for transaction of loading user
     * object and return the user object by user name
     *
     * @param username user name
     * @return user object
     */

    @Transactional
    public User searchUserByname(String username) {
        return userDao.searchUserByname(username);
    }

    /**
     * this is override method responsibe for transaction of delete all the
     * user's by it's organisation id
     */

    @Transactional
    public void getUserByOrgIdNdelete(Integer org_id) {
        userDao.getUserByOrgIdNdelete(org_id);

    }

    /**
     * this is override method responsible for transaction of load list of users
     * by it's organisation id and return the list of user's by it's
     * organisation id
     *
     * @return list of user objects
     */

    @Transactional
    public List<User> getOrganizationUsers(int org_id, int offset, int limit, LimitOffsetModel limitOffsetModel) {
        return userDao.getOrganizationUsers(org_id, offset, limit, limitOffsetModel);
    }

    @Transactional
    public User findByIdAndFetchRolesEagerly(int userId) {
        return userDao.findByIdAndFetchRolesEagerly(userId);
    }

    @Transactional
    public List<User> findUsers(LimitOffsetModel limitOffsetModel) {
        return userDao.findUsers(limitOffsetModel);
    }

    @Transactional
    public List<User> findOrganisationUsers(Integer orgId, LimitOffsetModel limitOffsetModel) {
        return userDao.findOrganisationUsers(orgId, limitOffsetModel);
    }

    @Transactional
    public List<User> searchUserByOrgId(int offset, int limit, Integer searchPhrase,
                                        LimitOffsetModel limitOffsetModel) {
        return userDao.searchUserByOrgId(offset, limit, searchPhrase, limitOffsetModel);
    }

    @Override
    public List<User> searchUserByRole(int offset, int limit, String role, LimitOffsetModel limitOffsetModel) {
        return userDao.searchUserByRole(offset, limit, role, limitOffsetModel);
    }

    @Override
    @Transactional
    public  void initializeData(){
        userDao.initializeData();
    }

    @Transactional
	@Override
	public List<User> getAllUsersOfOrganization(Integer orgId) {
		return userDao.getAllUsersOfOrganization(orgId);
	}

    @Transactional
	@Override
	public List<User> getAllUsers(int offset, int limit, LimitOffsetModel limitOffsetModel, boolean applyFilter) {
		return userDao.getAllUsers(offset, limit, limitOffsetModel, applyFilter);
	}
    
    @Transactional
	@Override
	public User findUserByNameNorgNull(String username, boolean applyFilter) {
		return userDao.findUserByNameNorgNull(username, applyFilter);
	}

    @Transactional
	@Override
	public User findUserByNameNOrgId(String username, Integer orgId, boolean applyFilter) {
		return userDao.findUserByNameNOrgId(username, orgId, applyFilter);
	}

	@Override
	@Transactional
	public User isUserAlreadyExisted(String uname, Integer orgId) {
		return userDao.isUserAlreadyExisted(uname, orgId);
	}

	@Transactional
	@Override
	public User getUser(int userId) {
		return userDao.getUser(userId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Long findUserCount() {
		return userDao.findUserCount();
	}
	
	@Transactional
	@Override
	public void restoreUser(Integer userId) {
		userDao.restoreUser(userId);
	}

}
