package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HILlmUsageAudit;
import com.helicalinsight.admin.service.LlmUsageAuditService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiLlmUsageAuditServiceImplTest {

    private final AiLlmUsageAuditServiceImpl service = new AiLlmUsageAuditServiceImpl();

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(service.isThreadSafeToCache());
    }

    @Test
    public void executePersistsAuditWhenPayloadIsValid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String body = "{\"userId\":42,\"endpoint\":\"/interactive\",\"userQuery\":\"show sales\","
                + "\"tokenUsage\":{\"input_tokens\":10,\"output_tokens\":5,\"total_tokens\":15},"
                + "\"requestStatus\":\"SUCCESS\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));

        LlmUsageAuditService auditService = mock(LlmUsageAuditService.class);

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserId).thenReturn("42");
            auth.when(AuthenticationUtils::getLoggedInUserOrganizationId).thenReturn(1);
            context.when(() -> ApplicationContextAccessor.getBean(LlmUsageAuditService.class))
                    .thenReturn(auditService);

            service.execute(request, response);

            verify(auditService).save(any(HILlmUsageAudit.class));
            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1}")));
        }
    }

    @Test
    public void executeSkipsPersistWhenTotalTokensAreZero() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String body = "{\"userId\":42,\"endpoint\":\"/interactive\",\"userQuery\":\"show sales\","
                + "\"tokenUsage\":{\"total_tokens\":0},\"requestStatus\":\"SUCCESS\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));

        LlmUsageAuditService auditService = mock(LlmUsageAuditService.class);

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserId).thenReturn("42");
            context.when(() -> ApplicationContextAccessor.getBean(LlmUsageAuditService.class))
                    .thenReturn(auditService);

            service.execute(request, response);

            verify(auditService, never()).save(any(HILlmUsageAudit.class));
            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":1,\"message\":\"Skipped audit with no LLM token usage.\"}")));
        }
    }

    @Test
    public void executeRejectsUserIdMismatch() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String body = "{\"userId\":99,\"endpoint\":\"/interactive\",\"userQuery\":\"show sales\","
                + "\"tokenUsage\":{\"total_tokens\":10},\"requestStatus\":\"SUCCESS\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));

        LlmUsageAuditService auditService = mock(LlmUsageAuditService.class);

        try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
             MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
             MockedStatic<ApplicationContextAccessor> context = mockStatic(ApplicationContextAccessor.class)) {
            controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
            auth.when(AuthenticationUtils::getUserId).thenReturn("42");
            context.when(() -> ApplicationContextAccessor.getBean(LlmUsageAuditService.class))
                    .thenReturn(auditService);

            service.execute(request, response);

            verify(auditService, never()).save(any(HILlmUsageAudit.class));
            controllerUtils.verify(() -> ControllerUtils.handleSuccess(
                    eq(response),
                    eq(true),
                    eq("{\"status\":0,\"message\":\"Authenticated user does not match audit payload.\"}")));
        }
    }
}
