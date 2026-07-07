package com.helicalinsight.adhoc.metadata.genericdb;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.model.GlobalConnections;
/**
 * The HeterogeneousDatasourceChangeHandler class handles changes in heterogeneous data sources.
 * It extends the {@link DatasourceChangeHandler} class and provides methods to change the metadata connection
 * based on the target data source type.
 */
@Component
public class HeterogeneousDatasourceChangeHandler extends DatasourceChangeHandler{

	/**
	 * Changes the metadata connection based on the provided data source details.
	 * @param dataSource       JSON object containing the data source details
	 * @param metadata         metadata object containing metadata ID.
	 * @param old			   HIMetadataConnections  provides old Id.
	 */
	@Override
	void change(JsonObject dataSource , Metadata metadata, HIMetadataConnections old) {
		String target = dataSource.get("baseType").getAsString();
		old.setConnectionType(target);
		String oldConnectionId = null;
		ConnectionDetails connectionDetails = null;
		mdServiceDb.editHIMetadataConnections(old);
		mdServiceDb.removeMetadataConnection(metadata.getMetadataId(),dataSource.get("dbId").getAsString(), "simple");
		if ( GlobalJdbcTypeUtils.isGlobalJdbc(target)) {
			oldConnectionId = ""+old.getMetadataConnectionEfwd().get(0).getHiEfwdConnection().getId();
			connectionDetails = connectionTemplate.getGlobalConnectionDetails(dataSource);
			HIMetadataConnectionGlobal global = new HIMetadataConnectionGlobal();
			global.setDialect(connectionDetails.getDialect());
			global.setDriverClass(connectionDetails.getDriverClass().getDriverClass());
			global.setDriverClassReference(connectionDetails.getDriverClass().getReference());
			GlobalConnections gConnections = globalConnectionService.findGlobalConnectionById(Integer.valueOf(dataSource.get("id").getAsString()));
			global.setGlobalConnections(gConnections);
			global.setHiMetadataConnections(old);
			mdServiceDb.saveHIMetadataConnectionGlobal(global);
		}
		else {
			oldConnectionId = ""+old.getMetadataGlobalConnList().get(0).getGlobalConnections().getGlobalId();
			connectionDetails =  connectionTemplate.getEfwdConnectionDetails(dataSource);
			HIMetadataConnectionEFWD efwd = new HIMetadataConnectionEFWD();
			efwd.setDialect(connectionDetails.getDialect());
			efwd.setDriverClass(connectionDetails.getDriverClass().getDriverClass());
			efwd.setDriverClassReference(connectionDetails.getDriverClass().getReference());
			HIEfwdConnection efwdConnection =  efwdConnectionService.findConnectionByIDAndType(Integer.valueOf(dataSource.get("id").getAsString()), target);
			efwd.setHiEfwdConnection(efwdConnection);
			efwd.setHiMetadataConnections(old);
			mdServiceDb.saveHIMetadataConnectionEfwd(efwd);
		}
		replaceOldConnectionInMetadata(connectionDetails,metadata,oldConnectionId,dataSource);
		
	}
	
	
}
