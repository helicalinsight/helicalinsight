package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.adhoc.metadata.genericdb.WorkflowDatabaseTemplate;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Component class for combining metadata from multiple catalogs and schemas into a single database,
 * utilizing WorkflowDatabaseTemplate.
 * Created by user on 5/16/2016.
 * @author Rajasekhar
 */
@Component
public class CalciteDatabaseTemplate extends AbstractDatabaseTemplate {

    @Autowired
    private WorkflowDatabaseTemplate workflowDatabaseTemplate;
    /**
     * Retrieves the database metadata by combining information from multiple catalogs and schemas.
     *
     * @param connection 		 connection to get database.
     * @param formData   		 JSON object containing metadata information such as catalogs, schemas etc.
     * @return combined database metadata.
     * @throws MetadataRetrievalException If metadata retrieval fails.
     */
    @Override
    public Database getDatabase(Connection connection, @NotNull JsonObject formData) {
        if (!formData.has("metadata")) {
            throw new MetadataRetrievalException("Couldn't retrieve metadata as the request does not have " +
                    "parameter called 'metadata'");
        }

        JsonObject metadataJson = formData.getAsJsonObject("metadata");
        JsonArray catalogs;
        try {
            catalogs = metadataJson.getAsJsonArray("catalogs");
        } catch (Exception ex) {
            throw new MetadataRetrievalException("Catalogs is not a json array.", ex);
        }

        formData.remove("metadata");

        List<Database> databases = new ArrayList<>();

        for (Object object : catalogs) {
            if (!(object instanceof JsonObject)) {
                throw new MetadataRetrievalException("Catalog is not a json array.");
            } else {
                JsonObject catalogJson = (JsonObject) object;
                JsonArray schemas = catalogJson.getAsJsonArray("schemas");
                catalogJson.remove("schemas");

                for (Object schema : schemas) {
                    JsonArray oneSchemaArray = new JsonArray();
                    oneSchemaArray.add((JsonArray)schema);
                    catalogJson.add("schemas", oneSchemaArray);
                    formData.add("singleCatalog", catalogJson);

                    Database database = this.workflowDatabaseTemplate.getDatabase(connection, formData);
                    databases.add(database);

                    formData.remove("singleCatalog");
                    catalogJson.remove("schemas");
                }
            }
        }

        return combineDatabases(databases);
    }
    /**
     * Combines multiple databases into a single database.
     *
     * @param databases 	 list of databases to combine.
     * @return The combined database.
     */
    private Database combineDatabases(List<Database> databases) {
        if (databases.size() == 1) {
            Database database = databases.get(0);
            createBaseDatabase(database);
            return database;
        } else {
            //Take first one and process it, also remove from list
            Database baseDatabase = databases.remove(0);
            if (baseDatabase == null) {
                baseDatabase = ApplicationContextAccessor.getBean(Database.class);
            }

            createBaseDatabase(baseDatabase);
            //Now the base database is free from NPE

            //Don't be afraid of NPE
            Tables databaseTables = baseDatabase.getTables();

            //Don't be afraid of NPE
            List<Table> list = databaseTables.getTableList();

            for (Database item : databases) {
                processRemainingDatabases(item, list);
            }

            return baseDatabase;
        }
    }
    /**
     * Creates a base database with default values and empty name.
     *
     * @param database 	 database to provide name , tables.
     */
    //To avoid NPE create a safe base database
    private void createBaseDatabase(Database database) {
        String name = database.getName();
        if (name == null) {
            name = "";
        }
        //Clear name and set it as empty
        database.setName("");
        Tables tables = database.getTables();
        if (tables == null) {
            tables = ApplicationContextAccessor.getBean(Tables.class);
        }

        List<Table> tableList = tables.getTableList();
        if (tableList == null) {
            tableList = new ArrayList<>();
        }

        for (Table table : tableList) {
            editTableName(name, table);
        }
    }
    /**
     * Processes the remaining databases by adding their tables to the base database.
     * @param database 		 database provides tables, name.
     * @param list     		 list of tables in the base database.
     */
    private void processRemainingDatabases(Database database, List<Table> list) {
        if (database == null) {
            return;
        }

        String name = database.getName();
        if (name == null) {
            name = "";
        }
        //Clear name and set it as empty
        database.setName("");
        Tables tables = database.getTables();
        if (tables == null) {
            return;
        }

        List<Table> tableList = tables.getTableList();
        if (tableList == null) {
            return;
        }

        for (Table table : tableList) {
            editTableName(name, table);
        }

        list.addAll(tableList);
    }
    /**
     * Modifies the table name by prefixing it with the catalog name.
     *
     * @param name  		 catalog name.
     * @param table 		 table to modify.
     */
    private void editTableName(String name, Table table) {
        String tableName = table.getName();
        table.setName(name + "." + tableName);
    }
}
