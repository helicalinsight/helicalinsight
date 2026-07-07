package com.helicalinsight.parallelprocessor.api;

import com.helicalinsight.parallelprocessor.ThreadPoolTaskExecutorImpl;

/**
 * @author Rajesh
 *         Created by helical019 on 1/21/2019.
 */
public interface IThreadPoolMonitorService extends Runnable {

    public void monitorThreadPool();

    public ThreadPoolTaskExecutorImpl getExecutor();

    public void setExecutor(ThreadPoolTaskExecutorImpl executor);
    public long getMonitoringPeriod();

    public void setMonitoringPeriod(long monitoringPeriod);

}