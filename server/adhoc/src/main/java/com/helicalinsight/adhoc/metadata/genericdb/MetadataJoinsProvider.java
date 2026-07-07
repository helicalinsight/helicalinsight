package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.ExternalRelationships;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;

/**
 * Provides methods to extract joins information from metadata.
 * 
 * Created by author on 25-03-2015.
 * @author Somen
 */
public class MetadataJoinsProvider {

    private final Metadata metadata;
    @NotNull
    private final ValidationUtility validationUtility;

    public MetadataJoinsProvider(Metadata metadata) {
        this.metadata = metadata;
        this.validationUtility = new ValidationUtility(this.metadata);
    }
    /**
     * Retrieves metadata joins information.
     * 
     * @return A JSON string representing the metadata joins.
     */
    public String getMetadataJoins() {
        JsonArray joinsArray = new JsonArray();

        Database database = this.metadata.getDatabase();
        String databaseName = database.getName();
        Relationships relationships = database.getRelationships();
        if (relationships == null) {
            return new JsonArray().toString();
        }
        
        Map<String, String> columnMap = this.validationUtility.getColumnsMap();

        List<Relationship> allRelationShips = relationships.getListOfRelations();
        allRelationShips.addAll(getInnerRelationships(metadata));
        ExternalRelationships external = metadata.getExternalRelationships();
        if(external != null) {
        	List<Relationship> externalRelations =  external.getListOfRelations();
        	allRelationShips.addAll(externalRelations);
        }

        int databaseNameLength = databaseName.length() + 1;
		List<Join> allJoins = new ArrayList<>();
        if (!allRelationShips.isEmpty()) {
			allRelationShips.forEach(relation -> {
				allJoins.addAll(relation.getJoin());
			});
			
			   allJoins.sort((o1,o2)-> {
				   if (o1.getPosition() != null && o2.getPosition() != null) {
					   return o1.getPosition() - o2.getPosition();
				   }
				   return 0;
			   });
			
                for (Join join : allJoins) {
                    JsonObject joinJson = new JsonObject();
                    String joinId = join.getId();
                    if(joinId.contains("-")){
                        joinId=MetadataUtils.getId(join.getLeftTable().toString(), join.getRightTable().toString(), join.getOperator());
                    }
                    if (join.getType() == null) {
                        joinJson.addProperty("id", joinId);
                        joinJson.addProperty("noAccess", "true");
                        joinsArray.add(joinJson);
                        continue;
                    }

                    JsonObject left = new JsonObject();
                    LeftTable leftTable = join.getLeftTable();
                    String leftTableName = leftTable.getTable();
                    String leftTableColumn = leftTable.getColumn();
                    String key = databaseName + "." + leftTableName + "." + leftTableColumn;
                    String fullyQualifiedColumnAlias = columnMap.get(key);
                    //String leftTableColumnAlias = subString(databaseNameLength, fullyQualifiedColumnAlias);
                    left.addProperty("table", leftTableName);
                    left.addProperty("column", leftTableColumn);
                    //left.put("alias", leftTableColumnAlias);
                    left.addProperty("tableId", leftTable.getId());
                    left.addProperty("dbId", leftTable.getDbId());

                    JsonObject right = new JsonObject();
                    RightTable rightTable = join.getRightTable();
                    String rightTableName = rightTable.getTable();
                    String rightTableColumn = rightTable.getColumn();
                    key = databaseName + "." + rightTableName + "." + rightTableColumn;
                    fullyQualifiedColumnAlias = columnMap.get(key);
                    //String rightTableColumnAlias = subString(databaseNameLength, fullyQualifiedColumnAlias);
                    right.addProperty("table", rightTableName);
                    right.addProperty("column", rightTableColumn);
                    //right.put("alias", rightTableColumnAlias);
                    right.addProperty("tableId",rightTable.getId());
                    right.addProperty("dbId",rightTable.getDbId());
                    
                    joinJson.addProperty("id", joinId);
                    joinJson.addProperty("type", join.getType());
                    joinJson.addProperty("operator", join.getOperator());
                    joinJson.add("left", left);
                    joinJson.add("right", right);

                    joinsArray.add(joinJson);
                }
        }
        return joinsArray.toString();
    }
    /**
     * Retrieves inner relationships from metadata.
     * 
     * @param metadata 		 metadata containing the relationships.
     * @return A list of inner relationships.
     */
    private List<Relationship> getInnerRelationships(Metadata metadata){
    	List<Relationship> innerList = new ArrayList<>();
    	Connections connections =  this.metadata.getConnections();
    	if(connections != null ) {
    		List<ConnectionDatabase> cdbList =  connections.getConnectionDatabase();
    		for(ConnectionDatabase cdb : cdbList) {
    		innerList.addAll(cdb.getDatabase().getRelationships().getListOfRelations());	
    		}
    	}
    	return innerList;
    }
    /**
     * Extracts a substring from a fully qualified column alias.
     * 
     * @param databaseNameLength 			 length of the database name.
     * @param fullyQualifiedColumnAlias 	 fully qualified column alias.
     * @return The substring representing the column alias.
     */
    private String subString(int databaseNameLength, String fullyQualifiedColumnAlias) {
        String rightTableColumnAlias = null;
        if (fullyQualifiedColumnAlias != null) {
            rightTableColumnAlias = fullyQualifiedColumnAlias.substring(databaseNameLength);
        }
        return rightTableColumnAlias;
    }
}
