package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
import com.helicalinsight.scheduling.utils.TemplateReplacer2;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.adhoc.jreport.HCRHelper;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.export.ExportUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The HCRScheduler class is responsible for executing (HCR) jobs
 * using Quartz Scheduler. It implements the {@link Job} interface and custom {@link ISchedule} interface.
 * This class retrieves job data from the context, prepares necessary data, generates HCR,
 * and sends it via email as per the specified schedule.
 * a "job" refers to a unit of work that needs to be executed based on a predefined schedule or trigger. 
 * a job is a task or process that you want to automate and execute at specific intervals or under specific conditions.
 *
 * Created by author on 11/28/2019.
 * @author Rajesh
 */
//TODO need to configure this class in setting.xml and need to send response in a proper way and need to use ScheduleUpdater class hcr methods in controller for triggering the hcrScheduling update in scheduling.xml

/**
 * @deprecated in favor of {@link #EnhancedHCRScheduler}
 */
@Deprecated(forRemoval = true)
public class HCRScheduler implements Job, ISchedule {
    private static final Logger logger = LoggerFactory.getLogger(HCRScheduler.class);
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * execute(JobExecutionContext context)
     * This method is called by Scheduler when the scheduled time is reached.
     * It retrieves job data, processes it, generates the report (HCR),
     * and sends it via email.
     *
     * @param context 				containing information about the job execution,scheduling 
     * @throws JobExecutionException If an exception occurs during job execution
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " +
                        "" + "Job Details {}", context.getFireTime(), context.getJobInstance(), context.getTrigger(),
                context.getJobDetail());

        JsonObject newData = new JsonObject();
        JsonObject parametersJSON;
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int jobId = dataMap.getInt("jobinput");
        String path = dataMap.getString("path");
        //JSONObject json = JSONObject.fromObject(dataMap.get("jsonobject"));
        JsonObject json = new Gson().fromJson(new Gson().toJson(dataMap), JsonObject.class);
        logger.debug("Path:  " + path + "JobId:  " + jobId + "  Json : " + json);

        final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
        String reportEfwFile = schedulingJob.get("reportFile").getAsString();
        String reportDirectory = schedulingJob.get("reportDirectory").getAsString();

        String jobType = schedulingJob.get("@type").getAsString();
        String hcrFileName = schedulingJob.get("reportFile").getAsString();
        String hcrDirectory = schedulingJob.get("reportDirectory").getAsString();
        parametersJSON = schedulingJob.getAsJsonObject("reportParameters");
        JsonObject printOptions = GsonUtility.optJsonObject(parametersJSON,"printOptions");
        if (printOptions == null) {
            printOptions = new JsonObject();
        }
        String extension = FileUtils.getExtension(hcrFileName);
        String hcrExtension = JsonUtils.getHCRExtension();
        if (!jobType.equals(hcrExtension) && !extension.equals(hcrExtension)) {
            throw new HCRException("The file type is not supported. Only " +
                    hcrExtension + " files are supported.");
        }
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

        ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

        parametersJSON.remove("printOptions");
        String reportCsvParameter = null;
        XmlOperation xmlOperation = new XmlOperation();
        JsonObject jsonObjectCsvData = xmlOperation.getParticularObject(path, String.valueOf(jobId));
        if (jsonObjectCsvData.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters").has("csvdata")) {
            reportCsvParameter = jsonObjectCsvData.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters")
                    .get("csvdata").getAsString();
        }

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

        //String parameters = ControllerUtils.concatenateParameters(parametersJSON);

        //FIXED a bug for phantom js login. Getting the user name, organization based on the
        //user id from the xml.
        //mock user
        Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
        String username = realNames.get("username");
        String organization = realNames.get("organization");
        String userId=realNames.get("userId");
        String downloadManager = AuthenticationUtils.getDownloadManager();
        String loginFirstUrl=baseUrl.replace("hi.html","?j_username=downloadManager&j_password="+downloadManager);
        String mockUrl;
        if (organization != null)
            mockUrl = "mock/impersonate?username=" + username + ":" + organization + "&j_username=downloadManager&j_password="+downloadManager;
        else {

            mockUrl = "mock/impersonate?username=" + username + "&j_username=downloadManager&j_password=" + downloadManager;
        }
        String finalUrl = baseUrl.replace("hi.html", mockUrl);
        //logger.error("final url :" + finalUrl);
        String formats = emailSettings.get("Formats").getAsString();
        String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");
        //logger.error("The parameters:  " + parametersJSON);
        JsonObject formData = HCRHelper.prepareFormDataForHCRParameters(parametersJSON, prepareFormDataForHCRExportReport(hcrFileName, hcrDirectory, theTotalFormats));
        List<String> resourceUrl = new ArrayList<>();
        resourceUrl.add(loginFirstUrl);
        resourceUrl.add(finalUrl);
        resourceUrl.add(baseUrl.replace("hi.html", "services.html"));
        resourceUrl.add(baseUrl.replace("hi.html", "logout"));

        String response = null;
        response = EmailUtility.makeHttpCall(formData, resourceUrl);

        //JSONObject responseFromJasper = JSONObject.fromObject(response);
        JsonObject responseFromJasper = new Gson().fromJson(response, JsonObject.class);
        JsonObject responseJson = responseFromJasper.getAsJsonObject("response");
        JsonObject jrxmlData = responseJson.getAsJsonObject("jrxmlData");
        JsonArray exportedFiles = GsonUtility.optJsonArray(jrxmlData,"exportedFiles");
        List<String> listOfFiles = new ArrayList<>();
        for (int index = 0; index < exportedFiles.size(); index++) {
            listOfFiles.add(exportedFiles.get(index).getAsString());
        }
        String[] attachments = listOfFiles.toArray(new String[0]);

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

        // Send mail to all the recipients with all the attachments
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
        try {
            mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
                    password, subject, body, attachments);
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


       /* Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
        String username = realNames.get("username");
        String organization = realNames.get("organization");
        String appPassword = realNames.get("password");*/

       /* String uuid = hcrFileName.replace("." + hcrExtension, "");
        if (!hcrFileName.contains("." + hcrExtension)) {
            uuid = hcrFileName;
            hcrFileName = hcrFileName.concat("." + hcrExtension);
        }
        String solutionDirectory = applicationProperties.getSolutionDirectory() + File.separator + hcrDirectory;

        File hcrFile = new File(solutionDirectory + File.separator + hcrFileName);
        if (!hcrFile.exists()) {
            throw new ResourceNotFoundException("There is no HCR " + "resource with the " +
                    "specified name: " + hcrFileName);
        }
        HCReport hcr = JaxbUtils.unMarshal(HCReport.class, hcrFile);
        String formData = hcr.getFormData();
        IComponent reportStatisticsProviderComponent = FactoryMethodWrapper
                .getTypedInstance("com.helicalinsight.adhoc.services.GenerateHCReport", IComponent.class);
        String result = reportStatisticsProviderComponent.executeComponent(formData);*/

    }
    /**
     * prepareFormDataForHCRExportReport(String hcrFileName, String hcrDirectory, String[] theTotalFormats)
     * Prepares a JsonObject containing HCR properties for exporting a report.
     * @param hcrFileName                name of the hcr file
     * @param hcrDirectory               directory of file
     * @param theTotalFormats			 formats of the attachments of the dashboard
     * @return jsonObject contains HCR properties.
     * @throws ResourceNotFoundException If the specified HCR resource does not exist.
     */
    protected JsonObject prepareFormDataForHCRExportReport(String hcrFileName, String hcrDirectory, String[] theTotalFormats) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource hiResource = serviceDB.getResourceByUrl(hcrDirectory+"/"+hcrFileName);
        if (hiResource == null) {
            throw new ResourceNotFoundException("There is no HCR " + "resource with the " +
                    "specified name: " + hcrFileName);
        }

        JsonObject fileDetails = new JsonObject();





        String fileName = hcrFileName + "_" + System.currentTimeMillis();
        //JSONObject jsonFormData = JSONObject.fromObject(formDataFromHcr);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonFormData = jsonParser.parseString(hiResource.getHiResourceHCR().getPreviewFormData()).getAsJsonObject();
        //here list is converted to string for the adding purpose in jsonFormData
        JsonArray jsonArray = new JsonArray();
        for (String str : theTotalFormats) {
            JsonElement element = new JsonPrimitive(str);
            jsonArray.add(element);
        }
        jsonFormData.add("format",jsonArray);
        jsonFormData.addProperty("isExport", true);
        jsonFormData.addProperty("isMail", true);
        jsonFormData.addProperty("emailExportName", fileName);
        JsonObject saveDetails =jsonFormData.get("saveDetails").getAsJsonObject();
        saveDetails.addProperty("uuid",hcrFileName.replace(".hcr",""));
        return jsonFormData;
    }
    
}
