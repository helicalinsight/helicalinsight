package com.helicalinsight.instant.ai;

import com.helicalinsight.efw.controllerutils.StatusValidator;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.instant.ai.payload.AgentDashboardPayload;
import com.helicalinsight.instant.ai.payload.ChatContextPayload;
import com.helicalinsight.instant.ai.payload.ConvertDashboardPayload;
import com.helicalinsight.instant.ai.payload.DataInsightPayload;
import com.helicalinsight.instant.ai.payload.InteractiveChatPayload;
import com.helicalinsight.instant.ai.payload.LlmUsageAuditPayload;
import com.helicalinsight.instant.ai.payload.RecommendAnalystPayload;
import com.helicalinsight.instant.ai.payload.RecommendDomainPayload;
import com.helicalinsight.instant.ai.payload.UtilityConfigPayload;
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
        InstantBIServiceFactory.getRecommendDomainService()
                .execute(new RecommendDomainPayload(model), request, response);
    }

    @RequestMapping("/recommendation/analyst")
    public void aiRecommendAnalyst(@RequestParam("model") String model, @RequestParam("domain") String domain,
                                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getRecommendAnalystService()
                .execute(new RecommendAnalystPayload(model, domain), request, response);
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
        InstantBIServiceFactory.getInteractiveChatService()
                .execute(new InteractiveChatPayload(input, chatid, chatSeqId, subject), request, response);
    }

    @RequestMapping("/agent-dashboard")
    public void aiAgentDashboard(
            @RequestParam("input") String input,
            @RequestParam("dashboardid") String dashboardid,
            @RequestParam("dashboard_sequence_id") String dashboardSeqId,
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "mode", required = false) String mode,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getAgentDashboardService()
                .execute(new AgentDashboardPayload(input, dashboardid, dashboardSeqId, subject, mode),
                        request, response);
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
                .execute(new DataInsightPayload(chatSeqId, chatid, inputParam, formData, subjectString, "/data-insight"),
                        request, response);
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
                .execute(new DataInsightPayload(chatSeqId, chatid, inputParam, formData, subjectString, "/instant-to-hr"),
                        request, response);
    }

    @RequestMapping("/convert-dashboard")
    public void convertDashboard(
            @RequestParam(value = "chatid", required = false) String chatid,
            @RequestParam(value = "items", required = false) String items,
            @RequestParam(value = "subject", required = false) String subjectString,
            @RequestParam(value = "formData", required = false) String formData,
            @RequestParam(value = "input", required = false) String inputParam,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getConvertDashboardService()
                .execute(new ConvertDashboardPayload(chatid, items, subjectString, formData, inputParam),
                        request, response);
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

    @RequestMapping("/settings/models")
    public void settingsModels(HttpServletRequest request, HttpServletResponse response) throws IOException {
        proxyUtility("/settings/models", request, response);
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

    public String doGetSessionId(HttpServletRequest request) {
        return InstantBIUtils.extractJsessionId(request);
    }

    @RequestMapping("/chat-context")
    public void aiChatForContext(@RequestParam("input") String input, HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getChatContextService()
                .execute(new ChatContextPayload(input), request, response);
    }

    @RequestMapping("/llm-usage-audit")
    public void auditLlmUsage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        validateStatus();
        InstantBIServiceFactory.getLlmUsageAuditService().execute(new LlmUsageAuditPayload(), request, response);
    }

    private void proxyUtility(String utilityPath, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        validateStatus();
        InstantBIServiceFactory.getUtilityConfigService()
                .execute(new UtilityConfigPayload(utilityPath), request, response);
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
