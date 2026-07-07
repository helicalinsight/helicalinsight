
package com.helicalinsight.adhoc;

import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.HIResourceReport;
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
 * The {@code AdhocReportCreatorDb} class is an implementation of the {@link IComponent} interface, providing
 * functionalities for creating and managing ad-hoc reports stored in a database. It handles the execution of the ad-hoc
 * report creation process, including the creation, editing, and saving of ad-hoc reports and HR reports.
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class AdhocReportCreatorDb implements IComponent {
	/**
     * This method is responsible for executing the component for creating and managing ad-hoc reports stored in a database. Handles the entire ad-hoc
     * report creation process, including copying metadata, saving reports, and obtaining ad-hoc report instances.
     *
     * @param jsonFormData 		 form data containing parameters for the ad-hoc report creation(location, uuid etc).
     * @return A JSON-formatted string containing the UUID and status message of the created or updated ad-hoc report.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);

        HIResource hiResource;
        String extension;

        HIResourceHReport hReport;
        Date createdDate = new Date();

        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String location = formDataJson.get("location").getAsString();
        JsonObject result = new JsonObject();
        String state = new Gson().toJson(formDataJson.get("state"));
        if (state == null) {
            throw new IncompleteFormDataException("The report state parameter is null.");
        }
        String metadataLocation = getMetadataLocation(formDataJson);
        String cubeLocation = getCubeLocation(formDataJson);
        HIResource metadataResource = metadataLocation!=null?serviceDB.getResourceByUrl(metadataLocation):null;
        HIResource cubeResource = cubeLocation!=null?serviceDB.getResourceByUrl(cubeLocation):null;
        if (metadataResource == null && cubeResource==null) {
            throw new EfwServiceException("The metadata/cube does not exists for this report");
        }

        Boolean isHrReport = GsonUtility.optBoolean(formDataJson,"isHrReport");
        String requiredExtension = "." + (isHrReport ? JsonUtils.getHrReportExtension() : JsonUtils.getReportExtension());
        if (formDataJson.has("uuid")) {
            String existingName = formDataJson.get("uuid").getAsString();
            if (existingName.endsWith(JsonUtils.getHrReportExtension())) {
                isHrReport = true;
            }
            hiResource = serviceDB.getResourceByUrl(location + "/" + existingName);
            if (hiResource == null) {
                throw new IllegalStateException("No such report exists");
            }
            if (isHrReport) {
                hReportEdit(hiResource, createdDate, formDataJson, state, metadataResource, cubeResource);
            } else {
                adhocReportEdit(hiResource, createdDate, formDataJson, state, metadataResource);
            }
            hiResource.setLastUpdatedTime(createdDate);
            serviceDB.editHIResource(hiResource);
            GsonUtility.accumulate(result,"uuid", existingName);
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

            List a = new ArrayList();

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
            String resourcePath = DBProcessor.checkAndReplaceSpecialChars(reportName).trim() + "." + JsonUtils.getReportExtension();

            if (isHrReport) {
                hiResource.setTitle(reportName);
                resourcePath = DBProcessor.checkAndReplaceSpecialChars(reportName).trim();
                hiResource.setResourceType(resourceTypeServiceDB.getResourceTypeByTypeAndExtension("hr", ".hr"));
                saveHReport(hiResource, createdDate, formDataJson, state, metadataResource, createdBy, reportName,cubeResource);
            } else {
                hiResource.setTitle(reportName);
                hiResource.setResourceType(resourceTypeServiceDB.getResourceTypeByTypeAndExtension("report", ".report"));
                saveReport(hiResource, createdDate, formDataJson, state, metadataResource, createdBy, reportName);
            }
            String resourceUrl = location + "/" + newName;
            hiResource.setResourcePath(resourcePath);
            hiResource.setResourceURL(resourceUrl);
            serviceDB.addHIResource(hiResource);

            GsonUtility.accumulate(result,"uuid", newName);
            result.addProperty("location", location);

        }
        result.addProperty("message", "Successfully saved report file");
        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
        String[] pathSplit = hiResource.getResourceURL().split(Pattern.quote("/"));
        FileInfo fileInfo = bean.prepareFileInfo(location, pathSplit[pathSplit.length - 1]);

        JsonArray data = new JsonArray();
        String json = new Gson().toJson(fileInfo);
        JsonObject fileInfoObject = new Gson().fromJson(json, JsonObject.class);
        data.add(fileInfoObject);
        result.add("data", data);

        return result.toString();
    }
    /**
     * Saves the ad-hoc report with the provided parameters.
     *
     * @param hiResource 			 {@code HIResource} instance representing the ad-hoc report.
     * @param createdDate 			 date when the ad-hoc report is created.
     * @param formDataJson 			 JSON-formatted form data containing parameters for the ad-hoc report creation.
     * @param state 				 state of the ad-hoc report as a JSON string.
     * @param metadataResource 		 {@code HIResource} instance representing the associated metadata.
     * @param createdBy 			 user ID of the creator.
     * @param reportName 			 name of the ad-hoc report.
     */
    private void saveReport(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource, String createdBy, String reportName) {
        HIResourceReport adhocReport = new HIResourceReport();
        adhocReport.setCreatedDate(createdDate);
        adhocReport.setCreatedBy(createdBy);
        adhocReport.setReportName(reportName);
        coreSave(adhocReport, createdDate, formDataJson, state, metadataResource);
        hiResource.setHiResourceReport(adhocReport);
    }
    /**
     * Saves the HR report with the provided parameters.
     *
     * @param hiResource 			 {@code HIResource} instance representing the HR report.
     * @param createdDate 			 date when the HR report is created.
     * @param formDataJson 			 JSON-formatted form data containing parameters for the HR report creation.
     * @param state 				 state of the HR report as a JSON string.
     * @param metadataResource 		 {@code HIResource} instance for metadata.
     * @param cubeResource 			 {@code HIResource} instance for cube (for HR reports).
     */
    private void saveHReport(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource, String createdBy, String reportName, HIResource cubeResource) {
        HIResourceHReport hReport = new HIResourceHReport();
        hReport.setCreatedDate(createdDate);
        hReport.setCreatedBy(Integer.valueOf(createdBy));
        hReport.setReportName(reportName);
        coreSaveHR(hReport, createdDate, formDataJson, state, metadataResource, cubeResource);
        hiResource.setHiResourceHReport(hReport);
    }

    /**
     * Edits the ad-hoc report with the provided parameters.
     *
     * @param hiResource 			 {@code HIResource} instance representing the ad-hoc report.
     * @param createdDate 			 date when the ad-hoc report is created.
     * @param formDataJson 			 JSON-formatted form data containing parameters for the ad-hoc report creation.
     * @param state 				 state of the ad-hoc report as a JSON string.
     * @param metadataResource 		 {@code HIResource} instance for metadata.
     */
    private void adhocReportEdit(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource) {
        HIResourceReport adhocReport = hiResource.getHiResourceReport();
        coreSave(adhocReport, createdDate, formDataJson, state, metadataResource);
    }

    /**
     * Edits the HR report with the provided parameters.
     *
     * @param hiResource 				 {@code HIResource} instance representing the HR report.
     * @param createdDate 				 date when the HR report is created.
     * @param formDataJson 				 JSON-formatted form data containing parameters for the HR report creation.
     * @param state 					 state of the HR report as a JSON string.
     * @param metadataResource 			 {@code HIResource} instance representing the associated metadata.
     * @param cubeResource 				 {@code HIResource} instance representing the associated cube (for HR reports).
     */
    private void hReportEdit(HIResource hiResource, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource, HIResource cubeResource) {
        HIResourceHReport adhocReport = hiResource.getHiResourceHReport();
        coreSaveHR(adhocReport, createdDate, formDataJson, state, metadataResource, cubeResource);
    }


    /**
     * Retrieves the metadata location from the form data.
     *
     * @param formDataJson 		 JSON-formatted form data containing parameters for the ad-hoc report creation.
     * @return The metadata location as a string.
     */
    private String getMetadataLocation(JsonObject formDataJson) {
        JsonObject metadata = null;
        if (formDataJson.has("metadata")) {
            metadata = formDataJson.getAsJsonObject("metadata");
            if (!metadata.has("location") || !metadata.has("metadataFileName")) {
                throw new IncompleteFormDataException("The metadata has no location or " + "metadataFileName");
            }
        return  metadata.get("location").getAsString() + "/" + metadata.get("metadataFileName").getAsString();
        }
        return null;

    }
    /**
     * Retrieves the cube location from the form data.
     *
     * @param formDataJson 			 JSON-formatted form data containing parameters for the HR report creation.
     * @return The cube location as a string.
     */
    private String getCubeLocation(JsonObject formDataJson) {
        if (formDataJson.has("cube")) {
            JsonObject metadata = formDataJson.getAsJsonObject("cube");
            return metadata.get("location").getAsString() + "/" + metadata.get("fileName").getAsString();
        }
        return null;

    }
    /**
     * Core method for saving properties common to both ad-hoc reports and HR reports.
     *
     * @param adhocReport 			 {@code HIResourceReport} instance to set parameters.
     * @param createdDate 			 date when the report is created.
     * @param formDataJson 			 JSON-formatted form data containing parameters for the report creation.
     * @param state 				 state of the report as a JSON string.
     * @param metadataResource 		 {@code HIResource} instance to set metadata.
     */
    private void coreSave(HIResourceReport adhocReport, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource) {
        adhocReport.setState(state);
        adhocReport.setCanvasColumns(formDataJson.get("columns").getAsString());
        adhocReport.setLastUpdatedTime(createdDate);
        if (metadataResource != null) {
            adhocReport.setHiResourceMetadata(metadataResource.getResourceId());
        }
    }
    /**
     * Core method for saving properties specific to HR reports.
     *
     * @param hReport 			 		{@code HIResourceHReport} instance to set parameters.
     * @param createdDate 		 		date when the HR report is created.
     * @param formDataJson 		 		JSON-formatted form data containing parameters for the HR report creation.
     * @param state 					state of the HR report as a JSON string.
     * @param metadataResource 			{@code HIResource} instance to store metadata data.
     * @param cubeResource 				{@code HIResource} instance to store cube details.
     */
    private void coreSaveHR(HIResourceHReport hReport, Date createdDate, JsonObject formDataJson, String state, HIResource metadataResource, HIResource cubeResource) {
        hReport.setState(state);
        hReport.setCanvasColumns(new Gson().toJson(formDataJson.get("columns")));
        hReport.setLastUpdatedTime(createdDate);
        if (metadataResource != null) {
            hReport.setHiResourceMetadata(metadataResource.getResourceId());
        }
        if (cubeResource != null) {
            hReport.setHiResourceCube(cubeResource.getResourceId());
        }
    }

    /**
     * It tells whether the component is thread-safe to cache.
     * @return {@code true} the component is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
