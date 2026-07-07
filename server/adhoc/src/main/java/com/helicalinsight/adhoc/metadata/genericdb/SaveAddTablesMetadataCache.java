package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.admin.model.DataSourceMapping;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helical021 on 4/9/2019.
 */
/**
 * The SaveAddTablesMetadataCache class extends the {@link SaveAllColumnsJoinsMetadataCache} class
 * and is responsible for caching metadata when adding tables.
 * 
 */
@Component
@Scope("prototype")
public class SaveAddTablesMetadataCache extends SaveAllColumnsJoinsMetadataCache {
    protected List<String> tableNameList = new ArrayList<>();
    /**
     * Sets the form data for adding tables.
     * @param formData 		 JsonObject containing tables to add.
     */
    @Override
    public void setFormData(JsonObject formData) {
        JsonObject addItem = formData.getAsJsonObject("addItem");
        JsonArray tables = addItem.getAsJsonArray("tables");
        for (JsonElement object : tables)
            addList.add(object.getAsString());
            super.setFormData(formData);
    }
    /**
     * Handles the addition of tables.
     * @param tableList 			 list of tables.
     * @param tableName 			 name of the table to be added.
     * @param tableItem 			 JsonObject representing the table item.
     */
    protected void handleTableAdd(List<Table> tableList, String tableName, JsonObject tableItem) {
        String tableId = tableItem.get("id").getAsString();
        if (addList.contains(tableId)) {
            super.handleTableAdd(tableList, tableName, tableItem);
            this.tableNameList.add(tableName);
        }
    }
    /**
     * Checks before adding a join.
     * @param join 			 join provides leftTable and RightTable.
     * @param joins 		 list of existing joins.
     */
    protected void checkBeforeAddingJoin(Join join, List<Join> joins) {
        String leftTableName = join.getLeftTable().getTable();
        String rightTableName = join.getRightTable().getTable();
        if (tableNameList.contains(leftTableName) && tableNameList.contains(rightTableName)) {
            super.checkBeforeAddingJoin(join, joins);
        }
    }
    /**
     * Retrieves all data from the data source mapping.
     * @param dsMapping 			 DataSourceMapping object.
     * @return The list of JsonObject containing all data.
     */
    protected List<JsonObject> getAllData(DataSourceMapping dsMapping) {
        /*
        //Commenting as of now will open it shortly
        List<JSONObject> allData1 = super.getAllData(dsMapping);
        if(allData1!=null && allData1.size()>0){
            return allData1;
        }*/
        return getDataForSubClasses(dsMapping, addList);

    }


}
