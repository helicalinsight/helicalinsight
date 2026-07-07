package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.IMetadataProducer;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * 
 * The GenericMetadataProducer class implements the {@link IMetadataProducer} interface to prepare metadata for a 
 * generic database based on the provided form data. It retrieves connection information, prepares the 
 * metadata object, and sets various metadata properties.
 *
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
@Component
public class GenericMetadataProducer implements IMetadataProducer {

    private static final Logger logger = LoggerFactory.getLogger(GenericMetadataProducer.class);
    @Autowired
    private ConnectionTemplate templateProducer;
    @Autowired
    private DatabaseTemplate databaseTemplate;

    /**
     * Prepares metadata for a generic database based on the provided form data.
     * 
     * @param formData 		 form data containing the metadata configuration like type, classifier etc.
     * @return The prepared metadata object.
     */
    public Metadata prepareMetadata(JsonObject formData) {
        long now = System.currentTimeMillis();
        long later;

        String type = formData.get("type").getAsString();

		/* ConnectionProviderFactory Call to get connection */
        Connection connection = null;
        Metadata metadata = null;

        try {
            DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formData,
                    type);

            if (driverConnection != null) {
                connection = driverConnection.getConnection();
            }

            if (connection == null) {
                throw new EfwdServiceException("The connection object is null.");
            }

            String metadataRetrievalType = formData.get("classifier").getAsString();
            String selectedSchema = getInfo(formData, "schemaInformation");

            metadata = ApplicationContextAccessor.getBean(Metadata.class);

            metadata.setType(metadataRetrievalType);
            metadata.setDatabaseType(MetadataUtils.getDatabaseDetails(connection));
            metadata.setVisible("true");

            this.templateProducer.setConnectionTag(formData, metadata);
            if (selectedSchema != null) {
                formData.addProperty("selectedSchema", selectedSchema);
            }
            metadata.setDatabase(this.databaseTemplate.getDatabase(connection, formData));
            metadata.setSecurity(SecurityUtils.securityObject());

            //Set dialect
            String driverName = driverConnection.getDriverClass();

            String dialect = MetadataUtils.dialectOfDatabase(driverName);
            metadata.getConnectionDetails().setDialect(dialect);
        } finally {
            /* In case of any exception, do not forget to close connection. */
            later = System.currentTimeMillis();
            DbUtils.closeQuietly(connection);
            if (logger.isDebugEnabled()) {
                logger.debug("The connection object is closed quietly from the finally block. " +
                        "Time taken including the preparation of the json is " + (later - now));
            }
        }
        return metadata;
    }
    /**
     * Returns information from the provided JSON object.
     * 
     * @param json    	 JSON object provides schemaName in JsonArray.
     * @param keyName 	 name of the key to extract information from.
     * @return The extracted information.
     */
    @Nullable
    private String getInfo(@NotNull JsonObject json, String keyName) {
        if (!json.has(keyName)) {
            return null;
        }

        JsonArray jsonArray;
        try {
            jsonArray = json.getAsJsonArray(keyName);
        } catch (Exception ignore) {
            throw new EfwdServiceException("The " + keyName + " is not an array. Expecting only an array.");
        }

        if (!jsonArray.isEmpty()) {
            // Access the first index as the current xml supports only one
            // Database node
            String selected =  jsonArray.get(0).getAsString();
            DataSourceUtils.validate(selected);
            return selected;
        }
        return null;
    }
}