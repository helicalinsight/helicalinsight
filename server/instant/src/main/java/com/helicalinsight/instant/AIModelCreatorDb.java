
package com.helicalinsight.instant;

import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

import java.util.Date;
import java.util.regex.Pattern;

import com.helicalinsight.admin.model.HIResourceAIModel;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

/**
 * The {@code AIModelCreatorDb} class is an implementation of the {@link IComponent} interface, responsible for
 * creating or editing  Reports. It handles the execution of model creation or editing based on
 * the provided JSON form data.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class AIModelCreatorDb implements IComponent {
	/**
     * Executes the model reation or editing based on the provided JSON form data.
     *
     * @param jsonFormData 		 formData containing parameters(like location, state, uuid etc.) for model creation or editing.
     * @return A JSON string representing the result of the operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);

        HIResource hiResource;
        String extension;

        Date createdDate = new Date();

        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = formDataJson.get("location").getAsString();
        JsonObject result = new JsonObject();
        if (formDataJson.get("state").isJsonNull()) {
            throw new IncompleteFormDataException("The model state parameter is null.");
        }
        String state = formDataJson.get("state").toString();
        String metadataLocation = getMetadataLocation(formDataJson);
        HIResource metadataResource = serviceDB.getResourceByUrl(metadataLocation);
        if (metadataResource == null) {
            throw new EfwServiceException("The metadata does not exists for this report");
        }

        String requiredExtension = "." + (JsonUtils.getAiModelExtension());
        if (formDataJson.has("uuid")) {
            String existingName = formDataJson.get("uuid").getAsString();
            hiResource = serviceDB.getResourceByUrl(location + "/" + existingName);
            if (hiResource == null) {
                throw new IllegalStateException("No such report exists");
            }
            if(formDataJson.has("modelName")) {
                String modelName = formDataJson.get("modelName").getAsString();
                hiResource.setTitle(modelName);
            }

            
            instantModelEdit(hiResource, createdDate, formDataJson, state, metadataResource);
            hiResource.setLastUpdatedTime(createdDate);
            serviceDB.editHIResource(hiResource);
            GsonUtility.accumulate(result,"uuid", existingName);
            result.addProperty("location", location);

        } else {
            HIResource folderResource = serviceDB.getResourceByUrl(location);
            Security security = SecurityUtils.securityObject();
            String createdBy = security.getCreatedBy();

            if (!formDataJson.has("modelName")) {
                throw new IllegalArgumentException("The parameter modelName is not present");
            }
            String modelName = formDataJson.get("modelName").getAsString();
            //modelName = DBProcessor.checkAndReplaceSpecialChars(modelName).trim();
            hiResource = serviceDB.getResourceByUrl(location + "/" + checkAndReplaceSpecialChars(modelName) + requiredExtension);
            if (modelName.length() < 3) {
                throw new EfwServiceException("At least 3 characters has to be provided for report name");
            }

            String newName = checkAndReplaceSpecialChars(modelName) + requiredExtension;
            if (hiResource != null) {
                newName = modelName + "_" + String.valueOf(System.currentTimeMillis()).substring(11, 13) + requiredExtension;
            }
            hiResource = new HIResource();
            hiResource.setCreatedBy(Integer.valueOf(createdBy));
            hiResource.setCreated_date(createdDate);
            if (folderResource != null) {
                hiResource.setParentId(folderResource.getResourceId());
            }
            hiResource.setLastUpdatedTime(createdDate);
            hiResource.setFolder(false);
            hiResource.setMigrated(false);
            hiResource.setVisible(true);
            hiResource.setTitle(modelName);
            String resourcePath = DBProcessor.checkAndReplaceSpecialChars(modelName).trim();
            hiResource.setResourceType(resourceTypeServiceDB.getResourceTypeByTypeAndExtension("model", ".model"));
            saveAiModel(hiResource, createdDate, formDataJson, state, metadataResource, createdBy, modelName);
            String resourceUrl = location + "/" + newName;
            hiResource.setResourcePath(resourcePath);
            hiResource.setResourceURL(resourceUrl);
            serviceDB.addHIResource(hiResource);

            GsonUtility.accumulate(result,"uuid", newName);
            result.addProperty("location", location);

        }
        result.addProperty("message", "Successfully saved model file");
        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
        String[] pathSplit = hiResource.getResourceURL().split(Pattern.quote("/"));
        FileInfo fileInfo = bean.prepareFileInfo(location, pathSplit[pathSplit.length - 1]);

        JsonArray data = new JsonArray();
        JsonObject fileInfoObject = JsonParser.parseString(new Gson().toJson(fileInfo)).getAsJsonObject();
        data.add(fileInfoObject);
        result.add("data", data);

        return result.toString();
    }

    /**
     * Saves the Report and HIResource information during model creation.
     *
     * @param hiResource    The HIResource instance representing the report being created.
     * @param createdDate   The date when the report is created.
     * @param formDataJson  The JSON data containing form parameters for the report.
     * @param state         The state of the report.
     * @param metadataResource The metadata resource associated with the report.
     * @param createdBy     The user ID who created the report.
     * @param modelName    The name of the report.
     */
    private void saveAiModel(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource, String createdBy, String modelName) {
        HIResourceAIModel aiModel = new HIResourceAIModel();
        aiModel.setCreatedDate(createdDate);
        aiModel.setCreatedBy(Integer.valueOf(createdBy));
        aiModel.setAiModelName(modelName);
        coreSave(aiModel, createdDate, formDataJson, state, metadataResource);
        hiResource.setAiModel(aiModel);
    }

    /**
     * Handles the report editing process during model creation or editing.
     *
     * @param hiResource    The HIResource instance representing the report being edited.
     * @param createdDate   The date when the report is edited.
     * @param formDataJson  The JSON data containing form parameters for the report.
     * @param state         The state of the report.
     * @param metadataResource The metadata resource associated with the report.
     */
    private void instantModelEdit(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource) {
        HIResourceAIModel aiModel = hiResource.getAiModel();
        coreSave(aiModel, createdDate, formDataJson, state, metadataResource);
    }

    /**
     * Gets the metadata location from the provided JSON form data.
     *
     * @param formDataJson 					 	JSON data containing form parameters for the report.
     * @return The metadata location.
     * @throws IncompleteFormDataException 		If the metadata location or metadataFileName is missing in the form data.
     */
    @NotNull
    private String getMetadataLocation(JsonObject formDataJson) {
        JsonObject metadata = null;
        if (formDataJson.has("metadata")) {
            metadata = formDataJson.getAsJsonObject("metadata");
            if (!metadata.has("location") || !metadata.has("metadataFileName")) {
                throw new IncompleteFormDataException("The metadata has no location or " + "metadataFileName");
            }
        }

        String metadataLocation = metadata.get("location").getAsString() + "/" + metadata.get("metadataFileName").getAsString();
        return metadataLocation;
    }

    /**
     * Saves the HReport information during report creation or editing.
     *
     * @param hReport       	The HIResourceAIModel instance to be saved or updated.
     * @param createdDate   	The date when the report is created or edited.
     * @param formDataJson  	The JSON data containing form parameters for the report.
     * @param state         	The state of the report.
     * @param metadataResource  The metadata resource associated with the report.
     */
    private void coreSave(HIResourceAIModel hReport, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource) {
        hReport.setState(state);
        hReport.setLastUpdatedTime(createdDate);
        if (metadataResource != null) {
            hReport.setHiResourceMetadata(metadataResource.getResourceId());
        }
    }

    /**
     * Indicates whether this component is thread-safe to be cached.
     *
     * @return {@code true} if the component is thread-safe to be cached, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
