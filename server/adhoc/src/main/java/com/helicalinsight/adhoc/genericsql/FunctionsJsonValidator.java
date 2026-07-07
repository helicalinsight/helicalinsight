package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.PropertiesFileReader;

/**
 * Represents a utility class for validating functions JSON in SQL queries.
 * Created by author on 10-04-2015.
 * @author Rajasekhar
 * @author Somen
 */
final class FunctionsJsonValidator {

    @NotNull
    private final List<String> orders;

    private final JsonObject formDataJson;

    private final Map<String, String> propertiesMap;

    @NotNull
    private final List<String> databaseColumns;
    /**
     * Constructs a new FunctionsJsonValidator object with the provided JSON form data and metadata container.
     *
     * @param formDataJson 	 JSON object containing form data.
     * @param container    	 metadata store container.
     */
    public FunctionsJsonValidator(JsonObject formDataJson, @NotNull IMetadataStore container) {
        this.formDataJson = formDataJson;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        this.propertiesMap = propertiesFileReader.read("Admin", "sqlQuery.properties");
        this.orders = Arrays.asList("desc", "DESC", "asc", "ASC");
        this.databaseColumns = container.getFullyQualifiedColumnsList();
    }
    /**
     * Validates the functions JSON.
     */
    void validateJson() {
        JsonObject functions;
        try {
            functions = this.formDataJson.getAsJsonObject("functions");
        } catch (Exception ex) {
            throw new QueryBuilderException("The parameter functions is present but is not json object");
        }

        //Ideally if groupBy is present and aggregate is not present some databases give error.
        //But since custom formula allows the user to use any aggregate function this code block
        //is commented.

        /*if (functions.has("groupBy") && !functions.has("aggregate")) {
            throw new QueryBuilderException("The parameter groupBy is present but the " +
                    "aggregate functions are are not applied. Valid SQL query can not be " +
                    "generated.");
        }*/

        try {
            if (functions.has("groupBy")) {
                verifyGroupBy(functions);
            }
            if (functions.has("aggregate")) {
                JsonArray aggregate = functions.getAsJsonArray("aggregate");
                checkEmptiness(aggregate, "aggregate");
                JsonArray copy = validateFunctionNameAndColumns(aggregate, false);
                functions.remove("aggregate");
                GsonUtility.accumulate(functions,"aggregate", copy);
            }
            if (functions.has("orderBy")) {
                verifyOrderBy(functions);
            }
        } catch (QueryBuilderException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new QueryBuilderException("One or both of the parameters groupBy, " +
                    "aggregate or orderBy are not in the form of array. Expecting array. Can not " +
                    "generate SQL.");
        }
    }
    /**
     * Verifies the groupBy function in the JSON.
     * @param functions 		 JSON object containing functions.
     */
    private void verifyGroupBy(@NotNull JsonObject functions) {
        JsonArray groupBy = functions.getAsJsonArray("groupBy");
        checkEmptiness(groupBy, "groupBy");
        List<String> columnsToCheck = new ArrayList<>();
        for (JsonElement object : groupBy) {
            JsonObject json = object.getAsJsonObject();
            isValid(json, "column");
            if (!json.has("custom")) {
                columnsToCheck.add(json.get("column").getAsString());
            }
        }
        verifyColumns(columnsToCheck);
    }
    /**
     * Checks if the provided JSON array is empty.
     * @param array 	 JSON array to check.
     * @param name  	 name of the array.
     */
    private void checkEmptiness(@Nullable JsonArray array, String name) {
        if (array != null && array.isEmpty()) {
            throw new QueryBuilderException("The parameter " + name + " is present " +
                    "but empty");
        }
    }
    /**
     * Validates function names and columns in the provided JSON array.
     *
     * @param jsonArray        	 JSON array containing functions.
     * @param optionalFunction   Indicates if the function is optional.
     * @return validated JSON array.
     */
    @NotNull
    JsonArray validateFunctionNameAndColumns(@NotNull JsonArray jsonArray, boolean optionalFunction) {
        JsonArray copy = new JsonArray();
        List<String> columnsToCheck = new ArrayList<>();
        for (JsonElement object : jsonArray) {
            JsonObject json = object.getAsJsonObject();
            if (!optionalFunction) {
                isValid(json, "function");
            }

            if (json.has("function")) {
                String function = json.get("function").getAsString();
                if (!function.contains("_")) {
                    checkFunctionName(function);
                    json.remove("function");
                    GsonUtility.accumulate(json,"function", this.propertiesMap.get(function));
                } else {
                    List<String> strings = Arrays.asList(function.split("_"));
                    for (String str : strings) {
                        checkFunctionName(str);
                    }
                    json.remove("function");
                    String mapping = "";
                    int count = 0;
                    for (String str : strings) {
                        if (count != 0) {
                            mapping = mapping + "_" + this.propertiesMap.get(str);
                        } else {
                            mapping = this.propertiesMap.get(str);
                        }
                        count++;
                    }
                    GsonUtility.accumulate(json,"function", mapping);
                }
            }

            isValid(json, "column");
            String column = json.get("column").getAsString();
            if (!json.has("custom")) {
                columnsToCheck.add(column);
            }

            if (json.has("usedColumns")) {
                JsonArray usedColumns = json.getAsJsonArray("usedColumns");
                for (JsonElement usedColumn : usedColumns) {
                    columnsToCheck.add(usedColumn.getAsString());
                }
            }

            copy.add(json);
        }
        verifyColumns(columnsToCheck);
        return copy;
    }
    /**
     * Verifies the orderBy function in the JSON.
     * @param functions 		 JSON object containing functions.
     */
    private void verifyOrderBy(@NotNull JsonObject functions) {
        JsonArray orderBy = functions.getAsJsonArray("orderBy");
        checkEmptiness(orderBy, "orderBy");
        List<String> columns = new ArrayList<>();
        for (JsonElement object : orderBy) {
            JsonObject json = object.getAsJsonObject();
            if (!json.has("alias") && !json.has("column")) {
                throw new QueryBuilderException("The orderBy json array is malformed" + "" +
                        ".Expecting alias and order keys in each object.");
            }
            if (!json.has("order")) {
                GsonUtility.accumulate(json,"order", "asc");
            }
            if (!this.orders.contains(json.get("order").getAsString())) {
                throw new QueryBuilderException("The orderBy json array is malformed" +
                        "." + "Expecting order types ASC or DESC ignoring case.");
            }
            if (!json.has("custom")) {
                columns.add(json.get("alias").getAsString());
            }
        }
        verifyColumns(columns);
    }
    /**
     * Checks if the provided JSON object has the specified key.
     * @param json 		 JSON object to check.
     * @param key  		 key to check.
     */
    private void isValid(@NotNull JsonObject json, String key) {
        if (!json.has(key)) {
            throw new QueryBuilderException("The json has no key " + key + ". Malformed " +
                    "json. Can not produce valid SQL.");
        }
    }
    /**
     * Verifies if the columns in the provided list are part of the database.
     * @param columns 		 list of columns to verify.
     */
    private void verifyColumns(@NotNull List<String> columns) {
        if (!this.databaseColumns.containsAll(columns)) {
            throw new QueryBuilderException("The json array is malformed as one of the non custom" + " column is not " +
                    "part of the database");
        }
    }
    /**
     * Checks if the provided function name is present in propertiesMap.
     * @param function 		 function name to check.
     */
    private void checkFunctionName(String function) {
        if (!this.propertiesMap.containsKey(function)) {
            throw new QueryBuilderException("The json has a function that is not defined. " + "Malformed json. Can " +
                    "not produce valid SQL.");
        }
    }
}