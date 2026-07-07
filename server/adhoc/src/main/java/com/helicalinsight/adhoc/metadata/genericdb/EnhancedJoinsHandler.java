package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIRelationshipPositions;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.efw.exceptions.EfwServiceException;

import jakarta.transaction.Transactional;

/**
 * The EnhancedJoinsHandler class manages the handling of joins for metadata operations,
 * including adding, updating, and deleting joins. It interacts with the database service
 * to persist changes to the metadata relationships.
 */
@Component("enhancedJoinsHandler")
@Scope("prototype")
public class EnhancedJoinsHandler {
	
	@Autowired
	protected HIMetadataResourceServiceDB mdServiceDb;
	
	@Autowired
	protected JoinMapper joinMapper;
	
	@Autowired
	private SaveAsJoinsHandler saveAsJoinsHandler;
	
	protected Map<String,Integer> joinsOrder = null;
	
	/**
     * Prepares default joins from the provided list of relationships.
     * 
     * @param relationList 		 list of relationships.
     * @param allJoins     		 map containing all joins.
     */
	protected void prepareDefaultJoins(List<Relationships> relationList, Map<String,Join> allJoins) {
		int count = 1;
		if (!relationList.isEmpty()) {
			for (Relationships rel : relationList) {
				List<Relationship> relations = rel.getListOfRelations();
				for (Relationship relationship : relations) {
					List<Join> joins = relationship.getJoin();
					for (Join join : joins) {
						if (!StringUtils.isNumeric(join.getId())) {
							allJoins.put(join.getId(), join);
							joinsOrder.put(join.getId(),count++);
						}
					}
				}
			}
		}
	}

	/**
     * Handles join operations based on form data, metadata, and column mapping.
     * 
     * @param formJson           	 JSON object containing joins.
     * @param hiResourceMetadata 	 metadata object.
     * @param columnMap          	 map containing column information.
     * @param relationList       	 list of relationships.
     * @param mode               	 mode of operation like saveas or delete.
     */
	@Transactional
	public void handle(JsonObject formJson, HIResourceMetadata hiResourceMetadata, Map<String, Object> columnMap,
			List<Relationships> relationList, String mode) {
		joinsOrder = new HashMap<>();
		if ("saveas".equalsIgnoreCase(mode)) {
			saveAsJoinsHandler.handle(formJson, hiResourceMetadata, relationList,columnMap);
			return;
		}
		JsonArray joinsArray = formJson.getAsJsonArray("joins");
		List<Integer> joinsToDelete = new ArrayList<>();
		Map<String, Join> allJoins = new LinkedHashMap<>();
		prepareDefaultJoins(relationList, allJoins);
		int count = 1;
		for (JsonElement object : joinsArray) {
			JsonObject join = object.getAsJsonObject();
			String action = join.get("action").getAsString();
			if (!"delete".equalsIgnoreCase(action)) joinsOrder.put(join.get("id").getAsString(),count++);
			if (action.equalsIgnoreCase("delete")) {
				removeJoins(join, joinsToDelete, allJoins);
			} else if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("update")) {
				Join processed = joinMapper.map(join.toString());
				allJoins.put(processed.getId(), processed);
			} else if (!action.equalsIgnoreCase("noChange")) {
				throw new EfwServiceException("Key action is unknown.");
			}
		}

