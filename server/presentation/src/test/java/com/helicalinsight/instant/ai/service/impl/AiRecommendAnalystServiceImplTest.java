package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.service.InstantBIServiceFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AiRecommendAnalystServiceImplTest {

  private final AiRecommendAnalystServiceImpl service = new AiRecommendAnalystServiceImpl();

  @Test
  public void isThreadSafeToCacheReturnsTrue() {
    assertTrue(service.isThreadSafeToCache());
  }

  @Test
  public void executeParsesQuestionsAndSendsResponse() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});
    when(request.getParameter("topN")).thenReturn(null);

    User loggedInUser = mock(User.class);
    when(loggedInUser.getUsername()).thenReturn("hiadmin");

    Principal principal = mock(Principal.class);
    when(principal.getUsername()).thenReturn("tester");
    when(principal.getLoggedInUser()).thenReturn(loggedInUser);

    IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
    when(httpService.callHttp(eq("/topNQuestion"), any(JsonObject.class)))
        .thenReturn("1. Revenue trend:\n2. Sales by region:");

    try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
         MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
         MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
      controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
      auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
      factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

      service.execute("{\"path\":\"/model\"}", "sales", request, response);

      controllerUtils.verify(() -> ControllerUtils.handleSuccess(
          eq(response),
          eq(true),
          eq("{\"questions\":[\"Revenue trend\",\"Sales by region\"]}")));
    }
  }

  @Test
  public void executeUsesDefaultTopNWhenParameterMissing() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JSESSIONID", "session-1")});
    when(request.getParameter("topN")).thenReturn("");

    User loggedInUser = mock(User.class);
    when(loggedInUser.getUsername()).thenReturn("hiadmin");

    Principal principal = mock(Principal.class);
    when(principal.getUsername()).thenReturn("tester");
    when(principal.getLoggedInUser()).thenReturn(loggedInUser);

    IInstantBIHttpService httpService = mock(IInstantBIHttpService.class);
    when(httpService.callHttp(eq("/topNQuestion"), any(JsonObject.class))).thenReturn("1. Question:");

    try (MockedStatic<ControllerUtils> controllerUtils = mockStatic(ControllerUtils.class);
         MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class);
         MockedStatic<InstantBIServiceFactory> factory = mockStatic(InstantBIServiceFactory.class)) {
      controllerUtils.when(() -> ControllerUtils.isAjax(request)).thenReturn(true);
      auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
      factory.when(InstantBIServiceFactory::getHttpService).thenReturn(httpService);

      service.execute("{\"path\":\"/model\"}", "sales", request, response);

      controllerUtils.verify(() -> ControllerUtils.handleSuccess(eq(response), eq(true), any(String.class)));
    }
  }
}
