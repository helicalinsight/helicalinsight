package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.PropertiesFileReader;

/**
 * Utility class containing various static methods used across the generic SQL adhoc package.
 * Includes methods for generating unique IDs, retrieving user input, manipulating database and table names, and column names etc.
 * 
 * Created by author on 08-03-2015.
 * @author Rajasekhar
 */
public class AdhocUtils {
	/**
	 * This method generates random unique name.
	 * @return unique name in string format.
	 */
    @NotNull
    public static String getUuid() {
        //Generate the random uuid
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    /**
     * it fetch the column data from jsonArray object and returns list of column name.
     * @param columns      it provides column details.
     * @return list of column names
     */
    @NotNull
    public static List<String> getUserInput(@NotNull JsonArray columns) {
        List<String> userInput = new ArrayList<>();
        for (JsonElement object : columns) {
            JsonObject json = object.getAsJsonObject();
            if (!json.has("custom")) {
                userInput.add(json.get("column").getAsString());
            }
            //The following 'if' is not being used now. The usedColumns was meant for nested database functions.
            if (json.has("usedColumns")) {
                JsonArray usedColumns = json.getAsJsonArray("usedColumns");
                for (JsonElement column : usedColumns) {
                    userInput.add(column.getAsString());
                }
            }
        }
        return userInput;
    }
    /**
     * it returns the name which having table and column name combined with dot.
     * @param column    column name
     * @return string which consist of table and column name.
     */
    public static String stripDatabaseName(String column) {
        if (!column.contains(".")) {
            return column;
        }
        String tableName = getTableNameFromColumn(column);
        column = tableName + "." + getColumnNameFromColumn(column);
        return column;
    }
    /**
     * returns table name from column name
     * @param column             name of column
     * @return table name.
     */
    public static String getTableNameFromColumn(@Nullable String column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }

        column = sanitizeStringIfStartsWithDot(column);

        if (!column.contains(".")) {
            return column;
        }

        int indexOf = column.lastIndexOf(".");
        String table = column.substring(0, indexOf);
        int tableIndex = table.lastIndexOf(".");
        return table.substring(tableIndex + 1);
    }
    /**
     * Returns column name.
     * @param column   name which is combined with table and column name.
     * @return column name.
     */
    public static String getColumnNameFromColumn(@Nullable String column) {
        if (column == null) {
            throw new IllegalArgumentException();
        }
        if (!column.contains(".")) {
            return column;
        }

        int indexOf = column.lastIndexOf(".");
        return column.substring(indexOf + 1);
    }
    /**
     * column name checking whether it has started with dot or not
     * @param column     column name
     * @return column name.
     */
    @NotNull
    public static String sanitizeStringIfStartsWithDot(@NotNull String column) {
        if (column.startsWith(".")) {
            column = column.substring(1);
        }
        return column;
    }
    /**
     * it retrieves table list and table names.
     * @param database      database provides tables.
     * @return list of table names.
     */
    @NotNull
    public static List<String> allVertices(@Nullable Database database) {
        if (database == null) {
            throw new QueryBuilderException("The schema information could not be retrieved. Parameter database is " +
                    "null");
        }
        List<String> tables = new ArrayList<>();
        Tables databaseTables = database.getTables();
        if (databaseTables != null) {
            List<Table> tableList = databaseTables.getTableList();

            if (tableList != null) {
                tables = tableList.stream().map(Table::getName).collect(Collectors.toList());
            }
        }
        return tables;
    }

    /**
     * returns list of databases.
     * @param metadata       provides list of databases
     * @return list of databases.
     */
    @NotNull
    private static List<Database> getAllDatabases(@Nullable Metadata metadata) {
        List<Database> otherDatabases = metadata.getConnections().getConnectionDatabase().stream().map(r -> r.getDatabase()).collect(Collectors.toList());
        otherDatabases.add(metadata.getDatabase());
        return otherDatabases;
    }

    /**
     * It returns list of fully Qualified names.
     * @param database     provides databaseName
     * @return list of fully Qualified names
     */
    public static List<String> fullyQualifiedTableNames(Database database) {
        List<String> tables = allVertices(database);
        String databaseName = database.getName();
        List<String> fullyQualifiedNames = new ArrayList<>();
        for (String table : tables) {
            fullyQualifiedNames.add(databaseName + "." + table);
        }
        return fullyQualifiedNames;
    }

    @Deprecated
    /**
     * @deprecated Use SqlQueryContext.getTableName
     */
    public static String getTableNameFromTable(String table) {
        if (table == null) {
            throw new IllegalArgumentException();
        }

        if (!table.contains(".")) {
            return table;
        }

        int indexOf = table.lastIndexOf(".");

        //Probably fails for drill  csv
        return table.substring(indexOf + 1);
    }

    /**
     * it returns the type of 
     * @param type                    type
     * @param dataTypesMapping		  provides data type value
     * @return
     */
    public static String getType(String type, @NotNull Map<String, String> dataTypesMapping) {
        JsonObject mapping;
        mapping = new JsonObject();
        String json = type.replace("\\", "");
        GsonUtility.accumulate(mapping,json, dataTypesMapping.get(json));
        return mapping.toString();
    }

    public static String singleQuotes(String value) {
        return "'" + value + "'";
    }

    public static Map<String, String> getDataTypeMapping() {
        return new PropertiesFileReader().read("Admin", "dataTypesMapping.properties");

    }
    /**
     * Retrieves columns array.
     * @param formDataJson       provides column arrays
     * @return  returns columns arrays
     */
    @NotNull
    public static JsonArray columnsArray(@NotNull JsonObject formDataJson) {
        JsonArray columns;
        try {
            columns = formDataJson.getAsJsonArray("columns");
            if (columns == null || "".equals(columns.toString()) || columns.isEmpty()) {
                throw new IllegalArgumentException("The user input columns is not in proper format or is null.");
            }
        } catch (Exception ex) {
            throw new RequiredParameterIsNullException("The parameter columns is not an array.", ex);
        }
        return columns;
    }
    /**
     * Retrieves tables from database and returns map object contains name of the table
     * @param database        provides list of tables
     * @return map containing tables original name and alias name.
     */
    public static Map<String, String> allTableAliasMap(Database database) {
        if (database == null) {
            throw new QueryBuilderException("The schema information could not be retrieved. Parameter database is " +
                    "null");
        }
        Map<String, String> tableAliasMap = new HashMap<>();
        Tables databaseTables = database.getTables();
        if (databaseTables != null) {
            List<Table> tableList = databaseTables.getTableList();
            if (tableList != null) {
                for (Table table : tableList) {
                    String name = table.getName();
                    String aliasName = table.getAliasName();
                    tableAliasMap.put(name, aliasName);
                }
            }
        }
        return tableAliasMap;

    }
}
