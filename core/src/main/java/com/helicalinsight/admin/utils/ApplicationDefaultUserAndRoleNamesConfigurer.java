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

package com.helicalinsight.admin.utils;

import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by author on 29-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("singleton")
@SuppressWarnings("ALL")
public class ApplicationDefaultUserAndRoleNamesConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDefaultUserAndRoleNamesConfigurer.class);
    private String rolePhantom;
    private String rolePhantomName;
    private String rolePhantomEmail;

    private String roleUser;

    private String roleUserName;

    private String roleUserEmail;

    private String roleAdmin;

    private String roleAdminName;

    private String roleAdminEmail;

    public ApplicationDefaultUserAndRoleNamesConfigurer(String roleUser, String roleAdmin) {
        this.roleUser = roleUser;
        this.roleAdmin = roleAdmin;
    }

    public ApplicationDefaultUserAndRoleNamesConfigurer() {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        try {
            if (settingsJson.has("defaultRoleNames")) {
                JSONObject defaultRoleNames = settingsJson.getJSONObject("defaultRoleNames");
                JSONObject roleUserJson = defaultRoleNames.getJSONObject("roleUser");
                this.roleUser = roleUserJson.getString("#text");
                this.roleUserName = roleUserJson.getString("@name");
                this.roleUserEmail = roleUserJson.getString("@email");

                JSONObject roleAdminJson = defaultRoleNames.getJSONObject("roleAdmin");
                this.roleAdmin = roleAdminJson.getString("#text");
                this.roleAdminName = roleAdminJson.getString("@name");
                this.roleAdminEmail = roleAdminJson.getString("@email");

                JSONObject rolePhantom = defaultRoleNames.getJSONObject("rolePhantom");
                this.rolePhantom = rolePhantom.getString("#text");
                this.rolePhantomName = rolePhantom.getString("@name");
                this.rolePhantomEmail = rolePhantom.getString("@email");
            }
        } catch (Exception ex) {
            logger.info("Exception", ex);
            init();
        }
        if (this.roleAdmin == null || this.roleUser == null) {
            init();
        }
    }

    private void init() {
        if (logger.isInfoEnabled()) {
            logger.info("The configuration file has no information related to the default users " + "and role names. " +
                    "Using standard naming convention.");
        }
        //In case if the configuration is not found.
        this.roleUser = "ROLE_USER";
        this.roleUserName = "hiuser";
        this.roleUserEmail = "user@helicalinsight.com";

        this.roleAdmin = "ROLE_ADMIN";
        this.roleAdminName = "hiadmin";
        this.roleAdminEmail = "admin@helicalinsight.com";

        this.rolePhantom = "ROLE_PHANTOM";
        this.rolePhantomEmail = "phantom@helicalinsight.com";
        this.rolePhantomName = "phantom";
    }

    public String getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(String roleUser) {
        this.roleUser = roleUser;
    }

    public String getRoleAdmin() {
        return roleAdmin;
    }

    public void setRoleAdmin(String roleAdmin) {
        this.roleAdmin = roleAdmin;
    }

    public String getRoleUserName() {
        return roleUserName;
    }

    public void setRoleUserName(String roleUserName) {
        this.roleUserName = roleUserName;
    }

    public String getRoleUserEmail() {
        return roleUserEmail;
    }

    public void setRoleUserEmail(String roleUserEmail) {
        this.roleUserEmail = roleUserEmail;
    }

    public String getRolePhantom() {
        return rolePhantom;
    }

    public void setRolePhantom(String rolePhantom) {
        this.rolePhantom = rolePhantom;
    }

    public String getRolePhantomName() {
        return rolePhantomName;
    }

    public void setRolePhantomName(String rolePhantomName) {
        this.rolePhantomName = rolePhantomName;
    }

    public String getRolePhantomEmail() {
        return rolePhantomEmail;
    }

    public void setRolePhantomEmail(String rolePhantomEmail) {
        this.rolePhantomEmail = rolePhantomEmail;
    }

    public String getRoleAdminName() {
        return roleAdminName;
    }

    public void setRoleAdminName(String roleAdminName) {
        this.roleAdminName = roleAdminName;
    }

    public String getRoleAdminEmail() {
        return roleAdminEmail;
    }

    public void setRoleAdminEmail(String roleAdminEmail) {
        this.roleAdminEmail = roleAdminEmail;
    }
}
