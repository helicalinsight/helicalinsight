package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIResourceMetadata;

/**
 * The SaveAsJoinsHandler class handles the saving or updating of join metadata,
 * including handling joins that have no changes.
 * It extends the {@link EnhancedJoinsHandler} class.
 */
@Component
public class SaveAsJoinsHandler extends EnhancedJoinsHandler {
	/**
     * Handles joins that have no changes.
     * @param noChangeJoins 			 list of joins containing id.
     * @param hiResourceMetadata 		 HIResourceMetadata object.
     * @param columnMap 				 map of column metadata.
     */
	private void handleNoChangeJoins(List<JsonObject> noChangeJoins, HIResourceMetadata hiResourceMetadata,
			Map<String, Object> columnMap) {
		for (JsonObject noChangeJoin : noChangeJoins) {
			String id = noChangeJoin.get("id").getAsString();
			HIMetadataRelationships relationship = mdServiceDb.findJoinById(id);
			if (relationship != null) {
				Join join = joinMapper.map(relationship);
				String oldId = join.getId();
				join.setId(null);
				int newId = saveOrUpdate(join, hiResourceMetadata, columnMap);
				updateMap(newId,oldId,joinsOrder);
			}
		}

	}
	/**
     * Handles the form data to save or update join metadata.
     * @param formJson 					 JsonObject containing joins.
     * @param hiResourceMetadata 		 HIResourceMetadata object.
     * @param relationList 				 list of Relationships.
     * @param columnMap 				 map of column metadata.
     */
	public void handle(JsonObject formJson, HIResourceMetadata hiResourceMetadata, List<Relationships> relationList,Map<String, Object> columnMap) {
		joinsOrder = new HashMap<>();
		JsonArray joinsArray = formJson.getAsJsonArray("joins");
		List<JsonObject> noChangeJoins = new ArrayList<>();
		Map<String, Join> allJoins = new LinkedHashMap<>();
		prepareDefaultJoins(relationList, allJoins);
		int count = 1;
		for (JsonElement object : joinsArray) {
			JsonObject join =  object.getAsJsonObject();
			String action = join.get("action").getAsString();
			if (!"delete".equalsIgnoreCase(action)) joinsOrder.put(join.get("id").getAsString(),count++);
			if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("update")) {
				Join processed = joinMapper.map(join.toString());
				allJoins.put(processed.getId(), processed);
			} else if (action.equalsIgnoreCase("noChange") && (StringUtils.isNumeric(join.get("id").getAsString()))) {
				noChangeJoins.add(join);
			}
		}
		for (Map.Entry<String, Join> entry : allJoins.entrySet()) {
			String oldId = entry.getValue().getId();
			entry.getValue().setId(null);
			int id = saveOrUpdate(entry.getValue(), hiResourceMetadata, columnMap);
			updateMap(id,oldId,joinsOrder);
		}
		if (!noChangeJoins.isEmpty()) {
			handleNoChangeJoins(noChangeJoins, hiResourceMetadata, columnMap);
		}
		updatePositions(hiResourceMetadata);
	}

}
