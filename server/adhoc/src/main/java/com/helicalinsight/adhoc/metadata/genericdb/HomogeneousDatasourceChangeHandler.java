package com.helicalinsight.adhoc.metadata.genericdb;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.datasource.model.GlobalConnections;
/**
 * The HomogeneousDatasourceChangeHandler class handles changes in homogeneous data sources.
 * It extends the {@link DatasourceChangeHandler} class and provides methods to change the metadata connection
 * based on the type of data source.
 */
@Component
public class HomogeneousDatasourceChangeHandler extends DatasourceChangeHandler {

	/**
     * Changes the metadata connection based on the provided data source details.
     *
     * @param datasource    		 JSON object containing the data source details.
     * @param metadata      		 metadata object containing metadata ID.
     * @param oldConnection 		 old HIMetadataConnections object.
     */
	@Override
	void change(JsonObject datasource, Metadata metadata, HIMetadataConnections oldConnection) {
		ConnectionDetails connectionDetails = null;
		String oldConnectionId = null;
		if (oldConnection.getMetadataGlobalConnList() != null && !oldConnection.getMetadataGlobalConnList().isEmpty()) {
			HIMetadataConnectionGlobal global = oldConnection.getMetadataGlobalConnList().get(0);
			oldConnectionId = ""+oldConnection.getMetadataGlobalConnList().get(0).getGlobalConnections().getGlobalId();
			GlobalConnections connection = globalConnectionService.findGlobalConnectionById(Integer.valueOf(datasource.get("id").getAsString()));
			connectionDetails =  connectionTemplate.getGlobalConnectionDetails(datasource);
			global.setDialect(connectionDetails.getDialect());
			global.setDriverClass(connectionDetails.getDriverClass().getDriverClass());
			global.setDriverClassReference(connectionDetails.getDriverClass().getReference());
			global.setGlobalConnections(connection);
			global.setHiMetadataConnections(oldConnection);
			mdServiceDb.editHIMetadataConnectionGlobal(global);
		}
		else {
			HIMetadataConnectionEFWD efwd = oldConnection.getMetadataConnectionEfwd().get(0);
			oldConnectionId = ""+oldConnection.getMetadataConnectionEfwd().get(0).getHiEfwdConnection().getId();
			HIEfwdConnection connection =  efwdConnectionService.findConnectionByIDAndType(Integer.valueOf(datasource.get("id").getAsString()), datasource.get("type").getAsString());
			connectionDetails =  connectionTemplate.getEfwdConnectionDetails(datasource);
			efwd.setDialect(connectionDetails.getDialect());
			efwd.setDriverClass(connectionDetails.getDriverClass().getDriverClass());
			efwd.setDriverClassReference(connectionDetails.getDriverClass().getReference());
			efwd.setHiEfwdConnection(connection);
			oldConnection.setConnectionType(datasource.get("type").getAsString());
			mdServiceDb.editHIMetadataConnections(oldConnection);
			efwd.setHiMetadataConnections(oldConnection);
			mdServiceDb.editHIMetadataConnectionEfwd(efwd);
		}
		replaceOldConnectionInMetadata(connectionDetails, metadata, oldConnectionId,datasource);
	}

}
