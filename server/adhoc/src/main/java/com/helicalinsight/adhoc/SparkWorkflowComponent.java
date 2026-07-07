package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataMultiThreadingUtilities;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.JdbcQueryExecutor;
import com.helicalinsight.datasource.managed.SparkJdbcExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The SparkWorkflowComponent class implements the {@link IComponent} interface and provides functionality
 * for executing Spark workflow to retrieve metadata from databases.
 *
 * @author Somen
 * @since 24-06-2015
 */
@SuppressWarnings("unused")
public class SparkWorkflowComponent implements IComponent {

    private static final String NULL_VALUE = ApplicationProperties.getInstance().getNullValue();

    private static final Logger logger = LoggerFactory.getLogger(SparkWorkflowComponent.class);
    /**
     * Executes the SparkWorkflowComponent, retrieves metadata based on the provided JSON form data.
     *
     * @param jsonFormData 				form data containing parameters for metadata retrieval.
     * @return A JSON string containing the classifier, metadata, and other relevant information.
     * @throws EfwServiceException 		If an error occurs during metadata retrieval.
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        long now = System.currentTimeMillis();
        JsonObject formJson = new Gson().fromJson(jsonFormData,JsonObject.class);

        JsonObject parameters;
        if (formJson.has("parameters")) {
            parameters = formJson.getAsJsonObject("parameters");
            if (parameters == null || parameters.entrySet().isEmpty()) {
                throw new IncompleteFormDataException("The parameter 'parameters' is empty or null");
            }
        } else {
            IComponent defaultMetadataProvider = new DatabaseMetadataProvider();
            return defaultMetadataProvider.executeComponent(jsonFormData);
        }

        Integer threshold = MetadataMultiThreadingUtilities.getThreshold();

        JsonObject response;
        response = new JsonObject();

        JsonObject metadata = new JsonObject();

        Connection connection = null;
        String type = formJson.get("type").getAsString();
        DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formJson, type);
        try {
            //noinspection ConstantConditions
            connection = driverConnection.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            addCatalogs(parameters, metadata, databaseMetaData);

            List<String> schemaFromQuery = getSchemaFromQuery(connection, "databases");
            JsonArray fromJson = new Gson().fromJson(schemaFromQuery.toString(), JsonArray.class);
            metadata.add("schemas", fromJson);


            if (parameters.has("fetchData")) {
                boolean isColumnsRequested = isColumnsRequested(parameters);
                JsonArray fetchData = parameters.getAsJsonArray("fetchData");

                JsonArray allCatalogs = new JsonArray();
                for (Object object : fetchData) {
                    JsonObject json = new Gson().fromJson((JsonObject)object,JsonObject.class);

                    String catalog = null;
                    JsonArray schemas = null;
                    try {
                        if (json.has("catalog")) {
                            catalog = json.get("catalog").getAsString();
                            if (json.has("schemas")) {
                                schemas = json.getAsJsonArray("schemas");
                            }
                        } else {
                            if (json.has("schemas")) {
                                schemas = json.getAsJsonArray("schemas");
                            }
                        }
                    } catch (JsonSyntaxException ex) {
                        throw new MalformedJsonException("Error in retrieving metadata. The " +
                                "parameter catalog should be a string if present and the schemas " +
                                "should be an array.");
                    }

                    JsonObject singleCatalog = new JsonObject();
                    singleCatalog.addProperty("name", (catalog == null) ? NULL_VALUE : catalog);
                    //Check if schemas are present. Else empty schemas to avoid NullPointer
                    if (schemas == null) {
                        throw new RequiredParameterIsNullException("The parameter schemas can't be empty.");
                    }

                    JsonArray allSchemas = new JsonArray();
                    if (!isColumnsRequested) {
                        addAllTablesSpark(connection, allCatalogs, catalog, schemas, singleCatalog, allSchemas);
                    } else {
                        //Now columns are requested. So schemas should never be empty. The
                        //parameter tables must be an array.
                        for (Object aSchema : schemas) {
                            addToAllSchemasPark(connection, allSchemas, aSchema, formJson, type);
                        }
                        singleCatalog.add("schemas", allSchemas);
                        allCatalogs.add(singleCatalog);

                    }
                }
                metadata.add("catalogs", allCatalogs);
            }
        } catch (Exception ex) {
            throw new EfwServiceException("Couldn't get database metadata.", ex);
        } finally {
            DbUtils.closeQuietly(connection);
            long after = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.debug("Releasing the database connection. The time taken to retrieve " +
                        "" + "metadata is nearly {} milli seconds.", (after - now));
            }
        }
        response.addProperty("classifier", formJson.get("classifier").getAsString());
        response.add("metadata", metadata);
        return response.toString();
    }


    private void addToAllSchemasPark(Connection connection, @NotNull JsonArray allSchemas, Object aSchema, JsonObject formJson,
                                     String type) {
        String schema;
        JsonObject schemaJson = new Gson().fromJson(aSchema.toString(),JsonObject.class);
        if (schemaJson.has("name")) {
            schema = schemaJson.get("name").getAsString();
        } else {
            schema = null;
        }

        JsonArray tables = schemaJson.getAsJsonArray("tables");

        JsonObject singleSchema = new JsonObject();
        singleSchema.addProperty("name", ((schema == null) ? NULL_VALUE : schema));

        JsonArray allTables = new JsonArray();

        for (JsonElement jsonObject : tables) {
            String tableName =  jsonObject.getAsString();
            allTables.add(addToAllTablesSpark(connection, schema, tableName));

        }

        singleSchema.add("tables", allTables);
        allSchemas.add(singleSchema);
    }

    private JsonObject addToAllTablesSpark(Connection connection, String schema, String eachTable) {


        JsonObject singleTable = new JsonObject();
        singleTable.addProperty("name", eachTable);
        Object columns = columnList(connection, schema, eachTable);
        if (columns instanceof JsonObject) {
            JsonArray array = new JsonArray();
            array.add((JsonObject)columns);
            singleTable.add("columns", array);
        } else {
            singleTable.add("columns", (JsonElement) columns);
        }
        return singleTable;

    }

    /**
     * Retrieves the list of columns for a given table in the specified schema and connection.
     *
     * @param conn     					 database connection.
     * @param schema   					 schema name.
     * @param tableName 				 table name.
     * @return A JsonObject containing information about the columns of the specified table.
     */
    public static Object columnList(Connection conn, String schema,
                                    String tableName) {
        String columnInfoForTable = getColumnInfoForTable(conn, tableName, schema);
        JsonObject columnsJson = new Gson().fromJson(columnInfoForTable,JsonObject.class);
        return columnsJson.get("columns");
    }

