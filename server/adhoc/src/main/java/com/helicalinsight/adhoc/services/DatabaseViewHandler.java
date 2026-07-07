package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.DialectHelper;
import org.apache.commons.dbutils.DbUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;

/**
 * A component for handling database views.
 */
public class DatabaseViewHandler implements IComponent {
	/**
     * Determines whether it is thread-safe to cache the component instance.
     * @return {@code true} if it is thread-safe to cache..
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to handle database views.
     *
     * @param jsonFormData 			 form data provides location and file name
     * @return A JSON string containing information about the database views.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        if (formData.has("location") && formData.has("metadataFileName")) {
            String location;
            String metadataFileName;
            try {
                location = formData.get("location").getAsString();
                metadataFileName = formData.get("metadataFileName").getAsString();
            } catch (Exception ex) {
                throw new MalformedJsonException("Required parameters location and/or " + "metadataFileName are " +
                        "missing.", ex);
            }

            String path = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + location +
                    File.separator + metadataFileName;

            Metadata metadata = MetadataDBUtility.getMetadata(location, metadataFileName);

            ConnectionDetails connectionDetails = metadata.getConnectionDetails();
            String directory = connectionDetails.getDirectory();
            if (directory == null) {
                String subType = connectionDetails.getSubType();
                formData.addProperty("type", subType);
                formData.addProperty("id", connectionDetails.getConnectionId());
            } else {
                String connectionType = connectionDetails.getConnectionType();
                formData.addProperty("type", connectionType);
                formData.addProperty("id", connectionDetails.getConnectionId());
                formData.addProperty("dir", directory);
            }
        }
        String type = MetadataUtils.parameter(formData, "type");
        AdhocServiceUtils.addExtraDataForNormalProcess(formData, type);
        DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formData, type);

        Connection connection = null;
        try {
            if (driverConnection != null) {
                connection = driverConnection.getConnection();
            }

            if (connection == null) {
                throw new EfwdServiceException("The connection object is null.");
            }
        } finally {
            DbUtils.closeQuietly(connection);
        }
        String driverClass = driverConnection.getDriverClass();
        String dialectOfDatabase = MetadataUtils.dialectOfDatabase(driverClass);


        Properties properties = new Properties();
        properties.put(Environment.DIALECT, dialectOfDatabase);

        Dialect hibernateDialect =DialectHelper.getDialect(properties);

        JsonObject resultJson = new Gson().fromJson(jsonFormData,JsonObject.class);

        resultJson.addProperty("openQuote", hibernateDialect.openQuote());
        resultJson.addProperty("closeQuote", hibernateDialect.closeQuote());
        resultJson.addProperty("dialectName", hibernateDialect.getClass().getName());


        return resultJson.toString();
    }

}
