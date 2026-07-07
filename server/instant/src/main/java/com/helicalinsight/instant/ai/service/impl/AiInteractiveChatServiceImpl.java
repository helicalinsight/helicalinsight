package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.instant.ai.service.IAiInteractiveChatService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AiInteractiveChatServiceImpl implements IAiInteractiveChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiInteractiveChatServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public void execute(String input, String chatid, String chatSeqId, String subject,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                if (StringUtils.isNotBlank(subject)) {
                    String decodedSubject = InstantBIUtils.getEncodedElseNormal(subject);
                    JsonObject subjectJson = GsonUtility.parseString(decodedSubject, JsonObject.class);
                    JsonObject agentJson = subjectJson.get("agent").getAsJsonObject();
                    userInput.add("agent", agentJson);
                    userInput.addProperty("reportId", InstantBIUtils.extractJsessionId(request));
                }
                JsonObject settingsJson = JsonUtils.newGetSettingsJson();
                String baseURL = settingsJson.get("BaseUrl").getAsString();
                baseURL = baseURL.replace("/hi.html", "");

                userInput.addProperty("baseUrl", baseURL);
                Principal userDetails = AuthenticationUtils.getUserDetails();
                User loggedInUser = userDetails.getLoggedInUser();
                InstantBIUtils.addRoleProfile(loggedInUser, js);

                InstantBIUtils.addSessionContext(request, userInput);
                userInput.addProperty("inputString", input);
                userInput.addProperty("chatid", chatid);
                userInput.addProperty("chat_seq_id", chatSeqId);

                js.add("input", userInput);
                return js;
            }, "/interactive");
            JsonObject responseObject = InstantBIUtils.prepareResponse(input, botResponse, null);

            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("status", 1);
            mainObject.add("response", responseObject);

            InstantBIUtils.sendResponse(response, ControllerUtils.isAjax(request), mainObject);
        } catch (EfwServiceException exception) {
            if (InstantBIUtils.isAbortException(exception)) {
                logger.info("Interactive chat request aborted for requestId={}", InstantBIUtils.resolveRequestId(request));
                return;
            }
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        } catch (Exception exception) {
            ControllerUtils.handleFailure(response, ControllerUtils.isAjax(request), exception);
        }
    }
}
