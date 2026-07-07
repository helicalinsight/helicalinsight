package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.genericsql.AliasNameExistsException;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import java.util.*;

/**
 * Utility class for handling duplicate items such as columns and tables in metadata.
 * This class provides methods to handle duplicate columns and tables during metadata updates.
 *
 * @author Somen
 * @since 29-06-2018
 */
@SuppressWarnings("unused")
public class DuplicateItemHandler {
	/**
     * Handles the duplication of columns in metadata.
     *
     * @param metadata              			 metadata object which which provides columns details
     * @param duplicateColumnsArray 			 JSON array containing information about duplicate columns
     * @throws DuplicateColumnException if a duplicate column is found
     */
    public static void duplicateColumns(Metadata metadata, JsonArray duplicateColumnsArray) {


        if (duplicateColumnsArray == null) {
            return;
        }
        Map<String, Table> tableMap = prepareMetadataTableColumnIdMap(metadata);
        for (Object item : duplicateColumnsArray) {
            JsonObject duplicateColumn = (JsonObject) (item);
            String alias = duplicateColumn.get("alias").getAsString();


            String tableId = duplicateColumn.get("tableId").getAsString();
            String originalColumnId = duplicateColumn.get("originalId").getAsString();

            if (tableMap.containsKey(tableId)) {

                Table existingTable = tableMap.get(tableId);
                List<Column> columnList = existingTable.getColumns().getColumn();
                List<String> columnNameList = prepareColumnNameList(columnList);
                Iterator<Column> iterator = columnList.iterator();

                Column newColumn = null;

                while (iterator.hasNext()) {
                    Column existingColumn = iterator.next();

                    if (existingColumn.getId().equals(originalColumnId)) {
                        newColumn = ApplicationContextAccessor.getBean(Column.class);
                        String name = duplicateColumn.get("alias").getAsString();
                        if (columnNameList.contains(name)) {
                            throw new DuplicateColumnException("The column name " + name + " already exists in this table");
                        }
                        newColumn.setId(AdhocUtils.getUuid());
                        newColumn.setType(duplicateColumn.get("type").getAsString().split(":")[0].replace("{\"", "").replace("\"", ""));//Also check if columnJson has a different one
                        newColumn.setName(duplicateColumn.get("originalName").getAsString());
                        newColumn.setDefaultFunction(duplicateColumn.get("defaultFunction").getAsString());
                        newColumn.setAliasName(duplicateColumn.get("alias").getAsString());
                        newColumn.setOriginalName(existingColumn.getOriginalName() == null ? existingColumn.getName() : existingColumn.getOriginalName());

                        break;
                    }
                }

                if (newColumn != null) {
                    columnList.add(newColumn);
                    columnNameList.add(newColumn.getName());
                }

            }

        }
    }
    /**
     * Prepares a list of column names from the provided list of columns.
     *
     * @param columnList 			 list of columns
     * @return a list of column names
     */
    private static List<String> prepareColumnNameList(List<Column> columnList) {
        List<String> columnNameList = new ArrayList<String>();
        for (Column item : columnList) {
            columnNameList.add(item.getName());
        }
        return columnNameList;

    }

