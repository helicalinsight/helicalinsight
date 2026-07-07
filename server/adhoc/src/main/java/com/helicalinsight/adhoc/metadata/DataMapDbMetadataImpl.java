package com.helicalinsight.adhoc.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.helicalinsight.efw.HIManagedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataMultiThreadingUtilities;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.IDataMap;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * This class is the one of the implementation of the IDataMap this class is
 * used to handle database Metadata with out using any "%".
 * Date of Creation:19-02-2018
 */
public class DataMapDbMetadataImpl implements IDataMap {
	private static final Logger logger = LoggerFactory.getLogger(DataMapDbMetadataImpl.class);

	/**
     * Retrieves the result in JSONObject format.
     *
     * @param dataMapTagContent 			 data map tag content.
     * @param queryParameters   			 query parameters.
     * @param con               			 database connection.
     * @return The result in JSONObject format.
     */
	@Override
	public JsonObject getResultSet(JsonObject dataMapTagContent, JsonObject queryParameters, Connection con) {
		logger.debug("dataMapTagContent   :" + dataMapTagContent);
		logger.debug("queryParameters    :" + queryParameters);

		EfwdQueryProcessor queryProcessor = ApplicationContextAccessor.getBean(EfwdQueryProcessor.class);
		String groovyScript = queryProcessor.getQuery(dataMapTagContent, queryParameters);
		JsonObject processedQueryJSONObject = new Gson().fromJson(groovyScript,JsonObject.class);
		int mapId = dataMapTagContent.get("id").getAsInt();
		try {

			JsonObject metadata = new JsonObject();

			DatabaseMetaData databaseMetaData = con.getMetaData();

			if (mapId == 1) {
				logger.debug("processedQueryJSONObject   :" + processedQueryJSONObject);
				String catalog = GsonUtility.optString(dataMapTagContent, "catalog");
				return addCatalogs(queryParameters, metadata, databaseMetaData, catalog);
			}
			if (mapId == 2) {
				logger.debug("processedQueryJSONObject   :" + processedQueryJSONObject);
				String schema = GsonUtility.optString(dataMapTagContent,"schema");
				return addSchemas(queryParameters, metadata, databaseMetaData, schema);
			}
			if (mapId == 3) {
				logger.debug("processedQueryJSONObject   :" + processedQueryJSONObject);
				String catalog = processedQueryJSONObject.get("catalog").getAsString();
				String schema = processedQueryJSONObject.get("schema").getAsString();
				JsonArray tables = processedQueryJSONObject.getAsJsonArray("table");
				String tableNamePattern = processedQueryJSONObject.get("tablePattern").getAsString();
				return tablesList(queryParameters, tableNamePattern, databaseMetaData, catalog, schema, tables);

			}
			if (mapId == 4) {
				logger.debug("processedQueryJSONObject   :" + processedQueryJSONObject);
				JsonElement el=processedQueryJSONObject.get("catalog");
				String catalog = el.isJsonArray()?el.toString():el.getAsString();
				String schema = processedQueryJSONObject.get("schema").getAsString();
				JsonArray tables = processedQueryJSONObject.getAsJsonArray("table");
				String columnPattern = processedQueryJSONObject.get("columnPattern").getAsString();
if(catalog!=null&& catalog!="" && !catalog.startsWith("[")) catalog="["+catalog+"]";
if(schema!=null&& schema!="" && !schema.startsWith("[")) schema="["+schema+"]";

				boolean enableMultiThreading = MetadataMultiThreadingUtilities.isMultiThreadingEnabled();
				JsonArray allTables = new JsonArray();
				logger.debug("is MultiThreading enables  :" + enableMultiThreading);
				if (!enableMultiThreading) {

					for (JsonElement object : tables) {
						logger.debug("*MULTI THREADING DISABLED*");
						String tableName =  object.getAsString();
						DbMetadataUtils.getColumnInfoForTable(databaseMetaData, catalog, schema, tableName,
								columnPattern, allTables);
					}

				} else {
					getColumnsInConcurrent(databaseMetaData, catalog, schema, tables, columnPattern, allTables);
				}
				JsonObject dataJSONObject = new JsonObject();
				dataJSONObject.add("data", allTables);
				return dataJSONObject;

			}

		} catch (Exception ex) {
			throw new EfwServiceException("Couldn't get database metadata.", ex);

		}

		return null;
	}

