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
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.export.ExportUtils;
import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <p>
 * This class is responsible to execute the scheduled job.
 * </p>
 *
 * @author Prashansa
 * @see ScheduleProcessCall
 */
public class ScheduleJob implements Job, ISchedule {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleJob.class);

    /**
     * <p>
     * This method is responsible to execute job.it is an overridden method from
     * Job interface
     * </p>
     */
    @Override
    public void execute(JobExecutionContext context) {
        logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                        "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                context.getJobDetail());

        JSONObject newData = new JSONObject();
        JSONObject parametersJSON;

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");
        JSONObject json = (JSONObject) dataMap.get("jsonobject");
        logger.debug("Path:  " + path + "JobId:  " + jobId + "  Json : " + json);


        // Read the properties file in the EFW/System/Mail directory
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration" + ".properties");
        Assert.notNull(propertiesMap, "The mailConfiguration.properties map is null!!");

        String hostName = propertiesMap.get("hostName");
        String port = propertiesMap.get("port");
        String from = propertiesMap.get("from");
        String isAuthenticated = propertiesMap.get("isAuthenticated");
        String isSSLEnabled = propertiesMap.get("isSSLEnabled");
        String user = propertiesMap.get("user");
        String password = propertiesMap.get("password");

        final JSONObject schedulingJob = json.getJSONObject("SchedulingJob");
        String reportEfwFile = schedulingJob.getString("reportFile");
        String reportDirectory = schedulingJob.getString("reportDirectory");
        parametersJSON = schedulingJob.getJSONObject("reportParameters");
        JSONObject printOptions = parametersJSON.optJSONObject("printOptions");
        if (printOptions == null) {
            printOptions = new JSONObject();
        }

        ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

        parametersJSON.discard("printOptions");
        String reportCsvParameter = null;


        /*
         * check reportParameter contains csv data or not
		 */

        XmlOperation xmlOperation = new XmlOperation();
        JSONObject jsonObjectCsvData = xmlOperation.getParticularObject(path, String.valueOf(jobId));
        if (jsonObjectCsvData.getJSONObject("SchedulingJob").getJSONObject("reportParameters").containsKey("csvdata")) {
            reportCsvParameter = jsonObjectCsvData.getJSONObject("SchedulingJob").getJSONObject("reportParameters")
                    .getString("csvdata");
        }

        JSONObject emailSettings = schedulingJob.getJSONObject("EmailSettings");
        String recipients = emailSettings.getString("Recipients");

        String[] totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

        String subject = "";
        if (emailSettings.containsKey("Subject")) {
            logger.debug("Email subject available");
            subject = emailSettings.getString("Subject");
            subject = subject + ":  " + json.getString("JobName");
        }

        String body = "";
        if (emailSettings.containsKey("Body")) {
            logger.debug("Email body available");
            body = emailSettings.getString("Body");
        }

        if ((body == null) || "".equals(body) || "[]".equals(body)) {
            body = propertiesMap.get("body");
        }

        String csvData = reportCsvParameter;

        String baseUrl = dataMap.getString("baseUrl");

        String parameters = ControllerUtils.concatenateParameters(parametersJSON);

        //FIXED a bug for phantom js login. Getting the user name based on the
        //user id from the xml.
        Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
        String username = realNames.get("username");
        String appPassword = realNames.get("password");

        /*Fixed a bug - index out of bounds*/
        if (parameters.length() > 0) {
            parameters = parameters.substring(0, parameters.length() - 1);
        }

        logger.debug("The parameters:  " + parameters);

        String data = baseUrl + "?j_username=" + username + "&j_password=" + appPassword +
                "&dir=" + reportDirectory + "&file=" +
                reportEfwFile + "&" + parameters;


        String encodedData = "";
        try {
            encodedData = URLEncoder.encode(data, ApplicationUtilities.getEncoding());
        } catch (UnsupportedEncodingException ignore) {
        }
        String reportName = ReportsUtility.getReportName(json.getString("JobName".trim()));
        String reportSourceType = "url";
        String formats = emailSettings.getString("Formats");
        String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");


        logger.info("The print option is " + printOptions);

        printOptions.put("domain", ApplicationProperties.getInstance().getDomain());
        printOptions.put("username", username);
        printOptions.put("passCode", appPassword);
        String[] attachments = EmailUtility.getAttachmentsArray(theTotalFormats, encodedData, reportSourceType,
                reportName, csvData, printOptions);

        if (logger.isDebugEnabled()) {
            logger.debug("The subject of the email is subject:  " + subject);
        }

        SendMail mailClient = new SendMail();
        // Send mail to all the recipients with all the attachments
        try {
            mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                    password, subject, body, attachments);
        } catch (AddressException ex) {
            logger.error("AddressException", ex);
        } catch (MessagingException ex) {
            logger.error("MessagingException", ex);
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }
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
