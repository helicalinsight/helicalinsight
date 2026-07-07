package com.helicalinsight.efw.serviceframework;

import com.google.gson.JsonObject;

/**
 * @author Somen
 * Created by helical021 on 3/19/2019.
 */
public interface PreExecution {

    public void preProcess(JsonObject formData);

}
