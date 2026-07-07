package com.helicalinsight.adhoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.ExternalRelationships;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Responsible for merging metadata from multiple database connections ,
 * ensuring seamless integration of data from diverse sources.
 */
public class MultiConnectionMergeAdhocTable {
    private Metadata metadata;
    private Map<String, String> dbIdDbNameMap;
    private Map<String,String> dbIdPrefixMap;
    private Map<String,String> fullyQualifiedColumnMap;

    public MultiConnectionMergeAdhocTable(Metadata metadata) {
        this.metadata = metadata;
        this.dbIdDbNameMap = new HashMap<>();
        dbIdPrefixMap = new HashMap<>();
        fullyQualifiedColumnMap = new HashMap<>();
    }
    /**
     * Performs merging of metadata, updating table names, and managing prefixes to avoid conflicts.
     * configured with multiple database connections.
     *
     */
    public void merge() {
        Boolean cached = this.metadata.getCached();

        if (cached == null || !cached) {
            return;
        }
        if (!(AppStatistics.isSPARK_STARTED() && AppStatistics.isMASTER_STARTED())) {
            return;
        }
        Database database = this.metadata.getDatabase();
        ConnectionDetails mdConnectionDetails = this.metadata.getConnectionDetails();
        String name = mdConnectionDetails.getConnectionId() + database.getName();
        mdConnectionDetails.getDriverClass().setDriverClass("com.mysql.jdbc.Driver");
        mdConnectionDetails.setDialect("org.hibernate.dialect.MySQLDialect");
        name = DBProcessor.checkAndReplaceSpecialChars(name);
        updateTableName(database, name);
        updateMultipleDatabase();
        mergeTablesAndJoin();
        this.metadata.getDatabase().setName("");
        this.metadata.setConnections(null);
        this.crossJoinRelationship();
    }
    /**
     * Updates table names, manages prefixes, and performs merging for multiple databases.
     */
    private void mergeTablesAndJoin() {
        Connections connections = this.metadata.getConnections();
        if (connections == null) {
            return;
        }
        List<ConnectionDatabase> connectionDatabase = connections.getConnectionDatabase();
        if (connectionDatabase != null && connectionDatabase.size() > 0) {
            for (ConnectionDatabase item : connectionDatabase) {
                Database multiDatabase = item.getDatabase();
                List<Table> tableList = multiDatabase.getTables().getTableList();
                this.metadata.getDatabase().getTables().getTableList().addAll(tableList);
                List<Relationship> listOfRelations = multiDatabase.getRelationships().getListOfRelations();
                if (listOfRelations != null) {
                    Relationships relationships = this.metadata.getDatabase().getRelationships();
                    if (relationships == null) {
                        relationships = ApplicationContextAccessor.getBean(Relationships.class);
                        relationships.setListOfRelations(new ArrayList<>());
                    }
                    relationships.getListOfRelations().addAll(listOfRelations);
                }

            }
        }
    }
    /**
     * Updates table names for multiple databases.
     */
    private void updateMultipleDatabase() {
        Connections connections = this.metadata.getConnections();
        if (connections == null) {
            return;
        }
        List<ConnectionDatabase> connectionDatabase = connections.getConnectionDatabase();
        if (connectionDatabase != null && connectionDatabase.size() > 0) {
            for (ConnectionDatabase item : connectionDatabase) {
                Database multiDatabase = item.getDatabase();
                String name = item.getConnectionDetails().getConnectionId() + multiDatabase.getName();
                name = DBProcessor.checkAndReplaceSpecialChars(name);
                updateTableName(multiDatabase, name);
            }
        }
    }
    /**
     * @return map of id name for database.
     */
    public Map<String, String> getDbIdDbNameMap() {
        return dbIdDbNameMap;
    }
    /**
     * @return map of columns.
     */
    public Map<String,String> getFullyQualifedColumnMap(){
    	return fullyQualifiedColumnMap;
    }

