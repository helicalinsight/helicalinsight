package com.helicalinsight.externalauth.cas;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.service.impl.UserDetailsServiceImpl;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * CasUserDetailService class implements {@link UserDetailsServiceImpl}
 * this class responsible for getting user details using user Name. 
 * Created by user on 2/11/2016.
 * @author Somen
 */
@Service
public class CasUserDetailService extends UserDetailsServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CasUserDetailService.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;
    @Autowired
    private ExternalAdminUsers externalAdminUsers;
    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer;
    
    /**
     * loadUserByUsername(String casUserName)
     * this method is used to get all user details using user name. 
     * @param casUserName    user name in string form
     * @return userDetails object.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String casUserName) throws UsernameNotFoundException, DataAccessException {
        String combinedUsername = casUserName;
        if (!externalAdminUsers.getAdminUsers().contains(casUserName)) {
            combinedUsername = combinedUsername + ":" + externalAdminUsers.getExternalOrganization();
        }
        UserDetails userDetails = null;
        try {
            userDetails = super.loadUserByUsername(combinedUsername);
        } catch (UsernameNotFoundException unfe) {
            logger.error("The external user is not found", unfe);
        } catch (Exception ignore) {
            logger.error("Unknown exception", ignore);
        }
        if (userDetails == null) {
            this.synchronize(casUserName);
        }
        logger.info(combinedUsername);
        return super.loadUserByUsername(combinedUsername);
    }

	/**
	 * synchronize(String userName)
	 * This method creates or updates a user record in
	 * the system based on whether the user is an external admin or a regular user.
	 * @param userName user name in String format
	 */
    @Transactional(propagation = Propagation.REQUIRED)
    public void synchronize(String userName) {
        User user = new User();
        Integer organization;
        boolean isAdmin;

        Role userRole;
        isAdmin = externalAdminUsers.getAdminUsers().contains(userName);
        String roleUser;

        if (isAdmin) {
            organization = null;
            roleUser = namesConfigurer.getRoleAdmin();
            userRole = roleService.findByName(roleUser);
        } else {
            String externalOrganization = externalAdminUsers.getExternalOrganization();
            Organization org = organizationService.getOrganization(externalOrganization);
            if (org != null) {
                organization = org.getId();
            } else {
                Organization newOrganization = new Organization();
                newOrganization.setOrg_desc("External User Organization for cas");
                newOrganization.setOrg_name(externalOrganization);
                organization = organizationService.add(newOrganization);
                roleService.add(new Role(this.namesConfigurer.getRoleAdmin(), organization));
                roleService.add(new Role(this.namesConfigurer.getRoleUser(), organization));
                logger.info("organization is " + organization);
            }
            roleUser = namesConfigurer.getRoleUser();
            user.setOrganization(organizationService.getOrganization(organization));
            userRole = roleService.findRoleByNameNOrgId(roleUser, organization);
        }

        user.setOrg_id(organization);


        List<Role> userRoleList = new ArrayList<>();
        userRoleList.add(userRole);
        user.setRoles(userRoleList);
        user.setUsername(userName);
        user.setIsExternallyAuthenticated(true);
        user.setPassword("");
        user.setEnabled(true);
        userService.addUser(user);
        logger.info("User  addded");
    }
}
