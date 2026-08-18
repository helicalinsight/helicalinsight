package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.payload.InteractiveChatPayload;
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

@Service(InstantBIServiceFactory.INTERACTIVE_CHAT_SERVICE)
public class AiInteractiveChatServiceImpl implements IInstantBIService {

    private static final Logger logger = LoggerFactory.getLogger(AiInteractiveChatServiceImpl.class);


    @Override
    public void execute(IInstantBIPayload instantBIPayload, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        InteractiveChatPayload payload = (InteractiveChatPayload) instantBIPayload;
        String input = payload.getInput();
        String chatid = payload.getChatid();
        String chatSeqId = payload.getChatSeqId();
        String subject = payload.getSubject();
        try {
            String botResponse = InstantBIServiceFactory.getHttpService().executeCancellableCall(request, () -> {
                JsonObject js = new JsonObject();
                JsonObject userInput = new JsonObject();

                if (StringUtils.isNotBlank(subject)) {
                    String decodedSubject = InstantBIUtils.getEncodedElseNormal(subject);
                    JsonObject subjectJson = GsonUtility.parseString(decodedSubject, JsonObject.class);
                    JsonObject modelJson = subjectJson.get("model").getAsJsonObject();
                    userInput.add("model", modelJson);
                    userInput.addProperty("reportId", InstantBIUtils.extractJsessionId(request));
                }
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
