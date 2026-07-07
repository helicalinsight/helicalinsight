package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.instant.ai.service.IAiLoadChatService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AiLoadChatServiceImpl implements IAiLoadChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiLoadChatServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String chatSeqId, String formData, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        boolean isAjax = ControllerUtils.isAjax(request);

        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                String decodedFormData = InstantBIUtils.getEncodedElseNormal(formData);
                JsonObject formDataJson = GsonUtility.parseString(decodedFormData, JsonObject.class);

                String input = formDataJson.get("input").getAsString();
                String location = formDataJson.get("location").getAsString();
                String fileName = formDataJson.get("fileName").getAsString();
                HIResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
                HIResource hiResource = serviceDb.getResourceByUrl(location + "/" + fileName);
                HIResourceInstantReport hiResourceInstantReport = hiResource.getHiResourceInstantReport();
                String state = hiResourceInstantReport.getState();

                JsonObject stateJson = GsonUtility.parseString(state, JsonObject.class);

                userInput.add("agent", stateJson.get("subject").getAsJsonObject().get("agent"));
                userInput.addProperty("inputString", input);
                userInput.addProperty("chatid", GsonUtility.optString(stateJson, "activeChatId"));

                JsonArray chatResponses = stateJson.get("chat_responses").getAsJsonArray();
                JsonObject chatResponseToSend = null;

                for (JsonElement element : chatResponses) {
                    JsonObject chatResponse = element.getAsJsonObject();
                    int sequenceId = chatResponse.get("chat_sequence_id").getAsInt();
                    if (sequenceId == Integer.valueOf(chatSeqId)) {
                        chatResponseToSend = chatResponse;
                        break;
                    }
                }

                userInput.addProperty("reportId", InstantBIUtils.extractJsessionId(request));

                JsonObject settingsJson = JsonUtils.newGetSettingsJson();
                String baseURL = settingsJson.get("BaseUrl").getAsString();
                baseURL = baseURL.replace("/hi.html", "");

                userInput.addProperty("baseUrl", baseURL);
                Principal userDetails = AuthenticationUtils.getUserDetails();
                User loggedInUser = userDetails.getLoggedInUser();
                InstantBIUtils.addRoleProfile(loggedInUser, js);

                InstantBIUtils.addSessionContext(request, userInput);
                userInput.addProperty("chat_seq_id", chatSeqId);
                userInput.add("chat_response_item", chatResponseToSend);
                userInput.add("last_chats", InstantBIUtils.getHistory(stateJson.get("inputs").getAsJsonArray(), Integer.valueOf(chatSeqId)));

                js.add("input", userInput);
                return js;
            }, "/load-chat");
            JsonObject responseObject = InstantBIUtils.prepareResponse(null, botResponse, null);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, isAjax, mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Load chat request aborted for requestId={}", InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, isAjax, exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, isAjax, exception);
        }
    }
}
