package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.adhoc.metadata.genericdb.*;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.serviceframework.IComponent;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * This class, `MetadataWorkflowComponent`, It implements the {@link IComponent} interface.
 * The primary purpose of this class is to retrieve metadata information based on provided form data parameters. The
 * information includes details about catalogs, schemas, and tables with their corresponding columns. The class supports
 * multi-threading for efficient metadata retrieval when dealing with a large number of tables.
 *
 *
 * @author Rajasekhar
 * @since 24-06-2015
 */
@SuppressWarnings("unused")
public class MetadataWorkflowComponent implements IComponent {

    private static final String NULL_VALUE = ApplicationProperties.getInstance().getNullValue();

    private static final Logger logger = LoggerFactory.getLogger(MetadataWorkflowComponent.class);

    /**
     * Returns metadata information based using formData parameters. The method helps
     * in getting catalogs, schemas.
     *
     * @param jsonFormData 			string containing the required parameters for metadata retrieval.
     * @return A JSON-formatted string containing the metadata information.
     * @throws EfwServiceException 	If there is an issue getting database metadata.
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        long now = System.currentTimeMillis();
        JsonObject formJson = new Gson().fromJson(jsonFormData,JsonObject.class);

        JsonObject parameters;
        if (formJson.has("parameters")) {
            if (formJson.get("parameters").isJsonNull() || formJson.getAsJsonObject("parameters").entrySet().isEmpty()) {
                throw new IncompleteFormDataException("The parameter 'parameters' is empty or null");
            }
            parameters = formJson.getAsJsonObject("parameters");
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

            addSchemas(parameters, metadata, databaseMetaData);

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
                    singleCatalog.addProperty("name", (catalog == null) ? NULL_VALUE : catalog.toString());
                    //Check if schemas are present. Else empty schemas to avoid NullPointer
                    if (schemas == null) {
                        throw new RequiredParameterIsNullException("The parameter schemas can't be empty.");
                    }

                    JsonArray allSchemas = new JsonArray();
                    if (!isColumnsRequested) {
                        addAllTables(databaseMetaData, allCatalogs, catalog, schemas, singleCatalog, allSchemas);
                    } else {
                        //Now columns are requested. So schemas should never be empty. The
                        //parameter tables must be an array.
                        boolean enableMultiThreading = MetadataMultiThreadingUtilities.isMultiThreadingEnabled();

                        if (!enableMultiThreading) {
                            for (Object aSchema : schemas) {
                                addToAllSchemas(databaseMetaData, catalog, allSchemas, aSchema);
                            }
                        } else {
                            //This is multi-threaded
                            for (Object aSchema : schemas) {
                                addToAllSchemas(catalog, allSchemas, aSchema, formJson, type, threshold);
                            }
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
    /**
     * Adds catalog information to the metadata based on the provided conditions.
     * @param parameters				provides condition to fetch Catalog
     * @param metadata					it adds catalog information
     * @param databaseMetaData			it helps in retrieving catalog info from database.	
     */
    private void addCatalogs(@NotNull JsonObject parameters, @NotNull JsonObject metadata,
                             @NotNull DatabaseMetaData databaseMetaData) {
        if (parameters.has("fetchCatalogs") && "true".equalsIgnoreCase(parameters.get("fetchCatalogs").getAsString())) {
            metadata.add("catalogs", DatabaseDetails.newRetrieveCatalogs(databaseMetaData).getAsJsonArray
                    ("catalogs"));
        }
    }
    /**
     * Adds schema information to the metadata based on the provided conditions.
     *
     * @param parameters      	JsonObject providing conditions to fetch schemas.
     * @param metadata        	JsonObject to which schema information is added.
     * @param databaseMetaData  DatabaseMetaData object used for retrieving schema information from the database.
     */
    private void addSchemas(@NotNull JsonObject parameters, @NotNull JsonObject metadata,
                            @NotNull DatabaseMetaData databaseMetaData) {
        Boolean insideIf = false;
        if (parameters.has("fetchSchemas") && "true".equalsIgnoreCase(parameters.get("fetchSchemas").getAsString())) {
            if (parameters.has("view") && "tree".equalsIgnoreCase(parameters.get("view").getAsString())) {
                JsonArray catalogs = metadata.getAsJsonArray("catalogs");
                JsonArray schemasTreeView = DatabaseDetails.retrieveSchemasTreeView(databaseMetaData, catalogs);
                metadata.add("catalogs", schemasTreeView);
                return;
            }
            metadata.add("schemas", DatabaseDetails.newRetrieveSchemas(databaseMetaData).getAsJsonArray("schemas"));
        }
    }

