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
    private String roleViewer;
    private String rolePhantomName;
    private String roleViewerName;
    private String rolePhantomEmail;
    private String roleViewerEmail;

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


                JSONObject roleViewer = defaultRoleNames.getJSONObject("roleViewer");
                this.roleViewer = roleViewer.getString("#text");
                this.roleViewerName = roleViewer.getString("@name");
                this.roleViewerEmail = roleViewer.getString("@email");



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

        this.roleViewer = "ROLE_VIEWER";
        this.roleViewerEmail = "hiviewer@helicalinsight.com";
        this.roleViewerName = "hiviewer";

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

    public String getRoleViewer() {
        return roleViewer;
    }

    public void setRoleViewer(String roleViewer) {
        this.roleViewer = roleViewer;
    }

    public String getRoleViewerName() {
        return roleViewerName;
    }

    public void setRoleViewerName(String roleViewerName) {
        this.roleViewerName = roleViewerName;
    }

    public String getRoleViewerEmail() {
        return roleViewerEmail;
    }

    public void setRoleViewerEmail(String roleViewerEmail) {
        this.roleViewerEmail = roleViewerEmail;
    }
}
