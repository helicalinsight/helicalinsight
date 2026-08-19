package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import com.helicalinsight.instant.ai.payload.RecommendDomainPayload;
import com.helicalinsight.instant.ai.service.IInstantBIService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service(InstantBIServiceFactory.RECOMMEND_DOMAIN_SERVICE)
public class AiRecommendDomainServiceImpl implements IInstantBIService {


    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        RecommendDomainPayload payload = (RecommendDomainPayload) instantBIPayload;
        boolean isAjax = ControllerUtils.isAjax(request);
        String model = InstantBIUtils.getEncodedElseNormal(payload.getModel());
        JsonObject modelPathJson = GsonUtility.parseString(model, JsonObject.class);
        JsonObject js = new JsonObject();
        js.add("model", modelPathJson);
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
