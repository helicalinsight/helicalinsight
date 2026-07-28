package com.helicalinsight.instant.ai;

import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
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
    public void aiRecommend(@RequestParam("model") String model, HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getRecommendDomainService().execute(model, request, response);
    }

    @RequestMapping("/recommendation/analyst")
    public void aiRecommendAnalyst(@RequestParam("model") String model, @RequestParam("domain") String domain,
                                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getRecommendAnalystService().execute(model, domain, request, response);
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
        InstantBIServiceFactory.getDataInsightService()
                .execute(chatSeqId, chatid, inputParam, formData, subjectString, "/data-insight", request, response);
    }

    @RequestMapping("/convert-hreport")
    public void convertHreport(
            @RequestParam("chat_sequence_id") String chatSeqId,
            @RequestParam(value = "chatid", required = false) String chatid,
            @RequestParam(value = "input", required = false) String inputParam,
            @RequestParam(value = "formData", required = false) String formData,
            @RequestParam(value = "subject", required = false) String subjectString,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getDataInsightService()
                .execute(chatSeqId, chatid, inputParam, formData, subjectString, "/instant-to-hr", request, response);
    }

    @RequestMapping("/convert-chart")
    public void convertChart(
            @RequestParam(value = "vf_template", required = false) String vfTemplate,
            @RequestParam("selected_chart") String selectedChart,
            @RequestParam(value = "chat_id", required = false) String chatId,
            @RequestParam(value = "chat_sequence_id", required = false) String chatSequenceId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getConvertChartService()
                .execute(vfTemplate, selectedChart, chatId, chatSequenceId, request, response);
    }

    @RequestMapping("/list-charts")
    public void listCharts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getListChartsService().execute(request, response);
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

    @RequestMapping("/llm-usage-audit")
    public void auditLlmUsage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getLlmUsageAuditService().execute(request, response);
    }

    private void validateStatus() {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occurred!");
        }
    }
}

