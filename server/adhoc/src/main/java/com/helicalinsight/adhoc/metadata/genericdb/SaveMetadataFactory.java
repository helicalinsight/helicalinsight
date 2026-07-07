package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * The SaveMetadataFactory class provides a factory method to obtain the appropriate implementation
 * It determines the type of operation (add, remove, edit) and returns the corresponding implementation.
 * @author Somen
 */
public class SaveMetadataFactory {
	/**
     * Returns the appropriate implementation of the ISaveMetadataCache interface
     * based on the provided form data.
     * @param formData 			 JsonObject containing metadata information, tables, columns, views.
     * @return An instance of the ISaveMetadataCache interface.
     */
    public static ISaveMetadataCache getSaveClass(JsonObject formData) {
        JsonObject addItem = formData.getAsJsonObject("addItem");
        JsonObject removeItem = formData.getAsJsonObject("removeItem");
        JsonArray removeTables = removeItem.getAsJsonArray("tables");
        JsonArray removeColumns = removeItem.getAsJsonArray("columns");
        JsonArray removeViews = removeItem.getAsJsonArray("views");
        JsonArray addTables = addItem.getAsJsonArray("tables");
        JsonArray addColumns = addItem.getAsJsonArray("columns");
        JsonArray addViews = addItem.getAsJsonArray("views");
        boolean addTableEmpty = addTables.size() == 0 && addColumns.size() == 0 && addViews.size() == 0;
        boolean removeTableEmpty = removeTables.size() == 0 && removeColumns.size() == 0 && removeViews.size() == 0;
        boolean caseEdit = formData.has("uuid");
        ISaveMetadataCache iSaveMetadataCache = null;

         if (caseEdit) {
             if(formData.has("metadataReload") && formData.get("metadataReload").getAsBoolean()){
                 if (addTableEmpty && removeTableEmpty) {
                     iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveAllColumnsJoinsMetadataCache");
                 } else if (!addTableEmpty && removeTableEmpty) {
                     iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveAddTablesMetadataCache");
                 } else if (addTableEmpty && !removeTableEmpty) {
                     iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveRemoveTableMetadataCache");
                 }else if (!addTableEmpty && !removeTableEmpty) {
                     iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveRemoveTableMetadataCache");
                 }
             }else
                 iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("editMetadataSaveHandler");

        } else if (addTableEmpty && removeTableEmpty) {
            iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveAllColumnsJoinsMetadataCache");
        } else if (!addTableEmpty && removeTableEmpty) {
            iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveAddTablesMetadataCache");
        } else if (addTableEmpty && !removeTableEmpty) {
            iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveRemoveTableMetadataCache");
        } else if (!addTableEmpty && !removeTableEmpty) {
            iSaveMetadataCache = (ISaveMetadataCache) ApplicationContextAccessor.getBean("saveRemoveTableMetadataCache");
        }

        return iSaveMetadataCache;

    }
}
