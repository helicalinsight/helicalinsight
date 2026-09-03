package com.helicalinsight.scheduling;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String scheduleId = dataMap.getString("scheduleId");
        Map<String, Object> schedule = Map.of();
        Object result = null;
        Throwable failure = null;
        logger.info("System schedule {} started", scheduleId);
        try {
            schedule = parseScheduleJson(dataMap.getString(KEY_SCHEDULE_JSON));
            result = executeSchedule(context, schedule);
            if (result == null) {
                result = context.getResult();
            }
            if (result == null) {
                result = "System schedule " + scheduleId + " completed successfully";
            }
            context.setResult(result);
            logger.info("System schedule {} completed: {}", scheduleId, result);
        } catch (Throwable ex) {
            failure = ex;
            result = "System schedule " + scheduleId + " failed: " + ex.getMessage();
            context.setResult(result);
            logger.error("System schedule {} failed", scheduleId, ex);
        } finally {
            try {
                sendMail(schedule, result);
            } catch (Throwable mailEx) {
                logger.error("System schedule {} email notification failed", scheduleId, mailEx);
            }
        }
        if (failure != null) {
            if (failure instanceof JobExecutionException jobEx) {
                throw jobEx;
            }
            if (failure instanceof Exception exception) {
                throw new JobExecutionException(exception);
            }
            throw new JobExecutionException(new Exception(failure));
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
