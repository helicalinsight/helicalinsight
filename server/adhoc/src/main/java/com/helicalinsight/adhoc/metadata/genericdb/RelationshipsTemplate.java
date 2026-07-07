package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Utility class for handling relationships templates.
 * 
 * Created by author on 26-06-2015.
 * @author Rajasekhar
 */
@Component
public class RelationshipsTemplate {

	
	private Map<String, String> tableNameMap;
	private Database database;
	/**
     * Retrieves the relationships from the provided Tables object.
     *
     * @param actualTables 		 Tables object containing table and foreign key information
     * @return The Relationships object containing the relationships between tables
     */
    public Relationships getRelationships(@NotNull Tables actualTables) {
        Relationships relationships = ApplicationContextAccessor.getBean(Relationships.class);
        List<Relationship> listOfRelations = getListOfRelations(actualTables);
        List<Relationship> actualRelations = getActualRelations(listOfRelations);
        relationships.setListOfRelations(actualRelations);
        return relationships;
    }

    @NotNull
    private List<Relationship> getListOfRelations(@NotNull Tables actualTables) {
        List<Relationship> relationshipList = new ArrayList<>();
        List<Table> tables = actualTables.getTableList();
        prepareMap(tables);
        for (Table table : tables) {
            ForeignKeys foreignKeys = table.getForeignKeys();
            if (foreignKeys != null) {
                getRelationshipsOfTable(relationshipList, foreignKeys, table.getName());
            }
        }
        return relationshipList;
    }
    /**
     * Prepares a map of table names and their corresponding IDs.
     *
     * @param tables 		 list of Table objects
     */
    private void prepareMap(List<Table> tables) {
    	this.tableNameMap=new HashMap<>();
        for (Table table : tables) {
            this.tableNameMap.put(table.getName(), table.getId());
        }
	}
    /**
     * Filters out duplicate relationships and returns the list of actual relationships.
     *
     * @param listOfRelations 		 list of relationships to filter
     * @return The list of actual relationships without duplicates
     */
	@NotNull
    private List<Relationship> getActualRelations(@NotNull List<Relationship> listOfRelations) {
        List<Relationship> actualRelations = new ArrayList<>();
        ListIterator<Relationship> iterator = listOfRelations.listIterator();

        while (iterator.hasNext()) {
            Relationship relationship = iterator.next();
            iterator.remove();

            List<Relationship> duplicatesList = new ArrayList<>();

            for (Relationship other : listOfRelations) {
                if (relationship.equals(other)) {
                    duplicatesList.add(other);
                }
            }
            List<Join> joins = relationship.getJoin();

            for (Relationship aDuplicate : duplicatesList) {
                joins.add(aDuplicate.getJoin().get(0));
            }

            if (!actualRelations.contains(relationship)) {
                actualRelations.add(relationship);
            }
        }
        return actualRelations;
    }
	/**
     * Extracts the relationships from the Tables object and adds them to the list.
     *
     * @param relationshipList 		 list to which relationships will be added
     * @param foreignKeys       	 ForeignKeys object containing foreign key information
     * @param tableName         	 name of the current table
     */
    private void getRelationshipsOfTable(@NotNull List<Relationship> relationshipList,
                                         @NotNull ForeignKeys foreignKeys, String tableName) {
        List<ForeignKey> foreignKeyList = foreignKeys.getForeignKeyList();
        for (ForeignKey foreignKey : foreignKeyList) {
            if (foreignKey != null) {
                relationshipList.add(getRelationship(foreignKey, tableName));
            }
        }
    }
    /**
     * Creates a Relationship object from the given ForeignKey object and table name.
     *
     * @param foreignKey 			 ForeignKey object representing the foreign key
     * @param tableName  		     name of the current table
     * @return The created Relationship object
     */
    private Relationship getRelationship(@NotNull ForeignKey foreignKey, String tableName) {
        Relationship relationship = ApplicationContextAccessor.getBean(Relationship.class);
        String primaryKeyTable = foreignKey.getReferenceTable();
        //Reference table is the one that has the foreign key
        relationship.setTable(primaryKeyTable);
        relationship.setReferenceTable(tableName);
        relationship.setJoin(getListOfJoins(foreignKey, tableName));
        return relationship;
    }
    /**
     * Creates a list of Join objects from the given ForeignKey object and table name.
     *
     * @param foreignKey 			 ForeignKey object representing the foreign key
     * @param tableName  			 name of the current table
     * @return The list of created Join objects
     */
    @NotNull
    private List<Join> getListOfJoins(@NotNull ForeignKey foreignKey, String tableName) {
        List<Join> joins = new ArrayList<>();
        /*
         * Now, per foreign key there will be only one Join. So, add that one to
		 * the list.
		 */
        joins.add(getJoin(foreignKey, tableName));
        return joins;
    }
    /**
     * Creates a Join object from the given ForeignKey object and table name.
     *
     * @param foreignKey 		 ForeignKey object representing the foreign key
     * @param tableName  		 name of the current table
     * @return The created Join object
     */
    private Join getJoin(@NotNull ForeignKey foreignKey, String tableName) {
        Join join = ApplicationContextAccessor.getBean(Join.class);
        //join.setId(UUID.randomUUID().toString());
        join.setOperator("=");
        join.setType("inner");
        //Left table is the one which has the primary key.
        join.setLeftTable(getLeftTable(foreignKey.getReferenceTable(), foreignKey.getReferenceColumn()));
        //Right table is the one which has the foreign key
        join.setRightTable(getRightTable(tableName, foreignKey.getName()));
        String joinId = MetadataUtils.getId(join.getLeftTable().toString(),join.getRightTable().toString(),join.getOperator()); 
        joinId = encryptJoin(joinId);
        join.setId(joinId);
        return join;
    }
    /**
     * Creates a LeftTable object from the given table name and column name.
     *
     * @param tableName 		 name of the table
     * @param name      		 name of the column
     * @return The created LeftTable object
     */
    private LeftTable getLeftTable(String tableName, String name) {
        LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
        leftTable.setTable(tableName);
        leftTable.setColumn(name);
        String tableId = this.tableNameMap.get(tableName);
        tableId = tableId != null ? tableId : getTableId(tableName);
        leftTable.setId(tableId);
        return leftTable;
    }
    /**
     * Creates a RightTable object from the given reference table name and reference column name.
     *
     * @param referenceTable  		 name of the reference table
     * @param referenceColumn 		 name of the reference column
     * @return The created RightTable object
     */
    private RightTable getRightTable(String referenceTable, String referenceColumn) {
        RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
        rightTable.setTable(referenceTable);
        rightTable.setColumn(referenceColumn);
        String tableId = this.tableNameMap.get(referenceTable);
        tableId = tableId != null ? tableId : getTableId(referenceTable);
        rightTable.setId(tableId);
        return rightTable;
    }
    /**
     * Encrypts the provided join ID.
     *
     * @param joinId 		 join ID to be encrypted
     * @return The encrypted join ID
     */
    public  String encryptJoin(String joinId) {
    	return joinId+ "_" + System.currentTimeMillis();
    }
    /**
     * Decrypts the provided join ID.
     *
     * @param joinId 		 encrypted join ID to be decrypted
     * @return The decrypted join ID
     */
    public  String decryptJoin(String joinId) {
    	String[] arr =  StringUtils.split(joinId,"_");
    	if(arr.length > 0) {
    		return arr[0];
    	}
    	return "";
    }
    /**
     * Sets the database for this RelationshipsTemplate.
     * @param database 		 database object to be set
     */
    public void setDatabase(Database database) {
    	this.database = database;
    }
    /**
     * Generates a unique ID for the table based on its name, schema, and catalog.
     *
     * @param tbName 		 name of the table
     * @return The generated unique ID for the table
     */
	private String getTableId(String tbName) {
		if (this.database != null) {
			String schema = this.database.getSchema();
			String catalog = this.database.getCatalog();
			return MetadataUtils.getId(catalog, schema, tbName);
		}
		return "";
	}

}
