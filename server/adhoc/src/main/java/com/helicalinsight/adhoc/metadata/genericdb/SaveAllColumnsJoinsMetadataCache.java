package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.CachedColumnsProviderComponent;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.admin.utils.AuditContext;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.*;

/**
 * This class is responsible for saving metadata cache for all columns and joins.
 * It implements the ISaveMetadataCache interface.
 * 
 * 
 * @author Somen
 * Created on 4/9/2019.
 */
@Component
@Scope("prototype")
public class SaveAllColumnsJoinsMetadataCache implements ISaveMetadataCache {
    protected List<String> addList = new ArrayList<>();
    protected List<String> addedList = new ArrayList<>();
    @Autowired
    protected DatabaseCacheService databaseCacheService;

    @Autowired
    GlobalConnectionService globalConnectionService;

    private boolean shouldChangeExistingTables=false;

    protected JsonObject formData;

    protected Map<String, List<JsonObject>> duplicateTableMaps = new HashMap<>();
    protected Map<String, List<JsonObject>> duplicateTableIdColumns = new HashMap<>();

    protected Map<String, String> tableAlias = new HashMap<>();
    protected Map<String, String> columnAlias = new HashMap<>();
    private JsonArray viewsArray;
    protected List<String> removeViewColumn = new ArrayList<>();

