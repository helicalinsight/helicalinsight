package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.genericdb.*;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * WorkflowMetadataProducer is responsible for producing metadata for workflows.
 * It implements the {@link IMetadataProducer} interface.
 * 
 * This class retrieves metadata based on the provided JSON information, sets various metadata properties,
 * and handles exceptions during the process.
 * 
 * @author Rajasekhar
 * @since 26-06-2015
 */
@Component
public class WorkflowMetadataProducer implements IMetadataProducer {

    @Autowired
    protected ConnectionTemplate connectionTemplate;
    @Autowired
    protected ConstraintsCleaner constraintsCleaner;
    @Autowired
    protected RelationshipsTemplate relationshipsTemplate;
    @Autowired
    private WorkflowDatabaseTemplate workflowDatabaseTemplate;
    
    /**
     * Prepares metadata based on the provided JSON information.
     *
     * @param formData 				 JSON object containing information to prepare metadata.
     * @return A Metadata object representing the prepared metadata.
     * @throws EfwServiceException If there is a problem in saving the metadata file.
     */
    public Metadata prepareMetadata(JsonObject formData) {
        Metadata metadata = ApplicationContextAccessor.getBean(Metadata.class);
        Boolean joinsOnly=  GsonUtility.optBoolean(formData, "joinsOnly");
        Boolean columnsOnly= GsonUtility.optBoolean(formData,"fetchColumnsOnly");
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

            Database database = this.workflowDatabaseTemplate.getDatabase(connection, formData);

            if(joinsOnly){
                onlyJoins(metadata,database);
                database.setTables(null);
            }else if(columnsOnly){
                metadata.setDatabase(database);
            }else{
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
     * Method to get Classifier type
     * @param formData         object provides classifier.
     * @param metadata		   instance to set classifier type.
     */
    protected void setRetrievalType(JsonObject formData, Metadata metadata) {
        String metadataRetrievalType = formData.get("classifier").getAsString();
        metadata.setType(metadataRetrievalType);
    }
    /**
     * Provides Drive Connection type from fromData object
     * @param formData        provides driver connection type
     * @return the driver connection type.
     */
    protected DriverConnection getDriverConnection(JsonObject formData) {
        String type = formData.get("type").getAsString();
        return (DriverConnection) ConnectionProviderFactory.getConnection(formData, type);
    }
    /**
     * It sets the database type for metadata object.
     * @param metadata				to set database type
     * @param connection			used to get connection details
     */
    protected void setDatabaseType(Metadata metadata, Connection connection) {
        metadata.setDatabaseType(MetadataUtils.getDatabaseDetails(connection));
    }
    /**
     * It Sets Visible property of metadata.
     * @param metadata     instance of metadata
     */
    protected void setVisible(Metadata metadata) {
        metadata.setVisible("true");
    }
    /**
     * Method helps in setting sql connection details for metadata.
     * @param formData				object containing form data provides jdbc type.
     * @param metadata				metadata object to which the connection details will be set.
     * @param driverConnection		to get Driver class.
     */
    protected void setConnectionDetails(JsonObject formData, Metadata metadata, DriverConnection driverConnection) {
        String driverName = driverConnection.getDriverClass();
        String dialect = MetadataUtils.dialectOfDatabase(driverName);
        this.connectionTemplate.setConnectionTag(formData, metadata);
        metadata.getConnectionDetails().setDialect(dialect);
    }
    /**
     * it set the security details.
     * @param metadata		metadata object
     */
    protected void setSecurityDetails(Metadata metadata) {
        metadata.setSecurity(SecurityUtils.securityObject());
    }
    /**
     * This method used to add constraints .
     * @param metadata			   metadata object
     * @param database             database object containing the tables with foreign key and primary key constraints.
     */
    protected void setRelationships(Metadata metadata, Database database) {

        this.constraintsCleaner.removeUnselectedColumns(database);

        onlyJoins(metadata, database);
    }
    /**
     * method deals with joins in metadata establishing relation .
     * @param metadata        to set database
     * @param database		  provides tables and establish relationship.
     */
    private void onlyJoins(Metadata metadata, Database database) {
        Tables tables = database.getTables();
        if (tables == null || tables.getTableList() == null) {
            database.setRelationships(null);
        } else {
        	this.relationshipsTemplate.setDatabase(database);
            Relationships relationships = this.relationshipsTemplate.getRelationships(tables);
            database.setRelationships(relationships);
        }

        metadata.setDatabase(database);
    }
}