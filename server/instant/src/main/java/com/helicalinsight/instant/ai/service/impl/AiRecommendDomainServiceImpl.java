package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.service.IAiRecommendDomainService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AiRecommendDomainServiceImpl implements IAiRecommendDomainService {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String agent, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        agent = InstantBIUtils.getEncodedElseNormal(agent);
        JsonObject agentPathJson = GsonUtility.parseString(agent, JsonObject.class);
        JsonObject js = new JsonObject();
        js.add("agent", agentPathJson);
        InstantBIUtils.addSessionContext(request, js);
        String domain = InstantBIServiceFactory.getHttpService().callHttp("/suggestDomain", js);
        JsonObject responseObj = new JsonObject();
        responseObj.addProperty("domain", domain);

        try {
            InstantBIUtils.sendResponse(response, isAjax, responseObj);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
}
