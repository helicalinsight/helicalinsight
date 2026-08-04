package com.helicalinsight.instant.ai.service;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;

public final class InstantBIServiceFactory {

    private static final String HTTP_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.InstantBIHttpServiceImpl";
    private static final String RECOMMEND_DOMAIN_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiRecommendDomainServiceImpl";
    private static final String RECOMMEND_ANALYST_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiRecommendAnalystServiceImpl";
    private static final String INTERACTIVE_CHAT_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiInteractiveChatServiceImpl";
    private static final String DATA_INSIGHT_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiDataInsightServiceImpl";
    private static final String LOAD_CHAT_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiLoadChatServiceImpl";
    private static final String CHAT_CONTEXT_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiChatContextServiceImpl";
    private static final String LLM_USAGE_AUDIT_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiLlmUsageAuditServiceImpl";
    private static final String CONVERT_CHART_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiConvertChartServiceImpl";
    private static final String LIST_CHARTS_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiListChartsServiceImpl";
    private static final String UTILITY_CONFIG_SERVICE_IMPL =
            "com.helicalinsight.instant.ai.service.impl.AiUtilityConfigServiceImpl";

    private InstantBIServiceFactory() {
    }

    public static IInstantBIHttpService getHttpService() {
        return FactoryMethodWrapper.getTypedInstance(HTTP_SERVICE_IMPL, IInstantBIHttpService.class);
    }

    public static IAiRecommendDomainService getRecommendDomainService() {
        return FactoryMethodWrapper.getTypedInstance(RECOMMEND_DOMAIN_SERVICE_IMPL, IAiRecommendDomainService.class);
    }

    public static IAiRecommendAnalystService getRecommendAnalystService() {
        return FactoryMethodWrapper.getTypedInstance(RECOMMEND_ANALYST_SERVICE_IMPL, IAiRecommendAnalystService.class);
    }

    public static IAiInteractiveChatService getInteractiveChatService() {
        return FactoryMethodWrapper.getTypedInstance(INTERACTIVE_CHAT_SERVICE_IMPL, IAiInteractiveChatService.class);
    }

    public static IAiDataInsightService getDataInsightService() {
        return FactoryMethodWrapper.getTypedInstance(DATA_INSIGHT_SERVICE_IMPL, IAiDataInsightService.class);
    }

    public static IAiLoadChatService getLoadChatService() {
        return FactoryMethodWrapper.getTypedInstance(LOAD_CHAT_SERVICE_IMPL, IAiLoadChatService.class);
    }

    public static IAiChatContextService getChatContextService() {
        return FactoryMethodWrapper.getTypedInstance(CHAT_CONTEXT_SERVICE_IMPL, IAiChatContextService.class);
    }

    public static IAiLlmUsageAuditService getLlmUsageAuditService() {
        return FactoryMethodWrapper.getTypedInstance(LLM_USAGE_AUDIT_SERVICE_IMPL, IAiLlmUsageAuditService.class);
    }

    public static IAiConvertChartService getConvertChartService() {
        return FactoryMethodWrapper.getTypedInstance(CONVERT_CHART_SERVICE_IMPL, IAiConvertChartService.class);
    }

    public static IAiListChartsService getListChartsService() {
        return FactoryMethodWrapper.getTypedInstance(LIST_CHARTS_SERVICE_IMPL, IAiListChartsService.class);
    }

    public static IAiUtilityConfigService getUtilityConfigService() {
        return FactoryMethodWrapper.getTypedInstance(UTILITY_CONFIG_SERVICE_IMPL, IAiUtilityConfigService.class);
    }
}
