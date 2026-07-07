package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AddRemoveTableColumnsHandler;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.DbMetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * 
 * The DatabaseMetadataUpdateHandler class handles the updating of database metadata.
 * It implements the {@code IComponent} interface and provides methods for executing
 * metadata update operations.
 * 
 * This class is responsible for processing JSON data containing metadata changes
 * and applying those changes to the corresponding metadata XML file.
 * 
 * It interacts with various components such as tables, columns, joins, and views,
 * and performs operations like adding, removing, or updating them based on the
 * provided JSON input.
 * 
 * The metadata update process involves validation of the provided metadata changes,
 * handling table and column aliases, managing database connections, and saving the
 * updated metadata XML file.
 * 
 * Created by Author on 05/03/2015
 *
 * @author Muqtar Ahmed
 * @author Somen
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class DatabaseMetadataUpdateHandler implements IComponent {
	/**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true} the method is thread-safe
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the metadata update operation based on the provided JSON data.
     * 
     * @param jsonFormData 				 JSON data containing metadata update information
     * @return a JSON string indicating the result of the metadata update operation
     * @throws RequiredParameterIsNullException if a required parameter is null
     * @throws MalformedJsonException if the JSON data is malformed
     * @throws MetadataServiceException if there is an error during the metadata update process
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public String executeComponent(String jsonFormData) {
        /*
         * When metadata is being saved during create-metadata, workflow then location and uniqueId are used.
         *
         * In other cases location and uuid parameters are used.
         * */
        JsonObject formJson = new Gson().fromJson(jsonFormData,JsonObject.class);


        String fileName = null;
        boolean firstTime = true;
        // fileName is name of the metadata given by user while saving
        if (formJson.has("fileName")) {
            fileName = formJson.get("fileName").getAsString();
        }

        String dataSourceId = null;
        String dataSourceType = null;
        String dataSourceDir = null;
        if (formJson.has("dataSource")) {
            JsonObject dataSourceJson = formJson.getAsJsonObject("dataSource");
            dataSourceId = dataSourceJson.get("id").getAsString();
            dataSourceType = dataSourceJson.get("type").getAsString();
            dataSourceDir = GsonUtility.optString(dataSourceJson, "dir");

        }

        String location;// Where to save is given by location
        if (formJson.has("location")) {
            location = formJson.get("location").getAsString();
        } else {
            throw new RequiredParameterIsNullException("Missing request parameter location");
        }

        String uniqueId = null;
        //During metadata create, workflow uniqueId is given to UI as each metadata file is saved in the same Temp
        // directory. All active tabs also will have their corresponding metadata safe in Temp
        if (formJson.has("uniqueId")) {
            uniqueId = formJson.get("uniqueId").getAsString();
        }

        String newLocation = null;
        if (formJson.has("newLocation")) {
            newLocation = formJson.get("newLocation").getAsString();
        }
        String reference;
        if (!GlobalJdbcTypeUtils.isTypeGlobal(dataSourceType)) {
            reference = prepareReferenceForEfwd(formJson, dataSourceId, dataSourceType, dataSourceDir);
        } else {
            reference = prepareReferenceForGlobal(formJson, dataSourceId);
        }

        JsonObject tables = null;
        JsonObject columns = null;
        JsonArray joins = null;
        String uuid = null;
        JsonArray viewsArray = null;

        if (formJson.has("tables")) {
            tables = formJson.getAsJsonObject("tables");
        }
        if (formJson.has("columns")) {
            columns = formJson.getAsJsonObject("columns");
        }
        if (formJson.has("joins")) {
            joins = formJson.getAsJsonArray("joins");
        }
        if (formJson.has("uuid")) {
            uuid = formJson.get("uuid").getAsString();
            firstTime = false;
        }
        if (formJson.has("views")) {
            viewsArray = formJson.getAsJsonArray("views");
        }

        Map<String, String> parameters = new HashMap<>();

        if (fileName != null) {
            parameters.put("filename", fileName);
        }

        if (newLocation != null) {
            parameters.put("newLocation", newLocation);
        }
        if (reference != null) {
            parameters.put("reference", reference);
        }


        parameters.put("location", location);

        // Check for empty strings. User may not provide any aliases for tables
        // or columns.
        // There may not be any joins that the user wants.
        if (tables != null) {
            parameters.put("tables", tables.toString());
        }
        if (columns != null) {
            parameters.put("columns", columns.toString());
        }
        if (joins != null) {
            parameters.put("joins", joins.toString());
        }
        if (viewsArray != null) {
            parameters.put("views", viewsArray.toString());
        }

        String extension = JsonUtils.getMetadataExtension();
        String fileToUnMarshall;

        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        if (uuid != null) {
            parameters.put("uuid", uuid);
            fileToUnMarshall = solutionDirectory + File.separator + location + File.separator + uuid;
        } else if (fileName != null) {
            if (uniqueId == null) {
                throw new RequiredParameterIsNullException("Parameter uniqueId is null.");
            }
            String path = TempDirectoryCleaner.getTempDirectory().getPath();
            fileToUnMarshall = path + File.separator + uniqueId + "." + extension;
        } else {
            throw new IllegalArgumentException("Either fileName or uuid arguments must be non " + "null");
        }

        String databaseName = null;
        String catalogName = null;
        String schemaName = null;
        JsonObject dataSource = null;
        boolean changeDataSource = false;
        try {
            if (formJson.has("database")) {
                databaseName = formJson.get("database").getAsString();
            }

            if (databaseName != null && !("".equals(databaseName))) {
                parameters.put("database", databaseName);
            }
            if (formJson.has("catalog")) {
                catalogName = formJson.get("catalog").getAsString();
            }

            if (catalogName != null && !("".equals(catalogName))) {
                parameters.put("catalog", catalogName);
            }
            if (formJson.has("schema")) {
                schemaName = formJson.get("schema").getAsString();
            }

            if (schemaName != null && !("".equals(schemaName))) {
                parameters.put("schema", schemaName);
            }


            if (formJson.has("dataSource")) {
                dataSource = formJson.getAsJsonObject("dataSource");
                catalogName = GsonUtility.optString(dataSource, "catalog");
                schemaName =  GsonUtility.optString(dataSource, "schema");
                changeDataSource = GsonUtility.optBoolean(dataSource, "changed");
            }
        } catch (Exception ex) {
            throw new MalformedJsonException("The json of dataSource and/or database is incorrect. Key " +
                    "databaseName is a string, and the dataSource is a json object.", ex);
        }

        if (changeDataSource && (dataSource == null || dataSource.isJsonNull())) {
            throw new MetadataServiceException("The object dataSource is null or empty.");
        }

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        File metadataFile = new File(fileToUnMarshall);
        Metadata metadata;
        try {
            metadata = JaxbUtils.unMarshal(Metadata.class, metadataFile);
        } catch (Exception ex) {
            throw new MetadataServiceException("The metadata file could not be located. Failed " + "to save changes.");
        }

        if (metadata == null) {
            throw new IllegalStateException("The metadata file is parsed but no data is found.");
        }
        Database database = metadata.getDatabase();
        String dbId = database.getId();
        if (dbId == null) {
            dbId = AdhocUtils.getUuid();
            database.setId(dbId);
        }
        if (databaseName != null) {
            database.setName(databaseName);
        }
        if (catalogName != null) {
            database.setCatalog(catalogName);
        }
        if (schemaName != null) {
            database.setSchema(schemaName);
        }
        if (reference != null) {
            metadata.getConnectionDetails().getDriverClass().setReference(reference);
        }
        /*Need to handle from frontend the metadata sends the datasource details while creating*/
        if (changeDataSource) {
            MetadataDataSourceChangeHandler changeHandler = ApplicationContextAccessor.getBean
                    (MetadataDataSourceChangeHandler.class);
            changeHandler.change(databaseName, dataSource, metadata);
        }

        //Added the temporarily saved tables to the existing tables list and then proceed for
        //validation. Otherwise newly added tables(views) name changes fail validation.
        if (viewsArray != null && !viewsArray.isEmpty()) {
            ViewsUpdateHandler handler = ApplicationContextAccessor.getBean(ViewsUpdateHandler.class);
            handler.updateViews(metadata, viewsArray, null, null, null);
        }

        Map<String, String> requestTableMap = prepareMapFromJsonObject(tables);
        Map<String, String> requestColumnMap = prepareMapFromJsonObject(columns);
        JsonArray duplicateTablesArray = prepareDuplicateTablesArray(formJson);
        JsonArray duplicateColumnsArray = prepareDuplicateColumnsArray(formJson);
        validate(requestTableMap, requestColumnMap, metadata);

        AddRemoveTableColumnsHandler.handleAddRemoveTableColumns(formJson, metadata);
        DuplicateItemHandler.duplicateTable(metadata, duplicateTablesArray);
        DuplicateItemHandler.duplicateColumns(metadata, duplicateColumnsArray);
//Add duplicate table and column
        JsonObject access = GsonUtility.optJsonObject(formJson, "access");
        if (access != null && !access.entrySet().isEmpty()) {
            if (access.has("action") && "deleteAll".equalsIgnoreCase(access.get("action").getAsString())) {
                metadata.setMetadataSecurity(null);
            } else {
                JsonArray expressionArray = access.getAsJsonArray("expression");
                // validateExpressionBelongsToMetadata(expressionArray,uuid,)
                if (!expressionArray.isEmpty()) {
                    MetadataSecurityUpdateHandler.setAccessTag(expressionArray, metadata);
                }
            }
        }

        updateMetadata(joins, requestTableMap, requestColumnMap, metadata);
        JsonArray externalJoinJson = GsonUtility.optJsonArray(formJson, "crossJoin");

//        if (externalJoinJson != null && !externalJoinJson.isEmpty()) {
//            ExternalRelationships relationships = JoinsHandler.prepareExternalRelationships(joins, metadata);
//            metadata.setExternalRelationships(relationships);
//        } else {
//            metadata.setExternalRelationships(null);
//        }

        if (DbMetadataUtils.checkMetadataBeforeSave(metadata)) {
            throw new MetadataServiceException("The selected database doesn't contains any tables/views/relationships.");
        }
        uuid = saveXml(fileName, location, uuid, extension, solutionDirectory, metadata, newLocation);

        JsonObject response = new JsonObject();
        response.addProperty("message", "Successfully saved metadata file");

        if (newLocation != null) {
            response.addProperty("location", newLocation);
        } else {
            response.addProperty("location", location);
        }

        if (firstTime) {
            response.addProperty("uuid", uuid + "." + extension);
        } else {
            response.addProperty("uuid", uuid);
        }
        return response.toString();
    }


    private JsonArray prepareDuplicateTablesArray(JsonObject formJson) {
        if (formJson.has("duplicate")) {
            JsonObject duplicate = formJson.getAsJsonObject("duplicate");
            if (duplicate.has("table")) {
                return duplicate.getAsJsonArray("table");
            }
        }
        return null;
    }

    private JsonArray prepareDuplicateColumnsArray(JsonObject formJson) {
        if (formJson.has("duplicate")) {
            JsonObject duplicate = formJson.getAsJsonObject("duplicate");
            if (duplicate.has("column")) {
                return duplicate.getAsJsonArray("column");
            }
        }
        return null;
    }
    /**
     * Prepares the reference for the database dialect based on the provided JSON data.
     * 
     * @param formJson 				 JSON object containing metadata update information
     * @param dataSourceId 			 ID of the data source
     * @param dataSourceType 		 type of the data source
     * @param dataSourceDir 		 directory of the data source
     * @return the reference to the database dialect
     */
    private String prepareReferenceForEfwd(JsonObject formJson, String dataSourceId, String dataSourceType,
                                           String dataSourceDir) {
        String reference;
        File efwdFile = ApplicationUtilities.getEfwdFile(dataSourceDir);

        JsonObject jsonOfEfwd = EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile);
        JsonObject efwdConnection = EfwdDatasourceUtils.getEfwdConnection(jsonOfEfwd, dataSourceId, dataSourceType);

        if (formJson.has("databaseDialect")) {
            reference = formJson.get("databaseDialect").getAsString();
        } else if ((efwdConnection.has("databaseDialect"))
                && !("".equals(efwdConnection.get("databaseDialect").getAsString())) && !("[]".equals(efwdConnection.get("databaseDialect").getAsString()))) {
            reference = efwdConnection.get("databaseDialect").getAsString();
        } else {
            reference = JsonUtils.functionsReference(efwdConnection.get(efwdConnection.has("Driver") ? "Driver" : "driverName").getAsString());
        }
        return reference;
    }
    /**
     * Prepares the reference for the database dialect for global data sources.
     * 
     * @param formJson 				 JSON object containing metadata update information
     * @param dataSourceId 			 ID of the data source
     * @return the reference to the database dialect
     */
    private String prepareReferenceForGlobal(JsonObject formJson, String dataSourceId) {
        String globalConnectionJson = DataSourceUtils.globalIdJson(Integer.parseInt(dataSourceId));
        JsonObject globalConnectionJSONObject = new Gson().fromJson(globalConnectionJson,JsonObject.class);
        String driverClassName = "";
        if (globalConnectionJSONObject.has("driverClassName")) {
            driverClassName = globalConnectionJSONObject.get("driverClassName").getAsString();
        } else if (globalConnectionJSONObject.has("driverName")) {
            driverClassName = globalConnectionJSONObject.get("driverName").getAsString();
        }

        String reference = null;
        if (formJson.has("databaseDialect")) {
            reference = formJson.get("databaseDialect").getAsString();
        } else if ((globalConnectionJSONObject.has("databaseDialect")) && !(formJson.has("databaseDialect"))
                && !("".equals(globalConnectionJSONObject.get("databaseDialect").getAsString()))) {
            reference = globalConnectionJSONObject.get("databaseDialect").getAsString();
        } else {
            reference = JsonUtils.functionsReference(driverClassName);
        }
        return reference;
    }
    /**
     * Saves the updated metadata XML file to the specified location.
     * 
     * @param fileName 				 name of the metadata file
     * @param location 				 location to save the metadata
     * @param uuid 					 UUID of the metadata file
     * @param extension 			 file extension of the metadata file
     * @param solutionDirectory 	 directory of the solution
     * @param metadata 				 metadata object containing the updated metadata
     * @param newLocation 			 new location to save the metadata
     * @return the UUID of the updated metadata file
     */
    private String saveXml(String fileName, String location, String uuid, String extension, String solutionDirectory,
                           Metadata metadata, String newLocation) {
        String fileLocation;
        if (uuid != null) {
            if (newLocation != null) {
                uuid = UUID.randomUUID().toString() + "." + extension;
                fileLocation = solutionDirectory + File.separator + newLocation + File.separator + uuid;
            } else {
                fileLocation = solutionDirectory + File.separator + location + File.separator + uuid;
            }
        } else {
            uuid = UUID.randomUUID().toString();
            fileLocation = solutionDirectory + File.separator + location + File.separator + uuid + "." + extension;
        }
        metadata.setFileName(fileName);

        File file = new File(fileLocation);
        if (file.getAbsolutePath().contains("_temp_")) {
            file.getParentFile().mkdirs();
        }
        synchronized (this) {
            JaxbUtils.marshal(metadata, file);
        }
        return uuid;
    }
    /**
     * Updates the metadata with the provided joins, tables, and columns.
     * 
     * @param joins 					 JSON array representing joins in the metadata
     * @param requestTableMap 			  map of requested table names and aliases
     * @param requestColumnMap            map of requested column names and aliases
     * @param metadata 					  metadata object to update
     */
    private void updateMetadata(JsonArray joins, Map<String, String> requestTableMap, Map<String,
            String> requestColumnMap, Metadata metadata) {

        Database database = metadata.getDatabase();
        String databaseName = database.getName();
        Tables tablesObject = database.getTables();

        List<Table> tableList;
        if (tablesObject != null) {
            tableList = tablesObject.getTableList();
            if (tableList != null) {
                for (Table table : tableList) {
                    if (requestColumnMap.isEmpty() && requestTableMap.isEmpty()) {
                        break;
                    }

                    if (!requestTableMap.isEmpty()) {
                        processTableAliases(requestTableMap, table, databaseName);
                    }
                    if (!requestColumnMap.isEmpty()) {
                        processColumnAliases(requestColumnMap, table);
                    }
                }
            }
        }

//        if (joins != null && !joins.isEmpty()) {
//            Relationships relationships = JoinsHandler.prepareRelationships(joins, metadata);
//            database.setRelationships(relationships);
//        } else {
//            database.setRelationships(null);
//        }
    }
    /**
     * Validates the provided metadata changes.
     * 
     * @param requestTableMap 			 map of requested table names and aliases
     * @param requestColumnMap 			 map of requested column names and aliases
     * @param metadata 					 metadata object to validate
     * @throws MetadataServiceException if the metadata changes are invalid
     */
    private void validate(@NotNull Map<String, String> requestTableMap, @NotNull Map<String,
            String> requestColumnMap, @NotNull Metadata metadata) {
        ValidationUtility validationUtility = new ValidationUtility(metadata);
        Map<String, String> actualTableMap = validationUtility.getTablesMap();
        if (!isValidTableOrColumn(actualTableMap, requestTableMap)) {
            throw new MetadataServiceException("The selected database does not contain one or " + "all of the " +
                    "provided table(s)");
        }

        Map<String, String> actualColumnMap = validationUtility.getColumnsMap();
        if (!isValidTableOrColumn(actualColumnMap, requestColumnMap)) {
            throw new MetadataServiceException("The selected database does not contain one or " + "all of the " +
                    "provided column(s)");
        }

        if (doesAliasExists(actualTableMap, requestTableMap)) {
            throw new MetadataServiceException("The selected database already contains one or " + "all of the " +
                    "provided table alias name(s). Please choose a different name.");
        }

        if (doesAliasExists(actualColumnMap, requestColumnMap)) {
            throw new MetadataServiceException("The selected database already contains one or " + "all of the " +
                    "provided column alias name(s). Please choose a different name.");
        }
    }

    /**
     * Column names have been checked and sets alias name .
     * @param table                table provides column
     * @param column			   column name 
     * @param alias				   column alias name
     */
    private void editColumn(@NotNull Table table, @NotNull String column, String alias) {
        Columns columns = table.getColumns();
        if (columns != null) {
            List<Column> columnList = columns.getColumn();
            for (Column theColumn : columnList) {
                if (theColumn != null) {
                    if (column.equalsIgnoreCase(theColumn.getName())) {
                        theColumn.setAliasName(alias);
                        break;
                    }
                }
            }
        }
    }
    /**
     * Processes table name and alias name based on the provided JSON data.
     * 
     * @param requestTables 	 map of requested table names and aliases
     * @param table    			 the table object to update
     * @param databaseName the name of the database
     */
    private void processTableAliases(@NotNull Map<String, String> requestTables, @NotNull Table table,
                                     String databaseName) {
        String tableName = table.getName();
        String changedAlias = requestTables.get(tableName);

        if (changedAlias == null) {
            if (!"".equals(databaseName)) {
                //In case of normal databases, the table name is not included with databaseName
                String key = databaseName + "." + tableName;
                changedAlias = requestTables.get(key);
                if (changedAlias != null) {
                    // Alias name is changed
                    table.setAliasName(changedAlias);
                    removeTable(requestTables, key);
                }
            } else {
                changedAlias = requestTables.get(tableName);
                if (changedAlias != null) {
                    // Alias name is changed
                    table.setAliasName(changedAlias);
                    removeTable(requestTables, tableName);
                }
            }
        } else {
            // Exact table name is found. So no need to check for equals
            table.setAliasName(changedAlias);
            removeTable(requestTables, tableName);
        }
    }
    /**
     * 
     * @param requestTables    tables map 
     * @param key			   to remove from map.
     */
    private void removeTable(Map<String, String> requestTables, String key) {
        if (key != null && !requestTables.isEmpty()) {
            requestTables.remove(key);
        }
    }
    /**
     * Processes column name and alias name based on the provided JSON data.
     * 
     * @param requestColumnsMap 			 map of requested column names and alias name
     * @param table                          the table object to update
     */
    private void processColumnAliases(@NotNull Map<String, String> requestColumnsMap, @NotNull Table table) {
        String toBeRemoved = null;
        //There may be multiple columns from the same table. So loop is required.
        for (String fullColumnName : requestColumnsMap.keySet()) {
            String changedAlias = requestColumnsMap.get(fullColumnName);

            int lastIndexOf = fullColumnName.lastIndexOf(".");//To extract full table name
            String simpleColumnName = fullColumnName.substring(lastIndexOf + 1);
            String fullTableName = fullColumnName.substring(0, lastIndexOf);

            String tableName = table.getName();

            if (fullTableName.equalsIgnoreCase(tableName)) {
                // Calcite use case
                if (changedAlias != null) {
                    toBeRemoved = fullColumnName;
                    editColumn(table, simpleColumnName, changedAlias);
                }
            } else {
                // Normal databases
                int indexOf = fullTableName.lastIndexOf(".");
                //String justTableName = fullTableName.substring(indexOf + 1);
                //The above logic dont work when the table name has period(.) in their name
                //Thus this logic is modified to

                String justTableName = fullTableName.length() > tableName.length() ? fullTableName.substring(fullTableName.length() - tableName.length()) : null;

                if (justTableName != null && justTableName.equalsIgnoreCase(tableName)) {
                    if (changedAlias != null) {
                        toBeRemoved = fullColumnName;
                        editColumn(table, simpleColumnName, changedAlias);
                    }
                }
            }
        }

        if (toBeRemoved != null && !requestColumnsMap.isEmpty()) {
            requestColumnsMap.remove(toBeRemoved);
        }
    }
    /**
     * Checks if the provided table or column names are valid.
     * 
     * @param actualMap 				 map of actual table or column names
     * @param requestMap 				 map of requested table or column names
     * @return {@code true} if the provided names are valid, {@code false} otherwise
     */
    private boolean isValidTableOrColumn(@NotNull Map<String, String> actualMap, @NotNull Map<String,
            String> requestMap) {
        Set<String> keySetActual = actualMap.keySet();
        Set<String> keySetRequested = requestMap.keySet();
        return keySetActual.containsAll(keySetRequested);
    }
    /**
     * Checks if the provided aliases already exist in the metadata.
     * 
     * @param actualMap 				 map of actual aliases
     * @param requestMap 				 map of requested aliases
     * @return {@code true} if any of the requested aliases already exist, {@code false} otherwise
     */
    private boolean doesAliasExists(@NotNull Map<String, String> actualMap, @NotNull Map<String, String> requestMap) {
        Collection<String> existingAliases = actualMap.values();
        Collection<String> requestedAliases = requestMap.values();
        for (String alias : requestedAliases) {
            if (existingAliases.contains(alias)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Prepares a map of table names and aliasName from the provided JSON object.
     * 
     * @param json 				 JSON object containing table or column information
     * @return a map of table or column names and aliases
     */
    @NotNull
    private Map<String, String> prepareMapFromJsonObject(@Nullable JsonObject json) {
        HashMap<String, String> changesMap = new HashMap<>();
        if (json == null) {
            return changesMap;
        }
        List<String> keyList = JsonUtils.getKeys(json);
        for (String key : keyList) {
            String value = json.get(key).getAsString();
            changesMap.put(key, value);
        }
        return changesMap;
    }
}