package com.helicalinsight.adhoc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.WorkflowDatabaseMetadataProvider;
import com.helicalinsight.adhoc.metadata.genericdb.DuplicateColumnException;
import com.helicalinsight.adhoc.metadata.genericdb.TableNameExistsException;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


import java.io.File;
import java.util.*;

/**
 * The {@code AddRemoveTableColumnsHandler} class provides functionality to handle the addition and removal of table columns
 * (metadata manipulation).
 * 
 * @author Somen
 * @since 7/4/2018
 */
public class AddRemoveTableColumnsHandler {

    /*
    * Other code has to be refactored and this code must be used
    * */
	/**
     * it fetches the metadata file path based on the provided form data.
     *
     * @param formData 	 object containing information about location, uniqueId , filename for metadata.
     * @return metadata file path to update , if formData is empty or null returns {@code null}.
     */
    public static String getMetadataToUpdate(JsonObject formData) {
        String tempDirectory = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        String metadataExtension = "." + JsonUtils.getMetadataExtension();
        if (formData.has("location")) {
            String location = formData.get("location").getAsString();
            String metadataFileName = formData.get("metadataFileName").getAsString();
            metadataExtension = metadataFileName.endsWith(metadataExtension) ? "" : metadataExtension;
            String tempFile = tempDirectory + File.separator + location + "_temp_" + metadataFileName + metadataExtension;
            File testFile = new File(tempFile);
            if (!testFile.exists()) {
                return solutionDirectory + File.separator + location + File.separator + metadataFileName + metadataExtension;
            }

            return tempFile;
        } else if (formData.has("uniqueId")) {

            return formData.get("uniqueId").getAsString();

        } else if (formData.has("metadata") && formData.getAsJsonObject("metadata").has("uniqueId")) {
            return formData.getAsJsonObject("metadata").get("uniqueId").getAsString();
        } else {
            return null;
        }

    }
    /**
     * Handles the addition or removal of table columns based on the provided form data.
     * Updates the metadata file accordingly and returns the response as a JSON string.
     *
     * @param formData 		  object containing information about location, uniqueId , filename for metadata.
     * @return JSON string representing the response.
     */
    public static String handleAddRemove(JsonObject formData) {

        String tempDirectory = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
        String absolutePath = tempDirectory;
        String metadataExtension = "." + JsonUtils.getMetadataExtension();
        String unMarshallLocation;
        String marshallLocation;
        String actualUniqueId = null;
        File metadataFile;
        if (formData.has("location")) {
            String location = formData.get("location").getAsString();
            File tempFolder = new File(absolutePath + File.separator + location);
            absolutePath = ApplicationProperties.getInstance().getSolutionDirectory();
            unMarshallLocation = location + File.separator + formData.get("metadataFileName").getAsString();
            marshallLocation = location + "_temp_" + formData.get("metadataFileName").getAsString();
            actualUniqueId = marshallLocation;
            metadataExtension = unMarshallLocation.endsWith(metadataExtension) ? "" : metadataExtension;

        } else if (formData.has("uniqueId")) {

            unMarshallLocation = formData.get("uniqueId").getAsString();
            marshallLocation = actualUniqueId = unMarshallLocation;
        } else if (formData.has("metadata") && formData.getAsJsonObject("metadata").has("uniqueId")) {
            unMarshallLocation = formData.getAsJsonObject("metadata").get("uniqueId").getAsString();
            actualUniqueId = marshallLocation = unMarshallLocation;
        } else {
            return null;
        }

        if (formData.has("metadata") && formData.getAsJsonObject("metadata").has("uniqueId")) {
            actualUniqueId = formData.getAsJsonObject("metadata").get("uniqueId").getAsString();
        }

        File marshalFile = new File(tempDirectory + File.separator + marshallLocation + metadataExtension);
        if (!marshalFile.exists()) {
            metadataFile = new File(absolutePath + File.separator +
                    unMarshallLocation + metadataExtension);
        } else {
            metadataFile = marshalFile;
        }


        Metadata metadata = JaxbUtils.unMarshal(Metadata.class, metadataFile);


        handleAddRemoveTableColumns(formData, metadata);

        if (marshalFile.getAbsolutePath().contains("_temp_")) {
            marshalFile.getParentFile().mkdirs();
        }
        JaxbUtils.marshal(metadata, marshalFile);

        JsonObject responseJson = WorkflowDatabaseMetadataProvider.prepareResponse(formData, metadata);
        GsonUtility.accumulate( responseJson,"uniqueId", actualUniqueId);
        return responseJson.toString();
    }

