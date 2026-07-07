package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.export.ExportUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.helicalinsight.efw.utility.JsonUtils.safeGetJsonObject;

/**
 * <p>
 * ScheduleJob implements {@link Job} interface and custom {@link ISchedule} interface.
 * This class is responsible to execute the scheduled job.
 * </p>
 * this class is no longer used
 * use {@link  EnhancedScheduleJob} class
 * @author Prashansa
 * @version 1.1
 * @see ScheduleProcessCall
 */
@Deprecated
public class ScheduleJob implements Job, ISchedule {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleJob.class);

    /**
     * <p>
     * execute(JobExecutionContext context)
     * This method is responsible to execute job.it is an overridden method from
     * Job interface.
     * </p>
     * @param context                   containing information about the job execution,scheduling
     */
    @Override
    public void execute(JobExecutionContext context) {
        logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                        "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                context.getJobDetail());

        JsonObject newData = new JsonObject();
        JsonObject parametersJSON;

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");
        //JSONObject json = JSONObject.fromObject(dataMap.get("jsonobject"));
        JsonObject json = safeGetJsonObject(dataMap.get("jsonobject"));
        //new Gson().fromJson(new Gson().toJson(dataMap.get("jsonobject")),JsonObject.class);
        String className = json.getAsJsonObject("SchedulingJob").get("@type").getAsString();
        String requiredClass = ScheduleJobFactory.getScheduleClass(className);
        if ("com.helicalinsight.scheduling.EnhancedScheduleJob".equalsIgnoreCase(requiredClass) && !JsonUtils.isScheduleStorageTypeIsDatabase()) {
            requiredClass = "com.helicalinsight.scheduling.ScheduleJob";
        }
        if (!"com.helicalinsight.scheduling.ScheduleJob".equalsIgnoreCase(requiredClass)) {
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
        JsonObject printOptions = optJsonObject(parametersJSON,"printOptions");
        JsonObject adhocFormData = optJsonObject(parametersJSON,"adhocFormData");
        parametersJSON.remove("adhocFormData");
        if (printOptions == null) {
            printOptions = new JsonObject();
        }

        ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

        parametersJSON.remove("printOptions");
        String reportCsvParameter = null;


        /*
         * check reportParameter contains csv data or not
         */
        XmlOperation xmlOperation = new XmlOperation();
       /*
        JSONObject jsonObjectCsvData = xmlOperation.getParticularObject(path, String.valueOf(jobId));
        if (jsonObjectCsvData.getJSONObject("SchedulingJob").getJSONObject("reportParameters").containsKey("csvdata")) {
            reportCsvParameter = jsonObjectCsvData.getJSONObject("SchedulingJob").getJSONObject("reportParameters")
                    .getString("csvdata");
        }*/

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
            body = emailSettings.get("Body").getAsString();
        }

        if ((body == null) || "".equals(body) || "[]".equals(body)) {
            body = propertiesMap.get("body");
        }

        String csvData = reportCsvParameter;

        String baseUrl = dataMap.getString("baseUrl");

        String parameters = ControllerUtils.concatenateParameters(parametersJSON);
        String jobNameString = json.get("JobName".trim()).getAsString();
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

        String formats = emailSettings.get("Formats").getAsString();
        String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");
        List<String> formatList = new ArrayList<String>(Arrays.asList(theTotalFormats));
        ArrayList<String> attachmentList = new ArrayList<String>();
        if (adhocFormData != null && !adhocFormData.entrySet().isEmpty()) {
            String mockUrl;
            String downloadManager = AuthenticationUtils.getDownloadManager();
            if (organization != null)
                mockUrl = "mock/impersonate?username=" + username + ":" + organization + "&username=downloadManager&password="+downloadManager;
            else {
                mockUrl = "mock/impersonate?username=" + username + "&username=downloadManager&password="+downloadManager;
            }
            String finalUrl = baseUrl.replace("visualizeAdhoc.html", mockUrl);
            List<String> resourceUrl = new ArrayList<>();
            resourceUrl.add(finalUrl);
            resourceUrl.add(baseUrl.replace("visualizeAdhoc.html", "exportData.html"));
            String response = null;
            String fileToDownload;
            if (formatList.contains("csv")) {
                formatList.remove("csv");
                adhocFormData.addProperty("type", "csv");
                adhocFormData.addProperty("reportName", jobNameString);
                response = makeCall(adhocFormData, baseUrl, resourceUrl);
                if (response != null) {
                    fileToDownload = response;
                    attachmentList.add(fileToDownload);
                }
            }
            if (formatList.contains("xls") || formatList.contains("xlsx")) {
                formatList.remove("xls");
                formatList.remove("xlsx");
                adhocFormData.addProperty("type", "xlsx");
                adhocFormData.addProperty("reportName", jobNameString);
                response = makeCall(adhocFormData, baseUrl, resourceUrl);
                if (response != null) {
                    fileToDownload = response;
                    attachmentList.add(fileToDownload);
                }
            }

        }


        logger.debug("The parameters:  " + parameters);

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

        String reportName = ReportsUtility.getReportName(jobNameString);
        String reportSourceType = "url";
        // String formats = emailSettings.getString("Formats");
        //String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");


        logger.info("The print option is " + printOptions);

        printOptions.addProperty("domain", ApplicationProperties.getInstance().getDomain());
        printOptions.addProperty("username", username);
        printOptions.addProperty("passCode", appPassword);
        if(organization!=null) {
            printOptions.addProperty("organization", organization);
        }
        if (!formatList.isEmpty()) {
            String[] attachments = EmailUtility.getAttachmentsArray(formatList.toArray(new String[formatList.size()]), encodedData, reportSourceType,
                    reportName, csvData, printOptions);
            attachmentList.addAll(Arrays.asList(attachments));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The subject of the email is subject:  " + subject);
        }



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
        recipients= TemplateReplacer2.replaceEmailComponents(parametersJSON.toString(),recipients,true,baseUrl);
        recipients=recipients.replaceAll("'","");
        subject= TemplateReplacer2.replaceEmailComponents(schedulingJob.toString(),subject,false,baseUrl);
        subject= TemplateReplacer2.replaceEmailComponents(nn.toString(),subject,false,baseUrl);
        subject= TemplateReplacer2.replaceEmailComponents(parametersJSON.toString(),subject,true,baseUrl);
       totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

        parametersJSON.remove("userIdSchedule");






        SendMail mailClient = new SendMail();
        // Send mail to all the recipients with all the attachments
        try {
            mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                    password, subject, body, attachmentList.toArray(new String[attachmentList.size()]));
            printOptions.remove("domain");
            printOptions.remove("username");
            printOptions.remove("passCode");
            printOptions.remove("organization");
            parametersJSON.add("printOptions", printOptions);
        } catch (AddressException ex) {
            logger.error("AddressException", ex);
        } catch (MessagingException ex) {
            logger.error("MessagingException", ex);
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }
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

    private static JsonObject optJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject != null) {
            JsonElement jsonElement = jsonObject.get(key);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            }
        }
        return null;
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
