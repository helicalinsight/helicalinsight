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


import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;
import java.util.Scanner;


/**
 * @author Somen
 */
public class HwfScheduler implements Job, ISchedule {
    private static final Logger logger = LoggerFactory.getLogger(HwfScheduler.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                        "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                context.getJobDetail());

        JSONObject newData = new JSONObject();
        JSONObject inputJson;

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");
        JSONObject json = (JSONObject) dataMap.get("jsonobject");
        logger.debug("Path:  " + path + "JobId:  " + jobId + "  Json : " + json);

        final JSONObject schedulingJob = json.getJSONObject("SchedulingJob");
        String hwfFile = schedulingJob.getString("hwfFile");
        String hwfDirectory = schedulingJob.getString("hwfDirectory");
        inputJson = schedulingJob.getJSONObject("hwfParameters");

        String parameters = ControllerUtils.concatenateParameters(inputJson);






        Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
        String username = realNames.get("username");
        String appPassword = realNames.get("password");

        /*Fixed a bug - index out of bounds*/
        if (parameters.length() > 0) {
            parameters = parameters.substring(0, parameters.length() - 1);
        }
        String baseUrl = ApplicationProperties.getInstance().getDomain();
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
        String hwfUrl = baseUrl + "workflow.html";
        logger.debug("The parameters:  " + parameters);

        String finalUrl;
        finalUrl = hwfUrl + "?j_username=" + username + "&j_password=" + appPassword +
                "&dir=" + hwfDirectory + "&fileName=" + hwfFile + "&" + parameters;
        InputStream response = null;
        try {

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            URL url = new URL(finalUrl);
            URLConnection connection = url.openConnection();
            response = connection.getInputStream();

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                logger.info(responseBody);
            }
            connection = new URL(baseUrl + "j_spring_security_logout").openConnection();
            response = connection.getInputStream();

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                logger.info(responseBody);
            }
        } catch (IOException ex) {
            logger.error("The exception occurred ", ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    logger.error("Error Occurred ", ex);
                }
            }
        }

        XmlOperation xmlOperation = new XmlOperation();

        String id = String.valueOf(jobId);
        String numberOfExecution = json.getString("NoOfExecutions");
        logger.debug("The numberOfExecution:  " + numberOfExecution);
        if (json.getJSONObject("ScheduleOptions").getString("endsRadio").equalsIgnoreCase("After")) {
            JSONObject object;
            object = xmlOperation.getParticularObject(path, id);
            newData.accumulate("NoOfExecutions", Integer.parseInt(object.getString("NoOfExecutions")) + 1);
        }
        newData.accumulate("NextExecutionOn", context.getNextFireTime());
        newData.accumulate("LastExecutedOn", context.getFireTime());

        logger.debug("The newData :  " + newData);
        logger.debug("Value of context.getResult():  " + context.getResult());
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();

        xmlOperationWithParser.updateExistingXml(newData, path, id);
    }
}