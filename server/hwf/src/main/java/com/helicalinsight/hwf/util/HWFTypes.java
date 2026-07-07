package com.helicalinsight.hwf.util;

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
public class HWFTypes implements IComponent {
    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        JSONArray hwfSources = JsonUtils.removeAtFromKey(settingsJson.getJSONArray("HwfSources"));
        JSONObject hwfJson = new JSONObject();
        JSONArray inputArray = new JSONArray();
        JSONArray outputArray = new JSONArray();
        JSONArray executionArray = new JSONArray();
        JSONArray stepsExpressionArray = new JSONArray();
        for (int index = 0; index < hwfSources.size(); index++) {
            JSONObject eachJson = hwfSources.getJSONObject(index);
            if (eachJson.getString("clazz").contains("input")) {
                inputArray.add(eachJson.getString("type"));
            } else if (eachJson.getString("clazz").contains("output")) {
                outputArray.add(eachJson.getString("type"));
            } else {
                executionArray.add(eachJson.getString("type"));
            }
        }
        stepsExpressionArray.add("groovy");
        hwfJson.accumulate("InputProcessTypes", inputArray);
        hwfJson.accumulate("OutputProcessTypes", outputArray);
        hwfJson.accumulate("ExecutionTypes", executionArray);
        hwfJson.accumulate("StepExpressionTypes", stepsExpressionArray);

        JSONObject response = new JSONObject();
        response.put("hwfTypes", hwfJson);

        return hwfJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
