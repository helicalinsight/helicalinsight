package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuditContext;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class EditMetadataSaveHandler extends {@link SaveAllColumnsJoinsMetadataCache}.
 * it deals with the metadata save, checking uniqueness of table , columns and 
 * This class is responsible for processing metadata and managing the relationships between joins.
 * @author Somen
 */
@Component
@Scope("prototype")
public class EditMetadataSaveHandler extends SaveAllColumnsJoinsMetadataCache {
	
	
	@Autowired
	HIMetadataResourceServiceDB metadataServiceDB;
	
    private Metadata metadata;
    private String fileLocation;
    private String newFileLocation;
    protected List<String> removeTableList = new ArrayList<>();
    protected List<String> removeColumnList = new ArrayList<>();
    protected Map<String, Join> newJoinsMap = new HashMap<>();
    protected List<String> newJoinsId = new ArrayList<>();
    private String fileNameWithExtension;
    private Map<String,Table> tableMap;
    /**
     * This method is returns metadata object using formData details like location and extension file.
     */
    public Metadata getMetadata() {
        String location = this.formData.get("location").getAsString();
        fileNameWithExtension = this.formData.get("uuid").getAsString();
        this.fileLocation =  location + "/" + fileNameWithExtension;
        this.newFileLocation = "";
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + fileNameWithExtension);
        if(metadataResource==null){
            throw new MetadataServiceException("The Resource does not exists");
        }
        Metadata databaseMetadata  = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());
        //databaseMetadata.setFileName(location);
        this.metadata = databaseMetadata;


        processRelationship();
        return metadata;
    }
    /**
     * This method process the relationships between joins.
     */
    public void processRelationship() {
        Relationships relationships = this.metadata.getDatabase().getRelationships();
        if (relationships != null) {
            List<Relationship> listOfRelations = relationships.getListOfRelations();
            if (listOfRelations != null) {
                for (Relationship relationship : listOfRelations) {
                    List<Join> joinsList = relationship.getJoin();
                    if (joinsList != null) {
                        for (Join joinId : joinsList) {
                            String joinIds = joinId.getId();
                            newJoinsId.remove(joinIds);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Database getDatabase() {
        return this.metadata.getDatabase();
    }


    @Override
    public void addConnectionDetails(JsonObject formJson, Metadata metadata) {
        //do nothing
    }
    /**
     * Sets the form data into their respective properties to perform necessary tasks.
     * @param formData      which provides added tables , removed tables , joins etc.
     */
    @Override
    public void setFormData(JsonObject formData) {
        JsonObject addItem = formData.getAsJsonObject("addItem");
        JsonObject removeItem = formData.getAsJsonObject("removeItem");
        JsonArray tables = addItem.getAsJsonArray("tables");
        JsonArray removeTables = removeItem.getAsJsonArray("tables");

        JsonArray joins = GsonUtility.optJsonArray(formData, "joins");
        if (joins != null && !joins.isEmpty()) {
            for (Object joinItem : joins) {
                JsonObject joinIt = (JsonObject) joinItem;
                if (joinIt.has("id"))//check for the manually added joins
                    newJoinsId.add(joinIt.get("id").getAsString());
            }
        }


        for (JsonElement object : removeTables)
            removeTableList.add(object.getAsString());
      /*  if(tables.isEmpty()){
            addList.add("all");
        }else {*/
        for (JsonElement object : tables) {
            addList.add(object.getAsString());
        }
        /*}*/
        JsonArray columns = removeItem.getAsJsonArray("columns");
        for (JsonElement object : columns)
            removeColumnList.add(object.getAsString());
        //removes the tables in removelist
        addList.removeAll(removeTableList);
        
        this.dataSource = formData.getAsJsonObject("dataSource");

        JsonArray removeView = removeItem.getAsJsonArray("views");
        for (int index = 0; index < removeView.size(); index++) {
            JsonObject removeItemView = removeView.get(index).getAsJsonObject();
            JsonArray viewColumns = removeItemView.getAsJsonArray("columns");
            if (viewColumns.size() == 0) {
                removeTableList.add(removeItemView.get("id").getAsString());
            } else {
                //Collection removeItemsCollection = JSONArray.toCollection(viewColumns, String.class);
            	List<String> removeItemsCollection = new ArrayList<>();
            	for (JsonElement element : viewColumns) {
            		removeItemsCollection.add(element.getAsString());
            	}
                removeColumnList.addAll(removeItemsCollection);
                removeViewColumn.addAll(removeItemsCollection);
            }
        }
        super.setFormData(formData);
    }
    /**
     * This method adds all joins from database to list of Relationship.
     * @param database      object provides Relationship of joins.
     */
    @Override
    public void addJoins(Database database) {
        Relationships oldRelationships = database.getRelationships();
        List<Relationship> listOfRelationsList = oldRelationships != null ? oldRelationships.getListOfRelations() : new ArrayList<>();
        super.addJoins(database);
        Relationships newRelationship = database.getRelationships();
        List<Relationship> newListOfRelationsList = newRelationship != null ? newRelationship.getListOfRelations() : new ArrayList<>();
        if (newListOfRelationsList != null && listOfRelationsList != null)
            newListOfRelationsList.addAll(listOfRelationsList);
        

    }
    /**
     * Adds tables and columns into list of tables and it also  checks for duplicate columns.
     * @param database      database object provides list of tables.
     */
    @Override
    public void addTableColumns(Database database) {
    	tableMap = new HashMap<>();
        List<Table> tableList = database.getTables().getTableList();
        if (tableList == null) {
//            super.addTableColumns(database);
            return;
        }
        Iterator<Table> iterator = tableList.iterator();
        List<Table> tempList = new ArrayList<>();
        Map<String, Column> toBeRemoved = new HashMap<>();
        while (iterator.hasNext()) {
            Table next = iterator.next();

            String tableId = next.getId();
          /*  if (addList.contains(tableId)) {
                addList.remove(tableId);
            }*/

            String tableAliasName = this.tableAlias.get(tableId);
            if (tableAliasName != null)
                next.setAliasName(tableAliasName);

            if (duplicateTableMaps.containsKey(tableId)) {
                tempList.add(next);
            }

            if (removeTableList.contains(tableId) || addList.contains(tableId)) {
                iterator.remove();
            } else {
                Columns columns = next.getColumns();

                if ( columns==null || columns.getColumn() == null) {
                    continue;
                }
                List<Column> column = columns.getColumn();
                Iterator<Column> columnIterator = column.iterator();
                List<JsonObject> jsonObjects = this.duplicateTableIdColumns.get(tableId);

                Map<String, Column> allColumns = new HashMap<>();
                while (columnIterator.hasNext()) {
                    Column nextColumn = columnIterator.next();
                    String columnId = nextColumn.getId();

                    String columnAlias = this.columnAlias.get(columnId);
                    if (columnAlias != null) {
                        nextColumn.setAliasName(columnAlias);
                    }
                    if (removeColumnList.contains(columnId)) {
                        toBeRemoved.put(columnId, nextColumn);
                      columnIterator.remove();
                    } else
                        allColumns.put(columnId, nextColumn);
                }

                if (jsonObjects != null) {
                    for (JsonObject dupColumn : jsonObjects) {
                        String originalId = dupColumn.get("originalId").getAsString();
                        Column aColumn = allColumns.getOrDefault(originalId, toBeRemoved.get(originalId));
                        if (aColumn != null) {
                            Column duplicateColumn = ApplicationContextAccessor.getBean(Column.class);

                            duplicateColumn.setName(dupColumn.get("name").getAsString());
                            duplicateColumn.setId(GsonUtility.optStringValue(dupColumn, "id",AdhocUtils.getUuid()));
                            duplicateColumn.setAliasName(dupColumn.get("alias").getAsString());
                            duplicateColumn.setDefaultFunction(aColumn.getDefaultFunction());
                            duplicateColumn.setType(aColumn.getType());
                            duplicateColumn.setOriginalName(aColumn.getOriginalName()==null?aColumn.getName():aColumn.getOriginalName());
                            column.add(duplicateColumn);
                            //break;
                        }
                    }
                }
            }
        }


        for (Table item : tempList) {
            List<JsonObject> jsonObjects = this.duplicateTableMaps.get(item.getId());
            List<Column> column = item.getColumns().getColumn();
            Map<String, Column> mapColIdCol = column.stream().collect(Collectors.toMap(Column::getId, it -> it));
            for (JsonObject colItem : jsonObjects) {
                String originalTableId = colItem.get("originalId").getAsString();
                if (originalTableId.equals(item.getId())) {
                    Table table = ApplicationContextAccessor.getBean(Table.class);
                    table.setAliasName(colItem.get("alias").getAsString());
                    List<Column> col = new ArrayList<>();
                    JsonArray columns = colItem.getAsJsonArray("columns");
                    Columns cols = ApplicationContextAccessor.getBean(Columns.class);
                    for (int index = 0; index < columns.size(); index++) {
                        JsonObject itt = columns.get(index).getAsJsonObject();
                        String oriColId = itt.get("originalId").getAsString();
                        Column aColumn = mapColIdCol.get(oriColId);
                        if (aColumn == null) {
                            aColumn = toBeRemoved.get(oriColId);
                        }
                        Column duplicateColumn = ApplicationContextAccessor.getBean(Column.class);

                        duplicateColumn.setName(itt.get("name").getAsString());
                        duplicateColumn.setId(GsonUtility.optStringValue(itt, "id",AdhocUtils.getUuid()));
                        duplicateColumn.setAliasName(itt.get("alias").getAsString());
                        duplicateColumn.setDefaultFunction(aColumn.getDefaultFunction());
                        duplicateColumn.setType(aColumn.getType());
                        if(!(itt.get("name").getAsString().equals(aColumn.getName()))) {
                        	duplicateColumn.setOriginalName(aColumn.getOriginalName()==null?aColumn.getName():aColumn.getOriginalName());
                        }
//                        duplicateColumn.setOriginalName(aColumn.getOriginalName()==null?aColumn.getName():aColumn.getOriginalName());
                        col.add(duplicateColumn);
                        cols.setColumn(col);
                    }
                    table.setColumns(cols);
                    table.setName(colItem.get("name").getAsString());
                    table.setId(GsonUtility.optStringValue(colItem, "id",AdhocUtils.getUuid()));
                    table.setOriginalName(item.getOriginalName() != null ? item.getOriginalName() : item.getName());
                    tableList.add(table);
                    duplicateTableMaps.remove(item.getId());
                }

            }
        }


        DataSourceMapping dsMapping = new DataSourceMapping();
        dsMapping.setType("partial_column"); 
       List<JsonObject> resultColumns = getDeSerializedResult(dsMapping);
       List<Table> listOfTablesFromCache = getListOfTables(resultColumns);
       if(listOfTablesFromCache != null && !listOfTablesFromCache.isEmpty()) {
    	   listOfTablesFromCache.forEach(item -> { 
    		   tableMap.put(item.getId(), item);
    		   if(!addList.contains(item.getId())) {
    			   addList.add(item.getId());
    		   }
    	   });
    	   
       }
       List<Table> listOfTables = getListOfTable(addList);
        if (listOfTables.size() < addList.size()) {
            throw new MetadataRetrievalException("Could not save metadata. Cache still in progress Please try again later");
        }
        tableList.addAll(listOfTables);


    }
    /**
     * Returns a list of tables based on the provided list of table IDs.
     * 
     * @param addList 		 list of table IDs to retrieve tables for.
     * @return A list of Table objects corresponding to the provided table IDs.
     */
	private List<Table> getListOfTable(List<String> addList) {
		List<Table> tables = new ArrayList<>();
		HIMetadataResourceServiceDB mdServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
		for (String id : addList) {
			Table table = null;
			if( StringUtils.isNumeric(id)) {
				table = metadataServiceDB.findTableById(Integer.valueOf(id));
			}
			else {
				table = tableMap.get(id);
			}
			handleDuplicateColumns(table);
			tables.add(table);
		}
		return tables;
	}
	
	/**
	 * Handles the addition of duplicate columns to the provided table.
	 * 
	 * @param table 		 Table object to which duplicate columns need to be added.
	 */
	private void handleDuplicateColumns(Table table) {
		String tableId = table.getId();
		Columns columns = table.getColumns();
		if (this.duplicateTableIdColumns.containsKey(tableId)) {
            List<Column> columnsList = columns.getColumn();
            Map<String,Column> columnMap = new HashMap<>();
            columnsList.forEach(col -> columnMap.put(col.getId(),col));
            List<JsonObject> jsonObjects = this.duplicateTableIdColumns.get(tableId);
            
            for (JsonObject dupColumn : jsonObjects) {
                String originalId = dupColumn.get("originalId").getAsString();
                for(Map.Entry<String, Column> entrySet : columnMap.entrySet()) {
                	String orgId = entrySet.getKey();
                	Column col = entrySet.getValue();
                	if (orgId.equalsIgnoreCase(originalId)) {
                		Column column = ApplicationContextAccessor.getBean(Column.class);
                        String alias = dupColumn.get("alias").getAsString();
                        String dupOriginalName = GsonUtility.optString(dupColumn, "name");
                        column.setName(dupOriginalName);
                        column.setId(GsonUtility.optStringValue(dupColumn,"id", AdhocUtils.getUuid()));
                        column.setAliasName(alias);
                        column.setDefaultFunction(col.getDefaultFunction());
                        column.setType(col.getType());
                        column.setOriginalName(col.getName());
                        if(!columnsList.contains(column)) {
                        	columnsList.add(column);
                        };
                        break;
                	}
                	
                }
            }
        }
	}
	/**
	 * It collects or adds the join in list before it checks with id's similar or not.
	 * @param join       Join object provides join id
	 * @param joins		 it adds the join.
	 */
	protected void checkBeforeAddingJoin(Join join, List<Join> joins) {
        String newId = join.getId();
        if (newJoinsId.contains(newId)) {
            newJoinsMap.put(newId, join);
            joins.add(join);
        }

    }
	/**
	 * Handles the addition of a table to the provided list of tables if it is included in the addList.
	 * 
	 * @param tableList   	 list of tables to which the new table will be added.
	 * @param tableName   	 name of the table.
	 * @param tableItem   	 JsonObject containing details of the table.
	 */
    protected void handleTableAdd(List<Table> tableList, String tableName, JsonObject tableItem) {
        String tableId = tableItem.get("id").getAsString();
        if (addList.contains(tableId)) {
            super.handleTableAdd(tableList, tableName, tableItem);
        }
    }
    /**
     * Adds the specified table to the provided list of tables if it is not in the removeTableList.
     * 
     * @param tableList   		 list of tables to which the new table will be added.
     * @param tableName   		 name of the table.
     * @param tableItem   		 JsonObject containing details of the table.
     */
    @Override
    public void addOthers(Metadata metadata) {

        Views views = metadata.getDatabase().getViews();
        if (views != null) {
            List<View> viewList = views.getViewList();
            if (viewList != null) {
                for (String viewId : removeTableList) {
                    Iterator<View> iterator = viewList.iterator();
                    while (iterator.hasNext()) {
                        View next = iterator.next();
                        if (next.getId().equalsIgnoreCase(viewId)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        super.addOthers(metadata);
    }

    /**
     * Saves the metadata file to disk and returns a JsonObject response.
     * 
     * @param formJson   		 JsonObject containing form data.
     * @param metadata   		 Metadata object not used .
     * @return JsonObject containing the response message and location details.
     */
    public JsonObject saveFileToDisk(JsonObject formJson, Metadata metadata) {
        JsonObject response = new JsonObject();
        String dir;
        String mode="edit";
        if (this.formData.has("newLocation")) {
            String fileName = this.formData.get("fileName").getAsString();
            fileName = DBProcessor.checkAndReplaceSpecialChars(fileName).trim();
            String newMd = fileName + "." + JsonUtils.getMetadataExtension();
            this.fileLocation = this.newFileLocation + this.formData.get("newLocation").getAsString() + File.separator + newMd;
            response.addProperty("location", this.formData.get("newLocation").getAsString());
            dir=this.formData.get("newLocation").getAsString();
            response.addProperty("uuid", newMd);
            fileNameWithExtension=newMd;
            mode="saveas";
        } else {
            response.addProperty("uuid", this.formData.get("uuid").getAsString());
            response.addProperty("location", this.formData.get("location").getAsString());
            dir= this.formData.get("location").getAsString();
        }
        MetadataDBUtility metadataDBUtility = new MetadataDBUtility();
        AuditContext context = new AuditContext();
        metadataDBUtility.setFormJson(formJson);
        metadataDBUtility.setContext(context);
        try {
            String uuid = metadataDBUtility.saveMetadataToDB(this.metadata, dir, fileNameWithExtension, mode);
            response.addProperty("uuid",uuid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        response.addProperty("message", "Successfully saved metadata file");

        return response;
    }
    /**
     * Adds the specified column to the list of columns if it is not in the removeColumnList.
     * 
     * @param columnsList   		 list of columns to which the new column will be added.
     * @param columnName    		 name of the column.
     * @param columnItem    		 JsonObject containing details of the column.
     */
    @Override
    protected void addToColumnList(List<Column> columnsList, String columnName, JsonObject columnItem) {
        String columnId = columnItem.get("id").getAsString();
        if (!removeColumnList.contains(columnId)) {
            super.addToColumnList(columnsList, columnName, columnItem);
        }
    }

    /**
     * Retrieves all data for the provided DataSourceMapping.
     * 
     * @param dsMapping   		 DataSourceMapping object for which data needs to be retrieved.
     * @return List of JsonObject containing all the data.
     */
    protected List<JsonObject> getAllData(DataSourceMapping dsMapping) {

        if (addList.isEmpty() && dsMapping.getType().equals("partial_column")) {
            return new ArrayList<>();
        }
        return getDataForSubClasses(dsMapping, addList);

    }


}
