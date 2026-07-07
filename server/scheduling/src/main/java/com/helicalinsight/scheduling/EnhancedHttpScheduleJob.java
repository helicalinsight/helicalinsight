package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.scheduling.utils.ScheduleOperation;



import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * EnhancedHttpScheduleJob class extends the functionality of the {@link HttpScheduleJob} class.
 * executes scheduled HTTP requests and handling related tasks includes utility methods for handling 
 * objects, extracting data, and updating schedule status.
 * Created by author on 4/2/2020.
 * @author Rajesh
 */
public class EnhancedHttpScheduleJob extends HttpScheduleJob {
	private static final Logger logger = LoggerFactory.getLogger(EnhancedHttpScheduleJob.class);
	/**
	 * execute(JobExecutionContext context)
	 * This method handles the scheduling logic and processing of HTTP requests for jobs.
     * It retrieves job parameters, prepares scheduling data, and executes the HTTP request.
     * If the schedule storage type is database, it checks for various conditions and updates scheduling data accordingly.
     * Otherwise, it falls back to the default behavior provided by the parent class.
	 * @param context               provides information about scheduling . 
	 */
	@Override
	public void execute(JobExecutionContext context) {
		if (JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
			ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			int jobId = dataMap.getInt("jobinput");
			JsonObject json = JsonParser.parseString(scheduleOperation.prepareScheduleEntityJson(jobId).toString()).getAsJsonObject();
			
			Integer noOfExecution = GsonUtility.optIntValue(json, "NoOfExecutions", 0);
			if (json.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("After")) {
				JsonObject ScheduleOptionsObject = json.getAsJsonObject("ScheduleOptions");
				String endAfterExecution = GsonUtility.optStringValue(ScheduleOptionsObject,"EndAfterExecutions",null);
				if (noOfExecution >= Integer.parseInt(endAfterExecution)) {
					logger.debug("Deleting job fom schedule");
					ScheduleProcess schedulerProcess = new ScheduleProcess();
					schedulerProcess.delete(String.valueOf(jobId));
					return;
				}
			}

			final JsonObject schedulingJob = json.getAsJsonObject("SchedulingJob");
			String reportEfwFile = schedulingJob.get("reportFile").getAsString();
			String reportDirectory = schedulingJob.get("reportDirectory").getAsString();

			JsonObject parametersJSON = schedulingJob.getAsJsonObject("reportParameters");
			JsonObject printOptions = GsonUtility.optJsonObject(parametersJSON,"printOptions");
			if (printOptions == null) {
				printOptions = new JsonObject();
			}

			ExportUtils.setPrintOptionsAndDiscardFromReportParameters(parametersJSON.toString(), printOptions);

			parametersJSON.remove("printOptions");
			String reportCsvParameter = null;

			/*
			 * check reportParameter contains csv data or not
			 */

			String path = dataMap.getString("path");

			if (parametersJSON.has("csvdata")) {
				reportCsvParameter = parametersJSON.get("csvdata").getAsString();
			}
			JsonObject emailSettings = schedulingJob.getAsJsonObject("EmailSettings");
			String recipients = emailSettings.get("Recipients").getAsString();

			String[] totalRecipients = recipients.substring(1, recipients.length() - 1).replace("\"", "").split(",");

			String subject = getSubjectLine(json, emailSettings);

			// Read the properties file in the EFW/System/Mail directory
			PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
			Map<String, String> propertiesMap = propertiesFileReader.read("Mail", "mailConfiguration" + ".properties");
			Assert.notNull(propertiesMap, "The mailConfiguration.properties map is null!!");

			String body = getBodyLine(emailSettings, propertiesMap);

			String csvData = reportCsvParameter;

			String baseUrl = dataMap.getString("baseUrl");
			Map<String, String> realNames = AuthenticationUtils.getRealNames(json);

			// FIXED a bug for phantom js login. Getting the user name, organization based
			// on the
			// user id from the xml.

			String parameters = ControllerUtils.concatenateParameters(parametersJSON);
			/* Fixed a bug - index out of bounds */
			if (parameters.length() > 0) {
				parameters = parameters.substring(0, parameters.length() - 1);
			}

			String reportUrl = getReportUrl(reportEfwFile, reportDirectory, baseUrl, parameters, realNames);

			String encodedUrl = "";
			try {
				encodedUrl = URLEncoder.encode(reportUrl, ApplicationUtilities.getEncoding());
			} catch (UnsupportedEncodingException ignore) {

			}
			String reportName = ReportsUtility.getReportName(json.get("JobName".trim()).getAsString());
			String reportSourceType = "url";
			String formats = emailSettings.get("Formats").getAsString();
			String[] theTotalFormats = formats.substring(1, formats.length() - 1).replace("\"", "").split(",");

			sessionStart(realNames, printOptions, csvData, encodedUrl, reportName, reportSourceType, theTotalFormats,
					parametersJSON, totalRecipients, subject, propertiesMap, body);

			updateDatabase(context, json, jobId);

		} else {
			super.execute(context);
		}
	}
	
	 
	
	/**
	 * updateDatabase(JobExecutionContext context, JsonObject json, int jobId)
	 * method updates the database with execution information for the scheduled job.
	 * @param context    		context provides timing to trigger schedule process
	 * @param json              Object containing the schedule configuration data.
	 * @param jobId				unique identifier for the job
	 */
	private void updateDatabase(JobExecutionContext context, JsonObject json, int jobId) {
		String id = String.valueOf(jobId);
		// JSONObject newData = new JSONObject();
		Map<String, Object> mapOfFireTimes = new HashMap<>();
		// if
		// (json.getJSONObject("ScheduleOptions").getString("endsRadio").equalsIgnoreCase("After"))
		// {
		Integer noOfExecution = GsonUtility.optIntValue(json,"NoOfExecutions", 0);

		noOfExecution = noOfExecution + 1;
		mapOfFireTimes.put("NoOfExecutions", noOfExecution);
		// }

		Date nextFireTime = context.getNextFireTime();
		if (nextFireTime != null)
			mapOfFireTimes.put("NextExecutionOn", nextFireTime);
		Date fireTime = context.getFireTime();
		if (fireTime != null)
			mapOfFireTimes.put("LastExecutedOn", fireTime);

		if (!mapOfFireTimes.isEmpty()) {
			ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
			scheduleOperation.updateScheduleStatus(Long.valueOf(id), mapOfFireTimes, json);
		}
	}

}
