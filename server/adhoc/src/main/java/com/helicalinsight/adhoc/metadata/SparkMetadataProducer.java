package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.HISparkContext;
import org.springframework.stereotype.Component;

/**
 * SparkMetadataProducer extends WorkflowMetadataProducer
 * 
 * This class produces metadata for Spark connection. Extends the
 * WorkflowMetadataProducer class.
 * 
 * @author Rajasekhar
 * @since 5/12/2016
 */
@Component
public class SparkMetadataProducer extends WorkflowMetadataProducer {
	/**
     * Sets the connection details for Spark metadata.
     *
     * @param formData         	 JSON object containing form data provides id.
     * @param metadata         	 metadata object to set the connection details.
     * @param driverConnection 	 driver connection object.
     */
	@Override
	public void setConnectionDetails(JsonObject formData, Metadata metadata, DriverConnection driverConnection) {
		ConnectionDetails connectionDetails = new ConnectionDetails();
		connectionDetails.setConnectionId(formData.get("id").getAsString());

		DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
		String reference = "spark";
		driverClass.setDriverClass(HISparkContext.getDriverClass());
		driverClass.setReference(reference);

		connectionDetails.setDriverClass(driverClass);
		connectionDetails.setConnectionType("db.noSql");
		connectionDetails.setSubType("noSqlDataSource");

		metadata.setConnectionDetails(connectionDetails);

		metadata.getConnectionDetails().setDialect("org.hibernate.dialect.MySQLDialect");
	}
}