    /**
     * Method Checks if columns are requested based on the provided conditions.
     *
     * @param parameters 			     JsonObject providing conditions for fetching tables and columns.
     * @return {@code true} if columns are requested, {@code false} otherwise.
     * @throws IllegalArgumentException  If the required parameters are missing, such as fetchTables or fetchColumns.
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
     * Adds all tables to the metadata for the specified catalog and schemas.
     *
     * @param databaseMetaData 		 DatabaseMetaData object to retrieve metadata.
     * @param allCatalogs      		 JsonArray to store all catalogs' information.
     * @param catalog          		 catalog for which tables need to be fetched.
     * @param schemas          		 JsonArray containing schemas for which tables need to be fetched.
     * @param singleCatalog    		 JsonObject to store information for a single catalog.
     * @param allSchemas       		 JsonArray to store information for all schemas.
     */
    private void addAllTables(@NotNull DatabaseMetaData databaseMetaData, @NotNull JsonArray allCatalogs,
                              String catalog, @NotNull JsonArray schemas, @NotNull JsonObject singleCatalog,
                              @NotNull JsonArray allSchemas) {
        //Tables are requested. So due to null schemas
        String schema;
        if (!schemas.isEmpty()) {
            for (Object aSchema : schemas) {
                JsonObject schemaJson = new Gson().fromJson((JsonObject)aSchema , JsonObject.class);
                if (schemaJson.has("name")) {
                    schema = schemaJson.get("name").getAsString();
                } else {
                    schema = null;
                }
                List<String> tables = tablesList(databaseMetaData, catalog, schema);
                JsonObject singleSchema = new JsonObject();
                singleSchema.addProperty("name", ((schema == null) ? NULL_VALUE : schema.toString()));
                singleSchema.add("tables", new Gson().fromJson(tables.toString(),JsonArray.class));
                allSchemas.add(singleSchema);
            }
        } else {
            JsonObject singleSchema = new JsonObject();
            singleSchema.addProperty("name", NULL_VALUE);
            JsonArray fromJson = new Gson().fromJson(tablesList(databaseMetaData, catalog, null).toString(),JsonArray.class);
            singleSchema.add("tables", fromJson);
            allSchemas.add(singleSchema);
        }
        singleCatalog.add("schemas", allSchemas);
        allCatalogs.add(singleCatalog);
    }
    /**
     * Adds a schema with tables to the metadata for the specified catalog.
     *
     * @param databaseMetaData 		 DatabaseMetaData object to retrieve metadata.
     * @param catalog          		 catalog for which tables need to be fetched.
     * @param allSchemas       		 JsonArray to store information for all schemas.
     * @param aSchema          		 schema object to be added to all schemas.
     */
    private void addToAllSchemas(@NotNull DatabaseMetaData databaseMetaData, String catalog,
                                 @NotNull JsonArray allSchemas, Object aSchema) {
        String schema;
        JsonObject schemaJson = new Gson().fromJson((JsonObject)aSchema,JsonObject.class);
        if (schemaJson.has("name")) {
            schema = schemaJson.get("name").getAsString();
        } else {
            schema = null;
        }

        JsonArray tables = schemaJson.getAsJsonArray("tables");
        JsonObject singleSchema = new JsonObject();

        singleSchema.addProperty("name", ((schema == null) ? NULL_VALUE : schema.toString()));

        JsonArray allTables = new JsonArray();

        for (JsonElement eachTable : tables) {
            addToAllTables(databaseMetaData, eachTable.getAsString(), catalog, schema, allTables);
        }

        singleSchema.add("tables", allTables);
        allSchemas.add(singleSchema);
    }
    /**
     * Adds schemas with tables to the metadata for the specified catalog using multiple threads.
     *
     * @param catalog     		 catalog for which tables need to be fetched.
     * @param allSchemas  		 JsonArray to store information for all schemas.
     * @param aSchema     		 schema object to be added to all schemas.
     * @param formJson    		 JsonObject containing the form data.
     * @param type        		 type of connection.
     * @param tableCount  		 maximum number of tables to be processed in each thread.
     */
    private void addToAllSchemas(String catalog, @NotNull JsonArray allSchemas, Object aSchema, JsonObject formJson,
                                 String type, Integer tableCount) {
        String schema;
        JsonObject schemaJson = new Gson().fromJson((JsonObject)aSchema,JsonObject.class);
        if (schemaJson.has("name")) {
            schema = schemaJson.get("name").getAsString();
        } else {
            schema = null;
        }

        JsonArray tables = schemaJson.getAsJsonArray("tables");

        JsonObject singleSchema = new JsonObject();
        singleSchema.addProperty("name", ((schema == null) ? NULL_VALUE : schema));

        JsonArray allTables = new JsonArray();
        //Add all the columns details to each table in multiple threads.
        int size = tables.size();

        List<Thread> threads = new ArrayList<>();

        final Boolean[] handlerFlag = {false};
        Thread.UncaughtExceptionHandler handler = MetadataMultiThreadingUtilities.getUncaughtExceptionHandler
                (handlerFlag);

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

                List list = new ArrayList<>();
                for (int i = start; i <= end; i++) {
                	list.add(tables.get(i).getAsString());
            
                }
                
                Thread thread = start(catalog, formJson, type, schema, allTables, handlerFlag, handler, counter, list);

                threads.add(thread);
                start = end;
                counter++;
            }
        } else {
        	 List list = new ArrayList<>();
        	 for (JsonElement element : tables) {
        		 list.add(element.getAsString());
        	 }
            Thread thread = start(catalog, formJson, type, schema, allTables, handlerFlag, handler, counter, list);
            threads.add(thread);
        }

        MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);

        singleSchema.add("tables", allTables);
        allSchemas.add(singleSchema);
    }
    /**
     * Retrieves a list of tables for the specified catalog and schema from the database metadata.
     *
     * @param databaseMetaData 		 DatabaseMetaData object to retrieve table details.
     * @param catalog           	 catalog for which tables need to be fetched.
     * @param schema            	 schema for which tables need to be fetched.
     * @return A List of table names.
     */
    private List<String> tablesList(@NotNull DatabaseMetaData databaseMetaData, String catalog, String schema) {
        return TableDetails.getListOfTables(databaseMetaData, catalog, schema);
    }
    /**
     * Adds details of a table, including its columns, to the list of tables.
     *
     * @param databaseMetaData 		 DatabaseMetaData object to retrieve column details.
     * @param eachTable        		 name of the table for which details are added.
     * @param catalog           	 catalog to which the table belongs.
     * @param schema            	 schema to which the table belongs.
     * @param allTables         	 JsonArray containing details of all tables.
     */
    private void addToAllTables(@NotNull DatabaseMetaData databaseMetaData, String eachTable, String catalog,
                                String schema, @NotNull JsonArray allTables) {
        JsonObject singleTable = new JsonObject();
        singleTable.addProperty("name", eachTable);
        Object columns = ColumnsRetrievalThread.columnList(databaseMetaData, catalog, schema, eachTable);
        if (columns instanceof JsonObject) {
            JsonArray array = new JsonArray();
            JsonArray arrJson = new JsonArray();
            arrJson.add((JsonObject)columns);
            array.addAll(arrJson);
            singleTable.add("columns", array);
        } else {
            singleTable.add("columns", (JsonArray)columns);
        }
        allTables.add(singleTable);
    }
    /**
     * Starts a new thread to retrieve columns for multiple tables and adds them to the list of tables.
     *
     * @param catalog      		 catalog to which the tables belong.
     * @param formJson     		 JsonObject containing form data.
     * @param type         		 type of the connection.
     * @param schema       		 schema to which the tables belong.
     * @param allTables    		 JsonArray containing details of all tables.
     * @param handlerFlag  		 array indicating whether an exception occurred in any thread.
     * @param handler      		 UncaughtExceptionHandler for handling exceptions in threads.
     * @param counter      		 counter indicating the thread's position.
     * @param list         		 list of tables for which columns are to be retrieved.
     * @return The thread started for retrieving columns.
     */
    private Thread start(String catalog, JsonObject formJson, String type, String schema, JsonArray allTables,
                         Boolean[] handlerFlag, Thread.UncaughtExceptionHandler handler, int counter, List list) {
        Thread thread = null;
        try {
            thread = runAsSeparateThread(catalog, formJson, type, schema, list, allTables, counter, handler);
        } catch (Exception ex) {
            logger.error("Exception: ", ex);
            handlerFlag[0] = true;
        }
        return thread;
    }
    /**
     * Runs the column retrieval process as a separate thread.
     *
     * @param catalog         		 catalog to which the tables belong.
     * @param formJson        		 JsonObject containing form data.
     * @param type            		 type of the connection.
     * @param schema          		 schema to which the tables belong.
     * @param tables          		 list of tables for which columns are to be retrieved.
     * @param allTables       		 JsonArray containing details of all tables.
     * @param threadNumber    		 thread number indicating the position of the thread.
     * @param handler         		 UncaughtExceptionHandler for handling exceptions in the thread.
     * @return The thread running the column retrieval process.
     */
    @NotNull
    private Thread runAsSeparateThread(String catalog, JsonObject formJson, String type, String schema,
                                       @NotNull List tables, JsonArray allTables, int threadNumber,
                                       Thread.UncaughtExceptionHandler handler) {
        List<String> assignedTables = new ArrayList<>();
        for (Object object : tables) {
            assignedTables.add((String) object);
        }
        try {
            ColumnsRetrievalThread retrievalThread = new ColumnsRetrievalThread(formJson, type, catalog, schema,
                    assignedTables, allTables);
            HIManagedThread columnsThread = new HIManagedThread(retrievalThread);
            columnsThread.setName("columns-retrieval-thread:" + threadNumber);
            columnsThread.setUncaughtExceptionHandler(handler);
            columnsThread.start();
            return columnsThread;
        } catch (Exception ex) {
            throw new MetadataRetrievalException(ex);
        }
    }
    /**
     * Indicates whether this component is thread-safe to be cached.
     * @return {@code true} if the component is thread-safe to be cached.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}