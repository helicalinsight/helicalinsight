package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.DatabaseDetails;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The {@code DatabaseMetadataInformationProvider} class is an implementation of the {@link IComponent} interface,
 * designed to provide metadata information about a database. It retrieves details such as schemas and catalogs
 * based on the provided form data, which includes information about the database connection.
 *
 * @author Rajasekhar
 * Created by Rajasekhar on 22-04-2015.
 */
public class DatabaseMetadataInformationProvider implements IComponent {
	/**
     * Method to provide metadata information about the database, such as schemas and catalogs.
     * It retrieves details based on the provided form data, including information about the database connection.
     *
     * @param jsonFormData 					 JSON-formatted form data containing parameters for retrieving metadata information.
     * @return A JSON-formatted string containing metadata information about the database.
     * @throws EfwdServiceException 		 If an error occurs during the execution, such as a failure to obtain the
     *                              		 database connection or retrieve metadata information.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = new Gson().fromJson(jsonFormData,JsonObject.class);

        String type = formJson.get("type").getAsString();

        DriverConnection databaseConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formJson,
                type);
        Connection connection = null;

        if (databaseConnection != null) {
            connection = databaseConnection.getConnection();
        }

        if (connection == null) {
            throw new EfwdServiceException("Could not get database connection for the type " + type);
        }

        DatabaseMetaData databaseMetaData;
        try {
            databaseMetaData = connection.getMetaData();
        } catch (SQLException ex) {
            throw new EfwdServiceException("Error occurred in getting database metadata information");
        }finally{
            DbUtils.closeQuietly(connection);
        }

        JsonObject response;
        response = new JsonObject();

        if (formJson.has("provideSchemas") && "true".equals(formJson.get("provideSchemas").getAsString())) {
            JsonObject schemas = DatabaseDetails.newRetrieveSchemas(databaseMetaData);
            response.add("schemas", schemas.getAsJsonArray("schemas"));
        }

        if (formJson.has("provideCatalogs") && "true".equals(formJson.get("provideCatalogs").getAsString())) {
            JsonObject catalogs = DatabaseDetails.newRetrieveCatalogs(databaseMetaData);
            response.add("catalogs", catalogs.getAsJsonArray("catalogs"));
        }


        return response.toString();
    }
    /**
     * Determines whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
