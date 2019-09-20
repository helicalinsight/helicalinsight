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

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controller.EFWController;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This class is responsible for doing scheduling related operation
 *
 * @author Prashansa
 */
public class ScheduleProcessCall {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleProcessCall.class);

    /**
     * <p>
     * scheduleOperation() is responsible for reading the scheduling.xml from
     * given path and schedule all the job given in XML
     * </p>
     *
     * @param path    a <code>String</code> specify path of scheduling.xml
     * @param baseUrl a <code>String</code> specify base URL like:
     *                http://localhost:9090/Example/test.html
     * @see EFWController
     */
    public void scheduleOperation(String path, String baseUrl, UserService userService) {
        JSONArray jsonArray1;
        String cronExpression;

        XmlOperation xmlOperation = new XmlOperation();

        jsonArray1 = xmlOperation.convertXmlStringIntoJSONArray(path);

        ISchedule scheduleClass;

		/*
         * This className is static value after proper integration this value
		 * will come from some other place
		 */
        String className = "com.helicalinsight.scheduling.ScheduleJob";
        String jobName;
        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        for (int count = 0; count < jsonArray1.size(); count++) {
            JSONObject node = jsonArray1.getJSONObject(count);
            String scheduleId = node.getString("@id");
            String id = scheduleId;
            if (id.equals("0")) {
                logger.debug("discarding 0 id");
                node.discard("@id");
                node.discard("isActive");
                logger.debug("jsonArray1" + node.isEmpty());
                logger.debug("" + node.containsKey("EndAfterExecutions"));
            }
            if (!node.isEmpty() && node.getString("isActive").equals("true")) {
                JSONObject newJsonObject;
                newJsonObject = node;
                logger.debug("count:  " + count);
                logger.debug("newJSoJsonObject:  " + newJsonObject);
                logger.debug("contains end after execution" + newJsonObject.getJSONObject("ScheduleOptions")
                        .containsKey("EndAfterExecutions"));
                if (newJsonObject.getJSONObject("ScheduleOptions").containsKey("EndAfterExecutions")) {
                    String endAfterExecution = newJsonObject.getJSONObject("ScheduleOptions").getString
                            ("EndAfterExecutions");
                    logger.debug("endAfterExecution: " + endAfterExecution);
                    logger.debug("newJSoJsonObject.getString: " + newJsonObject.getString("NoOfExecutions"));
                    logger.debug("condition: " + Integer.parseInt(newJsonObject.getString("NoOfExecutions")));
                    logger.debug("" + Integer.parseInt(endAfterExecution));
                    if (newJsonObject.getString("NoOfExecutions").equalsIgnoreCase(endAfterExecution) || Integer
                            .parseInt(newJsonObject.getString("NoOfExecutions")) >= Integer.parseInt
                            (endAfterExecution)) {
                        logger.debug("Deleting job fom schedule");
                        schedulerProcess.delete(id);
                        continue;
                    }
                }
                jobName = String.valueOf(id);
                net.sf.json.JSONObject scheduleOption = node.getJSONObject("ScheduleOptions");
                logger.debug("ScheduleOption:  " + scheduleOption.getString("Frequency"));
                String jobType = node.getJSONObject("SchedulingJob").getString("@type");
                logger.info("Scheduling type: " + jobType);
                if ("hwf".equalsIgnoreCase(jobType)) {
                    className = "com.helicalinsight.scheduling.HwfScheduler";
                }
                cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(scheduleOption);

                if (!scheduleId.equals("0")) {
                    int userId = Integer.valueOf(AuthenticationUtils.getUserId(node));
                    User currentUser = userService.findUser(userId);
                    if (currentUser != null && !currentUser.getUsername().equals("")) {
                        try {
                            //Calling FactoryMethodWrapper.getUntypedInstance() will produce NPE as the code is being
                            //called at application boot time.

                            //In case if the class is from an external Plugin then exception needs to be handled
                            Class<?> name = FactoryMethodWrapper.forName(className);
                            scheduleClass = (ISchedule) name.newInstance();
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                        schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, "DEFAULT", path,
                                newJsonObject, baseUrl);
                    }
                }
            }
        }
    }

    /**
     * this method is responsible to schedule specific job on the basis of id.
     *
     * @param path    a <code>String</code> specify path of scheduling.xml
     * @param theId   a <code>String</code> specify id.
     * @param baseUrl a <code>String</code> specify base URL like:
     *                http://localhost:9090/Example/test.html
     */
    public String scheduleSpecificJob(String path, String theId, String baseUrl) {
        ISchedule scheduleClass;
        String className = "com.helicalinsight.scheduling.ScheduleJob";
        String jobName;

        XmlOperation xmlOperation = new XmlOperation();

        JSONObject jsonObject = xmlOperation.getParticularObject(path, theId);

        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        JSONObject jsonObjectScheduleOption = jsonObject.getJSONObject("ScheduleOptions");
        xmlOperation.getParticularObject(path, theId);
        String jobType = "DEFAULT";
        String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(jsonObjectScheduleOption);

        jobName = jsonObject.getString("@id");

        JSONObject reportParameter;
        try {
            reportParameter = jsonObject.getJSONObject("SchedulingJob").getJSONObject("reportParameters");
            logger.debug("reportParameter:  " + reportParameter);
        } catch (Exception ignore) {
            logger.error("Exception stack trace is ", ignore);
        }

        logger.debug("jsonObject before sending to schedule: " + jsonObject);
        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, jobType, path, jsonObject, baseUrl);
        return "scheduled";
    }

    /**
     * getSchedulePath() is responsible to read scheduling.xml path from
     * project.properties file
     *
     * @return scheduling.xml file path
     * @see EFWController
     */
    public String getSchedulePath() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> map = propertiesFileReader.read("project.properties");
        return map.get("schedulerPath");
    }
}
