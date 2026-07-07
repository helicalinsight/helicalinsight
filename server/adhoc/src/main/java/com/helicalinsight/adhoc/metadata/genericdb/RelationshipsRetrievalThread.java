package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;

/**
 * The RelationshipsRetrievalThread class is responsible for retrieving relationships (foreign keys and primary keys)
 * for assigned tables in a separate thread. It implements the {@link Runnable} interface.
 * 
 * Created by author on 07-09-2015.
 * @author Rajasekhar
 */
public class RelationshipsRetrievalThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipsRetrievalThread.class);
    private final JsonObject formData;
    private final String connectionType;
    private final String catalog;
    private final String schema;
    private final List<Table> assignedTables;
    private final PrimaryKeyTemplate primaryKeyTemplate;
    private final ForeignKeyTemplate foreignKeyTemplate;

    public RelationshipsRetrievalThread(JsonObject formData, String connectionType, String catalog, String schema,
                                        List<Table> assignedTables, PrimaryKeyTemplate primaryKeyTemplate,
                                        ForeignKeyTemplate foreignKeyTemplate) {
        this.formData = formData;
        this.connectionType = connectionType;
        this.catalog = catalog;
        this.schema = schema;
        this.assignedTables = assignedTables;
        this.primaryKeyTemplate = primaryKeyTemplate;
        this.foreignKeyTemplate = foreignKeyTemplate;
    }
    /**
     * Executes the thread to retrieve relationships for assigned tables.
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
            for (Table table : this.assignedTables) {
                String name = table.getName();
                table.setForeignKeys(setForeignKeys(metaData, this.catalog, this.schema, name,
                        this.foreignKeyTemplate));
                table.setPrimaryKeys(setPrimaryKeys(metaData, this.catalog, this.schema, name,
                        this.primaryKeyTemplate));
            }
        } catch (Exception ex) {
            logger.error("Exception: ", ex);
            throw new MetadataRetrievalException(ex);
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("Closing the connection created for {} ", Thread.currentThread().getName());
            }
            DbUtils.closeQuietly(connection);
        }
    }
    /**
     * Retrieves foreign keys for a specific table.
     *
     * @param metaData          		 database metadata.
     * @param catalog           		 catalog name.
     * @param schema            		 schema name.
     * @param table             		 table name.
     * @param foreignKeyTemplate 		 foreign key template.
     * @return The foreign keys for the specified table.
     */
    static ForeignKeys setForeignKeys(@NotNull DatabaseMetaData metaData, String catalog, String schema,
                                      String table, ForeignKeyTemplate foreignKeyTemplate) {
        ForeignKeys foreignKeys = ApplicationContextAccessor.getBean(ForeignKeys.class);
        String foreignKeysString = "{}"; 
        try {
            foreignKeysString = ForeignKeyDetails.getForeignKeys(metaData, table, schema, catalog);
        } catch (SQLFeatureNotSupportedException e) {
            // Handle the case where foreign key retrieval is not supported by the database
            logger.warn("Foreign key retrieval is not supported for table '{}' in this database.", table);
        } catch (SQLException e) {
            // Handle general SQL exceptions with detailed logging
            logger.error("SQL Exception while retrieving foreign keys for table '{}': ", table);
            // In case of any other SQL exception, fallback to empty JSON string
            foreignKeysString = "{}";
        }
        JsonObject foreignKeyJson = JsonParser.parseString(foreignKeysString).getAsJsonObject();
        List<ForeignKey> foreignKeyList = foreignKeyTemplate.getForeignKeyList(foreignKeyJson);
        foreignKeys.setForeignKeyList(foreignKeyList);
        return foreignKeys;
    }
    /**
     * Retrieves primary keys for a specific table.
     *
     * @param metaData           		 database metadata.
     * @param catalog            		 catalog name.
     * @param schema             		 schema name.
     * @param table              		 table name.
     * @param primaryKeyTemplate 		 primary key template.
     * @return {@code PrimaryKeys} instance with list of primaryKeys.
     */
    static PrimaryKeys setPrimaryKeys(@NotNull DatabaseMetaData metaData, String catalog, String schema,
                                      String table, PrimaryKeyTemplate primaryKeyTemplate) {
        PrimaryKeys primaryKeys = ApplicationContextAccessor.getBean(PrimaryKeys.class);

        String primaryKeysString;
        try {
            primaryKeysString = PrimaryKeyDetails.getPrimaryKeys(metaData, table, schema, catalog);
        } catch (SQLException ignore) {
            logger.error("Ignored Exception: ", ignore);
            primaryKeysString = "{}";
        }
        JsonObject primaryKeyJson = JsonParser.parseString(primaryKeysString).getAsJsonObject();
        List<PrimaryKey> primaryKeyList = primaryKeyTemplate.getPrimaryKeyList(primaryKeyJson);
        primaryKeys.setPrimaryKey(primaryKeyList);
        return primaryKeys;
    }
}