	/**
	 * this method will retrieve columns in MultiThreading way. this method will
	 * invoke only when MultiThreading is enabled in settings.xml
	 * 
	 * @param databaseMetaData				DatabaseMetaData object. 		
	 * @param catalog						catalog name.
	 * @param schema						schema name.
	 * @param tables						list of tables
	 * @param columnPattern					pattern for column names.
	 * @param allTables						JSON array containing all tables
	 */
	private void getColumnsInConcurrent(DatabaseMetaData databaseMetaData, String catalog, String schema,
			JsonArray tables, String columnPattern, JsonArray allTables) {
		Integer threshold = MetadataMultiThreadingUtilities.getThreshold();

		int size = tables.size();
		List<Thread> threads = new ArrayList<>();

		final Boolean[] handlerFlag = { false };
		Thread.UncaughtExceptionHandler handler = MetadataMultiThreadingUtilities
				.getUncaughtExceptionHandler(handlerFlag);

		int counter = 0;
		if (size > threshold) {
			for (int start = 0; start < size;) {
				int end = start + threshold;
				if (end >= size) {
					end = size;
				}

				if (handlerFlag[0]) {
					break;
				}
				
				List list = new ArrayList<String>();
			    
			    for (int i = start; i <= end; i++) {
			        String element = tables.get(i).toString();
			        list.add(element);
			    }
//				List list = tables.subList(start, end);
				
				Thread thread = start(databaseMetaData, catalog, schema, allTables, handlerFlag, handler, counter, list,
						columnPattern);

				threads.add(thread);
				start = end;
				counter++;
			}
		} else {
			List list = new ArrayList<String>();
		    
		    for (int i = 0; i < tables.size(); i++) {
		        String element = tables.get(i).toString();
		        list.add(element);
		    }
			Thread thread = start(databaseMetaData, catalog, schema, allTables, handlerFlag, handler, counter, list,
					columnPattern);
			threads.add(thread);

		}
		MetadataMultiThreadingUtilities.pauseThreads(threads, handlerFlag);
	}

	/**
     * Adds catalogs to the metadata.
     *
     * @param queryParameters  		 query parameters.
     * @param metadata         		 metadata JSON object.
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param catalog          		 catalog name.
     * @return The metadata JSON object with added catalogs.
     */
	private JsonObject addCatalogs(JsonObject queryParameters, JsonObject metadata, DatabaseMetaData databaseMetaData,
			String catalog) {
		JsonObject dataObjects = new JsonObject();
		JsonArray dataArray = new JsonArray();
		List<String> retrieveCatalogs = DbMetadataUtils.retrieveCatalogs(databaseMetaData, catalog);

		for (String collectionName : retrieveCatalogs) {
			JsonObject dataObject = new JsonObject();
			dataObject.addProperty("catalogs", collectionName);
			dataArray.add(dataObject);
		}
		dataObjects.add("data", dataArray);
		return dataObjects;
	}

	/**
     * Adds schemas to the metadata.
     *
     * @param queryParameters  		 query parameters.
     * @param metadata         		 metadata JSON object.
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param schema           		 schema name.
     * @return The metadata JSON object with added schemas.
     */
	private JsonObject addSchemas(JsonObject queryParameters, JsonObject metadata, DatabaseMetaData databaseMetaData,
			String schema) {
		JsonObject dataObjects = new JsonObject();
		JsonArray dataArray = new JsonArray();
		List<String> schemas = DbMetadataUtils.retrieveSchemas(databaseMetaData, schema);
		for (String collectionName : schemas) {
			JsonObject dataObject = new JsonObject();
			
			dataObject.addProperty("schemas", collectionName);
			dataArray.add(dataObject);

		}
		dataObjects.add("data", dataArray);
		return dataObjects;
	}

