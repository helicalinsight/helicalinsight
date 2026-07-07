package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * 
 * @Creation Date:09-02-2018
 *  This class provides the Database without using Connection this is
 *  the implementation of the WorkflowDatabaseTemplate.         
 */
@Component
public class WorkflowMongoTemplate extends WorkflowDatabaseTemplate {

	/**
     * Retrieves the database based on the provided form data.
     *
     * @param formData 	 		 object provides catalog and table information.
     * @return The Database object representing the retrieved database information.
     * @throws EfwServiceException If there is an error retrieving the database information.
     */
	public Database getDatabase(@NotNull JsonObject formData) {
		JsonObject formJson = getJsonObject(formData);

		JsonObject singleCatalog = formJson.getAsJsonObject("singleCatalog");

		String catalog = null;

		if (singleCatalog.has("catalog")) {
			catalog = singleCatalog.get("catalog").getAsString();
		}
		JsonArray schemas = singleCatalog.getAsJsonArray("schemas");

		if (schemas.size() < 1) {
			throw new EfwServiceException(
					"There is no selected schema information. Please select a catalog and/or " + "schema.");
		}
		String schema = null;
		JsonObject schemaJson = schemas.get(0).getAsJsonObject();
		if (schemaJson.has("name")) {
			schema = schemaJson.get("name").getAsString();
		}

		Database database = ApplicationContextAccessor.getBean(Database.class);
		Tables tables = ApplicationContextAccessor.getBean(Tables.class);

		// boolean multiThreadingEnabled =
		// MetadataMultiThreadingUtilities.isMultiThreadingEnabled();

		JsonArray tablesJson = schemaJson.getAsJsonArray("tables");
		List<Table> listOfTables = getListOfTables(catalog,schema,tablesJson);

		setDatabaseName(catalog, schema, database);
		MetadataUtils.setCatalogAndSchemaNames(catalog, schema, database);
		tables.setTableList(listOfTables);
		database.setTables(tables);
		return database;

	}

}
