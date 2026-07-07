package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.admin.model.DataSourceMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * The SaveRemoveTableMetadataCache class manages the caching of metadata related to tables and columns,
 * specifically focusing on the addition and removal of tables and columns.
 * It extends the {@link SaveAllColumnsJoinsMetadataCache} class and provides additional functionality
 * for handling table and column removal and addition.
 * 
 * @author Somen
 */

@Component
@Scope("prototype")
public class SaveRemoveTableMetadataCache extends SaveAllColumnsJoinsMetadataCache {
    protected List<String> removeTableList = new ArrayList<>();
    protected List<String> removeColumnList = new ArrayList<>();

    protected List<String> tableNameList = new ArrayList<>();

    /**
     * Sets the form data by extracting information about tables and columns to be removed or added.
     * This method processes the formData JsonObject to update the removeTableList, removeColumnList,
     * and tableNameList based on the provided information.
     * @param formData 			 JsonObject containing tables, column, views.
     */
    @Override
    public void setFormData(JsonObject formData) {
        JsonObject removeItem = formData.getAsJsonObject("removeItem");
        JsonObject addItem = formData.getAsJsonObject("addItem");

        JsonArray tables = removeItem.getAsJsonArray("tables");
        JsonArray removeView = removeItem.getAsJsonArray("views");
        JsonArray addTables = addItem.getAsJsonArray("tables");
        for (Object object : tables)
            removeTableList.add(object.toString());
        for (Object object : addTables)
            addList.add(object.toString());
        JsonArray columns = removeItem.getAsJsonArray("columns");
        for (Object object : columns)
            removeColumnList.add(object.toString());

        for (int index = 0; index < removeView.size(); index++) {
            JsonObject removeItemView = removeView.get(index).getAsJsonObject();
            JsonArray viewColumns = removeItemView.getAsJsonArray("columns");
            List<String> collectionList = new ArrayList<>();
            for (JsonElement element : viewColumns) {
                collectionList.add(element.getAsString());
            }
            removeViewColumn.addAll(collectionList);
        }

        super.setFormData(formData);
    }
    /**
     * Handles the addition of tables to the metadata cache.
     * If a table is not in the remove list and either the add list is empty
     * or the table is in the add list, it adds the table to the cache.
     * @param tableList 			 list of tables in the metadata cache.
     * @param tableName 			 name of the table to be added.
     * @param tableItem 			 JsonObject containing information about the table.
     */
    @Override
    protected void handleTableAdd(List<Table> tableList, String tableName, JsonObject tableItem) {
        String tableId = tableItem.get("id").toString();
        if (!removeTableList.contains(tableId)) {
            if (addList.isEmpty()) {
                super.handleTableAdd(tableList, tableName, tableItem);
                this.tableNameList.add(tableName);
            } else if (this.addList.contains(tableId)) {
                super.handleTableAdd(tableList, tableName, tableItem);
                this.tableNameList.add(tableName);
            }
        }
    }
    /**
     * Checks before adding a join to the metadata cache.
     * It verifies if both tables involved in a join operation are in the tableNameList,
     * and if so, it adds the join to the cache.
     * @param join 				 Join object to be added to the cache.
     * @param joins 			 list of existing joins in the metadata cache.
     */
    @Override
    protected void checkBeforeAddingJoin(Join join, List<Join> joins) {
        String leftTableName = join.getLeftTable().getTable();
        String rightTableName = join.getRightTable().getTable();
        if (tableNameList.contains(leftTableName) && tableNameList.contains(rightTableName)) {
            super.checkBeforeAddingJoin(join, joins);
        }
    }
    /**
     * Adds columns to the metadata cache if they are not in the remove list.
     * @param columnsList 			 list of columns in the metadata cache.
     * @param columnName 			 name of the column to be added.
     * @param columnItem 			 JsonObject containing information about the column.
     */
    @Override
    protected void addToColumnList(List<Column> columnsList, String columnName, JsonObject columnItem) {
        String columnId = columnItem.get("id").toString();
        if (!removeColumnList.contains(columnId)) {
            super.addToColumnList(columnsList, columnName, columnItem);
        }
    }
    /**
     * Prepares data for subclasses, particularly focusing on retrieving data
     * if a UUID is not present or metadata reload is specified.
     * If metadata reload is required, it adds all tables and those from the remove list to the add list,
     * and then retrieves data for subclasses.
     * @param dsMapping 				 DataSourceMapping object.
     * @param addList 					 list of tables to be added.
     * @return The list of JsonObject containing data for subclasses.
     */
    @Override
    protected List<JsonObject> getDataForSubClasses(DataSourceMapping dsMapping, List<String> addList) {
        if(!formData.has("uuid")|| (formData.has("metadataReload") && formData.get("metadataReload").getAsBoolean()) ){
            this.addList.add("all");
            this.addList.addAll(this.removeTableList);
            return super.getDataForSubClasses(dsMapping,this.addList);
        }
        return new ArrayList<>();
    }
}
