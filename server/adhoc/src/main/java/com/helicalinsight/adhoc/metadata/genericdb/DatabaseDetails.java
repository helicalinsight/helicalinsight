package com.helicalinsight.adhoc.metadata.genericdb;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;



/**
 * Utility class for retrieving database metadata information such as schemas, catalogs, and tables.
 * Provides methods for obtaining schema, catalog, and table details from a given database connection.
 * 
 * Created by author on 28-02-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("ALL")
public class DatabaseDetails {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseDetails.class);
    public static final String HI_CATALOG = "HI_CATALOG";
    public static final String HI_SCHEMA = "HI_SCHEMA";
    public static final String HI_TABLE = "HI_TABLE";
    public static final String HI_DATABASE = "HI_DATABASE";
    public static final String HI_CATALOG_CONTAINS = "HI_CATALOG_CONTAINS";
    public static final String HI_SCHEMA_CONTAINS = "HI_SCHEMA_CONTAINS";
    public static final String HI_TABLE_CONTAINS = "HI_TABLE_CONTAINS";
    public static final String HI_DATABASE_CONTAINS = "HI_DATABASE_CONTAINS";
    /**
     * Retrieves the value associated with the specified key from the given URL.
     *
     * @param url      		 URL containing the query parameters.
     * @param keyType  		 key whose value needs to be retrieved.
     * @return The value associated with the specified key in the URL query parameters.
     */
    public static String getList(String url, String keyType) {
        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        List<String> result = parameters.get(keyType);
        return result == null ?
                EMPTY : result.get(0);

    }
    /**
     * Splits a comma-separated string into a list of strings.
     *
     * @param value 			 comma-separated string.
     * @return A list of strings obtained by splitting the input string.
     */
    public static List<String> getComaToList(String value) {
        if (isEmpty(value)) {
            return new ArrayList<String>();
        }
        return Arrays.asList(value.split("\\s*,\\s*"));
    }
    /**
     * Retrieves schema information for a given database connection and schema name.
     *
     * @param connection      database connection provides database metadata.
     * @param schemaName      name of the schema.
     * @return A JSON string representing the schema information.
     * @throws SQLException    If a database access error occurs.
     */
    public static String getDatabaseSchemaJson(@NotNull Connection connection, @NotNull String schemaName) throws
            SQLException {
        JsonObject database;
        ResultSet result = null;
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            List<String> tableNameList = TableDetails.getListOfTables(databaseMetaData, null, schemaName);

            List<JsonObject> tables = new ArrayList<JsonObject>();
            for (String table : tableNameList) {
                result = databaseMetaData.getColumns(null, null, table, null);
                JsonObject actualTable;
                actualTable = new JsonObject();
                GsonUtility.accumulate(actualTable,"name", table);
                List<String> columns = new ArrayList<String>();
                while (result.next()) {
                    columns.add(result.getString(4));
                }
                GsonUtility.accumulate(actualTable,"columns", columns);
                tables.add(actualTable);
            }
            database = new JsonObject();
            GsonUtility.accumulate(database,"name", connection.getCatalog());
            GsonUtility.accumulate(database,"tables", tables);
        } finally {
            DbUtils.closeQuietly(result);
        }
        return database.toString();
    }
    /**
     * Retrieves schema information for a given database metadata.
     *
     * @param databaseMetaData 		 database metadata provides resultSet, url.
     * @return A JSON object representing the retrieved schema information.
     */
    @NotNull
    public static JsonObject newRetrieveSchemas(@NotNull DatabaseMetaData databaseMetaData) {
        long now = System.currentTimeMillis();
        long later;
        ResultSet result = null;
        JsonObject response;
        response = new JsonObject();
        List<String> schemaList = new ArrayList<String>();
        Set<String> allSchemaList = new HashSet<String>();
        List<String> allSchemaListNone = new ArrayList<String>();

        try {
            result = databaseMetaData.getSchemas();
            String url = databaseMetaData.getURL();
            String query = getQuery(url);
            String path = getPath(url);
            List<String> databaseList = getComaToList(getList(query, HI_DATABASE));
            databaseList.addAll(getComaToList(getList(query, HI_SCHEMA)));
            String getContainsDatabse = getList(query, HI_DATABASE_CONTAINS);
            String getContainsSchema = getList(query, HI_SCHEMA_CONTAINS);

            while (result.next()) {
                String tableSchem = result.getString("TABLE_SCHEM");
                if (tableSchem != null) {
                    if (path.toLowerCase().contains(tableSchem.toLowerCase())) {
                        schemaList.add(tableSchem);
                    }
                    if (databaseList.contains(tableSchem)) {
                        allSchemaList.add(tableSchem);
                    }
                    if (isNotEmpty(getContainsDatabse) && tableSchem.contains(getContainsDatabse)) {
                        allSchemaList.add(tableSchem);
                    }
                    if (isNotEmpty(getContainsSchema) && tableSchem.contains(getContainsSchema)) {
                        allSchemaList.add(tableSchem);
                    }


                    allSchemaListNone.add(tableSchem);
                }
            }

            if (allSchemaList.isEmpty()) {
                allSchemaList.addAll(allSchemaListNone);
            } else {
                schemaList.clear();
            }

            later = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The time taken to complete the request of getting " +
                        "the schemas " +
                        "information is %s", (later - now)));
            }
            JsonArray fromJson = new Gson().fromJson((schemaList.isEmpty() ? allSchemaList : schemaList).toString(),JsonArray.class);
            response.add("schemas", fromJson);
        } catch (SQLException e) {
            logger.error("Exception at catlog/schema ", e);
            schemaList.add("none");
            
            response.add("schemas", new Gson().fromJson(schemaList.toString(),JsonArray.class));
        } finally {
            DbUtils.closeQuietly(result);
        }
        return response;
    }

    /**
     * Retrieves schema information in a tree view format for a given database metadata and catalog array.
     * This method constructs a JSON array containing schema details grouped by catalogs, if provided.
     * If no catalogs are provided, it retrieves schema details for all available catalogs.
     *
     * @param databaseMetaData 		 database metadata.
     * @param catalogArray     		 An array containing catalog names. If null or empty, schema details for all catalogs are retrieved.
     * @return A JSON array representing the retrieved schema information in a tree view format.
     */
    @NotNull
    public static JsonArray retrieveSchemasTreeView(@NotNull DatabaseMetaData databaseMetaData, JsonArray catalogArray) {
        ResultSet result = null;

        JsonArray response = new JsonArray();


        try {


            if (catalogArray == null || catalogArray.isEmpty()) {
                String nullValue = ApplicationProperties.getInstance().getNullValue();
                result = getSchema(databaseMetaData, result, response, nullValue, Boolean.TRUE);
                return response;
            }
            for (JsonElement catalogName : catalogArray) {
                result = getSchema(databaseMetaData, result, response, catalogName.getAsString(), Boolean.FALSE);
            }
        } finally {
            DbUtils.closeQuietly(result);
        }
        return response;
    }
    /**
     * Retrieves schema information for a specific catalog or all catalogs based on the provided parameters.
     * This method retrieves schema details from the database metadata and constructs a JSON array
     * containing schema information.
     *
     * @param databaseMetaData 			 database metadata.
     * @param result           			 result set containing schema information.
     * @param response         			 JSON array to store the retrieved schema information.
     * @param catalogName      			 name of the catalog for which schema details are to be retrieved.
     * @param catalogEmpty     			 A flag indicating whether the catalog is empty or not.
     * @return The result set containing schema information.
     */
    public static ResultSet getSchema(DatabaseMetaData databaseMetaData, ResultSet result, JsonArray response, String catalogName, Boolean catalogEmpty) {
        Set<JsonObject> schemaList = new HashSet<JsonObject>();
        Set<JsonObject> allSchemaList = new HashSet<JsonObject>();
        List<JsonObject> allSchemaListNone = new ArrayList<JsonObject>();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", catalogName);
        try {
            String url = databaseMetaData.getURL();
            String query = getQuery(url);
            String path = getPath(url);
            List<String> databaseList = getComaToList(getList(query, HI_DATABASE));
            databaseList.addAll(getComaToList(getList(query, HI_SCHEMA)));
            String getContainsDatabse = getList(query, HI_DATABASE_CONTAINS);
            String getContainsSchema = getList(query, HI_SCHEMA_CONTAINS);
            result = catalogEmpty ? databaseMetaData.getSchemas() : databaseMetaData.getSchemas(catalogName, "%");
            int count = 0;
            while (result.next()) {
                String tableSchem = result.getString("TABLE_SCHEM");
                JsonObject temp = new JsonObject();
                temp.addProperty("name", (isEmpty(tableSchem) ? ApplicationProperties.getInstance().getNullValue() : tableSchem).toString());
                if (path.toLowerCase().contains(tableSchem)) {
                    schemaList.add(temp);
                    count++;
                }
                if (databaseList.contains(tableSchem)) {
                    allSchemaList.add(temp);
                }
                if (isNotEmpty(getContainsDatabse) && tableSchem.contains(getContainsDatabse)) {

                    allSchemaList.add(temp);
                }
                if (isNotEmpty(getContainsSchema) && tableSchem.contains(getContainsSchema)) {
                    allSchemaList.add(temp);
                }

                allSchemaListNone.add(temp);

            }
            if (allSchemaList.isEmpty()) {
                allSchemaList.addAll(allSchemaListNone);
            } else {
                count = 0;
            }
            JsonArray fromJson = new Gson().fromJson((count == 0 ? allSchemaList : schemaList).toString(),JsonArray.class);
            responseJson.add("schemas", fromJson);


        } catch (SQLException e) {
            logger.error("Error retriving the catlog/schema ", e);
            addNone(schemaList, responseJson);
        }
        response.add(responseJson);
        return result;
    }
    /**
     * Adds a 'none' schema entry to the schema list if no schemas are retrieved.
     *
     * @param schemaList     	 set of schema JSON objects.
     * @param responseJson   	 JSON object to store the schema list.
     */
    public static void addNone(Set<JsonObject> schemaList, JsonObject responseJson) {
        JsonObject temp = new JsonObject();
        temp.addProperty("name", "none");
        schemaList.add(temp);
        JsonArray fromJson = new Gson().fromJson(schemaList.toString(),JsonArray.class);
        responseJson.add("schemas", fromJson);
    }

    public static String getPath(String url) {
        int index = url.indexOf("?");
        String subString;

        if (index > -1) {

            subString = url.substring(0, index);
        } else {
            subString = url.substring(0, url.length());
        }
        int lastIndexOfForwardSlash = subString.lastIndexOf("/");
        if (lastIndexOfForwardSlash > -1) {
            return subString.substring(lastIndexOfForwardSlash, subString.length());
        }

        return EMPTY;
    }

    public static String getQuery(String url) {
        int index = url.indexOf("?");

        if (index > -1) {
            return url.substring(index, url.length());
        }
        return url;
    }
   
    /**
     * Retrieves catalogs  for a given database metadata.
     *
     * @param databaseMetaData 		 database metadata provides url , resultSet.
     * @return A JSON object representing the retrieved catalog information.
     */
    @NotNull
    public static JsonObject newRetrieveCatalogs(@NotNull DatabaseMetaData databaseMetaData) {

        ResultSet result = null;
        JsonObject response;
        try {
            String url = databaseMetaData.getURL();
            String query = getQuery(url);
            String path = getPath(url);
            List<String> databaseList = getComaToList(getList(query, HI_DATABASE));
            databaseList.addAll(getComaToList(getList(query, HI_CATALOG)));
            String getContainsDatabse = getList(query, HI_DATABASE_CONTAINS);
            String getContainsCatalog = getList(query, HI_CATALOG_CONTAINS);
            List<String> catalogs;
            Set<String> allCatalogs = new HashSet<String>();
            List<String> allCatalogsNone = new ArrayList<String>();
            result = databaseMetaData.getCatalogs();
            catalogs = new ArrayList<String>();
            while (result.next()) {
                String table_cat = result.getString("TABLE_CAT");
                if (table_cat != null) {
                    if (path.toLowerCase().contains(table_cat.toLowerCase())) {
                        catalogs.add(table_cat);
                    }
                    if (databaseList.contains(table_cat)) {
                        allCatalogs.add(table_cat);
                    }
                    if (isNotEmpty(getContainsCatalog) && table_cat.contains(getContainsCatalog)) {
                        allCatalogs.add(table_cat);
                    }
                    if (isNotEmpty(getContainsDatabse) && table_cat.contains(getContainsDatabse)) {
                        allCatalogs.add(table_cat);
                    }
                    allCatalogsNone.add(table_cat);
                }
            }
            if (allCatalogs.isEmpty()) {
                allCatalogs.addAll(allCatalogsNone);
            } else {
                catalogs.clear();
            }
            response = new JsonObject();
            JsonArray fromJson = new Gson().fromJson((catalogs.isEmpty() ? allCatalogs : catalogs).toString(),JsonArray.class);
            response.add("catalogs", fromJson);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new MetadataRetrievalException("Could not get database metadata information", e);
        } finally {
            DbUtils.closeQuietly(result);
        }
        return response;
    }

}
