package com.helicalinsight.adhoc.service;

import com.helicalinsight.adhoc.MetadataCacheStatus;
import com.helicalinsight.adhoc.MetadataDriverReferences;
import com.helicalinsight.adhoc.MetadataProperties;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

import java.util.List;

/**
 * This interface provides a comprehensive set of methods for interacting with
 * metadata resources, including databases, tables, columns, connections,
 * security, cubes, and more
 */
public interface HIMetadataResourceServiceDB {

	MetadataDatabases getHIMetadataDatabases(Integer metadataId, String connectionId);

	Integer addHIResourceMetadata(HIResourceMetadata hiResourceMetadata);

	Integer editHIResourceMetadata(HIResourceMetadata hiResourceMetadata);

	void deleteHIResourceMetadata(Integer id);

	Metadata getHIResourceMetadataByResourceId(Integer resourceId);

	void setCache(Integer metadataId, boolean value);

	HIResourceMetadata giveHIResourceMetadataByResourceId(Integer resourceId);
	HIResourceMetadataDTO giveHIResourceMetadataByResId(Integer resourceId);

	void addHIMetadataTables(HIMetadataTables hiMetadataTables);

	void addHIMetadataRelationships(HIMetadataRelationships hiMetadataRelationships);

	Integer addHIMetadataConnections(HIMetadataConnections hiMetadataConnections);

	Integer editHIMetadataConnections(HIMetadataConnections hiMetadataConnections);

	Integer getViewEntryByViewNameAndDbIdAndMetadataId(String viewName, Integer dbId, Integer metadataId);

	public List<HIMetadataView> getMetadataViewList(Integer metadata);

	List<HIMetadataConnections> getHIMetadataConnections(Integer metadataId);

	HIMetadataConnections getHIMetadataConnection(Integer metdataId, Integer dsId, String dsType);

	Integer addHIMetadataColumns(HIMetadataColumns hiMetadataColumns);

	void editHIMetadataColumns(HIMetadataColumns hiMetadataColumns);

	Integer saveHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal);

	Integer editHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal);

	HIMetadataConnectionGlobal getHIMetadataConnectionGlobal(Integer metadataId, String globalConnectionId);

	HIMetadataConnectionEFWD getHIMetadataConnectionEFWD(Integer efwdConnectionId);

	Integer addHIMetadataDatabases(MetadataDatabases metadataDatabase);

	void editHIMetadataDatabases(MetadataDatabases metadataDatabase);

	void editHIMetadataTables(HIMetadataTables hiMetadataTables);

	public List<HIMetadataTables> getMetadataTablesList(Integer id, Integer dbId);

	public List<HIMetadataView> getMetadataViewList(Integer id, Integer dbId);

	public void addHIMetadataView(HIMetadataView addHIMetadataView);

	public void editHIMetadataView(HIMetadataView editHIMetadataView);

	void deleteHIMetadataColumn(Integer columnId, Integer metadataId);

	void deleteHIMetadataColumnById(Integer id);

	void deleteHIMetadataTable(Integer tableId, Integer dbId);

	void deleteAllRelationships(Integer metadataId, Integer dbId);

	void deleteAllExternalRelationships(Integer metadataId);

	List<HIMetadataColumns> getMetadataColumnsList(Integer metadataId, Integer dbId);

	List<HIMetadataColumns> getMetadataColumns(Integer tableId, Integer metadataId);

	List<HIMetadataSecurity> getMetaSecurity(Integer id);

	void addHIMetadataSecurity(HIMetadataSecurity metadataSecurity);

	void editHIMetadataSecurity(HIMetadataSecurity metadataSecurity);

	void deleteSecurity(Integer id);

	void deleteAllSecuritiesByMetadataId(Integer metadataId);

	void addCube(HIMetadataCube cube);

	void add(Object hiCubeDimension);

	Integer saveHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd);

	Integer editHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd);

	void deleteMetadataGlobalConnection(HIMetadataConnectionGlobal globalCon);

	void deleteMetadataEfwdConnection(HIMetadataConnectionEFWD efwdCon);

	List<MetadataDumpDTO> getDumpedMetadataList();

	Integer removeMetadataConnection(String metadataId, String connectionId, String mode);

	HIMetadataRelationships findJoinById(String joinId);

	void deleteHIMetadataRelationship(List<Integer> joinsToDelete);

	MetadataDatabases getHIMetadataDatabaseById(Integer valueOf);

	Table findTableById(Integer valueOf);

	HIMetadataColumns findColumnById(Integer colId);

	List<HIMetadataRelationships> getRelationshipListByMetadataIdAndDbId(Integer mdId, Integer dbId);

	MetadataCacheStatus getMetadataCacheStatusAndUpdateTime(Integer resourceId);

	MetadataProperties loadHiResourceMetadataPropertiesById(Integer resourceId);

	public List<ConnectionDetails> getConnectionList(Integer resourceId);

	public MetadataDriverReferences getConnectionRefAndDriver(Integer metadaid);
}
