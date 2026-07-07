package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.Pair;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
 * Created by helical021 on 4/17/2019.
 */


/**
 * 
 * Deprecating this class.
 * Please use {@link EnhancedJoinsHandler}
 *
 */

@Deprecated(forRemoval = true)
public class JoinsHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JoinsHandler.class);
	

    public static Relationships prepareRelationships(@NotNull JSONArray joins, Metadata metadata) {
        Relationships relationships = metadata.getDatabase().getRelationships();
        if (relationships == null) {
            relationships = ApplicationContextAccessor.getBean(Relationships.class);
        }
        List<Relationship> existingRelations = relationships.getListOfRelations();
        List<Relationship> newRelations = prepareRelationships(joins, existingRelations);
        relationships.setListOfRelations(newRelations);
        return relationships;
    }
    
   
    public static ExternalRelationships prepareExternalRelationships(@NotNull JSONArray joins, Metadata metadata) {
        ExternalRelationships relationships = metadata.getExternalRelationships();
        if (relationships == null) {
            relationships = ApplicationContextAccessor.getBean(ExternalRelationships.class);
        }

        List<Relationship> existingRelations = relationships.getListOfRelations();
        List<Relationship> newRelations = prepareRelationships(joins, existingRelations);
        relationships.setListOfRelations(newRelations);
        return relationships;

    }

    public static List<Relationship> prepareRelationships(@NotNull JSONArray joins, List<Relationship> existingRelations) {

        if (existingRelations == null) {
            existingRelations = new ArrayList<>();
        }

        List<Join> existingJoins = new ArrayList<>();
        for (Relationship relationship : existingRelations) {
            List<Join> join = relationship.getJoin();
            if (join != null) {
                existingJoins.addAll(join);
            }
        }

        for (Object object : joins) {
            JSONObject json = JSONObject.fromObject(object);
            String action = json.getString("action");

            if ("add".equalsIgnoreCase(action) || "update".equalsIgnoreCase(action)) {
                process(existingJoins, json);
            } else if ("noChange".equalsIgnoreCase(action)) {
                Join required = removeJoin(existingJoins, json);
                if (required != null)
                    existingJoins.add(required);
            } else if ("delete".equalsIgnoreCase(action)) {
                removeJoin(existingJoins, json);
            } else {
                throw new IllegalArgumentException("Key action is unknown.");
            }
        }

        List<Relationship> newRelations = createNewRelationships(existingJoins);
        return newRelations;
    }

    private static void process(List<Join> existingJoins, JSONObject json) {
        Join join = ApplicationContextAccessor.getBean(Join.class);

        join.setType(json.getString("type"));
        join.setOperator(json.getString("operator"));

        LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
        JSONObject jsonLeftTable = json.getJSONObject("left");
        String table = jsonLeftTable.getString("table");
        leftTable.setTable(table);
        leftTable.setColumn(jsonLeftTable.getString("column"));
        leftTable.setId(jsonLeftTable.getString("tableId"));
        join.setLeftTable(leftTable);
        
        RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
        JSONObject jsonRightTable = json.getJSONObject("right");
        String referenceTable = jsonRightTable.getString("table");
        rightTable.setTable(referenceTable);
        rightTable.setColumn(jsonRightTable.getString("column"));
        rightTable.setId(jsonRightTable.getString("tableId"));
        join.setRightTable(rightTable);
		String databaseId = jsonLeftTable.getString("dbId");
		leftTable.setDbId(databaseId);
		join.setDatabaseId(databaseId);
		String referenceDbId =  jsonRightTable.getString("dbId");
		join.setReferenceDatabaseId(referenceDbId);
		rightTable.setDbId(referenceDbId);
        RelationshipsTemplate template = ApplicationContextAccessor.getBean(RelationshipsTemplate.class);
		if (!json.has("id") || json.getString("id").contains("-")) {
            String id = MetadataUtils.getId(join.getLeftTable().toString() + AdhocUtils.getUuid(), join.getRightTable().toString(), join.getOperator());
            join.setId(id);

        } else {
        	String id = template.decryptJoin(json.getString("id"));
            join.setId(id);
            // To preserve the order of the joins, remove
            removeJoin(existingJoins, json);
        }
			existingJoins.add(join);
    }

    private static Join removeJoin(List<Join> existingJoins, JSONObject json) {
        Join required = getJoin(existingJoins, json);
        if (required != null) {
            existingJoins.remove(required);
        }
        return required;
    }

    private static Join getJoin(List<Join> existingJoins, JSONObject json) {
        String id = json.getString("id");
        Join required = null;
        for (Join join : existingJoins) {
            if (join.getId().equals(id)) {
                required = join;
                break;
            }
        }

        /*if (required == null) {
            throw new IllegalStateException("Join id " + id + " is not found.");
        }*/
        return required;
    }


    @NotNull
    private static List<Relationship> createNewRelationships(List<Join> existingJoins) {
        List<Relationship> newRelations = new ArrayList<>();

        for (Join join : existingJoins) {
            String left = join.getLeftTable().getTable();
            String right = join.getRightTable().getTable();

            Pair<String> pair = new Pair<>(left, right);
            Relationship relationship = getRelationship(newRelations, pair);

            List<Join> list = relationship.getJoin();
            if (list == null) {
                list = new ArrayList<>();
                list.add(join);
                relationship.setTable(left);
                relationship.setReferenceTable(right);
                relationship.setJoin(list);
                newRelations.add(relationship);
            } else {
                list.add(join);
            }
        }
        return newRelations;
    }

    private static Relationship getRelationship(@NotNull List<Relationship> relationships, Pair<String> pair) {
        for (Relationship relationship : relationships) {
            Pair<String> existing = new Pair<>(relationship.getTable(), relationship.getReferenceTable());
            if (existing.isIdentical(pair)) {
                return relationship;
            }
        }
        return ApplicationContextAccessor.getBean(Relationship.class);
    }
    

}
