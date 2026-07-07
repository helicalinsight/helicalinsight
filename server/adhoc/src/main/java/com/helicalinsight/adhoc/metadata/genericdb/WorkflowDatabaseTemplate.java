package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.AbstractDatabaseTemplate;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This WorkflowDatabaseTemplate component extends {@link AbstractDatabaseTemplate} and is responsible for fetching metadata
 * such as tables, columns, foreign keys, and primary keys from the database.
 *
 * It interacts with the database connection and schema information to retrieve metadata.
 * The class supports multithreading for improved performance when handling large datasets.
 *
 * 
 * Created by author on 27-06-2015.
 * @author Rajasekhar
 */
@Component
public class WorkflowDatabaseTemplate extends AbstractDatabaseTemplate {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowDatabaseTemplate.class);

    @Autowired
    private PrimaryKeyTemplate primaryKeyTemplate;
    @Autowired
    private ForeignKeyTemplate foreignKeyTemplate;
    @Autowired
    private ColumnsTemplate columnsTemplate;
    /**
     * Retrieves database metadata including tables, columns, foreign keys, and primary keys.
     * This method fetches metadata based on the provided database connection and form data.
     * 
     * @param connection 		 database connection  provides metadata.
     * @param formData   		 JSON object provides catalog, schema information.
     * @return {@code Database} object containing tables and related information.
     * @throws EfwServiceException If schema array size is zero .
     */
    public Database getDatabase(Connection connection, @NotNull JsonObject formData) {
        JsonObject formJson = getJsonObject(formData);

        JsonObject singleCatalog = formJson.getAsJsonObject("singleCatalog");

        String catalog = null;
        String schema = null;
        if (singleCatalog.has("catalog")) {
            catalog = GsonUtility.optString(singleCatalog, "catalog");
        }

        //Access the current schema and store its tables and columns, foreign and primary keys
        JsonArray schemas = singleCatalog.getAsJsonArray("schemas");

        if (schemas.size() < 1) {
            throw new EfwServiceException("There is no selected schema information. Please select a catalog and/or "
                    + "schema.");
        }

        JsonObject schemaJson = schemas.get(0).getAsJsonObject();
        if (schemaJson.has("name")) {
            schema = schemaJson.get("name").getAsString();
        }

        JsonArray tablesJson = schemaJson.getAsJsonArray("tables");

        Database database = ApplicationContextAccessor.getBean(Database.class);
        Tables tables = ApplicationContextAccessor.getBean(Tables.class);

        boolean multiThreadingEnabled = MetadataMultiThreadingUtilities.isMultiThreadingEnabled();

        List<Table> listOfTables = getListOfTables(catalog, schema, tablesJson);

        if (multiThreadingEnabled) {
            int size = listOfTables.size();

            List<Thread> threads = new ArrayList<>();

            final Boolean[] handlerFlag = {false};
            Thread.UncaughtExceptionHandler handler = MetadataMultiThreadingUtilities.getUncaughtExceptionHandler
                    (handlerFlag);

            int tableCount = MetadataMultiThreadingUtilities.getThreshold();

            int counter = 0;
            if (size > tableCount) {
                for (int start = 0; start < size; ) {
                    int end = start + tableCount;
                    if (end >= size) {
                        end = size;
                    }

                    if (handlerFlag[0]) {
                        break;
                    }

                    List<Table> list = listOfTables.subList(start, end);
                    Thread thread = startThread(formData, catalog, schema, handlerFlag, handler, counter, list);

                    threads.add(thread);
                    start = end;
                    counter++;
                }
            } else {
                Thread thread = startThread(formData, catalog, schema, handlerFlag, handler, counter, listOfTables);
                threads.add(thread);
            }

            MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);
        } else {
            java.sql.DatabaseMetaData metaData;
            try {
                metaData = connection.getMetaData();
            } catch (SQLException ex) {
                throw new MetadataRetrievalException(ex);
            }
            setConstraints(listOfTables, metaData, catalog, schema);
        }

        setDatabaseName(catalog, schema, database);
        MetadataUtils.setCatalogAndSchemaNames(catalog, schema, database);
        tables.setTableList(listOfTables);
        database.setTables(tables);
        return database;
    }

    /**
     * Retrieves a JSON object from the form data containing metadata information.
     * This method extracts relevant information and prepares it for further processing.
     *
     * @param formData 		 JSON object containing matadata and catalog info.
     * @return The JSON object with simplified metadata information.
     */
    protected JsonObject getJsonObject(JsonObject formData) {
        if (formData.has("metadata")) {
            JsonObject metadataJson = formData.getAsJsonObject("metadata");
            JsonArray catalogs = metadataJson.getAsJsonArray("catalogs");

            //Access the first catalog information.
            JsonObject catalogJson = new Gson().fromJson(catalogs.get(0),JsonObject.class);
            //Discard the unnecessary information
            formData.remove("metadata");
            formData.add("singleCatalog", catalogJson);
            return formData;
        } else {
            return formData;
        }
    }
    /**
     * Retrieves a list of tables with their columns from the JSON input.
     * This method parses the JSON object to extract table details and their corresponding columns.
     *
     * @param catalog     		 catalog name.
     * @param schema      		 schema name.
     * @param tablesJson  		 JSON array containing table details.
     * @return A list of table objects with associated columns.
     */
    @NotNull
    protected List<Table> getListOfTables(String catalog, String schema, @NotNull JsonArray tablesJson) {
        List<Table> tableList = new ArrayList<>();
        final File file = JsonUtils.defaultFunctionsFile();
        final Properties properties = ConfigurationFileReader.getPropertiesFromFile(file);
        for (JsonElement object : tablesJson) {
            JsonObject tableJson = object.getAsJsonObject();
            Table table = ApplicationContextAccessor.getBean(Table.class);

            String tableName = tableJson.get("name").getAsString();
            table.setId(MetadataUtils.getId(catalog, schema, tableName));
            table.setName(tableName);
            table.setAliasName(tableName);
            table.setColumns(setColumns(tableJson, properties));
            tableList.add(table);
        }
        return tableList;
    }
    /**
     * 
     * This method  starts a new thread for constraint retrieval to handle multithreading.
     *
     * @param formData     		 JSON object containing form data.
     * @param catalog      		 catalog name.
     * @param schema       		 schema name.
     * @param listOfTables 		 list of tables for which constraints need to be retrieved.
     * @param handler      		 uncaught exception handler for the thread.
     * @param counter      		 counter value for the thread.
     * @return The newly created thread for constraint retrieval.
     */
    private Thread startThread(JsonObject formData, String catalog, String schema, Boolean[] handlerFlag,
                               Thread.UncaughtExceptionHandler handler, int counter, List<Table> list) {
        Thread thread = null;
        try {
            thread = runAsSeparateThread(formData, catalog, schema, list, handler, counter);
        } catch (Exception ex) {
            logger.error("Exception: ", ex);
            handlerFlag[0] = true;
        }
        return thread;
    }
    /**
     * Sets constraints (foreign keys, primary keys) for the tables in the database metadata.
     * This method retrieves and sets foreign keys and primary keys for each table in the metadata.
     *
     * @param tableList 		 list of tables for which constraints need to be set.
     * @param metaData  		 database metadata object.
     * @param catalog   		 catalog name.
     * @param schema    		 schema name.
     */
    private void setConstraints(List<Table> tableList, java.sql.DatabaseMetaData metaData, String catalog,
                                String schema) {
        for (Table table : tableList) {
            String name = table.getName();
            table.setForeignKeys(RelationshipsRetrievalThread.setForeignKeys(metaData, catalog, schema, name,
                    this.foreignKeyTemplate));
            table.setPrimaryKeys(RelationshipsRetrievalThread.setPrimaryKeys(metaData, catalog, schema, name,
                    this.primaryKeyTemplate));
        }
    }
    /**
     * Sets the name of the database based on the catalog and schema information.
     * This method constructs the database name using the catalog and schema names.
     *
     * @param catalog  		 catalog name.
     * @param schema   		 schema name.
     * @param database 		 database object to set the name.
     */
    protected void setDatabaseName(@Nullable String catalog, @Nullable String schema, @NotNull Database database) {
        String databaseName;
        if (catalog != null && schema != null) {
            databaseName = catalog + "." + schema;
        } else if (catalog == null && schema == null) {
            databaseName = "";
        } else {
            if (catalog == null) {
                databaseName = schema;
            } else {
                databaseName = catalog;
            }
        }
        database.setName(databaseName);
        String dbId = database.getId();
        if(dbId==null){
            dbId = AdhocUtils.getUuid();
            database.setId(dbId);
        }
    }
    /**
     * Retrieves column information for a table from the JSON input.
     * This method parses the JSON object to extract column details for a given table.
     *
     * @param tableJson 		 JSON object containing table and column details.
     * @param properties 		 properties object containing column properties.
     * @return The columns object with associated column details.
     */
    private Columns setColumns(@NotNull JsonObject tableJson, Properties properties) {
        Columns columns = ApplicationContextAccessor.getBean(Columns.class);
        List<Column> columnsList = this.columnsTemplate.getColumnsList(tableJson, properties);
        columns.setColumn(columnsList);
        return columns;
    }
    /**
     * Initiates and runs a separate thread for retrieving constraints (foreign keys, primary keys) from the database.
     * This method creates and starts a new thread to handle the retrieval of constraints in a separate execution context.
     *
     * @param formData     		 JSON object containing form data.
     * @param catalog      		 catalog name.
     * @param schema       		 schema name.
     * @param listOfTables 		 list of tables for which constraints need to be retrieved.
     * @param handler      		 uncaught exception handler for the thread.
     * @param counter      		 counter value for the thread.
     * @return The newly created thread for constraint retrieval.
     */
    private Thread runAsSeparateThread(JsonObject formData, String catalog, String schema, List<Table> listOfTables,
                                       Thread.UncaughtExceptionHandler handler, int counter) {
        RelationshipsRetrievalThread relationshipsRetrievalThread = new RelationshipsRetrievalThread(formData,
                formData.get("type").getAsString(), catalog, schema, listOfTables, this.primaryKeyTemplate,
                this.foreignKeyTemplate);
        HIManagedThread thread = new HIManagedThread(relationshipsRetrievalThread);
        thread.setName("constraints-retrieval-thread:" + counter);
        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        if (logger.isInfoEnabled()) {
            logger.info("{} has started. ", thread.getName());
        }
        return thread;
    }


	
}
