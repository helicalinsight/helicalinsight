package com.helicalinsight.adhoc.metadata;

import java.sql.DatabaseMetaData;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

/**
 * Date of Creation :19-02-2018 This class is used to retrieve columns data for
 * table in MultiThreading way.
 * It implements the Runnable interface to be used in a separate thread.
 *
 */
public class ColumnsRetrievalImpl implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger("ColumnsRetrievalImpl.class");
	private DatabaseMetaData databaseMetaDataValue;
	private String catalogValue;
	private String schemaValue;
	private List<String> assignedTablesValues;
	private JsonArray allTables;
	private String columnPattern;

	@Override
	public void run() {
		logger.debug("ColumnsRetrievalImpl Thread run method called");
		for (String table : this.assignedTablesValues) {
			addToAllTables(this.databaseMetaDataValue, table);
		}
	}

	

	public ColumnsRetrievalImpl(DatabaseMetaData databaseMetadata, String catalog, String schema,
			List<String> assignedTables, JsonArray allTables, String columnPattern) {

		this.databaseMetaDataValue = databaseMetadata;
		this.catalogValue = catalog;
		this.schemaValue = schema;
		this.assignedTablesValues = assignedTables;
		this.allTables = allTables;
		this.columnPattern = columnPattern;
	}
	 /**
     * Adds table columns to the JSON array of all tables.
     *
     * @param databaseMetaData   	 DatabaseMetaData object.
     * @param eachTable          	 name of the table.
     */
	private void addToAllTables(DatabaseMetaData databaseMetaData, String eachTable) {

		columnList(databaseMetaData, catalogValue, schemaValue, eachTable);
	}

	/**
     * Retrieves column information for a specific table and adds it to the JSON array of all tables.
     *
     * @param databaseMetaData   	 DatabaseMetaData object.
     * @param catalog            	 catalog name.
     * @param schema             	 schema name.
     * @param tableName          	 name of the table.
     */
	public void columnList(DatabaseMetaData databaseMetaData, String catalog, String schema, String tableName) {
		DbMetadataUtils.getColumnInfoForTable(databaseMetaData, catalog, schema, tableName, this.columnPattern,
				this.allTables);
	}
}
