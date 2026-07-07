package com.helicalinsight.scheduling;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.apache.commons.codec.binary.Base64;
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
import java.util.Set;


/**
 * HwfScheduler class implements the {@link Job} interface and custom {@link ISchedule} interface.
 * It is responsible for execution of HWF and scheduled tasks related to (HWF) .
 * HWF stands Helical work flow, a set of dashboard template.
 * @author Somen
 * Created on 5/18/2016.
 */
public class HwfScheduler implements Job, ISchedule {
    private static final Logger logger = LoggerFactory.getLogger(HwfScheduler.class);
    /**
     * execute(JobExecutionContext context) throws JobExecutionException
     * Executes the scheduled HWF job based on the provided JobExecutionContext.
     *
     * This method handles the execution of HWF by constructing URLs, making HTTP requests,
     * and updating associated data. It also performs parameter encoding, manages authentication,
     * and handles response data.
     *
     * @param context 				containing information about the job execution,scheduling 
     * @throws JobExecutionException if an error occurs during job execution.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                        "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                context.getJobDetail());

        JsonObject newData = new JsonObject();


        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");
        //JSONObject json = JSONObject.fromObject(dataMap.get("jsonobject"));
        JsonObject json = new Gson().fromJson(new Gson().toJson(dataMap),JsonObject.class);
        logger.debug("Path:  " + path + "JobId:  " + jobId + "  Json : " + json);

        final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
        String hwfFile = schedulingJob.get("reportFile").getAsString();
        String hwfDirectory = schedulingJob.get("reportDirectory").getAsString();
        JsonObject inputJson = schedulingJob.getAsJsonObject("reportParameters");
        //encode to the base64 before passing it to the url
        String parameters;
        if (!optBoolean(inputJson,"base64Encoded")) {
            Set<String> set = inputJson.keySet();
            JsonObject copyInput = new JsonObject();
            Base64 base64 = new Base64();
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

        /*Fixed a bug - index out of bounds*/
        if (parameters.length() > 0) {
            parameters = parameters.substring(0, parameters.length() - 1);
        }
        String baseUrl = ApplicationProperties.getInstance().getDomain();
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/") + 1);
        String hwfUrl = baseUrl + "workflow.html";
        logger.debug("The parameters:  " + parameters);

        String finalUrl;
        if (organization == null) {
            finalUrl = hwfUrl + "#?username=" + username + "&password=" + appPassword +
                    "&dir=" + hwfDirectory + "&fileName=" + hwfFile + "&" + parameters;
        } else {
            finalUrl = hwfUrl + "?j_organization=" + organization + "&username=" + username +
                    "&password=" + appPassword + "&dir=" + hwfDirectory + "&fileName=" +
                    hwfFile + "&" + parameters;
        }
        logger.info(finalUrl);
        InputStream response = null;
        try {

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            //finalUrl = URLEncoder.encode(finalUrl, StandardCharsets.UTF_8.toString());
            URL url = new URL(finalUrl);
            URLConnection connection = url.openConnection();
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
        String numberOfExecution = json.get("NoOfExecutions").getAsString();
        logger.debug("The numberOfExecution:  " + numberOfExecution);
        if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {
            JsonObject object;
            object = xmlOperation.getParticularObject(path, id);
            newData.addProperty("NoOfExecutions", Integer.parseInt(object.get("NoOfExecutions").getAsString()) + 1);
        }
        newData.addProperty("NextExecutionOn", context.getNextFireTime().toString());
        newData.addProperty("LastExecutedOn", context.getFireTime().toString());

        logger.debug("The newData :  " + newData);
        logger.debug("Value of context.getResult():  " + context.getResult());
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();

        xmlOperationWithParser.updateExistingXml(newData, path, id);
    }
    /**
     * optBoolean(JsonObject jsonObject, String key)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns true ,otherwise false.
     * @param jsonObject 		
     * @param key 				key to check .
     * @return boolean			true or false
     */
	private static boolean optBoolean(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isBoolean()) {
				return jsonElement.getAsBoolean();
			}
		}
		return false;
	}
}