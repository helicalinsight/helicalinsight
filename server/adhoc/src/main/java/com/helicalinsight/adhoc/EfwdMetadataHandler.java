package com.helicalinsight.adhoc;

import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_CATALOG;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_CATALOG_CONTAINS;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_DATABASE;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_DATABASE_CONTAINS;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_SCHEMA;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.HI_SCHEMA_CONTAINS;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.getComaToList;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.getList;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.getPath;
import static com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails.getQuery;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Date:09-02-2018
 * <p>
 * This is the Helper class for the EnhancedMetadataWorkflowComponent
 * class
 * <p>
 * This class picks the appropriate efwd file based on the
 * driverClass. This class checks the parameters from the formData to
 * get the appropriate mapId. This class passes the required
 * information like mapId, targetEFWDPath, efwd file and parameters to
 * the QueryExecuter for further process This class is responsible for
 * process the result in required format which is returned by the
 * QueryExecutor.
 */

public class EfwdMetadataHandler {

    private static final String DB_CONFIG_DIRECTORY = EfwdRequirements.DB_CONFIG_DIRECTORY.getData();
    private static final String ADMIN_DIRECTORY = EfwdRequirements.ADMIN_DIRECTORY.getData();
    private static final Logger logger = LoggerFactory.getLogger(EfwdMetadataHandler.class);
    private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    private JsonObject parameters;
    private String efwdFileNameWithExtension;
    private Connection connection;
    private JsonObject connectionJson;
    private JsonObject metadata;


    public EfwdMetadataHandler(JsonObject parameters, String efwdFileNameWithExtension, Connection connection,
                               JsonObject connectionJson, JsonObject metadata) {

        this.parameters = parameters;
        this.efwdFileNameWithExtension = efwdFileNameWithExtension;
        this.connection = connection;
        this.connectionJson = connectionJson;
        this.metadata = metadata;
    }

    /**
     * checks the parameters that it contains fetchCatalogs and its value is
     * true then sets the mapId then calls to the getResult method after getting
     * the result from getResult method then calls the getCatalogs method by
     * passing the result.
     */
    public void handleFetchCatalog() {
        if (!(parameters.has("fetchCatalogs") && parameters.get("fetchCatalogs").getAsBoolean())) {
            return;
        }
        int mapId = MetadataEnumeration.CATALOG.getData();
        JsonObject jsonObject = getResult(efwdFileNameWithExtension, mapId, connection, connectionJson);
        JsonArray catalog = getCatalogs(jsonObject);
        metadata.add("catalogs", catalog);
    }

