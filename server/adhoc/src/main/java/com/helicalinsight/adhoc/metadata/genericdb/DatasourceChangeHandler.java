package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;

/**
 * Class {@code DatasourceChangeHandler} is responsible for updating the database with latest catalog and schema detail.s.
 */
@Component
public abstract class DatasourceChangeHandler {

	@Autowired
	protected GlobalConnectionService globalConnectionService;

	@Autowired
	protected EFWDConnectionService efwdConnectionService;

	@Autowired
	protected HIMetadataResourceServiceDB mdServiceDb;

	@Autowired
	protected ConnectionTemplate connectionTemplate;

	abstract void change(JsonObject dataSource, Metadata metadata, HIMetadataConnections oldConnection);

	/**
	 * The method is responsible for updating latest schema and catalog details.
	 * @param conDetails                ConnectionDetails object  
	 * @param metadata					Metadata object used to get connection and connection details
	 * @param oldConnectionId			provides old id
	 * @param dataSource				object provides schema and catalog name for database update
	 */
	public void replaceOldConnectionInMetadata(ConnectionDetails conDetails, Metadata metadata,
			String oldConnectionId,JsonObject dataSource) {
		boolean changed = false;
		if (metadata.getConnections() != null) {
			Connections connections = metadata.getConnections();
			List<ConnectionDatabase> cdbList = connections.getConnectionDatabase();
			for (ConnectionDatabase cdb : cdbList) {
				if (cdb.getConnectionDetails().getConnectionId().equalsIgnoreCase(oldConnectionId)) {
					updateDatabase(dataSource, cdb.getDatabase());
					cdb.setConnectionDetails(conDetails);
					changed = true;
				}
			}

		} 
		if (!changed && oldConnectionId.equalsIgnoreCase(metadata.getConnectionDetails().getConnectionId())) {
			updateDatabase(dataSource, metadata.getDatabase());
			metadata.setConnectionDetails(conDetails);
			metadata.setConnectionType(conDetails.getConnectionType());
		}
	}
	
	/**
	 * It simply updates the database using provided jsonObject data.
	 * @param changedDatasource      jsonObject provides catalog and schema name
	 * @param database				 Database instance used to set catalog and schema name
	 */
	private void updateDatabase(JsonObject changedDatasource , Database database) {
    	
	  	if(GsonUtility.optBooleanValue(changedDatasource, "changed",false)) {
			String catalog = changedDatasource.get("catalog").getAsString();
			String schema = changedDatasource.get("schema").getAsString();
			String dot = "";
			if (!catalog.isEmpty() && !schema.isEmpty()) {
				dot = ".";
			}
			database.setName(catalog + dot + schema);
			database.setCatalog(catalog);
			database.setSchema(schema);
			changedDatasource.remove("changed");
	}
}

}
