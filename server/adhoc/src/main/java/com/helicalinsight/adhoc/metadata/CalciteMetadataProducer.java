package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * Component class for producing metadata using Calcite database connections.
 * Extends WorkflowMetadataProducer and implements IMetadataProducer.
 * Created by user on 5/13/2016.
 * @author Rajasekhar
 */
@Component
public class CalciteMetadataProducer extends WorkflowMetadataProducer implements IMetadataProducer {

    @Autowired
    private CalciteDatabaseTemplate calciteDatabaseTemplate;
    /**
     * Prepares metadata based on the provided form data.
     *
     * @param formData 		 		JSON object containing form data.
     * @return prepared metadata.
     * @throws EfwServiceException 	If there is an error in saving the metadata file.
     */
    public Metadata prepareMetadata(JsonObject formData) {
        Metadata metadata = ApplicationContextAccessor.getBean(Metadata.class);
        Connection connection = null;
        try {
            setRetrievalType(formData, metadata);

            DriverConnection driverConnection = getDriverConnection(formData);
            //noinspection ConstantConditions
            connection = driverConnection.getConnection();

            setDatabaseType(metadata, connection);
            setVisible(metadata);
            setConnectionDetails(formData, metadata, driverConnection);
            setSecurityDetails(metadata);

            Database database = this.calciteDatabaseTemplate.getDatabase(connection, formData);

            setRelationships(metadata, database);
        } catch (Exception ex) {
            throw new EfwServiceException("There was a problem in saving the metadata file.", ex);
        } finally {
            DbUtils.closeQuietly(connection);
        }

        return metadata;
    }
}