    /**
     * Handles the addition or removal of table columns in the metadata based on the provided form data.
     *
     * @param formData 		 object containing metadata info it includes tables, schemas, columns etc.
     * @param metadata 		 metadata to be modified.
     */
    public static void handleAddRemoveTableColumns(JsonObject formData, Metadata metadata) {
        JsonObject metadataJson = formData.getAsJsonObject("metadata");

        JsonArray catalogsArray;
		JsonArray schemaArray;
		JsonArray allTableArray;
        List<JsonObject> newTableArray = new ArrayList<>();
        Map<String, JsonArray> tableIdColMap = new HashMap<>();
        if (metadataJson.size() != 0) {
            catalogsArray = metadataJson.getAsJsonArray("catalogs");

            if (!catalogsArray.isEmpty()) {
                schemaArray = catalogsArray.get(0).getAsJsonObject().getAsJsonArray("schemas");


                if (!schemaArray.isEmpty()) {
                    allTableArray = schemaArray.get(0).getAsJsonObject().getAsJsonArray("tables");

                    for (Object object : allTableArray) {
                        JsonObject itemTable = (JsonObject) object;
                        if (!itemTable.has("id")) {
                            newTableArray.add(itemTable);
                        } else {
                            String tableId = itemTable.get("id").getAsString();
                            JsonArray columnsNew = itemTable.getAsJsonArray("columns");
                            for (Object objCol : columnsNew) {
                                JsonObject colObject = (JsonObject) (objCol);
                                if (!colObject.has("id")) {
                                    JsonArray jsonObjects = tableIdColMap.get(tableId);
                                    if (jsonObjects == null) {
                                        jsonObjects = new JsonArray();
                                    }
                                    jsonObjects.add(colObject);
                                    tableIdColMap.put(tableId, jsonObjects);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (formData.has("removeItem")) {

            JsonObject removeItem = formData.getAsJsonObject("removeItem");

            handlRemoveItems(metadata, removeItem);


        }

        if (!newTableArray.isEmpty()) {
        	newHandleAddNewTable(metadata, newTableArray);
            //logic to add new tables
        }

        if (!tableIdColMap.isEmpty()) {
            List<Table> tableList = metadata.getDatabase().getTables().getTableList();
            for (Table table : tableList) {
                if (tableIdColMap.containsKey(table.getId())) {
                    JsonArray jsonObjects = tableIdColMap.get(table.getId());
                    setColumns(jsonObjects, table);
                }
            }
            //logic to add new column
        }
    }

    /**
     * Handles the addition of new tables in the metadata based on the provided form data.
     *
     * @param metadata       		 		metadata provides tables and column information.
     * @param newTableArray  		 		list of new table JSON objects.
     * @throws TableNameExistsException 	If a table with the same name already exists.
     */
    public static void newHandleAddNewTable(Metadata metadata, List<JsonObject> newTableArray) {
        Tables tables = metadata.getDatabase().getTables();
        List<Table> tableList = tables.getTableList();
        if (tableList == null) {
            tableList = new ArrayList<>();
        }
        List<String> existingTableList = new ArrayList<>();
        for (Table table : tableList) {
            existingTableList.add(table.getName());
        }
        for (JsonObject item : newTableArray) {

            String tableName = item.get("name").getAsString();
            int index = existingTableList.indexOf(tableName);
            if (existingTableList.contains(tableName)) {
                Table table = tableList.get(index);
                //continue;
                throw new TableNameExistsException("The table " + tableName + " already exists. Please remove the table " + table.getAliasName());
            }
            Table newTable = ApplicationContextAccessor.getBean(Table.class);
            String tableAlias = item.has("alias") ? item.get("alias").getAsString() : tableName;
            newTable.setAliasName(tableAlias);
            newTable.setId(AdhocUtils.getUuid());
            JsonArray columns = item.getAsJsonArray("columns");
            //get all the list of columns
            setColumns(columns, newTable);
            //newTable.setForeignKeys();
            newTable.setName(tableName);
            //newTable.setPrimaryKeys();

            tableList.add(newTable);
        }
        tables.setTableList(tableList);

    }



    /**
     * Handles the removal of items (tables, columns, views) from the metadata based on the provided form data.
     *
     * @param metadata   		 metadata provides info about database and views..
     * @param removeItem 		 JSON object containing items to be removed.
     */
    public static void handlRemoveItems(Metadata metadata, JsonObject removeItem) {
        boolean removeColumn = removeItem.has("columns");
        boolean removeTables = removeItem.has("tables");
        boolean removeViews = removeItem.has("views");
        if (removeTables || removeColumn || removeViews) {
            JsonArray tableArray = removeTables ? removeItem.getAsJsonArray("tables") : null;
            JsonArray columnsArray = removeColumn ? removeItem.getAsJsonArray("columns") : null;
            JsonArray viewArray = removeViews ? removeItem.getAsJsonArray("views") : null;
            Database database = metadata.getDatabase();
            if (viewArray != null && !viewArray.isEmpty()) {
                Views views = database.getViews();
                if(views!=null) {
                    List<View> viewList = views.getViewList();
                    for (int i = 0; i < viewArray.size(); i++) {
                        JsonObject jsonObject = viewArray.get(i).getAsJsonObject();
                        if (jsonObject.has("columns") && !jsonObject.getAsJsonArray("columns").isEmpty()) {
                            JsonArray colViewArray = jsonObject.getAsJsonArray("columns");
                            if (columnsArray == null) {
                                columnsArray = new JsonArray();
                            }
                            columnsArray.addAll(colViewArray);
                        } else {
                            if (tableArray == null) {
                                tableArray = new JsonArray();
                            }
                            String viewId = jsonObject.get("id").getAsString();
                            tableArray.add(viewId);
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
            }
            Tables tables = database.getTables();
            List<Table> tableList = tables != null ? tables.getTableList() : new ArrayList<Table>();
            if (tableList == null) {
                //nothing to remove
                return;
            }
            Iterator<Table> tableIterator = tableList.iterator();

            while (tableIterator.hasNext()) {
                Table nextTable = tableIterator.next();
                if (removeTables && tableArray.contains(new JsonPrimitive(nextTable.getId()))) {
                    tableIterator.remove();
                    continue;
                }


                if (removeColumn) {

                    List<Column> column = nextTable.getColumns().getColumn();
                    if(column != null) {
                        Iterator<Column> columnIterator = column.iterator();
                        while (columnIterator.hasNext()) {
                            Column nextColumn = columnIterator.next();
                            if (columnsArray.contains(new JsonPrimitive(nextColumn.getId()))) {
                                columnIterator.remove();
                            }
                        }
                    }
                }

            }
        }
    }
    /**
     * Sets the columns of a table based on the provided JSON array in the metadata.
     *
     * @param columns  		 				JSON array containing column details(name, type).
     * @param newTable 		 				table to which columns will be added.
     * @throws DuplicateColumnException 	If a column with the same name already exists in the table.
     */
    private static void setColumns(JsonArray columns, Table newTable) {
        final File file = JsonUtils.defaultFunctionsFile();
        final Properties properties = ConfigurationFileReader.getPropertiesFromFile(file);
        Columns tableColumns = newTable.getColumns();
        List<Column> columnList;
        if (tableColumns == null) {
            tableColumns = ApplicationContextAccessor.getBean(Columns.class);

        }
        columnList = tableColumns.getColumn();
        List<String> existingColumnList = new ArrayList<>();
        if (columnList == null) {
            columnList = new ArrayList<>();
        }
        for (Column column : columnList) {
            existingColumnList.add(column.getName());
        }


        for (Object obj : columns) {
            JsonObject item = (JsonObject) obj;

            String name = item.get("name").getAsString();
            if (existingColumnList.contains(name)) {
                throw new DuplicateColumnException("The column name " + name + " already exists in this table");
            }
            String type = item.get("type").getAsString().split(":")[0].replace("{\"", "").replace("\"", "");
            Column columnItem = ApplicationContextAccessor.getBean(Column.class);
            columnItem.setId(AdhocUtils.getUuid());
            columnItem.setType(type);//Also check if columnJson has a different one
            columnItem.setName(name);
            String property = properties.getProperty(type);
            if (property != null) {
                columnItem.setDefaultFunction(property);
            }
            columnItem.setAliasName(name);

            columnList.add(columnItem);

        }

        tableColumns.setColumn(columnList);
        newTable.setColumns(tableColumns);
    }

}
