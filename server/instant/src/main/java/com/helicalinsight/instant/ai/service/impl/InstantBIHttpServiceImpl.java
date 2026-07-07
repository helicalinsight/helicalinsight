package com.helicalinsight.instant.ai.service.impl;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.instant.ai.service.IInstantBIHttpService;
import com.helicalinsight.instant.ai.util.InstantBIUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class InstantBIHttpServiceImpl implements IInstantBIHttpService {

    private static final Logger logger = LoggerFactory.getLogger(InstantBIHttpServiceImpl.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String callHttp(String endpoint, JsonObject body) {
        return callHttp(endpoint, body, null);
    }

    @Override
    public String executeCancellableCall(HttpServletRequest request, Callable<JsonObject> bodyPreparer, String endpoint) {
        String requestId = InstantBIUtils.resolveRequestId(request);
        if (InstantBIUtils.isRequestCancelled(requestId)) {
            throw new EfwServiceException("Request has been cancelled.");
        }

        CountDownLatch bodyReady = new CountDownLatch(1);
        AtomicReference<JsonObject> bodyRef = new AtomicReference<>();
        AtomicReference<String> responseBody = new AtomicReference<>();
        AtomicReference<RuntimeException> preparationError = new AtomicReference<>();

        TaskExecutorService taskExecutorService = ApplicationContextAccessor.getBean(TaskExecutorService.class);
        Future<?> taskFuture = taskExecutorService.submit(() -> {
            try {
                bodyReady.await();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }
            if (preparationError.get() != null || InstantBIUtils.isRequestCancelled(requestId)) {
                return;
            }
            responseBody.set(callHttp(endpoint, bodyRef.get(), requestId));
        }, requestId);

        try {
            bodyRef.set(bodyPreparer.call());
        } catch (EfwServiceException exception) {
            preparationError.set(exception);
            throw exception;
        } catch (Exception exception) {
            EfwServiceException wrapped = new EfwServiceException("problem while loading the call " + endpoint);
            preparationError.set(wrapped);
            throw wrapped;
        } finally {
            bodyReady.countDown();
        }

        try {
            taskFuture.get();
        } catch (CancellationException | InterruptedException exception) {
            InstantBIUtils.cancelActiveHttpCall(requestId);
            Thread.currentThread().interrupt();
            throw new EfwServiceException("Request has been cancelled.");
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof EfwServiceException) {
                throw (EfwServiceException) cause;
            }
            throw new EfwServiceException("problem while loading the call " + endpoint);
        }

        if (InstantBIUtils.isRequestCancelled(requestId)) {
            throw new EfwServiceException("Request has been cancelled.");
        }

        return responseBody.get();
    }

    private String callHttp(String endpoint, JsonObject body, @Nullable String requestId) {
        if (InstantBIUtils.isRequestCancelled(requestId)) {
            throw new EfwServiceException("Request has been cancelled.");
        }

        HttpClient client = HttpClient.newHttpClient();
        String url = InstantBIUtils.getInstantBIServiceUrl();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture =
                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        InstantBIUtils.registerActiveHttpCall(requestId, responseFuture);

        try {
            HttpResponse<String> response = responseFuture.get();
            logger.info("response -----------------------" + response);
            logger.info("response -----------------------" + response.body());
            if (InstantBIUtils.isRequestCancelled(requestId)) {
                throw new EfwServiceException("Request has been cancelled.");
            }
            return response.body();
        } catch (InterruptedException exception) {
            responseFuture.cancel(true);
            Thread.currentThread().interrupt();
            throw new EfwServiceException("Request has been cancelled.");
        } catch (ExecutionException exception) {
            if (exception.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw new EfwServiceException("Request has been cancelled.");
            }
            throw new EfwServiceException("problem while loading the call " + endpoint);
        } finally {
            InstantBIUtils.unregisterActiveHttpCall(requestId);
        }
    }
}
