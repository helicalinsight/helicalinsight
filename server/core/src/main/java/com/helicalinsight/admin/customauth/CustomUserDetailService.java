package com.helicalinsight.admin.customauth;

import com.helicalinsight.admin.exception.AuthenticationException;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AdminUtils;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class CustomUserDetailService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);


    private RoleService roleService;
    private OrganizationService organizationService;
    private UserService userService;
    private ProfileService profileService;
    private ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer;
    private Properties defaultProperties = new Properties();

    public RoleService getRoleService() {
        return this.roleService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public OrganizationService getOrganizationService() {
        return this.organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public UserService getUserService() {
        return this.userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProfileService getProfileService() {
        return this.profileService;
    }

    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    public ApplicationDefaultUserAndRoleNamesConfigurer getNamesConfigurer() {
        return this.namesConfigurer;
    }

    public void setNamesConfigurer(ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer) {
        this.namesConfigurer = namesConfigurer;
    }

    public void processToken(HttpServletRequest request,String encryptedToken)
            throws IOException {

        String token = CipherUtils.decryptString(encryptedToken);
        TokenValidator tokenValidator = new TokenValidator(token);
        Map<String, String> tokenMap = tokenValidator.processToken();
        this.defaultProperties=tokenValidator.getDefaultProperties();
        String combinedUsername=synchronize(tokenMap);
        request.setAttribute("userName", combinedUsername.trim());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String synchronize(Map<String, String> userDetailsMap) {
        User user = new User();
        String userName = userDetailsMap.get("username");
        Integer userId = null;
        Organization organization = synchronizeOrg(userDetailsMap);
        String combinedUsername = null;
        String timeZone = defaultProperties.getProperty("defaultTimezone");

        if(StringUtils.isBlank(defaultProperties.getProperty("defaultEmail")) && StringUtils.isBlank(userDetailsMap.get("email"))){
            throw new AuthenticationException("Email Should either be present in token or property file");
        }

        if(!StringUtils.isBlank(userDetailsMap.get("email"))){
            if(userDetailsMap.get("email").split(Pattern.quote(",")).length>1){
                throw new AuthenticationException("Multiple Emails Cannot be Assigned");
            }
        }else if(!StringUtils.isBlank(defaultProperties.getProperty("defaultEmail"))){
            if(defaultProperties.getProperty("defaultEmail").split(Pattern.quote(",")).length>1){
                throw new AuthenticationException("Multiple Emails Cannot be Assigned");
            }
        }

        if(organization==null){
            combinedUsername = userName.trim();
        }else{
            combinedUsername = userName.trim() + ":" + organization.getOrg_name().trim();
        }
        List<Role> userRoleList = synchronizeRoles(userDetailsMap, organization);
        UserDetails userDetails = null;
        try {
            userDetails = this.userService.loadUserByUsername(combinedUsername);
        } catch (UsernameNotFoundException unfe) {
            //logger.error("Requested UserName is not found.");
        } catch (Exception ignore) {
            logger.error("Exception occurred while loading the username");
        }

        if (userDetails == null) {
            if (organization == null) {
                user.setOrganization(null);
            } else {
                user.setOrg_id(organization.getId());
            }
            user.setUsername(userDetailsMap.get("username"));
            user.setIsExternallyAuthenticated(true);
            user.setPassword("");
            user.setEnabled(true);
            user = saveUser(user, combinedUsername);
        } else {
            if (organization == null) {
                //userName=
                user = this.userService.findUserByNameNorgNull(userDetailsMap.get("username").trim());
            } else {
                user = this.userService.findUserByNameNOrgId(userDetailsMap.get("username").trim(), organization.getId());
            }
        }
        List<Profile> profileList=profileService.getUserProfile(user.getId());
        if(profileList.size()>0){
            for(Profile profile:profileList){
                profileService.delete(profile.getId());
            }
        }

        user.setRoles(null);
        synchronizeEmails(userDetailsMap, user);
        this.userService.editUser(user);
        List<Profile> userProfileList = synchronizeProfiles(userDetailsMap, user.getId());
        user.setProfile(userProfileList);
        user.setRoles(userRoleList);
        this.userService.editUser(user);
        return combinedUsername;
    }

    public synchronized void synchronizeEmails(Map<String, String> userDetailsMap, User user){
        if (user != null) {
            String email = user.getEmailAddress();
            String requestedEmail = userDetailsMap.get("email");
            String[] requestedEmailArr = null;
            if(StringUtils.isNotEmpty(requestedEmail)){
                requestedEmailArr=requestedEmail.split(Pattern.quote(","));
                if(requestedEmailArr.length>1){
                    throw new AuthenticationException("Multiple emails should not be assigned");
                }else{
                    user.setEmailAddress(requestedEmail);
                }
            }else if(StringUtils.isBlank(email)){
                email = defaultProperties.getProperty("defaultEmail");
                requestedEmailArr=email.split(Pattern.quote(","));
                if(requestedEmailArr.length>1){
                    throw new AuthenticationException("Multiple emails should not be assigned");
                }
                if (StringUtils.isBlank(email.trim())) {
                    throw new AuthenticationException("Email Should either be present in token or property file");
                }
                user.setEmailAddress(email);
            }
            userService.editUser(user);
        }
    }

    public synchronized List<Profile> synchronizeProfiles(Map<String, String> userDetailsMap, Integer userId){
        userDetailsMap.remove("username");
        userDetailsMap.remove("company");
        userDetailsMap.remove("role");
        userDetailsMap.remove("expTime");
        userDetailsMap.remove("email");

        Set<String> profileKeys=userDetailsMap.keySet();
        List<Profile> userProfileList = new ArrayList();
        for(String key:profileKeys){
            String  profileName = key;
            String  profileValue = userDetailsMap.get(key);
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

    public List<String> profileToList(List<Profile> profiles) {
        List<String> profileList = new ArrayList();
        for (Profile profile : profiles) {
            profileList.add(profile.getProfile_name());
        }
        return profileList;
    }

    public synchronized Organization synchronizeOrg(Map<String, String> userDetailsMap){
        Organization org = null;
        String companyName=null;
        String[] companyNameArr = null;

        if(!userDetailsMap.containsKey("company")){
            companyName = defaultProperties.getProperty("defaultCompany");
            companyNameArr = companyName.split(Pattern.quote(","));
            companyName = companyName.trim();
            if (StringUtils.isBlank(companyName)) {
                companyName = null;
            }
            if(companyNameArr.length>1){
                throw new AuthenticationException("Multiple company names should not be assigned");
            }
        }else{
            companyName =  userDetailsMap.get("company");
        }
        if(StringUtils.isNotEmpty(companyName)){
            companyName = companyName.trim();
            if(companyName.isEmpty()){
                companyName = defaultProperties.getProperty("defaultCompany");
            }
            org = this.organizationService.getOrganization(companyName);
            companyNameArr=companyName.split(Pattern.quote(","));
            if(companyNameArr.length>1){
                throw new AuthenticationException("Multiple company names cannot not be assigned");
            }
        }else{
            org = this.organizationService.getOrganization(null);
        }

       if (StringUtils.isNotEmpty(companyName) && org==null) {
            Organization newOrganization = new Organization();
            newOrganization.setOrg_desc("");
            newOrganization.setOrg_name(companyName);
            Integer orgId=Integer.valueOf(this.organizationService.add(newOrganization));
            newOrganization=this.organizationService.findOrganization(orgId,new LimitOffsetModel());
            return newOrganization;
        }
        return org;
    }

    public synchronized List<Role> synchronizeRoles(Map<String, String> userDetailsMap, Organization organizationId){
        String roleUser = defaultProperties.getProperty("defaultRole");
        Set<String> roles = new HashSet<>();
        String[] roleArray = null;
        if (StringUtils.isNotEmpty(roleUser)) {
            roleArray = roleUser.split(Pattern.quote(","));
            roles = new HashSet<>(Arrays.asList(roleArray));
        }
        if (userDetailsMap.containsKey("role")) {
            String role = userDetailsMap.get("role");
            if (StringUtils.isNotEmpty(role.trim())) {
                roleArray = ((String) role).split(Pattern.quote(","));
                roles.addAll(new HashSet<>(Arrays.asList(roleArray)));
            }
        }
        return addRolesFromSetToList(organizationId, roles);
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
                    roleId = this.roleService.add(new Role(roleString.trim(), organizationId == null ? null : organizationId.getId())) ;
                } else {
                    roleId = byName.getId();
                }
                userRoleList.add(this.roleService.getRole(roleId));
            }
        }
        return userRoleList;
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

        User requestedUser = null;
        if (user.getOrg_id() == null) {
            requestedUser = this.userService.findUserByNameNorgNull(user.getUsername());
        } else {
            logger.debug("Helical org id is not null!!!");
            requestedUser = this.userService.findUserByNameNOrgId(user.getUsername(), user.getOrg_id());
        }
        logger.debug("Returning user " + requestedUser);
        return requestedUser;
    }
}
