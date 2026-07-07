package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ResponseUtils;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import com.helicalinsight.scheduling.utils.SchedulerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;



/**
 * ScheduleController class implements {@link ApplicationContextAware}
 *  This controller has methods with request mappings /getScheduleData,
 * /updateScheduleData. It also deals with providing scheduling data and updating schedule tasks.
 */
@Controller
@Component
@DependsOn("applicationContextAccessor")
public class ScheduleController implements ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    @Autowired
    private SchedulesService schedulesService;
    @Autowired
    private JobParametersService jobParametersService;

    /**
     * The <code>UserService</code> object
     */
    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    /**
     * getScheduleData(HttpServletRequest request, HttpServletResponse response)
     * Get ID from request and send JSONObject as response of that id
     * @param request         provides id 
     * @param response        sets the content type
     * @return response or status if id found in schedule.xml, otherwise response {@code null}.
     * @throws IOException if any exception occurs
     */
    @RequestMapping(value = "/getScheduleData", method = RequestMethod.POST)
    @ResponseBody
    public String getScheduleData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String theId = request.getParameter("id");
        Map<String, String> map;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        map = propertiesFileReader.read("project.properties");
        String SchedulerPath = map.get("schedulerPath");
        File scheduleFile = new File(SchedulerPath);
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            if (!scheduleFile.exists()) {
                throw new ResourceNotFoundException("The scheduling.xml is not found");
            }

            boolean idExist;
            JsonObject jsonObject;

            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            jsonObject = processor.getJsonObject(SchedulerPath, true);
            XmlOperation xmlOperation = new XmlOperation();
            idExist = xmlOperation.searchId(jsonObject, theId);

            String responseData;
            if (idExist) {
                jsonObject = xmlOperation.getParticularObject(SchedulerPath, theId);
                jsonObject.remove("security");
                responseData = jsonObject.toString();

                logger.debug("JsonObject sending to front-end for update " + jsonObject);

                JsonObject responseMessage = new JsonObject();
                responseMessage.addProperty("status", "1");
                responseMessage.addProperty("response", responseData);
                return responseMessage.toString();
            } else {
                String result = ResponseUtils.createJsonResponse("Id not found in schedule.xml");
                ControllerUtils.handleSuccess(response, isAjax, result);
            }
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        return null;
    }

    /**
     * updateScheduleData(HttpServletRequest request, HttpServletResponse response)
     * get update schedule related information from front end in JSON format
     * update scheduling.xml and update trigger.
     * @param request				object containing schedule related data
     * @param response 				send the updated response to front-end 
     * @return response in string format indicating the success or failure of the update process.
     */
    @RequestMapping(value = "/updateScheduleData", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateScheduleData(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (JsonUtils.newIsScheduleStorageTypeIsDatabase())
            return updateScheduleInDb(request, response);
        else
            return updateSchedule(request, response);
    }

    /**
     * deleteSchedule(String id, HttpServletResponse response)
     * @param id				schedule id
     * @param response          sends response message
     * @return message indicating the successful deletion of the schedule.
     */
    private String deleteSchedule(String id, HttpServletResponse response) {
        DereferenceScheduling.deleteSchedule(id);
        return "schedule deleted successfully..";
    }
    /**
     * deleteScheduleInDb(String id, HttpServletResponse response)
     * it deletes schedule from database.
     * @param id				schedule id
     * @param response          sends response message
     * @return message indicating the successful deletion of the schedule.
     */
    private String deleteScheduleInDb(String id, HttpServletResponse response) {
        schedulesService.deleteSchedule(Long.valueOf(id));
        return "schedule deleted successfully..;";
    }
    /**
     * updateSchedule(HttpServletRequest request, HttpServletResponse response)
     * This method is responsible for update schedule.
     * @param request				object containing schedule related data
     * @param response				send the updated response to front-end 
     * @return always returns {@code null}
     * @throws IOException If an I/O error occurs while processing the request or response.
     */
    private String updateSchedule(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
            JsonObject existingScheduleOption;
            ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
            String existingCronExpression;
            String updatedCronExpression;
            ScheduleProcess scheduleProcess = new ScheduleProcess();

            boolean isValidUSer;
            String path;
            ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
            JsonObject jsonData = prepareScheduleJson(request, scheduleOperation);
            String id;
            JsonObject jsonObject;
            id = jsonData.get("id").getAsString();
            logger.debug("Final jsonData:  " + jsonData);
            Map<String, String> hashMap;
            PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
            hashMap = propertiesFileReader.read("project.properties");
            path = hashMap.get("schedulerPath");
            String message = "";
            isValidUSer = isValidUser(id, path);
            logger.debug("Is the current user a valid user? " + isValidUSer);
            if (isValidUSer) {
                XmlOperation xmlOperation = new XmlOperation();
                IProcessor processor = ResourceProcessorFactory.getIProcessor();
                jsonObject = processor.getJsonObject(path, true);
                boolean idExist = xmlOperation.searchId(jsonObject, id);
                logger.debug("Id Exists? " + idExist);
                if (idExist) {
                    jsonObject = xmlOperation.getParticularObject(path, id);
                    existingScheduleOption = jsonObject.getAsJsonObject("ScheduleOptions");
                    existingCronExpression = convertIntoCronExpression.convertDateIntoCronExpression
                            (existingScheduleOption);
                    String updatedStartDate = existingScheduleOption.get("StartDate").getAsString();
                    DateFormat formatter;
                    Date sDate = null;
                    Date eDate = null;
                    //String timezone = jsonData.getAsJsonObject("ScheduleOptions").optString("timeZone");
                    JsonObject ScheduleOptionsObjct = jsonData.getAsJsonObject("ScheduleOptions");
                    String timezone = GsonUtility.optString(ScheduleOptionsObjct,"timeZone");
                    try {
                        formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (timezone != null || timezone.length() > 0) {
                            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        sDate = formatter.parse(updatedStartDate);
                    } catch (ParseException e) {
                        logger.error("Parsing exception ", e);
                    }
                    String endDate;

                    if (jsonData.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("on")) {
                        endDate = jsonData.getAsJsonObject("ScheduleOptions").get("EndDate").getAsString();
                        try {
                            formatter = new SimpleDateFormat("yyyy-MM-dd");
                            if (timezone != null || timezone.length() > 0) {
                                formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                            }
                            eDate = formatter.parse(endDate);
                        } catch (ParseException e) {
                            logger.error("Parsing exception ", e);
                        }
                    }
                    xmlOperationWithParser.removeElementFromXml(path, id);
                    xmlOperationWithParser.updateJobInExistingXML(jsonData, path, Integer.parseInt(id));
                    message = "Data updated successfully";
                }
            } else {
                message = "Not a valid user";
            }
            String result = ResponseUtils.createJsonResponse(message);
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        return null;
    }
    /**
     * prepareScheduleJson(HttpServletRequest request, ScheduleOperation scheduleOperation)
     * It fetches schedule parameters from request and returns schedule id and file extension.
     * @param request                      provides schedule data
     * @param scheduleOperation			   instance of ScheduleOperation
     * @return jsonObject which contains schedule related data and
     * security related data.
     */
    private JsonObject prepareScheduleJson(HttpServletRequest request, ScheduleOperation scheduleOperation) {
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            throw new SchedulingException("invalid form data @id cannot be empty or null");
        }
        String reportName = request.getParameter("reportName");
        String reportDirectory = request.getParameter("reportDirectory");
        String reportFile = request.getParameter("reportFile");
        String reportParameters = request.getParameter("reportParameters");
        String updatedScheduleOptionString = request.getParameter("ScheduleOptions");
        String adhocFormData = request.getParameter("adhocFormData");
        if (StringUtils.isNotBlank(updatedScheduleOptionString)) {
            throw new EfwServiceException("The ScheduleOptions cannot be updated only email and report parameter can be  updated.");
        }
        String emailSettingsString = request.getParameter("EmailSettings");
        String isActive = validateEmailAndGetEmail(request, emailSettingsString);


        String efwExtension = JsonUtils.getEfwExtension();
        String reportExtension = JsonUtils.getReportExtension();
        String hcrExtension = JsonUtils.getHCRExtension();
        String extension = FileUtils.getExtension(reportFile);
        /*if (extension != null && !(extension.equalsIgnoreCase(efwExtension) || extension.equalsIgnoreCase
                (reportExtension) || extension.equalsIgnoreCase(hcrExtension))) {
            throw new EfwServiceException("The file type is not supported. Only " +
                    efwExtension + " files are supported.");
        }*/
        JsonObject jsonData = JsonParser.parseString(SchedulerUtils.prepareJsonFromUserData(null, emailSettingsString, reportParameters,
                isActive, reportDirectory, reportFile, reportName, adhocFormData).toString()).getAsJsonObject();
        if (extension != null) {
            jsonData.addProperty("scheduleType", extension);
        }
        jsonData.addProperty("id", id);
        return jsonData;
    }
    /**
     * validateEmailAndGetEmail(HttpServletRequest request, String emailSettingsString)
     * it simply validates coming email data .
     * @param request						request tells whether schedule is active or not
     * @param emailSettingsString			email information
     * @return schedule is active or not in string format.
     */
    private String validateEmailAndGetEmail(HttpServletRequest request, String emailSettingsString) {
        String isActive = request.getParameter("isActive");


        if (StringUtils.isNotEmpty(emailSettingsString)) {
            //String emailAddresses = JSONObject.fromObject(emailSettingsString).getString("Recipients");
        	String emailAddresses = new Gson().fromJson(emailSettingsString,JsonObject.class).get("Recipients").getAsString();
            if (emailAddresses != null) {
                if (emailAddresses.length() == 0 || "[]".equals(emailAddresses))
                    throw new RequiredParameterIsNullException("Please enter the email address.");
            }
        }
        return isActive;
    }
    /**
     * updateScheduleInDb(HttpServletRequest request, HttpServletResponse response)
     * Updates the schedule data in the database.
     * @param request			 request provides schedule data
     * @param response           response message whether data is updated successfully or not.
     * @return always {@code null}
     * @throws IOException If an I/O error occurs while processing the request or response.
     */
    private String updateScheduleInDb(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
            String existingCronExpression;
            String updatedCronExpression;
            ScheduleProcess scheduleProcess = new ScheduleProcess();
            JsonObject updatedScheduleOption;
            ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
            JsonObject jsonData = prepareScheduleJson(request, scheduleOperation);
            String id;
            id = jsonData.get("id").getAsString();
            boolean isValidUSer;

            logger.debug("Final jsonData:  " + jsonData);

            String message = "";
            Schedules schedule = schedulesService.getSchedule(Long.parseLong(id));
            if (schedule != null) {
                isValidUSer = isValidUser(schedule);
                logger.debug("Is the current user a valid user? " + isValidUSer);
                if (isValidUSer) {
                    JsonObject scheduleEntityJson = scheduleOperation.prepareSchedulesEntityToJSON(schedule);
                    JsonObject existingScheduleOption = scheduleEntityJson.getAsJsonObject("ScheduleOptions");
                    String updatedStartDate = existingScheduleOption.get("StartDate").getAsString();
                    DateFormat formatter;
                    Date sDate = null;
                    Date eDate = null;
                    String timezone = GsonUtility.optString(existingScheduleOption,"timeZone");
                    try {
                        formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (timezone != null || timezone.length() > 0) {
                            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                        }
                        sDate = formatter.parse(updatedStartDate);
                    } catch (ParseException e) {
                        logger.error("Parsing exception ", e);
                    }
                    String endDate;
                    if (existingScheduleOption.get("endsRadio").getAsString().equalsIgnoreCase("on")) {
                        endDate = existingScheduleOption.get("EndDate").getAsString();
                        try {
                            formatter = new SimpleDateFormat("yyyy-MM-dd");
                            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                            eDate = formatter.parse(endDate);
                        } catch (ParseException e) {
                            logger.error("Parsing exception ", e);
                        }
                    }
                    Schedules updateSchedule = scheduleOperation.prepareSchedulesEntity(jsonData, schedule.getHIResource());
                    schedulesService.editSchedule(updateSchedule);
                    Schedules updatedScheduleEntity = schedulesService.getSchedule(updateSchedule.getScheduleId());
                    jobParametersService.deleteAllJobParametersRelatedToScheduleId(updatedScheduleEntity.getScheduleId());
                    JsonObject reportParametersJson = jsonData.getAsJsonObject("reportParameters");
                    scheduleOperation.saveJobParameters(updatedScheduleEntity, reportParametersJson, false);
                    message = "Data updated successfully";
                } else {
                    message = "Not a valid user";
                }
            } else {
                throw new SchedulingException("Invalid id there is no schedule present with given id: " + id);
            }
            String result = ResponseUtils.createJsonResponse(message);
            ControllerUtils.handleSuccess(response, isAjax, result);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
        return null;
    }
    

	/**
	 * isValidUser(String id, String SchedulerPath)
     * this method is responsible to validate user
     *
     * @param id            a <code>String</code> which specify schedule id
     * @param SchedulerPath a <code>String</code> which specify scheduling.xml path
     * @return {@code true} if valid user else return {@code false}
     */
    public boolean isValidUser(String id, String SchedulerPath) {
        XmlOperation xmlOperation = new XmlOperation();
        JsonObject jsonObject = xmlOperation.getParticularObject(SchedulerPath, id);
        final JsonObject security = jsonObject.getAsJsonObject("security");
        return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), security);
    }
    /**
     * isValidUser(Schedules schedules)
     * it validates the schedule creted user and logged in user are same or not.
     * @param schedules         to get created user information
     * @return {@code true if logged in user and schedule created user are same}, otherwise {@code false}
     */
    public boolean isValidUser(Schedules schedules) {
        User createdBy = schedules.getCreatedBy();
        return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), createdBy);
    }

    /**
     * setApplicationContext(ApplicationContext applicationContext)
     * Sets the application context or provides access to application context
     *
     * @param applicationContext The applicationContext of the app
     * @throws org.springframework.beans.BeansException If some thing goes wrong ):-
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
