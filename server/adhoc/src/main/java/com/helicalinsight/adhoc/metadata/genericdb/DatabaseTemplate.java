package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.AbstractDatabaseTemplate;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Represents a template for interacting with databases and retrieving metadata.
 * This component provides methods to fetch database metadata such as tables, columns,
 * primary keys, and foreign keys.
 * 
 * Created by author on 26-06-2015.
 * @author Rajasekhar
 */
@Component
public class DatabaseTemplate extends AbstractDatabaseTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTemplate.class);

    @Autowired
    private RelationshipsTemplate relationshipsTemplate;

    @Autowired
    private PrimaryKeyTemplate primaryKeyTemplate;

    @Autowired
    private ForeignKeyTemplate foreignKeyTemplate;

    @Autowired
    private ColumnsTemplate columnsTemplate;
    /**
     * Retrieves the database metadata based on the provided connection and form data.
     *
     * @param connection 		 connection object provides catalog name and other info
     * @param formData           object containing the form data
     * @return the database metadata
     */
    public Database getDatabase(Connection connection, @NotNull JsonObject formData) {
        String selectedSchema = null;
        if (formData.has("selectedSchema")) {
            selectedSchema = formData.get("selectedSchema").getAsString();
        }
        long now = System.currentTimeMillis();
        long later;
        Database database = ApplicationContextAccessor.getBean(Database.class);
        try {
            String databaseName;
            String catalog = connection.getCatalog();
            if (selectedSchema == null) {
                databaseName = catalog;
            } else {
                if (catalog == null) {
                    databaseName = selectedSchema;
                } else {
                    databaseName = catalog + "." + selectedSchema;
                }
            }

            if (databaseName == null) {
                databaseName = "";
            }
            MetadataUtils.setCatalogAndSchemaNames(catalog, selectedSchema, database);
            database.setName(databaseName);
            Tables tables = getTables(connection, selectedSchema);
            Relationships relationships = this.relationshipsTemplate.getRelationships(tables);
            if (relationships.getListOfRelations().size() != 0) {
                database.setRelationships(relationships);
            }
            database.setTables(tables);
        } catch (SQLException ex) {
            throw new MetadataRetrievalException("Could not get DatabaseMetaData from connection", ex);
        }

        later = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("The Database object of the Metadata class is prepared successfully. " +
                    "And it took " + (later - now) + " milli seconds.");
        }
        return database;
    }
    /**
     * Retrieves the tables from the database.
     *
     * @param connection     	 database connection
     * @param selectedSchema 	 schema name
     * @return the list of tables
     */
    private Tables getTables(@NotNull Connection connection, String selectedSchema) {
        Tables tables = ApplicationContextAccessor.getBean(Tables.class);
        tables.setTableList(getListOfTables(connection, selectedSchema));
        return tables;
    }
    /**
     * Retrieves the list of tables from the database.
     *
     * @param connection     	 database connection  provides metadata object and catalog name
     * @param selectedSchema 	 selected schema
     * @return the list of tables
     */
    @NotNull
    private List<Table> getListOfTables(@NotNull Connection connection, String selectedSchema) {
        List<Table> tableList = new ArrayList<>();
        DatabaseMetaData databaseMetaData;
        try {
            databaseMetaData = connection.getMetaData();
            String catalog = connection.getCatalog();

            List<String> listOfTables = TableDetails.getListOfTables(databaseMetaData, catalog, selectedSchema);
            final File file = JsonUtils.defaultFunctionsFile();
            final Properties properties = ConfigurationFileReader.getPropertiesFromFile(file);

            for (String table : listOfTables) {
                Table actualTable = ApplicationContextAccessor.getBean(Table.class);
                actualTable.setId(UUID.randomUUID().toString());
                actualTable.setName(table);
                actualTable.setAliasName(table);

                if (logger.isDebugEnabled()) {
                    logger.debug("Retrieving table " + table + "'s column information.");
                }

                String columnInfoForTable = ColumnDetails.getColumnInfoForTable(databaseMetaData, table, catalog,
                        selectedSchema);

                JsonObject columnsJson = JsonParser.parseString(columnInfoForTable).getAsJsonObject();
                long now = System.currentTimeMillis();

                setColumns(actualTable, columnsJson, properties);

                setPrimaryKey(databaseMetaData, catalog, table, actualTable);

                setForeignKey(databaseMetaData, catalog, table, actualTable);

                long later = System.currentTimeMillis();

                if (logger.isDebugEnabled()) {
                    logger.debug("The table " + table + "'s columns, primary keys, " +
                            "foreign keys information is retrieved in " + (later - now) + " " +
                            "milli seconds");
                }

                tableList.add(actualTable);
            }
        } catch (SQLException ex) {
            throw new MetadataRetrievalException("Could not get DatabaseMetaData from connection", ex);
        }
        return tableList;
    }
    /**
     * Sets the columns for a table.
     *
     * @param actualTable 		 table object to set Column
     * @param columnsJson 		 JSON object containing column information
     * @param properties  		 properties object
     */
    private void setColumns(@NotNull Table actualTable, @NotNull JsonObject columnsJson, Properties properties) {
        Columns columns = getColumns(columnsJson, properties);
        List<Column> columnList = columns.getColumn();
        for (Column aColumn : columnList) {
            if (aColumn != null) {
                actualTable.setColumns(columns);
                break;
            }
        }
    }
    /**
     * Sets the primary key for a table.
     *
     * @param databaseMetaData 		 database metadata
     * @param catalog          		 catalog name
     * @param table            		 name of the table
     * @param actualTable      		 table object
     */
    private void setPrimaryKey(@NotNull DatabaseMetaData databaseMetaData, String catalog, String table,
                               @NotNull Table actualTable) {
        PrimaryKeys primaryKeys = getPrimaryKeys(databaseMetaData, table, catalog);
        List<PrimaryKey> keyList = primaryKeys.getPrimaryKey();
        for (PrimaryKey key : keyList) {
            if (key != null) {
                actualTable.setPrimaryKeys(primaryKeys);
                break;
            }
        }
    }
    /**
     * Sets the foreign keys for a table.
     *
     * @param databaseMetaData 		 database metadata
     * @param catalog          		 catalog name
     * @param table            		 name of the table
     * @param actualTable      		 table object
     */
    private void setForeignKey(@NotNull DatabaseMetaData databaseMetaData, String catalog, String table,
                               @NotNull Table actualTable) {
        ForeignKeys foreignKeys = getForeignKeys(databaseMetaData, table, catalog);
        List<ForeignKey> foreignKeyList = foreignKeys.getForeignKeyList();
        for (ForeignKey key : foreignKeyList) {
            if (key != null) {
                actualTable.setForeignKeys(foreignKeys);
                break;
            }
        }
    }
    /**
     * Retrieves the columns for a table from the database.
     *
     * @param columnsJson 		 JSON object containing column information
     * @param properties  		 properties object provides type column property
     * @return instance of {@code Columns}.
     */
    private Columns getColumns(@NotNull JsonObject columnsJson, Properties properties) {
        Columns columns = ApplicationContextAccessor.getBean(Columns.class);
        columns.setColumn(this.columnsTemplate.getColumnsList(columnsJson, properties));
        return columns;
    }
    /**
     * Retrieves the primary keys for a table from the database metadata.
     *
     * @param databaseMetaData 		databaseMetadata object provides primary Keys 
     * @param table            		name of the table
     * @param catalog          	    catalog name
     * @return {@code PrimaryKeys} object.
     */
    private PrimaryKeys getPrimaryKeys(@NotNull DatabaseMetaData databaseMetaData, String table, String catalog) {
        PrimaryKeys primaryKeys = ApplicationContextAccessor.getBean(PrimaryKeys.class);
        String primaryKeysString;
        try {
            primaryKeysString = PrimaryKeyDetails.getPrimaryKeys(databaseMetaData, table, null, catalog);
        } catch (SQLException ignore) {
            logger.error("Ignored Exception: ", ignore);
            primaryKeysString = "{}";
        }
        JsonObject primaryKeyInfo = JsonParser.parseString(primaryKeysString).getAsJsonObject();
        primaryKeys.setPrimaryKey(this.primaryKeyTemplate.getPrimaryKeyList(primaryKeyInfo));
        return primaryKeys;
    }
    /**
     * Retrieves the foreign keys for a table from the database metadata.
     *
     * @param databaseMetaData 		 databaseMetadata object provides foreign Keys 
     * @param table            		 name of the table
     * @param catalog          		 catalog name
     * @return {@code ForeignKeys} object
     */
    private ForeignKeys getForeignKeys(@NotNull DatabaseMetaData databaseMetaData, String table, String catalog) {
        String foreignKeysString;
        try {
            foreignKeysString = ForeignKeyDetails.getForeignKeys(databaseMetaData, table, null, catalog);
        } catch (SQLException ignore) {
            logger.error("Ignored Exception: ", ignore);
            foreignKeysString = "{}";
        }
        JsonObject foreignKeyInfo = JsonParser.parseString(foreignKeysString).getAsJsonObject();
        ForeignKeys foreignKeys = ApplicationContextAccessor.getBean(ForeignKeys.class);
        foreignKeys.setForeignKeyList(this.foreignKeyTemplate.getForeignKeyList(foreignKeyInfo));
        return foreignKeys;
    }
}