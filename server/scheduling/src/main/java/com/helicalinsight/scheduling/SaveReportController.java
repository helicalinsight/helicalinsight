package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFWSR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwfav;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.ResourceTypeService;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import com.helicalinsight.scheduling.utils.SchedulerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

/**
 * SaveReportController
 * This controller has methods with request mappings /saveReport,
 * /executeFavourite. It also deals with scheduling related tasks.
 *
 * @author Rajasekhar
 * @author Prashansa Kumari
 * @author Rajesh
 * @version 1.1
 * @since 1.1
 */
@Controller
@Component
public class SaveReportController {

    private final static Logger logger = LoggerFactory.getLogger(SaveReportController.class);
    private static final Map<String, String> STRING_MAP = ControllerUtils.getPropertyMap();
    @Autowired
    private HiResourceService hiResourceService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private SchedulesService schedulesService;

    @Autowired
    private JobParametersService jobParameterService;


    /**
     * downloadEnableSavedResult(@RequestParam("dir") String dir, @RequestParam("filename") String file,
     * HttpServletRequest request, HttpServletResponse response)
     * This method is mapped with downloadEnableSavedReport request with Http
     * get method, this method gets the file name from request parameter and
     * search file in solution folder and download the file
     *
     * @param dir      directory name
     * @param file     file name
     * @param request  for checking header
     * @param response sets content type
     * @throws IOException if an exception occurs while doing operation
     */
    @RequestMapping(value = "/downloadEnableSavedReport", method = RequestMethod.GET)
    public void downloadEnableSavedResult(@RequestParam("dir") String dir, @RequestParam("filename") String file,
                                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String solutionDirectory = applicationProperties.getSolutionDirectory();
            String actualFile = solutionDirectory + File.separator + dir + File.separator + file;
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JsonObject resultObject = processor.getJsonObject(actualFile, false);
            String resultFile = resultObject.get("resultFile").getAsString();
            String resultDirectory = resultObject.get("resultDirectory").getAsString();
            String downloadName = resultObject.get("resultName").getAsString();

            if (resultDirectory.matches("^\\[.*\\]$")) {
                resultDirectory = "";
            }

            String absoluteFileFilePath = solutionDirectory + File.separator + resultDirectory + File.separator +
                    resultFile;

            String directoryPath = solutionDirectory + File.separator + resultDirectory;

            if (resultFile.matches("^\\[.*\\]$")) {
                throw new OperationFailedException("Parameter resultFile cannot be null");
            }


            if (downloadName.matches("^\\[.*\\]$")) {
                throw new OperationFailedException("resultName tag in " + resultFile + " is " +
                        "null");
            }

            File resultDirExist = new File(directoryPath);
            if (!resultDirExist.exists()) {
                throw new OperationFailedException("Result Directory does not exist");
            }

            File resultFileExist = new File(absoluteFileFilePath);
            if (!resultFileExist.exists()) {
                throw new OperationFailedException("Result file does not exist");
            }

            String[] tokens = resultFile.split("\\.(?=[^\\.]+$)");
            if (tokens.length > 1) {
                String attachment = downloadName + "." + tokens[1];
                // Set the content type for the response from the properties file
                response.setContentType(STRING_MAP.get(tokens[1]));
                OutputStream outputStream;
                FileInputStream fileInputStream;
                try {
                    // Set the response headers
                    response.setHeader("Content-Disposition", String.format("attachment; " + "filename=\"%s\"",
                            attachment));

                    // Write to outputStream
                    fileInputStream = new FileInputStream(absoluteFileFilePath);
                    outputStream = response.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    ApplicationUtilities.closeResource(fileInputStream);
                    ApplicationUtilities.closeResource(outputStream);
                } catch (IOException ex) {
                    throw new OperationFailedException(ex);
                }
            } else {
                logger.error(resultFile + " Does not have the extension");
                throw new OperationFailedException(resultFile + " Does not have the extension");
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }


    /**
     * saveReport(@RequestParam("reportDirectory") String reportDirectory,
     *
     * @param reportDirectory The directory of the report
     * @param reportFile      The file under concern
     * @param request         object provides required parameter saving file.
     * @param response        sets content type
     * @RequestParam("reportFile") String reportFile, HttpServletRequest request,
     * HttpServletResponse response)
     * The method deals with saving a report, marking a report as favourite. An
     * efwsr file represents a saved report and efwFav file represents a
     * favourite file.
     * <p>
     * These files are created in the solution directory of the EFW-Project. As
     * the method deals with saving of reports, reportName parameter is
     * mandatory for the add operation, which saves a report.
     * <p>
     * The method supports marking or un-marking an efwsr file as favourite by
     * creating a meta data file.
     */
    @RequestMapping(value = "/saveReport", method = RequestMethod.POST)
    public void saveReport(@RequestParam("reportDirectory") String reportDirectory,
                           @RequestParam("reportFile") String reportFile, HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        String operation = request.getParameter("operation");
        String reportName = request.getParameter("reportName");
        if (operation == null) {
            operation = "add";
        }

        String location = request.getParameter("location");
        boolean isAjax = ControllerUtils.isAjax(request);
        String message = "";
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("reportDirectory", reportDirectory);
            if (location != null) {
                parameters.put("location", location);
            }
            parameters.put("reportFile", reportFile);
            ControllerUtils.checkForNullsAndEmptyParameters(parameters);

            boolean schedule = false;
            if (request.getParameter("ScheduleOptions") != null) {
                schedule = true;
            }
            String pathEfwsr = null;
            if ("add".equalsIgnoreCase(operation)) {
                String result = saveReport(reportFile, reportDirectory, location, reportName, request);
                pathEfwsr = result;
                if (!"failure".equals(result)) {
                    if (schedule) {
                        message = "Successfully scheduled the report";
                    } else {
                        message = "Successfully saved the report!";
                    }
                } else if ("failure".equals(result)) {
                    throw new OperationFailedException("Couldn't save the report");
                }
            } else if ("favourite".equalsIgnoreCase(operation)) {
                FileOperationsUtility fileOperationsUtility = new FileOperationsUtility();
                String result;
                String favouriteLocation = request.getParameter("favouriteLocation");

                if ("unmark".equalsIgnoreCase(request.getParameter("markAsFavourite"))) {
                    // Delete the favourite file and remove mark the file as favourite
                    fileOperationsUtility.deleteFavouriteFile(reportFile, reportDirectory);
                    result = fileOperationsUtility.markFavourite(reportFile, reportDirectory, false, null);
                } else {
                    if (favouriteLocation == null) {
                        throw new RequiredParameterIsNullException("Provide parameter " + "favouriteLocation to mark " +
                                "file as favourite.");
                    }
                    // Create a favourite file and save its name in RDF(mark as favourite)
                    String favFileName = fileOperationsUtility.createFavouriteFile(reportFile, reportDirectory,
                            favouriteLocation);
                    result = fileOperationsUtility.markFavourite(reportFile, reportDirectory, true, favFileName);
                }
                if ("success".equals(result)) {
                    message = "Successfully marked as Favourite!";
                } else if ("alreadyFavourite".equals(result)) {
                    message = "The report is already marked as Favourite!";
                } else if ("unmarked".equals(result)) {
                    message = "The report is unmarked as Favourite!";
                } else if ("wasNotAFavourite".equals(result)) {
                    throw new OperationFailedException("The report was not favourite to unmark!");
                } else {
                    throw new OperationFailedException("Couldn't mark as favourite due to " + "insufficient " +
                            "privileges");
                }
            }
            ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
            String result = ResponseUtils.createJsonResponse(message);
            JsonObject resultJson = new Gson().fromJson(result, JsonObject.class);
            if (!reportFile.endsWith("hwf")) {
                FileInfo efwFileInfo = bean.prepareFileInfo(location, pathEfwsr + "." + JsonUtils.getEFWSRExtension());
                JsonArray dataArray = new JsonArray();
                dataArray.add(new Gson().fromJson(new Gson().toJson(efwFileInfo), JsonObject.class));
                resultJson.getAsJsonObject("response").add("data", dataArray);
            }
            result = resultJson.toString();
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * saveReport(String reportFile, String reportDirectory, String location, String reportName,
     * HttpServletRequest request)
     * Saves a particular report in the dashboard as a saved report by writing
     * an efwsr file with relevant information about the EFW file from which the
     * report was developed.
     * <p>
     * The saved file consists of schedulingReference, indicating the scheduling
     * related information if any.
     * <p>
     * The visibility of the saved report is set to be true by default.
     * <p>
     * Note: reportParameters of the report is an optional parameter.
     *
     * @param reportFile      The file under concern
     * @param reportDirectory The directory of the report
     * @param location        The location where the report has to be saved
     * @param reportName      The name of the report to be saved
     * @param request         The http request object
     * @return Writes a string to the response body to avoid 404 indicating the
     * result of processing
     */
    private String saveReport(String reportFile, String reportDirectory, String location, String reportName,
                              HttpServletRequest request) {
        Boolean scheduleStorageTypeIsDatabase = JsonUtils.isScheduleStorageTypeIsDatabase();
        String reportParameters = null;
        Long schedulingReference = 0l;

        if (request.getParameter("reportParameters") != null) {
            reportParameters = request.getParameter("reportParameters");
        }

        String visible = "true";
        if (request.getParameter("visible") != null) {
            visible = request.getParameter("visible");
        }
        HIResource hiResource = null;
        Efwsr efwsr = prepareEFWSR(reportFile, reportDirectory, location, reportName, reportParameters, visible);
        if (request.getParameter("ScheduleOptions") != null) {
            //schedulingReference = schedule(request);
            //calling from db
            if (scheduleStorageTypeIsDatabase) {
                try {
                    ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
                    hiResource = scheduleOperation.prepareHiResource(efwsr);
                    schedulingReference = scheduleInDb(request, hiResource);
                } catch (Exception e) {
                    logger.error("Error occurred while deleting resource {}", e);
                    // if (hiResource != null)
                    //  hiResourceService.deleteHiResource(hiResource.getResourceId());
                    throw e;
                }
            } else {
                schedulingReference = Long.valueOf(schedule(request));
            }
        }
        logger.debug("scheduling reference :" + schedulingReference);

        if (schedulingReference != 0) {
            efwsr.setSchedulingReference(schedulingReference + "");
        }

        String extension = JsonUtils.getEFWSRExtension();

        ApplicationProperties properties = ApplicationProperties.getInstance();
        location = properties.getSolutionDirectory() + File.separator + location;


        String path = checkAndReplaceSpecialChars(reportName) + "_" + System.currentTimeMillis();

        try {
            location = location.replace(ApplicationProperties.getInstance().getSolutionDirectory(), "");
            if (location.startsWith("\\") || location.startsWith("/")) {
                location = location.substring(1);
            }
            HIResourceServiceDB dbService = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
            HIResource resourceByUrl = dbService.getResourceByUrl(location);
            if (resourceByUrl != null) {
                String efwsrRepName = efwsr.getReportName();
                String url = location + "/" + path + "." + extension;
                HIResource efwsrReport = ResourceUtils.newHIResource(location, false, efwsrRepName, path, url, extension);
                efwsrReport.setParentId(resourceByUrl.getResourceId());
                //dbService.addHIResource(efwsrReport);
                HIResourceEFWSR hiResourceEFWSR = new HIResourceEFWSR();
                hiResourceEFWSR.setCreatedBy(efwsrReport.getCreatedBy());
                hiResourceEFWSR.setReportFile(efwsr.getReportFile());
                hiResourceEFWSR.setReportName(efwsrRepName);
                hiResourceEFWSR.setReportDirectory(efwsr.getReportDirectory());
                hiResourceEFWSR.setReportParameters(efwsr.getReportParameters());
                hiResourceEFWSR.setCreatedDate(efwsrReport.getCreated_date());
                hiResourceEFWSR.setLastUpdatedTime(efwsrReport.getLastUpdatedTime());
                hiResourceEFWSR.setFavorite(false);
                efwsrReport.setHiResourceEFWSR(hiResourceEFWSR);
                dbService.addHIResource(efwsrReport);
                path = efwsrReport.getResourcePath();

            }
        } catch (Exception ex) {
            //if (hiResource != null)
            //  hiResourceService.deleteHiResource(hiResource.getResourceId());
            logger.error("Stack trace: {}", ex);
            return "failure";
        }

        return path;
    }

    /**
     * prepareEFWSR(String reportFile, String reportDirectory, String location, String reportName, String reportParameters, String visible)
     * create report in the dashboard as to save
     * an efwsr file with relevant information about the EFW file from which the
     * report was developed.
     *
     * @param reportFile       efwsr file name
     * @param reportDirectory  efwsr file directory
     * @param location         file location
     * @param reportName       to set efwsr report name
     * @param reportParameters parameters
     * @param visible          visibility of report  by default true.
     * @return efwsr object with required information to create efwsr file.
     */
    private Efwsr prepareEFWSR(String reportFile, String reportDirectory, String location, String reportName, String reportParameters, String visible) {
        // Accumulate the parameters
        Efwsr efwsr = ApplicationContextAccessor.getBean(Efwsr.class);
        efwsr.setReportName(reportName);

        /*
         * If the report file extension is not EFW, then read the reportFile
         */
        List<String> list = getActualFile(reportFile, location);
        if (list == null) {
            efwsr.setReportFile(reportFile);
            efwsr.setReportDirectory(reportDirectory);
        } else {
            efwsr.setReportFile(list.get(0));
            efwsr.setReportDirectory(list.get(1));
        }

        if (!(reportParameters == null)) {
            efwsr.setReportParameters(reportParameters);
        }

        efwsr.setVisible(visible);


        efwsr.setSecurity(SecurityUtils.securityObject());
        efwsr.setFavourite("false");

        //prepare HiResource

        return efwsr;
    }

    /**
     * prepareCommonJson(HttpServletRequest request, ScheduleOperation scheduleOperation)
     * this method fetch the data from request and scheduleOperation object  , and prepares jsonObject with all data
     * for scheduling data.
     *
     * @param request           provides required data of report and email related
     * @param scheduleOperation provides execution details like start time , end time.
     * @return json object contains information to schedule task.
     */
    private JsonObject prepareCommonJson(HttpServletRequest request, ScheduleOperation scheduleOperation) {
        JsonObject jsonObject = new JsonObject();
        String domain = ApplicationProperties.getInstance().getDomain();
        String adhocReportUrl = ApplicationProperties.getInstance().getAdhocReportUrl();
        String scheduleUrl = domain.substring(domain.lastIndexOf("/") + 1);

        String command = request.getParameter("command");
        String reportName = request.getParameter("reportName");
        String reportDirectory = request.getParameter("reportDirectory");
        String reportFile = request.getParameter("reportFile");
        String reportParameters = request.getParameter("reportParameters");
        String ScheduleOptions = request.getParameter("ScheduleOptions");
        String emailSettingsString = request.getParameter("EmailSettings");
        String isActive = request.getParameter("isActive");
        String adhocFormData = request.getParameter("adhocFormData");
        jsonObject.addProperty("command", command);
        jsonObject.addProperty("reportName", reportName);
        jsonObject.addProperty("reportDirectory", reportDirectory);
        jsonObject.addProperty("reportFile", reportFile);
        jsonObject.addProperty("reportParameters", reportParameters);
        jsonObject.addProperty("ScheduleOptions", ScheduleOptions);
        jsonObject.addProperty("emailSettingsString", emailSettingsString);
        jsonObject.addProperty("isActive", isActive);
        if (adhocFormData != null)
            jsonObject.addProperty("adhocFormData", adhocFormData);

        if (StringUtils.isNotEmpty(emailSettingsString)) {
            String emailAddresses = new Gson().fromJson(emailSettingsString, JsonObject.class).get("Recipients").toString();
            if (emailAddresses != null) {
                if (emailAddresses.length() == 0 || "[]".equals(emailAddresses))
                    throw new RequiredParameterIsNullException("Please enter the email address.");
            }
        }

        JsonObject scheduleJson = new Gson().fromJson(ScheduleOptions, JsonObject.class);
        String startDate = scheduleJson.get("StartDate").getAsString();
        String endDate = null;
        String timezone = GsonUtility.optString(scheduleJson, "timeZone");

        String startTime = GsonUtility.optString(scheduleJson, "ScheduledTime");
        String endRadio = GsonUtility.optString(scheduleJson, "endsRadio");
        String endTime = GsonUtility.optString(scheduleJson, "ScheduledEndTime");
        if (!"never".equalsIgnoreCase(endRadio) && !"After".equalsIgnoreCase(endRadio)) {
            endDate = scheduleJson.get("EndDate").getAsString();
        }


        scheduleOperation.validateTime(startDate, endDate, timezone, startTime, endTime);

        String efwExtension = JsonUtils.getEfwExtension();
        String reportExtension = JsonUtils.getReportExtension();
        String hcrExtension = JsonUtils.getHCRExtension();
        String extension = FileUtils.getExtension(reportFile);
        String hReportExtension = JsonUtils.getHrReportExtension();
        if (extension != null && extension.equalsIgnoreCase(reportExtension)) {
            scheduleUrl = adhocReportUrl;

        }

        jsonObject.addProperty("scheduleUrl", scheduleUrl);
        jsonObject.addProperty("extension", extension);
        return jsonObject;

    }

    /**
     * schedule(HttpServletRequest request)
     * This method is responsible to get schedule related data from request
     * object, retrieving data from request object and convert into
     * <code>JSONObject</code> send JSONObject for scheduling process. The http
     * request object contains command , reportName , reportDirectory,
     * reportFile, location, reportParameters, ScheduleOptions, EmailSettings,
     * isActive. If request object does not contain command then it will add
     * schedule.
     *
     * @param request request object provides schedule related data
     * @return An integer that is the schedule id of currently created schedule.
     */
    private int schedule(HttpServletRequest request) {
        Map<String, String> hashMap;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        hashMap = propertiesFileReader.read("project.properties");
        String SchedulerPath = hashMap.get("schedulerPath");
        int maxId = 0;
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        JsonObject requestJson = prepareCommonJson(request, scheduleOperation);
        String command = requestJson.get("command").getAsString();
        String ScheduleOptions = requestJson.get("ScheduleOptions").getAsString();
        String emailSettingsString = GsonUtility.optString(requestJson, "emailSettingsString");
        String reportParameters = requestJson.get("reportParameters").getAsString();
        String isActive = requestJson.get("isActive").getAsString();
        String reportFile = requestJson.get("reportFile").getAsString();
        String reportDirectory = requestJson.get("reportDirectory").getAsString();
        String reportName = requestJson.get("reportName").getAsString();
        String extension = requestJson.get("extension").getAsString();
        String scheduleUrl = requestJson.get("scheduleUrl").getAsString();
        //getting adhocFormData from request
        String adhocFormData = GsonUtility.optString(requestJson, "adhocFormData");

        JsonObject jsonObject;
        XmlOperation xmlOperation = new XmlOperation();
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
        ScheduleProcessCall scheduleProcessCall = new ScheduleProcessCall();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (command.equalsIgnoreCase("add") || command.equals("")) {
            File file = new File(SchedulerPath);

            if (file.exists() && file.length() == 0) {
                if (file.delete()) {
                    logger.info("Found 0kb scheduling.xml file and deleted the same");
                }
            }

            if (file.exists()) {
                JsonObject scheduleXmlAsJson = processor.getJsonObject(SchedulerPath, true);
                maxId = xmlOperation.searchMaxIdInXml(JsonParser.parseString(scheduleXmlAsJson.toString()).getAsJsonObject());

                jsonObject = SchedulerUtils.prepareJsonFromUserData(ScheduleOptions, emailSettingsString, reportParameters,
                        isActive, reportDirectory, reportFile, reportName, adhocFormData);
                jsonObject.addProperty("scheduleType", extension);//accumulate
                xmlOperationWithParser.addNewJobInExistingXML(JsonParser.parseString(jsonObject.toString()).getAsJsonObject(), SchedulerPath, maxId + 1);
                String servletPath = request.getServletPath();
                StringBuffer servletUrl = request.getRequestURL();
                String baseUrlPath = getServletOrBaseUrl(servletPath, servletUrl);
                baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
                scheduleProcessCall.scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), baseUrlPath);
            } else {
                jsonObject = SchedulerUtils.prepareJsonFromUserData(ScheduleOptions, emailSettingsString, reportParameters,
                        isActive, reportDirectory, reportFile, reportName, adhocFormData);
                String servletPath = request.getServletPath();
                StringBuffer servletUrl = request.getRequestURL();
                String baseUrlPath = getServletOrBaseUrl(servletPath, servletUrl);
                baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
                jsonObject.addProperty("scheduleType", extension);//accumulate

                xmlOperationWithParser.addNewJobInXML(JsonParser.parseString(jsonObject.toString()).getAsJsonObject(), SchedulerPath);

                scheduleProcessCall.scheduleSpecificJob(SchedulerPath, String.valueOf("1"), baseUrlPath);
            }
        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = request.getParameter("id");
            String servletPath = request.getServletPath();
            StringBuffer servletUrl = request.getRequestURL();
            String baseUrlPath = getServletOrBaseUrl(servletPath, servletUrl);
            baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
            logger.debug("baseUrlPath:  " + baseUrlPath);
            scheduleProcessCall.scheduleSpecificJob(SchedulerPath, id, baseUrlPath);
        }

        return maxId + 1;
    }

