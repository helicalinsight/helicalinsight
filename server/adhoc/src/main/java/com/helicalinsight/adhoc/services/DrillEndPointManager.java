package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
public class DrillEndPointManager {
    public JsonObject provideRequiredEndpoints(JsonObject endPoints, String type) {
        JsonObject resultJSON = GsonUtility.optJsonObject(endPoints,type);

        if (resultJSON == null || resultJSON.entrySet().isEmpty()) {
            throw new RequiredParameterIsNullException("Provided " + type + " value is Invalid.");
        }
        return resultJSON;
    }


}
