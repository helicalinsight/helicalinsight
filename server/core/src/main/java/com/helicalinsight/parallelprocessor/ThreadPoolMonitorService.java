package com.helicalinsight.parallelprocessor;

import com.helicalinsight.parallelprocessor.api.IThreadPoolMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 *         Created by helical019 on 1/21/2019.
 */
@Component
public class ThreadPoolMonitorService implements IThreadPoolMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitorService.class);
    private ThreadPoolTaskExecutorImpl executor;
    private long monitoringPeriod = 60;

    public void run() {
        try {
            while (true) {
                monitorThreadPool();
                Thread.sleep(monitoringPeriod * 1000);
            }
        } catch (Exception e) {
            logger.error("Exception occurred",e);
        }
    }

    public void monitorThreadPool() {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("ActiveTaskCount : ").append(executor.getActiveCount());
        strBuff.append(" - CompletedTaskCount : ").append(executor.getThreadPoolExecutor().getCompletedTaskCount());
        strBuff.append(" - TotalTaskCount : ").append(executor.getThreadPoolExecutor().getTaskCount());
        strBuff.append(" - CurrentPoolSize : ").append(executor.getPoolSize());
        strBuff.append(" - CorePoolSize : ").append(executor.getCorePoolSize());
        strBuff.append(" - MaximumPoolSize : ").append(executor.getMaxPoolSize());
        strBuff.append(" - Queued Task(s)  : ").append(executor.getQueueSize());
        strBuff.append(" - Queued Capacity  : ").append(executor.getQueueCapacity());
        strBuff.append(" - isTerminated : ").append(executor.getThreadPoolExecutor().isTerminated());
        

        logger.debug(strBuff.toString());
    }

    public ThreadPoolTaskExecutorImpl getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolTaskExecutorImpl executor) {
        this.executor = executor;
    }

    public long getMonitoringPeriod() {
        return monitoringPeriod;
    }

    public void setMonitoringPeriod(long monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod;
    }
}