    protected JsonObject dataSource;
    /**
     * Sets the form data for saving metadata cache.
     * @param formData 		 form data containing metadata information like views , joins.
     */
    @Override
    public void setFormData(JsonObject formData) {
        this.formData = formData;
        JsonObject addItem = this.formData.getAsJsonObject("addItem");
        viewsArray = addItem.getAsJsonArray("views");
        populateDuplicateMap();
    }
    /**
     * Populates the map containing duplicate table and column information.
     */
    protected void populateDuplicateMap() {
        if (this.formData.has("duplicate")) {
            JsonObject duplicate = this.formData.getAsJsonObject("duplicate");
            if (duplicate.has("table")) {
                JsonArray tableArray = duplicate.getAsJsonArray("table");
                for (Object item : tableArray) {
                    JsonObject jsonItem = (JsonObject) item;
                    String key = jsonItem.get("originalId").getAsString();
                    List<JsonObject> duplicateItems = this.duplicateTableMaps.get(key);
                    if (duplicateItems == null) {
                        duplicateItems = new ArrayList<>();
                    }
                    duplicateItems.add(jsonItem);
                    this.duplicateTableMaps.put(key, duplicateItems);
                }
            }
            if (duplicate.has("column")) {
                JsonArray columnArray = duplicate.getAsJsonArray("column");
                for (Object item : columnArray) {
                    JsonObject jsonItem = (JsonObject) item;
                    String key = jsonItem.get("tableId").getAsString();
                    List<JsonObject> duplicateItems = this.duplicateTableIdColumns.get(key);
                    if (duplicateItems == null) {
                        duplicateItems = new ArrayList<>();
                    }
                    duplicateItems.add(jsonItem);
                    this.duplicateTableIdColumns.put(key, duplicateItems);
                }
            }

        }

        if (this.formData.has("changeItem")) {
            JsonObject changeItem = this.formData.getAsJsonObject("changeItem");
            JsonArray tables = changeItem.getAsJsonArray("tables");
            for (int index = 0; index < tables.size(); index++) {
                JsonObject jsonObject = tables.get(index).getAsJsonObject();
                if(!jsonObject.has("id")){
                    throw new MetadataRetrievalException("Please have id and alias for changeItem tables");
                }
                this.tableAlias.put(jsonObject.get("id").getAsString(), jsonObject.get("alias").getAsString());
            }
            JsonArray columns = changeItem.getAsJsonArray("columns");
            for (int index = 0; index < columns.size(); index++) {
                JsonObject jsonObject = columns.get(index).getAsJsonObject();
                if(!jsonObject.has("columnId")){
                    throw new MetadataRetrievalException("Please have id and alias for changeItem columns");
                }
                this.columnAlias.put(jsonObject.get("columnId").getAsString(), jsonObject.get("alias").getAsString());
            }
        }
    }
    /**
     * Adds joins to the database metadata.
     * @param database 			 database object
     */
    @Override
    public void addJoins(Database database) {
        DataSourceMapping dsMapping = new DataSourceMapping();
        dsMapping.setType("partial_joins");
        List<JsonObject> resultJoins = getDeSerializedResult(dsMapping);
        Relationships relationships = ApplicationContextAccessor.getBean(Relationships.class);
        List<Relationship> relationshipList = getRelationShips(resultJoins);
        relationships.setListOfRelations(relationshipList);
        database.setRelationships(relationships);
    }
    /**
     * Adds table columns to the database metadata.
     * @param database 			 database object
     */
    @Override
    public void addTableColumns(Database database) {
        DataSourceMapping dsMapping = new DataSourceMapping();
        dsMapping.setType("partial_column");
        List<JsonObject> resultColumns = getDeSerializedResult(dsMapping);
        // if (resultColumns == null || resultColumns.isEmpty()) {

        resultColumns = getDataForSubClasses(dsMapping, addList);
        //}
        Tables tables = ApplicationContextAccessor.getBean(Tables.class);
        List<Table> listOfTables = getListOfTables(resultColumns);
        if (addList.size() > 0 && addList.contains("all")){
            addList.remove("all");
        }
        if (listOfTables.size() < addList.size()) {
            throw new MetadataRetrievalException("Could not save metadata. Cache still in progress Please try again later");
        }
        tables.setTableList(listOfTables);
        database.setTables(tables);
    }
    /**
     * Adds other metadata information to the database.
     * @param metadata 			 metadata object
     */
    @Override
    public void addOthers(Metadata metadata) {
        ViewsUpdateHandler handler = ApplicationContextAccessor.getBean(ViewsUpdateHandler.class);
        handler.updateViews(metadata, this.viewsArray, this.tableAlias, this.columnAlias, this.removeViewColumn);
        //JSONArray joins = this.formData.optJSONArray("joins");
        //Relationships relationships = ApplicationContextAccessor.getBean(Relationships.class);
        //if(joins != null)   relationships = JoinsHandler.prepareRelationships(joins, metadata);
        Database database = metadata.getDatabase();
        //if(relationships.getListOfRelations() != null) database.setRelationships(relationships);
        if (GsonUtility.optBoolean(dataSource, "changed")) {
            MetadataDataSourceChangeHandler changeHandler = ApplicationContextAccessor.getBean
                    (MetadataDataSourceChangeHandler.class);
            if(formData.has("newLocation"))
            	dataSource.addProperty("saveas",true);
            	dataSource.addProperty("oldConnectionId", metadata.getConnectionDetails().getConnectionId());
            changeHandler.change(null, dataSource, metadata);
            String catalog = dataSource.get("catalog").getAsString();
            String schema = dataSource.get("schema").getAsString();
            if (!shouldChangeExistingTables) {
                //No change.
                return;
            }
            database.getSchema();
            Tables tables = database.getTables();
            if (tables != null) {
                List<Table> tableList = tables.getTableList();
                if (tableList != null) {
                    for (Table table : tableList) {
                        if (table.getType() == null && table.getOriginalName() == null) {
                        	if(table.getId() != null && !StringUtils.isNumeric(table.getId())) {
                        		table.setId(MetadataUtils.getId(catalog, schema, table.getName()));
                        	}
                        }
                    }
                }
            }
        }

    }
    /**
     * Retrieves the metadata.
     * @return The metadata object
     */
    @Override
    public Metadata getMetadata() {

        return ApplicationContextAccessor.getBean(Metadata.class);
    }