    /**
     * Retrieves column information for a specific table in the given schema and connection.
     *
     * @param con      		 database connection.
     * @param table    		 table name.
     * @param schema   		 schema name.
     * @return A JSON string containing information about the columns of the specified table.
     */
    public static String getColumnInfoForTable(Connection con, String table, String schema) {
        JsonObject columns;
        columns = new JsonObject();
        try {
            Statement stmt = con.createStatement();


            table = escape(table);
            Statement statement = con.createStatement();
            SparkJdbcExecutor jdbcQueryExecutor = new SparkJdbcExecutor(statement, "select * from " + schema + "." + table + " limit 1");
            JsonObject result = JsonParser.parseString(jdbcQueryExecutor.executeSql().toString()).getAsJsonObject();
            JsonObject metadataJson = result.getAsJsonArray("metadata").get(0).getAsJsonObject();


            Map<String, String> columnInfo;


            for (int index = 0; index < metadataJson.size(); index++) {
                JsonObject indexElement = metadataJson.getAsJsonObject(String.valueOf(index + 1));
                String columnName = indexElement.get("name").getAsString();
                String columnType = indexElement.get("type").getAsString();
                // allSchemas.add(columnName);

                columnInfo = new HashMap<>();

                columnInfo.put("name", columnName);
                columnInfo.put("type", columnType);
                //columnInfo.put("size", "0");
                //columnInfo.put("nullable", "TRUE");
                columnInfo.put("position", String.valueOf(index + 1));
                JsonObject column = new Gson().toJsonTree(columnInfo).getAsJsonObject();//map converted to JsonObject
                GsonUtility.accumulate(columns,"columns", column);
            }


        } catch (SQLException ex) {
            throw new MetadataRetrievalException(ex);
        }
        return columns.toString();
    }
    /**
     * Escapes special characters in a string for use in SQL queries.
     *
     * @param string 		 input string.
     * @return The escaped string.
     */
    public static String escape(String string) {
        if (string.contains("/")) {
            string = string.replaceAll("/", "//");
        }

        if (string.contains("\\")) {
            string = string.replaceAll("\\\\", "\\\\\\\\");
        }
        return string;
    }

