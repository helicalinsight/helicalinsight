package com.helicalinsight.instant.report;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class GenerateModelFromMDTest {

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(new GenerateModelFromMD().isThreadSafeToCache());
    }

    @Test(expected = EfwServiceException.class)
    public void executeComponentThrowsWhenSessionCookieMissing() {
        GenerateModelFromMD component = new GenerateModelFromMD();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Principal principal = mock(Principal.class);
        when(principal.getUsername()).thenReturn("tester");

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            component.executeComponent("{\"dir\":\"metadata\",\"file\":\"sales.md\"}");
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test(expected = EfwServiceException.class)
    public void executeComponentThrowsWhenRequestContextMissing() {
        GenerateModelFromMD component = new GenerateModelFromMD();
        RequestContextHolder.resetRequestAttributes();

        Principal principal = mock(Principal.class);
        when(principal.getUsername()).thenReturn("tester");

        try (MockedStatic<AuthenticationUtils> auth = mockStatic(AuthenticationUtils.class)) {
            auth.when(AuthenticationUtils::getUserDetails).thenReturn(principal);
            component.executeComponent("{\"dir\":\"metadata\",\"file\":\"sales.md\"}");
        }
    }
}
