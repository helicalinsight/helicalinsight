package com.helicalinsight.efw.resourceloader.rules.impl;

import java.util.Iterator;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;



/**
 * For EFWSR file type this class is used. This file structure is typically
 * different from efw.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
@SuppressWarnings("unused")
public final class EFWSRRule extends AbstractResourceRule implements IResourceRule {

    /**
     * For singleton structure
     */
    private EFWSRRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IResourceRule getInstance() {
        return EFWSRRuleHolder.INSTANCE;
    }

    /**
     * First validates the security tag details present in the file, which is a
     * mandatory tag. The file should also be visible to be returned true. Then,
     * validates the file in the following scenarios:
     * <p/>
     * 1. If resource tag not is present it would be considered as a ‘Public’
     * resource. 2. If resource tag blank, accessible only to Global users, i.e.
     * users with organization as null. 3. If the tag is present and not blank,
     * it would indicate that it belongs to that particular organization.
     *
     * @param fileAsJson The file under concern
     * @return <code>true</code> if the file can be sent to the currently logged
     * in user's view
     * @throws ImproperXMLConfigurationException      If resourceSecurity tag is not configured
     * @throws UnSupportedRuleImplementationException
     */
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        return validationResult(fileAsJson);
    }

    /**
     * Pretty toString method
     *
     * @return The class name
     */
    @NotNull
    public String toString() {
        return "EFWSRRuleValidator";
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
            if ("reportName".equals(key)) {
                foldersMap.put("title", fileAsJson.get("reportName").getAsString());
                JsonObject json = new JsonObject();
                json.addProperty("selectable", "true");
                foldersMap.put("options", json.toString());
                continue;
            }
            if (("security".equals(key)) || "share".equals(key) || "reportParameters".equals(key)) {
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
     * Initialization-on-demand holder idiom. Instance is created only when
     * there is a call to getInstance.
     */
    private static class EFWSRRuleHolder {
        public static final IResourceRule INSTANCE = new EFWSRRule();
    }
}