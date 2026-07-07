package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.exception.AuthenticationException;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class UserSynchronizationUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserSynchronizationUtils.class);


    private ProfileService profileService;

    private RoleService roleService;

    private OrganizationService organizationService;

    private UserService userService;

    public UserSynchronizationUtils() {

    }

    public UserSynchronizationUtils(ProfileService profileService, RoleService roleService, OrganizationService organizationService, UserService userService) {
        this.profileService = profileService;
        this.roleService = roleService;
        this.organizationService = organizationService;
        this.userService = userService;

    }


    public User synchronizeUser(String requestedUser, Organization organization) {
        User user = new User();
        UserDetails userDetails = null;
        try {
            userDetails = this.userService.loadUserByUsername(requestedUser);
        } catch (Exception ignore) {
            //logger.error("Exception occurred while loading the username");
        }

        if (userDetails == null) {
            user.setUsername(requestedUser);
            user.setIsExternallyAuthenticated(true);
            user.setPassword(CipherUtils.encrypt(""));
            user.setEnabled(true);
            user.setOrg_id(organization!=null?organization.getId():null);
            user.setOrganization(organization);
            user.setIsExternallyAuthenticated(true);
            user = saveUser(user, requestedUser);
            return user;
        } else {
            if (organization == null) {
                //userName=
                user = this.userService.findUserByNameNorgNull(requestedUser.trim());
            } else {
                user = this.userService.findUserByNameNOrgId(requestedUser.trim(), organization.getId());
            }
        }
        this.userService.editUser(user);
        return user;
    }


    public synchronized User saveUser(User user, String combinedUsername) {
        logger.debug("About to save user " + user);
        try {
            UserDetails userDetails;
            userDetails = this.userService.loadUserByUsername(combinedUsername);
            logger.debug("User Found !!!" + userDetails);
        } catch (UsernameNotFoundException unfe) {
            this.userService.addUser(user);
            logger.debug("Saving user" + user);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        User requestedUser;
        if (user.getOrg_id() == null) {
            requestedUser = this.userService.findUserByNameNorgNull(user.getUsername());
        } else {
            logger.debug("Helical org id is not null!!!");
            requestedUser = this.userService.findUserByNameNOrgId(user.getUsername(), user.getOrg_id());
        }
        logger.debug("Returning user " + requestedUser);
        return requestedUser;
    }


    public synchronized List<Role> synchronizeRoles(Set<String> roles, Organization organizationId) {
        return addRolesFromSetToList(organizationId, roles);
    }

    public synchronized void updateUser(User user) {
        this.userService.editUser(user);
    }


    private List<Role> addRolesFromSetToList(Organization organizationId, Set<String> roles) {
        List<Role> userRoleList = new ArrayList();
        if (null != roles) {
            for (String roleString : roles) {
                Role byName;
                if (organizationId == null) {
                    byName = this.roleService.findByName(roleString.trim());
                } else {
                    byName = this.roleService.findRoleByNameNOrgId(roleString.trim(), organizationId.getId());
                }
                Integer roleId;
                if (byName == null) {
                    roleId = this.roleService.add(new Role(roleString.trim(), organizationId == null ? null : organizationId.getId()));
                } else {
                    roleId = byName.getId();
                }
                userRoleList.add(this.roleService.getRole(roleId));
            }
        }
        return userRoleList;
    }


    public synchronized List<Profile> synchronizeProfiles(Map<String, String> profileMap, Integer userId) {
        List<Profile> userProfileList = new ArrayList();
        for (String profileName : profileMap.keySet()) {
            String profileValue = profileMap.get(profileName);
            Profile profileObj = new Profile();
            profileObj.setProfile_name(profileName);
            profileObj.setProfile_value(profileValue);
            profileObj.setUser_id(userId);
            profileService.add(profileObj);
            userProfileList.add(profileObj);
        }

        return userProfileList;
    }

    public List<String> rolesToList(List<Role> roles) {
        List<String> roleList = new ArrayList();
        for (Role role : roles) {
            roleList.add(role.getRole_name());
        }
        return roleList;
    }


    public synchronized void synchronizeEmails(String requestedEmail, User user) {
        if (user != null) {
            String email = user.getEmailAddress();
            String[] requestedEmailArr;
            if (StringUtils.isNotEmpty(requestedEmail)) {
                requestedEmailArr = requestedEmail.split(Pattern.quote(","));
                if (requestedEmailArr.length > 1) {
                    throw new AuthenticationException("Multiple emails should not be assigned");
                } else {
                    user.setEmailAddress(requestedEmail);
                }
            } else if (StringUtils.isBlank(email)) {
               /* if (StringUtils.isBlank(email.trim())) {
                    throw new AuthenticationException("Email Should either be present in token or property file");
                }*/
                user.setEmailAddress(email);
            }
            userService.editUser(user);
        }
    }


    public synchronized Organization synchronizeOrg(String companyName) {
        Organization org;
        String[] companyNameArr;

        if (StringUtils.isNotEmpty(companyName)) {
            companyName = companyName.trim();
            org = this.organizationService.getOrganization(companyName);
            companyNameArr = companyName.split(Pattern.quote(","));
            if (companyNameArr.length > 1) {
                throw new AuthenticationException("Multiple company names cannot not be assigned");
            }
        } else {
            org = this.organizationService.getOrganization(null);
        }

        if (StringUtils.isNotEmpty(companyName) && org == null) {
            Organization newOrganization = new Organization();
            newOrganization.setOrg_desc("");
            newOrganization.setOrg_name(companyName);
            Integer orgId = this.organizationService.add(newOrganization);
            newOrganization = this.organizationService.findOrganization(orgId, new LimitOffsetModel());
            return newOrganization;
        }
        return org;
    }


}
