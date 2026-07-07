package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.instant.ai.service.IAiChatContextService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AiChatContextServiceImpl implements IAiChatContextService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatContextServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String input, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        JsonObject js = new JsonObject();
        js.addProperty("input", input);

        String chatOutput = InstantBIServiceFactory.getHttpService().callHttp("/chat", js);
        JsonObject outputJson = new JsonObject();
        try {
            JsonElement parsedOutput = JsonParser.parseString(chatOutput);
            if (!parsedOutput.isJsonObject()) {
                throw new IllegalStateException("Expected JSON object but got: " + chatOutput);
            }
            outputJson = parsedOutput.getAsJsonObject();
            String context = outputJson.get("context").getAsString();
            if ("metadata".equals(context)) {
                String file = outputJson.get("fileName").getAsString();
                String location = outputJson.get("location").getAsString();

                IService iService = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.services.MetadataProvider",
                        IService.class);
                JSONObject formData = new JSONObject();
                formData.put("location", location);
                formData.put("metadataFileName", file);
                formData.put("uniqueId", true);
                formData.put("provideJoins", true);
                String result = iService.doService("adhoc", "metadata", "getMetadataForEdit", formData.toString());
                JsonObject dataResult = GsonUtility.parseString(result, JsonObject.class);
                JsonObject responseJson = dataResult.get("response").getAsJsonObject();
                String output = InstantBIServiceFactory.getHttpService().callHttp("/metadataInsight", responseJson);

                outputJson.addProperty("insightResponse", output);
            } else if ("report".equals(context)) {
                String file = outputJson.get("fileName").getAsString();
                String location = outputJson.get("location").getAsString();
            }
        } catch (Exception e) {
            logger.warn("Failed to process chat context output: {}", e.getMessage());
            outputJson.addProperty("error", e.getMessage());
            outputJson.addProperty("output", chatOutput);
        }

        JsonObject responseFinal = new JsonObject();
        responseFinal.add("output", outputJson);

        try {
            InstantBIUtils.sendResponse(response, isAjax, responseFinal);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
}
