package com.helicalinsight.instant.ai.service;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public final class InstantBIServiceFactory {

    public static final String HTTP_SERVICE = "instantBIHttpService";
    public static final String RECOMMEND_DOMAIN_SERVICE = "aiRecommendDomainService";
    public static final String RECOMMEND_ANALYST_SERVICE = "aiRecommendAnalystService";
    public static final String INTERACTIVE_CHAT_SERVICE = "aiInteractiveChatService";
    public static final String DATA_INSIGHT_SERVICE = "aiDataInsightService";
    public static final String LOAD_CHAT_SERVICE = "aiLoadChatService";
    public static final String CHAT_CONTEXT_SERVICE = "aiChatContextService";
    public static final String LLM_USAGE_AUDIT_SERVICE = "aiLlmUsageAuditService";
    public static final String CONVERT_CHART_SERVICE = "aiConvertChartService";
    public static final String LIST_CHARTS_SERVICE = "aiListChartsService";
    public static final String UTILITY_CONFIG_SERVICE = "aiUtilityConfigService";

    private InstantBIServiceFactory() {
    }

    public static IInstantBIHttpService getHttpService() {
        return getService(HTTP_SERVICE);
    }

    public static IInstantBIService getRecommendDomainService() {
        return getService(RECOMMEND_DOMAIN_SERVICE);
    }

    public static IInstantBIService getRecommendAnalystService() {
        return getService(RECOMMEND_ANALYST_SERVICE);
    }

    public static IInstantBIService getInteractiveChatService() {
        return getService(INTERACTIVE_CHAT_SERVICE);
    }

    public static IInstantBIService getDataInsightService() {
        return getService(DATA_INSIGHT_SERVICE);
    }

    public static IInstantBIService getLoadChatService() {
        return getService(LOAD_CHAT_SERVICE);
    }

    public static IInstantBIService getChatContextService() {
        return getService(CHAT_CONTEXT_SERVICE);
    }

    public static IInstantBIService getLlmUsageAuditService() {
        return getService(LLM_USAGE_AUDIT_SERVICE);
    }

    public static IInstantBIService getConvertChartService() {
        return getService(CONVERT_CHART_SERVICE);
    }

    public static IInstantBIService getListChartsService() {
        return getService(LIST_CHARTS_SERVICE);
    }

    public static IInstantBIService getUtilityConfigService() {
        return getService(UTILITY_CONFIG_SERVICE);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getService(String beanName) {
        return (T) ApplicationContextAccessor.getBean(beanName);
    }
}
