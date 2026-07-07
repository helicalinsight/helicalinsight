package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.adhoc.report.AdhocReport;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code AdhocReportCreator} class is an implementation of the {@link IComponent} interface, serving as a
 * component for creating and managing ad-hoc reports . It handles the execution of the ad-hoc report
 * creation process and provides methods for copying metadata, saving reports, and obtaining ad-hoc report instances.
 *
 * @author Rajasekhar
 * @since 20-03-2015
 */
@SuppressWarnings("unused")
public class AdhocReportCreator implements IComponent {

	/**
     * Executes the component for creating and managing ad-hoc reports. Handles the entire ad-hoc report creation
     * process, including copying metadata, saving reports, and obtaining ad-hoc report instances.
     *
     * @param jsonFormData 			 form data containing parameters for the ad-hoc report creation(uniqueId,state,location etc.).
     * @return JSON-formatted string containing the UUID of the created or updated ad-hoc report.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String location;
        String columns;
        String groups = null;
        JsonObject state;
        String reportName = null;
        String uniqueId = null;
        JsonObject metadata = null;

        if (formDataJson.has("uniqueId")) {
            uniqueId = formDataJson.get("uniqueId").getAsString();
        }

        try {
            state = formDataJson.getAsJsonObject("state");
            location = formDataJson.get("location").getAsString();
            columns = formDataJson.get("columns").getAsString();
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ex);
        }

        String uuid = null;

        if (formDataJson.has("uuid")) {
            uuid = formDataJson.get("uuid").getAsString();
        }

        if (formDataJson.has("metadata")) {
            metadata = formDataJson.getAsJsonObject("metadata");
            if (!metadata.has("location") || !metadata.has("metadataFileName")) {
                throw new IncompleteFormDataException("The metadata has no location or " + "metadataFileName");
            }
        }

        if (uuid == null) {
            if (!formDataJson.has("reportName")) {
                throw new IllegalArgumentException("The parameter reportName is not present");
            }
            reportName = formDataJson.get("reportName").getAsString();
        }

        String description = null;

        if (formDataJson.has("groups")) {
            groups = formDataJson.get("groups").getAsString();
        }

        if (formDataJson.has("description")) {
            description = formDataJson.get("description").getAsString();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("location", location);

        if (reportName != null) {
            parameters.put("reportName", reportName);
        }

        parameters.put("columns", columns);

        if (groups != null) {
            parameters.put("groups", groups);
        }

        if (uniqueId != null) {
            parameters.put("uniqueId", uniqueId);
        }

        if (state != null) {
            parameters.put("state", state.toString());
        } else {
            throw new IncompleteFormDataException("The report state parameter is null.");
        }

        boolean firstTime = true;

        if (uuid != null) {
            firstTime = false;
            parameters.put("uuid", uuid);
        }

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        if (!firstTime) {
            File resource = new File(solutionDirectory + File.separator + location + File.separator + uuid);
            if (!resource.exists()) {
                throw new EfwServiceException("The file doesn't exist in the given location");
            }
            reportName = JsonUtils.newGetAsJson(resource).get("reportName").getAsString();
        }
        Boolean isHrReport = GsonUtility.optBoolean(formDataJson,"isHrReport");
        return process(solutionDirectory, uuid, location, reportName, columns, groups, uniqueId, state, description,
                firstTime, metadata,isHrReport);
    }
    /**
     * Processes the ad-hoc report creation, including copying metadata files, saving reports, and handling various
     * parameters.
     *
     * @param solutionDirectory 	 	solution directory where reports are stored.
     * @param uuid              		UUID associated with the ad-hoc report.
     * @param location          		location of the ad-hoc report within the solution directory.
     * @param reportName        		name of the ad-hoc report.
     * @param columns           		columns used in the ad-hoc report.
     * @param groups            		groups defined in the ad-hoc report.
     * @param uniqueId          		unique identifier associated with the metadata.
     * @param state             		state of the ad-hoc report as a JSON object.
     * @param description       		description of the ad-hoc report.
     * @param firstTime         		Flag indicating whether it is the first time creating the ad-hoc report.
     * @param metadata          		metadata associated with the ad-hoc report as a JSON object.
     * @param isHrReport        		Flag indicating whether the report is an HR report.
     * @return A JSON-formatted string containing the UUID of the created or updated ad-hoc report.
     */
    private String process(String solutionDirectory, String uuid, String location, String reportName, String columns,
                           String groups, String uniqueId, @NotNull JsonObject state, String description,
                           boolean firstTime, @Nullable JsonObject metadata,Boolean isHrReport) {
        String reportFileName;
        if (firstTime) {
            uuid = AdhocUtils.getUuid();
            reportFileName = solutionDirectory + File.separator + location + File.separator + uuid + ".";
        } else {
            reportFileName = solutionDirectory + File.separator + location + File.separator + uuid;
        }

        String metadataExtension = JsonUtils.getMetadataExtension();
        String reportExtension = isHrReport?JsonUtils.getHrReportExtension():JsonUtils.getReportExtension();

        try {
            if ((metadata == null) && firstTime) {
                copyMetadataFile(reportName, uniqueId, reportFileName, metadataExtension);
            }
            saveReport(location, metadataExtension, columns, groups, state, description, reportFileName, uuid,
                    reportName, firstTime, metadata, reportExtension);
        } catch (Exception ex) {
            throw new EfwServiceException(ex);
        }

        JsonObject result;
        result = new JsonObject();
        if (firstTime) {
            GsonUtility.accumulate(result,"uuid", uuid + "." + reportExtension);
        } else {
            GsonUtility.accumulate(result,"uuid", uuid);
        }
        return result.toString();
    }
    /**
     * Copies the metadata file by updating its file name based on the provided parameters.
     *
     * @param reportName      		 	name of the ad-hoc report.
     * @param uniqueId         		 	unique identifier associated with the metadata.
     * @param reportFileName   	 		file name of the ad-hoc report.
     * @param metadataExtension 		file extension for metadata files.
     */
    private void copyMetadataFile(String reportName, String uniqueId, String reportFileName, String extension) {
        File tempDirectory = TempDirectoryCleaner.getTempDirectory();

        File metadataFile = new File(tempDirectory.getAbsolutePath() + File.separator +
                uniqueId + "." + extension);

        Metadata metadata = JaxbUtils.unMarshal(Metadata.class, metadataFile);
        metadata.setFileName(reportName);
        File destination = new File(reportFileName + extension);
        synchronized (this) {
            JaxbUtils.marshal(metadata, destination);
        }
    }
    /**
     * Saves the ad-hoc report by updating or creating its XML file based on the provided parameters.
     *
     * @param location           	 	location of the ad-hoc report within the solution directory.
     * @param metadataExtension  	 	file extension for metadata files.
     * @param columns            	 	columns used in the ad-hoc report.
     * @param groups             	 	groups defined in the ad-hoc report.
     * @param state              	 	state of the ad-hoc report as a JSON object.
     * @param description        	 	description of the ad-hoc report.
     * @param reportFileName     	 	file name of the ad-hoc report.
     * @param uuid               	 	UUID associated with the ad-hoc report.
     * @param reportName         	 	name of the ad-hoc report.
     * @param firstTime          		Flag indicating whether it is the first time creating the ad-hoc report.
     * @param metadata           	 	metadata associated with the ad-hoc report as a JSON object.
     * @param reportExtension    	 	file extension for ad-hoc report files.
     */
    private void saveReport(String location, String metadataExtension, String columns, String groups,
                            @NotNull JsonObject state, String description, @NotNull String reportFileName,
                            String uuid, String reportName, boolean firstTime, JsonObject metadata,
                            String reportExtension) {
        File reportFile;
        AdhocReport adhocReport;
        if (firstTime) {
            reportFile = new File(reportFileName + reportExtension);/*reportFileName already has period(.)*/
            adhocReport = getAdhocReport(location, metadataExtension, columns, groups, state, description, uuid,
                    reportName, metadata);
        } else {
            reportFile = new File(reportFileName);
            adhocReport = JaxbUtils.unMarshal(AdhocReport.class, reportFile);
            CanvasElements canvasElements = adhocReport.getCanvasElements();

            /*If a metadataFileName and location are changed for an existing report*/
            if ((metadata != null) && metadata.has("metadataFileName") && metadata.has("location")) {
                MetadataReference metadataReference = adhocReport.getMetadataReference();
                metadataReference.setMetadataFileName(metadata.get("metadataFileName").getAsString());
                metadataReference.setLocation(metadata.get("location").getAsString());
            }

            setProperties(columns, groups, state, adhocReport, canvasElements);
        }

        synchronized (this) {
            JaxbUtils.marshal(adhocReport, reportFile);
        }
    }
    /**
     * Creates and returns an instance of {@code AdhocReport} based on the provided parameters.
     *
     * @param location 					 location of the ad-hoc report within the solution directory.
     * @param metadataExtension 		 file extension for metadata files.
     * @param columns 					 columns used in the ad-hoc report.
     * @param groups 					 groups defined in the ad-hoc report.
     * @param state 					 state of the ad-hoc report as a JSON object.
     * @param description 				 description of the ad-hoc report.
     * @param uuid 						 UUID associated with the ad-hoc report.
     * @param reportName 				 name of the ad-hoc report.
     * @param metadata 					 metadata associated with the ad-hoc report as a JSON object.
     * @return An instance of {@code AdhocReport}.
     */
    private AdhocReport getAdhocReport(String location, String metadataExtension, String columns, String groups,
                                       @NotNull JsonObject state, @Nullable String description, String uuid,
                                       String reportName, @Nullable JsonObject metadata) {
        AdhocReport adhocReport = ApplicationContextAccessor.getBean(AdhocReport.class);
        CanvasElements canvasElements = ApplicationContextAccessor.getBean(CanvasElements.class);
        adhocReport.setReportName(reportName);

        if (description != null) {
            adhocReport.setDescription(description);
        }

        setProperties(columns, groups, state, adhocReport, canvasElements);

        MetadataReference metadataReference = ApplicationContextAccessor.getBean(MetadataReference.class);
        if (metadata == null) {
            metadataReference.setMetadataFileName(uuid + "." + metadataExtension);
            metadataReference.setLocation(location);
        } else {
            String fileName = GsonUtility.optString(metadata,"fileName");
            metadataReference.setMetadataFileName(GsonUtility.optStringValue(metadata,"metadataFileName", fileName));
            metadataReference.setLocation(metadata.get("location").getAsString());
            metadataReference.setCube(StringUtils.isEmpty(fileName));
        }
        adhocReport.setMetadataReference(metadataReference);

        final Security security = SecurityUtils.securityObject();
        adhocReport.setSecurity(security);

        return adhocReport;
    }
    /**
     * Sets properties of the ad-hoc report, including visibility, state, columns, and groups.
     *
     * @param columns 			 columns used in the ad-hoc report.
     * @param groups 			 groups defined in the ad-hoc report.
     * @param state 			 state of the ad-hoc report as a JSON object.
     * @param adhocReport 		 {@code AdhocReport} instance to set properties on.
     * @param canvasElements 	 {@code CanvasElements} instance to set properties on.
     */
    private void setProperties(String columns, @Nullable String groups, @NotNull JsonObject state,
                               @NotNull AdhocReport adhocReport, @NotNull CanvasElements canvasElements) {
        adhocReport.setVisible("true");
        adhocReport.setState(state);

        if (groups != null) {
            canvasElements.setGroups(groups);
        }
       
        canvasElements.setColumns( JsonParser.parseString(columns).getAsJsonArray());
        adhocReport.setCanvasElements(canvasElements);
    }
    /**
     * Determines whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
