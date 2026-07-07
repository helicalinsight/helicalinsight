package com.helicalinsight.datasource.nosql;

import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

/**
 * @author Somen
 * Created by helical021 on 11/24/2017.
 */
public abstract class NoSQLLoader {
    abstract public boolean loadToMiddleWare(JsonObject formData);
    abstract public boolean testConnection(JsonObject formData);
}
