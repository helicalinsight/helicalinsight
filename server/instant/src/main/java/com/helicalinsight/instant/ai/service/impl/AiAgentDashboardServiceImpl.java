package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.payload.AgentDashboardPayload;
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
 * Proxies InstantBI {@code /agent-dashboard} with the interactive envelope, using {@code dashboardid}.
 */
@Service(InstantBIServiceFactory.AGENT_DASHBOARD_SERVICE)
public class AiAgentDashboardServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiAgentDashboardServiceImpl.class);

    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        AgentDashboardPayload payload = (AgentDashboardPayload) instantBIPayload;
        String input = payload.getInput();
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () ->
                    InstantBIUtils.buildAgentDashboardRequest(
                            request, input, payload.getDashboardid(), payload.getDashboardSeqId(),
                            payload.getSubject(), payload.getMode()),
                    "/agent-dashboard");
            JsonObject responseObject = InstantBIUtils.prepareResponse(input, botResponse, null);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Agent-dashboard request aborted for requestId={}",
                        InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }
}
