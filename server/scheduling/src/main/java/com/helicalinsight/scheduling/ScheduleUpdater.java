package com.helicalinsight.scheduling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * ScheduleUpdater
 * This class is responsible for updating and scheduling job.
 * @author Somen
 * Created by helical021 on 5/18/2016.
 */
public class ScheduleUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdater.class);
    private static final Map<String, String> STRING_MAP = ConfigurationFileReader.getProjectPropertiesFile();
    /**
     * stringToDate(String dateString, String timezone, String time)
     * it converts string date into {@code java.util.Date}.
     * @param dateString     date in String format  
     * @param timezone       Timezone represents different locations around the world in terms of their local time. 
     * @param time           time in string format
     * @return A {@code java.util.Date} object representing the combined date and time..
     */
    public static Date stringToDate(String dateString, String timezone, String time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return formatter.parse(dateString + " " + time);
        } catch (ParseException e) {
            logger.error("Parse exception", e);
        }
        return null;
    }
    /**
     * findDateAtTimeZone(String timeZone, Date requestDate)
     * @param timeZone            timezone of particular location
     * @param requestDate		  Date object
     * @return a {@code Date} representing the time value in timezone.
     */
    public static Date findDateAtTimeZone(String timeZone, Date requestDate) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        java.util.Calendar calTZ = new GregorianCalendar(tz);
        calTZ.setTimeInMillis(requestDate.getTime());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, calTZ.get(java.util.Calendar.YEAR));
        cal.set(java.util.Calendar.MONTH, calTZ.get(java.util.Calendar.MONTH));
        cal.set(java.util.Calendar.DAY_OF_MONTH, calTZ.get(java.util.Calendar.DAY_OF_MONTH));
        cal.set(java.util.Calendar.HOUR_OF_DAY, calTZ.get(java.util.Calendar.HOUR_OF_DAY));
        cal.set(java.util.Calendar.MINUTE, calTZ.get(java.util.Calendar.MINUTE));
        cal.set(java.util.Calendar.SECOND, calTZ.get(java.util.Calendar.SECOND));
        cal.set(java.util.Calendar.MILLISECOND, calTZ.get(java.util.Calendar.MILLISECOND));
        return cal.getTime();
    }
    /**
     * UpdateSchedule(JsonObject scheduleData)
     * Updates or adds a new schedule based on the provided schedule data.
     * @param scheduleData         jsonObject containing the schedule data to be updated or added.  
     */
    public void UpdateSchedule(JsonObject scheduleData) {
        String SchedulerPath = STRING_MAP.get("schedulerPath");

        String className = scheduleData.get("className").getAsString();
        String command = scheduleData.get("command").getAsString();
        String hwfFileName = optString(scheduleData,"reportFile");
        String hwfDirectory = optString(scheduleData,"reportDirectory");
        String hwfParameters = optString(scheduleData,"reportParameters");

        String isActive = optString(scheduleData,"isActive");
        int maxId;

        JsonObject scheduleJson = scheduleData.getAsJsonObject("ScheduleOptions");
        String startDate = scheduleJson.get("StartDate").getAsString();
        String endDate = null;
        String timezone = optString(scheduleJson,"timeZone");

        String startTime = optString(scheduleJson,"ScheduledTime");
        String endRadio = optString(scheduleJson,"endsRadio");
        String endTime = optString(scheduleJson,"ScheduledEndTime");
        if (!"never".equalsIgnoreCase(endRadio) && !"After".equalsIgnoreCase(endRadio)) {
            endDate = scheduleJson.get("EndDate").getAsString();
        }

        validateTime(startDate, endDate, timezone, startTime, endTime);

        String extension = "hwf";

        JsonObject jsonObject;
        XmlOperation xmlOperation = new XmlOperation();
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (command.equalsIgnoreCase("add") || command.equals("")) {
            File file = new File(SchedulerPath);

            if (file.exists() && file.length() == 0) {
                if (file.delete()) {
                    logger.info("Found 0kb scheduling.xml file and deleted the same");
                }
            }

            if (file.exists()) {
                JsonObject scheduleXmlAsJson = processor.getJsonObject(SchedulerPath, true);
                maxId = xmlOperation.searchMaxIdInXml(scheduleXmlAsJson);

                jsonObject = prepareJsonFromUserData(scheduleJson.toString(), hwfParameters, isActive, hwfDirectory,
                        hwfFileName);
                jsonObject.addProperty("scheduleType", extension);
                xmlOperationWithParser.addNewJobInExistingXML(jsonObject, SchedulerPath, maxId + 1);

                scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), className);
            } else {
                jsonObject = prepareJsonFromUserData(scheduleJson.toString(), hwfParameters, isActive, hwfDirectory,
                        hwfFileName);
                jsonObject.addProperty("scheduleType", extension);

                xmlOperationWithParser.addNewJobInXML(jsonObject, SchedulerPath);

                scheduleSpecificJob(SchedulerPath, String.valueOf("1"), className);
            }
        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = scheduleData.get("id").getAsString();
            scheduleSpecificJob(SchedulerPath, id, className);
        }

    }

    private static String optString(JsonObject jsonObject, String key) {
    	if (jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            } 
            else if(element.isJsonNull()) {
            	return "";
            }
            else {
                return element.toString();
            }
        } else {
            return "";
        }
	}
    /**
     * updateScheduleForHcr(JsonObject scheduleData)
     * Updates the schedule related to hcr file.
     * @param scheduleData				jsonObject containing the schedule data.
     */
	public void updateScheduleForHcr(JsonObject scheduleData) {
        String SchedulerPath = STRING_MAP.get("schedulerPath");

        String className = scheduleData.get("className").getAsString();
        String command = scheduleData.get("command").getAsString();
        String hcrFileName = optString(scheduleData,"hcrFile");
        String hcrDirectory = optString(scheduleData,"hcrDirectory");
        String hcrParameters = optString(scheduleData,"hcrParameters");

        String isActive = optString(scheduleData,"isActive");
        int maxId;

        JsonObject scheduleJson = scheduleData.getAsJsonObject("ScheduleOptions");
        String startDate = scheduleJson.get("StartDate").getAsString();
        String endDate = null;
        String timezone = optString(scheduleJson,"timeZone");

        String startTime = optString(scheduleJson,"ScheduledTime");
        String endRadio = optString(scheduleJson,"endsRadio");
        String endTime = optString(scheduleJson,"ScheduledEndTime");
        if (!"never".equalsIgnoreCase(endRadio) && !"After".equalsIgnoreCase(endRadio)) {
            endDate = scheduleJson.get("EndDate").getAsString();
        }

        validateTime(startDate, endDate, timezone, startTime, endTime);

        String extension = "hcr";

        JsonObject jsonObject;
        XmlOperation xmlOperation = new XmlOperation();
        XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (command.equalsIgnoreCase("add") || command.equals("")) {
            File file = new File(SchedulerPath);

            if (file.exists() && file.length() == 0) {
                if (file.delete()) {
                    logger.info("Found 0kb scheduling.xml file and deleted the same");
                }
            }
            if (file.exists()) {
                JsonObject scheduleXmlAsJson = processor.getJsonObject(SchedulerPath, true);
                maxId = xmlOperation.searchMaxIdInXml(scheduleXmlAsJson);
                jsonObject = prepareJsonFromUserDataHcr(scheduleJson.toString(), hcrParameters, isActive, hcrDirectory,
                        hcrFileName);
                jsonObject.addProperty("scheduleType", extension);
                xmlOperationWithParser.addNewJobInExistingXML(jsonObject, SchedulerPath, maxId + 1);

                scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), className);
            } else {
                jsonObject = prepareJsonFromUserData(scheduleJson.toString(), hcrParameters, isActive, hcrDirectory,
                        hcrFileName);
                jsonObject.addProperty("scheduleType", extension);

                xmlOperationWithParser.addNewJobInXML(jsonObject, SchedulerPath);

                scheduleSpecificJob(SchedulerPath, String.valueOf("1"), className);
            }
        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = scheduleData.get("id").getAsString();
            scheduleSpecificJob(SchedulerPath, id, className);
        }
    }
	/**
	 * prepareJsonFromUserDataHcr(String ScheduleOptions, String hcrParameters, String isActive,
                                                  String hcrDirectory, String hcrFileName)
	 * Prepares a JsonObject with parameters for creating or updating a schedule, including HCR related details.
	 * @param ScheduleOptions   contains scheduled time, timezone.
	 * @param hcrParameters     HCR parameters.
	 * @param isActive          user is active or not.
	 * @param hcrDirectory      HCR directory.
	 * @param hcrFileName       HCR filename.
	 * @return JsonObject containing the parameters for Hcr report.
	 */
    private JsonObject prepareJsonFromUserDataHcr(String ScheduleOptions, String hcrParameters, String isActive,
                                                  String hcrDirectory, String hcrFileName) {
        JsonObject jsonObject = new JsonObject();
        if (!(hcrParameters == null)) {
            jsonObject.addProperty("hcrParameters", hcrParameters);
        }

        if (!(ScheduleOptions == null)) {
            jsonObject.addProperty("ScheduleOptions", ScheduleOptions);
        }


        if (!(isActive == null)) {
            jsonObject.addProperty("isActive", isActive);
        }

        if (!(hcrDirectory == null)) {
            jsonObject.addProperty("hcrDirectory", hcrDirectory);
        }


        if (!(hcrFileName == null)) {
            jsonObject.addProperty("hcrFile", hcrFileName);
            jsonObject.addProperty("JobName", hcrFileName);
        }

        jsonObject.add("security", RulesUtils.newGetSecurityJsonTemplate());
        logger.debug("JSON Before creating xml tag:" + jsonObject);
        return jsonObject;
    }
    /**
     * validateTime(String startDate, String endDate, String timeZone, String startTime, String endTime)
     * Validates the provided time parameters including start date, end date, timezone, start time, and end time.
     * @param startDate           date in string format.
     * @param endDate			  date in string format.
     * @param timeZone			  timezone of location.
     * @param startTime			  start time in string format
     * @param endTime			  end time in string format
     * @return {@code true} validation is successful.
     * @throws FormValidationException If there are issues with the provided time parameters.
     */
    public boolean validateTime(String startDate, String endDate, String timeZone, String startTime, String endTime) {
        Date actualStartDate = stringToDate(startDate, timeZone, startTime);
        Date currentDateInTimeZone = findDateAtTimeZone(timeZone, new Date());
        Date requestedStartDate = findDateAtTimeZone(timeZone, actualStartDate);

        logger.info("Current time in " + timeZone + " IS " + currentDateInTimeZone);
        logger.info("Requested startDate is " + requestedStartDate);

        if (requestedStartDate.getTime() < currentDateInTimeZone.getTime()) {
            throw new FormValidationException("The start date is a past date");
        }

        if (endDate != null) {
            Date actualEndDate = stringToDate(endDate, timeZone, endTime);
            Date requestedEndDate = findDateAtTimeZone(timeZone, actualEndDate);
            logger.info("Requested EndDate is " + requestedEndDate);

            if (requestedEndDate.getTime() < currentDateInTimeZone.getTime()) {
                throw new FormValidationException("The end date is a past date");
            }
            if (requestedEndDate.getTime() < requestedStartDate.getTime()) {
                throw new FormValidationException("The end date is a less than start date");
            }
        }
        return true;
    }
    /**
     * prepareJsonFromUserData(String ScheduleOptions, String hwfParameters, String isActive,
                                               String hwfDirectory, String hwfFileName)
     * @param ScheduleOptions				   contains scheduled time, timezone.
     * @param hwfParameters					   hwf parametrs
     * @param isActive						   state user is active or not
     * @param hwfDirectory                     directory of hef file
     * @param hwfFileName                      name of file
     * @return JsonObject containing the parameters for Hwf report.
     */
    private JsonObject prepareJsonFromUserData(String ScheduleOptions, String hwfParameters, String isActive,
                                               String hwfDirectory, String hwfFileName) {
        JsonObject jsonObject = new JsonObject();
        if (!(hwfParameters == null)) {
            jsonObject.addProperty("reportParameters", hwfParameters);
        }

        if (!(ScheduleOptions == null)) {
            jsonObject.addProperty("ScheduleOptions", ScheduleOptions);
        }


        if (!(isActive == null)) {
            jsonObject.addProperty("isActive", isActive);
        }

        if (!(hwfDirectory == null)) {
            jsonObject.addProperty("reportDirectory", hwfDirectory);
        }


        if (!(hwfFileName == null)) {
            jsonObject.addProperty("reportFile", hwfFileName);
            jsonObject.addProperty("JobName", hwfFileName);
        }

        jsonObject.add("security", RulesUtils.newGetSecurityJsonTemplate());
        logger.debug("JSON Before creating xml tag:" + jsonObject);
        return jsonObject;
    }
    /**
     * scheduleSpecificJob(String path, String theId, String className)
     * Method schedule a specific job using the provided path, schedule ID
     * @param path            path for getting schedule.xml data
     * @param theId			  schedule id
     * @param className       class instance of ISchedule
     * @return message " scheduled " successful scheduling job.
     */
    public String scheduleSpecificJob(String path, String theId, String className) {
        ISchedule scheduleClass;
        String jobName;

        XmlOperation xmlOperation = new XmlOperation();

        JsonObject jsonObject = xmlOperation.getParticularObject(path, theId);

        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        JsonObject jsonObjectScheduleOption = jsonObject.getAsJsonObject("ScheduleOptions");
        xmlOperation.getParticularObject(path, theId);
        String jobType = "DEFAULT";
        String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(jsonObjectScheduleOption);

        jobName = jsonObject.get("@id").getAsString();

        JsonObject reportParameter;
        try {
            reportParameter = jsonObject.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters");
            logger.debug("reportParameter:  " + reportParameter);
        } catch (Exception ignore) {
            logger.error("Exception stack trace is ", ignore);
        }

        logger.debug("jsonObject before sending to schedule: " + jsonObject);
        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        scheduleJob(cronExpression, scheduleClass, jobName, jobType, path, jsonObject);
        return "scheduled";
    }
    /**
     * scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JsonObject jsonobject)
     * Schedules a job using the using provided data.
     *
     * @param cronExpression   Cron expression specifying the job execution schedule.
     * @param className        Instance of the ISchedule class 
     * @param jobName          Name of the job.
     * @param jobGroup         Group of the job.
     * @param path             Path related to the job.
     * @param jsonobject       JSON object containing schedule timing.
     * @return new JsonObject.
     */
    @SuppressWarnings("unchecked")
    public JsonObject scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JsonObject jsonobject) {
        logger.debug("Cron Expression is: {}", cronExpression);
        Scheduler sch = SchedulerUtility.getInstance();
        String startDate = jsonobject.getAsJsonObject("ScheduleOptions").get("StartDate").getAsString();
        JsonObject ScheduleOptionsObject = jsonobject.getAsJsonObject("ScheduleOptions");
        String timezone = optString(ScheduleOptionsObject,"timeZone");
        logger.debug("startDate:  " + startDate);
        DateFormat formatter;

        Date sDate = null;
        Date eDate = null;
        String ScheduledTime = "";
        if (jsonobject.getAsJsonObject("ScheduleOptions").has("ScheduledTime")) {
            ScheduledTime = jsonobject.getAsJsonObject("ScheduleOptions").get("ScheduledTime").getAsString();
            logger.debug("startDate:  " + startDate);
            startDate = startDate + " " + ScheduledTime;
        }

        try {
            formatter = new SimpleDateFormat("yyyy-MM-dd k:m:s");
            if (timezone != null || timezone.length() > 0) {
                formatter.setTimeZone(TimeZone.getTimeZone(timezone));
            }

            sDate = (Date) formatter.parse(startDate);

            logger.debug("sDate: " + sDate);
        } catch (ParseException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        String endDate;
        if (jsonobject.getAsJsonObject("ScheduleOptions").get("endsRadio").getAsString().equalsIgnoreCase("on")) {
            endDate = jsonobject.getAsJsonObject("ScheduleOptions").get("EndDate").getAsString();
            endDate = endDate + " " + ScheduledTime;
            logger.debug("endDate: " + endDate);

            try {
                formatter = new SimpleDateFormat("yyyy-MM-dd k:m:s");
                if (timezone != null || timezone.length() > 0) {
                    formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                }
                eDate = formatter.parse(endDate);
                logger.debug("eDate:  " + eDate);
            } catch (ParseException ex) {
                logger.error("Exception stack trace is ", ex);
            }
        }
        TriggerUtility tr;
        Trigger trigger1 = null;
        JsonObject jobdata = null;
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        if (eDate == null || eDate.getTime() > scheduleOperation.findDateAtTimeZone(timezone,
                new Date()).getTime()) {
            tr = new TriggerUtility();
            trigger1 = tr.getInstance(cronExpression, jobName, sDate, eDate, timezone);
            jobdata = new JsonObject();
        } else {
            logger.info("Entry not taken for schedule " + jobName);
        }
        try {
            if (!sch.checkExists(JobKey.jobKey(jobGroup + "." + jobName))) {
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) className.getClass()).withIdentity(jobName,
                        jobGroup).build();
                logger.debug("sch.getJobGroupNames()" + sch.getJobGroupNames());
                JobDataMap jobDataMap = job.getJobDataMap();
                jobDataMap.put("jobinput", jobName);
                jobDataMap.put("path", path);
                jobDataMap.put("jsonobject", jsonobject.toString());
                sch.start();

                sch.scheduleJob(job, trigger1);
            } else if (sch.checkExists(JobKey.jobKey(jobGroup + "." + jobName))) {
                logger.debug("JOB EXIST");
            } else {
                logger.error("JOB is already executing");
            }
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return jobdata;
    }
}
