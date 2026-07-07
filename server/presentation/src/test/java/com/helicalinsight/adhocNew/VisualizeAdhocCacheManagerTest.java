package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.VisualizeAdhocCacheManager;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VisualizeAdhocCacheManagerTest {

	@Test
	public void ut_a1_test_serveCachedContent() {
		VisualizeAdhocCacheManager visualizeAdhocCacheManager = new VisualizeAdhocCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
		JsonObject fileContent = new JsonObject();
		
		when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
		boolean serveCachedContent = visualizeAdhocCacheManager.serveCachedContent(request, response, fileContent);
		assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_a2_test_serveCachedContent() throws ServletException, IOException {
		VisualizeAdhocCacheManager visualizeAdhocCacheManager = new VisualizeAdhocCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
		JsonObject fileContent = new JsonObject();
		
		when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
		
		doAnswer((invocation)->{
			throw new IOException();
		}).when(requestDispatcher).forward(request, response);
		boolean serveCachedContent = visualizeAdhocCacheManager.serveCachedContent(request, response, fileContent);
		assertTrue(serveCachedContent);
	}
	
	@Test
	public void ut_a3_test_serveCachedContent() throws ServletException, IOException {
		VisualizeAdhocCacheManager visualizeAdhocCacheManager = new VisualizeAdhocCacheManager();
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
		JsonObject fileContent = new JsonObject();
		
		when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
		doAnswer((invocation)->{
			throw new ServletException();
		}).when(requestDispatcher).forward(request, response);
		boolean serveCachedContent = visualizeAdhocCacheManager.serveCachedContent(request, response, fileContent);
		assertTrue(serveCachedContent);
	}
}
