package com.helicalinsight.parallelprocessor.api;

import net.sf.json.JSONObject;

import java.util.Observable;

import com.google.gson.JsonObject;

/**
 * @author Somen
 *         Created by helical021 on 1/17/2019.
 */
public interface IWorker extends Runnable  {

    void run();
    String getWorkerName();
    void setWorkerName(String name);
    void map();
    JsonObject reduce();
    void abort();

}