    @Override
    public void setMetadata(Metadata metadata) {

    }
    /**
     * Retrieves the database information.
     * @return The database object
     */
    @Override
    public Database getDatabase() {
        return ApplicationContextAccessor.getBean(Database.class);
    }
    /**
     * Retrieves the relationships between tables.
     * 
     * @param resultJoins 				 list of joins
     * @return The list of relationships
     */
    protected List<Relationship> getRelationShips(List<JsonObject> resultJoins) {
        List<Relationship> relationshipList = new ArrayList<>();
        Map<String, List<Join>> joinMaps = new LinkedHashMap<>();
        for (JsonObject joinsJson : resultJoins) {
            JsonArray joinsArray = joinsJson.getAsJsonObject("response").getAsJsonArray("joins");
            eachJoin(joinMaps, joinsArray);
        }

        Set<String> keySet = joinMaps.keySet();
        for (String key : keySet) {
            setRelationship(relationshipList, joinMaps, key);
        }

        return relationshipList;
    }
    /**
     * Sets the relationship between tables.
     * 
     * @param relationshipList 		 list of relationships
     * @param joinMaps 				 map containing joins
     * @param key 					 key for the relationship
     */
    protected void setRelationship(List<Relationship> relationshipList, Map<String, List<Join>> joinMaps, String key) {
        Relationship relationship = ApplicationContextAccessor.getBean(Relationship.class);
        String[] split = key.split("_#_");
        relationship.setTable(split[0]);
        relationship.setReferenceTable(split[1]);
        List<Join> join = joinMaps.get(key);
        relationship.setJoin(join);
        if (!join.isEmpty())
            relationshipList.add(relationship);
    }
    /**
     * Processes each join in the list of joins.
     * 
     * @param joinMaps 		 map containing joins
     * @param joinsArray 	 array containing joins
     */
    protected void eachJoin(Map<String, List<Join>> joinMaps, JsonArray joinsArray) {
        for (Object joinJsonObject : joinsArray) {
            setJoins(joinMaps, (JsonObject) joinJsonObject);
        }
    }
    /**
     * Sets the join information.
     * 
     * @param joinMaps 			 map containing joins
     * @param joinJsonObject 	 JSON object containing join information
     */
    protected void setJoins(Map<String, List<Join>> joinMaps, JsonObject joinJsonObject) {
        JsonObject joinJson = (JsonObject) joinJsonObject;
        String id = joinJson.get("id").getAsString();
        String type = joinJson.get("type").getAsString();
        String operator = joinJson.get("operator").getAsString();
        
        JsonObject dataSource = this.formData.getAsJsonObject("dataSource");

        JsonObject leftInfo = joinJson.getAsJsonObject("left");
        String leftTableName = leftInfo.get("table").getAsString();
        String leftColumn = leftInfo.get("column").getAsString();
        LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
        leftTable.setColumn(leftColumn);
        leftTable.setTable(leftTableName);
        // TODO :: this is required.Since many test cases failing made it as optional.
        leftTable.setDbId(GsonUtility.optString(dataSource, "dbId"));

        JsonObject rightInfo = joinJson.getAsJsonObject("right");
        String rightTableName = rightInfo.get("table").getAsString();
        String rightColumn = rightInfo.get("column").getAsString();
        RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
        rightTable.setColumn(rightColumn);
        rightTable.setTable(rightTableName);
        // TODO :: this is required.Since many test cases failing made it as optional.
        rightTable.setDbId(GsonUtility.optString(dataSource,"dbId"));

        Join join = ApplicationContextAccessor.getBean(Join.class);
        join.setType(type);
        join.setLeftTable(leftTable);
        join.setRightTable(rightTable);
        join.setOperator(operator);
        join.setId(id);

        String relationShipKey = leftTableName + "_#_" + rightTableName;
        List<Join> joins = joinMaps.get(relationShipKey);
        if (joins == null) {
            joins = new ArrayList<>();
            joinMaps.put(relationShipKey, joins);
        }
        checkBeforeAddingJoin(join, joins);
    }
    /**
     * Checks if the join already exists before adding it.
     * 
     * @param join 				 join object to add
     * @param joins 			 list of joins
     */
    protected void checkBeforeAddingJoin(Join join, List<Join> joins) {
        if(!joins.contains(join))
        		joins.add(join);
    }
    /**
     * Retrieves the list of tables from the JSON object.
     * 
     * @param tablesJsonList 			 list of tables JSON objects
     * @return The list of table objects
     */
    @NotNull
    protected List<Table> getListOfTables(List<JsonObject> tablesJsonList) {
        List<Table> tableList = new ArrayList<>();
        for (JsonObject tableJson : tablesJsonList) {
            //json.response.metadata.tables
            JsonObject tablesJson = tableJson.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonObject("tables");
            Set<String> set = tablesJson.keySet();
            setTable(tableList, tablesJson, set);
        }
        return tableList;
    }
    /**
     * Processes the tables JSON object and adds tables to the list.
     * 
     * @param tableList 				 list of tables
     * @param tablesJson 				 tables JSON object
     * @param set 						 set containing table names
     */
    protected void setTable(List<Table> tableList, JsonObject tablesJson, Set<String> set) {
        for (String tableName : set) {
            if (!this.addedList.contains(tableName)) {
                setTable(tableList, tablesJson, tableName);
                this.addedList.add(tableName);
            }
        }
    }
    
