package com.helicalinsight.adhoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.cache.manager.MetadataCacheException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.serviceframework.ServiceManager;
import com.helicalinsight.efw.utility.ApplicationUtilities;

/**
 * The {@code CachedColumnsProviderComponent} class is an implementation of the {@link IComponent} interface, designed
 * to provide column information for ad-hoc reports. It interacts with the database cache and metadata cache
 * to retrieve and present column data efficiently.
 *
 * @author Rajesh
 * Created by author on 2/26/2019.
 */
public class CachedColumnsProviderComponent implements IComponent {
	/**
     * Method/component to provide cached column information for ad-hoc reports. It retrieves information from
     * the database cache and metadata cache, handling the auto-triggering of cache refresh for certain operations.
     *
     * @param formData 		 JSON-formatted form data containing parameters about metadata details.
     * @return A JSON-formatted string containing metadata information for the requested ad-hoc report.
     */
    @Override
    public String executeComponent(String formData) {
        DataSourceMapping dataSourceKey = new DataSourceMapping();
        JsonObject formJsonData = new Gson().fromJson(formData,JsonObject.class);
        JsonObject dataSourceJson = formJsonData.getAsJsonObject("dataSource");
        String requestedTableName = prepareKey(dataSourceKey, formJsonData);
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        List<String> addList = new ArrayList<>();
        addList.addAll(prepareIdNameMap(dataSourceKey).keySet());
        List<String> unClickedTables = databaseCacheService.findUnClickedTables(dataSourceKey, addList);

        JsonObject response = new JsonObject();
        JsonObject responseMetadata = new JsonObject();
        List<JsonObject> listOfJsonData = new ArrayList<>();
        String catalog = dataSourceJson.get("catalog").getAsString();
        String schema = dataSourceJson.get("schema").getAsString();
        String tableOriginal = formJsonData.getAsJsonObject("metadata").get("table").getAsString();
        String requestedTable = MetadataUtils.getId(catalog, schema, tableOriginal);
        if (unClickedTables.contains( "\""+requestedTable+ "\"")) {
            JsonObject columnResults = getResult(formJsonData);
            listOfJsonData.add(columnResults);
        } else {
            List<ApplicationCache> allData = databaseCacheService.findApplicationCacheByDataSourceMapping(dataSourceKey);
            if (allData != null && !allData.isEmpty()) {
                allData.forEach(item -> {
                    Object deSerialize = ApplicationUtilities.unCompressObject(item);
                    listOfJsonData.add( new Gson().fromJson(deSerialize.toString(),JsonObject.class));
                });
            }

        }
        searchInList(requestedTableName, responseMetadata, listOfJsonData);
        if (responseMetadata.size() == 0) {
            throw new MetadataCacheException("No data found for the catalog: "+catalog +"schema: "+schema+" and table: "+requestedTableName);
        }
        response.add("metadata", responseMetadata);

        return response.toString();
    }
    /**
     * Retrieves the result (column information) from the specified metadata.
     *
     * @param formJsonData 		 JSON-formatted form data containing parameters for metadata column.
     * @return A JSON-formatted string containing the column information for metadata.
     */
    public static JsonObject getResult(JsonObject formJsonData) {
        JsonObject formDataFetch = prepareFormData(formJsonData);
        ServiceManager serviceManager = ApplicationContextAccessor.getBean(ServiceManager.class);
        String workerClass = serviceManager.pickTheWorkerClass("adhoc", "metadata", "metadataWorkflow");
        JsonObject result = (JsonObject) serviceManager.getResult("adhoc", "metadata", "metadataWorkflow", formDataFetch, workerClass);
        JsonObject parametere = formDataFetch.getAsJsonObject("parameters");
        parametere.addProperty("fetchJoins", true);
        parametere.remove("fetchColumns");
        formDataFetch.addProperty("partial", "partial_joins");
        JsonObject joinsResult = (JsonObject) serviceManager.getResult("adhoc", "metadata", "retrieveJoins", formDataFetch, workerClass);
        return result;
    }
    /**
     * Prepares the form data for fetching column information from the metadata.
     *
     * @param allData 		 JSON-formatted form data containing all parameters.
     * @return A JSON-formatted string containing the prepared form data.
     */
    private static JsonObject prepareFormData(JsonObject allData) {
        JsonObject formData = allData.getAsJsonObject("dataSource");
        String id = formData.get("id").getAsString();
        String type = formData.get("type").getAsString();
        String catalog = formData.get("catalog").getAsString();
        String schema = formData.get("schema").getAsString();
        String table = "";
        String tableOriginal = "";
        String dir = GsonUtility.optString(formData, "dir");
        String requestId = GsonUtility.optString(allData, "requestId");

        boolean unclicked = allData.has("tableNames");
        if (unclicked) {
            table = allData.getAsJsonArray("tableNames").toString();
            table = table.replace("[", "").replace("]", "");
            tableOriginal = table;
        } else {
            tableOriginal = allData.getAsJsonObject("metadata").get("table").getAsString();
            table = "\"" + tableOriginal + "\"";
        }
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("\"id\": \"" + id + "\",");
        stringBuilder.append("\"type\": \"" + type + "\",");
        stringBuilder.append("\"parameters\": {");
        stringBuilder.append("\"fetchColumns\": true,");
        stringBuilder.append("\"fetchData\": [");
        stringBuilder.append("{");
        if (StringUtils.isNotEmpty(catalog)) {
            stringBuilder.append("\"catalog\": \"" + catalog + "\",");
        }
        stringBuilder.append("\"schemas\": [");
        stringBuilder.append("{");
        stringBuilder.append("\"tables\": [" + table + "]");
        if (StringUtils.isNotEmpty(schema)) {
            stringBuilder.append(",\"name\": \"" + schema + "\"");
        }
        stringBuilder.append("}]}]},");
        stringBuilder.append("\"requestId\": \"" + requestId + "\"");
        stringBuilder.append("}");
        String formDataDummy = stringBuilder.toString();
        JsonObject jsonObject = new Gson().fromJson(formDataDummy,JsonObject.class);
        if (StringUtils.isNotEmpty(dir)) {
            jsonObject.addProperty("dir", dir);
        }
        jsonObject.addProperty("requestId", requestId);
        String tableIdArray = GsonUtility.optString(allData, "tableIds");
        String tableId = tableIdArray.replace("[", "").replace("]", "");
        if (!unclicked) {
            tableId = MetadataUtils.getId(catalog, schema, tableOriginal);
            jsonObject.addProperty("tableIdIndex", "\"" + tableId + "\"");
        } else {
            jsonObject.addProperty("tableIdIndex", tableId);
        }
        jsonObject.addProperty("tableNameIndex", table);
        jsonObject.addProperty("partial", "partial_column");
        return jsonObject;

    }

