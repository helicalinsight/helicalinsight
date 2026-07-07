package com.helicalinsight.adhoc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.datasource.GsonUtility;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.SQLTypeMap;

/**
 * Helper class for DataMapDbMetadataUtils.
 * Date of Creation :19-02-2018
 */
public class DbMetadataUtils {
	private static Logger logger = LoggerFactory.getLogger(DbMetadataUtils.class);

	/**
     * Retrieves catalogs.
     *
     * @param databaseMetaData 			 DatabaseMetaData object.
     * @param catalog          			 catalog name.
     * @return The list of catalogs.
     */
	public static List<String> retrieveCatalogs(DatabaseMetaData databaseMetaData, String catalog) {
		ResultSet result = null;
		try {
			List<String> catalogs;
			result = databaseMetaData.getCatalogs();
			catalogs = new ArrayList<String>();
			while (result.next()) {
				catalogs.add(result.getString(catalog));
			}
			return catalogs;
		} catch (SQLException e) {
			throw new EfwServiceException("Could not get database metadata information", e);
		} finally {
			DbUtils.closeQuietly(result);
		}
	}

	/**
     * Retrieves schemas.
     *
     * @param databaseMetaData 				 DatabaseMetaData object.
     * @param schema           				 schema name.
     * @return The list of schemas.
     */
	public static List<String> retrieveSchemas(DatabaseMetaData databaseMetaData, String schema) {
		long now = System.currentTimeMillis();
		long later;
		ResultSet result = null;

		try {
			List<String> schemaList;
			result = databaseMetaData.getSchemas();
			schemaList = new ArrayList<String>();
			while (result.next()) {
				schemaList.add(result.getString(schema));
			}

			later = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format(
						"The time taken to complete the request of getting " + "the schemas " + "information is %s",
						(later - now)));
			}
			return schemaList;
		} catch (SQLException e) {
			throw new EfwServiceException("Could not get database schemas information", e);
		} finally {
			DbUtils.closeQuietly(result);
		}

	}

	
	/**
     * Retrieves the list of tables.
     *
     * @param databaseMetaData 			 DatabaseMetaData object.
     * @param schemaPattern    			 schema pattern.
     * @param catalogName      			 catalog name.
     * @param schemaName       			 schema name.
     * @param tableName        			 JSON array of table names.
     * @return The list of tables.
     */
	public static List<String> getListOfTables(DatabaseMetaData databaseMetaData, String schemaPattern,
			String catalogName, String schemaName, JsonArray tableName) {

		String[] types = { "TABLE", "VIEW" };

		if (tableName != null && !tableName.isEmpty()) {
            types= new String[tableName.size()];
			for (int index = 0; index < tableName.size(); index++) {
				types[index] = tableName.get(index).getAsString();
			}
		}

		if (catalogName.isEmpty()) {
			catalogName = null;
		}
		if (schemaName.isEmpty()) {
			schemaName = null;
		}
		if (schemaPattern == null || schemaPattern.isEmpty()) {
			schemaPattern = null;
		}
		long now = System.currentTimeMillis();
		long later;
		List<String> tableNameList;
		ResultSet result = null;
		try {

			result = databaseMetaData.getTables(catalogName, schemaName, schemaPattern, types);

			tableNameList = new ArrayList<>();
			while (result.next()) {
				String singleTableName = result.getString(3);
				tableNameList.add(singleTableName);
			}
			later = System.currentTimeMillis();

			if (logger.isDebugEnabled()) {
				logger.debug("Fetched the tables information from the database. Time taken is " + (later - now));
			}
		} catch (SQLException e) {
			throw new EfwServiceException("Could not get database tables information", e);
		} finally {
			DbUtils.closeQuietly(result);
		}
		return tableNameList;
	}


	
	 /**
     * Retrieves column information for a table.
     *
     * @param databaseMetaData 			 DatabaseMetaData object.
     * @param catalog           		 catalog name.
     * @param schema            	     schema name.
     * @param tableName         		 table name.
     * @param columnPattern     		 column pattern.
     * @param allTables         		 JSON array containing all tables.
     */
	public static void getColumnInfoForTable(DatabaseMetaData databaseMetaData, String catalog, String schema,
			String tableName, String columnPattern, JsonArray allTables) {
		 Map<String, String> dataTypesMapping = AdhocUtils.getDataTypeMapping();
		
		String aCatalog = null;
		JsonArray catalogArray;
		if (catalog.isEmpty()) {
			catalog = null;
		}else{
			catalogArray = new Gson().fromJson(catalog,JsonArray.class);
			aCatalog = GsonUtility.optStringFromJsonArray(catalogArray, 0);
			//aCatalog = catalog;
			if(aCatalog ==null ||"".equals(aCatalog)){
				aCatalog=null;
			}
		}
		String aSchema = null;
		JsonArray schemaArray;
		if (schema.isEmpty()) {
			schema = null;
			schemaArray = new JsonArray();	
		} else {
			schemaArray = new Gson().fromJson(schema,JsonArray.class);
			aSchema = GsonUtility.optStringFromJsonArray(schemaArray,0);
			//aSchema = schema;
			if(aSchema ==null ||"".equals(aSchema)){
				aSchema=null;
			}
		}
		if (columnPattern == null || columnPattern.isEmpty()) {
			columnPattern = null;
		}

		try {
			ResultSet resultSetOfColumns;
			long now = System.currentTimeMillis();
			long later;

			
			String table_name = tableName.replace("\"", "");
			resultSetOfColumns = databaseMetaData.getColumns(aCatalog, aSchema, table_name, columnPattern);

			tableName = escape(table_name);
			if (aSchema == null || aSchema.isEmpty()) {
				aSchema = ApplicationProperties.getInstance().getNullValue();
			}
			if (catalog == null || catalog.isEmpty()) {
				catalog = ApplicationProperties.getInstance().getNullValue();
			}
			while (resultSetOfColumns.next()) {
				int dataType = resultSetOfColumns.getInt("DATA_TYPE");
				String columnName = resultSetOfColumns.getString("COLUMN_NAME");
				int size = resultSetOfColumns.getInt("COLUMN_SIZE");
				int nullable = resultSetOfColumns.getInt("NULLABLE");
				int position = resultSetOfColumns.getInt("ORDINAL_POSITION");
				JsonObject columnInfo = new JsonObject();
				columnInfo.addProperty("catalogs", catalog);
				columnInfo.addProperty("schemas",aSchema);
				columnInfo.addProperty("tables", tableName);
				columnInfo.addProperty("columns", columnName);
				 String type =  ApplicationUtilities.removeClass(SQLTypeMap.toClass(dataType));
				 if ( "java.lang.Object".equalsIgnoreCase(type) ) {
					 type  =  resultSetOfColumns.getString("TYPE_NAME");
					 if (type.startsWith("STRUCT")) {
						 type = "java.lang.Object";
					 }
				 }
				columnInfo.addProperty("type", type);
				String jsType = AdhocUtils.getType(type, dataTypesMapping);

                columnInfo.addProperty("dataType", jsType);
				columnInfo.addProperty("size", String.valueOf(size));
				columnInfo.addProperty("nullable", (DatabaseMetaData.columnNullable == nullable) ? "TRUE" : "FALSE");
				columnInfo.addProperty("position", String.valueOf(position));
				allTables.add(columnInfo);
			}

			later = System.currentTimeMillis();

			if (logger.isDebugEnabled()) {
				logger.debug("The time taken to retrieve the column metadata for the table " + tableName + " is "
						+ (later - now));
			}
		} catch (SQLException ex) {
			throw new EfwServiceException(ex);
		}
	}


	/**
     * Escapes special characters in a string.
     *
     * @param string 			 string to escape.
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
     * Checks if metadata is empty before saving.
     *
     * @param metadata 		 metadata object.
     * @return {@code true} if metadata is empty, otherwise {@code false}
     */
    public static boolean checkMetadataBeforeSave(Metadata metadata) {
        Database database1 = metadata.getDatabase();
        Tables tables = database1.getTables();//.getTableList();
        if (tables != null) {
            List<Table> tableList = tables.getTableList();
            if (tableList != null) {
                return tableList.isEmpty();
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

}
