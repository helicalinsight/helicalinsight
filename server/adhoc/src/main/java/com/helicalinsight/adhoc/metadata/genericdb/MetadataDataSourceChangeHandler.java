package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.MetadataDatabases;

/**
 * The MetadataDataSourceChangeHandler class handles changes in metadata data sources.
 * 
 * Created by author on 9/28/2015.
 * @author Rajasekhar
 */
@Component
public class MetadataDataSourceChangeHandler {

    @Autowired
    private ConnectionTemplate connectionTemplate;
    
    
    
    @Autowired
    private HIMetadataResourceServiceDB mdServiceDb;
    
    
    /**
     * Handles changes in metadata data sources.
     * 
     * @param databaseName 		 name of the database
     * @param dataSource 		 new data source configuration
     * @param metadata 			 metadata object to be updated
     */
    public void change(String databaseName, JsonObject dataSource, Metadata metadata) {
        
    	if (databaseName != null) {
            metadata.getDatabase().setName(databaseName);
        }
        try {
            dataSource.addProperty("dialect", MetadataUtils.dialectOfDatabase(MetadataUtils.databaseDriverClassName
                    (dataSource)));
        } catch (Exception ex) {
            throw new MetadataServiceException("Couldn't change the metadata datasource", ex);
        }
        
		String target = dataSource.get("type").getAsString();
		HIMetadataConnections oldConnection = getOldConnection(dataSource, Integer.valueOf(metadata.getMetadataId()));
		String source = oldConnection.getConnectionType();
		DatasourceChangeHandler handler = DatasourceChangeHandlerFactory.getHandler(source, target);
		if (dataSource.has("saveas") && dataSource.get("saveas").getAsBoolean()) {
			String oldConnectionId = dataSource.get("oldConnectionId").getAsString();
			Metadata newMetadata = MetadataUtils.getNewMetadata(oldConnectionId, metadata);
			connectionTemplate.setConnectionTag(dataSource, newMetadata);
			ConnectionDetails connectionDetails = newMetadata.getConnectionDetails();
			handler.replaceOldConnectionInMetadata(connectionDetails, metadata,oldConnectionId, dataSource);
		} else {
			handler.change(dataSource, metadata, oldConnection);
		}
    }
    
    /**
     * Retrieves the old connection based on the provided data source and metadata ID.
     * 
     * @param datasource 			 new data source configuration
     * @param metadataId 			 ID of the metadata
     * @return the old HIMetadataConnections object
     */
	public HIMetadataConnections getOldConnection(JsonObject datasource, int metadataId) {
		List<HIMetadataConnections> hiMetadataConnections = mdServiceDb.getHIMetadataConnections(metadataId);
		String tobeChanged = datasource.get("dbId").getAsString();
		for (HIMetadataConnections eachConnection : hiMetadataConnections) {
			List<MetadataDatabases> metadataDatabases = eachConnection.getMetadataDatabases();
			for (MetadataDatabases eachDb : metadataDatabases) {
				if (tobeChanged.equalsIgnoreCase("" + eachDb.getId())) {
					return eachConnection;
				}
			}
		}
		return null;

	}
    

	/**
     * Removes the old connection from the metadata.
     * 
     * @param metadata 		 metadata object provides connection
     * @param oldConId 		 ID of the old connection
     */
	public void removeOldConnection(Metadata metadata, String oldConId) {
		Connections connections = metadata.getConnections();
		if (connections != null) {
			List<ConnectionDatabase> cdbList = connections.getConnectionDatabase();
			for (ConnectionDatabase cdb : cdbList) {
				if (cdb.getConnectionDetails().getConnectionId().equals(oldConId)) {
					cdbList.remove(cdb);
					break;
				}
			}
		}
	}
	
   
}
