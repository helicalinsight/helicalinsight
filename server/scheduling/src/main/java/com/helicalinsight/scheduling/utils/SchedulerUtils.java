package com.helicalinsight.scheduling.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.scheduling.model.JobParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * SchedulerUtils utility class of scheduler deals with Date. Created by author
 * on 3/17/2020.
 * 
 * @author Rajesh
 */
public class SchedulerUtils {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerUtils.class);

	/**
	 * convertStringIntoCalender(String date, String format) Converts the string
	 * date to calendar date.
	 * 
	 * @param date   date in string format
	 * @param format date format
	 * @return Calendar date .
	 */
	public static Calendar convertStringIntoCalender(String date, String format) {
		if (format == null) {
			format = "yyyy-MM-dd";
		}
		Calendar calendarDate = new GregorianCalendar();
		DateFormat formatter = null;
		Date convertDate = null;
		try {
			formatter = new SimpleDateFormat(format);
			convertDate = (Date) formatter.parse(date);
		} catch (ParseException ex) {
			logger.error("Exception stack trace is ", ex);
		}
		calendarDate.setTime(convertDate);
		logger.debug("Year: " + calendarDate.get(Calendar.YEAR));
		return calendarDate;
	}

	/**
	 * convertStringIntoDate(String date)
	 * 
	 * @param date date in string format
	 * @return Date {@code java.util.Date}
	 */
	public static Date convertStringIntoDate(String date) {
		return convertStringIntoCalender(date, null).getTime();
	}

	/**
	 * convertStringIntoDate(String date, String format)
	 * 
	 * @param date   date in string format
	 * @param format formate like "yyyy-MM-dd"
	 * @return {@code java.util.Date}
	 */
	public static Date convertStringIntoDate(String date, String format) {
		return convertStringIntoCalender(date, format).getTime();
	}

	/**
	 * dateToString(Date date)
	 * 
	 * @param date date
	 * @return date in string format, otherwise {@code null} if date is null.
	 */
	public static String dateToString(Date date) {
		if (date != null) {
			String pattern = "yyyy-MM-dd HH:mm:ss";
			DateFormat df = new SimpleDateFormat(pattern);
			return df.format(date);
		}
		return null;
	}

	/**
	 * prepareJsonFromUserData(String ScheduleOptions, String emailSettingsString,
	 * String reportParameters, String isActive, String reportDirectory, String
	 * reportFile, String reportName, String adhocFormData)
	 * <p>
	 * This method is responsible to create JSONObject on the basis of given
	 * parameters name and value.
	 * </p>
	 *
	 * @param ScheduleOptions     The scheduling option parameter from request
	 * @param emailSettingsString A string
	 * @param reportParameters    The report parameters request parameter
	 * @param isActive            a boolean
	 * @param reportDirectory     The directory of the report
	 * @param reportFile          The file under concern
	 * @param reportName          The name of the report
	 * @return <code>JSONObject</code> which contains schedule related data and
	 *         security related data.
	 */
	public static JsonObject prepareJsonFromUserData(String ScheduleOptions, String emailSettingsString,
			String reportParameters, String isActive, String reportDirectory, String reportFile, String reportName,
			String adhocFormData) {
		JsonObject jsonObject = new JsonObject();
		JsonObject reportParametersJson = new Gson().fromJson(reportParameters, JsonObject.class);
		if (adhocFormData != null && !adhocFormData.isEmpty()) {
			JsonObject adhocFormDataObj = JsonParser.parseString(adhocFormData).getAsJsonObject();
			reportParametersJson.add("adhocFormData", adhocFormDataObj);
		}
		if (!(reportParameters == null)) {
			jsonObject.add("reportParameters", reportParametersJson);// accumulate
		}

		if (!(ScheduleOptions == null)) {
			JsonObject ScheduleOptionsObj = JsonParser.parseString(ScheduleOptions).getAsJsonObject();
			jsonObject.add("ScheduleOptions", ScheduleOptionsObj);// accumulate
		}

		if (!(emailSettingsString == null)) {
			JsonObject emailSettingsObj = JsonParser.parseString(emailSettingsString).getAsJsonObject();
			jsonObject.add("EmailSettings", emailSettingsObj);// accumulate
		}

		if (!(isActive == null)) {
			jsonObject.addProperty("isActive", isActive);// accumulate
		}

		if (!(reportDirectory == null)) {
			jsonObject.addProperty("reportDirectory", reportDirectory);// accumulate
		}

		if (!(reportFile == null)) {
			jsonObject.addProperty("reportFile", reportFile);// accumulate
		}

		if (!(reportName == null)) {
			jsonObject.addProperty("JobName", reportName);// accumulate
		}

		jsonObject.add("security", RulesUtils.newGetSecurityJsonTemplate());// accumulate
		logger.debug("JSON Before creating xml tag:" + jsonObject);
		return jsonObject;
	}

	/**
	 * prepareJobValue(JobParameters jobParameters, Object value) The method
	 * determines the type of the value and stores it in the appropriate format
	 * within the job parameters, updating the value and type properties
	 * accordingly.
	 * 
	 * @param jobParameters job parameters to which the value will be added.
	 * @param value         value to be added to the job parameters.
	 * @return updated {@link JobParameters} object.
	 */
	public static JobParameters prepareJobValue(JobParameters jobParameters, Object value) {
		if (value instanceof JsonArray jsonArray) {
            jobParameters.setValue(jsonArray.toString());
			jobParameters.setType("Collection");
		}else if (value instanceof JsonObject jsonObject) {
            jobParameters.setValue(jsonObject.toString());
			jobParameters.setType("JsonObject");
		} else if (value instanceof Integer) {
			jobParameters.setValue(value.toString());
			jobParameters.setType("Integer");
		} else {
			String originalValue = value.toString();
			jobParameters.setValue(originalValue);
			jobParameters.setType("String");
		}

		return jobParameters;
	}

}
