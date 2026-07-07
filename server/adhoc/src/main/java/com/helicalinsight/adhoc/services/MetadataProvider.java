package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class MetadataProvider implements IService {

    /**
     * The method uses the metadata file and converts it into json format. 
     * library and there by xml structure is not hardcoded at the service level. Jaxb limitation is there by avoided
     * and xml structure can be changed.
     *
     * @param type        The type
     * @param serviceType The serviceType
     * @param service     The actual service
     * @param formData    The formData from http request
     * @return The metadata of the data source
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formJson = new Gson().fromJson(formData,JsonObject.class);
        String location=null;
        String dataSourceType;
        String metadataFileName=null;
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        Metadata databaseMetadata = null;
        if (!(formJson.has("metadataFileName") && formJson.has("location"))) {
            validateGlobalOrEFWD(formJson);
            dataSourceType = formJson.get("type").getAsString();
        } else {
             metadataFileName = formJson.get("metadataFileName").getAsString();
            location = formJson.get("location").getAsString();
            if (formJson.has("newLocation")) {
                location = formJson.get("newLocation").getAsString();
                formJson.addProperty("location", location);
            }
            HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
            if (metadataResource == null) {
                throw new EfwServiceException("The file " + metadataFileName + " does not exists " +
                        "in the specified location.");
            }

            databaseMetadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());
            if (databaseMetadata == null) {
                throw new EfwServiceException("Metadata is corrupted or is blank.");
            }

            String connectionType = databaseMetadata.getConnectionType();
            if (connectionType != null) {
                dataSourceType = connectionType;
            } else {
                dataSourceType = databaseMetadata.getConnectionDetails().getConnectionType();
            }
        }

        AdhocServiceUtils.addExtraDataForNormalProcess(formJson, dataSourceType);
        if( "getMetadataForEdit".equalsIgnoreCase(service)) {
        	formJson.addProperty("mode", "edit");
        }
        String result = ServiceUtils.executeService(type, serviceType, service, formJson.toString());
        JsonObject jsonResult = new Gson().fromJson(result,JsonObject.class);

        JsonObject responseData = ControllerUtils.getDataFromResponse(jsonResult);
        responseData.addProperty("metadataName", databaseMetadata != null ?databaseMetadata.getFileName():"");//accumulate
        responseData.addProperty("metadataDir", location);//accumulate
        responseData.addProperty("databaseName",( databaseMetadata!=null && databaseMetadata.getDatabase()!=null?databaseMetadata.getDatabase().getName():""));//accumulate


        return jsonResult.toString();
    }



    private void validateGlobalOrEFWD(@NotNull JsonObject formData) {

        String type = GsonUtility.optString(formData, "type");
        String id = GsonUtility.optString(formData,"id");

        if (StringUtils.isBlank(type) || StringUtils.isBlank(id)) {
            throw new RequiredParameterIsNullException("The type or id is null");
        }

        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {
            String dir = GsonUtility.optString(formData,"dir");
            if (StringUtils.isBlank(dir)) {
                throw new RequiredParameterIsNullException("The parameter dir is null");
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}