    protected void setTable(List<Table> tableList, JsonObject tablesJson, String tableName) {
        JsonObject tableItem = tablesJson.getAsJsonObject(tableName);
        handleTableAdd(tableList, tableName, tableItem);

        String tableId = tableItem.get("id").getAsString();
        if (this.duplicateTableMaps.containsKey(tableId)) {
            List<JsonObject> listOfDuplicate = this.duplicateTableMaps.get(tableId);
            for (JsonObject item : listOfDuplicate) {
                Table table = ApplicationContextAccessor.getBean(Table.class);
                String id = GsonUtility.optStringValue(item, "id",AdhocUtils.getUuid());
                table.setId(id);
                table.setOriginalName(item.get("originalName").getAsString());
                table.setAliasName(item.get("alias").getAsString());
                table.setName(item.get("name").getAsString());
                Columns columns = ApplicationContextAccessor.getBean(Columns.class);
                List<Column> columnsList = new ArrayList<>();
                JsonObject columnJson = tableItem.getAsJsonObject("columns");
                JsonArray duplicateColumns = item.getAsJsonArray("columns");
                
                List<JsonObject> dupColList = this.duplicateTableIdColumns.get(id);
                Map<String,JsonObject> map = new HashMap<>();
                if( dupColList != null && !dupColList.isEmpty()) {
                	dupColList.forEach(it -> map.put(it.get("name").getAsString(), it));
                }
                if(!map.isEmpty()) {
                	for(Object obj : duplicateColumns) {
                		JsonObject colObj = (JsonObject) obj;
                		
                		if( map.containsKey(colObj.get("name").getAsString())) {
                			JsonObject mappedCol = map.get(colObj.get("name").getAsString());
                			duplicateColumns.remove(colObj);
                			duplicateColumns.add(mappedCol);
                		}
                	}
                }
                
                Set<String> columnSet = columnJson.keySet();
                handleDuplicateTable(columnsList, columnJson, duplicateColumns, columnSet);
                columns.setColumn(columnsList);
                table.setColumns(columns);
                tableList.add(table);
            }

        }

    }
    
   

    protected void handleDuplicateTable(List<Column> columnsList, JsonObject columnJson, JsonArray duplicateColumns, Set<String> columnSet) {
        for (Object it : duplicateColumns) {
            JsonObject object = (JsonObject) it;
            String columnId = object.get("originalId").getAsString();
            for (String columnName : columnSet) {
                JsonObject columnItem = columnJson.getAsJsonObject(columnName);
                String actualId = columnItem.get("id").getAsString();
                if (actualId.equalsIgnoreCase(columnId)) {
                    columnsDuplication(columnsList, object, columnName, columnItem);
                    break;
                }
            }
        }
    }