    /**
     * Updates table name, manages prefixes.
     *
     * @param database 		database to get table details.
     * @param name     		Name for creating prefixes and updating table names.
     */
    private void updateTableName(Database database, String name) {

        List<Table> tableList = database.getTables().getTableList();
        if (StringUtils.isNotBlank(name)) {
            String prefix = name + "_#1_";
            String dbName = database.getName();
            tablePrefix(tableList, prefix,dbName);
            this.dbIdDbNameMap.put(database.getId(), dbName);
            dbIdPrefixMap.put(database.getId(), prefix);
            relationPrefix(database, name, prefix);
        }
    }
   /** Updates the table prefix for relationships within the specified database.
    *
    * @param database 		database to get table details.
    * @param name     		Name for creating prefixes and updating table names.
    * @param prefix   		Prefix to be added to table names.
    */
    private void relationPrefix(Database database, String name, String prefix) {
        Relationships relationships = database.getRelationships();
        List<Relationship> listOfRelations = relationships.getListOfRelations();
        if (StringUtils.isNotBlank(name) && listOfRelations != null) {
            for (Relationship item : listOfRelations) {
                item.setTable(prefix + item.getTable());
                item.setReferenceTable(prefix + item.getReferenceTable());
                List<Join> joins = item.getJoin();
                joinPrefix(prefix, joins);
            }
        }
    }
    /**
     * Handles cross join relationships, updating table names with prefixes.
     */
    private void crossJoinRelationship() {
        ExternalRelationships relationships = metadata.getExternalRelationships();
        if(relationships==null) return;

        List<Relationship> listOfRelations = relationships.getListOfRelations();
        if (listOfRelations != null) {
            for (Relationship item : listOfRelations) {

                List<Join> joins = item.getJoin();
                String prefixes = joinPrefixCrossJoin(joins);
                String[] split = prefixes.split("_##_");
                item.setTable(split[0] + item.getTable());
                item.setReferenceTable(split[1] + item.getReferenceTable());
            }
            Database database = this.metadata.getDatabase();
            Relationships allRelationships = database.getRelationships();
            if (allRelationships == null) {
                allRelationships = ApplicationContextAccessor.getBean(Relationships.class);
            }
            List<Relationship> listOfExistingRelationship = allRelationships.getListOfRelations();
            if (listOfExistingRelationship == null) {
                listOfExistingRelationship = new ArrayList<>();
            }
            listOfExistingRelationship.addAll(listOfRelations);
            database.setRelationships(allRelationships);
        }

    }
    /**
     * Adds prefixes to left and right table names in the specified join list.
     *
     * @param prefix 	Prefix to be added to table names.
     * @param joins  	List of joins to be processed.
     */
    private void joinPrefix(String prefix, List<Join> joins) {
        for (Join join : joins) {
            LeftTable leftTable = join.getLeftTable();
            leftTable.setTable(prefix + leftTable.getTable());
            RightTable rightTable = join.getRightTable();
            rightTable.setTable(prefix + rightTable.getTable());
        }
    }

    /**
     * Adds prefixes to left and right table names in the specified join list, specifically for cross joins.
     *
     * @param joins 		List of joins to be processed.
     * @return Concatenated left and right prefixes separated by '_##_'.
     */
    private String joinPrefixCrossJoin(List<Join> joins) {
        String leftPrefix = "";
        String rightPrefix = "";
        for (Join join : joins) {
        	String leftDbId = join.getLeftTable().getDbId();
        	String rightDbId = join.getRightTable().getDbId();
            leftPrefix = dbIdPrefixMap.get(leftDbId);
            rightPrefix = dbIdPrefixMap.get(rightDbId);
            LeftTable leftTable = join.getLeftTable();
            leftTable.setTable(leftPrefix + leftTable.getTable());
            RightTable rightTable = join.getRightTable();
            rightTable.setTable(rightPrefix + rightTable.getTable());
        }
        return leftPrefix + "_##_" + rightPrefix;
    }
    /**
     * Adds prefixes to table names in the specified list.
     *
     * @param tableList 	List of tables to be processed.
     * @param prefix    	Prefix to be added to table names.
     * @param dbName    	Name of the database.
     */
    private void tablePrefix(List<Table> tableList, String prefix,String dbName) {
        for (Table item : tableList) {
        	String name = prefix + item.getName();
            item.setName(name);
            if(!StringUtils.isBlank(item.getOriginalName())) {
            	String originalName = prefix + item.getOriginalName();
            	item.setOriginalName(originalName);
            }
            item.getColumns().getColumn().forEach(col -> fullyQualifiedColumnMap.put(col.getId(), name+"."+col.getName()));
        }
    }
}
