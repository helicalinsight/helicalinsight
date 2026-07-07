package com.helicalinsight.hwf.util;

import net.sf.json.JSONObject;

/**
 * @author Somen
 *         Created on 5/14/2016.
 */
public class ComponentUtils {

    public static JSONObject handleSuccess(Object result) {
        JSONObject output = new JSONObject();
        output.put("status", 0);
        output.put("response", result);
        return output;
    }

    public static JSONObject setExecutionStatus(Object executionState) {
        JSONObject output = new JSONObject();
        output.put("executionStatus", executionState);
        return output;
    }

}

