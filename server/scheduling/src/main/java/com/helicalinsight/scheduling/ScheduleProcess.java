package com.helicalinsight.scheduling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.delete.DeleteOperationHandler;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * <p>
 * ScheduleProcess class
 * responsible for creating new job.call <code>Scheduler</code>
 * <code>Trigger</code>
 * </p>
 *
 * @author Prashansa Kumari
 * @version 1.1
 */
public class ScheduleProcess {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleProcess.class);

    /**
     * scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JsonObject jsonobject, String baseUrl)
     * Schedules a job using parameters.
     * @param cronExpression            for Triggering instance on the basis of cronExpression Start date and EndDate.
     * @param className					required for creating job
     * @param jobName					used as a trigger group and get job key
     * @param jobGroup					to get job key
     * @param path					    job path 
     * @param jsonobject				schedule-related data in JSON format
     * @param baseUrl					url for scheduling
     * @return empty jsonObject
     */
    @SuppressWarnings("unchecked")
    public JsonObject scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  String path, JsonObject jsonobject, String baseUrl) {
        logger.debug("Cron Expression is: {}", cronExpression);
        Scheduler sch = SchedulerUtility.getInstance();
        String startDate = jsonobject.getAsJsonObject("ScheduleOptions").get("StartDate").getAsString();
        //String timezone = jsonobject.getAsJsonObject("ScheduleOptions").optString("timeZone");
        JsonObject ScheduleOptionsObject = jsonobject.getAsJsonObject("ScheduleOptions");
        String timezone = optString(ScheduleOptionsObject,"timeZone");
        if (jsonobject.getAsJsonObject("SchedulingJob").get("@type").getAsString().equalsIgnoreCase(JsonUtils
                .getReportExtension())) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/")) + "/" + ApplicationProperties.getInstance()
                    .getAdhocReportUrl();
        }
        logger.debug("startDate:  " + startDate);
        DateFormat formatter = null;

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
        String endDate = "";
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
//check here with timezone
        //long now = System.currentTimeMillis();
        TriggerUtility tr;
        Trigger trigger1 = null;
        JsonObject jobdata = null;
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        if (eDate == null || eDate.getTime() > scheduleOperation.findDateAtTimeZone(timezone, new Date()).getTime()) {
            tr = new TriggerUtility();
            trigger1 = tr.getInstance(cronExpression, jobName, sDate, eDate, timezone);
            jobdata = new JsonObject();
        } else {
            logger.info("Entry not taken for schedule " + jobName);

        }
        try {
            if ((trigger1 != null) && !sch.checkExists(JobKey.jobKey(jobGroup + "." + jobName))) {
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) className.getClass()).withIdentity(jobName,
                        jobGroup).build();
                logger.debug("sch.getJobGroupNames()" + sch.getJobGroupNames());
                JobDataMap jobDataMap = job.getJobDataMap();
                jobDataMap.put("jobinput", jobName);
                jobDataMap.put("path", path);
                jobDataMap.put("baseUrl", baseUrl);
                jobDataMap.put("jsonobject", jsonobject.toString());
                sch.start();

                sch.scheduleJob(job, trigger1);
            } else if (sch.checkExists(JobKey.jobKey(jobGroup + "." + jobName))) {
                logger.debug("JOB EXIST");
            } else {
                logger.error("JOB is already executed or expired");
            }
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return jobdata;
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
     * scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                 Schedules schedules, String baseUrl)
     * Schedules a job using parameters.
     * @param cronExpression            for Triggering instance on the basis of cronExpression Start date and EndDate.
     * @param className					required for creating job
     * @param jobName					used as a trigger group and get job key
     * @param jobGroup					to get job key
     * @param schedules				    schedule object provides data like start date,time-zone.
     * @param baseUrl					url for scheduling
     * @return empty jsonObject
     */
	@SuppressWarnings("unchecked")
    public JsonObject scheduleJob(String cronExpression, ISchedule className, String jobName, String jobGroup,
                                  Schedules schedules, String baseUrl) {
        logger.debug("Cron Expression is: {}", cronExpression);
        Scheduler sch = SchedulerUtility.getInstance();
        Date startDate = schedules.getStartDate();
        String timezone = schedules.getTimeZone();

        if (schedules.getScheduleType().getExtension().equalsIgnoreCase(JsonUtils
                .getReportExtension())) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/")) + "/" + ApplicationProperties.getInstance()
                    .getAdhocReportUrl();
        }
        logger.debug("startDate:  " + startDate);
        DateFormat formatter = null;

        Date sDate = null;
        Date eDate = null;
        String ScheduledTime = "";
        ScheduledTime = schedules.getScheduledTime();
        if (ScheduledTime != null) {
            logger.debug("startDate:  " + startDate);
            String[] tokens = ScheduledTime.split(":");
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            startDate = DateUtils.addHours(startDate, hours);
            startDate = DateUtils.addMinutes(startDate, minutes);
            startDate = DateUtils.addSeconds(startDate, seconds);


        }

        try {
            //new SimpleDateFormat("yyyy-MM-dd k:m:s").format(startDate);
            formatter = new SimpleDateFormat("yyyy-MM-dd k:m:s");
            if (timezone != null || timezone.length() > 0) {
                formatter.setTimeZone(TimeZone.getTimeZone(timezone));
            }

            sDate = (Date) formatter.parse(formatter.format(startDate));

            logger.debug("sDate: " + sDate);
        } catch (ParseException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        Date endDate;
        //endsRadio is changed to endsOn
        String endsOn = schedules.getEndsOn();
        if (endsOn.equalsIgnoreCase("on")) {
            endDate = schedules.getEndDate();
            String[] tokens = ScheduledTime.split(":");
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            endDate = DateUtils.addHours(endDate, hours);
            endDate = DateUtils.addMinutes(endDate, minutes);
            endDate = DateUtils.addSeconds(endDate, seconds);
            logger.debug("endDate: " + endDate);

            try {
                formatter = new SimpleDateFormat("yyyy-MM-dd k:m:s");
                if (timezone != null || timezone.length() > 0) {
                    formatter.setTimeZone(TimeZone.getTimeZone(timezone));
                }
                eDate = formatter.parse(formatter.format(endDate));
                logger.debug("eDate:  " + eDate);
            } catch (ParseException ex) {
                logger.error("Exception stack trace is ", ex);
            }
        }
//check here with timezone
        //long now = System.currentTimeMillis();
        TriggerUtility tr;
        Trigger trigger1 = null;
        JsonObject jobdata = null;
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        if (eDate == null || eDate.getTime() > scheduleOperation.findDateAtTimeZone(timezone, new Date()).getTime()) {
            tr = new TriggerUtility();
            trigger1 = tr.getInstance(cronExpression, jobName, sDate, eDate, timezone);
            jobdata = new JsonObject();
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
                jobDataMap.put("baseUrl",baseUrl);
                jobDataMap.put("jsonobject", schedules);
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
	 * stopJob(Scheduler sch)
	 * Stops the triggering of jobs from scheduler by shutting down
	 * @param sch    Scheduler instance to stops/shutdown Triggering of job
	 * @return A string indicating the status, "jobStopped".
	 */
    public String stopJob(Scheduler sch) {
        try {
            sch.shutdown();
        } catch (SchedulerException e) {
            //handle error
        }
        return "jobStopped";
    }

    /**
     * delete(String jobKey)
     * method is responsible for delete job from scheduler
     *
     * @param jobKey  job key which has to be delete.
     *               
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
     * listOfExecutingJob() 
     * Retrieves a list of currently executing job from the scheduler.
     *
     * @return A list of JobExecutionContext objects representing the currently executing jobs. otherwise
     * Returns {@code null} if there's an exception while retrieving the list.
     */
    public List<JobExecutionContext> listOfExecutingJob() {
        List<JobExecutionContext> listOfJobs = null;
        try {
            listOfJobs = SchedulerUtility.getInstance().getCurrentlyExecutingJobs();
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return listOfJobs;
    }
   /**
    * listOfCurrentlyExecutingJob()
    * Retrieves information about a specific job identified by its key from the scheduler 
    * and checks if its execution is single or concurrent.
    *
    * @return Returns "ok" if the job is found and its not concurrent , execution is allowed.
    *         Returns an error message or exception if concurrent execution is done.
    */
    public String listOfCurrentlyExecutingJob() {
        JobKey key = JobKey.jobKey("1", "DEFAULT");
        try {
            logger.debug("Found job identified by: " + SchedulerUtility.getInstance().getJobDetail(key)
                    .isConcurrentExectionDisallowed());
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }

        return "ok";
    }
    /**
     * jobDetail()
     * @return job detail object based on default job key, or {@code null} if the job is not found.
     * If an exception occurs while retrieving the job detail,exception details will be logged
     */
    public JobDetail jobDetail() {
        JobDetail jobdetail = null;
        try {
            jobdetail = SchedulerUtility.getInstance().getJobDetail(JobKey.jobKey("1", "DEFAULT"));
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return jobdetail;
    }
    /**
     * pauseJob(String jobkey) 
     * @param jobkey     key for pausing execution
     * If an exception occurs while pausing job.
     * @return "pausejob" message.
     */
    public String pauseJob(String jobkey) {
        try {
            // scheduler.pauseJob(jobkey);
            SchedulerUtility.getInstance().pauseJob(JobKey.jobKey(jobkey, "DEFAULT"));
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return "pausejob";
    }
    /**
     * pauseAllJob(Scheduler scheduler) 
     * @param scheduler     Scheduler instance to Pause all triggers
     * @return "pausealljob" message.
     */
    public String pauseAllJob(Scheduler scheduler) {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return "pausealljob";
    }

    /**
     * updateTrigger(String previousCronExpression, String newCronExpression, String jobName, Date sDate,
                              Date eDate, String timezone)
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
