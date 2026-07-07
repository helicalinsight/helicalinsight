package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.google.gson.JsonParser;
import com.helicalinsight.scheduling.utils.TemplateReplacer2;
import com.helicalinsight.adhoc.jreport.HCRHelper;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.SendMail;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;


import java.io.IOException;
import java.util.*;

/**
 * EnhancedHCRScheduler extends the functionality of the {@link HCRScheduler} class and provides enhancements for handling scheduled tasks.
 * This class overrides the execute method to incorporate advanced scheduling features and job execution handling.
 * Created by author on 4/2/2020.
 * @author Rajesh
 */
public class EnhancedHCRScheduler extends HCRScheduler {
	private static final Logger logger = LoggerFactory.getLogger(EnhancedHCRScheduler.class);

	/**
	 * execute(JobExecutionContext context)
	 * Executes the scheduled job, advanced scheduling features and job execution handling.
     * If the schedule storage type is database, it checks for various conditions and updates scheduling data accordingly.
     * Otherwise, it falls back to the default behavior provided by the parent class.
     * @param context 				containing information about the job execution,scheduling 
     * @throws JobExecutionException If an exception occurs during job execution
     */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
			Date fireTime = context.getFireTime();
			logger.debug("Trigger time of Job : {}, Instance of Job {}, Trigger of Job {}, " + "" + "Job Details {}",
					fireTime, context.getJobInstance(), context.getTrigger(), context.getJobDetail());
			ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
			// JSONObject newData = new JSONObject();
			Map<String, Object> mapOfFireTimes = new HashMap<>();
			JsonObject parametersJSON;
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			int jobId = dataMap.getInt("jobinput");
			String path = dataMap.getString("path");
			JsonObject json = JsonParser.parseString(scheduleOperation.prepareScheduleEntityJson(jobId).toString()).getAsJsonObject();
			String id = String.valueOf(jobId);
			Integer noOfExecution = GsonUtility.optIntValue(json, "NoOfExecutions", 0);
			if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {

				 JsonObject ScheduleOptionsObject = json.getAsJsonObject("ScheduleOptions");
				 String endAfterExecution = GsonUtility.optStringValue(ScheduleOptionsObject,"EndAfterExecutions",null);
				if (noOfExecution >= Integer.parseInt(endAfterExecution)) {
					logger.debug("Deleting job fom schedule");
					ScheduleProcess schedulerProcess = new ScheduleProcess();
					schedulerProcess.delete(id);
					return;
				}
			}

			logger.debug("json :" + json);
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
				throw new HCRException(
						"The file type is not supported. Only " + hcrExtension + " files are supported.");
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
			if (parametersJSON.has("csvdata")) {
				reportCsvParameter = parametersJSON.get("csvdata").getAsString();
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
				body = emailSettings.get("Body").isJsonNull()?null:emailSettings.get("Body").getAsString();
			}

			if ((body == null) || "".equals(body) || "[]".equals(body)) {
				body = propertiesMap.get("body");
			}

			String csvData = reportCsvParameter;

			String baseUrl = dataMap.getString("baseUrl");

			// String parameters = ControllerUtils.concatenateParameters(parametersJSON);

			// FIXED a bug for phantom js login. Getting the user name, organization based
			// on the
			// user id from the xml.
			// mock user
			String username = null;
			String organization = null;

			Map<String, String> realNames = AuthenticationUtils.getRealNames(json);
			username = realNames.get("username");
			organization = realNames.get("organization");
			String userId=realNames.get("userId");
			String downLoadManager = AuthenticationUtils.getDownloadManager();

			String loginFirstUrl=baseUrl.replace("hi.html","?j_username=downloadManager&j_password="+downLoadManager);
			String mockUrl;
			if (organization != null) {
				mockUrl = "mock/impersonate?username=" + username + ":" + organization;
			}else {
				mockUrl = "mock/impersonate?username=" + username;
			}
			String finalUrl = baseUrl.replace("hi.html", mockUrl);
			// logger.error("final url :" + finalUrl);
			String formats = emailSettings.get("Formats").getAsString();
			String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");
			// logger.error("The parameters: " + parametersJSON);
			JsonObject hcrJsonData = HCRHelper.prepareFormDataForHCRExportReport(hcrFileName, hcrDirectory, theTotalFormats);
			JsonObject formData = HCRHelper.prepareFormDataForHCRParameters(parametersJSON,hcrJsonData);
			logger.debug("formData :" + formData);
			List<String> resourceUrl = new ArrayList<>();
			resourceUrl.add(loginFirstUrl);
			resourceUrl.add(finalUrl);
			resourceUrl.add(baseUrl.replace("hi.html", "services"));
			resourceUrl.add(baseUrl.replace("hi.html", "logout"));

			String response = null;
				response = EmailUtility.makeHttpCall(formData, resourceUrl);

			if(response==null || response==""){
				logger.error("Response is null");
				return;
			}
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
			// Send mail to all the recipients with all the attachments
			Boolean success = true;

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
			try {
				mailClient.sendMessage(hostName, port, totalRecipients, from, isAuthenticated, isSSLEnabled, user,
						password, subject, body, attachments);
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

			logger.debug("The numberOfExecution:  " + noOfExecution);
			noOfExecution = noOfExecution + 1;
			mapOfFireTimes.put("NoOfExecutions", noOfExecution);

			Date nextFireTime = context.getNextFireTime();
			if (nextFireTime != null)
				mapOfFireTimes.put("NextExecutionOn", nextFireTime);
			if (fireTime != null)
				mapOfFireTimes.put("LastExecutedOn", fireTime);
			logger.debug("The newData :  " + mapOfFireTimes);
			logger.debug("Value of context.getResult():  " + context.getResult());
			if (!success) {
				mapOfFireTimes.put("LastExecutionStatus", "1");
			}

			if (!mapOfFireTimes.isEmpty())
				scheduleOperation.updateScheduleStatus(Long.valueOf(id), mapOfFireTimes, json);
		} else {
			super.execute(context);
		}

	}
	
}
