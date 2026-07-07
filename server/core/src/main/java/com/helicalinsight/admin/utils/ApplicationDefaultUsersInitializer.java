package com.helicalinsight.admin.utils;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.datasource.model.DSTypeTomcat;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.ApplicationValuesInitializer;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Creates two users. One is admin and the another one is
 * user with admin and user roles respectively when application is being deployed(If the database
 * is empty). Sets the users passwords with the same name as that of the user.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @version 1.1
 */
@Component
public class ApplicationDefaultUsersInitializer implements ApplicationValuesInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDefaultUsersInitializer.class);
    private static final String LOCK_FILE = ".user_init.lock";
    private static final String TEMP_LOCK_FILE = ".user_init.lock.tmp";

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Autowired
    @Qualifier(value = "roleServiceImpl")
    private RoleService roleService;


    @Autowired
    private ApplicationDefaultUserAndRoleNamesConfigurer applicationDefaultUserAndRoleNamesConfigurer;

    @Override
    public void initializeData(ApplicationContext applicationContext) throws BeansException {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String hiRepo = applicationProperties.getSolutionDirectory();

        File lockFile = new File(hiRepo + File.separator + LOCK_FILE);
        File tempFile = new File(hiRepo + File.separator + TEMP_LOCK_FILE);

//  Step 1: If final lock exists → skip
        if (lockFile.exists()) {
            logger.info("Lock file exists. Skipping initialization.");
            return;
        }

//  Step 2: Try acquiring lock (atomic)
        try {
            if (!tempFile.createNewFile()) {
                logger.info("Another instance is initializing users. Skipping...");
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp lock file", e);
        }

        try {
            //  Step 3: DB check AFTER acquiring lock
            Long userCount = this.userService.findUserCount();

            if (userCount != null && userCount > 0) {
                logger.info("Users already exist in DB. Skipping initialization.");

                //  IMPORTANT: cleanup temp lock
                tempFile.delete();
                return;
            }

            //  Step 4: Initialize
            logger.info("Initializing default users...");
            initData();

            //  Step 5: Rename temp → final lock
            boolean renamed = tempFile.renameTo(lockFile);
            if (!renamed) {
                throw new RuntimeException("Failed to rename temp lock to final lock");
            }

            logger.info("User initialization completed successfully.");

        } catch (Exception e) {
            // Cleanup on failure
            tempFile.delete();
            throw e;
        }
    }

    /**
     * This method creates two users with admin and user role
     */
    private void initData() {
        String roleUser = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUser();
        String roleAdmin = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdmin();
        String rolePhantom = this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantom();
        String roleViewer = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleViewer();

        if (logger.isInfoEnabled()) {
            logger.info("Application initialization started. Creating 2 default users with " + "roles for the " +
                    "application.");
        }
        try {
            this.roleService.add(new Role(roleAdmin, null));
            this.roleService.add(new Role(roleUser, null));
            this.roleService.add(new Role(roleViewer,null));

            LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
            List<Role> allRoles = roleService.getAllRole(limitOffsetModel);

            User user = new User();
            String roleAdminName = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdminName();
            user.setUsername(roleAdminName);
            user.setPassword(CipherUtils.encrypt(roleAdminName));
            user.setEnabled(true);
            user.setDeleted(false);
            user.setOrg_id(null);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRoleAdminEmail());
            user.setRoles(allRoles);
            this.userService.addUser(user);


            Role userRole = this.roleService.findByName(roleUser);
            List<Role> userRoleList = new ArrayList<>();
            userRoleList.add(userRole);
            String roleUserName = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUserName();
            user.setUsername(roleUserName);
            user.setPassword(CipherUtils.encrypt(roleUserName));
            user.setEnabled(true);
            user.setOrg_id(null);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRoleUserEmail());
            user.setRoles(userRoleList);
            userService.addUser(user);




            User viewerUser= new User();
            Role viewerRole = this.roleService.findByName(roleViewer);
            List<Role> viewerRoleList = new ArrayList<>();
            viewerRoleList.add(viewerRole);
            String roleViewerName = this.applicationDefaultUserAndRoleNamesConfigurer.getRoleViewerName();
            viewerUser.setUsername(roleViewerName);
            viewerUser.setPassword(CipherUtils.encrypt(roleViewerName));
            viewerUser.setEnabled(true);
            viewerUser.setOrg_id(null);
            viewerUser.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRoleViewerEmail());
            viewerUser.setRoles(viewerRoleList);
            userService.addUser(viewerUser);



            String[] split = rolePhantom.split(",");
            for(String s: split) {
                Role byName = this.roleService.findByName(s);
                if(byName==null)
                    this.roleService.add(new Role(s,null));
            }


            List<Role> phantomRoleList = new ArrayList<>();
            for(String s: split) {
                Role phantomRole = this.roleService.findByName(s);
                phantomRoleList.add(phantomRole);
            }

            String rolePhantomName = this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantomName();
            user.setUsername(rolePhantomName);
            user.setPassword(CipherUtils.encrypt(rolePhantomName));
            user.setEnabled(true);
            user.setOrg_id(null);
            user.setEmailAddress(this.applicationDefaultUserAndRoleNamesConfigurer.getRolePhantomEmail());
            user.setRoles(phantomRoleList);
            userService.addUser(user);


            //TODO NEED TO CHECK THIS
            GlobalConnections globalConnections = new GlobalConnections();
            globalConnections.setCreatedBy("1");
            globalConnections.setId(1);
            globalConnections.setName("SampleTravelDataDerby");
            globalConnections.setType("dynamicDataSource");
            globalConnections.setBaseType("global.jdbc");
            globalConnections.setIsMigrated(false);
            globalConnections.setCreatedDate(new Date());
            globalConnections.setLastUpdatedTime(new Date());
        } catch (Exception ex) {
            logger.error("An exception occurred. ", ex);
        }
    }


}
