package com.helicalinsight.scheduling;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Template for system schedule Quartz jobs.
 * Subclasses implement the actual work; this class always attempts email delivery afterward.
 */
public abstract class SystemScheduleJob implements Job {

    public static final String KEY_SCHEDULE_JSON = "scheduleJson";

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleJob.class);

    @Override
    public final void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String scheduleId = dataMap.getString("scheduleId");
        Map<String, Object> schedule = parseScheduleJson(dataMap.getString(KEY_SCHEDULE_JSON));
        Object result = null;
        try {
            result = executeSchedule(context, schedule);
            if (result == null) {
                result = "System schedule " + scheduleId + " completed successfully";
            }
        } catch (Exception ex) {
            result = "System schedule " + scheduleId + " failed: " + ex.getMessage();
            logger.error("System schedule {} failed", scheduleId, ex);
        } finally {
            sendMail(schedule, result);
        }
    }

    /**
     * Subclass implementation (groovy script, purge class job, etc.).
     *
     * @return response to include in the notification email
     */
    protected abstract Object executeSchedule(JobExecutionContext context, Map<String, Object> schedule)
            throws Exception;

    /**
     * Sends the execution response when email is configured on the schedule node.
     */
    protected void sendMail(Map<?, ?> schedule, Object result) {
        SystemScheduleMailHelper.sendResultEmail(schedule, result);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseScheduleJson(String scheduleJson) {
        if (StringUtils.isBlank(scheduleJson)) {
            return Map.of();
        }
        Object parsed = new groovy.json.JsonSlurper().parseText(scheduleJson);
        return (Map<String, Object>) parsed;
    }
}
