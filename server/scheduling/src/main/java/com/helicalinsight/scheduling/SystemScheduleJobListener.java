package com.helicalinsight.scheduling;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs SYSTEM-group Quartz executions, including failures that occur before
 * {@link SystemScheduleJob#execute} (for example job instantiation errors).
 */
public class SystemScheduleJobListener implements JobListener {

    static final String NAME = "SystemScheduleJobListener";

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleJobListener.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        logger.info("System schedule {} about to execute", jobId(context));
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.warn("System schedule {} execution was vetoed", jobId(context));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String scheduleId = jobId(context);
        if (jobException != null) {
            logger.error("System schedule {} failed", scheduleId, jobException);
            return;
        }
        logger.info("System schedule {} finished. result={}", scheduleId, context.getResult());
    }

    private static String jobId(JobExecutionContext context) {
        if (context == null || context.getJobDetail() == null || context.getJobDetail().getKey() == null) {
            return "";
        }
        return context.getJobDetail().getKey().getName();
    }
}
