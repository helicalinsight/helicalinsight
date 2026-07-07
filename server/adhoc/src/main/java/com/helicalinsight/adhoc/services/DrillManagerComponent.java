package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
public class DrillManagerComponent implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject jsonFormDataJSON = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String result = DrillManager.manageDrill(jsonFormDataJSON);

        JsonObject response = new JsonObject();
        response.addProperty("message", result);

        return response.toString();

    }

}
