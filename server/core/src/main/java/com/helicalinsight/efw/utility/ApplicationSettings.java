package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Scope;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.stereotype.Component;

/**
 * Created by author on 02-09-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class ApplicationSettings {
    private String enableReportSave;
    private String rootDirectoryPermission;
    private String defaultEmailResourceType;
    private boolean provideExportViaHtml;
    private JsonObject settingJson;
    private static boolean samlEnabled;
    private static boolean isSet = false;
    private String samlLogoutUrl;

    public String getSamlLogoutUrl() {
        return samlLogoutUrl;
    }

    public void setSamlLogoutUrl(String samlLogoutUrl) {
        this.samlLogoutUrl = samlLogoutUrl;
    }

    public void setSettingJson(JsonObject settingJson) {
        this.settingJson = settingJson;
    }

    public boolean isSamlEnabled() {
            try {
                ApplicationContextAccessor.getBean(KeyManager.class);
                samlEnabled = true;
            } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                samlEnabled = false;
            }
        return samlEnabled;
    }

    public void setSamlEnabled(boolean samlEnabled) {
        ApplicationSettings.samlEnabled = samlEnabled;
    }

    public JsonObject getSettingJson() {
        return settingJson;
    }

    public ApplicationSettings() {
      init();
    }
    
    public void init() {
    	  ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
          this.enableReportSave = applicationProperties.getEnableSavedResult();
          this.defaultEmailResourceType = applicationProperties.getDefaultEmailResourceType();
          this.provideExportViaHtml = applicationProperties.isProvideExportViaHtml();
          this.rootDirectoryPermission = String.valueOf(ApplicationContextAccessor.getBean
                  (ResourcePermissionLevelsHolder.class).readWriteDeleteAccessLevel());
          this.settingJson = JsonUtils.newGetSettingsJson();
          samlEnabled = false;
          this.samlLogoutUrl = SAMLLogoutFilter.FILTER_URL + "/?";
    }

    public String getEnableReportSave() {
        return enableReportSave;
    }

    public void setEnableReportSave(String enableReportSave) {
        this.enableReportSave = enableReportSave;
    }

    public String getRootDirectoryPermission() {
        return rootDirectoryPermission;
    }

    public void setRootDirectoryPermission(String rootDirectoryPermission) {
        this.rootDirectoryPermission = rootDirectoryPermission;
    }

    public boolean isProvideExportViaHtml() {
        return provideExportViaHtml;
    }

    public void setProvideExportViaHtml(boolean provideExportViaHtml) {
        this.provideExportViaHtml = provideExportViaHtml;
    }

    public String getDefaultEmailResourceType() {
        return defaultEmailResourceType;
    }

    public void setDefaultEmailResourceType(String defaultEmailResourceType) {
        this.defaultEmailResourceType = defaultEmailResourceType;
    }
}
