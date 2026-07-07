package com.helicalinsight.adhoc.genericsql;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.DatabaseFunctions;
import com.helicalinsight.efw.utility.JaxbUtils;

/**
 * Represents a standard SQL function implementation.
 * Created by author on 10/12/2015.
 * @author Rajasekhar
 */
final class StandardSqlFunction implements SqlFunction {

    private final static String prefix = "${";
    private final static String suffix = "}";
    private static final QuotableTypes[] values = QuotableTypes.values();
    private final SqlQueryContext context;
    private Map<String, DatabaseFunctions.DbFunction> databaseFunctions;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StandardSqlFunction.class);
    /**
     * Constructs a StandardSqlFunction instance.
     * 
     * @param referenceFile 	 reference file path.
     * @param context       	 SQL query context.
     */
    public StandardSqlFunction(String referenceFile, SqlQueryContext context) {
        if (referenceFile != null) {
            File path = new File(referenceFile);
            if (path.exists()) {
                DatabaseFunctions databaseFunctions = JaxbUtils.unMarshal(DatabaseFunctions.class, path);
                List<DatabaseFunctions.DbFunction> dbFunctions = databaseFunctions.getDbFunctions();

                if (dbFunctions != null) {
                    this.databaseFunctions = new HashMap<>();
                    for (DatabaseFunctions.DbFunction function : dbFunctions) {
                        this.databaseFunctions.put(function.getKey(), function);
                    }
                }
            }
        }
        this.context = context;
    }
    /**
     * Generates the SQL function based on the provided JSON object.
     * 
     * @param databaseFunction 		 JSON object representing the database function.
     * @return The generated SQL function string.
     */
    public String sqlFunction(JsonObject databaseFunction) {
        if (this.databaseFunctions == null || databaseFunction == null) {
            return "";
        }

        if (!databaseFunction.has("functionName")) {
            throw new IllegalArgumentException("One of the databaseFunctions have no functionName");
        }

        /*If there is a function name, get the signature.
        * If there are no parameters from the xml, return the signature. Else, these xmlParameters
        * need to be used.
        *
        * Get the list of xmlParameters.
        * Loop through the xmlParameters and get each xmlParameter name.
        *
        * While looping, check the httpParameters json for the function.
        *
        * I.    If there is no value provided for
        *       the xmlParameter get the default xmlValue. Check if the xmlValue is column. If yes, then apply quotes
        *       and continue looping. If not, then check if the value is quotable and if quotable put the value
        *       in single quotes and continue looping.
        *
        * II.   If there is a value provided, check if it is a string or json object.
        *
        *       1.  If the value is a string, then check if the jsonValue is a column. If yes, then apply quotes
        *           and continue looping. If not, then check if the value is quotable and if quotable put the value
        *           in single quotes and continue looping.
        *
        *       2.  If the value is an object, do recursion.
        * */

        String functionName = databaseFunction.get("functionName").getAsString();
        return function(databaseFunction, functionName);
    }
    /**
     * Generates the SQL function string.
     * 
     * @param databaseFunction 			 JSON object representing the database function.
     * @param functionName     			 name of the function.
     * @return The generated SQL function string.
     */
    private String function(JsonObject databaseFunction, String functionName) {
        DatabaseFunctions.DbFunction dbFunction = this.databaseFunctions.get(functionName);
        String signature = functionSignature(functionName, dbFunction);

        DatabaseFunctions.Parameters parameters = dbFunction.getParameters();
        if (parameters == null || parameters.getParameterList() == null) {
            return signature;
        } else {
            List<DatabaseFunctions.Parameter> parameterList = parameters.getParameterList();
            if (parameterList.isEmpty()) {
                return signature;
            }

            JsonObject httpParameters = null;
            if (databaseFunction.has("parameters")) {
                httpParameters = databaseFunction.getAsJsonObject("parameters");
            }

            if (httpParameters == null) {
                //To avoid null pointer
                httpParameters = new JsonObject();
            }

            for (DatabaseFunctions.Parameter parameter : parameterList) {
                String name = parameter.getName();
                String pattern = prefix + name + suffix;

                if (httpParameters.has(name)) {
                     JsonElement object = httpParameters.get(name);
                    if (object instanceof JsonObject && httpParameters.getAsJsonObject(name).has("functionName")) {
                        JsonObject function = (JsonObject) object;
                        String namedFunctionKey = function.get("functionName").getAsString();
                        signature = replacePattern(signature, pattern, function(function, namedFunctionKey));
                    } else {
                        String value = object.getAsString();
                        if (object instanceof JsonObject || object instanceof JsonArray) {
                            value = "'" + value + "'";
                        } else {

                            value = sanitize(parameter, value);
                        }
                        signature = replacePattern(signature, pattern, value);
                    }
                } else {
                    String defaultValue = parameter.getDefaultValue();
                    if (defaultValue == null) {
                        logger.error("A parameter " + parameter + " is found without default value. But there was no " +
                                "matching value from the request. Probable cause of the issue is change in the " +
                                "parameter names, or malformed xml or illegal request.");

                        throw new QueryBuilderException(String.format("The parameter %s for a database function has "
                                + "no value from parameters or from default configuration. Either the " +
                                "DatabaseFunctions xml is malformed or the request is invalid.", name));
                    }
                    defaultValue = sanitize(parameter, defaultValue);
                    signature = replacePattern(signature, pattern, defaultValue);
                }
            }
        }
        return signature;
    }
    /**
     * Sanitizes the value of a parameter.
     * 
     * @param parameter 		 parameter.
     * @param value     		 value to sanitize.
     * @return The sanitized value.
     */
    private String sanitize(DatabaseFunctions.Parameter parameter, String value) {
        if ("true".equalsIgnoreCase(parameter.getColumn()) && isDatabaseColumn(value)) {
            return this.context.quotes(value);
        } else {
            if (isQuotable(parameter.getType())) {
                return AdhocUtils.singleQuotes(value);
            }
        }
        return value;
    }
    /**
     * Generates the SQL function signature.
     * 
     * @param functionName 			 name of the function.
     * @param dbFunction   			 database function.
     * @return SQL function signature.
     * @throws QueryBuilderException If the function or its signature is invalid.
     */
    @NotNull
    private String functionSignature(String functionName, DatabaseFunctions.DbFunction dbFunction) {
        if (dbFunction == null) {
            throw new QueryBuilderException("The function " + functionName + " is not a valid function. Function is " +
                    "not defined.");
        }

        String signature = dbFunction.getSignature();

        if (signature == null) {
            throw new QueryBuilderException("The function " + functionName + " is not a valid function. Function is " +
                    "signature not defined.");
        }
        return signature;
    }
    /**
     * Replaces a pattern with a value in the SQL function signature.
     * 
     * @param signature 		 SQL function signature.
     * @param pattern   		 pattern to replace.
     * @param value     		 value to replace with.
     * @return The modified SQL function signature.
     */
    private String replacePattern(String signature, String pattern, String defaultValue) {
        if (defaultValue != null) {
            signature = signature.replace(pattern, defaultValue);
        }
        return signature;
    }
    /**
     * Checks if a parameter type is quotable.
     * 
     * @param type 					 parameter type.
     * @return {@code true} if the parameter type is quotable, {@code false} otherwise.
     */
    private boolean isQuotable(String type) {
        if (type == null) {
            return false;
        }

        for (QuotableTypes quotableType : values) {
            if (quotableType.toString().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a value represents a database column.
     * 
     * @param value 				 value to check.
     * @return {@code true} if the value represents a database column, {@code false} otherwise.
     */
    private boolean isDatabaseColumn(String value) {
        String[] valueArray = value.split("\\.");
        for (String key : this.context.getColumnsMap().keySet()) {
            String[] keyArray = key.split("\\.");
            if (keyArray[keyArray.length - 1].equalsIgnoreCase(valueArray[valueArray.length - 1])) {
                return true;
            }
        }
        return false;
    }
    /**
     * Enumerates the types of quotable parameters.
     */
    enum QuotableTypes {
        text, date, time, dateTime
    }
}
