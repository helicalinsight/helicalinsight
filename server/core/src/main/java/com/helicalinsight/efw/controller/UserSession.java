package com.helicalinsight.efw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.JsonUtils;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UserSession {
    public void addSessionDetails(String service, HttpServletRequest request, JsonObject formJson) {
        String jwtToken = (String) request.getAttribute("jwtToken");
        if ("getContents".equalsIgnoreCase(service)) {
            JsonObject responseJson = new JsonObject();
            long currentTime = System.currentTimeMillis();
            HttpSession session = request.getSession(false);
            responseJson.addProperty("serverTime", currentTime);
            if (session != null) {
                int maxInactiveInterval = session.getMaxInactiveInterval();
                long expiryTime = currentTime + maxInactiveInterval * 1000L;
                responseJson.addProperty("expiryTime", expiryTime);
            }
            addUserData(responseJson);
            addMapData(formJson);
            ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
            JsonObject saml = samlDetails(applicationSettings);
            responseJson.addProperty("rootDirectoryPermission", applicationSettings.getRootDirectoryPermission());
            responseJson.addProperty("provideHTMLExport", applicationSettings.isProvideExportViaHtml());
            responseJson.addProperty("enableReportSave", applicationSettings.getEnableReportSave());
            responseJson.addProperty("defaultEmailResourceType", applicationSettings.getDefaultEmailResourceType());

            responseJson.add("saml", saml);
            responseJson.addProperty("jwtToken", jwtToken);
//            JSONObject settingJson2 = applicationSettings.getSettingJson();
//            JsonObject settingJson = new Gson().fromJson(settingJson2.toString(), JsonObject.class);
            JsonObject settingJson = applicationSettings.getSettingJson();
            
            String baseURL = "";
            if (GsonUtility.optBooleanValue(settingJson, "defaultBaseurl", true)) {
                String url = request.getRequestURL().toString();
                baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";

            } else {
                baseURL = settingJson.get("BaseUrl").getAsString();
                baseURL = baseURL.replace("hi.html", "");
            }
            responseJson.addProperty("baseUrl", baseURL);

            formJson.add("userData", responseJson);
        }
    }

    @NotNull
    private JsonObject samlDetails(ApplicationSettings applicationSettings) {
        JsonObject saml = new JsonObject();
        boolean samlEnabled = applicationSettings.isSamlEnabled();
        saml.addProperty("enabled", samlEnabled);
        saml.addProperty("logoutLink", applicationSettings.getSamlLogoutUrl());

        if (samlEnabled) {
            MetadataManager mm = ApplicationContextAccessor.getBean(MetadataManager.class);
            Set<String> idps = mm.getIDPEntityNames();
            List<String> idAlias = new ArrayList();
            List<String> idpsNew = new ArrayList();
            for (String idp : idps) {

                if (StringUtils.isNotBlank(idp)) {
                    int beginIndex = idp.indexOf(".");
                    int secondDot = idp.indexOf(".", beginIndex + 1);
                    String similar;
                    if (secondDot > -1) {
                        similar = idp.substring(beginIndex + 1, secondDot);
                    } else {
                        int firstWord = idp.indexOf("://");
                        similar = idp.substring(firstWord + 3, beginIndex);
                    }

                    idAlias.add(similar);
                    idpsNew.add(idp);
                }
            }
            saml.add("samlIds", new Gson().fromJson(idpsNew.toString(),JsonArray.class));
            saml.add("samlIdValues", new Gson().fromJson(idAlias.toString(),JsonArray.class));
        }
        return saml;
    }

    @NotNull
    private void addUserData(JsonObject responseJson) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            responseJson.addProperty("user", "{}");
            return;
        }
        List<String> userRoles = AuthenticationUtils.getUserRoles();
        String userName = AuthenticationUtils.getUserName();
        Principal userDetails = AuthenticationUtils.getUserDetails();
        String organization = AuthenticationUtils.getOrganization();
        List<Profile> profiles = userDetails.getProfiles();
        JsonArray profilesJson = new JsonArray();
        for (Profile profile : profiles) {
            JsonObject item = new JsonObject();
            item.addProperty("profileName", profile.getProfile_name());
            item.addProperty("profileValue", profile.getProfile_value());
            profilesJson.add(item);
        }

        String actualUserName = userName;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority item : authorities) {
            if (item instanceof SwitchUserGrantedAuthority) {
                SwitchUserGrantedAuthority sw = (SwitchUserGrantedAuthority) item;
                Principal principal = (Principal) sw.getSource().getPrincipal();
                actualUserName = principal.getUsername();
            }
        }
        String emailAddress = userDetails.getEmailAddress();
        responseJson.addProperty("sessionUserName", userName);
        responseJson.addProperty("sessionUserEmail", emailAddress);
        responseJson.addProperty("sessionUserOrganization", organization);
        JsonObject user = new JsonObject();
        user.addProperty("name", userName);
        user.addProperty("email", emailAddress);

        user.addProperty("actualUserName", actualUserName);
        user.addProperty("organization", organization == null ? "" : organization);
        user.add("roles", new Gson().fromJson(userRoles.toString(),JsonArray.class));
        user.add("profile", profilesJson);
        responseJson.add("user", user);
    }
    
    private void addMapData(JsonObject responseJson) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }
        responseJson.add("map", new Gson().fromJson(JsonUtils.getMapProperties(),JsonObject.class));
    }
}
