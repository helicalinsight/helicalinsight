package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.security.MetadataAccess;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import java.io.File;
import java.util.*;

/**
 * Handles the updating of metadata security 
 * 
 * Created  on 06/08/2016.
 * @author Somen
 */
public class MetadataSecurityUpdateHandler {

	/**
     * Sets access tags based on the provided expression array and metadata.
     * 
     * @param expressionArray 		 JSON array of security expressions
     * @param metadata 				 metadata provides metadata security object.
     */
    public static void setAccessTag(JsonArray expressionArray, Metadata metadata) {
        MetadataSecurity metadataSecurity = metadata.getMetadataSecurity();
        List<SecurityExpression> securityExpressions;
        if (metadataSecurity == null) {
            metadataSecurity = ApplicationContextAccessor.getBean(MetadataSecurity.class);
            securityExpressions = new ArrayList<>();
        } else {
            securityExpressions = metadataSecurity.getExpressions();
        }

        addExpressionToMetadata(expressionArray, securityExpressions);
        metadataSecurity.setExpressions(securityExpressions);
        if (securityExpressions.isEmpty()) {
            metadata.setMetadataSecurity(null);
        } else {
            metadata.setMetadataSecurity(metadataSecurity);
        }
    }

    /**
     * Adds security expressions to the metadata based on the provided JSON array.
     * 
     * @param expressionArray 			 JSON array of security expressions
     * @param securityExpressions 		 list of security expressions in the metadata
     */
	private static void addExpressionToMetadata(JsonArray expressionArray,
			List<SecurityExpression> securityExpressions) {
		List<String> tobeAdded = new ArrayList<>();
		List<String> tobeDeleted = new ArrayList<>();
		List<String> tobeEdited = new ArrayList<>();

       //Map that contains the changes made to "on" expression
		Map<String, String> onExpressionChangesMap = new HashMap<>();

		prepareAddDeleteList(expressionArray, tobeDeleted, tobeAdded, tobeEdited, onExpressionChangesMap);

		List<String> deleteArrayList = new ArrayList<>();

		deleteArrayList.addAll(tobeEdited);// Delete from metadata and simply add from Temp
		deleteArrayList.addAll(tobeDeleted);

		handleDeleteExpression(securityExpressions, deleteArrayList, onExpressionChangesMap, tobeEdited, tobeAdded);

		tobeAdded.addAll(tobeEdited);// Add and edit are same now
		processAdd(securityExpressions, tobeAdded, onExpressionChangesMap);
	}
	/**
	 * Processes the addition of security expressions to the metadata.
	 * 
	 * @param securityExpressions 			 list of security expressions in the metadata
	 * @param addArray 						 list of security expressions to be added
	 * @param onExpressionChangesMap 		 map containing changes made to "on" expressions
	 */
    private static void processAdd(List<SecurityExpression> securityExpressions, List<String> addArray, Map<String,
            String> onExpressionChangesMap) {

        for (String expressionId : addArray) {
            String expressionFilePath = TempDirectoryCleaner.getTempDirectory() + File.separator + expressionId + "" +
                    ".xml";

            MetadataAccess metadataAccess = JaxbUtils.unMarshal(MetadataAccess.class, new File(expressionFilePath));

            //check for relation with metadata
            SecurityExpression securityExpression = metadataAccess.getExpression();

            String editedOn = onExpressionChangesMap.get(securityExpression.getId());
            if (editedOn != null) {
                securityExpression.setOn(editedOn);
            }
            securityExpressions.add(securityExpression);
        }
    }
    /**
     * Prepares lists of expressions to be added, deleted, or edited based on the provided JSON array.
     * 
     * @param expressionArray 				 JSON array provides security expressionsId , action.
     * @param deleteList 					 list of expressions to be deleted
     * @param addArray 						 list of expressions to be added
     * @param tobeEdited 					 list of expressions to be edited
     * @param onExpressionChangesMap 		 map containing changes made to "on" expressions
     */
	private static void prepareAddDeleteList(JsonArray expressionArray, List<String> deleteList, List<String> addArray,
			List<String> tobeEdited, Map<String, String> onExpressionChangesMap) {

		for (int index = 0; index < expressionArray.size(); index++) {
			JsonObject expression = expressionArray.get(index).getAsJsonObject();
			String expressionId = expression.get("expressionId").getAsString();
			String action = expression.get("action").getAsString();

			if ("add".equalsIgnoreCase(action)) {
				addArray.add(expressionId);
			} else if ("edit".equalsIgnoreCase(action)) {
				if (expression.has("on")) {
					JsonArray on = expression.getAsJsonArray("on");
					if (on == null || on.isEmpty()) {
						throw new MalformedJsonException("Security on expression id " + expressionId + " is invalid.");
					}
					String onValue = "";
					for (Object element : on) {
						String onVal = (String) element;
						if ("".equals(onVal.trim())) {
							throw new MalformedJsonException(
									"The value of on Array for expressionId " + expressionId + " is incorrect");
						}
						onValue = onValue + element + ",";
					}
					onValue = onValue.substring(0, onValue.length() - 1);

					onExpressionChangesMap.put(expressionId, onValue);
				}

				tobeEdited.add(expressionId);
			} else if ("delete".equalsIgnoreCase(action)) {
				deleteList.add(expressionId);
			}
		}
	}
	/**
	 * Handles the deletion of security expressions from the metadata.
	 * 
	 * @param securityExpressions 			 list of security expressions in the metadata
	 * @param deleteList 					 list of expressions to be deleted
	 * @param onExpressionChangesMap 		 A map containing changes made to "on" expressions
	 * @param tobeEdited 					 list of expressions to be edited
	 * @param tobeAdded 					 list of expressions to be added
	 */
    private static void handleDeleteExpression(List<SecurityExpression> securityExpressions, List<String> deleteList,
                                               Map<String, String> onExpressionChangesMap, List<String> tobeEdited,
                                               List<String> tobeAdded) {
        Iterator<SecurityExpression> securityExpressionIterator = securityExpressions.iterator();
        while (securityExpressionIterator.hasNext()) {
            SecurityExpression securityExpression = securityExpressionIterator.next();
            String id = securityExpression.getId();
            if (deleteList.contains(id)) {
                if (onExpressionChangesMap.containsKey(id)) {
                    securityExpression.setOn(onExpressionChangesMap.get(id));
                    tobeEdited.remove(id);
                } else {
                    securityExpressionIterator.remove();
                }
                deleteList.remove(id);
            }
            if (tobeAdded.contains(id)) {
                tobeAdded.remove(id);
            }
        }
    }
}
