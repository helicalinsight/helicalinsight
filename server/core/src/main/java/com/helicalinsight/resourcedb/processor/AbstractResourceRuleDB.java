package com.helicalinsight.resourcedb.processor;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceloader.rules.ConfigurableRulesHelper;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.IResourcePermission;
import com.helicalinsight.resourcesecurity.ResourcePermissionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public abstract class AbstractResourceRuleDB {

    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceRuleDB.class);

    private ResourcePermissionFactory factory = new ResourcePermissionFactory();


    public static boolean isSecurityMatching(@NotNull final JsonObject settings, Map<String,Object> resourceMap) throws
            ImproperXMLConfigurationException, UnSupportedRuleImplementationException {
        String clazz = getResourceSecurityRulesClass(settings);
        IResourceSecurityRule rule = FactoryMethodWrapper.getTypedInstance(clazz, IResourceSecurityRule.class);
        return (rule != null) && (rule.validateMap(resourceMap));
    }


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


    protected boolean validationResult(Map<String,Object> resourceMap) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {
        final JsonObject settings = JsonUtils.newGetSettingsJson();
        boolean isOwner = isSecurityMatching(settings, resourceMap);
        return isOwner || resultOfConfiguredRules(settings, resourceMap);
    }

    protected boolean resultOfConfiguredRules(JsonObject settings, Map<String,Object> resourceMap) throws
            UnSupportedRuleImplementationException {

        final String mode = ConfigurableRulesHelper.getConfigurableRulesMode(settings);
        final List<String> configurableRulesList = ConfigurableRulesHelper.getConfigurableRulesList(settings);

        if (configurableRulesList == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("The configurableRulesList is null. Returning " + false);
            }
            return false;
        } else {
            final ConfigurableRulesFactoryDB configurableRulesFactory = new ConfigurableRulesFactoryDB(resourceMap,
                    configurableRulesList, mode);
            return configurableRulesFactory.apply();
        }
    }



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

            Iterator<?> keys = fileAsJson.keySet().iterator();
            addContentOfJson(foldersMap, fileAsJson, keys);
        }
        return foldersMap;
    }

    protected abstract void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys);
}