    /**
     * Handles the duplication of tables in metadata.
     *
     * @param metadata              	 metadata object to which the tables belong
     * @param duplicateTablesArray  	 JSON array containing information about duplicate tables
     * @throws TableNameExistsException if a duplicate table name is found
     * @throws AliasNameExistsException if a duplicate table alias name is found
     */
    public static void duplicateTable(Metadata metadata, JsonArray duplicateTablesArray) {

        if (duplicateTablesArray == null) {
            return;
        }
        Map<String, Table> tableMap = prepareMetadataTableColumnIdMap(metadata);
        List<String> tableNameList = prepareMetadataNameList(metadata);

        for (Object item : duplicateTablesArray) {
            JsonObject duplicateTable = (JsonObject) (item);
            String alias = duplicateTable.get("originalName").getAsString();
            if (tableNameList.contains(alias)) {
                throw new TableNameExistsException("The table  name " + alias + " already exists ");
            }

            tableNameList.add(alias);

            String tableId = duplicateTable.get("originalId").getAsString();
            JsonObject columnsJson = duplicateTable.getAsJsonObject("columns");

            if (tableMap.containsKey(tableId)) {
                Table newTable = ApplicationContextAccessor.getBean(Table.class);
                Table existingTable = tableMap.get(tableId);

                if (existingTable.getAliasName().equals(alias)) {
                    throw new AliasNameExistsException("The alias name for actual and existing cannot be same");
                }
                newTable.setAliasName(duplicateTable.get("alias").getAsString());
                newTable.setId(AdhocUtils.getUuid());

                //get all the list of columns
                setColumns(columnsJson, newTable, existingTable, tableMap);
                //newTable.setForeignKeys();
                newTable.setOriginalName(existingTable.getOriginalName() == null ? existingTable.getName() : existingTable.getOriginalName());
                newTable.setName(alias);
                newTable.setType(existingTable.getType());
                //newTable.setPrimaryKeys();
                List<Table> existingTableList = metadata.getDatabase().getTables().getTableList();
                existingTableList.add(newTable);
            }

        }

    }
    /**
     * Sets the columns for the new table based on the provided JSON object.
     *
     * @param columnsJson   		 JSON object containing column information
     * @param newTable      		 new table object
     * @param existingTable 		 existing table object
     * @param tableMap				 a map of table IDs to table objects
     */
    private static void setColumns(JsonObject columnsJson, Table newTable, Table existingTable, Map<String, Table> tableMap) {
        List<Column> column = existingTable.getColumns().getColumn();
        List<Column> newColumn = new ArrayList<>();
        Columns columns = ApplicationContextAccessor.getBean(Columns.class);

        for (Column existingColumnItem : column) {
            String existingColumnName = existingColumnItem.getName();
            if (columnsJson.has(existingColumnName)) {
                JsonObject columnJson = columnsJson.getAsJsonObject(existingColumnName);
                Column columnItem = ApplicationContextAccessor.getBean(Column.class);
                columnItem.setId(AdhocUtils.getUuid());
                columnItem.setType(existingColumnItem.getType());//Also check if columnJson has a different one
                columnItem.setName(existingColumnItem.getName());
                columnItem.setDefaultFunction(existingColumnItem.getDefaultFunction());
                columnItem.setAliasName(columnJson.get("alias").getAsString());
                columnItem.setOriginalName(existingColumnItem.getOriginalName());
                columnsJson.remove(existingColumnName);
                newColumn.add(columnItem);
            }
        }


        if (!columnsJson.entrySet().isEmpty()) {
            Iterator keys =  columnsJson.keySet().iterator();
            while (keys.hasNext()) {
                String next = (String) keys.next();
                JsonObject columnItemJson = columnsJson.getAsJsonObject(next);
                Column columnItem = ApplicationContextAccessor.getBean(Column.class);
                columnItem.setId(AdhocUtils.getUuid());
                String tableId = columnItemJson.get("tableId").getAsString();
                String columnId = columnItemJson.get("originalId").getAsString();
                Table table = tableMap.get(tableId);
                List<Column> columnList = table.getColumns().getColumn();
                for (Column col : columnList) {
                    if (col.getId().equals(columnId)) {
                        columnItem.setType(col.getType());
                        columnItem.setName(columnItemJson.get("originalName").getAsString());
                        columnItem.setDefaultFunction(columnItemJson.get("defaultFunction").getAsString());
                        columnItem.setAliasName(columnItemJson.get("alias").getAsString());
                        columnItem.setOriginalName(col.getOriginalName() == null ? col.getName() : col.getOriginalName());
                        newColumn.add(columnItem);
                        break;
                    }
                }

            }

        }
        columns.setColumn(newColumn);
        newTable.setColumns(columns);

    }
    /**
     * Prepares a map of table IDs to table objects from the provided metadata.
     *
     * @param metadata 		 metadata object
     * @return a map of table IDs to table objects
     */
    private static Map<String, Table> prepareMetadataTableColumnIdMap(Metadata metadata) {
        Map<String, Table> mapTableIdTables = new HashMap<>();

        Tables tables = metadata.getDatabase().getTables();
        List<Table> tableList = tables.getTableList();
        if(tableList==null){
            return mapTableIdTables;
        }
        for (Table table : tableList) {
            mapTableIdTables.put(table.getId(), table);
        }
        return mapTableIdTables;


    }
    /**
     * Prepares a list of table names from the provided metadata.
     *
     * @param metadata 		 metadata object
     * @return a list of table names
     */
    private static List<String> prepareMetadataNameList(Metadata metadata) {
        List<String> aliasNameList = new ArrayList<>();

        List<Table> tableList = metadata.getDatabase().getTables().getTableList();
        if(tableList==null){
            return aliasNameList;
        }
        for (Table table : tableList) {
            aliasNameList.add(table.getName());
        }
        return aliasNameList;


    }
}