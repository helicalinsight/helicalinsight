package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.utility.JsonUtils;
import groovy.json.JsonSlurper;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the setting.xml configuration and gives the configured elements as an Object
 * <p/>
 * Created by Author on 03-03-2015
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public class SqlQueryUtilities {
	/**
     * Retrieves the appropriate QueryGeneratorUtility based on the connection type.
     *
     * @param connectionType 			 type of the database connection.
     * @return QueryGeneratorUtility object.
     * @throws IllegalArgumentException  If the connection type is null.
     */
    @Nullable
    public static QueryGeneratorUtility getQueryGeneratorUtility(@Nullable String connectionType) {
        if (connectionType == null) {
            throw new IllegalArgumentException("The argument connection type is null");
        }

        JsonObject settingsJson = JsonUtils.newGetSettingsJson();

        try {
            JsonArray queryGenerators = settingsJson.getAsJsonObject("queryGenerators").getAsJsonArray("generator");
            for (JsonElement object : queryGenerators) {
                JsonObject generator = object.getAsJsonObject();
                String useDefault = generator.get("useDefault").getAsString();
                if ("true".equalsIgnoreCase(useDefault)) {
                    try {
                        JsonArray reference = generator.getAsJsonArray("ref");
                        for (JsonElement aReference : reference) {
                            String aType = aReference.getAsJsonObject().get("type").getAsString();
                            if (connectionType.equalsIgnoreCase(aType)) {
                                return getQueryGeneratorUtility(generator);
                            }
                        }
                    } catch (Exception ignore) {
                        String aType = generator.getAsJsonObject("ref").get("type").getAsString();
                        if (connectionType.equalsIgnoreCase(aType)) {
                            return getQueryGeneratorUtility(generator);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new ConfigurationException("Setting.xml metadata implementations node is " + "configured " +
                    "incorrectly", ex);
        }
        return null;
    }

    /**
     * Retrieves the appropriate QueryGeneratorUtility based on the provided JSON object.
     *
     * @param generator 			 JSON object containing class name and type details.
     * @return QueryGeneratorUtility object.
     * @throws ConfigurationException If the configuration is incorrect.
     */
    @NotNull
    private static QueryGeneratorUtility getQueryGeneratorUtility(@NotNull JsonObject generator) {
        String implementationClass;
        String type;
        try {
            implementationClass = generator.get("class").getAsString();
            type = generator.get("type").getAsString();
        } catch (Exception ex) {
            throw new ConfigurationException("Setting.xml queryGenerators implementations node is" +
                    " configured " + "incorrectly", ex);
        }
        return new QueryGeneratorUtility(implementationClass, type);
    }
    /**
     * Retrieves the driver class from the metadata.
     *
     * @param metadata 		 metadata containing connection details.
     * @return The DriverClass object.
     */
    public static DriverClass driverClass(Metadata metadata) {
        ConnectionDetails connectionDetails = metadata.getConnectionDetails();
        DriverClass driverClass = connectionDetails.getDriverClass();

        if (driverClass != null) {
            return driverClass;
        } else {
            return null;
        }
    }
    /**
     * Translates a condition string to its SQL equivalent.
     *
     * @param json      		 JSON object containing condition details.
     * @param condition 		 condition string to translate.
     * @return SQL equivalent of the condition.
     * @throws QueryBuilderException      If the JSON is malformed.
     * @throws IllegalArgumentException   If the condition is invalid.
     */
    public static String condition(@NotNull JsonObject json, String condition) {
        String actualCondition;
        if ("EQUALS".equalsIgnoreCase(condition)) {
            actualCondition = "=";
        } else if ("IS_ONE_OF".equalsIgnoreCase(condition)) {
            actualCondition = "in";
        } else if ("IN_RANGE".equalsIgnoreCase(condition)) {
            actualCondition = "inRange";
        } else if ("NOT_IN_RANGE".equalsIgnoreCase(condition)) {
            actualCondition = "notInRange";
        } else if ("CUSTOM".equalsIgnoreCase(condition)) {
            String customCondition;
            if (json.has("customCondition")) {
                customCondition = json.get("customCondition").getAsString();
            } else {
                throw new QueryBuilderException("Json has custom as condition " + "but no customCondition is provided" +
                        ". Malformed Json.");
            }
            actualCondition = customCondition;
        } else {
            throw new IllegalArgumentException("The condition should be either 'equals' " + "or " +
                    "'is one of' or 'custom'");
        }
        return actualCondition;
    }
    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param settingFilePath 			 path to the file.
     * @return The contents of the file as a string.
     * @throws RuntimeException 		 If an error occurs while reading the file.
     */
    public static String getFileAsString(String settingFilePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(settingFilePath)), ControllerUtils.defaultCharSet());
        } catch (IOException ioe) {
            throw new RuntimeException("There was a problem in the operation" + " " + ioe.getMessage());
        }
    }
    /**
     * Evaluates Groovy expressions in the provided SQL query string using form data only.
     */
    public static String evalAllExpressionFromFormData(JsonObject formData, String query) {
        JsonObject safeFormData = formData != null ? formData : new JsonObject();
        return evalGroovyExpression(safeFormData, "", query);
    }

    /**
     * Evaluates Groovy expressions in the provided SQL query string.
     *
     * @param context 		 SqlQueryContext provides formData.
     * @param query   		 SQL query
     * @return The evaluated SQL query in string format.
     */
    public static String evalAllExpression(SqlQueryContext context, String query) {
        JsonObject formData = context != null ? context.getFormData() : new JsonObject();
        Object sqlContextBinding = context != null ? new SqlContextProxy(context) : "";
        return evalGroovyExpression(formData, sqlContextBinding, query);
    }

    private static String evalGroovyExpression(JsonObject formData, Object sqlContextBinding, String query) {
        String contentPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" +
                File.separator + "sqlQueryExpression.groovy";


        File file = new File(contentPath);

        if (file.exists()) {
            String groovyScript = getFileAsString(contentPath);
            Binding binding = new Binding();

            JsonSlurper slurper = new JsonSlurper();
            Object groovyJson = slurper.parseText(formData.toString());
            binding.setVariable("formData", groovyJson);
            binding.setVariable("sqlContext", sqlContextBinding);
            if (query.contains("import ")) {
                query.lastIndexOf("import ");
                int lastImport = query.lastIndexOf("import ");
                int indexOfN = query.indexOf("\n", lastImport);

                String importString = query.substring(0, indexOfN);
                groovyScript = groovyScript.replace("_INSERT_IMPORTS_HERE_", importString);
                query = query.substring(indexOfN, query.length());
            } else {
                groovyScript = groovyScript.replace("_INSERT_IMPORTS_HERE_", "");
            }
            query = query.replace("filter}.label", "filter.label}");
            query = query.replace("filter}.condition", "filter.condition}");
            query = query.replace("filter}.mode", "filter.mode}");
            query = query.replace("filter}.column", "filter.column}");
            query = query.replace("filter}.values", "filter.values}");
            query = query.replace("filter}.value", "filter.value}");
            query = query.replace("filter}.id", "filter.id}");
            query = query.replace("filter}.fullyQualifiedColumn", "filter.fullyQualifiedColumn}");
            groovyScript = groovyScript.replace("__INSERT_CODE_HERE__", query);
            GroovyShell shell = new GroovyShell(binding);

            Object evaluate = shell.evaluate(groovyScript);
            return String.valueOf(evaluate);
        }
        return query;
    }
    /**
     * Sanitizes the values in a list to prevent SQL injection.
     *
     * @param list 		 list of values to sanitize.
     * @return The sanitized list of values.
     */
    public static List getSanitizedValueForList(List list) {
        if (list == null) {
            return null;
        }
        List newList = new ArrayList();
        for (Object item : list) {
            String newItem = String.valueOf(item);
            if (newItem.contains("$")) {
                String temp = newItem.replaceAll("\\$\\{", "_hiBug_");
                temp = temp.replaceAll("\\$", "\\\\\\$");
                temp = temp.replaceAll("_hiBug_", "\\$\\{");
                newList.add(temp);
            } else {
                newList.add(item);
            }
        }
        return newList;
    }
}