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

import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import net.sf.json.JSONObject;
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
 * @author Somen
 */
public class ScheduleUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdater.class);
    private static final Map<String, String> STRING_MAP = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");

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

    public void UpdateSchedule(JSONObject scheduleData) {
        String SchedulerPath = STRING_MAP.get("schedulerPath");

        String className = scheduleData.getString("className");
        String command = scheduleData.getString("command");
        String hwfFileName = scheduleData.optString("hwfFile");
        String hwfDirectory = scheduleData.optString("hwfDirectory");
        String hwfParameters = scheduleData.optString("hwfParameters");

        String isActive = scheduleData.optString("isActive");
        int maxId;

        JSONObject scheduleJson = scheduleData.getJSONObject("ScheduleOptions");
        String startDate = scheduleJson.getString("StartDate");
        String endDate = null;
        String timezone = scheduleJson.optString("timeZone");

        String startTime = scheduleJson.optString("ScheduledTime");
        String endRadio = scheduleJson.optString("endsRadio");
        String endTime = scheduleJson.optString("ScheduledEndTime");
        if (!"never".equalsIgnoreCase(endRadio) && !"After".equalsIgnoreCase(endRadio)) {
            endDate = scheduleJson.getString("EndDate");
        }

        validateTime(startDate, endDate, timezone, startTime, endTime);

        String extension = "hwf";

        JSONObject jsonObject;
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
                JSONObject scheduleXmlAsJson = processor.getJSONObject(SchedulerPath, true);
                maxId = xmlOperation.searchMaxIdInXml(scheduleXmlAsJson);

                jsonObject = prepareJsonFromUserData(scheduleJson.toString(), hwfParameters, isActive, hwfDirectory,
                        hwfFileName);
                jsonObject.accumulate("scheduleType", extension);
                xmlOperationWithParser.addNewJobInExistingXML(jsonObject, SchedulerPath, maxId + 1);

                scheduleSpecificJob(SchedulerPath, String.valueOf(maxId + 1), className);
            } else {
                jsonObject = prepareJsonFromUserData(scheduleJson.toString(), hwfParameters, isActive, hwfDirectory,
                        hwfFileName);
                jsonObject.accumulate("scheduleType", extension);

                xmlOperationWithParser.addNewJobInXML(jsonObject, SchedulerPath);

                scheduleSpecificJob(SchedulerPath, String.valueOf("1"), className);
            }
        } else if (command.equalsIgnoreCase("scheduleSpecificJob")) {
            String id = scheduleData.getString("id");
            scheduleSpecificJob(SchedulerPath, id, className);
        }


    }

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

    private JSONObject prepareJsonFromUserData(String ScheduleOptions, String hwfParameters, String isActive,
                                               String hwfDirectory, String hwfFileName) {
        JSONObject jsonObject = new JSONObject();
        if (!(hwfParameters == null)) {
            jsonObject.accumulate("hwfParameters", hwfParameters);
        }

        if (!(ScheduleOptions == null)) {
            jsonObject.accumulate("ScheduleOptions", ScheduleOptions);
        }


        if (!(isActive == null)) {
            jsonObject.accumulate("isActive", isActive);
        }

        if (!(hwfDirectory == null)) {
            jsonObject.accumulate("hwfDirectory", hwfDirectory);
        }


        if (!(hwfFileName == null)) {
            jsonObject.accumulate("hwfFile", hwfFileName);
            jsonObject.accumulate("JobName", hwfFileName);
        }

        jsonObject.accumulate("security", RulesUtils.getSecurityJsonTemplate());
        logger.debug("JSON Before creating xml tag:" + jsonObject);
        return jsonObject;
    }

    public String scheduleSpecificJob(String path, String theId, String className) {
        ISchedule scheduleClass;
        String jobName;

        XmlOperation xmlOperation = new XmlOperation();

        JSONObject jsonObject = xmlOperation.getParticularObject(path, theId);

        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        JSONObject jsonObjectScheduleOption = jsonObject.getJSONObject("ScheduleOptions");
        xmlOperation.getParticularObject(path, theId);
        String jobType = "DEFAULT";
        String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(jsonObjectScheduleOption);

        jobName = jsonObject.getString("@id");

        JSONObject reportParameter;
        try {
            reportParameter = jsonObject.getJSONObject("SchedulingJob").getJSONObject("hwfParameters");
            logger.debug("reportParameter:  " + reportParameter);
        } catch (Exception ignore) {
            logger.error("Exception stack trace is ", ignore);
        }

        logger.debug("jsonObject before sending to schedule: " + jsonObject);
        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        scheduleJob(cronExpression, scheduleClass, jobName, jobType, path, jsonObject);
        return "scheduled";
    }

    @SuppressWarnings("unchecked")
    public JSONObject scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JSONObject jsonobject) {
        logger.debug("Cron Expression is: {}", cronExpression);
        Scheduler sch = SchedulerUtility.getInstance();
        String startDate = jsonobject.getJSONObject("ScheduleOptions").getString("StartDate");
        String timezone = jsonobject.getJSONObject("ScheduleOptions").optString("timeZone");

        logger.debug("startDate:  " + startDate);
        DateFormat formatter;

        Date sDate = null;
        Date eDate = null;
        String ScheduledTime = "";
        if (jsonobject.getJSONObject("ScheduleOptions").containsKey("ScheduledTime")) {
            ScheduledTime = jsonobject.getJSONObject("ScheduleOptions").getString("ScheduledTime");
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
        if (jsonobject.getJSONObject("ScheduleOptions").getString("endsRadio").equalsIgnoreCase("on")) {
            endDate = jsonobject.getJSONObject("ScheduleOptions").getString("EndDate");
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
        JSONObject jobdata = null;
        if (eDate == null || eDate.getTime() > SaveReportController.findDateAtTimeZone(timezone,
                new Date()).getTime()) {
            tr = new TriggerUtility();
            trigger1 = tr.getInstance(cronExpression, jobName, sDate, eDate, timezone);
            jobdata = new JSONObject();
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
                jobDataMap.put("jsonobject", jsonobject);
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
