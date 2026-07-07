package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.adhoc.metadata.jaxb.DatabaseFunctions;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Utility class for reading database-specific functions from a DatabaseFunctions object and adding them to a JsonObject response.
 * It allows filtering functions by group and includes them in the response based on specified groups.
 * Created by author on 10/9/2015.
 * @author Rajasekhar
 */
public class DatabaseFunctionFileReader {

	/**
     * Adds database-specific functions to a JsonObject response, filtered by group.
     * 
     * @param response                  JsonObject to which database functions will be added
     * @param databaseFunctions         DatabaseFunctions object containing the functions to be added
     * @param groups JsonArray specifying groups to filter functions (null to include all)
     */
    public void addDatabaseSpecificFunctions(JsonObject response, DatabaseFunctions databaseFunctions,
                                             JsonArray groups) {
        List<DatabaseFunctions.DbFunction> dbFunctions = databaseFunctions.getDbFunctions();

        Map<String, List<JsonObject>> allFunctions = new HashMap<>();

        boolean all = false;
        if (groups == null) {
            all = true;
        }

        if (dbFunctions != null) {
            for (DatabaseFunctions.DbFunction dbFunction : dbFunctions) {
                String group = dbFunction.getGroup();
                if (all || groups.contains(new JsonPrimitive(group))) {
                    JsonObject function;
                    function = new JsonObject();
                    GsonUtility.accumulate(function,"key", dbFunction.getKey());
                    String description = dbFunction.getDescription();
                    if (description != null) {
                        GsonUtility.accumulate(function,"description", description);
                    }
                    GsonUtility.accumulate(function,"value", dbFunction.getValue());
                    GsonUtility.accumulate(function,"signature", dbFunction.getSignature());

                    if (dbFunction.getReturns() != null) {
                        GsonUtility.accumulate(function,"returns", dbFunction.getReturns());
                    }

                    if (dbFunction.getId() != null) {
                        GsonUtility.accumulate(function,"id", dbFunction.getId());
                    }
                    if (dbFunction.getName() != null) {
                        GsonUtility.accumulate(function,"name", dbFunction.getName());
                    }


                    DatabaseFunctions.Parameters dbFunctionParameters = dbFunction.getParameters();
                    if (dbFunctionParameters != null) {
                        JsonArray parameters = new JsonArray();
                        List<DatabaseFunctions.Parameter> parameterList = dbFunctionParameters.getParameterList();
                        if (parameterList != null) {
                            for (DatabaseFunctions.Parameter parameter : parameterList) {
                                String name = parameter.getName();
                                if (name != null) {
                                    JsonObject functionParameter = new JsonObject();
                                    GsonUtility.accumulate(functionParameter,"name", name);

                                    String type = parameter.getType();
                                    if (type != null) {
                                        GsonUtility.accumulate(functionParameter,"type", type);
                                    }

                                    String column = parameter.getColumn();
                                    if (column != null) {
                                        if ("true".equalsIgnoreCase(column)) {
                                            GsonUtility.accumulateBoolean(functionParameter,"column", true);
                                        } else {
                                        	 GsonUtility.accumulateBoolean(functionParameter,"column", false);
                                        }
                                    }

                                    String defaultValue = parameter.getDefaultValue();
                                    if (defaultValue != null) {
                                        GsonUtility.accumulate(functionParameter,"defaultValue", defaultValue);
                                    }
                                    parameters.add(functionParameter);
                                }
                            }
                        }
                        GsonUtility.accumulate(function,"parameters", parameters);
                    }

                    List<JsonObject> jsonObjects = allFunctions.get(group);
                    if (jsonObjects == null) {
                        jsonObjects = new ArrayList<>();
                        jsonObjects.add(function);
                        allFunctions.put(group, jsonObjects);
                    } else {
                        jsonObjects.add(function);
                    }
                }
            }
        }

        if (!allFunctions.isEmpty()) {
            GsonUtility.accumulate(response,"databaseFunctions", allFunctions);
        }
    }
}
