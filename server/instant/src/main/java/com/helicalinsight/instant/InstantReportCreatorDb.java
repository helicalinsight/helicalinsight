package com.helicalinsight.instant;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
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
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.regex.Pattern;

import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

/**
 * The {@code InstantReportCreatorDb} class is an implementation of the {@link IComponent} interface, responsible for
 * creating or editing  Reports. It handles the execution of instant report creation or editing based on
 * the provided JSON form data.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class InstantReportCreatorDb implements IComponent {
    /**
     * Executes the instant report creation or editing based on the provided JSON form data.
     *
     * @param jsonFormData formData containing parameters(like location, state, uuid etc.) for instant report creation or editing.
     * @return A JSON string representing the result of the operation.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);

        HIResource hiResource;
        String extension;

        HIResourceInstantReport instantReport;
        Date createdDate = new Date();

        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = formDataJson.get("location").getAsString();
        JsonObject result = new JsonObject();
        if (formDataJson.get("state").isJsonNull()) {
            throw new IncompleteFormDataException("The report state parameter is null.");
        }
        String state = formDataJson.get("state").toString();
        String modelLocation = getModelLocation(formDataJson);
        HIResource modelResource = serviceDB.getResourceByUrl(modelLocation);
        if (modelResource == null) {
            throw new EfwServiceException("The model does not exists for this report");
        }

        String requiredExtension = "." + (JsonUtils.getInstantReportExtension());
        if (formDataJson.has("uuid")) {
            String existingName = formDataJson.get("uuid").getAsString();

            hiResource = serviceDB.getResourceByUrl(location + "/" + existingName);
            if (hiResource == null) {
                throw new IllegalStateException("No such report exists");
            }
            instantReportEdit(hiResource, createdDate, formDataJson, state, modelResource);
            hiResource.setLastUpdatedTime(createdDate);
            serviceDB.editHIResource(hiResource);
            GsonUtility.accumulate(result, "uuid", existingName);
            result.addProperty("location", location);

        } else {
            HIResource folderResource = serviceDB.getResourceByUrl(location);
            Security security = SecurityUtils.securityObject();
            String createdBy = security.getCreatedBy();

            if (!formDataJson.has("reportName")) {
                throw new IllegalArgumentException("The parameter reportName is not present");
            }
            String reportName = formDataJson.get("reportName").getAsString();
            //reportName = DBProcessor.checkAndReplaceSpecialChars(reportName).trim();
            hiResource = serviceDB.getResourceByUrl(location + "/" + checkAndReplaceSpecialChars(reportName) + requiredExtension);
            if (reportName.length() < 3) {
                throw new EfwServiceException("At least 3 characters has to be provided for report name");
            }

            String newName = checkAndReplaceSpecialChars(reportName) + requiredExtension;
            if (hiResource != null) {
                newName = reportName + "_" + String.valueOf(System.currentTimeMillis()).substring(11, 13) + requiredExtension;
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
            hiResource.setTitle(reportName);
            hiResource.setTitle(reportName);
            String resourcePath = DBProcessor.checkAndReplaceSpecialChars(reportName).trim();
            hiResource.setResourceType(resourceTypeServiceDB.getResourceTypeByTypeAndExtension("instant", ".instant"));
            saveHReport(hiResource, createdDate, formDataJson, state, modelResource, createdBy, reportName);
            String resourceUrl = location + "/" + newName;
            hiResource.setResourcePath(resourcePath);
            hiResource.setResourceURL(resourceUrl);
            serviceDB.addHIResource(hiResource);

            GsonUtility.accumulate(result, "uuid", newName);
            result.addProperty("location", location);

        }
        result.addProperty("message", "Successfully saved instant file");
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
     * Saves the Report and HIResource information during instant report creation.
     *
     * @param hiResource       The HIResource instance representing the report being created.
     * @param createdDate      The date when the report is created.
     * @param formDataJson     The JSON data containing form parameters for the report.
     * @param state            The state of the report.
     * @param modelResource The modelResource resource associated with the report.
     * @param createdBy        The user ID who created the report.
     * @param reportName       The name of the report.
     */
    private void saveHReport(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource modelResource, String createdBy, String reportName) {
        HIResourceInstantReport hReport = new HIResourceInstantReport();
        hReport.setCreatedDate(createdDate);
        hReport.setCreatedBy(Integer.valueOf(createdBy));
        hReport.setReportName(reportName);
        coreSave(hReport, createdDate, formDataJson, state, modelResource);
        hiResource.setHiResourceInstantReport(hReport);
    }

    /**
     * Handles the report editing process during instant report creation or editing.
     *
     * @param hiResource    The HIResource instance representing the report being edited.
     * @param createdDate   The date when the report is edited.
     * @param formDataJson  The JSON data containing form parameters for the report.
     * @param state         The state of the report.
     * @param modelResource The modelResource resource associated with the report.
     */
    private void instantReportEdit(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource modelResource) {
        HIResourceInstantReport adhocReport = hiResource.getHiResourceInstantReport();
        coreSave(adhocReport, createdDate, formDataJson, state, modelResource);
    }

    /**
     * Gets the modelResource location from the provided JSON form data.
     *
     * @param formDataJson JSON data containing form parameters for the report.
     * @return The modelResource location.
     * @throws IncompleteFormDataException If the modelResource location or modelResource is missing in the form data.
     */
    @NotNull
    private String getModelLocation(JsonObject formDataJson) {
        JsonObject modelResource = null;
        if (formDataJson.has("metadata")) {
            modelResource = formDataJson.getAsJsonObject("metadata");
            if (!modelResource.has("location") || !modelResource.has("metadataFileName")) {
                throw new IncompleteFormDataException("The metadata has no location or metadataFileName");
            }
        }

        String metadataLocation = modelResource.get("location").getAsString() + "/" + modelResource.get("metadataFileName").getAsString();
        return metadataLocation;
    }

    /**
     * Saves the HReport information during report creation or editing.
     *
     * @param hReport       The HIResourceInstantReport instance to be saved or updated.
     * @param createdDate   The date when the report is created or edited.
     * @param formDataJson  The JSON data containing form parameters for the report.
     * @param state         The state of the report.
     * @param modelResource The modelResource resource associated with the report.
     */
    private void coreSave(HIResourceInstantReport hReport, Date createdDate, JsonObject formDataJson, String state, HIResource modelResource) {
        hReport.setState(state);
        hReport.setLastUpdatedTime(createdDate);
        if (modelResource != null) {
            hReport.setHiResourceModel(modelResource.getResourceId());
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