		for (Map.Entry<String, Join> entry : allJoins.entrySet()) {
			String oldId = entry.getValue().getId();
			int id = saveOrUpdate(entry.getValue(), hiResourceMetadata, columnMap);
			updateMap(id, oldId, joinsOrder);
		}
		updatePositions(hiResourceMetadata);
		if (!joinsToDelete.isEmpty()) {
			mdServiceDb.deleteHIMetadataRelationship(joinsToDelete);
		}

	}
	/**
     * Removes joins based on the provided JSON object.
     * 
     * @param join           		 JSON object representing the join.
     * @param joinsToDelete 		 list of joins to delete.
     * @param allJoins      		 	 map containing all joins.
     */
	public void removeJoins(JsonObject join, List<Integer> joinsToDelete, Map<String, Join> allJoins) {
		String joinId = join.get("id").getAsString();
		if (StringUtils.isNumeric(joinId)) {
			joinsToDelete.add(Integer.valueOf(joinId));
		} else {
			allJoins.remove(joinId);
		}
	}
	
	/**
     * Updates the positions of joins within the metadata.
     * 
     * @param hiResourceMetadata 	 metadata object.
     */
	public void updatePositions(HIResourceMetadata hiResourceMetadata) {
		for (Map.Entry<String, Integer> entry : joinsOrder.entrySet()) {
			HIRelationshipPositions relationshipOrder = new HIRelationshipPositions();
			relationshipOrder.setMetadataId(hiResourceMetadata.getId());
			relationshipOrder.setPosition(entry.getValue());
			if (StringUtils.isNumeric(entry.getKey())) {
				HIMetadataRelationships metadataRelationship = mdServiceDb.findJoinById(entry.getKey());
				if ( metadataRelationship != null  ) {
					metadataRelationship.setJoinsPositions(relationshipOrder);
					mdServiceDb.addHIMetadataRelationships(metadataRelationship);
				}
			}
		}
	}
	
	/**
     * Saves or updates the join within the metadata.
     * 
     * @param eachJoin           		 join object provides left .
     * @param hiResourceMetadata 		 metadata object.
     * @param columnMap          		 map containing column information.
     * @return The ID of the saved or updated join.
     */
	protected int saveOrUpdate(Join eachJoin,HIResourceMetadata hiResourceMetadata , Map<String,Object> columnMap) {

		LeftTable left = eachJoin.getLeftTable();
		RightTable right = eachJoin.getRightTable();
		HIMetadataColumns leftColumns = (HIMetadataColumns) columnMap.get(left.getDbId() + left.getTable() + left.getColumn());
		HIMetadataColumns rightColumns = (HIMetadataColumns) columnMap.get(right.getDbId() + right.getTable() + right.getColumn());
		if (leftColumns != null && rightColumns != null) {
			HIMetadataRelationships metadataRelationship = new HIMetadataRelationships();
			metadataRelationship.setHiResourceMetadata(hiResourceMetadata);
			metadataRelationship.setJoinType(eachJoin.getType());
			metadataRelationship.setLeftMetadataColumns(leftColumns);
			metadataRelationship.setOperator(eachJoin.getOperator());
			metadataRelationship.setRightMetadataColumns(rightColumns);
			metadataRelationship.setId(StringUtils.isNumeric(eachJoin.getId())?Integer.valueOf(eachJoin.getId()):null);
			if (!left.getDbId().equalsIgnoreCase(right.getDbId())) {
				metadataRelationship.setExternal(true);
			} else {
				metadataRelationship.setHiMetadataDatabases(rightColumns.getHiMetadataTables().getHiMetadataDatabases());
				metadataRelationship.setExternal(false);
			}
			mdServiceDb.addHIMetadataRelationships(metadataRelationship);
			return metadataRelationship.getId();
		}
		return -1;
	}
	 /**
     * Updates the mapping of joins with their new IDs.
     * 
     * @param newId      		 new ID of the join.
     * @param oldId      		 old ID of the join.
     * @param joinsOrder 		 map containing join IDs and their order.
     */
	protected void updateMap(Integer newId, String oldId, Map<String, Integer> joinsOrder) {
		joinsOrder.put("" + newId, joinsOrder.get(oldId));
		if (!oldId.equals("" + newId)) {
			joinsOrder.remove(oldId);
		}
	}
	/**
     * Sets the order of joins.
     * 
     * @param joinsOrder 		 map containing join IDs and their order.
     */
	public void setJoinsOrder(Map<String,Integer> joinsOrder) {
		this.joinsOrder = joinsOrder;
	}

}
