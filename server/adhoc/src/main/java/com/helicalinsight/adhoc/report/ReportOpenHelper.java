package com.helicalinsight.adhoc.report;

import java.io.File;

import com.helicalinsight.admin.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;

/**
 * The ReportOpenHelper class provides utility methods to open different types of reports.
 *
 * Created by Rajasekhar on 27-04-2015.
 * Modified by Rajasekhar
 */
public class ReportOpenHelper {
    /**
     * Retrieves an adhoc report from the file system.
     *
     * @param dir      directory where the report is located.
     * @param fileName name of the report file.
     * @return An instance of the adhoc report.
     * @throws EfwServiceException If the report file does not exist or is not supported.
     */
    @Nullable
    public static IResource getAdhocReport(String dir, String fileName) {
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        File reportFile;

        try {
            DataSourceUtils.validate(dir);
            DataSourceUtils.validate(fileName);
            reportFile = new File(solutionDirectory + File.separator + dir + File.separator + fileName);
        } catch (RequiredParameterIsNullException e) {
            throw new EfwServiceException("One of the required parameters dir or file name is " + "null/empty");
        }

        if (!reportFile.exists()) {
            throw new ReportFileNotFoundException("The report file does not exists.");
        }

        String reportExtension = JsonUtils.getReportExtension();
        String hcrExtension = JsonUtils.getHCRExtension();
        String hr = JsonUtils.getHrReportExtension();
        String extensionOfFile = FileUtils.getExtensionOfFile(reportFile);

        if (!((reportExtension.equalsIgnoreCase(extensionOfFile)) || hcrExtension.equalsIgnoreCase(extensionOfFile) || hr.equalsIgnoreCase(extensionOfFile))) {
            throw new EfwServiceException(String.format("Only %s files are supported to be " +
                    "opened" + ".", reportExtension + "," + hcrExtension));
        }

        // Marshal the file
        if (hcrExtension.equals(extensionOfFile)) {
            return JaxbUtils.unMarshal(HCReport.class, reportFile);
        }

        return JaxbUtils.unMarshal(AdhocReport.class, reportFile);
    }

    @Nullable
    public static IResource getAdhocReportDb(String dir, String fileName) {
        return getAdhocReportDb(dir, fileName, false);
    }

    /**
     * Retrieves an ad hoc report from the database.
     *
     * @param dir      directory where the report is located.
     * @param fileName name of the report file.
     * @param isEdit   boolean indicating whether the report is being edited.
     * @return An instance of the adhoc report.
     * @throws EfwServiceException If the report file does not exist or is not supported.
     */
    @Nullable
    public static IResource getAdhocReportDb(String dir, String fileName, boolean isEdit) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

        HIResource hiResource = serviceDB.getResourceByUrl(dir + "/" + fileName);


        if (hiResource == null) {
            throw new ReportFileNotFoundException("The report file does not exists.");
        }

        String reportExtension = JsonUtils.getReportExtension();
        String hr = JsonUtils.getHrReportExtension();
        String extensionOfFile = FileUtils.getExtensionOfFile(new File(fileName));

        if (!((reportExtension.equalsIgnoreCase(extensionOfFile)) || hr.equalsIgnoreCase(extensionOfFile))) {

            throw new EfwServiceException(String.format("Only %s files are supported to be " +
                    "opened" + ".", reportExtension));
        }

        AdhocReport adhocReport = ApplicationContextAccessor.getBean(AdhocReport.class);
        CanvasElements canvas = ApplicationContextAccessor.getBean(CanvasElements.class);
        MetadataReference metadataReference = ApplicationContextAccessor.getBean(MetadataReference.class);

        try {

            if (fileName.endsWith(JsonUtils.getReportExtension())) {
                HIResourceReport adhocJpa = hiResource.getHiResourceReport();
                adhocReport.setReportName(adhocJpa.getReportName());
                canvas.setColumns(JsonParser.parseString(adhocJpa.getCanvasColumns()).getAsJsonArray());
                Integer hiResourceMetadata = adhocJpa.getHiResourceMetadata();
                if (hiResourceMetadata != null) {
                    HIResource hiResourceById = serviceDB.getHIResourceById(hiResourceMetadata);
                    String resourcePath = hiResourceById.getResourcePath();
                    String resourceURL = hiResourceById.getResourceURL();
                    metadataReference.setMetadataFileName(resourcePath + "." + JsonUtils.getMetadataExtension());
                    int lastSeparator = resourceURL.lastIndexOf("/");
                    String location = resourceURL.substring(0, lastSeparator);
                    metadataReference.setLocation(location);

                }
                JsonObject asJsonObject = JsonParser.parseString(adhocJpa.getState()).getAsJsonObject();
                adhocReport.setState(asJsonObject);
            } else {
                HIResourceHReport hiResourceHReport = hiResource.getHiResourceHReport();
                adhocReport.setReportName(hiResourceHReport.getReportName());
                //canvas.setColumns(hiResourceHReport.getCanvasColumns());
                JsonArray asJsonArray = JsonParser.parseString(hiResourceHReport.getCanvasColumns()).getAsJsonArray();
                canvas.setColumns(asJsonArray);
                JsonObject asJsonObject = JsonParser.parseString(hiResourceHReport.getState()).getAsJsonObject();
                adhocReport.setState(asJsonObject);
                Integer hiResourceMetadata = hiResourceHReport.getHiResourceMetadata();
                Integer cubeId = hiResourceHReport.getHiResourceCube();
                Integer resourceid = hiResourceMetadata != null ? hiResourceMetadata : cubeId;

                HIResource hiResourceById = serviceDB.getHIResourceById(resourceid);

                String resourceURL = hiResourceById.getResourceURL();

                int lastSeparator = resourceURL.lastIndexOf("/");
                String location = resourceURL.substring(0, lastSeparator);
                metadataReference.setLocation(location);
                metadataReference.setMetadataFileName(resourceURL.substring(lastSeparator + 1));
                metadataReference.setCube(cubeId != null);

            }
        } catch (Exception e) {
            if (!isEdit) {
                throw e;
            }
        }
        adhocReport.setMetadataReference(metadataReference);
        adhocReport.setCanvasElements(canvas);


