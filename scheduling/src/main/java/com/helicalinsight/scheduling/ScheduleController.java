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

import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ResponseUtils;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Controller
@Component
public class ScheduleController implements ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    /**
     * The <code>UserService</code> object
     */
    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    /**
     * <p>
     * Get ID from request and send JSONObject as response of that id
     * </p>
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
            JSONObject jsonObject;

            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            jsonObject = processor.getJSONObject(SchedulerPath, true);
            XmlOperation xmlOperation = new XmlOperation();
            idExist = xmlOperation.searchId(jsonObject, theId);

            String responseData;
            if (idExist) {
                jsonObject = xmlOperation.getParticularObject(SchedulerPath, theId);
                jsonObject.discard("security");
                responseData = jsonObject.toString();

                logger.debug("JsonObject sending to front-end for update " + jsonObject);

                JSONObject responseMessage = new JSONObject();
                responseMessage.put("status", "1");
                responseMessage.put("response", responseData);
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
     * <p>
     * get update schedule related information from front end in JSON format
     * update scheduling.xml and update trigger.
     * </p>
     */
    @RequestMapping(value = "/updateScheduleData", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateScheduleData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
            String data = request.getParameter("data");
            JSONObject existingScheduleOption;
            JSONObject updatedScheduleOption;
            ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
            String existingCronExpression;
            String updatedCronExpression;
            ScheduleProcess scheduleProcess = new ScheduleProcess();

            boolean isValidUSer;
            String id;
            String path;

            JSONObject jsonObject = new JSONObject();

            JSONObject jsonData;
            jsonObject.accumulate("data", data);
            jsonData = jsonObject.getJSONObject("data");

            id = jsonData.getString("@id");
            jsonData.remove("@id");
            jsonData.remove("@type");
            JSONObject schedulingJob = jsonData.getJSONObject("SchedulingJob");
            String reportParameters = schedulingJob.getString("reportParameters");
            String EmailSettings = schedulingJob.getString("EmailSettings");
            String reportDirectory = schedulingJob.getString("reportDirectory");
            String reportFile = schedulingJob.getString("reportFile");
            jsonData.remove("SchedulingJob");

            jsonData.accumulate("reportParameters", reportParameters);
            jsonData.accumulate("EmailSettings", EmailSettings);
            jsonData.accumulate("reportDirectory", reportDirectory);
            jsonData.accumulate("reportFile", reportFile);
            logger.debug("Final jsonData:  " + jsonData);
            updatedScheduleOption = jsonData.getJSONObject("ScheduleOptions");
            String updatedStartDate = jsonData.getJSONObject("ScheduleOptions").getString("StartDate");
            logger.debug("updatedScheduleOption:  " + updatedScheduleOption);
            /*
             * getting Schedule path from project.properties file
             */
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
                jsonObject = processor.getJSONObject(path, true);
                boolean idExist = xmlOperation.searchId(jsonObject, id);
                logger.debug("Id Exists? " + idExist);
                if (idExist) {
                    jsonObject = xmlOperation.getParticularObject(path, id);
                    existingScheduleOption = jsonObject.getJSONObject("ScheduleOptions");

                    existingCronExpression = convertIntoCronExpression.convertDateIntoCronExpression
                            (existingScheduleOption);

                    updatedCronExpression = convertIntoCronExpression.convertDateIntoCronExpression
                            (updatedScheduleOption);
                    DateFormat formatter;
                    Date sDate = null;
                    Date eDate = null;
                    String timezone = jsonData.getJSONObject("ScheduleOptions").optString("timeZone");
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

                    if (jsonData.getJSONObject("ScheduleOptions").getString("endsRadio").equalsIgnoreCase("on")) {
                        endDate = jsonData.getJSONObject("ScheduleOptions").getString("EndDate");
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
                    scheduleProcess.updateTrigger(existingCronExpression, updatedCronExpression, id, sDate, eDate,
                            timezone);

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
     * this method is responsible to validate user
     *
     * @param id            a <code>String</code> which specify schedule id
     * @param SchedulerPath a <code>String</code> which specify scheduling.xml path
     * @return true if valid user else return false
     */
    public boolean isValidUser(String id, String SchedulerPath) {
        XmlOperation xmlOperation = new XmlOperation();
        JSONObject jsonObject = xmlOperation.getParticularObject(SchedulerPath, id);
        Object unDeterminedObject = jsonObject.get("security");
        if (unDeterminedObject instanceof JSONObject) {
            return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), (JSONObject) unDeterminedObject);
        } else {
            final String security = jsonObject.getString("security");
            return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), security);
        }
    }

    /**
     * Sets the application context or provides access to application context
     *
     * @param applicationContext The applicationContext of the app
     * @throws org.springframework.beans.BeansException If some thing goes wrong ):-
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduleProcessCall scheduleProcessCall = new ScheduleProcessCall();
        String schedulePath = scheduleProcessCall.getSchedulePath();
        logger.debug("The schedulePath parameter is found and it is " + schedulePath);
        File file = new File(schedulePath);
        try {
            if (file.exists()) {
                userService = (UserService) applicationContext.getBean("userDetailsService");
                String baseUrl = ApplicationProperties.getInstance().getDomain();
                if (logger.isDebugEnabled()) {
                    logger.debug("The base url of the application is " + baseUrl);
                }
                ScheduleProcess scheduleProcess = new ScheduleProcess();
                XmlOperation xmlOperation = new XmlOperation();
                List<String> idsList = xmlOperation.getIdFromJson(schedulePath);
                for (String anId : idsList) {
                    //Delete from memory
                    scheduleProcess.delete(anId);
                }
                scheduleProcessCall.scheduleOperation(schedulePath, baseUrl, userService);
            }
        } catch (JSONException ignore) {
            logger.warn("There was an exception in handling the scheduling file processing. It " +
                    "is" + " ", ignore);
        }
    }
}
