package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.Iterator;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.utility.JsonUtils;



/**
 * Different reports are represented using different types of files. For EFW
 * file type this class is used.
 *
 * @author Rajasekhar
 * @version 1.1
 * @see com.helicalinsight.efw.resourceloader.rules.IResourceRule
 * @see com.helicalinsight.efw.resourceloader.rules.IRule
 * @since 1.1
 */
@Deprecated
@SuppressWarnings("unused")
public final class EFWRule extends AbstractResourceRule implements IResourceRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWRule.class);

    /**
     * For Singleton structure - A private constructor
     */
    private EFWRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRule getInstance() {
        return EFWRuleHolder.INSTANCE;
    }

    /**
     * First validates the security tag details present in the file, which is a mandatory tag.
     * The file should also be visible to be returned true.
     * Then, validates the file in the following scenarios:
     * <p/>
     * 1.	If resource tag not is present it would be considered as a ‘Public’ resource.
     * 2.	If resource tag blank, accessible only to Global users, i.e. users with organization
     * as null.
     * 3.	If the tag is present and not blank, it would indicate that it belongs to that
     * particular organization.
     *
     * @param fileAsJson The file under concern
     * @return <code>true</code> if the file can be sent to the currently logged in user's view
     * @throws ImproperXMLConfigurationException      If resourceSecurity tag is not configured
     * @throws UnSupportedRuleImplementationException
     */
    public boolean validateFile(@NotNull JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        //For old code compatibility. When there were no security or share rules only visible was
        // the rule
        if (!"true".equalsIgnoreCase(fileAsJson.get("visible").getAsString())) {
            return false;
        }
        final JsonObject settings = JsonUtils.newGetSettingsJson();
        boolean enabled = settings.has("security");
        if (!enabled) {
            return true;
        }

        boolean supportForOldVersions = checkSupportForOldVersions(fileAsJson, settings);
        boolean isOwner;

        if (fileAsJson.has("security")) {
            isOwner = isSecurityMatching(settings, fileAsJson);
        } else {
            isOwner = supportForOldVersions;
        }
        Map<String,Object> resourceMap = new Gson().fromJson(fileAsJson, new TypeToken<Map<String, Object>>() {}.getType());
        return isOwner || resultOfConfiguredRules(settings, resourceMap);
    }

    private boolean checkSupportForOldVersions(JsonObject fileAsJson, @NotNull final JsonObject settings) throws
            ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        JsonObject security = settings.getAsJsonObject("security");
        if (security.has("supportOldVersions")) {
            String supportOldVersions = security.get("supportOldVersions").getAsString();
            if (supportOldVersions.equalsIgnoreCase("true")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Supporting old efw resources");
                }
                return true;
            }
        }
        return (isSecurityMatching(settings, fileAsJson));
    }

    /**
     * Pretty toString method
     *
     * @return The class name
     */
    @NotNull
    public String toString() {
        return "EFWRuleValidator";
    }

    /**
     * Includes the resource under concern in the json being sent to the view
     *
     * @param fileAsJson   The file under concern
     * @param extensionKey The extension of the file type. The tag key and not the value
     * @return A map of the file content
     */
    public Map<String, String> getResourceMap(JsonObject fileAsJson, String extensionKey, String path, String name,
                                              String lastModified) {
        return include(fileAsJson, extensionKey, path, name, lastModified);
    }

    protected void addContentOfJson(@NotNull Map<String, String> foldersMap, @NotNull JsonObject fileAsJson,
                                    @NotNull Iterator<?> keys) {
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (("security".equals(key)) || "share".equals(key)) {
                continue;
            }
            foldersMap.put(key.toLowerCase(), fileAsJson.get(key).getAsString());
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EFWRuleHolder {
        public static final IResourceRule INSTANCE = new EFWRule();
    }
}
