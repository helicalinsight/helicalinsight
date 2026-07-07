package com.helicalinsight.instant.ai.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;

import jakarta.servlet.http.HttpServletRequest;

public class InstantBIHttpServiceImplTest {

    private final InstantBIHttpServiceImpl service = new InstantBIHttpServiceImpl();

    @After
    public void cleanup() {
        RequestRegistryFilter.cancelledRequests.clear();
        RequestContext.clear();
    }

    @Test
    public void isThreadSafeToCacheReturnsTrue() {
        assertTrue(service.isThreadSafeToCache());
    }

    @Test(expected = EfwServiceException.class)
    public void executeCancellableCallThrowsWhenRequestAlreadyCancelled() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("requestId")).thenReturn("cancelled-request");
        RequestRegistryFilter.cancelledRequests.add("cancelled-request");

        service.executeCancellableCall(request, JsonObject::new, "/interactive");
    }
}
