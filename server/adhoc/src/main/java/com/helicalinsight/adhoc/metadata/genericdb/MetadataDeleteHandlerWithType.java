package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.services.DatasourceDeleteClassFactory;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Handles the deletion of metadata with the specified type.
 * 
 * Created by Author on 01/04/2015
 * @author Somen
 */
public class MetadataDeleteHandlerWithType implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the deletion of metadata based on the provided JSON form data.
     * 
     * @param jsonFormData 		 JSON form data containing metadata information like file name.
     * @return A JSON string representing the response of the deletion operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String type=formJson.get("type").getAsString();
        JsonArray metadataFileName = formJson.getAsJsonArray("metadataFileName");
        JsonArray reportFileNames = formJson.getAsJsonArray("reportFileNames");
       
        JsonObject responseJson = new JsonObject();

        IMetadataDeleteRule iMetadataDeleteClass = DatasourceDeleteClassFactory.getIMetadataDeleteClass(type);
        return  iMetadataDeleteClass.deleteMetadata(metadataFileName, responseJson, reportFileNames);
      
    }


   
}