    protected void columnsDuplication(List<Column> columnsList, JsonObject object, String columnName, JsonObject columnItem) {
        Column column = ApplicationContextAccessor.getBean(Column.class);
        String originalName = object.has("name") ? object.get("name").getAsString() : columnName;
        column.setName(originalName);
        column.setId(GsonUtility.optStringValue(object, "id",AdhocUtils.getUuid()));
        column.setAliasName(object.get("alias").getAsString());
        column.setDefaultFunction(GsonUtility.optString(columnItem, "defaultFunction"));
        JsonElement jsonElement = columnItem.get("type");
        String type = jsonElement.toString();
        String replace = type.split(":")[0].replace("\"", "").replace("{", "");
        column.setType(replace);
        if (!originalName.equals(columnName))
            column.setOriginalName(columnName);
        columnsList.add(column);
    }

    protected void handleTableAdd(List<Table> tableList, String tableName, JsonObject tableItem) {
        Table table = ApplicationContextAccessor.getBean(Table.class);
        String originalAlias = tableItem.get("alias").getAsString();
        String tableId = tableItem.get("id").getAsString();
        String tableAliasName = this.tableAlias.get(tableId);
        table.setAliasName(tableAliasName == null ? originalAlias : tableAliasName);
        table.setId(tableId);
        table.setName(tableName);
        JsonObject originalColumns = tableItem.getAsJsonObject("columns");
        Columns columns = setColumns(originalColumns);
        table.setColumns(columns);
        if (this.duplicateTableIdColumns.containsKey(tableId)) {
            List<Column> columnsList = columns.getColumn();
            List<JsonObject> jsonObjects = this.duplicateTableIdColumns.get(tableId);
            for (JsonObject dupColumn : jsonObjects) {
                String originalId = dupColumn.get("originalId").getAsString();
                Set<String> columnSet = originalColumns.keySet();
                for (String columnName : columnSet) {
                    JsonObject columnItem = originalColumns.getAsJsonObject(columnName);
                    String columnId = columnItem.get("id").getAsString();
                    if (columnId.equalsIgnoreCase(originalId)) {
                        Column column = ApplicationContextAccessor.getBean(Column.class);

                        String alias = dupColumn.get("alias").getAsString();
                        String dupOriginalName = GsonUtility.optString(dupColumn, "name");
                        column.setName(dupOriginalName);
                        column.setId(GsonUtility.optStringValue(dupColumn,"id",AdhocUtils.getUuid()));
                        column.setAliasName(alias);
                        column.setDefaultFunction(GsonUtility.optString(columnItem,"defaultFunction"));
                        JsonElement jsonElement = columnItem.get("type");
                        String string = jsonElement.toString();
                        String replace = string.split(":")[0].replace("\"", "").replace("{", "");
                        column.setType(replace);
                        column.setOriginalName(columnName);
                        columnsList.add(column);
                        break;
                    }
                }
            }
        }
        tableList.add(table);
    }

