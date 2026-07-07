package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.serviceframework.IComponent;

public class UpdateDrillConfig implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject drillConfigJson = new Gson().fromJson(jsonFormData,JsonObject.class);
        String result = DrillConfigUpdateHandler.updateDrillConfig(jsonFormData);
        JsonObject response = new JsonObject();
        response.addProperty("message", result);
        return response.toString();
    }

}
