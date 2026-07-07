package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Utility class for validating JSON columns data and ensuring uniqueness of column aliases.
 * Created by author on 06-05-2015.
 * @author Rajasekhar
 */
@Component
final class ColumnsJsonValidator {
	/**
     * Validates the JSON columns data and checks for uniqueness of column aliases.
     * @param columns 			 			JSON array containing column data.
     * @param container 			 		metadata store container for accessing column information.
     * @throws IllegalStateException    	if one or more alias names of selected columns are not unique.
     * @throws IllegalColumnNameException   if one or more non-custom column names are incorrect or if alias names
     *                                      are existing database column names.
     */
    public void validateJson(@NotNull JsonArray columns, @NotNull IMetadataStore container) {
        List<String> aliasNames = new ArrayList<>();
        List<String> userRequestedColumns = new ArrayList<>();
        for (JsonElement object : columns) {
            JsonObject json = object.getAsJsonObject();
            if (json.has("alias")) {
                String alias = json.get("alias").getAsString();
                if (!aliasNames.contains(alias)) {
                    aliasNames.add(alias);
                } else {
                    throw new IllegalStateException("One or more of the alias names of the selected columns are " +
                            "same. Choose different alias names for the selected columns.");
                }
            }

            if (!json.has("custom")) {
                userRequestedColumns.add(json.get("column").getAsString());
            }

            if (json.has("usedColumns")) {
                JsonArray usedColumns = json.getAsJsonArray("usedColumns");
                for (JsonElement column : usedColumns) {
                    userRequestedColumns.add(column.getAsString());
                }
            }
        }
        validateColumns(userRequestedColumns, container, aliasNames);
    }
    /**
     * Validates the requested columns against the available metadata and checks for conflicts in column names
     * and aliases.
     * @param userInputColumns 			   list of requested column names.
     * @param container 				   metadata store container for accessing column information.
     * @param aliasNames 				   list of column aliases.
     * @throws IllegalColumnNameException  if one or more non-custom column names are incorrect or if alias names
     *                                     are existing database column names.
     */
    private void validateColumns(@NotNull List<String> userInputColumns, @NotNull IMetadataStore container,
                                 List<String> aliasNames) {
        List<String> fullyQualifiedColumnsList = container.getFullyQualifiedColumnsList();

        if (!fullyQualifiedColumnsList.containsAll(userInputColumns)) {
            throw new IllegalColumnNameException("One or more non custom column names are " +
                    "incorrect. Wrong input. Check the column names for typos or extra " +
                    "characters.");
        }

        for (String alias : aliasNames) {
            if (fullyQualifiedColumnsList.contains(alias)) {
                throw new IllegalColumnNameException("One or more of the alias names used for the columns is/are " +
                        "existing database column name(s). Please choose a different name as alias.");
            }
        }
    }
}
