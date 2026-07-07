package com.helicalinsight.parallelprocessor;

import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.exceptions.TaskInterruptedException;
import com.helicalinsight.efw.exceptions.TaskTimeoutException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.api.GenericWorker;
import com.helicalinsight.parallelprocessor.api.IThreadPoolMonitorService;
import com.helicalinsight.parallelprocessor.api.TaskStatus;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Somen
 *         Created by helical021 on 1/17/2019.
 * @author Rajesh
 *         Updated by helical019 on 1/17/2019.
 *         Main task of this class is to execute the worker threads.
 *         Handle the exception.
 *         Store the reference of the worker class an abort them if rquired.
 *         Provide statistics such as number of workers executed, waiting and queued.
 *         It sould provide the number of workers.
 */
@Component
public class WorkerManager {
    private static final Logger logger = LoggerFactory.getLogger(WorkerManager.class);
    private String threadPoolPrefix;
    private JSONObject threadPoolConfigJson = JsonUtils.getThreadPoolConfig();
    private Map<String, List<GenericWorker>> serviceWorkerActiveMap = new HashMap<>();
    private Map<String, List<Future<?>>> futureObjectsMap = new HashMap<>();

    public void clearAll(){
        serviceWorkerActiveMap.clear();
        futureObjectsMap.clear();
    }
    @Autowired
    private ServiceThreadDetail serviceThreadDetail;

    private Integer maxWaitingTime;
    private Long threadPoolTimeout;
    private ThreadPoolTaskExecutorImpl executor;
    private Long taskTimeout;
    private String taskTimeUnit;
    private WorkerRejectionExecutionHandler workerRejectionExecutionHandler;
    private IThreadPoolMonitorService threadPoolMonitorService;

    @Autowired
    private TaskExecutorFactory taskExecutorFactory;
    private Thread monitorThread;


    public WorkerManager() {
        threadPoolPrefix = threadPoolConfigJson.getString("threadPoolPrefix");

        taskTimeout = threadPoolConfigJson.getLong("taskTimeout");
        taskTimeUnit = threadPoolConfigJson.getString("taskTimeoutUnit");
        maxWaitingTime = threadPoolConfigJson.getInt("maxWaitingTime");
        threadPoolTimeout = threadPoolConfigJson.getLong("threadPoolTimeout");

    }

    @PostConstruct
    private void init() {
        taskExecutorFactory.setThreadPoolConfig(threadPoolConfigJson);
        this.executor = taskExecutorFactory.getTaskExecutor();
        workerRejectionExecutionHandler = taskExecutorFactory.getWorkerRejectionExecutionHandler();
        threadPoolMonitorService = taskExecutorFactory.getIThreadPoolMonitorService();
        configure();
        monitorThread = new HIManagedThread(threadPoolMonitorService);
        monitorThread.setName("TaskMonitorService");
        monitorThread.setDaemon(true);
        monitorThread.start();

    }

    private void configure() {

        executor.setMaxPoolSize(threadPoolConfigJson.getInt("maxPoolSize"));
        executor.setCorePoolSize(threadPoolConfigJson.getInt("corePoolSize"));
        executor.setThreadNamePrefix(threadPoolPrefix);
        executor.setWaitForTasksToCompleteOnShutdown(threadPoolConfigJson.getBoolean("waitForTasksToCompleteOnShutdown"));
        executor.setQueueCapacity(threadPoolConfigJson.getInt("queueCapacity"));
        executor.setRejectedExecutionHandler(workerRejectionExecutionHandler);
        executor.setAllowCoreThreadTimeOut(threadPoolConfigJson.getBoolean("allowCoreThreadTimeOut"));
        executor.setDaemon(true);
        executor.setKeepAliveSeconds(threadPoolConfigJson.getInt("corePoolKeepAliveSeconds"));
        threadPoolMonitorService.setExecutor(executor);
        threadPoolMonitorService.setMonitoringPeriod(threadPoolConfigJson.getInt("threadPoolMonitorPeriod"));
        
        executor.initialize();
    }