    /**
     * checks the parameters that it contains fetchSchemas and its value is true
     * then sets the mapId then calls to the getResult method after getting the
     * result from getResult method then calls the getSchemas method by passing
     * the result.
     */
    public void handleFetchSchema() {
        if (!(parameters.has("fetchSchemas") && parameters.get("fetchSchemas").getAsBoolean())) {
            return;
        }

        int mapId = MetadataEnumeration.SCHEMA.getData();
        JsonObject schemaObject = getResult(efwdFileNameWithExtension, mapId, connection, connectionJson);
        logger.debug("dataSchemaResult   :" + schemaObject);
        JsonArray schema = getSchemas(schemaObject);

        metadata.add("schemas", schema);

        if (parameters.has("view") && "tree".equalsIgnoreCase(parameters.get("view").getAsString())) {
            String nullValue = ApplicationProperties.getInstance().getNullValue();
            JsonArray catalogs = metadata.getAsJsonArray("catalogs");

            if (catalogs == null || catalogs.isEmpty() || catalogs.get(0).toString().equalsIgnoreCase("")) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("name", nullValue);
                responseJson.add("schemas", getSchemasForTree(schemaObject));
                JsonArray catalogArray = new JsonArray();
                catalogArray.add(responseJson);
                metadata.add("catalogs", catalogArray);
                metadata.remove("schemas");
            }
            String url = getUrl();
            String path = getPath(url);
            String query = getQuery(url);
            if (catalogs != null && !catalogs.isEmpty() && !catalogs.get(0).toString().equalsIgnoreCase("")) {
                List<String> databaseList = getComaToList(getList(query, HI_DATABASE));
                databaseList.addAll(getComaToList(getList(query, HI_CATALOG)));
                String getContainsDatabse = getList(query, HI_DATABASE_CONTAINS);
                String getContainsCatalog = getList(query, HI_CATALOG_CONTAINS);
                Set<JsonObject> allCatalogs = new HashSet<>();
                JsonArray schemasForTree = getSchemasForTree(schemaObject);
                JsonArray catalogArray = new JsonArray();
                JsonArray filteredArray = new JsonArray();
                for (JsonElement item : catalogs) {
                    String itemJson = item.getAsString();
                    JsonObject responseJson = new JsonObject();

                    responseJson.addProperty("name", itemJson);
                    responseJson.add("schemas",
                            schemasForTree == null || schemasForTree.isEmpty() ? new JsonArray() : schemasForTree
                            //                getSchemasTreeWithCatalog(item.toString())
                    );
                    if (path.contains(itemJson)) {
                        filteredArray.add(responseJson);
                    }
                    if (databaseList.contains(itemJson)) {
                        allCatalogs.add(responseJson);
                    }
                    if (isNotEmpty(getContainsDatabse) && itemJson.contains(getContainsDatabse)) {

                        allCatalogs.add(responseJson);
                    }
                    if (isNotEmpty(getContainsCatalog) && itemJson.contains(getContainsCatalog)) {
                        allCatalogs.add(responseJson);
                    }
                    catalogArray.add(responseJson);
                }
                if (!allCatalogs.isEmpty()) {
                    metadata.addProperty("catalogs", new Gson().toJson(allCatalogs));

                } else if (!filteredArray.isEmpty()) {
                    metadata.add("catalogs", filteredArray);
                } else {
                    metadata.add("catalogs", catalogArray);
                }
                metadata.remove("schemas");
            }

        }

    }
    /**
     * Returns connection url for efwd.
     * @return url 
     */
    public String getUrl() {
        JsonObject conJson = connectionJson.getAsJsonObject("connection");
        String url = conJson.has("url") ? conJson.get("url").getAsString() : conJson.has("Url") ? conJson.get("Url").getAsString() : "";
        return url;
    }
    /**
     * Method Provides schema details.
     * @param catalogName        catalog name
     * @return JsonArray consisting schema details.
     */
    private JsonArray getSchemasTreeWithCatalog(String catalogName) {
        parameters.addProperty("forCatalog", catalogName);
        parameters.addProperty("catalogs", catalogName);

        int mapId = MetadataEnumeration.SCHEMA.getData();
        JsonObject schemaObject = getResult(efwdFileNameWithExtension, mapId, connection, connectionJson);
        logger.debug("dataSchemaResult   :" + schemaObject);
        JsonArray schema = getSchemas(schemaObject);
        JsonArray responseSchema = new JsonArray();
        for (JsonElement item : schema) {
            JsonObject newSchema = new JsonObject();
            newSchema.add("name", item);

            responseSchema.add(newSchema);
        }
        return responseSchema;

    }

    /**
     * checks the parameters that it contains fetchTables and its value is true
     * then sets the mapId then calls to the getResult method after getting the
     * result from getResult method then calls the getTables method by passing
     * the result.
     */
    public void handleFetchTables() {
        if (!(parameters.has("fetchTables") && parameters.get("fetchTables").getAsBoolean())) {
            return;
        }


        JsonObject fetchDataJSONObject = getFetchData();

        JsonObject efwdParameters = new JsonObject();
        for(String key : connectionJson.keySet()) {
        	efwdParameters.add(key, connectionJson.get(key));
        }
        JsonObject catalogJSON = whenCatalogIsEmpty(fetchDataJSONObject, efwdParameters);

        JsonObject schemasJSON = prepareSchemaJSON(fetchDataJSONObject, efwdParameters);
        prepareParametersForDrillCSV(efwdParameters);

        catalogJSON.addProperty("schema", fetchDataJSONObject != null ? GsonUtility.optString(fetchDataJSONObject, "schemas")
                : ApplicationProperties.getInstance().getNullValue());
        int mapId = MetadataEnumeration.TABLES.getData();
        JsonObject jsonObject = getResult(efwdFileNameWithExtension, mapId, connection, efwdParameters);
        logger.debug("dataTableResult   :" + jsonObject);
        JsonArray tables = getTables(jsonObject);

        logger.debug("tables  :" + tables);
        //catalogJSON.discard("schema");
        //catalogJSON.put("schemas", tables);
        JsonObject catalogJsonObject = new JsonObject();
        catalogJsonObject.addProperty("name", GsonUtility.optString(catalogJSON,"name"));
        if (tables != null && tables.isEmpty()) {
            schemasJSON.add("tables", tables);
            JsonArray schemaArrayWithEmptyTables = new JsonArray();
            schemaArrayWithEmptyTables.add(schemasJSON);
            catalogJsonObject.add("schemas", schemaArrayWithEmptyTables);
        } else {
            schemasJSON.add("schemas", tables);
            catalogJsonObject.add("schemas", tables);
        }
      /*  schemasJSON.put("schemas", tables);
        catalogJsonObject.put("schemas", tables);*/

        JsonArray catalogs = new JsonArray();
        catalogs.add(catalogJsonObject);
        metadata.add("catalogs", catalogs);
    }
    /**
     * Method is responible for checking connection for drill CSV.
     * @param efwdParameters   JsonObject provides efwd related details.
     */
    private void prepareParametersForDrillCSV(JsonObject efwdParameters) {
        JsonObject connectionJSON = efwdParameters.getAsJsonObject("connection");
        String driverClassName = GsonUtility.optString(connectionJSON, "driverClassName");
        if (connectionJSON.has("Driver")) {
            driverClassName = connectionJSON.get("Driver").getAsString();
        }
        String url = GsonUtility.optString(connectionJSON,"url");
        if (connectionJSON.has("Url")) {
            url = connectionJSON.get("Url").getAsString();
        }
        //HiMiddleWare need to be dynamic
        if (driverClassName.contains(JsonUtils.getHiMiddleWareName()) && url.contains("jdbc:drill")) {
            JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
            drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
            JsonObject drillEnabledTypes = getFormatedDrillDatasources(drillConfig);

            efwdParameters.addProperty("tableType", driverClassName);
            efwdParameters.add("drillConfigObj", drillEnabledTypes);
        }


    }
    /**
     * method formats the provided JSON object
     * representing drill configuration into a specific structure used for presenting drill datasources.
     *
     * @param drillJSONObejct 		object representing drill configuration.
     * @return A formatted {@code JsonObject} representing drill datasources.
     */
    public static JsonObject getFormatedDrillDatasources(JsonObject drillJSONObejct) {


    	JsonObject derivedJsonObject = new JsonObject();

        if (drillJSONObejct.has("enabledTypes")) {
            JsonObject enabledTypesJSON = drillJSONObejct.getAsJsonObject("enabledTypes");

            Set<String> enableTypeKeySet = enabledTypesJSON.keySet();

            List<JsonObject> listValue = new ArrayList<>();
            for (String eachKey : enableTypeKeySet) {

                JsonObject singleDs = new JsonObject();
                JsonObject configJson = enabledTypesJSON.getAsJsonObject(eachKey).getAsJsonObject("config");
                JsonObject parameters = new JsonObject();
                for(String key : configJson.keySet()) {
                	parameters.add(key, configJson.get(key));
                }
                singleDs.addProperty("driver", JsonUtils.getHiMiddleWareName() + eachKey);
                singleDs.addProperty("available", "true");


                singleDs.add("parameter", parameters);
                listValue.add(singleDs);
            }
            derivedJsonObject.addProperty("drillDatasources", new Gson().toJson(listValue));

        }

        return derivedJsonObject;
    }

    /**
     * checks the parameters that it contains fetchColumns and its value is true
     * then sets the mapId then calls to the getResult method after getting the
     * result from getResult method then calls the getColumns method by passing
     * the result.
     */
    public void handleFetchColumns() {
        if (!(parameters.has("fetchColumns") && parameters.get("fetchColumns").getAsBoolean())) {
            return;
        }
        JsonArray catalogs = new JsonArray();
        JsonObject catalogJSON = new JsonObject();

        JsonObject fetchDataJSONObject = getFetchData();
        JsonArray schemaArray = fetchDataJSONObject.getAsJsonArray("schemas");

        JsonObject schemaJSONObject = schemaArray.get(0).getAsJsonObject();

        JsonArray tablesArray = schemaJSONObject.getAsJsonArray("tables");

        JsonObject efwdParameters = prepareEfwdParameters(fetchDataJSONObject, catalogJSON, tablesArray);

        JsonObject schemasJSON = new JsonObject();
        prepareSchemaJson(fetchDataJSONObject, efwdParameters, schemasJSON);

        int mapId = MetadataEnumeration.COLUMN.getData();
        JsonObject jsonObject = getResult(efwdFileNameWithExtension, mapId, connection, efwdParameters);
        logger.debug("dataColumnResult :" + jsonObject);
        catalogs = getColumns(jsonObject);
        logger.debug("catalogs  :" + catalogs);

        metadata.add("catalogs", catalogs);

    }

    /**
     * checks if the parameter contains fetchDate then converts that as
     * JSONObejct and returns it
     *
     * @return JSONObject
     */
    private JsonObject getFetchData() {
        JsonObject fetchDataJSONObject = null;
        if (parameters.has("fetchData")) {
            JsonArray fetchData = parameters.getAsJsonArray("fetchData");
            if (!(fetchData.isEmpty())) {
                fetchDataJSONObject = fetchData.get(0).getAsJsonObject();
            }
        }
        return fetchDataJSONObject;
    }

    /**
     * this method gets all the requested tables from the formData and those
     * tables belongs to which catalog then adds these info into efwdParameters
     * @param fetchDataJSONObject      provides catalog info
     * @param catalogJSON			   it stores catalog inforamation
     * @param fetchDataTablesArray     array of tables
     */
    private JsonObject prepareEfwdParameters(JsonObject fetchDataJSONObject, JsonObject catalogJSON,
                                             JsonArray fetchDataTablesArray) {
        JsonObject efwdParameters = new JsonObject();
        for(String key : connectionJson.keySet()) {
        	efwdParameters.add(key, connectionJson.get(key));
        }
        List<String> tablesList = new ArrayList<>();
        for (Object table : fetchDataTablesArray) {
            tablesList.add(table.toString());
            catalogJSON.addProperty("name", GsonUtility.optString(fetchDataJSONObject, "catalog"));
        }
        efwdParameters.addProperty("tables", new Gson().toJson(tablesList));
        logger.debug("efwdParameters  :" + efwdParameters.toString());
        efwdParameters.addProperty("catalogs", GsonUtility.optString(fetchDataJSONObject,"catalog"));
        return efwdParameters;
    }

    /**
     * this method checks in formData that requesting schemas or not if not then
     * adds "Null" to the efwd parameters or else adds all the schema names to
     * the efwd parameters.
     *
     * @param fetchDataJSONObject		schema details
     * @param efwdParameters			to store schema
     * @param schemasJSON				to store name
     */
    private void prepareSchemaJson(JsonObject fetchDataJSONObject, JsonObject efwdParameters, JsonObject schemasJSON) {
        if ((fetchDataJSONObject.has("schemas") && (!GsonUtility.optString(fetchDataJSONObject, "schemas").isEmpty()))) {
            schemasJSON.addProperty("name", GsonUtility.optString(fetchDataJSONObject, "schemas"));
            List<String> schemaArrayList = new ArrayList<>();
            JsonArray schemaJsonArray = fetchDataJSONObject.getAsJsonArray("schemas");
            for (int index = 0; index < schemaJsonArray.size(); index++) {
            	JsonObject asJsonObject = schemaJsonArray.get(index).getAsJsonObject();
                schemaArrayList.add(GsonUtility.optString(asJsonObject,"name"));
            }
            //JsonArray asJsonArray = new Gson().toJsonTree(schemaArrayList).getAsJsonArray();
            efwdParameters.addProperty("schemas", schemaArrayList.toString());
        } else {
            schemasJSON.addProperty("name", ApplicationProperties.getInstance().getNullValue());
        }
    }

    /**
     * this method checks whether the fetchDataJsonObject contains schema and
     * its value is empty or not if the catalog is empty then it returns "Null"
     * value otherwise returns the JSONObject with schema name in it and gets
     * all the schema names if multiples are provide adds all those into efwd
     * parameters.
     * 
     * @param fetchDataJSONObject       provides schema
     * @param efwdParameters			adds schema
     */
    private JsonObject prepareSchemaJSON(JsonObject fetchDataJSONObject, JsonObject efwdParameters) {


    	JsonObject schemasList = new JsonObject();
    	JsonObject schemasJSON = new JsonObject();
        if (fetchDataJSONObject != null
                && (fetchDataJSONObject.has("schemas") && (!fetchDataJSONObject.getAsJsonArray("schemas").isEmpty()))) {

            JsonArray schemaArray = fetchDataJSONObject.getAsJsonArray("schemas");
            for (Object object : schemaArray) {
                JsonObject item = (JsonObject) object;
                schemasList.addProperty("schemas", item.get("name").getAsString());
                schemasJSON.addProperty("name", item.get("name").getAsString());
            }
            efwdParameters.addProperty("schemas", schemasList.get("schemas").getAsString());

        } else {
            schemasJSON.addProperty("name", ApplicationProperties.getInstance().getNullValue());
        }
        return schemasJSON;
    }

    /**
     * this method checks whether the fetchDataJsonObject contains catalog and
     * its value is empty or not if the catalog is empty then it returns empty
     * JSONObject otherwise returns the JSONObject with catalog name in it.
     *
     * @param fetchDataJSONObject     provides catalog data
     * @param efwdParameters          adds catalog info
     * @return JsonObject with catalog details.
     */
    private JsonObject whenCatalogIsEmpty(JsonObject fetchDataJSONObject, JsonObject efwdParameters) {
        JsonObject catalogJSON = new JsonObject();
        if (fetchDataJSONObject != null
                && (fetchDataJSONObject.has("catalog") && (!fetchDataJSONObject.get("catalog").getAsString().isEmpty()))) {

            efwdParameters.addProperty("catalog", fetchDataJSONObject.get("catalog").getAsString());

            catalogJSON.addProperty("name", fetchDataJSONObject.get("catalog").getAsString());
        } else {
            catalogJSON.addProperty("name", ApplicationProperties.getInstance().getNullValue());
        }
        return catalogJSON;
    }

    /**
     * this method returns the query result by calling QueryExecuter by passing
     * mapId, efwdFileNameWithExtension, Connection and request parameters.
     *
     * @param efwdFileNameWithExtension		EFWD file name with extension.
     * @param mapId							map ID used in the query execution
     * @param connection					JDBC Connection object for executing the query.
     * @param parameters					JsonObject containing additional parameters required for the query execution.
     * @return  A JsonObject representing the result of the query execution.
     */
    private JsonObject getResult(String efwdFileNameWithExtension, int mapId, Connection connection,
                                 JsonObject parameters) {
        JsonObject dataJson = new JsonObject();
        dataJson.addProperty("dir", DB_CONFIG_DIRECTORY);
        dataJson.addProperty("map_id", mapId);
        if (parameters != null) {
        	for(String key : parameters.keySet()) {
        		dataJson.add(key, parameters.get(key));
        	}
        }

        dataJson.addProperty("file", efwdFileNameWithExtension);
        String systemDirectory = applicationProperties.getSystemDirectory();
        String absolutePath = systemDirectory + File.separator + ADMIN_DIRECTORY;
        QueryExecutor queryExecutor = new QueryExecutor(dataJson.toString(), applicationProperties);
        JsonObject jsonObject = queryExecutor.getResultSet(connection, absolutePath);
        return jsonObject;
    }

    /**
     * this method gets all the catalog names from the dataArray and then adds
     * all those names into an array and then sends that array
     * @param jsonObject             provides catalog data
     * @return jsonArray with catalog details.
     */
    public JsonArray getCatalogs(JsonObject jsonObject) {


        JsonArray catalog = new JsonArray();

        if (jsonObject != null) {

            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            for (int index = 0; index < dataArray.size(); index++) {
                JsonObject object = dataArray.get(index).getAsJsonObject();
                String catalogs = GsonUtility.optString(object, "catalogs");

                catalog.add(catalogs);
            }

        }

        return catalog;

    }

    /**
     * this mehtod gets all the schema names from the dataArray and then adds
     * all those names into an array and then sends that array
     * @param jsonObject   provides schema
     * @return JsonArray of schema
     */
    public JsonArray getSchemas(JsonObject jsonObject) {
        JsonArray schemas = new JsonArray();


        if (jsonObject != null) {

            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            for (int index = 0; index < dataArray.size(); index++) {
                JsonObject object = dataArray.get(index).getAsJsonObject();
                String schemaFromResult = object.get("schemas").getAsString();

                schemas.add(schemaFromResult);
            }

        }


        return schemas;
    }
    /**
     * Returns schema details.
     * @param jsonObject     schema details
     * @return jsonArray of schema names
     */
    public JsonArray getSchemasForTree(JsonObject jsonObject) {
        JsonArray schemas = new JsonArray();
        JsonArray filteredSchemas = new JsonArray();
        Set<JsonObject> allSchemaList = new HashSet<>();
        ;
        String url = getUrl();
        String query = getQuery(url);
        List<String> databaseList = getComaToList(getList(query, HI_DATABASE));
        databaseList.addAll(getComaToList(getList(query, HI_SCHEMA)));
        String getContainsDatabse = getList(query, HI_DATABASE_CONTAINS);
        String getContainsSchema = getList(query, HI_SCHEMA_CONTAINS);
        if (jsonObject != null && !jsonObject.entrySet().isEmpty()) {
            String path = getPath(url);
            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            for (int index = 0; index < dataArray.size(); index++) {
                JsonObject object = dataArray.get(index).getAsJsonObject();
                String schemaString = object.get("schemas").getAsString();
                object.addProperty("name", schemaString);
                object.remove("schemas");
                if (path.contains(schemaString)) {
                    filteredSchemas.add(object);
                }
                if (databaseList.contains(schemaString)) {
                    allSchemaList.add(object);
                }
                if (isNotEmpty(getContainsDatabse) && schemaString.contains(getContainsDatabse)) {
                    allSchemaList.add(object);
                }
                if (isNotEmpty(getContainsSchema) && schemaString.contains(getContainsSchema)) {
                    allSchemaList.add(object);
                }

                schemas.add(object);
            }


            if (!allSchemaList.isEmpty()) {
                JsonArray newArray = new JsonArray();
                allSchemaList.forEach(newArray::add);
                return newArray;
            }

            if (!filteredSchemas.isEmpty()) {
                return filteredSchemas;
            }

        }
        return schemas;
    }
    /**
     * Retrieves the schema tree structure from the provided JsonObject.
     *
     * @param jsonObject 		 JsonObject containing schema and catalog information.
     * @return A JsonArray representing the schema tree structure.
     */
    public JsonArray getSchemasTree(JsonObject jsonObject) {
        JsonArray schemas = new JsonArray();
        Map<String, List<String>> catalogSchemaMap = new HashMap<>();
        if (jsonObject != null) {

            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            for (int index = 0; index < dataArray.size(); index++) {
                JsonObject object = dataArray.get(index).getAsJsonObject();
                String schemaFromResult = object.get("schemas").getAsString();
                String catalogFromResult = object.get("catalog").getAsString();
                if (!catalogSchemaMap.containsKey(catalogFromResult)) {
                    catalogSchemaMap.put(catalogFromResult, new ArrayList<String>());
                }
                List<String> schemaList = catalogSchemaMap.get(catalogFromResult);
                schemaList.add(schemaFromResult);
            }

        }
        if (!catalogSchemaMap.isEmpty()) {
            for (String keys : catalogSchemaMap.keySet()) {
                JsonObject newJson = new JsonObject();
                newJson.addProperty("name", keys);
                newJson.addProperty("schemas", new Gson().toJson(catalogSchemaMap.get(keys)));
                schemas.add(newJson);
            }
        }
        return schemas;
    }

    /**
     * this method will prepare the tables in form of JSONArray by adding all
     * the table names and those tables are belongs to which schema.
     * @param jsonObject 		JsonObject containing table and schema information.
     * @return A JsonArray representing tables organized by schema.
     */
    public JsonArray getTables(JsonObject jsonObject) {
        Map<String, List<String>> schemaTablesMap = new HashMap<>();
        List<JsonObject> tableList = new ArrayList<>();
        String url = getUrl();
        String query = DatabaseDetails.getQuery(url);
        List<String> databaseList = DatabaseDetails.getComaToList(DatabaseDetails.getList(query, DatabaseDetails.HI_TABLE));
        String getContainsTable = DatabaseDetails.getList(query, DatabaseDetails.HI_TABLE_CONTAINS);

        if (jsonObject != null) {

            JsonArray dataArray = jsonObject.getAsJsonArray("data");
            if (dataArray.isEmpty()) {
                return dataArray;
            }
            for (int index = 0; index < dataArray.size(); index++) {
                JsonObject object = dataArray.get(index).getAsJsonObject();
                String tableName = object.get("tables").getAsString();
                String schemaName = GsonUtility.optString(object, "schemas");
                if (schemaName == null || "".equals(schemaName)) {
                    schemaName = "Null";
                }
                if (schemaName.length() > 0) {
                    List<String> tables = schemaTablesMap.get(schemaName);
                    List<String> tableNameList = schemaTablesMap.get(schemaName + "_filtered");
                    if (tables == null) {
                        tables = new ArrayList<>();
                    }
                    if (tableNameList == null) {
                        tableNameList = new ArrayList<>();
                    }
                    tables.add(tableName);
                    schemaTablesMap.put(schemaName, tables);
                    if (databaseList.contains(tableName)) {
                        tableNameList.add(tableName);
                    }
                    if (StringUtils.isNoneEmpty(getContainsTable) && tableName.contains(getContainsTable)) {
                        tableNameList.add(tableName);
                    }
                    schemaTablesMap.put(schemaName + "_filtered", tableNameList);

                }
            }
            populateTableList(schemaTablesMap, tableList);
        }

        return new Gson().fromJson(new Gson().toJson(tableList),JsonArray.class);
    }

    /**
     * this method will populate lableList with schemaName and list of tables.
     */
    private void populateTableList(Map<String, List<String>> schemaTablesMap, List<JsonObject> tableList) {
        Set<String> schemasSet = schemaTablesMap.keySet();
        for (String schema : schemasSet) {
            if (!schema.contains("_filtered")) {
                JsonObject table = new JsonObject();
                List<String> tablesList = schemaTablesMap.get(schema);
                List<String> tablesListFiltered = schemaTablesMap.get(schema + "_filtered");
                table.addProperty("name", schema);
                JsonArray fromJson = new Gson().fromJson(new Gson().toJson(tablesListFiltered != null && tablesListFiltered.size() > 0 ? tablesListFiltered : tablesList),JsonArray.class);
                table.add("tables", fromJson);
                tableList.add(table);
            }
        }
    }

    /**
     * this method converts the requested column data in a specific format and
     * then returns it
     *
     *  @param jsonObject 			JsonObject containing column information.
     * @return A JsonArray representing formatted columns.
     */
    public JsonArray getColumns(JsonObject jsonObject) {
        JsonObject schemas = new JsonObject();
        JsonObject catalogs = new JsonObject();
        Map<String, List<JsonObject>> mapOfTables = new HashMap<>();

        prepareColumnJSON(jsonObject, schemas, catalogs, mapOfTables);
        JsonArray catalogsJsonArray = putColumnsInTables(schemas, catalogs, mapOfTables);
        return catalogsJsonArray;
    }

    /**
     * this method will add all the formated columns into a specific table then
     * adds that table in to the schema and the adds the schema in to the
     * catalog.
     */
    private JsonArray putColumnsInTables(JsonObject schemas, JsonObject catalogs,
                                         Map<String, List<JsonObject>> mapOfTables) {
        Set<String> tableNames = mapOfTables.keySet();
        Iterator<String> it = tableNames.iterator();
        JsonArray tablesJsonArray = new JsonArray();
        JsonArray catalogsJsonArray = new JsonArray();
        JsonArray schemasJsonArray = new JsonArray();
        while (it.hasNext()) {
        	JsonObject tablesObject = new JsonObject();
            String table = it.next();
            tablesObject.addProperty("name", table);
            tablesObject.add("columns", JsonParser.parseString(new Gson().toJson(mapOfTables.get(table))).getAsJsonArray());
            tablesJsonArray.add(tablesObject);
        }
        schemas.add("tables", tablesJsonArray);
        schemasJsonArray.add(schemas);
        catalogs.add("schemas", schemasJsonArray);
        catalogsJsonArray.add(catalogs);
        return catalogsJsonArray;
    }

    /**
     * this method prepares the data in a specific format
     */
    private void prepareColumnJSON(JsonObject jsonObject, JsonObject schemas, JsonObject catalogs,
                                   Map<String, List<JsonObject>> mapOfTables) {
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        for (int index = 0; index < dataArray.size(); index++) {
            JsonObject dataJson = dataArray.get(index).getAsJsonObject();
            JsonObject columnJsonObject = new JsonObject();

            String tableName = dataJson.get("tables").getAsString();

            columnJsonObject.addProperty("size", dataJson.get("size").getAsString());
            columnJsonObject.addProperty("nullable", dataJson.get("nullable").getAsString());
            columnJsonObject.addProperty("name", dataJson.get("columns").getAsString());
            columnJsonObject.addProperty("position", dataJson.get("position").getAsString());
            columnJsonObject.addProperty("type", dataJson.get("type").getAsString());
            columnJsonObject.addProperty("dataType", GsonUtility.optString(dataJson, "dataType"));

            List<JsonObject> columns = mapOfTables.get(tableName);
            if (columns == null) {
                columns = new ArrayList<>();
            }
            columns.add(columnJsonObject);
            mapOfTables.put(tableName, columns);
            String schemaName = GsonUtility.optString(dataJson,"schemas");
            checkForBrackets(schemaName, schemas);
            String catalogName = GsonUtility.optString(dataJson,"catalogs");
            checkForBrackets(catalogName, catalogs);
        }
    }
    /**
     * Checks for brackets in the value and sets the "name" property accordingly in the provided JsonObject.
     */
    public void checkForBrackets(String value, JsonObject object) {
        if (value.contains("[")) {
            JsonArray valueArray = new Gson().fromJson(value,JsonArray.class);
            
            if (valueArray.size() == 0 || valueArray.isEmpty()) {

                object.addProperty("name", ApplicationProperties.getInstance().getNullValue());
            } else {
                object.addProperty("name", GsonUtility.optStringFromJsonArray(valueArray, 0)); //valueArray.opt(0) changed 
            }

        } else {
            if ("".equals(value)) {
                object.addProperty("name", ApplicationProperties.getInstance().getNullValue());
            } else {
                object.addProperty("name", value);
            }
        }


    }
}
