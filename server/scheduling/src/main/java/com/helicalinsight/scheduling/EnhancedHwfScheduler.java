package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

/**
 * EnhancedHwfScheduler class extends the functionality of the  {@link HwfScheduler} class.
 * Responsible for execution of HWF file.
 * Created by author on 4/2/2020.
 *
 * @author Rajesh
 */
public class EnhancedHwfScheduler extends HwfScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedHwfScheduler.class);

    /**
     * execute(JobExecutionContext context)
     * It manages execution counts, constructs URLs,
     * makes HTTP requests, handles responses, and updates relevant data in the database.
     *
     * @param context containing information about the job execution,scheduling
     * @throws JobExecutionException if an error occurs during job execution.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (JsonUtils.isScheduleStorageTypeIsDatabase()) {
            logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " + "" + "Job Details {}",
                    context.getFireTime(), context.getJobInstance(), context.getTrigger(), context.getJobDetail());
            ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
            // JSONObject newData = new JSONObject();
            Map<String, Object> mapOfFireTimes = new HashMap<>();
            JsonObject inputJson;

            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            int jobId = dataMap.getInt("jobinput");
            String path = dataMap.getString("path");

            JsonObject json = JsonParser.parseString(scheduleOperation.prepareScheduleEntityJson(jobId).toString()).getAsJsonObject();

            logger.debug("Path:  " + path + "JobId:  " + jobId + "  Json : " + json);

            String numberOfExecution = GsonUtility.optString(json, "NoOfExecutions");
            logger.debug("The numberOfExecution:  " + numberOfExecution);
            String id = String.valueOf(jobId);
            Integer noOfExecution = GsonUtility.optIntValue(json, "NoOfExecutions", 0);
            if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {
                mapOfFireTimes.put("NoOfExecutions", noOfExecution);
                JsonObject ScheduleOptionsObject = json.getAsJsonObject("ScheduleOptions");
                String endAfterExecution = GsonUtility.optStringValue(ScheduleOptionsObject, "EndAfterExecutions", null);
                if (noOfExecution >= Integer.parseInt(endAfterExecution)) {
                    logger.debug("Deleting job fom schedule");
                    ScheduleProcess schedulerProcess = new ScheduleProcess();
                    schedulerProcess.delete(id);
                    return;
                }
            }
            final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
            String hwfFile = "";
            hwfFile = schedulingJob.get("reportFile").getAsString();
            String hwfDirectory = "";
            hwfDirectory = schedulingJob.get("reportDirectory").getAsString();
            inputJson = schedulingJob.getAsJsonObject("reportParameters");

            // encode to the base64 before passing it to the url
            String parameters;
            if (inputJson.has("hwfDir")) {
                hwfDirectory = inputJson.get("hwfDir").getAsString();
                inputJson.remove("hwfDir");
            }
            if (inputJson.has("hwfFile")) {
                hwfFile = inputJson.get("hwfFile").getAsString();
                inputJson.remove("hwfFile");
            }


            if (!GsonUtility.optBoolean(inputJson, "base64Encoded")) {
                Set<String> set = inputJson.keySet();
                JsonObject copyInput = new JsonObject();
                org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
                for (String key : set) {
                    String valueOfKey = inputJson.get(key).getAsString();
                    String encodedValue = base64.encodeToString(valueOfKey.getBytes());
                    copyInput.addProperty(key, encodedValue);
                }
                copyInput.addProperty("base64Encoded", "true");
                parameters = ControllerUtils.concatenateParameters(copyInput);
            } else {
                parameters = ControllerUtils.concatenateParameters(inputJson);
            }

            Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
            String username = realNames.get("username");
            String organization = realNames.get("organization");
            String appPassword = realNames.get("password");

            /* Fixed a bug - index out of bounds */
            if (parameters.length() > 0) {
                parameters = parameters.substring(0, parameters.length() - 1);
            }
            String baseUrl = ApplicationProperties.getInstance().getDomain();
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
            String hwfUrl = baseUrl + "workflow";
            logger.debug("The parameters:  " + parameters);
            String downloadManager = AuthenticationUtils.getDownloadManager();
            String loginFirstUrl = baseUrl + "?j_username=downloadManager&j_password="+downloadManager;
            String mockUrl;
            if (organization != null) {
                mockUrl = "mock/impersonate?username=" + username + ":" + organization;
            } else {
                mockUrl = "mock/impersonate?username=" + username;
            }
            mockUrl = baseUrl+mockUrl;


            String finalUrl = hwfUrl + "?dir=" + hwfDirectory + "&fileName=" + hwfFile + "&" + parameters;

            logger.info(finalUrl);
            InputStream response = null;
            Boolean success = true;
            try {

                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                URL url = new URL(loginFirstUrl);
                URLConnection connection = null;
                try {
                    connection = url.openConnection();
                    response = connection.getInputStream();
                } catch (Exception e) {
                    logger.debug("Ignore error login");
                }
                if (response != null) {
                    try (Scanner scanner = new Scanner(response)) {
                        String responseBody = scanner.useDelimiter("\\A").next();
                        logger.info(responseBody);
                    }
                }
                try {
                    connection = new URL(mockUrl).openConnection();
                    response = connection.getInputStream();
                } catch (Exception e) {
                    logger.debug("Mock error");
                }
                if (response != null) {
                    try (Scanner scanner = new Scanner(response)) {
                        String responseBody = scanner.useDelimiter("\\A").next();
                        logger.info(responseBody);
                    }
                }

                connection = new URL(finalUrl).openConnection();
                response = connection.getInputStream();

                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    logger.info(responseBody);
                }


                connection = new URL(baseUrl + "logout").openConnection();
                response = connection.getInputStream();

                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();
                    logger.info(responseBody);
                }
            } catch (IOException ex) {
                success = false;
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

            noOfExecution = noOfExecution + 1;
            mapOfFireTimes.put("NoOfExecutions", noOfExecution);
            Date nextFireTime = context.getNextFireTime();
            if (nextFireTime != null)
                mapOfFireTimes.put("NextExecutionOn", nextFireTime);
            Date fireTime = context.getFireTime();
            if (fireTime != null)
                mapOfFireTimes.put("LastExecutedOn", fireTime);

            logger.debug("The newData :  " + mapOfFireTimes);
            logger.debug("Value of context.getResult():  " + context.getResult());
            if (!success) {
                mapOfFireTimes.put("LastExecutionStatus", "1");
            }

            if (!mapOfFireTimes.isEmpty()) {
                scheduleOperation.updateScheduleStatus(Long.valueOf(id), mapOfFireTimes, json);
            }

        } else {
            super.execute(context);
        }
    }

}
