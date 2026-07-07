package com.helicalinsight.datasource;

import com.google.gson.JsonObject;

/**
 * Created by Somen on 6/2/2021.
 */
public interface DsOperation {
    public String writeDataSource(JsonObject formData, String mode);
}
