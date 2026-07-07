package com.helicalinsight.adhoc.metadata.genericdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Utility class for retrieving view details from a database connection.
 */
@SuppressWarnings("ALL")
public class ViewsDetails {
	 /**
     * Retrieves all view names from the  database connection.
     *
     * @param connection 		 database connection
     * @return a JSON string containing the list of view names
     * @throws MetadataRetrievalException if an error occurs while retrieving view information
     */
    public static String getAllViewNames(@NotNull Connection connection) {
        ResultSet resultSet = null;
        Statement statement = null;
        JsonObject viewsJson;
        try {
            statement = connection.createStatement();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, null, null, new String[]{"VIEW"});

            viewsJson = new JsonObject();

            List<String> viewsList = new ArrayList<String>();

            while (resultSet.next()) {
                viewsList.add(resultSet.getString("TABLE_NAME"));
            }
            GsonUtility.accumulate(viewsJson,"views", viewsList);
        } catch (SQLException exception) {
            throw new MetadataRetrievalException("Could not retrieve views information.", exception);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
        return viewsJson.toString();
    }
}