	/**
     * Retrieves table list from the metadata.
     *
     * @param queryParameters 		 query parameters.
     * @param schemaPattern   		 schema pattern.
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param catalog          		 catalog name.
     * @param schema           		 schema name.
     * @param tables           		 JSON array of tables.
     * @return The metadata JSON object with added tables.
     */
	private JsonObject tablesList(JsonObject queryParameters, String schemaPattern, DatabaseMetaData databaseMetaData,
			String catalog, String schema, JsonArray tables) {
		JsonObject dataObjects = new JsonObject();
		JsonArray dataArray = new JsonArray();
		List<String> listOfTables = DbMetadataUtils.getListOfTables(databaseMetaData, schemaPattern, catalog, schema,
				tables);

		for (String tableName : listOfTables) {

			JsonObject dataObject = new JsonObject();
			dataObject.addProperty("tables", tableName);
			dataObject.addProperty("schemas", schema);
			dataObject.addProperty("catalogs", catalog);
			dataArray.add(dataObject);

		}
		dataObjects.add("data", dataArray);
		return dataObjects;

	}

	/**
     * Starts a separate thread for metadata retrieval.
     *
     * @param databaseMetaData  	DatabaseMetaData object.
     * @param catalog           	catalog name.
     * @param schema            	schema name.
     * @param tables            	list of tables.
     * @param allTables         	JSON array containing all tables.
     * @param handlerFlag       	flag to handle exceptions.
     * @param handler           	exception handler.
     * @param counter           	counter for thread identification.
     * @param list              	list of tables.
     * @param columnPattern     	pattern for column names.
     * @return  started thread.
     */
	private Thread start(DatabaseMetaData databaseMetaData, String catalog, String schema, JsonArray allTables,
			Boolean[] handlerFlag, Thread.UncaughtExceptionHandler handler, int counter, List list,
			String columnPattern) {
		Thread thread = null;
		try {
			thread = runAsSeparateThread(databaseMetaData, catalog, schema, list, allTables, counter, handler,
					columnPattern);
		} catch (Exception ex) {
			logger.error("Exception: ", ex);
			handlerFlag[0] = true;
		}
		return thread;
	}

	/**
     * Runs metadata retrieval as a separate thread.
     *
     * @param databaseMetaData 		 DatabaseMetaData object.
     * @param catalog          		 catalog name.
     * @param schema           		 schema name.
     * @param tables            	 list of tables.
     * @param allTables         	 JSON array containing all tables.
     * @param threadNumber      	 thread number.
     * @param handler                exception handler.
     * @param columnPattern     	 pattern for column names.
     * @return  started thread.
     */
	private Thread runAsSeparateThread(DatabaseMetaData databaseMetaData, String catalog, String schema, List tables,
			JsonArray allTables, int threadNumber, Thread.UncaughtExceptionHandler handler, String columnPattern) {
		List<String> assignedTables = new ArrayList<>();
		for (Object object : tables) {
			assignedTables.add((String) object);
		}
		try {
			ColumnsRetrievalImpl retrievalThread = new ColumnsRetrievalImpl(databaseMetaData, catalog, schema,
					assignedTables, allTables, columnPattern);

            HIManagedThread columnsThread = new HIManagedThread(retrievalThread);
			columnsThread.setName("columns-retrieval-thread:" + threadNumber);
			columnsThread.setUncaughtExceptionHandler(handler);
			columnsThread.start();
			return columnsThread;
		} catch (Exception ex) {
			throw new MetadataRetrievalException(ex);
		}
	}

}
