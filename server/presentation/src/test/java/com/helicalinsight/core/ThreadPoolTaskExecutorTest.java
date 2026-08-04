package com.helicalinsight.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.parallelprocessor.ThreadPoolTaskExecutorImpl;

public class ThreadPoolTaskExecutorTest {

    private ThreadPoolTaskExecutorImpl executor;
    private Authentication authentication;

    @Before
    public void setup() {
        executor = spy(new ThreadPoolTaskExecutorImpl());
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.initialize();

        authentication = mock(Authentication.class);
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(ctx);
    }

    @After
    public void tearDown() {
        RequestContext.clear();
        SecurityContextHolder.clearContext();
        RequestRegistryFilter.cancelledRequests.clear();
        executor.shutdown();
    }

    @Test
    public void testSubmitRunnableWithoutRequestId() throws Exception {
        Runnable task = mock(Runnable.class);

        Future<?> future = executor.submit(task);

        future.get();

        verify(task).run();
    }

    @Test
    public void testSubmitRunnableWithRequestId() throws Exception {
        String requestId = "REQ-123";
        Runnable task = mock(Runnable.class);
        Future<?> future = executor.submit(task, requestId);
        future.get();
        verify(task).run();
    }

    @Test
    public void testSubmitCallableWithRequestId() throws Exception {
        String requestId = "REQ-456";
        Callable<String> callable = mock(Callable.class);
        when(callable.call()).thenReturn("success");
        Future<?> future = executor.submit(callable, requestId);
        Object result = future.get();
        assertEquals("success", result);
    }

    @Test(expected = EfwServiceException.class)
    public void testSubmitRunnableWithCancelledRequestId() {
        String requestId = "REQ-CANCELLED";
        Runnable task = mock(Runnable.class);
        RequestContext.set(requestId);
        RequestRegistryFilter.cancelledRequests.add(requestId);
        executor.submit(task, requestId);
    }

    @Test
    public void testCallableBusinessExceptionCausePreservedOnFutureGet() throws Exception {
        String requestId = "REQ-ERR";
        Callable<String> callable = () -> {
            throw new EfwServiceException("business failure");
        };

        Future<?> future = executor.submit(callable, requestId);

        try {
            future.get();
            fail("Expected ExecutionException wrapping the business failure");
        } catch (ExecutionException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof EfwServiceException);
            assertEquals("business failure", e.getCause().getMessage());
        }
    }

    @Test
    public void testRunnableInterruptIsNotSwallowed() throws Exception {
        String requestId = "REQ-INT";
        // Leave the interrupt flag set so the Runnable wrapper detects it after run().
        Runnable task = () -> Thread.currentThread().interrupt();

        Future<?> future = executor.submit(task, requestId);

        try {
            future.get();
            fail("Expected ExecutionException when interrupt is propagated");
        } catch (ExecutionException e) {
            assertNotNull(e.getCause());
            Throwable root = e.getCause();
            while (root.getCause() != null) {
                root = root.getCause();
            }
            assertTrue("Root cause should be InterruptedException, was: " + root,
                    root instanceof InterruptedException);
        }
    }
}
