package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import com.helicalinsight.scheduling.utils.TemplateReplacer2;
import net.sf.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * EnhancedScheduleJob Class
 * class extends the {@link ScheduleJob} class
 * This class is responsible to execute the scheduled job.
 * It handles database-based scheduling operations.
 * Created by author on 4/2/2020.
 * @author Rajesh
 */
public class EnhancedScheduleJob extends ScheduleJob {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedScheduleJob.class);
    /**
     * Executes the scheduled job, enhances the functionality, and handles fetching scheduled data.
     * reading properties file, generating url, getting email details etc.
     * @param context      context containing information about the job execution and scheduling details
     */
    @Override
    public void execute(JobExecutionContext context) {
        if (JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
            logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                            "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                    context.getJobDetail());

            //JSONObject newData = new JSONObject();
            Map<String, Object> mapOfFireTimes = new HashMap<>();
            JsonObject parametersJSON;
            ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            int jobId = dataMap.getInt("jobinput");
            String path = dataMap.getString("path");
            JsonObject scheduleJson = scheduleOperation.prepareScheduleEntityJson(jobId);
            if(scheduleJson==null){
                logger.error("The job id "+jobId+ " is not found. Continuing to next");
                return;
            }
            JsonObject json = JsonParser.parseString(scheduleJson.toString()).getAsJsonObject();
            Integer noOfExecution = GsonUtility.optIntValue(json,"NoOfExecutions", 0);
            if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {
                JsonObject ScheduleOptionsObject = json.getAsJsonObject("ScheduleOptions");
                String endAfterExecution = GsonUtility.optStringValue(ScheduleOptionsObject,"EndAfterExecutions", null);
                if (noOfExecution >= Integer.parseInt(endAfterExecution)) {
                    logger.debug("Deleting job fom schedule");
                    ScheduleProcess schedulerProcess = new ScheduleProcess();
                    schedulerProcess.delete(String.valueOf(jobId));
                    return;
                }
            }
            String className = json.getAsJsonObject("SchedulingJob").get("@type").getAsString();
            String requiredClass = ScheduleJobFactory.getScheduleClass(className);

            if (!"com.helicalinsight.scheduling.EnhancedScheduleJob".equalsIgnoreCase(requiredClass)) {
                ISchedule scheduleClass = ScheduleJobFactory.getIScheduleJobInstance(requiredClass);
                Job job = (Job) scheduleClass;
                try {
                    job.execute(context);
                    return;
                } catch (JobExecutionException e) {
                    throw new EfwServiceException("Cannot schedule ");
                }

            }
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

            final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
            String reportEfwFile = schedulingJob.get("reportFile").getAsString();
            String reportDirectory = schedulingJob.get("reportDirectory").getAsString();
            parametersJSON = schedulingJob.getAsJsonObject("reportParameters");
            JsonObject printOptions = GsonUtility.optJsonObject(parametersJSON,"printOptions");
            JsonObject adhocFormData = GsonUtility.optJsonObject(parametersJSON,"adhocFormData");
            parametersJSON.remove("adhocFormData");
            if (printOptions == null) {
                printOptions = new JsonObject();
            }

            ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

            parametersJSON.remove("printOptions");
            String reportCsvParameter = null;


            JsonObject emailSettings = schedulingJob.getAsJsonObject("EmailSettings");
            String recipients = emailSettings.get("Recipients").getAsString();

            String[] totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

            String subject = "";
            if (emailSettings.has("Subject")) {
                logger.debug("Email subject available");
                subject = emailSettings.get("Subject").getAsString();
                subject = subject + ":  " + json.get("JobName").getAsString();
            }

            String body = "";
            if (emailSettings.has("Body")) {
                logger.debug("Email body available");
                body = emailSettings.get("Body").isJsonNull()?null:emailSettings.get("Body").getAsString();
            }

            if ((body == null) || "".equals(body) || "[]".equals(body)) {
                body = propertiesMap.get("body");
            }

            String csvData = reportCsvParameter;

            String baseUrl = dataMap.getString("baseUrl");

            String parameters = ControllerUtils.concatenateParameters(parametersJSON);

            //FIXED a bug for phantom js login. Getting the user name, organization based on the
            //user id from the xml.
            Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
            String username = realNames.get("username");
            String organization = realNames.get("organization");
            String appPassword = realNames.get("password");
            String userId=realNames.get("userId");

            /*Fixed a bug - index out of bounds*/
            if (parameters.length() > 0) {
                parameters = parameters.substring(0, parameters.length() - 1);
            }
            String reportName = ReportsUtility.getReportName(json.get("JobName".trim()).getAsString());
            logger.debug("The parameters:  " + parameters);
            String formats = emailSettings.get("Formats").getAsString();
            String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");
            List<String> formatList = new ArrayList<String>(Arrays.asList(theTotalFormats));
            ArrayList<String> attachmentList = new ArrayList<String>();
            if (adhocFormData != null && !adhocFormData.entrySet().isEmpty()) {
                logger.debug("Base url is "+baseUrl);
                String downloadManager = AuthenticationUtils.getDownloadManager();
                String loginFirstUrl=baseUrl.replace("hi.html","?j_username=downloadManager&j_password="+downloadManager);
                String mockUrl;
                if (organization != null) {
                    mockUrl = "mock/impersonate?username=" + username + ":" + organization ;
                }else {
                    mockUrl = "mock/impersonate?username=" + username ;
                }
                String finalUrl = baseUrl.replace("hi.html", mockUrl);
                List<String> resourceUrl = new ArrayList<>();
                resourceUrl.add(loginFirstUrl);
                resourceUrl.add(finalUrl);
                resourceUrl.add(baseUrl.replace("hi.html", "downloadReport"));
                resourceUrl.add(baseUrl.replace("hi.html", "logout"));
                String response = null;
                String fileToDownload;

                if (formatList.contains("csv")) {
                    formatList.remove("csv");
                    adhocFormData.addProperty("type", "csv");
                    adhocFormData.addProperty("reportName", json.get("JobName".trim()).getAsString());
                    response = makeCall(adhocFormData, baseUrl, resourceUrl);
                    if (response != null && response!="") {
                        fileToDownload = response;
                        attachmentList.add(fileToDownload);
                    }
                }
                if (formatList.contains("xls") || formatList.contains("xlsx")) {
                    formatList.remove("xls");
                    formatList.remove("xlsx");
                    adhocFormData.addProperty("type", "xlsx");
                    adhocFormData.addProperty("reportName", json.get("JobName".trim()).getAsString());
                    response = makeCall(adhocFormData, baseUrl, resourceUrl);
                    if (response != null && response!="") {
                        fileToDownload = response;
                        attachmentList.add(fileToDownload);
                    }
                }

            }

            String data;
            if (organization == null) {
                data = baseUrl + "#?username=" + username + "&password=" + appPassword +
                        "&dir=" + reportDirectory + "&file=" +
                        reportEfwFile + "&" + parameters;
            } else {
                data = baseUrl + "?j_organization=" + organization + "&username=" + username +
                        "&password=" + appPassword + "&dir=" + reportDirectory + "&file=" +
                        reportEfwFile + "&" + parameters;
            }

            String encodedData = "";
            try {
                encodedData = URLEncoder.encode(data, ApplicationUtilities.getEncoding());
            } catch (UnsupportedEncodingException ignore) {
            }

            String reportSourceType = "url";


            logger.info("The print option is " + printOptions);

            printOptions.addProperty("domain", ApplicationProperties.getInstance().getDomain());
            printOptions.addProperty("username", username);
            printOptions.addProperty("passCode", appPassword);
            if(organization!=null) {
                printOptions.addProperty("organization", organization);
            }

            if (!formatList.isEmpty()) {
                String[] array = formatList.toArray(new String[formatList.size()]);
                String[] attachments = EmailUtility.getAttachmentsArray(array, encodedData, reportSourceType,
                        reportName, csvData, printOptions);
                attachmentList.addAll(Arrays.asList(attachments));
            }

            if (logger.isDebugEnabled()) {
                logger.debug("The subject of the email is subject:  " + subject);
            }

            SendMail mailClient = new SendMail();


            JSONObject nn=new JSONObject();

            nn.put("reportDir", reportDirectory);
            nn.put("reportFile", reportEfwFile);
            nn.put("reportFileName", reportEfwFile);
            nn.put("reportNameWithExtension", reportEfwFile);





            parametersJSON.addProperty("userIdSchedule",userId);
            body= TemplateReplacer2.replaceEmailComponents(schedulingJob.toString(),body,false,baseUrl);
            body= TemplateReplacer2.replaceEmailComponents(nn.toString(),body,false,baseUrl);
            body= TemplateReplacer2.replaceEmailComponents(parametersJSON.toString(),body,true,baseUrl);
            recipients= TemplateReplacer2.replaceEmailComponents(schedulingJob.toString(),recipients,false,baseUrl);
            recipients= TemplateReplacer2.replaceEmailComponents(nn.toString(),recipients,false,baseUrl);
            recipients= TemplateReplacer2.replaceEmailComponents(parametersJSON.toString(),recipients,true,baseUrl);
            recipients=recipients.replaceAll("'","");
            subject= TemplateReplacer2.replaceEmailComponents(schedulingJob.toString(),subject,false,baseUrl);
            subject= TemplateReplacer2.replaceEmailComponents(nn.toString(),subject,false,baseUrl);
            subject= TemplateReplacer2.replaceEmailComponents(parametersJSON.toString(),subject,true,baseUrl);
            totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

            parametersJSON.remove("userIdSchedule");
            // Send mail to all the recipients with all the attachments
            Boolean success = true;
            try {

                mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                        password, subject, body, attachmentList.toArray(new String[attachmentList.size()]));
                printOptions.remove("domain");
                printOptions.remove("username");
                printOptions.remove("passCode");
                printOptions.remove("organization");
                parametersJSON.add("printOptions", printOptions);
            } catch (AddressException ex) {
                success = false;
                logger.error("AddressException", ex);
            } catch (MessagingException ex) {
                success = false;
                logger.error("MessagingException", ex);
            } catch (IOException ex) {
                success = false;
                logger.error("IOException", ex);
            }

            String id = String.valueOf(jobId);
            //String numberOfExecution = json.optString("NoOfExecutions");
            logger.debug("The numberOfExecution:  " + noOfExecution);


            noOfExecution = noOfExecution + 1;

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
            mapOfFireTimes.put("NoOfExecutions", noOfExecution);

            if (!mapOfFireTimes.isEmpty()) {
                scheduleOperation.updateScheduleStatus(Long.valueOf(id), mapOfFireTimes, json);
            }


        } else {
            super.execute(context);
        }
    }

    /**
     * makeCall(JsonObject adhocFormData, String baseUrl, List<String> resourceUrl)
     * @param adhocFormData        formData
     * @param baseUrl			   url used for login, logout
     * @param resourceUrl		   urls to retrieve information form an Email related.
     * @return HTTP response data in string format
     */
    private String makeCall(JsonObject adhocFormData, String baseUrl, List<String> resourceUrl) {
        String response = null;
        try {
            response = EmailUtility.makeBinaryHttpCall(adhocFormData, resourceUrl);
        } finally {
            EmailUtility.doLogout(baseUrl);
        }
        return response;
    }
}