        return adhocReport;
    }

    /**
     * Retrieves an instant report from the database.
     *
     * @param dir      directory where the report is located.
     * @param fileName name of the report file.
     * @return An instance of the instant report.
     * @throws EfwServiceException If the report file does not exist or is not supported.
     */
    @Nullable
    public static IResource getInstantReportDb(String dir, String fileName) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

        HIResource hiResource = serviceDB.getResourceByUrl(dir + "/" + fileName);


        if (hiResource == null) {
            throw new ReportFileNotFoundException("The report file does not exists.");
        }

        String hr = JsonUtils.getInstantReportExtension();
        String extensionOfFile = FileUtils.getExtensionOfFile(new File(fileName));

        if (!hr.equalsIgnoreCase(extensionOfFile)) {

            throw new EfwServiceException(String.format("Only %s files are supported to be " +
                    "opened" + ".", hr));
        }

        AdhocReport adhocReport = ApplicationContextAccessor.getBean(AdhocReport.class);
        CanvasElements canvas = ApplicationContextAccessor.getBean(CanvasElements.class);
        MetadataReference metadataReference = ApplicationContextAccessor.getBean(MetadataReference.class);

        HIResourceInstantReport hiResourceHReport = hiResource.getHiResourceInstantReport();
        adhocReport.setReportName(hiResourceHReport.getReportName());
        Integer hiResourceMetadata = hiResourceHReport.getHiResourceAgent();
        if (hiResourceMetadata != null) {
            HIResource hiResourceById = serviceDB.getHIResourceById(hiResourceMetadata);
            String resourcePath = hiResourceById.getResourcePath();
            String resourceURL = hiResourceById.getResourceURL();

            int lastSeparator = resourceURL.lastIndexOf("/");
            String location = resourceURL.substring(0, lastSeparator);
            metadataReference.setLocation(location);
            metadataReference.setMetadataFileName(resourcePath + "." + JsonUtils.getMetadataExtension());
        }
        JsonObject asJsonObject = JsonParser.parseString(hiResourceHReport.getState()).getAsJsonObject();
        adhocReport.setState(asJsonObject);
        adhocReport.setMetadataReference(metadataReference);
        adhocReport.setCanvasElements(canvas);


        return adhocReport;
    }

    public static IResource getAgentStateDb(String dir, String fileName) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

        HIResource hiResource = serviceDB.getResourceByUrl(dir + "/" + fileName);


        if (hiResource == null) {
            throw new ReportFileNotFoundException("The agent file does not exists.");
        }

        String hr = JsonUtils.getAiAgentExtension();
        String extensionOfFile = FileUtils.getExtensionOfFile(new File(fileName));

        if (!hr.equalsIgnoreCase(extensionOfFile)) {

            throw new EfwServiceException(String.format("Only %s files are supported to be " +
                    "opened" + ".", hr));
        }

        AdhocReport adhocReport = ApplicationContextAccessor.getBean(AdhocReport.class);
        MetadataReference metadataReference = ApplicationContextAccessor.getBean(MetadataReference.class);

        HIResourceAIAgent hiResourceHReport = hiResource.getAiAgent();
        adhocReport.setReportName(hiResourceHReport.getAgentName());
        Integer hiResourceMetadata = hiResourceHReport.getHiResourceMetadata();
        if (hiResourceMetadata != null) {
            HIResource hiResourceById = serviceDB.getHIResourceById(hiResourceMetadata);
            String resourcePath = hiResourceById.getResourcePath();
            String resourceURL = hiResourceById.getResourceURL();

            int lastSeparator = resourceURL.lastIndexOf("/");
            String location = resourceURL.substring(0, lastSeparator);
            metadataReference.setLocation(location);
            metadataReference.setMetadataFileName(resourcePath + "." + JsonUtils.getMetadataExtension());
        }
        JsonObject asJsonObject = JsonParser.parseString(hiResourceHReport.getState()).getAsJsonObject();
        adhocReport.setState(asJsonObject);
        adhocReport.setMetadataReference(metadataReference);


        return adhocReport;
    }

    /**
     * Converts the content of an adhoc report to JSON format.
     *
     * @param adhocReport adhoc report object.
     * @return A JsonObject representing the content of the adhoc report.
     */
    @NotNull
    public static JsonObject reportContentAsJson(@NotNull AdhocReport adhocReport) {
        CanvasElements canvasElements = adhocReport.getCanvasElements();
        JsonArray columns = canvasElements.getColumns();
        String groups = canvasElements.getGroups();
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();

        JsonObject canvas = new JsonObject();

        if (columns != null) {
            GsonUtility.accumulate(canvas, "columns", columns);
        }

        if (groups != null) {
            GsonUtility.accumulate(canvas, "groups", groups);
        }
        if (!canvas.entrySet().isEmpty())
            GsonUtility.accumulate(data, "canvas", canvas);
        GsonUtility.accumulate(data, "reportName", adhocReport.getReportName());

        JsonObject metadata = new JsonObject();


        MetadataReference metadataReference = adhocReport.getMetadataReference();
        if (metadataReference != null) {
            JsonObject md = new JsonObject();
            md.addProperty("location", metadataReference.getLocation());
            md.addProperty("metadataFileName", metadataReference.getMetadataFileName());
            data.add("metadata", md);

        }
        metadataReference = null;
        if (metadataReference != null) {
            if (Boolean.FALSE.equals(metadataReference.getCube())) {
                String location = metadataReference.getLocation();
                GsonUtility.accumulate(metadata, "location", location);
                String metadataFileName = metadataReference.getMetadataFileName();
                GsonUtility.accumulate(metadata, "metadataFileName", metadataFileName);
                GsonUtility.accumulate(data, "metadata", metadata);
            } else {
                String location = metadataReference.getLocation();
                GsonUtility.accumulate(metadata, "location", location);
                String metadataFileName = metadataReference.getMetadataFileName();
                GsonUtility.accumulate(metadata, "fileName", metadataFileName);

                GsonUtility.accumulate(data, "cube", metadata);
                HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

                HIResource cubeResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
                HICubeDAOService hiCubeDAOService = ApplicationContextAccessor.getBean(HICubeDAOService.class);
                String cubeAsJsonStr = hiCubeDAOService.getCubeAsJsonObj(cubeResource.getResourceId()).toString();
                JsonObject cubeAsJsonObj = JsonParser.parseString(cubeAsJsonStr).getAsJsonObject();
                JsonObject mds = cubeAsJsonObj.getAsJsonObject("metadata");
                data.add("cubes", cubeAsJsonObj.getAsJsonArray("cubes"));
                GsonUtility.accumulate(data, "metadata", mds);
            }
        } else {
            //response.addProperty("message", "Metadata resource not found.");
        }

        GsonUtility.accumulate(data, "state", adhocReport.getState());
        response.add("data", data);
        return response;
    }

    /**
     * Converts the content of an HCReport to JSON format.
     *
     * @param hcrReport HCReport object.
     * @return A JsonObject representing the content of the HCReport.
     */
    @NotNull
    public static JsonObject newReportContentAsJson(@NotNull HCReport hcrReport) {
        String diagramData = hcrReport.getDiagramData();
        String directory = hcrReport.getDirectory();
        String formData = hcrReport.getFormData();
        String name = hcrReport.getName();
        String state = hcrReport.getState();
        JsonObject data = new JsonObject();
        GsonUtility.accumulate(data, "diagramData", diagramData);
        GsonUtility.accumulate(data, "directory", directory);
        GsonUtility.accumulate(data, "previewFormData", formData);
        GsonUtility.accumulate(data, "state", state);
        GsonUtility.accumulate(data, "reportName", name);

        return data;
    }


    @NotNull
    public static JsonObject getAgentContentAsJson(@NotNull AdhocReport adhocReport) {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();



        GsonUtility.accumulate(data, "agentName", adhocReport.getReportName());

        JsonObject metadata = new JsonObject();


        MetadataReference metadataReference = adhocReport.getMetadataReference();
        if (metadataReference != null) {
            JsonObject md = new JsonObject();
            md.addProperty("location", metadataReference.getLocation());
            md.addProperty("metadataFileName", metadataReference.getMetadataFileName());
            data.add("metadata", md);

        }
        metadataReference = null;
        if (metadataReference != null) {
            if (Boolean.FALSE.equals(metadataReference.getCube())) {
                String location = metadataReference.getLocation();
                GsonUtility.accumulate(metadata, "location", location);
                String metadataFileName = metadataReference.getMetadataFileName();
                GsonUtility.accumulate(metadata, "metadataFileName", metadataFileName);
                GsonUtility.accumulate(data, "metadata", metadata);
            }
        }

        GsonUtility.accumulate(data, "state", adhocReport.getState());
        response.add("data", data);
        return response;
    }
}