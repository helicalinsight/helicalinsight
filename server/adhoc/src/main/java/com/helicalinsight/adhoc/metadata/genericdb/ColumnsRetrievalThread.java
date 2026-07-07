package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * The ColumnsRetrievalThread class is responsible for retrieving columns information for assigned tables in a separate
 * thread.
 * 
 * Created by author on 06-09-2015.
 * @author Rajasekhar
 * 
 */

public class ColumnsRetrievalThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ColumnsRetrievalThread.class);

    private final JsonObject formData;
    private final String connectionType;
    private final String catalog;
    private final String schema;
    private final List<String> assignedTables;
    private final JsonArray allTables;

    public ColumnsRetrievalThread(JsonObject formData, String connectionType, String catalog, String schema,
                                  List<String> assignedTables, JsonArray allTables) {
        this.formData = formData;
        this.connectionType = connectionType;
        this.catalog = catalog;
        this.schema = schema;
        this.assignedTables = assignedTables;
        this.allTables = allTables;
    }
    /**
     * Runs the columns retrieval process in a separate thread.
     */
    @Override
    public void run() {
        Connection connection = null;
        try {
            DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(this
                    .formData, this.connectionType);

            //noinspection ConstantConditions
            connection = driverConnection.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            for (String table : this.assignedTables) {
                addToAllTables(metaData, table);
            }
        } catch (Exception ex) {
            throw new MetadataRetrievalException(ex);
        } finally {
            logger.debug("Closing the connection created for {} ", Thread.currentThread().getName());
            DbUtils.closeQuietly(connection);
        }
    }
    /**
     * Adds columns information for a specified table to the JSON array containing all tables.
     *
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param eachTable        		 name of the table.
     */
    private void addToAllTables(@NotNull DatabaseMetaData databaseMetaData, String eachTable) {
        JsonObject singleTable = new JsonObject();
        singleTable.addProperty("name", eachTable);
        Object columns = columnList(databaseMetaData, this.catalog, this.schema, eachTable);
        if (columns instanceof JsonObject) {
            JsonArray array = new JsonArray();
            array.add((JsonObject)columns);
            singleTable.add("columns", array);
        } else {
            singleTable.add("columns", columns==null?new JsonArray():(JsonArray)columns);
        }
        this.allTables.add(singleTable);
    }
    /**
     * Retrieves column information for a specified table.
     *
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param catalog          		 catalog name.
     * @param schema           		 schema name.
     * @param tableName        		 table name.
     * @return The column information as a JsonObject or JsonArray.
     */
    public static Object columnList(@NotNull DatabaseMetaData databaseMetaData, String catalog, String schema,
                                    String tableName) {
        String columnInfoForTable = ColumnDetails.getColumnInfoForTable(databaseMetaData, tableName, catalog, schema);
        JsonObject columnsJson = new Gson().fromJson(columnInfoForTable,JsonObject.class);
        return columnsJson.get("columns");
    }
}