package com.helicalinsight.adhoc.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.DbMetadataUtils;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataServiceException;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

/**
 * Handles the update of metadata service.
 *
 * created by Author on 04-03-15
 * @author Muqtar Ahmed
 */
public class MetadataUpdateService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataUpdateService.class);
    /**
     * Executes the metadata update service.
     *
     * @param type       			 type of service.
     * @param serviceType 		     service type.
     * @param service    			 specific service.
     * @param formData   			 form data provides inmemory, uuid, location etc.
     * @return The result of the service execution.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();

        updateIfTempFileExists(formDataJson);

        String responseString = ServiceUtils.executeService(type, serviceType, service, formDataJson.toString());
        JsonObject inMemory = GsonUtility.optJsonObject(formDataJson,"inmemory");
        if (inMemory != null && !inMemory.entrySet().isEmpty()) {
            if (GsonUtility.optBoolean(inMemory,"middleware")) {
                formDataJson.addProperty("command", "SYNC_METADATA");
            } else {
                formDataJson.addProperty("command", "CLEAR_SYNC_METADATA");
            }
            String uuid = GsonUtility.optString(formDataJson,"uuid");
            if (uuid.contains(".")) {
                formDataJson.addProperty("file", uuid);
                formDataJson.remove("uuid");
            }
            formDataJson.addProperty("dir", formDataJson.get("location").getAsString());
            ServiceUtils.execute("adhoc", "metadata", "sync", formDataJson.toString());
        }
        String location = formDataJson.get("location").getAsString();

        JsonObject responseJSON = JsonParser.parseString(responseString).getAsJsonObject();
        String status = responseJSON.get("status").getAsString();
        if(status.equals("0")){
        return  responseString;
        }
        JsonObject firstResponse = responseJSON.getAsJsonObject("response");
        
        JsonObject newFormData = new JsonObject();


        newFormData.addProperty("location", location);
        newFormData.addProperty("uniqueId", true);
        newFormData.addProperty("provideJoins", true);
        newFormData.addProperty("metadataFileName", firstResponse.get("uuid").getAsString());
        IService iService = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.MetadataProvider", IService.class);
        if(GsonUtility.optStringValue(formDataJson,"newLocation",null)!=null){
            location = formDataJson.get("newLocation").getAsString();
            newFormData.addProperty("newLocation", location);
        }
        if (iService != null) {
            String result = iService.doService(type, serviceType, "getMetadataForEdit", newFormData.toString());
            JsonObject response = JsonParser.parseString(result).getAsJsonObject();
            JsonObject metadataJson = response.getAsJsonObject("response");
            firstResponse.add("metadata", metadataJson);
        }
         
        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
        FileInfo fileInfo = bean.prepareFileInfo(location,newFormData.get("metadataFileName").getAsString());
        JsonArray data = new JsonArray();
        JsonObject asJsonObject = JsonParser.parseString(new Gson().toJson(fileInfo)).getAsJsonObject();
        data.add(asJsonObject);
        GsonUtility.accumulate(firstResponse,"data", data);
        return responseJSON.toString();
       
        
    }
    /**
     * Checks and updates if the temporary file exists.
     *
     * @param formDataJson 	 JsonObject provides location, uuid.
     */
    public void updateIfTempFileExists(JsonObject formDataJson) {
        String mdExtension = "." + JsonUtils.getMetadataExtension();
        String metadataExtension = mdExtension;
        String location = formDataJson.get("location").getAsString();

        if (!formDataJson.has("uuid")) {
            return;
        }
        String metadata = formDataJson.get("uuid").getAsString();
        if (metadata.endsWith(metadataExtension)) {
            metadataExtension = "";
        }
        String tempLocation = TempDirectoryCleaner.getTempDirectory() + File.separator + location + "_temp_" + metadata + metadataExtension;
        File notSureExistsFile = new File(tempLocation);
        if (notSureExistsFile.exists()) {
            Metadata metadataTemp = JaxbUtils.unMarshal(Metadata.class, notSureExistsFile);
            if (DbMetadataUtils.checkMetadataBeforeSave(metadataTemp)) {
                throw new MetadataServiceException("The selected database doesn't contains any tables/views/relationships.");
            }

            //Do check for time difference
            logger.info("Found unsaved metadata. Updating the changes first");
            if (formDataJson.has("newLocation")) {
                location = formDataJson.get("newLocation").getAsString();
                metadata = AdhocUtils.getUuid() + mdExtension;
                formDataJson.remove("newLocation");
                formDataJson.addProperty("location", location);
                formDataJson.addProperty("uuid", metadata);

            }
            String actualLocation = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + location + File.separator + metadata + metadataExtension;
            File destFile = new File(actualLocation);
            if (destFile.exists()) {
                destFile.delete();
            }
            boolean isRenamed = notSureExistsFile.renameTo(destFile);
        }
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

}
