package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.payload.ConvertDashboardPayload;
import com.helicalinsight.instant.ai.payload.IInstantBIPayload;
import com.helicalinsight.instant.ai.service.IInstantBIService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Proxies InstantBI dashboard conversion to the Python {@code /convert-dashboard} service.
 */
@Service(InstantBIServiceFactory.CONVERT_DASHBOARD_SERVICE)
public class AiConvertDashboardServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiConvertDashboardServiceImpl.class);

    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ConvertDashboardPayload payload = (ConvertDashboardPayload) instantBIPayload;
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                InstantBIUtils.addSessionContext(request, userInput);
                if (StringUtils.isNotBlank(payload.getChatId())) {
                    userInput.addProperty("chatid", payload.getChatId());
                }
                if (StringUtils.isNotBlank(payload.getInputParam())) {
                    userInput.addProperty("inputString", payload.getInputParam());
                }
                addSubjectModel(payload.getSubjectString(), userInput);
                addItems(payload.getItems(), userInput);
                addFormDataFields(payload.getFormData(), userInput);

                js.add("input", userInput);
                return js;
            }, "/convert-dashboard");

            JsonObject responseObject = InstantBIUtils.prepareConvertDashboardResponse(botResponse);
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Convert-dashboard request aborted for requestId={}",
                        InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }

    private void addSubjectModel(String subjectString, JsonObject userInput) {
        if (StringUtils.isBlank(subjectString)) {
            return;
        }
        String decoded = InstantBIUtils.getEncodedElseNormal(subjectString);
        JsonObject subjectJson = GsonUtility.parseString(decoded, JsonObject.class);
        if (subjectJson != null && subjectJson.has("model")) {
            userInput.add("model", subjectJson.get("model"));
        } else if (subjectJson != null) {
            userInput.add("model", subjectJson);
        }
    }

    private void addItems(String items, JsonObject userInput) {
        if (StringUtils.isBlank(items)) {
            return;
        }
        String decoded = InstantBIUtils.getEncodedElseNormal(items);
        JsonArray itemsArray;
        try {
            itemsArray = GsonUtility.parseString(decoded, JsonArray.class);
        } catch (Exception exception) {
            logger.debug("convert-dashboard items were not a JSON array", exception);
            return;
        }
        if (itemsArray != null) {
            userInput.add("items", itemsArray);
        }
    }

    private void addFormDataFields(String formData, JsonObject userInput) {
        if (StringUtils.isBlank(formData)) {
            return;
        }
        String decoded = InstantBIUtils.getEncodedElseNormal(formData);
        JsonObject formDataJson = GsonUtility.parseString(decoded, JsonObject.class);
        if (formDataJson == null) {
            return;
        }
        if (formDataJson.has("model") && !userInput.has("model")) {
            userInput.add("model", formDataJson.get("model"));
        }
        copyIfPresent(formDataJson, userInput, "location");
        copyIfPresent(formDataJson, userInput, "metadata_dir");
        copyIfPresent(formDataJson, userInput, "metadata_file_name");
        copyIfPresent(formDataJson, userInput, "dialect");
        if (formDataJson.has("items") && !userInput.has("items")) {
            JsonElement itemsElement = formDataJson.get("items");
            if (itemsElement != null && itemsElement.isJsonArray()) {
                userInput.add("items", itemsElement.getAsJsonArray());
            }
        }
    }

    private void copyIfPresent(JsonObject source, JsonObject target, String key) {
        if (source.has(key) && !target.has(key)) {
            target.add(key, source.get(key));
        }
    }
}
