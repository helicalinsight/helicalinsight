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
import com.helicalinsight.instant.ai.service.IAiDataInsightService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AiDataInsightServiceImpl implements IAiDataInsightService {

    private static final Logger logger = LoggerFactory.getLogger(AiDataInsightServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String chatSeqId, String chatid, String inputParam, String formData, String subjectString,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                String input;
                JsonObject formDataJson = null;
                if (StringUtils.isNotBlank(formData)) {
                    String decodedFormData = InstantBIUtils.getEncodedElseNormal(formData);
                    formDataJson = GsonUtility.parseString(decodedFormData, JsonObject.class);
                    input = formDataJson.get("input").getAsString();
                } else if (StringUtils.isNotBlank(inputParam)) {
                    input = inputParam;
                } else {
                    throw new EfwServiceException("input or formData is required");
                }

                if (StringUtils.isNotBlank(subjectString)) {
                    String decodedSubject = InstantBIUtils.getEncodedElseNormal(subjectString);
                    JsonObject subjectJson = GsonUtility.parseString(decodedSubject, JsonObject.class);
                    JsonObject agentJson = subjectJson.get("agent").getAsJsonObject();
                    userInput.add("agent", agentJson);
                    userInput.addProperty("inputString", input);
                    userInput.addProperty("chatid", chatid);
                } else {
                    if (formDataJson == null) {
                        throw new EfwServiceException("formData is required when subject is not provided");
                    }
                    populateUserInputFromReportState(userInput, formDataJson, chatSeqId);
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

                js.add("input", userInput);
                return js;
            }, "/data-insight");
            JsonObject responseObject = InstantBIUtils.prepareDataInsightResponse(botResponse);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Data insight request aborted for requestId={}", InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }

    private void populateUserInputFromReportState(JsonObject userInput, JsonObject formDataJson, String chatSeqId) {
        HIResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        String location = formDataJson.get("location").getAsString();
        String fileName = formDataJson.get("fileName").getAsString();
        HIResource hiResource = serviceDb.getResourceByUrl(location + "/" + fileName);
        HIResourceInstantReport hiResourceInstantReport = hiResource.getHiResourceInstantReport();
        String state = hiResourceInstantReport.getState();

        JsonObject stateJson = GsonUtility.parseString(state, JsonObject.class);

        userInput.add("agent", stateJson.get("subject").getAsJsonObject().get("agent"));
        userInput.addProperty("inputString", formDataJson.get("input").getAsString());
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

        userInput.add("chat_response_item", chatResponseToSend);
        userInput.add("last_chats", InstantBIUtils.getHistory(stateJson.get("inputs").getAsJsonArray(), Integer.valueOf(chatSeqId)));
    }
}
