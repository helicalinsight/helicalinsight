package com.helicalinsight.adhoc.metadata;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.security.MetadataAccess;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

/**
 * MetadataSecurityAccessHandler
 *
 * This class handles security access to metadata. It implements the IComponent interface
 * and provides methods to execute security-related operations on metadata.
 * @author Somen
 * Created on 8/4/2016.
 */
@SuppressWarnings("UnusedDeclaration")
public class MetadataSecurityAccessHandler implements IComponent {
	/**
     * Executes the security access handler component.
     *
     * @param jsonFormData 			 JSON data containing security access information.
     * @return A JSON string containing the response message.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject request = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonArray expressionArray = request.getAsJsonArray("expression");
        JsonObject response = new JsonObject();
        for (JsonElement expression : expressionArray) {
            JsonObject expressionJson = expression.getAsJsonObject();
            String expressionId = this.handleAddEditOperation(expressionJson, request);
            if (expressionId != null) {
                GsonUtility.accumulate(response,"expressionId", expressionId);
            }
        }
        if (response.entrySet().isEmpty()) {
            response.addProperty("message", "Expression edited successfully ");
        }
        return response.toString();
    }

    /**
     * Handles the add or edit operation for security access.
     *
     * @param expressionJson 		 JSON object containing the security access expression id.
     * @param request        		 JSON object containing the request data.
     * @return The ID of the security expression.
     */
    private String handleAddEditOperation(JsonObject expressionJson, JsonObject request) {
        MetadataAccess metadataAccess = ApplicationContextAccessor.getBean(MetadataAccess.class);
        SecurityExpression securityExpression = ApplicationContextAccessor.getBean(SecurityExpression.class);
        String tempLocation = TempDirectoryCleaner.getTempDirectory() + File.separator;
        String expressionId;
        boolean edit = false;
        if (expressionJson.has("expressionId")) {
            expressionId = expressionJson.get("expressionId").getAsString();
            edit = true;
        } else {
            expressionId = AdhocUtils.getUuid();
        }
        handleAccess(expressionJson, metadataAccess, securityExpression, request, expressionId);
        File targetLocation = new File(tempLocation + expressionId + ".xml");
        JaxbUtils.marshal(metadataAccess, targetLocation);
        return edit ? null : expressionId;
    }
    /**
     * Handles the security access for metadata.
     *
     * @param expressionJson    		 JSON object containing expression name, type , condition.
     * @param metadataAccess    		 metadata access object.
     * @param securityExpression 		 security expression object.
     * @param request           		 JSON object containing the request data.
     * @param expressionId      		 ID of the security expression.
     */
    private void handleAccess(JsonObject expressionJson, MetadataAccess metadataAccess,
                              SecurityExpression securityExpression, JsonObject request, String expressionId) {
        /*String metadataFileName = request.get("uuid").getAsString();
        String metadataExtension = JsonUtils.getMetadataExtension();
        if (metadataFileName.endsWith(metadataExtension)) {
            String location = request.get("location").getAsString();
            metadataAccess.setLocation(location);
        }
        metadataAccess.setUuid(metadataFileName);
        */
        securityExpression.setExpressionName(expressionJson.get("expressionName").getAsString());
        securityExpression.setAccessType(expressionJson.get("accessType").getAsString());
        securityExpression.setCondition(expressionJson.get("condition").getAsString());
        if (expressionJson.has("filter")) {
            securityExpression.setFilter(expressionJson.get("filter").getAsString());
        }
        String entityNames = joinArray(expressionJson);
        securityExpression.setOn(entityNames);
        securityExpression.setId(expressionId);
        securityExpression.setExpressionType(expressionJson.get("expressionType").getAsString());
        securityExpression.setType(expressionJson.get("executionType").getAsString());
        metadataAccess.setExpression(securityExpression);
    }
    /**
     * Joins the entity names from a JSON array.
     * @param expressionJson 		 JSON object containing the security access expression.
     * @return A string containing the concatenated entity names.
     */
    private String joinArray(JsonObject expressionJson) {
        JsonArray entityNames = expressionJson.getAsJsonArray("on");
        String entity = "";
        for (JsonElement string : entityNames) {
            entity = entity + string.getAsString() + ",";
        }
        return entity.substring(0, entity.length() - 1);
    }

    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, otherwise {@code false}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
