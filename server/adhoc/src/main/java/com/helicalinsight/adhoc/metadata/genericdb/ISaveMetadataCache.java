package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;


/**
 * The ISaveMetadataCache interface defines methods for saving metadata cache.
 * It provides methods to set form data, add joins, add table columns, add other metadata details,
 * get metadata, set metadata, get database, set database name, add connection details, and save file to disk.
 * @author Somen
 */
public interface ISaveMetadataCache {
	
    void setFormData(JsonObject formData);

    void addJoins(Database database);

    void addTableColumns(Database database);

    void addOthers(Metadata metadata);

    Metadata getMetadata();

    void setMetadata(Metadata metadata);

    Database getDatabase();

    void setDatabaseName(Database database);
    
    public void addConnectionDetails(JsonObject formJson, Metadata metadata);
    
    public JsonObject saveFileToDisk(JsonObject formJson, Metadata metadata);

}
