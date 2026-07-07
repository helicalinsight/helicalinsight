package com.helicalinsight.adhoc.metadata;

import java.sql.Connection;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.WorkflowMongoTemplate;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * 
 * @created Date:09-02-2018
 * 
 * This class is responsible for preparing the Metadata details and
 * provides that. this class only prepares the metadata when we don't
 * have connection otherwise delegates the control to
 * WorkflowMetadataProducer class.
 * 
 */
@Component
public class ExtendedWorkflowMetadataProducer extends WorkflowMetadataProducer {

	private static final Logger logger = LoggerFactory.getLogger(ExtendedWorkflowMetadataProducer.class);
	@Autowired
	private WorkflowMongoTemplate workflowMongoTemplate;

	/**
     * Prepares metadata.
     *
     * @param formData 			 JSON object containing form data.
     * @return The prepared metadata.
     */
	public Metadata prepareMetadata(JsonObject formData) {

		Metadata metadata = ApplicationContextAccessor.getBean(Metadata.class);

		Connection connection = null;
		try {

			DriverConnection driverConnection = getDriverConnection(formData);
			// noinspection ConstantConditions
			connection = driverConnection.getConnection();
			if (connection != null) {
				return super.prepareMetadata(formData);
			} else {

				// TODO we need to consider for the other datasource which has
				// no connection
				// Implementation
				setRetrievalType(formData, metadata);
				setDatabaseType(metadata);
				setVisible(metadata);
				setConnectionDetails(formData, metadata, driverConnection);
				setSecurityDetails(metadata);

				Database database = workflowMongoTemplate.getDatabase(formData);
				setRelationships(metadata, database);

			}
		} catch (Exception ex) {
			throw new EfwServiceException("There was a problem in saving the metadata file.", ex);
		} finally {
			DbUtils.closeQuietly(connection);
		}

		return metadata;

	}

	/**
	 * Overridden method used to set databaseType in metadata when there is no
	 * connection.
	 * 
	 * @param metadata         metadata object 
	 */

	protected void setDatabaseType(Metadata metadata) {

		metadata.setDatabaseType("mongoDb" + "mongo");
	}

}
