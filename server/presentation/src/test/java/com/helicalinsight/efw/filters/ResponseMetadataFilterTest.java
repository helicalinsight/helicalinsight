package com.helicalinsight.efw.filters;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseMetadataFilterTest {

    @Test
    public void doFilter_bypassesNonAjaxRequestsWithoutWrappingResponse() throws ServletException, IOException {
        ResponseMetadataFilter filter = new ResponseMetadataFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Requested-With")).thenReturn(null);
        when(request.getServletPath()).thenReturn("/applicationSettings");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(eq(request), eq(response));
    }

    @Test
    public void doFilter_bypassesStaticAssetRequestsWithoutWrappingResponse() throws ServletException, IOException {
        ResponseMetadataFilter filter = new ResponseMetadataFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
        when(request.getServletPath()).thenReturn("/js/main.chunk.js");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(eq(request), eq(response));
    }

    @Test
    public void doFilter_wrapsAjaxApiRequests() throws ServletException, IOException {
        ResponseMetadataFilter filter = new ResponseMetadataFilter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");
        when(request.getServletPath()).thenReturn("/services");
        when(response.getStatus()).thenReturn(200);
        when(response.getHeader("Content-Encoding")).thenReturn(null);
        when(response.getContentType()).thenReturn("application/json");

        ArgumentCaptor<ServletResponse> responseCaptor = ArgumentCaptor.forClass(ServletResponse.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(eq(request), responseCaptor.capture());
        assertTrue(responseCaptor.getValue() instanceof ContentCachingResponseWrapper);
    }

    @Test
    public void doFilter_passesThroughNonHttpRequests() throws ServletException, IOException {
        ResponseMetadataFilter filter = new ResponseMetadataFilter();
        jakarta.servlet.ServletRequest request = mock(jakarta.servlet.ServletRequest.class);
        jakarta.servlet.ServletResponse response = mock(jakarta.servlet.ServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(chain, never()).doFilter(any(), any(HttpServletResponse.class));
    }
}