    private Long scheduleInDb(HttpServletRequest request, HIResource hiResource) {
        Long maxId = 0l;
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        JsonObject requestJson = prepareCommonJson(request, scheduleOperation);
        String command = requestJson.get("command").getAsString();
        String ScheduleOptions = requestJson.get("ScheduleOptions").getAsString();
        String emailSettingsString = GsonUtility.optString(requestJson, "emailSettingsString");
        String reportParameters = requestJson.get("reportParameters").getAsString();
        String isActive = requestJson.get("isActive").getAsString();
        String reportFile = requestJson.get("reportFile").getAsString();
        String reportDirectory = requestJson.get("reportDirectory").getAsString();
        String reportName = requestJson.get("reportName").getAsString();
        String extension = requestJson.get("extension").getAsString();
        String scheduleUrl = requestJson.get("scheduleUrl").getAsString();
        //getting adhocFormData from request
        String adhocFormData = GsonUtility.optString(requestJson, "adhocFormData");

        EnhancedScheduleProcessCall scheduleProcessCall = new EnhancedScheduleProcessCall();
        if (command.equalsIgnoreCase("add") || command.equals("")) {
            JsonObject jsonObject;
            jsonObject = SchedulerUtils.prepareJsonFromUserData(ScheduleOptions, emailSettingsString, reportParameters,
                    isActive, reportDirectory, reportFile, reportName, adhocFormData);
            jsonObject.addProperty("scheduleType", extension);//accumulate
            String servletPath = request.getServletPath();
            StringBuffer servletUrl = request.getRequestURL();
            String baseUrlPath = getServletOrBaseUrl(servletPath, servletUrl);
            baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
            // scheduleProcessCall.scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), baseUrlPath);
            //calling db based scheduling
            //Create schedules entity

            Schedules preparedSchedule = scheduleOperation.prepareSchedulesEntity(jsonObject, hiResource);
            Schedules schedule = schedulesService.addSchedule(preparedSchedule);
            maxId = schedule.getScheduleId();
            JsonObject reportParametersJson = jsonObject.getAsJsonObject("reportParameters");
            logger.debug("reportParameters :", reportParametersJson);
            if(reportFile.endsWith("hwf")){
                reportParametersJson.addProperty("hwfFile",reportFile);
                reportParametersJson.addProperty("hwfDir",reportDirectory);
            }
            scheduleOperation.saveJobParameters(schedule, JsonParser.parseString(reportParametersJson.toString()).getAsJsonObject(), false);
            scheduleProcessCall.scheduleSpecificJob(schedule, baseUrlPath);

        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = request.getParameter("id");
            maxId = Long.valueOf(id);
            String servletPath = request.getServletPath();
            StringBuffer servletUrl = request.getRequestURL();
            String baseUrlPath = getServletOrBaseUrl(servletPath, servletUrl);
            baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
            logger.debug("baseUrlPath:  " + baseUrlPath);
            // scheduleProcessCall.scheduleSpecificJob(SchedulerPath, id, baseUrlPath);
            //calling db based scheduling
            Schedules schedule = schedulesService.getSchedule(Long.parseLong(id));
            if (schedule == null) {
                throw new SchedulingException("There are no schedule present with with given id:" + maxId);
            }
            scheduleProcessCall.scheduleSpecificJob(schedule, baseUrlPath);
        }

        return maxId;
    }