    protected Columns setColumns(@NotNull JsonObject columnJson) {
        Columns columns = ApplicationContextAccessor.getBean(Columns.class);
        List<Column> columnsList = new ArrayList<>();

        Set<String> columnSet = columnJson.keySet();
        for (String columnName : columnSet) {
            JsonObject columnItem = columnJson.getAsJsonObject(columnName);
            addToColumnList(columnsList, columnName, columnItem);
        }
        columns.setColumn(columnsList);
        return columns;
    }
    /**
     * Processes the columns JSON object and adds columns to the list.
     * 
     * @param columnsList 			 list of column
     * @param columnName 			 column name
     * @param columnItem			 object provides id, alias , type .
     */
    protected void addToColumnList(List<Column> columnsList, String columnName, JsonObject columnItem) {
        Column column = ApplicationContextAccessor.getBean(Column.class);
        column.setName(columnName);
        String columnId = columnItem.get("id").getAsString();
        String columnsAliasName = this.columnAlias.get(columnId);
        column.setId(columnId);
        String originalAlias = columnItem.get("alias").getAsString();
        column.setAliasName(columnsAliasName != null ? columnsAliasName : originalAlias);
        column.setDefaultFunction(GsonUtility.optString(columnItem, "defaultFunction"));
        JsonElement jsonElement = columnItem.get("type");
        String string = jsonElement.toString();
        String replace = string.split(":")[0].replace("\"", "").replace("{", "");
        column.setType(replace);
        columnsList.add(column);
    }
    /**
     * Sets the database name and other details.
     * 
     * @param database 		 database object provides  catalog and schema .
     */
    public void setDatabaseName(@NotNull Database database) {
        String catalog = dataSource.get("catalog").getAsString();
        String schema = dataSource.get("schema").getAsString();
        String dot = "";
        if (!catalog.isEmpty() && !schema.isEmpty()) {
            dot = ".";
        }
        shouldChangeExistingTables=!(catalog.equals(database.getCatalog()) && schema.equals(database.getSchema()));
        database.setName(catalog + dot + schema);
        database.setCatalog(catalog);
        database.setSchema(schema);
        String dbId = database.getId();
        if (dbId == null) {
            dbId = AdhocUtils.getUuid();
            database.setId(dbId);
    }
    }
    /**
     * Adds connection details to the metadata.
     * 
     * @param formJson 		 JSON object containing dataSource
     * @param metadata 		 metadata object
     */
    @Override
    public void addConnectionDetails(JsonObject formJson, Metadata metadata) {
        ConnectionTemplate connectionTemplate = ApplicationContextAccessor.getBean(ConnectionTemplate.class);
        JsonObject dataSource = formJson.getAsJsonObject("dataSource");

        setDatabaseType(metadata, dataSource);
        connectionTemplate.setConnectionTag(dataSource, metadata);
        metadata.setVisible("true");
        this.dataSource = dataSource;
    }
    /**
     * Saves the metadata cache to disk.
     * 
     * @param formJson 				 JSON object containing location
     * @param metadata 				 metadata object provides fileName
     * @return The response JSON object
     */
    @Override
    public JsonObject saveFileToDisk(JsonObject formJson, Metadata metadata) {
        String extension = JsonUtils.getMetadataExtension();
        String mode="create";
        String innerMetadataFileName = metadata.getFileName();
        innerMetadataFileName = DBProcessor.checkAndReplaceSpecialChars(innerMetadataFileName).trim();
        if (formData.has("metadataReload") && formData.get("metadataReload").getAsBoolean()
                && formData.has("uniqueId")) {

           mode="create";
        }else  if (formData.has("metadataReload") && formData.get("metadataReload").getAsBoolean()
                && formData.has("uuid")) {

            mode="edit";
        }
        String dir = formJson.get("location").getAsString();
        if (this.formData.has("newLocation")) {
            dir = formData.get("newLocation").getAsString();
            mode="saveas";
        }

        metadata.setVersion("5.0");
        metadata.setMetadataId("");
        MetadataDBUtility metadataDBUtility = new MetadataDBUtility();
        AuditContext context = new AuditContext();
        String fileNameWithExtension = innerMetadataFileName + "." + extension;
        JsonObject response = new JsonObject();
        metadataDBUtility.setFormJson(formJson);
        metadataDBUtility.setContext(context);
        String uuid = metadataDBUtility.saveMetadataToDB(metadata, dir, fileNameWithExtension, mode);
        response.addProperty("uuid", uuid);
        response.addProperty("message", "Successfully saved metadata file");
        response.addProperty("location", dir);
        return response;
    }

