package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.PrimaryKey;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * Utility class for handling primary key templates.
 *
 * Created by author on 27-06-2015.
 * @author Rajasekhar
 */
@Component
public class PrimaryKeyTemplate {
	/**
     * Retrieves a list of primary keys from the provided JSON object.
     *
     * @param primaryKeyInfo 			 JSON object containing primary key information
     * @return A list of PrimaryKey objects
     */
    @NotNull
    public List<PrimaryKey> getPrimaryKeyList(@NotNull JsonObject primaryKeyInfo) {
        List<PrimaryKey> primaryKeyList = new ArrayList<>();
        try {
            JsonArray primaryKeys = primaryKeyInfo.getAsJsonArray("primaryKeys");
            for (JsonElement object : primaryKeys) {
                JsonObject primaryKey = object.getAsJsonObject();
                addPrimaryKey(primaryKeyList, primaryKey);
            }
        } catch (Exception e) {
            JsonObject primaryKey = primaryKeyInfo.getAsJsonObject("primaryKeys");
            addPrimaryKey(primaryKeyList, primaryKey);
        }
        return primaryKeyList;
    }
    /**
     * Adds a primary key to the provided list based on the given JSON object.
     *
     * @param primaryKeyList 		 list of PrimaryKey objects
     * @param primaryKey     		 JSON object provides name 
     */
    private void addPrimaryKey(@NotNull List<PrimaryKey> primaryKeyList, @NotNull JsonObject primaryKey) {
        String name;
        try {
            name = primaryKey.get("name").getAsString();
        } catch (Exception ignore) {
            primaryKeyList.add(null);
            return;
        }
        PrimaryKey actualPrimaryKey = ApplicationContextAccessor.getBean(PrimaryKey.class);
        actualPrimaryKey.setName(name);
        primaryKeyList.add(actualPrimaryKey);
    }
}
