package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ForeignKey;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * The ForeignKeyTemplate class is responsible for parsing JSON data containing foreign key information
 * and converting it into a list of ForeignKey objects.
 *
 * Created by author on 27-06-2015.
 * @author Rajasekhar
 */
@Component
public class ForeignKeyTemplate {
	/**
     * Retrieves a list of ForeignKey objects from the given JSON object containing foreign key information.
     *
     * @param foreignKeyInfo 		 JSON object containing foreign key information.
     * @return A list of ForeignKey objects parsed from the JSON.
     */
    @NotNull
    public List<ForeignKey> getForeignKeyList(@NotNull JsonObject foreignKeyInfo) {
        List<ForeignKey> foreignKeyList = new ArrayList<>();
        try {
            JsonArray foreignKeys = foreignKeyInfo.getAsJsonArray("foreignKeys");
            for (JsonElement object : foreignKeys) {
                JsonObject foreignKey = object.getAsJsonObject();
                addForeignKey(foreignKeyList, foreignKey);
            }
        } catch (Exception ex) {
            JsonObject foreignKey = foreignKeyInfo.getAsJsonObject("foreignKeys");
            addForeignKey(foreignKeyList, foreignKey);
        }
        return foreignKeyList;
    }
    /**
     * Adds a ForeignKey object to the list based on the provided JSON object.
     *
     * @param foreignKeyList 		 list of ForeignKey objects.
     * @param foreignKey     		 JSON object containing foreign key details.
     */
    private void addForeignKey(@NotNull List<ForeignKey> foreignKeyList, @NotNull JsonObject foreignKey) {
        ForeignKey actualForeignKey = ApplicationContextAccessor.getBean(ForeignKey.class);
        String name;
        String referenceColumn;
        String referenceTable;
        try {
            name = foreignKey.get("name").getAsString();
            referenceColumn = foreignKey.get("referenceColumn").getAsString();
            referenceTable = foreignKey.get("referenceTable").getAsString();
        } catch (Exception ignore) {
            foreignKeyList.add(null);
            return;
        }
        actualForeignKey.setName(name);
        actualForeignKey.setReferenceColumn(referenceColumn);
        actualForeignKey.setReferenceTable(referenceTable);
        foreignKeyList.add(actualForeignKey);
    }
}
