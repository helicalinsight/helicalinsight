package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Validates JSON data containing filter information.
 * Created by Rajasekhar on 06-05-2015.
 * @author Rajasekhar
 */
@Component
final class FiltersJsonValidator {
	/**
     * Validates the provided JSON data containing filter information.
     *
     * @param formData               	 JSON object containing filter information.
     * @param container              	 metadata store containing database information.
     * @param functionsJsonValidator 	 validator for functions JSON data.
     */
    public void validateJson(@NotNull JsonObject formData, @NotNull IMetadataStore container,
                             @NotNull FunctionsJsonValidator functionsJsonValidator) {
        if (formData.has("customFilterExpression") || formData.has("filters")) {
            /*if (!formData.has("customFilterExpression") || !formData.has("filters")) {
                throw new QueryBuilderException("One of the two required fields customFilterExpression or " +
                        "filters is missing");
            }*/

            /*String customFilterExpression = formData.getString("customFilterExpression");
            if ("".equals(customFilterExpression) || customFilterExpression.trim().length() == 0) {
                throw new QueryBuilderException("The parameter customFilterExpression is present " +
                        "" + "but is empty");
            }*/

            JsonArray filters = formData.getAsJsonArray("filters");
            validate(container, filters);
        }

        if (formData.has("customHavingExpression") || formData.has("having")) {
            /*if (!formData.has("customHavingExpression") || !formData.has("having")) {
                throw new QueryBuilderException("One of the two required fields customHavingExpression or having" + "" +
                        " is missing");
            }*/

            //String customHavingExpression = formData.getString("customHavingExpression");
            /*if ("".equals(customHavingExpression) || customHavingExpression.trim().length() == 0) {
                throw new QueryBuilderException("The parameter customHavingExpression is present " +
                        "" + "but is empty");
            }*/

            JsonArray having = formData.getAsJsonArray("having");
            JsonArray alteredJson = functionsJsonValidator.validateFunctionNameAndColumns(having, true);
            formData.remove("having");
            GsonUtility.accumulate(formData,"having", alteredJson);
        }
    }
    /**
     * Validates the filters contained in the provided JSON array.
     *
     * @param container 		 metadata store containing database information.
     * @param filters   		 JSON array containing filter information.
     */
    private void validate(@NotNull IMetadataStore container, @NotNull JsonArray filters) {
        List<String> columns = new ArrayList<>();
        for (JsonElement object : filters) {
            JsonObject json = object.getAsJsonObject();
            if (!json.has("custom")) {
                String column = json.get("column").getAsString();
                columns.add(column);
            }

            if (json.has("usedColumns")) {
                JsonArray usedColumns = json.getAsJsonArray("usedColumns");
                for (JsonElement column : usedColumns) {
                    columns.add(column.getAsString());
                }
            }
        }

        final List<String> databaseColumns = container.getFullyQualifiedColumnsList();
        if (!databaseColumns.containsAll(columns)) {
            throw new QueryBuilderException("One or more of the non custom columns are illegal as they are not " +
                    "part of the database.");
        }
    }
}