    /**
     * Sets the database type based on the data source.
     * 
     * @param metadata 		 	 metadata object
     * @param dataSource 		 data source JSON object
     */
    private void setDatabaseType(Metadata metadata, JsonObject dataSource) {
        Connection connection = null;
        String dataSourceType = GsonUtility.optString(dataSource, "type");
        AdhocServiceUtils.addExtraDataForNormalProcess(dataSource, dataSourceType);
        DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(dataSource,
                dataSourceType);
        if (driverConnection != null) {
            connection = driverConnection.getConnection();
            dataSource.addProperty("dialect", MetadataUtils.dialectOfDatabase(driverConnection.getDriverClass()));
        }
        if (connection == null) {
            throw new EfwdServiceException("The connection object is null.");
        }
        metadata.setDatabaseType(MetadataUtils.getDatabaseDetails(connection));
    }
    /**
     * Retrieves deserialized result from the data source mapping.
     * 
     * @param dsMapping 		 data source mapping object
     * @return The list of JSON objects containing result
     */
    protected List<JsonObject> getDeSerializedResult(DataSourceMapping dsMapping) {
        dsMapping.setDir(GsonUtility.optString(dataSource, "dir"));
        dsMapping.setConnectionId(dataSource.get("id").getAsInt());
        dsMapping.setCatalog(GsonUtility.optString(dataSource,"catalog"));
        dsMapping.setSchema(dataSource.get("schema").getAsString());
        if (GsonUtility.optBoolean(this.dataSource,"changed")) {
            dsMapping.setFile("ignore");
        }
        return getAllData(dsMapping);
    }
    /**
     * Retrieves all data from the data source mapping.
     * 
     * @param dsMapping 		 data source mapping object
     * @return The list of JSON objects containing result
     */
    protected List<JsonObject> getAllData(DataSourceMapping dsMapping) {
        return getPartialData(dsMapping);
    }
    /**
     * Retrieves partial data from the data source mapping.
     * 
     * @param dsMapping 			 data source mapping object
     * @return The list of JSON objects containing result
     */
    private List<JsonObject> getPartialData(DataSourceMapping dsMapping) {
        List<ApplicationCache> allData = databaseCacheService.findApplicationCacheByDataSourceMapping(dsMapping);
        List<JsonObject> jsonArray = new ArrayList<>();
        if (allData != null && !allData.isEmpty()) {
            allData.forEach(item -> {
                Object deSerialize = ApplicationUtilities.unCompressObject(item);
                String json = deSerialize.toString();
                JsonObject fromJson = new Gson().fromJson(json,JsonObject.class);
                jsonArray.add(fromJson);
            });
        }
        return jsonArray;
    }
    /**
     * Handles duplicate tables and columns.
     * 
     * @param dsMapping 		 data source mapping object
     * @param addList 			 list of tables to add
     * @return The list of JSON objects containing result
     */
    protected List<JsonObject> getDataForSubClasses(DataSourceMapping dsMapping, List<String> addList) {
        if (dsMapping.getType().equals("partial_joins")) {
            return getPartialData(dsMapping);
        }
        String type = dsMapping.getType();
        Map<String, String> idNameMap = CachedColumnsProviderComponent.prepareIdNameMap(dsMapping);

        Set<String> tableNames = idNameMap.keySet();
        if (addList == null || addList.isEmpty()) {
            addList = new ArrayList<>();
            addList.addAll(tableNames);
        }
        if (addList.size() > 0 && addList.contains("all")) {

            this.addList.remove("all");

        }

        List<String> unClickedTables = databaseCacheService.findUnClickedTables(dsMapping, addList);
        List<String> tableNameList = new ArrayList<String>();
        List<String> tableIdList = new ArrayList<String>();

        if (!unClickedTables.isEmpty()) {
            for (String it : unClickedTables) {
                for (String id : tableNames) {
                    String substring = it.substring(1, it.length() - 1);
                	//String substring = it.replace("\"", "");
                    if (id.equals(substring)) {
                        tableNameList.add(idNameMap.get(id));
                        tableIdList.add(id);
                    }
                }
            }
        }

        dsMapping.setType(type);
        this.formData.add("tableNames", new Gson().fromJson(tableNameList.toString(),JsonArray.class));
        this.formData.add("tableIds", new Gson().fromJson(tableIdList.toString(),JsonArray.class));
        //trigger the service call in threads
        if (!tableNameList.isEmpty()) {
            CachedColumnsProviderComponent.getResult(this.formData);
            //todo Hardcoded to wait until all completes
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            if (tableIdList.size() > 25) {
                Integer timeToWait = tableIdList.size() / 25 + 1;
                for (int i = 0; i < timeToWait; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {

                    }
                }
            }
        }

        List<JsonObject> jsonArray = new ArrayList<>();
        jsonArray.addAll(getPartialData(dsMapping));
        return jsonArray;
    }

	


}
