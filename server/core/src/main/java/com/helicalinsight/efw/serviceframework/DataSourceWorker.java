package com.helicalinsight.efw.serviceframework;

import com.google.gson.JsonObject;
import com.helicalinsight.parallelprocessor.api.GenericWorker;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Somen
 *         Created by helical021 on 2/14/2019.
 */
@Component
@Scope("prototype")
public class DataSourceWorker extends GenericWorker {
	
    /**
     * using gson
     * setFormData(JsonObject formData)
     */
    @Override
    public void setFormData(JsonObject formData) {
        this.formData = formData;
    }
    @Override
    public void setResultData(JsonObject resultData) {
        this.resultData=resultData;
    }

    @Override
    public void runProcess() {
        super.genericRun();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public void setPriority(Integer priority) {
        this.priority = Thread.MAX_PRIORITY;
    }

    @Override
    public String getWorkerName() {
        return "DataSource-Worker";
    }

    @Override
    public void setWorkerName(String name) {

    }

    @Override
    public void map() {

    }

    @Override
    public JsonObject reduce() {
        return this.resultData;
    }

    @Override
    public void abort() {

    }


}
