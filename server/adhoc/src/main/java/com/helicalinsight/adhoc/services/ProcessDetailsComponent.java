package com.helicalinsight.adhoc.services;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.ProcessDetails;
import com.helicalinsight.admin.service.ProcessDetailsService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Provides process details as a component. which implements {@link IComponent}
 *
 */
public class ProcessDetailsComponent implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Executes the component to fetch process details.
     *
     * @param jsonFormData 		 form data provides offset, limit.
     * @return The JSON string representing the process details.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        ProcessDetailsService processDetailsService = ApplicationContextAccessor.getBean(ProcessDetailsService.class);
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
        String offset = GsonUtility.optStringValue(formData,"offset", "5");
        String limit = GsonUtility.optStringValue(formData,"limit", "5");
        limitOffsetModel.setLimit(limit);
        limitOffsetModel.setOffset(offset);
        List<ProcessDetails> processDetails = processDetailsService.fetchAllCubes(limitOffsetModel);
        JsonObject resultJson = new JsonObject();
        JsonArray jsonObject = new Gson().toJsonTree(processDetails).getAsJsonArray();
        jsonObject.add(new JsonObject());
        resultJson.add("cubeList",jsonObject);
        resultJson.addProperty("totalPage", limitOffsetModel.getTotalCount());
        return resultJson.toString();
    }

}
