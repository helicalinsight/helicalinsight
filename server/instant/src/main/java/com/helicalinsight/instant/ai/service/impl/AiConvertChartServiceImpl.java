package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.service.IAiConvertChartService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Proxies InstantBI chart-type conversion to the Python {@code /convert-chart} service.
 */
public class AiConvertChartServiceImpl implements IAiConvertChartService {

    private static final Logger logger = LoggerFactory.getLogger(AiConvertChartServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String vfTemplate, String selectedChart, String chatId, String chatSequence,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                InstantBIUtils.addSessionContext(request, userInput);
                userInput.addProperty("vf_template", StringUtils.defaultString(vfTemplate));
                userInput.addProperty("selected_chart", StringUtils.defaultString(selectedChart));
                if (StringUtils.isNotBlank(chatId)) {
                    userInput.addProperty("chat_id", chatId);
                }
                if (StringUtils.isNotBlank(chatSequence)) {
                    userInput.addProperty("chat_sequence_id", chatSequence);
                }

                js.add("input", userInput);
                return js;
            }, "/convert-chart");

            JsonObject responseObject = InstantBIUtils.prepareResponse(selectedChart, botResponse, null);
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Convert-chart request aborted for requestId={}",
                        InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }
}
