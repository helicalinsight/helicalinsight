package com.helicalinsight.adhoc.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataServiceException;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
/**
 * This MetadataSecurityProvider class helps in modifies metadata security expressions associated with a metadata file.
 */
public class MetadataSecurityProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * This method retrieve and modify metadata security expressions.
     *
     * @param jsonFormData 			 form data JSON string containing location and metadata file name.
     * @return A JSON string containing the modified metadata security expressions.
     * @throws MetadataServiceException If the resource does not exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String location = formJson.has("location") ? formJson.get("location").getAsString() : "";
        String fileName = formJson.get("metadataFileName").getAsString();
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + fileName);
        if(metadataResource==null){
            throw new MetadataServiceException("The Resource does not exists");
        }
        Metadata metadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());

        MetadataSecurity metadataSecurity = metadata.getMetadataSecurity();
        if (metadataSecurity == null || metadataSecurity.getExpressions() == null || metadataSecurity.getExpressions().isEmpty()) {
            JsonObject response = new JsonObject();
            response.add("expressions", new JsonArray());
            response.addProperty("message", "There is no security expression(s) associated with this metadata ");
            return response.toString();
        } else {
            JsonObject accessObject = JsonParser.parseString(new Gson().toJson(metadataSecurity)).getAsJsonObject();
            modifyAccessObject(accessObject);
            return accessObject.toString();
        }

    }
    /**
     * Modifies the metadata security expressions object by removing unnecessary fields and adding additional ones.
     *
     * @param accessObject 			 JSON object containing metadata security expressions.
     */
    private void modifyAccessObject(JsonObject accessObject) {
        JsonArray expressionArray = accessObject.getAsJsonArray("expressions");
        for (JsonElement object : expressionArray) {
            JsonObject expression =  object.getAsJsonObject();
            if (expression.has("filter") && expression.get("filter").getAsString().isEmpty() ) {
                expression.remove("filter");
            }
            String executionType = expression.get("type").getAsString();
            expression.remove("type");
            String expressionId = expression.get("id").getAsString();
            expression.remove("id");
            expression.addProperty("expressionId", expressionId);
            expression.addProperty("executionType", executionType);
            String on = expression.get("on").getAsString();
            expression.remove("on");
            String[] split = on.split(",");
            JsonArray jsonArray = new JsonArray();
            for (String value : split) {
                jsonArray.add(value);
            }
            expression.add("on", jsonArray);
        }
    }
}

  