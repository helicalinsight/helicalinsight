package com.helicalinsight.externalauth.saml;

import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ProfileService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.service.impl.UserDetailsServiceImpl;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.UserSynchronizationUtils;
import com.helicalinsight.externalauth.cas.ExternalAdminUsers;
import org.audit4j.core.schedule.util.StringUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * HISamlUserDetailService extends {@link UserDetailsServiceImpl} and implements {@link SAMLUserDetailsService}
 * This service class is responsible for loading user details and performing synchronization based on SAML credentials.
 *
 * Created by helical021 on 2/9/2021.
 */
@Service
public class HISamlUserDetailService extends UserDetailsServiceImpl implements SAMLUserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(HISamlUserDetailService.class);


    @Autowired
    private RoleService roleService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ExternalAdminUsers externalAdminUsers;


    private Properties samlProperties = new Properties();


    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer;

    private String organizationKey;
    private String profileKey;
    private String roleKey;
    private String emailKey;

    /**
     * getProperties()
     * Initializes SAML-related configuration properties from the "samlKeys.properties" file.
     */
    @PostConstruct
    void getProperties() {

        try {
            samlProperties.load(HISamlUserDetailService.class.getResourceAsStream("/samlKeys.properties"));
            organizationKey = samlProperties.getProperty("organization");
            profileKey = samlProperties.getProperty("profile");
            roleKey = samlProperties.getProperty("role");
            emailKey = samlProperties.getProperty("email");
            if (emailKey == null) {
                emailKey = "";
            }

        } catch (IOException e) {
            logger.error("Problem loading the custom authentication properties file!");
        }
    }
    /**
     * loadUserBySAML(SAMLCredential credential)
     * @param credential           provides data like name, organization details
     * @return userDetails object fetched from credentials
     */
    @Override
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        NameID nameID = credential.getNameID();
        String casUserName = nameID.getValue();
        String combinedUsername = casUserName;
        try {


            String organizationValue = null;
            if (organizationKey != null) {
                organizationValue = credential.getAttributeAsString(organizationKey);
                if (organizationValue != null) {
                    combinedUsername = combinedUsername + ":" + organizationValue;
                }
            }
            UserDetails userDetails = null;
            try {
                userDetails = this.userService.loadUserByUsername(combinedUsername);
                if (userDetails != null) {
                    return userDetails;
                }
            } catch (UsernameNotFoundException unfe) {
                logger.error("The external user is not found", unfe);
            } catch (Exception ignore) {
                logger.error("Unknown exception", ignore);
            }
            sync(credential, casUserName, combinedUsername, organizationValue);
        } catch (Exception ex) {
            logger.error("Exception occurred while loading...{}", ex);
        }
        logger.error("combinedUsername ", combinedUsername);

        return this.userService.loadUserByUsername(combinedUsername);

    }
    /**
     * sync(SAMLCredential credential, String casUserName, String combinedUsername, String organizationValue)
     * Synchronizes user details based on SAML credentials and performs user registration.
	 * 
     * @param credential                  information like name, email address
     * @param casUserName                 cas user name to set organization
     * @param combinedUsername			  consists of username and organization name
     * @param organizationValue			  organization value which associated with user.
     */
    private void sync(SAMLCredential credential, String casUserName, String combinedUsername, String organizationValue) {
        Map<String, String> profilesMap = new HashMap<>();

        List<Attribute> attributesz = credential.getAttributes();
        if (attributesz != null) {
            logger.error("Attributez size" + attributesz.size());
            List<String> profileList = new ArrayList<>();
            if (profileKey != null) {
                String[] split = profileKey.split(",");
                profileList = Arrays.asList(split);
            }
            if (profileList.size() > 0) {
                for (Attribute attribute : attributesz) {

                    String attributeName = attribute.getName();
                    if (attributeName != null) {
                        String attributeValues = credential.getAttributeAsString(attributeName);

                        if (profileList.contains(attributeName)) {
                            profilesMap.put(attributeName, attributeValues);
                        }
                    }

                }
            }


        }

        try {
            String s = XMLHelper.nodeToString(SAMLUtil.marshallMessage(credential.getAuthenticationAssertion()));
            logger.info("Xml obtained is " + s);
        } catch (MessageEncodingException e) {
            logger.error("Exception occurred {}", e);
        }

        UserSynchronizationUtils userSynchronizationUtils = new UserSynchronizationUtils(profileService, roleService, organizationService, userService);

        String emailValue = credential.getAttributeAsString(emailKey);
        Organization organization = userSynchronizationUtils.synchronizeOrg(organizationValue);

        User user = userSynchronizationUtils.synchronizeUser(casUserName, organization);

        if (StringUtils.isEmpty(emailValue)) {
            emailValue = namesConfigurer.getRoleUserEmail();
        }

        userSynchronizationUtils.synchronizeEmails(emailValue, user);


        String role = credential.getAttributeAsString(roleKey == null ? "" : roleKey);
        Set<String> roleSet = new HashSet<>();
        if (role != null) {
            if (role.contains(",")) {
                String[] split = role.split(",");
                for (String item : split) {
                    roleSet.add(item);
                }
            } else {
                roleSet.add(role);
            }
        }

        if (roleSet.isEmpty()) {
            roleSet.add(namesConfigurer.getRoleUser());
        }
        logger.error("Roles is tttttttttttttttttttttttttttttt" + roleSet);
        List<Role> roles = userSynchronizationUtils.synchronizeRoles(roleSet, organization);
        user.setRoles(roles);
        userSynchronizationUtils.updateUser(user);

        if (!profilesMap.isEmpty()) {
            userSynchronizationUtils.synchronizeProfiles(profilesMap, user.getId());
        }

        logger.error(combinedUsername + "EOL<<<<<<<<<<<<<");
    }

}
