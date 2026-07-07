package com.helicalinsight.adhoc.metadata.genericdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Utility class for retrieving catalog details from a database connection.
 */
@SuppressWarnings("unused")
class CatalogDetails {
	/**
     * Retrieves all catalog names from the specified database connection.
     *
     * @param connection 			 database connection
     * @return a JSON string containing the list of catalog names
     * @throws MetadataRetrievalException if an error occurs while retrieving catalog information
     */
    public static String getAllCatalogNames(@NotNull Connection connection) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getCatalogs();
            List<String> catalogs = new ArrayList<>();
            while (resultSet.next()) {
                catalogs.add(resultSet.getString("TABLE_CAT"));
            }
            JsonObject catalogsJson;
            catalogsJson = new JsonObject();
            GsonUtility.accumulate(catalogsJson,"catalogs", catalogs);
            return catalogsJson.toString();
        } catch (SQLException exception) {
            throw new MetadataRetrievalException("Could not retrieve catalog information.", exception);
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
    }
    /**
     * Retrieves the catalog name from the specified database connection.
     *
     * @param connection 		 database connection
     * @return the catalog name
     * @throws MetadataRetrievalException if an error occurs while retrieving catalog information
     */
    public static String getCatalogName(@NotNull Connection connection) {
        try {
            return connection.getCatalog();
        } catch (SQLException exception) {
            throw new MetadataRetrievalException("Could not retrieve catalog information.", exception);
        }
    }
}
