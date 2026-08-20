package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import com.helicalinsight.instant.ai.service.IInstantBIService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Proxies InstantBI chart catalog listing to the Python {@code /list-charts} service.
 */
@Service(InstantBIServiceFactory.LIST_CHARTS_SERVICE)
public class AiListChartsServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiListChartsServiceImpl.class);


    @Override
    public void execute(IInstantBIPayload payload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);
        try {
            JsonObject js = new JsonObject();
            InstantBIUtils.addSessionContext(request, js);

            String botResponse = InstantBIServiceFactory.getHttpService().callHttp("/list-charts", js);
            JsonObject chartsPayload = GsonUtility.parseString(botResponse, JsonObject.class);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", chartsPayload);

            InstantBIUtils.sendResponse(response, isAjax, mainObject);
        } catch (Exception exception) {
            logger.error("Failed to list charts", exception);
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
}