    @PreDestroy
    public void destroy() {
        logger.debug("Shutting down the thread pool task executor");
        executor.shutdown();
    }


    public String getConfig() {
        return threadPoolConfigJson.toString();
    }


    private void startServiceWorkers(List<GenericWorker> workerListInMap, String id) {
    	String requestId = RequestContext.get();
    	logger.debug("RequestId : {}", requestId);
        List<Future<?>> futureList = new ArrayList<>();
        if (workerListInMap.size() == 1) {
            for (GenericWorker workerItem : workerListInMap) {
                workerItem.setId(id);
                logger.debug("ThreadPoolExecutor is executing ");
                Future<?> futureObject = executor.submit(workerItem, requestId);
                futureList.add(futureObject);
                futureObjectsMap.put(id, futureList);
                try {
                    futureObject.get(taskTimeout, TimeUnit.valueOf(taskTimeUnit));
                } catch (TimeoutException e) {
                    workerItem.setTaskStatus(TaskStatus.FAILED);
                    this.stopWorkerByServiceId(id);
                    throw new TaskTimeoutException("Exceeded the time to complete the task.");
                } catch (InterruptedException e) {
                    workerItem.setTaskStatus(TaskStatus.FAILED);
                    throw new TaskInterruptedException("Exceeded the time to complete the task.");
                } catch (ExecutionException ignore) {
                    workerItem.setTaskStatus(TaskStatus.FAILED);
                    futureObject.cancel(true);
                } catch (CancellationException ex) {
                    workerItem.setTaskStatus(TaskStatus.FAILED);
                }
            }


        } else {
            for (GenericWorker workerItem : workerListInMap) {
                workerItem.setId(id);
                logger.debug(" ThreadPoolExecutor is executing ");
                Future<?> futureObject = executor.submit(workerItem, requestId);
                futureList.add(futureObject);
                futureObjectsMap.put(id, futureList);
            }


        }
    }

    public boolean stopWorkerByServiceId(String serviceId) {
        return serviceThreadDetail.stopThreadsWithId(serviceId);
    }


    public void startWorkerByServiceId(String serviceName) {
        if (serviceWorkerActiveMap.containsKey(serviceName)) {
            startServiceWorkers(serviceWorkerActiveMap.get(serviceName), serviceName);
        }
    }

    public boolean submitWorkers(List<GenericWorker> workerList, String serviceId) {
        this.serviceWorkerActiveMap.put(serviceId, workerList);
        startWorkerByServiceId(serviceId);
        return true;
    }

    public JSONObject getAllStatus() {
        JSONObject allStatus = new JSONObject();
        allStatus.put("threadPoolConfiguration", threadPoolConfigJson);
        allStatus.put("executorPoolSize", executor.getPoolSize());
        allStatus.put("executorCorePoolSize", executor.getCorePoolSize());
        allStatus.put("executorMaxPoolSize", executor.getMaxPoolSize());
        allStatus.put("activeCount", executor.getActiveCount());
        allStatus.put("completedTaskCount", executor.getThreadPoolExecutor().getCompletedTaskCount());
        allStatus.put("taskCount", executor.getThreadPoolExecutor().getTaskCount());
        allStatus.put("isTerminated", executor.getThreadPoolExecutor().isTerminated());
        return allStatus;
    }

    public JSONObject getStatus(String serviceId) {
        JSONObject statusJson = new JSONObject();
        List<GenericWorker> genericWorkers = this.serviceWorkerActiveMap.get(serviceId);

        if (genericWorkers != null) {
            statusJson.put("size", genericWorkers.size());
            for (GenericWorker genericWorker : genericWorkers) {
                statusJson.accumulate("status", genericWorker.getStatus());
            }
        }
        return statusJson;
    }

    public void reset() {
        init();
    }


    public void stopAll() {
        executor.getThreadPoolExecutor().shutdownNow();
        logger.debug("Stopping all the threads in thread pool executor");
    }

}

