package com.helicalinsight.efw.resourceloader.rules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.processor.ConfigurableRulesFactoryDB;
import com.helicalinsight.resourcesecurity.IResourcePermission;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class enables code reuse by acting as a super class for the specific
 * rule implementation classes. The file tree in the HDI left panel (as of
 * version 1.1) shows a set of folders.
 * <p/>
 * Some folders need to be shown to the user and some shouldn't be. For such purposes
 * and for the sake of the data that is being shown to the user this business
 * rules related classes are designed.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
public abstract class AbstractResourceRule {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceRule.class);

    private ResourcePermissionFactory factory = new ResourcePermissionFactory();

    /**
     * A file is validated only if the security details are matching.
     *
     * @param fileAsJson The file under concern
     * @return The result of xsd conformation
     * @throws ImproperXMLConfigurationException
     * @throws UnSupportedRuleImplementationException
     */
    public static boolean isSecurityMatching(@NotNull final JsonObject settings, final JsonObject fileAsJson) throws
            ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        String clazz = getResourceSecurityRulesClass(settings);
        IResourceSecurityRule rule = FactoryMethodWrapper.getTypedInstance(clazz, IResourceSecurityRule.class);
        return (rule != null) && (rule.validate(fileAsJson));
    }

    /**
     * Reads the resourceSecurityRule tag in setting.xml and returns its class
     * attribute
     *
     * @return A string form of a class configured in setting.xml
     * @throws ImproperXMLConfigurationException
     */
    static String getResourceSecurityRulesClass(@NotNull final JsonObject settings) throws
            ImproperXMLConfigurationException {
        String resourceSecurityClass;
        try {
            resourceSecurityClass = settings.getAsJsonObject("security").getAsJsonObject("resourceSecurityRule")
                    .get("class").getAsString();
        } catch (Exception e) {
            throw new ImproperXMLConfigurationException("Setting.xml configuration is incorrect. " +
                    "" + "Provide resourceSecurityRule class");
        }
        return resourceSecurityClass;
    }

    /**
     * Returns true if the file is owned by the user. If not, the shared rules are applied.
     *
     * @param fileAsJson The file under concern
     * @return Returns true if the logged in user is owner or if the resource is shared
     * @throws ImproperXMLConfigurationException
     * @throws UnSupportedRuleImplementationException
     */
    protected boolean validationResult(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        final JsonObject settings = JsonUtils.newGetSettingsJson();
        boolean isOwner = isSecurityMatching(settings, fileAsJson);
        Map<String,Object> resourceMap = new Gson().fromJson(fileAsJson, new TypeToken<Map<String, Object>>() {}.getType());
        return isOwner || resultOfConfiguredRules(settings, resourceMap);
    }

    protected boolean resultOfConfiguredRules(JsonObject settings, Map<String,Object> resourceMap) throws
            UnSupportedRuleImplementationException {
        //Any further rules?
        final String mode = ConfigurableRulesHelper.getConfigurableRulesMode(settings);
        final List<String> configurableRulesList = ConfigurableRulesHelper.getConfigurableRulesList(settings);
        //Apply the ones at hand.
        if (configurableRulesList == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("The configurableRulesList is null. Returning " + false);
            }
            //No further rules. The resource can't be shown
            return false;
        } else {
            //There are rules. Apply
            final ConfigurableRulesFactoryDB configurableRulesFactory = new ConfigurableRulesFactoryDB(resourceMap,
                    configurableRulesList, mode);
            return configurableRulesFactory.apply();
        }
    }

    /**
     * Includes the resource under concern in the json being sent to the view
     *
     * @param fileAsJson   The file under concern
     * @param extensionKey The extension of the file type. The tag key and not the value
     * @return A map of the file content
     */
    @NotNull
    public Map<String, String> include(@NotNull JsonObject fileAsJson, String extensionKey, String absolutePath,
                                       String fileName, String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        String visible = fileAsJson.get("visible").getAsString();
        if ("TRUE".equalsIgnoreCase(visible)) {
            String relativePath = ApplicationUtilities.getRelativeSolutionPath(absolutePath);

            foldersMap.put("type", "file");
            foldersMap.put("extension", extensionKey);
            foldersMap.put("name", fileName);
            foldersMap.put("path", relativePath);
            foldersMap.put("lastModified", lastModified);

            if (fileAsJson.has("description")) {
                foldersMap.put("description", fileAsJson.get("description").getAsString());
            } else {
                foldersMap.put("description", fileName);
            }

            if (GsonUtility.optBoolean(fileAsJson, "inherit")) {
                foldersMap.put("permissionLevel", fileAsJson.get("permissionLevel").getAsString());
                foldersMap.put("inherit", "true");
            } else {
                IResourcePermission resourcePermission = this.factory.resourcePermission(fileAsJson);
                int maximumPermissionLevelOnResource = resourcePermission.maximumPermissionLevelOnResource();
                foldersMap.put("permissionLevel", Integer.toString(maximumPermissionLevelOnResource));
            }
            //Add custom file based content.
            Iterator<?> keys = fileAsJson.keySet().iterator();
            addContentOfJson(foldersMap, fileAsJson, keys);
        }
        return foldersMap;
    }

    protected abstract void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys);
}