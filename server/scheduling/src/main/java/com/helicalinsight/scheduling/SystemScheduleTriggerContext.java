package com.helicalinsight.scheduling;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Date;

/**
 * Minimal {@link JobExecutionContext} used when a system schedule is triggered
 * synchronously from the admin API so failures are logged on the request thread.
 */
final class SystemScheduleTriggerContext implements JobExecutionContext {

    private final JobDetail jobDetail;
    private Object result;

    SystemScheduleTriggerContext(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    @Override
    public JobDetail getJobDetail() {
        return jobDetail;
    }

    @Override
    public JobDataMap getMergedJobDataMap() {
        return jobDetail.getJobDataMap();
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public Scheduler getScheduler() {
        return SchedulerUtility.getInstance();
    }

    @Override
    public Trigger getTrigger() {
        return null;
    }

    @Override
    public Calendar getCalendar() {
        return null;
    }

    @Override
    public boolean isRecovering() {
        return false;
    }

    @Override
    public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
        return null;
    }

    @Override
    public int getRefireCount() {
        return 0;
    }

    @Override
    public Job getJobInstance() {
        return null;
    }

    @Override
    public Date getFireTime() {
        return new Date();
    }

    @Override
    public Date getScheduledFireTime() {
        return new Date();
    }

    @Override
    public Date getPreviousFireTime() {
        return null;
    }

    @Override
    public Date getNextFireTime() {
        return null;
    }

    @Override
    public String getFireInstanceId() {
        return "trigger-now";
    }

    @Override
    public Object get(Object key) {
        return jobDetail.getJobDataMap().get(String.valueOf(key));
    }

    @Override
    public void put(Object key, Object value) {
        jobDetail.getJobDataMap().put(String.valueOf(key), value);
    }

    @Override
    public long getJobRunTime() {
        return -1;
    }
}
