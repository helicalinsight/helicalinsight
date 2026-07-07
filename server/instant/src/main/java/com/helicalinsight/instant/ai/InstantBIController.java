package com.helicalinsight.instant.ai;

import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping(value = "/ai", method = {RequestMethod.GET, RequestMethod.POST})
public class InstantBIController {

    @Autowired
    private StatusValidator statusValidator;

    @RequestMapping("/recommendation/domain")
    public void aiRecommend(@RequestParam("agent") String agent, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getRecommendDomainService().execute(agent, request, response);
    }

    @RequestMapping("/recommendation/analyst")
    public void aiRecommendAnalyst(@RequestParam("agent") String agent, @RequestParam("domain") String domain,
                                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getRecommendAnalystService().execute(agent, domain, request, response);
    }

    @RequestMapping("/interactive-chat")
    public void aiChatInteractive(
            @RequestParam("input") String input,
            @RequestParam("chatid") String chatid,
            @RequestParam("chat_sequence_id") String chatSeqId,
            @RequestParam(value = "subject", required = false) String subject,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getInteractiveChatService().execute(input, chatid, chatSeqId, subject, request, response);
    }

    @RequestMapping("/data-insight")
    public void provideDataInsight(
            @RequestParam("chat_sequence_id") String chatSeqId,
            @RequestParam(value = "chatid", required = false) String chatid,
            @RequestParam(value = "input", required = false) String inputParam,
            @RequestParam(value = "formData", required = false) String formData,
            @RequestParam(value = "subject", required = false) String subjectString,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getDataInsightService().execute(chatSeqId, chatid, inputParam, formData, subjectString, request, response);
    }

    @RequestMapping("/load-chat")
    public void loadPastChat(
            @RequestParam("chat_sequence_id") String chatSeqId,
            @RequestParam(value = "formData", required = true) String formData,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getLoadChatService().execute(chatSeqId, formData, request, response);
    }

    public String doGetSessionId(HttpServletRequest request) {
        return InstantBIUtils.extractJsessionId(request);
    }

    @RequestMapping("/chat-context")
    public void aiChatForContext(@RequestParam("input") String input, HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getChatContextService().execute(input, request, response);
    }

    private void validateStatus() {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occurred!");
        }
    }
}
