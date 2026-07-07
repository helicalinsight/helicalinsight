package com.helicalinsight.adhoc.services;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.CubePhaseDetails;
import com.helicalinsight.admin.service.PhaseDetailsService;
import com.helicalinsight.admin.utils.LimitOffsetModel;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * Provides phase details as a component.
 */
public class PhaseDetailsProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to fetch phase details.
     *
     * @param jsonFormData 	 form data provides offset, limit etc.
     * @return The JSON string representing the phase details.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        PhaseDetailsService processDetailsService = ApplicationContextAccessor.getBean(PhaseDetailsService.class);
        LimitOffsetModel limitOffsetModel = new LimitOffsetModel();
        String offset = GsonUtility.optStringValue(formData,"offset", "5");
        String limit = GsonUtility.optStringValue(formData,"limit", "5");
        limitOffsetModel.setLimit(limit);
        limitOffsetModel.setOffset(offset);
        List<CubePhaseDetails> processDetails = processDetailsService.fetchAllPhases(limitOffsetModel);
        JsonObject resultJson = new JsonObject();


        JsonArray jsonObject = new Gson().toJsonTree(processDetails).getAsJsonArray();
        jsonObject.add(new JsonObject());
        resultJson.add("phaseInfo",jsonObject);
        resultJson.addProperty("totalPage", limitOffsetModel.getTotalCount());
        return resultJson.toString();
    }

}
