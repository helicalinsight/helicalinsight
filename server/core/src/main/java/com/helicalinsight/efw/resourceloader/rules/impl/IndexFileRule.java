package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Some business specific rules (like whether to allow the currently logged in
 * user should view the folder in question in the view or not) are applied on
 * the folders in the solution directory. For the same purpose this class
 * instance can be used.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class IndexFileRule extends AbstractResourceRule implements IResourceRule {

    private static final Logger logger = LoggerFactory.getLogger(IndexFileRule.class);

    /**
     * For singleton structure
     */
    private IndexFileRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRule getInstance() {
        return EfwFolderRuleHolder.INSTANCE;
    }

    /**
     * Pretty toString() of the class
     *
     * @return The class name itself
     */
    @NotNull
    public String toString() {
        return "EFWFolderRuleValidator";
    }

    /**
     * Currently as it stands the folder is validated only if the the user
     * credentials in the json parameter are matching with the currently logged
     * in user
     *
     * @param fileAsJson The json of the file used for indicating the credentials of
     *                   the user
     * @return The result of user credentials matching or not.
     */
    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        final JsonObject settings = JsonUtils.newGetSettingsJson();
        boolean isOwner = false;
        try {
            isOwner = AbstractResourceRule.isSecurityMatching(settings, fileAsJson);
        } catch (Exception ex) {
            logger.error("An exception has taken place. The stack trace is ", ex);
        }
        //Check if the folder is shared with other users, if it is shared it should be shown
        Map<String,Object> resourceMap  = new Gson().fromJson(fileAsJson, new TypeToken<Map<String, Object>>() {}.getType());
        return isOwner || resultOfConfiguredRules(settings, resourceMap);
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(JsonObject fileAsJson, String extensionKey, String path, String name,
                                              String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        foldersMap.put("type", "folder");
        foldersMap.put("name", fileAsJson.get("title").getAsString());

        JsonObject json = new JsonObject();
        json.addProperty("selectable", "true");
        foldersMap.put("options", json.toString());
        if (GsonUtility.optBoolean(fileAsJson,"inherit")) {
            foldersMap.put("permissionLevel", fileAsJson.get("permissionLevel").getAsString());
            foldersMap.put("inherit", "true");
        } else {
            foldersMap.put("permissionLevel", "" + SecurityUtils.whatIsMyPermission(fileAsJson));
        }
        return foldersMap;

    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys) {

    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwFolderRuleHolder {
        public static final IResourceRule INSTANCE = new IndexFileRule();
    }
}