    /**
     * getServletOrBaseUrl(String servletPath, StringBuffer servletUrl)
     * Returns base url for scheduler using servletUrl.
     *
     * @param servletPath String path
     * @param servletUrl  url in string buffer
     * @return url in String format.
     */
    private String getServletOrBaseUrl(String servletPath, StringBuffer servletUrl) {
        String baseUrl = ApplicationProperties.getInstance().getDomain();
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
        return baseUrl;
        //return servletUrl.toString().replace(servletPath, "");
    }


    /**
     * getActualFile(String reportFile, String reportDirectory)
     * This method is responsible to get efw file name and directory name.
     *
     * @param reportFile      A <code>String</code> which specifies efwsr file name
     * @param reportDirectory A <code>String</code> which specifies efwsr file directory
     * @return A <code>List</code> with efw file name at index 0 and efw
     * directory name at index 1
     */
    private List<String> getActualFile(String reportFile, String reportDirectory) {
        String[] file = reportFile.split("\\.(?=[^\\.]+$)");
        if (file.length > 1) {
            String fileExtension = file[1];
            String efwsr = JsonUtils.getEFWSRExtension();
            if (fileExtension.equalsIgnoreCase(efwsr)) {
                IProcessor processor = ResourceProcessorFactory.getIProcessor();
                List<String> list = new ArrayList<>();
                ApplicationProperties properties = ApplicationProperties.getInstance();
                reportDirectory = properties.getSolutionDirectory() + File.separator +
                        reportDirectory + File.separator + reportFile;
                JsonObject json = processor.getJsonObject(reportDirectory, false);
                list.add(0, json.get("reportFile").getAsString());
                list.add(1, json.get("reportDirectory").getAsString());
                return list;
            }
        } else {
            logger.error("The reportFile " + reportFile + " has no extension!");
        }
        return null;
    }


