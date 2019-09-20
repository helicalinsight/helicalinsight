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

import com.helicalinsight.efw.io.delete.DeleteOperationHandler;
import net.sf.json.JSONObject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * responsible for creating new job.call <code>Scheduler</code>
 * <code>Trigger</code>
 * </p>
 *
 * @author Prashansa
 */
public class ScheduleProcess {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleProcess.class);

    /**
     * <p>
     * In this method we map data into DataMap
     * </p>
     *
     * @param cronExpression a <code>String</code> specify cron expression
     * @param className      a <code>String</code> specify class name which perform job or
     *                       class which override execute method
     * @param jobName        a <code>String</code> specify the unique name of job
     * @param jobGroup       a <code>String</code> specify group of job
     * @param path           a <code>String</code> specify Scheduling.xml path
     * @param jsonobject     a <code>JSONObject</code> specify details contain in schedule
     *                       tag
     * @param baseUrl        a <code>String</code> specify base URL like:
     *                       http://localhost:9090/Example/test.html
     * @see ScheduleProcessCall
     */
    @SuppressWarnings("unchecked")
    public JSONObject scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JSONObject jsonobject, String baseUrl) {
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
            if (timezone.length() > 0) {
                formatter.setTimeZone(TimeZone.getTimeZone(timezone));
            }

            sDate = formatter.parse(startDate);

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
                if (timezone.length() > 0) {
                    formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                }
                eDate = formatter.parse(endDate);
                logger.debug("eDate:  " + eDate);
            } catch (ParseException ex) {
                logger.error("Exception stack trace is ", ex);
            }
        }
//check here with timezone
        //long now = System.currentTimeMillis();
        TriggerUtility tr;
        Trigger trigger1 = null;
        JSONObject jobdata = null;
        if (eDate == null || eDate.getTime() > SaveReportController.findDateAtTimeZone(timezone, new Date()).getTime()) {
            tr = new TriggerUtility();
            trigger1 = tr.getInstance(cronExpression, jobName, sDate, eDate, timezone);
            jobdata = new JSONObject();
        } else {
            logger.info("Entry not taken for schedule " + jobName);
           /* XmlOperationWithParser xmlOperationWithParser = new XmlOperationWithParser();
            xmlOperationWithParser.removeElementFromXml(path, jobName);*/
        }
        try {
            if (!sch.checkExists(JobKey.jobKey(jobGroup + "." + jobName))) {
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) className.getClass()).withIdentity(jobName,
                        jobGroup).build();
                logger.debug("sch.getJobGroupNames()" + sch.getJobGroupNames());
                JobDataMap jobDataMap = job.getJobDataMap();
                jobDataMap.put("jobinput", jobName);
                jobDataMap.put("path", path);
                jobDataMap.put("baseUrl", baseUrl);
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


    /**
     * delete() is responsible for delete job from scheduler
     *
     * @param jobKey a <code>String</code> which specify the job key which has to
     *               be delete.
     * @see DeleteOperationHandler ,EFWController, XmlOperationWithParser
     */
    public void delete(String jobKey) {
        try {
            SchedulerUtility.getInstance().deleteJob(JobKey.jobKey(jobKey, "DEFAULT"));
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
    }


    /**
     * updateTrigger method is responsible to update trigger
     *
     * @param previousCronExpression a <code>String</code> which specify the previous
     *                               cronExpression
     * @param newCronExpression      a <code>String</code> which specify the cronExpression which
     *                               has to be updated in trigger.
     * @param jobName                a <code>String</code> which specify the trigger name
     * @param sDate                  a <code>String</code> which specify the start date
     * @param eDate                  a <code>String</code> which specify the end date
     */
    @SuppressWarnings("unchecked")
    public void updateTrigger(String previousCronExpression, String newCronExpression, String jobName, Date sDate,
                              Date eDate, String timezone) {
        logger.debug("Update trigger call..");
        TriggerUtility triggerUtility = new TriggerUtility();
        // Date sDate = null;
        // Date eDate = null;
        Trigger trigger = triggerUtility.getInstance(previousCronExpression, jobName, sDate, eDate, timezone);
        @SuppressWarnings("rawtypes") TriggerBuilder triggerBuilder = trigger.getTriggerBuilder();

        try {
            Trigger newTrigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression)
                    .inTimeZone(TimeZone.getTimeZone(timezone))).build();
            logger.debug("trigger.getKey():" + trigger.getKey());
            SchedulerUtility.getInstance().rescheduleJob(trigger.getKey(), newTrigger);
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
    }
}
