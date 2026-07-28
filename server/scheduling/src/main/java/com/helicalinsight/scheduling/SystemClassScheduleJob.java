package com.helicalinsight.scheduling;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Runs a configured Java {@link Job} class (for example LLM purge) for a system schedule.
 */
public class SystemClassScheduleJob extends SystemScheduleJob {

    public static final String KEY_TASK_CLASS = "taskClass";

    private static final Logger logger = LoggerFactory.getLogger(SystemClassScheduleJob.class);

    @Override
    protected Object executeSchedule(JobExecutionContext context, Map<String, Object> schedule) throws Exception {
        String scheduleId = context.getJobDetail().getJobDataMap().getString("scheduleId");
        String taskClassName = context.getJobDetail().getJobDataMap().getString(KEY_TASK_CLASS);
        if (StringUtils.isBlank(taskClassName)) {
            throw new IllegalStateException("taskClass is not configured for system schedule " + scheduleId);
        }

        Class<? extends Job> jobClass = FactoryMethodWrapper.forName(taskClassName).asSubclass(Job.class);
        Job job = jobClass.getDeclaredConstructor().newInstance();
        job.execute(context);

        Object result = context.getResult();
        logger.info("System schedule {} class job {} completed", scheduleId, taskClassName);
        return result;
    }
}