    /**
     * executeFavourite(@RequestParam("dir") String directoryName,
     *
     * @param directoryName The name of the directory which is relative
     * @param fileName      The favourite file to be executed
     * @param request       The http request object
     * @return A ModelAndView object
     * @RequestParam("file") String fileName, HttpServletRequest request)
     * <p>
     * This method is responsible to execute a favourite report saved earlier by
     * the user.
     * </p>
     * <p>
     * Sets the request attribute decorator and forwards the request to
     * serviceLoadView.jsp page after processing.
     * </p>
     */
    @RequestMapping(value = "/executeFavourite", method = RequestMethod.POST)
    public ModelAndView executeFavourite(@RequestParam("dir") String directoryName,
                                         @RequestParam("file") String fileName, HttpServletRequest request) {
        request.setAttribute("decorator", "empty");

        ApplicationProperties properties = ApplicationProperties.getInstance();
        File resource = new File(properties.getSolutionDirectory() + File.separator + directoryName + File.separator
                + fileName);
        logger.debug("Looking for the resource " + resource);
        Efwfav efwfav = JaxbUtils.unMarshal(Efwfav.class, resource);
        String file = efwfav.getSavedReportFileName();
        logger.debug("The file obtained from " + directoryName + " and " + fileName + " is file " + file);
        logger.debug("Searching for the file " + file);

        FileOperationsUtility fileOperationsUtility = new FileOperationsUtility();
        String filePath = fileOperationsUtility.search(properties.getSolutionDirectory(), file);
        logger.info("The path for the file is " + filePath);

        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, new File(filePath));

        ExecuteReport executeReport = new ExecuteReport();
        JsonObject parametersJSON = new Gson().fromJson(efwsr.getReportParameters(), JsonObject.class);
        List<String> list = executeReport.execute(efwsr.getReportDirectory(), efwsr.getReportFile(), parametersJSON);

        String templateData = list.get(0);
        String dirPath = list.get(1);

        ModelAndView serviceLoadView = new ModelAndView();

        //Added the report parameters and the url parameters to the jsp
        ControllerUtils.addUrlParameters(request, serviceLoadView);
        JsonObject attributeValue = ResponseUtils.newReportContentAsJson(efwsr);

        logger.debug("Parameters of the report are " + attributeValue);
        serviceLoadView.addObject("reportParameters", attributeValue);

        serviceLoadView.addObject("dir", dirPath.replace("\\", "\\\\"));
        serviceLoadView.addObject("templateData", templateData);
        serviceLoadView.setViewName("serviceLoadView");
        return serviceLoadView;
    }
}