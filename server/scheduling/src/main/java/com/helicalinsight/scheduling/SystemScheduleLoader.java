package com.helicalinsight.scheduling;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/**
 * Loads system scheduler definitions from systemschedule.xml and registers Quartz jobs.
 */
public class SystemScheduleLoader {

    public static final String SYSTEM_JOB_GROUP = "SYSTEM";

    private static final String SYSTEM_SCHEDULE_XML = "systemschedule.xml";

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleLoader.class);

    public void loadAll() {
        try {
            // Read System/Admin/systemschedule.xml directly; do not resolve via setting.xml imports
            JSONObject systemScheduleJson = JsonUtils.getImportedXmlJson(SYSTEM_SCHEDULE_XML);
            JSONArray schedules = getScheduleEntries(systemScheduleJson);
            Scheduler scheduler = SchedulerUtility.getInstance();
            removeExistingSystemJobs(scheduler);
            for (int i = 0; i < schedules.size(); i++) {
                registerSchedule(scheduler, schedules.getJSONObject(i));
            }
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (Exception ex) {
            logger.error("Failed to load system schedules", ex);
        }
    }

    private static void removeExistingSystemJobs(Scheduler scheduler) throws SchedulerException {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SYSTEM_JOB_GROUP))) {
            scheduler.deleteJob(jobKey);
        }
    }

    private void registerSchedule(Scheduler scheduler, JSONObject schedule) {
        if (!isEnabled(schedule)) {
            return;
        }
        String scheduleId = schedule.optString("@id", "");
        if (StringUtils.isBlank(scheduleId)) {
            logger.warn("Skipping system schedule with missing id");
            return;
        }
        String taskClassName = resolveTaskClass(schedule);
        if (StringUtils.isBlank(taskClassName)) {
            logger.warn("Skipping system schedule {} because task class is not configured", scheduleId);
            return;
        }
        String scheduledTime = schedule.optString("scheduledTime", "00:00:00");
        String timeZone = schedule.optString("timeZone", "");
        if (StringUtils.isBlank(timeZone)) {
            timeZone = TimeZone.getDefault().getID();
        }
        try {
            Class<? extends Job> jobClass = FactoryMethodWrapper.forName(taskClassName).asSubclass(Job.class);
            String cronExpression = buildDailyCron(scheduledTime);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("scheduleId", scheduleId);
            jobDataMap.put("retentionDays", schedule.optString("retentionDays", "30"));
            jobDataMap.put("exportPath", schedule.optString("exportPath", "Audit/LLM"));
            jobDataMap.put("timeZone", timeZone);

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(scheduleId, SYSTEM_JOB_GROUP)
                    .usingJobData(jobDataMap)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(scheduleId, SYSTEM_JOB_GROUP)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                            .inTimeZone(TimeZone.getTimeZone(timeZone))
                            .withMisfireHandlingInstructionDoNothing())
                    .build();

            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Registered system schedule {} with cron {} in timezone {}", scheduleId, cronExpression, timeZone);
        } catch (Exception ex) {
            logger.error("Failed to register system schedule {}", scheduleId, ex);
        }
    }

    static String buildDailyCron(String scheduledTime) {
        String[] tokens = scheduledTime.split(":");
        int hours = tokens.length > 0 ? Integer.parseInt(tokens[0].trim()) : 0;
        int minutes = tokens.length > 1 ? Integer.parseInt(tokens[1].trim()) : 0;
        int seconds = tokens.length > 2 ? Integer.parseInt(tokens[2].trim()) : 0;
        return seconds + " " + minutes + " " + hours + " * * ?";
    }

    private static boolean isEnabled(JSONObject schedule) {
        if (!schedule.has("@enabled")) {
            return true;
        }
        return "true".equalsIgnoreCase(schedule.getString("@enabled"));
    }

    private static String resolveTaskClass(JSONObject schedule) {
        if (!schedule.has("task")) {
            return null;
        }
        Object taskNode = schedule.get("task");
        if (taskNode instanceof JSONObject taskObject) {
            if (taskObject.has("@class")) {
                return taskObject.getString("@class");
            }
            return taskObject.optString("class", null);
        }
        return null;
    }

    static JSONArray getScheduleEntries(JSONObject root) {
        if (root == null) {
            return new JSONArray();
        }
        JSONObject scheduleRoot = root;
        if (root.has("systemSchedules")) {
            scheduleRoot = root.getJSONObject("systemSchedules");
        }
        if (!scheduleRoot.has("schedule")) {
            return new JSONArray();
        }
        Object scheduleNode = scheduleRoot.get("schedule");
        if (scheduleNode instanceof JSONArray scheduleArray) {
            return scheduleArray;
        }
        JSONArray schedules = new JSONArray();
        schedules.add(scheduleNode);
        return schedules;
    }
}
