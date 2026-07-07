package com.helicalinsight.adhoc.dao;

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
 * This interface having methods for interacting with the database to perform CRUD operations related to
 * resource metadata. Implementations of this interface are responsible for providing concrete implementations of
 * these methods for specific database systems.
 */
public interface HIMetadataResourceDAO {
	/**
     * Adds the HIResourceMetadata to the database.
     *
     * @param hiResourceMetadata       HIResourceMetadata to add
     * @return The ID of the added metadata
     */
    Integer addHIResourceMetadata(HIResourceMetadata hiResourceMetadata);
    /**
     * Edits the HIResourceMetadata in the database.
     *
     * @param hiResourceMetadata     HIResourceMetadata to edit
     * @return The ID of the edited metadata
     */
    Integer editHIResourceMetadata(HIResourceMetadata hiResourceMetadata);
    /**
     * Deletes the HIResourceMetadata from the database.
     *
     * @param id 			 ID of the metadata to delete
     */
    void deleteHIResourceMetadata(Integer id);
    /**
     * Retrieves the metadata for a given resource ID from the database.
     *
     * @param resourceId 		 ID of the resource
     * @return The metadata associated with the given resource ID
     */
    Metadata getHIResourceMetadataByResourceId(Integer resourceId);
    /**
     * Adds a  HIMetadataTables object to the database.
     *
     * @param hiMetadataTables    HIResourceMetadata object to add
     */
    void addHIMetadataTables(HIMetadataTables hiMetadataTables);
    /**
     * Adds HIMetadataRelationships object into database.
     * @param hiMetadataRelationships   HIMetadataRelationships object.
     */
    void addHIMetadataRelationships(HIMetadataRelationships hiMetadataRelationships);
    /**
     * It Adds HIMetadataConnections object .
     * @param hiMetadataConnections    HIMetadataConnections object
     * @return id of HIMetadataConnections object.
     */
    Integer addHIMetadataConnections(HIMetadataConnections hiMetadataConnections);
    /**
     * Adds HIMetadataColumns object to database.
     * @param hiMetadataColumns     HIMetadataColumns object
     * @return id of HIMetadataColumns.
     */
    Integer addHIMetadataColumns(HIMetadataColumns hiMetadataColumns);
    /**
     * it saves HIMetadataConnectionGlobal to database.
     * @param hiMetadataConnectionGlobal  HIMetadataConnectionGlobal object.
     * @return  id of HIMetadataConnectionGlobal object.
     */
    Integer saveHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal);
    /**
     * Adds a new {@link MetadataDatabases} object to the database.
     *
     * @param hiMetadataDatabases The MetadataDatabases object to add
     * @return The ID of the added MetadataDatabases object
     */
    Integer addHIMetadataDatabases(MetadataDatabases hiMetadataDatabases);
    /**
     * Retrieves the HIResourceMetadata by the given resource ID from the database.
     *
     * @param resourceId The ID of the resource
     * @return The HIResourceMetadata associated with the given resource ID
     */
    HIResourceMetadata giveHIResourceMetadataByResourceId(Integer resourceId);
    /**
     * Edits an existing {@link HIMetadataConnections} object in the database.
     * @param hiMetadataConnections         HIMetadataConnections object to edit
     * @return The ID of the edited HIMetadataConnections object
     */
    Integer editHIMetadataConnections(HIMetadataConnections hiMetadataConnections);
    /**
     * Retrieves a list of all {@link HIMetadataConnections} associated with the given metadata ID from the database.
     * @param metadataId        ID of the metadata
     * @return A list of HIMetadataConnections associated with the given metadata ID
     */
    List<HIMetadataConnections> getHIMetadataConnections(Integer metadataId);
    /**
     * Edits an existing {@link HIMetadataConnectionGlobal} object in the database.
     * @param hiMetadataConnectionGlobal    HIMetadataConnectionGlobal object to edit
     * @return The ID of the edited HIMetadataConnectionGlobal object
     */
    Integer editHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal);
    /**
     * Retrieves the {@link HIMetadataConnectionGlobal} object from the database based on the metadata ID and XML global connection ID.
     * @param metadataId            	 ID of the metadata
     * @param xmlGlobalConnectionId      XML global connection ID
     * @return The HIMetadataConnectionGlobal object associated with the given metadata ID and XML global connection ID
     */
    HIMetadataConnectionGlobal getHIMetadataConnectionGlobal(Integer metadataId,String xmlGlobalConnectionId);
    /**
     * Retrieves the {@link HIMetadataConnectionEFWD} object from the database based on the EFWD connection ID.
     * @param efwdConnectionId 		 EFWD connection ID
     * @return The HIMetadataConnectionEFWD object associated with the given EFWD connection ID
     */
    HIMetadataConnectionEFWD getHIMetadataConnectionEFWD(Integer efwdConnectionId);
    /**
     * Retrieves the {@link MetadataDatabases} object from the database based on the metadata ID and database ID.
     * @param metadataId  		 ID of the metadata
     * @param databaseId  		 ID of the database
     * @return The MetadataDatabases object associated with the given metadata ID and database ID
     */
    MetadataDatabases getHIMetadataDatabases(Integer metadataId, String databaseId);
    /**
     * Edits an existing {@link MetadataDatabases} object in the database.
     * @param metadataDatabase 		 MetadataDatabases object to edit
     */
    void editHIMetadataDatabases(MetadataDatabases metadataDatabase);
    /**
     * Edits an existing {@link HIMetadataTables} object in the database.
     * @param hiMetadataTables 			 HIMetadataTables object to edit
     */
    void editHIMetadataTables(HIMetadataTables hiMetadataTables);
    /**
     * Edits an existing {@link HIMetadataColumns} object in the database.
     * @param hiMetadataColumns          HIMetadataColumns object to edit
     */
    void editHIMetadataColumns(HIMetadataColumns hiMetadataColumns);
    /**
     * Retrieves a list of all {@link HIMetadataTables} associated with the given ID and database ID from the database.
     *
     * @param id    	 ID of the metadata
     * @param dbId  	 ID of the database
     * @return A list of HIMetadataTables associated with the given ID and database ID
     */
     List<HIMetadataTables> getMetadataTablesList(Integer id, Integer dbId);
     /**
      * Retrieves a list of all {@link HIMetadataView} associated with the given ID and database ID from the database.
      *
      * @param id   		 ID of the metadata
      * @param dbId 		 ID of the database
      * @return A list of HIMetadataView associated with the given ID and database ID
      */
    List<HIMetadataView> getMetadataViewList(Integer id, Integer dbId);
    /**
     * Adds a new {@link HIMetadataView} object to the database.
     * @param mdview 		 HIMetadataView object to add
     */
    void addHIMetadataView(HIMetadataView mdview);
    /**
     * Edits an existing {@link HIMetadataView} object in the database.
     * @param mdview      HIMetadataView object to edit
     */
    void editHIMetadataView(HIMetadataView mdview);
    /**
     * Deletes the {@link HIMetadataColumns} from the database based on the column ID and metadata ID.
     *
     * @param columnId    	 ID of the column to delete
     * @param metadataId  	 ID of the metadata
     */
    void deleteHIMetadataColumn(Integer columnId,Integer metadataId);
    /**
     * Deletes the {@link HIMetadataTables} from the database based on the table ID and database ID.
     *
     * @param tableId 	 ID of the table to delete
     * @param dbId    	 ID of the database
     */
    void deleteHIMetadataTable(Integer tableId, Integer dbId);
    /**
     * Deletes all relationships associated with the given metadata ID and database ID from the database.
     *
     * @param metadataId 		 ID of the metadata
     * @param dbId       		 ID of the database
     */
    void deleteAllRelationships(Integer metadataId, Integer dbId);
    /**
     * Retrieves a list of {@link HIMetadataColumns} associated with the given metadata ID and database ID from the database.
     *
     * @param metadataId 			 ID of the metadata
     * @param dbId       			 ID of the database
     * @return A list of HIMetadataColumns associated with the given metadata ID and database ID
     */
    List<HIMetadataColumns> getMetadataColumnsList(Integer metadataId,Integer dbId);
    /**
     * Retrieves a list of {@link HIMetadataSecurity} associated with the given ID from the database.
     * @param id 		 ID of the metadata
     * @return A list of HIMetadataSecurity associated with the given ID
     */
    List<HIMetadataSecurity> getMetaSecurity(Integer id);
    /**
     * Adds a new {@link HIMetadataSecurity} object to the database.
     * @param metadataSecurity      HIMetadataSecurity object to add
     */
    void addHIMetadataSecurity(HIMetadataSecurity metadataSecurity);
    /**
     * Edits an existing {@link HIMetadataSecurity} object in the database.
     * @param metadataSecurity     HIMetadataSecurity object to edit
     */
    void editHIMetadataSecurity(HIMetadataSecurity metadataSecurity);
    /**
     * Deletes the {@link HIMetadataSecurity} from the database based on the ID.
     * @param id         ID of the metadata security to delete
     */
    void deleteSecurity(Integer id);
    /**
     * Adds a new {@link HIMetadataCube} object to the database.
     * @param cube        HIMetadataCube object to add
     */
    void addCube(HIMetadataCube cube);
    /**
     * Adds a new object to the database.
     * @param obj     object to add
     */
    void add(Object obj);
    /**
     * Edits an existing {@link HIMetadataConnectionEFWD} object in the database.
     *
     * @param metadataConnectionEfwd The HIMetadataConnectionEFWD object to edit
     * @return The ID of the edited HIMetadataConnectionEFWD object
     */
	Integer saveHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd);
	/**
	 * Edits an existing {@link HIMetadataConnectionEFWD} object in the database.
	 *
	 * @param metadataConnectionEfwd       HIMetadataConnectionEFWD object to edit
	 * @return The ID of the edited HIMetadataConnectionEFWD object
	 */
	Integer editHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd);
	/**
	 * Deletes the {@link HIMetadataConnectionGlobal} object from the database.
	 * @param globalCon 		 HIMetadataConnectionGlobal object to delete
	 */
	void deleteMetadataGlobalConnection(HIMetadataConnectionGlobal globalCon);
	/**
	 * Deletes the {@link HIMetadataConnectionEFWD} object from the database.
	 * @param efwdCon         HIMetadataConnectionEFWD object to delete
	 */
	void deleteMetadataEfwdConnection(HIMetadataConnectionEFWD efwdCon);
	/**
	 * Retrieves the {@link HIMetadataConnections} object from the database based on the metadata ID, data source ID, and data source type.
	 * @param metadataId 		 ID of the metadata
	 * @param dsId       		 ID of the data source
	 * @param dsType     		 type of the data source
	 * @return The HIMetadataConnections object associated with the given metadata ID, data source ID, and data source type
	 */
	HIMetadataConnections getHIMetadataConnection(Integer metadataId, Integer dsId,String dsType);
	/**
	 * Retrieves a list of {@link MetadataDumpDTO} objects from the database.
	 * @return A list of MetadataDumpDTO objects
	 */
	List<MetadataDumpDTO> getDumpedMetadataList();
	/**
	 * Deletes all external relationships associated with the given metadata ID from the database.
	 * @param metadataId       ID of the metadata
	 */
	void deleteAllExternalRelationships(Integer metadataId);
	/**
	 * Sets the cache status for the specified metadata ID.
	 *
	 * @param metadataId The ID of the metadata
	 * @param value      The boolean value to set for the cache status
	 */
	void setCache(Integer metadataId,boolean value);
	/**
	 * Removes the metadata connection specified by metadata ID, database ID, and mode from the database.
	 *
	 * @param metadataId        ID of the metadata
	 * @param databaseId        ID of the database
	 * @param mode              mode of the connection
	 * @return The ID of the removed metadata connection
	 */
	Integer removeMetadataConnection(String metadataId, String databaseId, String mode);
	/**
	 * Finds the {@link HIMetadataRelationships} object by its join ID in the database.
	 *
	 * @param joinId          ID of the join
	 * @return The HIMetadataRelationships object
	 */
	HIMetadataRelationships findJoinById(String joinId);
	/**
	 * Deletes multiple HIMetadataRelationships objects specified by their IDs from the database.
	 *
	 * @param joinsToDelete        list of join IDs to delete
	 */
	void deleteHIMetadataRelationship(List<Integer> joinsToDelete);
	/**
	 * Retrieves the {@link MetadataDatabases} object from the database based on the database ID.
	 *
	 * @param dbId       ID of the database
	 * @return The MetadataDatabases object
	 */
	MetadataDatabases getHIMetadataDatabaseById(Integer dbId);
	/**
	 * Retrieves the {@link Table} object from the database based on the table ID.
	 * @param id       ID of the table
	 * @return The Table object
	 */
	Table findTableById(Integer id);
	/**
	 * Retrieves the {@link HIMetadataColumns} object from the database based on the column ID.
	 * @param colId        ID of the column
	 * @return The HIMetadataColumns object
	 */
	HIMetadataColumns findColumnById(Integer colId);
	/**
	 * Retrieves a list of {@link HIMetadataRelationships} objects from the database based on the metadata ID and database ID.
	 * @param mdId 		 ID of the metadata
	 * @param dbId 		 ID of the database
	 * @return A list of HIMetadataRelationships objects
	 */
	List<HIMetadataRelationships> getRelationshipListByMetadataIdAndDbId(Integer mdId, Integer dbId);
	/**
	 * Retrieves the metadata cache status and updates the last updated time for the specified resource ID.
	 * @param resourceId       ID of the resource
	 * @return The MetadataCacheStatus object containing cache status and last updated time
	 */
	MetadataCacheStatus getMetadataCacheStatusAndUpdateTime(Integer resourceId);
	/**
	 * Loads the metadata properties for the specified resource ID from the database.
	 * @param resourceId        ID of the resource
	 * @return The MetadataProperties object containing metadata properties
	 */
	MetadataProperties loadHiResourceMetadataPropertiesById(Integer resourceId);
	/**
	 * Retrieves a list of {@link ConnectionDetails} objects representing metadata connections from the database based on the resource ID.
	 * @param resourceId          ID of the resource
	 * @return A list of ConnectionDetails objects representing metadata connections
	 */
    List<ConnectionDetails>  getMetadataConnection(Integer resourceId);
    /**
     * Retrieves the {@link MetadataDriverReferences} object containing connection reference and driver information for the specified metadata ID.
     * @param metadaid          ID of the metadata
     * @return The MetadataDriverReferences object containing connection reference and driver information
     */
    public MetadataDriverReferences getConnectionRefAndDriver(Integer metadaid);
    /**
     * Deletes the metadata column from the database based on the column ID.
     * @param id 		 ID of the column to delete
     */
	void deleteHIMetadataColumnById(Integer id);
	/**
	 * Deletes all securities associated with the specified metadata ID from the database.
	 * @param metadataId 		 ID of the metadata
	 */
	void deleteAllSecuritiesByMetadataId(Integer metadataId);
	
	/**
	 * Retrieves a list of {@link HIMetadataColumns} objects from the database based on the table ID and metadata ID.
	 *
	 * @param tableId    		 ID of the table
	 * @param metadataId 		 ID of the metadata
	 * @return A list of HIMetadataColumns objects
	 */
	List<HIMetadataColumns> getMetadataColumns(Integer tableId, Integer metadataId);
	Integer getViewEntryByViewNameAndDbIdAndMetadataId(String viewName,Integer dbId, Integer metadataId);
	public List<HIMetadataView> getMetadataViewList(Integer metadata);
}












