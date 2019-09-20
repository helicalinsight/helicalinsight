/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.scheduling;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwfav;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This controller has methods with request mappings /saveReport,
 * /executeFavourite. It also deals with scheduling related tasks.
 *
 * @author Rajasekhar
 * @author Prashansa
 */
@Controller
@Component
public class SaveReportController {

    private final static Logger logger = LoggerFactory.getLogger(SaveReportController.class);
    private static final Map<String, String> STRING_MAP = ControllerUtils.getPropertyMap();

    private static Date stringToDate(String dateString, String timezone, String time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return formatter.parse(dateString + " " + time);
        } catch (ParseException e) {
            logger.error("Parse exception", e);
        }
        return null;
    }

    public static Date findDateAtTimeZone(String timeZone, Date requestDate) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Calendar calTZ = new GregorianCalendar(tz);
        calTZ.setTimeInMillis(requestDate.getTime());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, calTZ.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, calTZ.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, calTZ.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, calTZ.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, calTZ.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, calTZ.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, calTZ.get(Calendar.MILLISECOND));
        return cal.getTime();
    }

    /**
     * This method is mapped with downloadEnableSavedReport request with Http
     * get method, this method gets the file name from request parameter and
     * search file in solution folder and download the file
     *
     * @param dir      directory name
     * @param file     file name
     * @param response HttpServletResponse
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
            JSONObject resultObject = processor.getJSONObject(actualFile, false);
            String resultFile = resultObject.getString("resultFile");
            String resultDirectory = resultObject.getString("resultDirectory");

            String downloadName = resultObject.getString("resultName");

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
     * The method deals with saving a report, marking a report as favourite. An
     * efwsr file represents a saved report and efwFav file represents a
     * favourite file.
     * <p/>
     * These files are created in the solution directory of the EFW-Project. As
     * the method deals with saving of reports, reportName parameter is
     * mandatory for the add operation, which saves a report.
     * <p/>
     * The method supports marking or un-marking an efwsr file as favourite by
     * creating a meta data file.
     *
     * @param reportDirectory The directory of the report
     * @param reportFile      The file under concern
     * @param request         The http request object
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

            if ("add".equalsIgnoreCase(operation)) {
                String result = saveReport(reportFile, reportDirectory, location, reportName, request);
                if ("success".equals(result)) {
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
            String result = ResponseUtils.createJsonResponse(message);
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }

    /**
     * Saves a particular report in the dashboard as a saved report by writing
     * an efwsr file with relevant information about the EFW file from which the
     * report was developed.
     * <p/>
     * The saved file consists of schedulingReference, indicating the scheduling
     * related information if any.
     * <p/>
     * The visibility of the saved report is set to be true by default.
     * <p/>
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
        String reportParameters = null;
        int schedulingReference = 0;

        if (request.getParameter("ScheduleOptions") != null) {
            schedulingReference = schedule(request);
        }

        if (request.getParameter("reportParameters") != null) {
            reportParameters = request.getParameter("reportParameters");
        }

        String visible = "true";
        if (request.getParameter("visible") != null) {
            visible = request.getParameter("visible");
        }

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
        if (schedulingReference != 0) {
            efwsr.setSchedulingReference(schedulingReference + "");
        }

        efwsr.setSecurity(SecurityUtils.securityObject());
        efwsr.setFavourite("false");

        String extension = JsonUtils.getEFWSRExtension();

        ApplicationProperties properties = ApplicationProperties.getInstance();
        location = properties.getSolutionDirectory() + File.separator + location;

        File fileLocation = new File(location);
        if (!fileLocation.exists()) {
            if (fileLocation.mkdir()) {
                logger.debug("Created the directory location for saving efwsr file.");
            }
        }

        File xmlFile = new File(location + File.separator + reportName + "_" + System.currentTimeMillis() + "." +
                extension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(efwsr, xmlFile);
            }
        } catch (Exception ex) {
            logger.error("Stack trace: ", ex);
            return "failure";
        }

        return "success";
    }

    /**
     * This method is responsible to get schedule related data from request
     * object, retrieving data from request object and convert into
     * <code>JSONObject</code> send JSONObject for scheduling process. The http
     * request object contains command , reportName , reportDirectory,
     * reportFile, location, reportParameters, ScheduleOptions, EmailSettings,
     * isActive. If request object does not contain command then it will add
     * schedule.
     *
     * @param request The http request object
     * @return An integer that is the schedule id of currently created schedule.
     */
    private int schedule(HttpServletRequest request) {
        Map<String, String> hashMap;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        hashMap = propertiesFileReader.read("project.properties");
        String SchedulerPath = hashMap.get("schedulerPath");

        String domain = ApplicationProperties.getInstance().getDomain();
        String scheduleUrl = domain.substring(domain.lastIndexOf("/") + 1);

        String command = request.getParameter("command");
        String reportName = request.getParameter("reportName");
        String reportDirectory = request.getParameter("reportDirectory");
        String reportFile = request.getParameter("reportFile");
        String reportParameters = request.getParameter("reportParameters");
        String ScheduleOptions = request.getParameter("ScheduleOptions");
        String emailSettingsString = request.getParameter("EmailSettings");
        String isActive = request.getParameter("isActive");
        int maxId = 0;

        String emailAddresses = JSONObject.fromObject(emailSettingsString).getString("Recipients");
        if (emailAddresses != null) {
            if (emailAddresses.length() == 0 || "[]".equals(emailAddresses))
                throw new RequiredParameterIsNullException("Please enter the email address.");
        }

        JSONObject scheduleJson = JSONObject.fromObject(ScheduleOptions);
        String startDate = scheduleJson.getString("StartDate");
        String endDate = null;
        String timezone = scheduleJson.optString("timeZone");

        String startTime = scheduleJson.optString("ScheduledTime");
        String endRadio = scheduleJson.optString("endsRadio");
        String endTime = scheduleJson.optString("ScheduledEndTime");
        if (!"never".equalsIgnoreCase(endRadio) && !"After".equalsIgnoreCase(endRadio)) {
            endDate = scheduleJson.getString("EndDate");
        }

        validateTime(startDate, endDate, timezone, startTime, endTime);

        String efwExtension = JsonUtils.getEfwExtension();
        String extension = FileUtils.getExtension(reportFile);

        if (extension != null && !extension.equalsIgnoreCase(efwExtension)) {
            throw new EfwServiceException("The file type is not supported. Only " +
                    efwExtension + " files are supported.");
        }

        JSONObject jsonObject;
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
                JSONObject scheduleXmlAsJson = processor.getJSONObject(SchedulerPath, true);
                maxId = xmlOperation.searchMaxIdInXml(scheduleXmlAsJson);

                jsonObject = prepareJsonFromUserData(ScheduleOptions, emailSettingsString, reportParameters,
                        isActive, reportDirectory, reportFile, reportName);
                jsonObject.accumulate("scheduleType", extension);
                xmlOperationWithParser.addNewJobInExistingXML(jsonObject, SchedulerPath, maxId + 1);
                String servletPath = request.getServletPath();
                StringBuffer servletUrl = request.getRequestURL();
                String baseUrlPath = servletUrl.toString().replace(servletPath, "");
                baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
                scheduleProcessCall.scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), baseUrlPath);
            } else {
                jsonObject = prepareJsonFromUserData(ScheduleOptions, emailSettingsString, reportParameters,
                        isActive, reportDirectory, reportFile, reportName);
                String servletPath = request.getServletPath();
                StringBuffer servletUrl = request.getRequestURL();
                String baseUrlPath = servletUrl.toString().replace(servletPath, "");
                baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
                jsonObject.accumulate("scheduleType", extension);

                xmlOperationWithParser.addNewJobInXML(jsonObject, SchedulerPath);

                scheduleProcessCall.scheduleSpecificJob(SchedulerPath, String.valueOf("1"), baseUrlPath);
            }
        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = request.getParameter("id");
            String servletPath = request.getServletPath();
            StringBuffer servletUrl = request.getRequestURL();
            String baseUrlPath = servletUrl.toString().replace(servletPath, "");
            baseUrlPath = baseUrlPath.trim() + "/" + scheduleUrl;
            logger.debug("baseUrlPath:  " + baseUrlPath);
            scheduleProcessCall.scheduleSpecificJob(SchedulerPath, id, baseUrlPath);
        }

        return maxId + 1;
    }

    /**
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
                JSONObject json = processor.getJSONObject(reportDirectory, false);
                list.add(0, json.getString("reportFile"));
                list.add(1, json.getString("reportDirectory"));
                return list;
            }
        } else {
            logger.error("The reportFile " + reportFile + " has no extension!");
        }
        return null;
    }

    private boolean validateTime(String startDate, String endDate, String timeZone, String startTime, String endTime) {
        Date actualStartDate = stringToDate(startDate, timeZone, startTime);
        Date currentDateInTimeZone = findDateAtTimeZone(timeZone, new Date());
        Date requestedStartDate = findDateAtTimeZone(timeZone, actualStartDate);

        logger.info("Current time in " + timeZone + " IS " + currentDateInTimeZone);
        logger.info("Requested startDate is " + requestedStartDate);

        if (requestedStartDate.getTime() < currentDateInTimeZone.getTime()) {
            throw new FormValidationException("The start date is a past date");
        }

        if (endDate != null) {
            Date actualEndDate = stringToDate(endDate, timeZone, endTime);
            Date requestedEndDate = findDateAtTimeZone(timeZone, actualEndDate);
            logger.info("Requested EndDate is " + requestedEndDate);

            if (requestedEndDate.getTime() < currentDateInTimeZone.getTime()) {
                throw new FormValidationException("The end date is a past date");
            }
            if (requestedEndDate.getTime() < requestedStartDate.getTime()) {
                throw new FormValidationException("The end date is a less than start date");
            }
        }
        return true;
    }

    /**
     * <p>
     * This method is responsible to create JSONObject on the basis of given
     * parameters name and value.
     * </p>
     *
     * @param ScheduleOptions     The scheduling option parameter from request
     * @param emailSettingsString A string
     * @param reportParameters    The report parameters request parameter
     * @param isActive            a boolean
     * @param reportDirectory     The directory of the report
     * @param reportFile          The file under concern
     * @param reportName          The name of the report
     * @return <code>JSONObject</code> which contains schedule related data and
     * security related data.
     */
    private JSONObject prepareJsonFromUserData(String ScheduleOptions, String emailSettingsString,
                                               String reportParameters, String isActive, String reportDirectory,
                                               String reportFile, String reportName) {
        JSONObject jsonObject = new JSONObject();
        if (!(reportParameters == null)) {
            jsonObject.accumulate("reportParameters", reportParameters);
        }

        if (!(ScheduleOptions == null)) {
            jsonObject.accumulate("ScheduleOptions", ScheduleOptions);
        }

        if (!(emailSettingsString == null)) {
            jsonObject.accumulate("EmailSettings", emailSettingsString);
        }

        if (!(isActive == null)) {
            jsonObject.accumulate("isActive", isActive);
        }

        if (!(reportDirectory == null)) {
            jsonObject.accumulate("reportDirectory", reportDirectory);
        }

        if (!(reportFile == null)) {
            jsonObject.accumulate("reportFile", reportFile);
        }

        if (!(reportName == null)) {
            jsonObject.accumulate("JobName", reportName);
        }

        jsonObject.accumulate("security", RulesUtils.getSecurityJsonTemplate());
        logger.debug("JSON Before creating xml tag:" + jsonObject);
        return jsonObject;
    }

    /**
     * <p>
     * This method is responsible to execute a favourite report saved earlier by
     * the user.
     * </p>
     * <p>
     * Sets the request attribute decorator and forwards the request to
     * serviceLoadView.jsp page after processing.
     * </p>
     *
     * @param directoryName The name of the directory which is relative
     * @param fileName      The favourite file to be executed
     * @param request       The http request object
     * @return A ModelAndView object
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
        JSONObject parametersJSON = (JSONObject) JSONSerializer.toJSON(efwsr.getReportParameters());
        List<String> list = executeReport.execute(efwsr.getReportDirectory(), efwsr.getReportFile(), parametersJSON);

        String templateData = list.get(0);
        String dirPath = list.get(1);

        ModelAndView serviceLoadView = new ModelAndView();

        //Added the report parameters and the url parameters to the jsp
        ControllerUtils.addUrlParameters(request, serviceLoadView);
        JSONObject attributeValue = ResponseUtils.reportContentAsJson(efwsr);

        logger.debug("Parameters of the report are " + attributeValue);
        serviceLoadView.addObject("reportParameters", attributeValue);

        serviceLoadView.addObject("dir", dirPath.replace("\\", "\\\\"));
        serviceLoadView.addObject("templateData", templateData);
        serviceLoadView.setViewName("serviceLoadView");
        return serviceLoadView;
    }
}