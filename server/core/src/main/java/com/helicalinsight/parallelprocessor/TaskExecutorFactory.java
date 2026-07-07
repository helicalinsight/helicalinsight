package com.helicalinsight.parallelprocessor;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.parallelprocessor.api.IThreadPoolMonitorService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Rajesh
 *         Created by helical019 on 1/17/2019.
 */

@Component
public class TaskExecutorFactory {
    private JSONObject threadPoolConfig;
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorFactory.class);

    @Autowired
    private ApplicationContextAccessor applicationContextAccessor;

    public ThreadPoolTaskExecutorImpl getTaskExecutor() {
        String taskExecutorBean = this.threadPoolConfig.getString("taskExecutorBean");
        logger.debug("The taskExecutorBean is " + taskExecutorBean);
        Object beanName = applicationContextAccessor.getBean(taskExecutorBean);
        if (beanName != null) {
            return (ThreadPoolTaskExecutorImpl) beanName;
        }
        return null;
    }

    public WorkerRejectionExecutionHandler getWorkerRejectionExecutionHandler() {
        String taskExecutorBean = this.threadPoolConfig.getString("workerRejectionExecutionHandler");
        Object beanName = ApplicationContextAccessor.getBean(taskExecutorBean);
        if (beanName != null) {
            return (WorkerRejectionExecutionHandler) beanName;
        }
        return null;
    }

    public IThreadPoolMonitorService getIThreadPoolMonitorService() {
        String taskExecutorBean = this.threadPoolConfig.getString("threadPoolMonitorService");
        Object beanName = ApplicationContextAccessor.getBean(taskExecutorBean);
        if (beanName != null) {
            return (IThreadPoolMonitorService) beanName;
        }
        return null;
    }

    public void setThreadPoolConfig(JSONObject threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }


}
