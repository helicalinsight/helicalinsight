package com.helicalinsight.efw.resourceloader.rules.impl;

import static com.helicalinsight.efw.components.DataSourceSecurityUtility.getMaxPermissionDataSources;
import static com.helicalinsight.efw.components.DataSourceSecurityUtility.throwException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.AbstractResourceRule;
import com.helicalinsight.efw.resourceloader.rules.IResourceRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.GroovyUtils;
import com.helicalinsight.efw.utility.JsonUtils;



/**
 * Created by author on 26-05-2020.
 *
 * @author Somen
 */
@Deprecated
public final class EFWDRule extends AbstractResourceRule implements IResourceRule {

    private EFWDRule() {
    }

    @NotNull
    public static IResourceRule getInstance() {
        return EFWDRuleHolder.INSTANCE;
    }

    @Override
    public boolean validateFile(JsonObject fileAsJson) throws ImproperXMLConfigurationException,
            UnSupportedRuleImplementationException {

        String id = fileAsJson.get("id").getAsString();
        JsonArray dataSources = fileAsJson.getAsJsonArray("dataSources");
        String operation = fileAsJson.get("operation").getAsString();
        int counter = 0;
        for (JsonElement object : dataSources) {
            JsonObject connection = object.getAsJsonObject();
            String connectionId = connection.has("id")?connection.get("id").getAsString():connection.get("@id").getAsString();
            String type = connection.has("type")?connection.get("type").getAsString():connection.get("@type").getAsString();
            if (id.equalsIgnoreCase(connectionId)) {
                if (GlobalJdbcTypeUtils.isManagedGroovyDataSource(type)) {
                    connection = GroovyUtils.executeGroovy(connection.get("Condition").getAsString(), "evalCondition", JsonObject.class);
                }
                if (connection.has("globalId")) {
                    connectionId = connection.get("globalId").getAsString();
                    List<Map<String, Object>> dataSourcesGlobal = new ArrayList<>();
                    Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
                    if (!dsTypeStorageDatabase) {
                        GlobalXmlReaderUtility globalXmlReaderUtility = new GlobalXmlReaderUtility();
                        globalXmlReaderUtility.addDataSources(dataSourcesGlobal, operation);
                    } else {
                        GlobalDSReaderUtility globalDSReaderUtility = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
                        Map<String, Object> stringObjectMap = globalDSReaderUtility.addDataSourcesId(operation, Integer.valueOf(connectionId));
                        if (stringObjectMap != null) {
                            Map data = (Map) stringObjectMap.get("data");
                            if (data.get("id").equals(connectionId)) {
                                return true;
                            }
                        }
                    }

                    if (!dataSourcesGlobal.isEmpty()) {
                        for (Map<String, Object> jsonObject : dataSourcesGlobal) {
                            Map data = (Map) jsonObject.get("data");
                            if (data.get("id").equals(connectionId)) {
                                return true;
                            }
                        }
                    }
                    throwException();
                }

                final JsonObject settings = JsonUtils.newGetSettingsJson();
                String absolutePath = fileAsJson.get("absolutePath").getAsString();
                connection.addProperty("absolutePath", absolutePath);

                if (connection.has("share") || connection.has("security")) {
                    if (getMaxPermissionDataSources(JSONObject.fromObject(connection.toString()), operation) == null) {
                        throwException();
                    } else {
                        return true;
                    }
                }
                if (!connection.has("share") && !connection.has("security")) {
                    return true;
                }
                Map<String,Object> resourceMap  = new Gson().fromJson(connection, new TypeToken<Map<String, Object>>() {}.getType());
                if (resultOfConfiguredRules(settings, resourceMap)) {
                    return true;
                }
            }
            counter++;
        }
//        if (counter == dataSources.size()) {
//            throwResourceNotFoundException();
//        }
        return true;
    }

    @NotNull
    @Override
    public Map<String, String> getResourceMap(@NotNull JsonObject fileAsJson, String extensionKey, String path,
                                              String name, String lastModified) {
        Map<String, String> foldersMap = new HashMap<>();

        String permission = RulesUtils.permissionLevelForNonVisibleFiles();
        foldersMap.put("permissionLevel", permission);

        String relativePath = ApplicationUtilities.getRelativeSolutionPath(path);

        foldersMap.put("title", FilenameUtils.removeExtension(name));
        foldersMap.put("type", "file");
        foldersMap.put("extension", extensionKey);
        foldersMap.put("lastModified", lastModified);
        foldersMap.put("name", name);
        foldersMap.put("path", relativePath);
        if (fileAsJson.has("description")) {
            foldersMap.put("description", fileAsJson.get("description").getAsString());
        } else {
            foldersMap.put("description", name);
        }


        return foldersMap;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    protected void addContentOfJson(Map<String, String> foldersMap, JsonObject fileAsJson, Iterator<?> keys) {
        foldersMap.put("title", GsonUtility.optString(fileAsJson, "fileName"));
    }

    private static class EFWDRuleHolder {
        public static final IResourceRule INSTANCE = new EFWDRule();
    }
}
