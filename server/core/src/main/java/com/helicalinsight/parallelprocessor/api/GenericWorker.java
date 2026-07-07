package com.helicalinsight.parallelprocessor.api;

import static com.helicalinsight.parallelprocessor.api.TaskStatus.FAILED;
import static com.helicalinsight.parallelprocessor.api.TaskStatus.RUNNING;
import static com.helicalinsight.parallelprocessor.api.TaskStatus.SUCCESS;
import static java.lang.System.currentTimeMillis;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.parallelprocessor.WorkerManager;
import com.helicalinsight.parallelprocessor.cache.ApplicationCacheManager;

import net.sf.json.JSONObject;

/**
 * @author Somen
 *         Created by helical021 on 1/17/2019.
 */
public abstract class GenericWorker implements IWorker {
    private static final Logger logger = LoggerFactory.getLogger(GenericWorker.class);
    protected String id;
    protected Integer priority;
    protected JsonObject formData = new JsonObject();
    protected JsonObject resultData;
    private Long startTime;
    private Long endTime;
    private Integer taskStatus;
    

    
    /**
     * using gson
     * setFormData(JsonObject formData)
     * @param formData
     */
    public abstract void setFormData(JsonObject formData);
    
    public abstract void setResultData(JsonObject resultData);

    public abstract void runProcess();

    public void genericRun() {
        logger.debug("Main worker started at the time" + this.getWorkerName() + " " + new Date().toString());
        if (this.formData != null) {
        	String requestId = GsonUtility.optString(formData,"requestId");
        	if(RequestRegistryFilter.cancelledRequests.contains(requestId)) {
        		logger.debug("Request has been cancelled, interrupting thread : {} ",Thread.currentThread());
        		Thread.currentThread().interrupt();
        		return ;
        	}
            
            final JsonObject serviceDetails = this.formData.getAsJsonObject("serviceDetails");
            String type = serviceDetails.get("type").getAsString();
            String workerClass = serviceDetails.get("serviceClass").getAsString();
            String serviceType = serviceDetails.get("serviceType").getAsString();
            String service = serviceDetails.get("service").getAsString();
            this.formData.remove("serviceDetails");
            IService iService = FactoryMethodWrapper.getTypedInstance(workerClass, IService.class);
            String result;
            if (iService != null) {
                result = iService.doService(type, serviceType, service, this.formData.toString());
                this.setResultData(new Gson().fromJson(result,JsonObject.class));
                logger.debug("Main worker completed at the time " + this.getWorkerName() + " " + new Date().toString());
            } else {
                throw new EfwServiceException(String.format("The instance of the class %s could not be obtained." + "" +
                        " Check for typos.", workerClass));
            }
           
        }
    }

    public abstract String getId();

    public abstract void setId(String id);

    @Override
    public void run() {
        //ServiceThreadDetail serviceThreadDetail = ApplicationContextAccessor.getBean(ServiceThreadDetail.class);

        Thread thread = Thread.currentThread();

        //serviceThreadDetail.putInMap(this.id,thread);

        logger.debug("Run method for the Generic Worker Started");

        final long startTimeBegins = currentTimeMillis();
        logger.debug("The Start time is set to " + startTimeBegins + " " + new Date().toString());
        ApplicationCacheManager applicationCacheManager = ApplicationContextAccessor.getBean(ApplicationCacheManager.class);
        setStartTime(startTimeBegins);
        boolean error = false;
        try {
            logger.debug("The Status of the Worker is set to Running");
            this.setTaskStatus(RUNNING);
            logger.debug("The Run Process of the specific worker is triggered");

            this.runProcess();
            JsonObject reduce = this.reduce();
            if (reduce.has("status")) {
                final String status = reduce.get("status").getAsString();
                if (status.equals("0")) {
                    this.setTaskStatus(FAILED);
                    WorkerManager workerManager = ApplicationContextAccessor.getBean(WorkerManager.class);
                    workerManager.stopWorkerByServiceId(this.id);
                    logger.error("The service returned the Failed Status");
                }
            }

            logger.debug("Adding the json result to the cache Manager");
            reduce.addProperty("position", this.formData.get("position").getAsInt());
            reduce.addProperty("maxSize", this.formData.get("maxSize").getAsInt());
            reduce.addProperty("totalPage", this.formData.get("maxSize").getAsInt());
            reduce.addProperty("resultPage", this.formData.get("position").getAsInt() + 1);
            applicationCacheManager.addToCache(reduce, this.id);
        } catch (Exception e) {
            logger.error("The Worker stopped due to an exception. Marking the status as FAILED {}", e);
            error = true;
            this.setTaskStatus(FAILED);
        }


        if (!error) {
            logger.debug("The Generic Worker task completed successfully" + " " + new Date().toString());
            this.setTaskStatus(SUCCESS);
        }
        final long endTimeEnds = currentTimeMillis();
        logger.debug("The worker completed the task in " + endTimeEnds);
        logger.debug("The worker time taken is " + (endTimeEnds - startTimeBegins));
        this.setEndTime(endTimeEnds);
    }

    public JSONObject getStatus() {
        JSONObject status = new JSONObject();
        status.put("name", this.getWorkerName());
        status.put("currentStatus", this.getTaskStatus());
        status.put("startTime", this.getStartTime());
        status.put("endTime", this.getEndTime());
        return status;
    }


    public Integer getPriority() {
        if (this.priority == null) {
            return Thread.NORM_PRIORITY;
        }
        return this.priority;
    }

    public abstract void setPriority(Integer priority);

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
