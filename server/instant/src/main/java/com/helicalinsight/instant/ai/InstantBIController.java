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

/**
 * InstantBI HTTP facade. Chat / insight endpoints plus Admin Settings utility
 * proxies under {@code /ai/utility/*} that forward to the Python InstantBI service.
 */
@Controller
@RequestMapping(value = "/ai", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
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

    // ------------------------------------------------------------------
    // Admin InstantBI Settings — explicit utility proxies
    // ------------------------------------------------------------------

    @RequestMapping("/utility/settings")
    public void utilitySettings(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/settings", request, response);
    }

    @RequestMapping("/utility/llm")
    public void utilityLlm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm", request, response);
    }

    @RequestMapping("/utility/llm/models")
    public void utilityLlmModels(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm/models", request, response);
    }

    @RequestMapping("/utility/llm/change-model")
    public void utilityChangeModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm/change-model", request, response);
    }

    @RequestMapping("/utility/llm/default-provider")
    public void utilityDefaultProvider(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm/default-provider", request, response);
    }

    @RequestMapping("/utility/llm/provider")
    public void utilityLlmProvider(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm/provider", request, response);
    }

    @RequestMapping("/utility/llm/config")
    public void utilityLlmConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/llm/config", request, response);
    }

    @RequestMapping("/utility/app-config")
    public void utilityAppConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/app-config", request, response);
    }

    @RequestMapping("/utility/logging")
    public void utilityLogging(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/logging", request, response);
    }

    @RequestMapping("/utility/question-config")
    public void utilityQuestionConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/utility/question-config", request, response);
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

    private void proxyUtility(String utilityPath, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        validateStatus();
        InstantBIServiceFactory.getUtilityConfigService().execute(utilityPath, request, response);
    }

    static String resolveUtilityPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri == null ? -1 : uri.indexOf("/utility");
        String utilityPath = idx >= 0 ? uri.substring(idx) : "/utility";
        if (utilityPath.length() > "/utility".length() && utilityPath.endsWith("/")) {
            utilityPath = utilityPath.substring(0, utilityPath.length() - 1);
        }
        // Drop servlet context noise after utility path (query already excluded from URI path).
        int semicolon = utilityPath.indexOf(';');
        if (semicolon >= 0) {
            utilityPath = utilityPath.substring(0, semicolon);
        }
        return StringUtils.defaultIfBlank(utilityPath, "/utility");
    }

    private void validateStatus() {
        if (this.statusValidator.isStatusNotOkay()) {
            throw new EfwServiceException("Unexpected error occurred!");
        }
    }
}