    private JsonObject prepareDataForAutoTrigger(JsonObject dataSourceJson) {
        JsonObject data = new JsonObject();
        data.addProperty("name", GsonUtility.optString(dataSourceJson,"name"));
        data.addProperty("id", dataSourceJson.get("id").getAsString());
        data.addProperty("type", dataSourceJson.get("type").getAsString());
        data.addProperty("driver", GsonUtility.optString(dataSourceJson,"driver"));
        data.addProperty("database", GsonUtility.optString(dataSourceJson,"catalog"));
        return data;
    }
    /**
     * Searches in the list of metadata information for the requested table name and builds the response metadata.
     *
     * @param requestedTableName 		 name of the requested table.
     * @param responseMetadata 			 response metadata JSON object to be populated.
     * @param listOfJsonData 			 list of metadata information.
     */
    private void searchInList(String requestedTableName, JsonObject responseMetadata, List<JsonObject> listOfJsonData) {
        for (JsonObject eachJsonElement : listOfJsonData) {
            if (eachJsonElement.get("status").getAsString().equals("1")) {
                JsonObject metadataJson = eachJsonElement.getAsJsonObject("response").getAsJsonObject("metadata");
                responseMetadata.addProperty("classifier", metadataJson.get("classifier").getAsString());
                responseMetadata.addProperty("name", metadataJson.get("name").getAsString());
                responseMetadata.add("dataSource", metadataJson.getAsJsonObject("dataSource"));
                JsonObject tablesJson = metadataJson.getAsJsonObject("tables");
                if (tablesJson.has(requestedTableName)) {
                    JsonObject table = new JsonObject();
                    table.add(requestedTableName, tablesJson.getAsJsonObject(requestedTableName));
                    responseMetadata.add("table", table);
                    break;
                }
            }
        }
    }
    /**
     * Prepares the key for retrieving cached column information based on the form data.
     *
     * @param dataSourceKey 		 {@code DataSourceMapping} instance to be populated as a key.
     * @param formJsonData 			 JSON-formatted form data containing parameters for the ad-hoc report.
     * @return requested table name.
     */
    private String prepareKey(DataSourceMapping dataSourceKey, JsonObject formJsonData) {
        JsonObject formDataMetadataJson = formJsonData.getAsJsonObject("metadata");
        String requestedCatalogName = GsonUtility.optString(formDataMetadataJson, "catalog");
        String requestedSchemaName = GsonUtility.optString(formDataMetadataJson,"schema");
        String requestedTableName = GsonUtility.optString(formDataMetadataJson,"table");
        JsonObject dataSourceJson = formJsonData.getAsJsonObject("dataSource");
        dataSourceKey.setCatalog(requestedCatalogName);
        dataSourceKey.setConnectionId(GsonUtility.optInt(dataSourceJson, "id"));
        dataSourceKey.setSchema(requestedSchemaName);
        dataSourceKey.setDir(GsonUtility.optString(dataSourceJson, "dir"));
        dataSourceKey.setType("partial_column");
        return requestedTableName;
    }
    /**
     * Prepares the ID-to-Name map for the specified {@code DataSourceMapping}.
     *
     * @param dsMapping 			 {@code DataSourceMapping} instance for which the ID-to-Name map is prepared.
     * @return A {@code Map} containing the ID-to-Name mapping.
     */
    public static Map prepareIdNameMap(DataSourceMapping dsMapping) {
        String type = dsMapping.getType();
        dsMapping.setType("table");
        Map<String, String> idNameMap = new HashMap<>();
        DatabaseCacheService databaseCacheService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
        List<ApplicationCache> applicationCacheByDataSourceMapping = databaseCacheService.findApplicationCacheByDataSourceMapping(dsMapping);
        if (applicationCacheByDataSourceMapping != null) {
            for (ApplicationCache a : applicationCacheByDataSourceMapping) {
                Object deSerialize = ApplicationUtilities.unCompressObject(a);
                JsonObject tableName = JsonParser.parseString(deSerialize.toString()).getAsJsonObject();
                //todo check for status 1 only
                JsonArray jsonArray = tableName.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("catalogs").get(0).getAsJsonObject().getAsJsonArray("schemas").get(0).getAsJsonObject().getAsJsonArray("tables");
                for (JsonElement o : jsonArray) {
                    JsonObject obj = o.getAsJsonObject();
                    idNameMap.put(obj.get("id").getAsString(), obj.get("name").getAsString());
                }

            }
        }
        dsMapping.setType(type);
        if (idNameMap.isEmpty() && !"ignore".equals(dsMapping.getFile())) {
            throw new MetadataCacheException("Cannot proceed as no cache found. Please refresh datasource and try again");
        }
        return idNameMap;
    }
    /**
     * Determines whether the component is thread-safe to cache. 
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