    /**
     * Adds information about tables in the specified schemas to the catalog and schemas of metadata.
     * 
     * @param connection    		 Connection object representing the database connection.
     * @param allCatalogs   		 JsonArray containing information about all catalogs.
     * @param catalog       		 name of the catalog (can be null).
     * @param schemas       		 JsonArray containing information about schemas.
     * @param singleCatalog 		 JsonObject containing information about a single catalog.
     * @param allSchemas    		 JsonArray containing information about all schemas.
     */
    private void addAllTablesSpark(Connection connection, JsonArray allCatalogs, String catalog, JsonArray schemas, JsonObject singleCatalog, JsonArray allSchemas) {
//Tables are requested. So due to null schemas
        String schema;
        if (!schemas.isEmpty()) {
            for (Object aSchema : schemas) {
                JsonObject schemaJson = new Gson().fromJson((JsonObject)aSchema,JsonObject.class);
                if (schemaJson.has("name")) {
                    schema = schemaJson.get("name").getAsString();
                } else {
                    schema = null;
                }
                List<String> tables = getTablesFromQuery(connection, schema);
                JsonObject singleSchema = new JsonObject();
                singleSchema.addProperty("name", ((schema == null) ? NULL_VALUE : schema));
                JsonArray fromJson = new Gson().fromJson(tables.toString(),JsonArray.class);
                singleSchema.add("tables", fromJson);
                allSchemas.add(singleSchema);
            }
        }
        singleCatalog.add("schemas", allSchemas);
        allCatalogs.add(singleCatalog);
    }
    /**
     * Retrieves a list of schema names from the specified database using SQL queries.
     *
     * @param con           		 Connection object representing the database connection.
     * @param databaseTable 		 name of the table or database used in the query.
     * @return A List of Strings containing the names of schemas in the specified database.
     */
    private List<String> getSchemaFromQuery(Connection con, String databaseTable) {


        List<String> allSchemas = new ArrayList<>();
        try {

            Statement statement = con.createStatement();
            JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(statement, "show databases");
            JsonObject result = JsonParser.parseString(jdbcQueryExecutor.executeSql().toString()).getAsJsonObject();
            JsonArray data = result.getAsJsonArray("data");
            if (data != null && data.size() > 0) {
                for (int index = 0; index < data.size(); index++) {
                    allSchemas.add(data.get(index).getAsJsonObject().get("databaseName").getAsString());
                }
            }

        } catch (Exception ignore) {

        }
        return allSchemas;
    }

    /**
     * Retrieves a list of table names from the specified schema using SQL queries.
     *
     * @param con        		 Connection object representing the database connection.
     * @param schemaName 		 name of the schema for which tables are to be retrieved.
     * @return A List of Strings containing the names of tables in the specified schema.
     */
    private List<String> getTablesFromQuery(Connection con, String schemaName) {
        List<String> allSchemas = new ArrayList<>();
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("use " + schemaName);
            ResultSet rs1 = stmt.executeQuery("show  tables");
            while (rs1.next()) {
                allSchemas.add(rs1.getString(2));
            }
            rs.close();
            rs1.close();
        } catch (Exception ignore) {

        }
        return allSchemas;
    }
    /**
     * Adds catalog information to the metadata based on the provided parameters and DatabaseMetaData.
     *
     * @param parameters      		 JsonObject parameters for metadata retrieval(details about catalogs).
     * @param metadata        		 JsonObject to which the catalog information is added.
     * @param databaseMetaData 		 DatabaseMetaData object providing database-related metadata.
     */
    private void addCatalogs(@NotNull JsonObject parameters, @NotNull JsonObject metadata,
                             @NotNull DatabaseMetaData databaseMetaData) {
        if (parameters.has("fetchCatalogs") && "true".equalsIgnoreCase(parameters.get("fetchCatalogs").getAsString())) {
            metadata.add("catalogs", DatabaseDetails.newRetrieveCatalogs(databaseMetaData).getAsJsonArray
                    ("catalogs"));
        }
    }
    /**
     * Adds schema information to the metadata based on the provided parameters and DatabaseMetaData.
     *
     * @param parameters      		 JsonObject containing parameters for metadata retrieval such as fetchSchemas.
     * @param metadata        		 JsonObject to which the schema information is added.
     * @param databaseMetaData 		 DatabaseMetaData object providing database-related metadata.
     */
    private void addSchemas(@NotNull JsonObject parameters, @NotNull JsonObject metadata,
                            @NotNull DatabaseMetaData databaseMetaData) {
        if (parameters.has("fetchSchemas") && "true".equalsIgnoreCase(parameters.get("fetchSchemas").getAsString())) {
            GsonUtility.accumulate(metadata,"schemas", DatabaseDetails.newRetrieveSchemas(databaseMetaData).getAsJsonArray("schemas"));
        }
    }
    /**
     * Method checks the condition of fetch table and fetch column.
     * @param parameters       provides information about tables and columns.
     * @return {@code true} if object having fetchTable condition , {@code false} otherwise.
     */
    private boolean isColumnsRequested(@NotNull JsonObject parameters) {
        boolean isColumnsRequested;
        if (parameters.has("fetchTables") && "true".equalsIgnoreCase(parameters.get("fetchTables").getAsString())) {
            isColumnsRequested = false;
        } else if (parameters.has("fetchColumns") && "true".equalsIgnoreCase(parameters.get("fetchColumns").getAsString())) {
            isColumnsRequested = true;
        } else {
            throw new IllegalArgumentException("The parameter fetchTables or fetchColumns " + "is missing. Aborting " +
                    "metadata retrieval.");
        }
        return isColumnsRequested;
    }

    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} the component is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}