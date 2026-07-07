package com.helicalinsight.cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mockito.MockedStatic;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.cache.filter.CacheFilter;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.AccessDeniedException;

public class CacheFilterTest {

	@Test
	public void test() throws ServletException {
		CacheFilter cacheFilter =  new CacheFilter();
		cacheFilter.init(null);
	}
	@Test
	public void testdestory() throws ServletException {
		CacheFilter cacheFilter =  new CacheFilter();
		cacheFilter.destroy();
	}
	@Test
	public void testDoFilter_a1() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		CacheFilter cacheFilter =  new CacheFilter();
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(false);
			cacheFilter.doFilter(request, response, chain);
			verify(chain, times(1)).doFilter(request,response);
		}
		
	}
	@Test
	public void testDoFilter_a2() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("url");
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			cacheFilter.doFilter(request, response, chain);
			
		}
		
	}
	
	@Test
	public void testDoFilter_a3() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("url");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
       
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	
	@Test
	public void testDoFilter_a4() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("url");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        when(request.getParameter("file")).thenReturn("file");
        when(request.getParameter("dir")).thenReturn("AB:");
        when(request.getParameter("refresh")).thenReturn("refresh");
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	
	@Test
	public void testDoFilter_a5() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("/services");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	@Test(expected = NullPointerException.class)
	public void testDoFilter_a6() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheManager cacheManager = mock(CacheManager.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("/services");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        when(request.getParameter("type")).thenReturn("type");
        when(request.getParameter("serviceType")).thenReturn("report");
        when(request.getParameter("service")).thenReturn("fetchData");
        
        
        JsonObject cacheXmlJsonObject = new JsonObject();
        JsonObject adhocManager = new JsonObject();
        adhocManager.addProperty("type", "type");
        adhocManager.addProperty("visualizeUrl", "/services");
        adhocManager.addProperty("serviceType", "report");
        cacheXmlJsonObject.add("adhocManager", adhocManager);
        
        JsonObject formData = new JsonObject();
        formData.addProperty("refresh","true");
        formData.addProperty("uniqueId","reportName");
        formData.addProperty("location","AB:");
        formData.addProperty("metadataFileName", "");
        String jsonString = formData.toString();
        byte[] base64Encoded = Base64.encodeBase64(jsonString.getBytes());
        String base64EncodedString = new String(base64Encoded);

        when(request.getParameter("formData")).thenReturn(base64EncodedString);
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			mockedStatic.when(() -> CacheUtils.getCacheManager("/services")).thenReturn(cacheManager);
			mockedStatic.when(() -> CacheUtils.getCacheXmlJson()).thenReturn(cacheXmlJsonObject);
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	
	@Test(expected = NullPointerException.class)
	public void testDoFilter_a7() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheManager cacheManager = mock(CacheManager.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("/services");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        when(request.getParameter("type")).thenReturn("type");
        when(request.getParameter("serviceType")).thenReturn("report");
        when(request.getParameter("service")).thenReturn("fetchData");
        
        
        JsonObject cacheXmlJsonObject = new JsonObject();
        JsonObject adhocManager = new JsonObject();
        adhocManager.addProperty("type", "type");
        adhocManager.addProperty("visualizeUrl", "/services");
        adhocManager.addProperty("serviceType", "report");
        cacheXmlJsonObject.add("adhocManager", adhocManager);
        
        JsonObject formData = new JsonObject();
        formData.addProperty("refresh","false");
        formData.addProperty("uniqueId","");
        formData.addProperty("location","AB:");
        formData.addProperty("metadataFileName", "");
        String jsonString = formData.toString();
        byte[] base64Encoded = Base64.encodeBase64(jsonString.getBytes());
        String base64EncodedString = new String(base64Encoded);

        when(request.getParameter("formData")).thenReturn(base64EncodedString);
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			mockedStatic.when(() -> CacheUtils.getCacheManager("/services")).thenReturn(cacheManager);
			mockedStatic.when(() -> CacheUtils.getCacheXmlJson()).thenReturn(cacheXmlJsonObject);
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	
	@Test
	public void testDoFilter_a8() throws IOException, ServletException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheManager cacheManager = mock(CacheManager.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("/Url_Url");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        
        
       
        
        when(request.getParameter("data")).thenReturn(null);
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			mockedStatic.when(() -> CacheUtils.getCacheManager("/Url_Url")).thenReturn(cacheManager);
			
			cacheFilter.doFilter(request, response, chain);
			verify(chain,times(1)).doFilter(request, response);
		}
		
	}
	
	@Test
	public void testDoFilter_a9() throws IOException, ServletException,IllegalAccessException, IllegalArgumentException, 
						InvocationTargetException, NoSuchMethodException,NoSuchFieldException, SecurityException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		ServletResponse response = mock(ServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		HttpSession session = mock(HttpSession.class);
		CacheManager cacheManager = mock(CacheManager.class);
		CacheHelper cacheHelper = mock(CacheHelper.class);
		Cache cache = mock(Cache.class);
		CacheFilter cacheFilter =  new CacheFilter();
		when(request.getServletPath()).thenReturn("/Url_Url");
		String url = "url";
        JsonArray refreshUrl = new JsonArray();
        JsonPrimitive primitive = new JsonPrimitive(url);
        refreshUrl.add(primitive);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("queryId")).thenReturn("12A");
        when(request.getParameter("cancelQuery")).thenReturn("false");
        when(session.getAttribute("queryCount")).thenReturn(12l);
        
        JsonObject data = new JsonObject();
        data.addProperty("hi_refresh", true); 
        data.addProperty("hi_requestedReport", "reportName");
        
        Field field = CacheFilter.class.getDeclaredField("cacheHelper");
        field.setAccessible(true);
        field.set(cacheFilter, cacheHelper);
        
        when(session.getAttribute("refresh")).thenReturn("true");
        when(request.getParameter("data")).thenReturn(data.toString());
        when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenReturn(cache);
        when(cacheHelper.processCache(request, response, "reportName", true, cache, cacheManager)).thenReturn(true);
        
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			mockedStatic.when(() -> CacheUtils.getCacheManager("/Url_Url")).thenReturn(cacheManager);
			
			cacheFilter.doFilter(request, response, chain);
		}
		
	}
	
	@Test
	public void testDoFilter_b1() throws IOException, ServletException,IllegalAccessException, IllegalArgumentException, 
						InvocationTargetException, NoSuchMethodException,NoSuchFieldException, SecurityException {
		
        
		try(MockedStatic<CacheUtils> mockedStatic = mockStatic(CacheUtils.class)){
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			FilterChain chain = mock(FilterChain.class);
			HttpSession session = mock(HttpSession.class);
			CacheManager cacheManager = mock(CacheManager.class);
			CacheHelper cacheHelper = mock(CacheHelper.class);
			Cache cache = mock(Cache.class);
			CacheFilter cacheFilter =  new CacheFilter();
			when(request.getServletPath()).thenReturn("/Url_Url");
			String url = "url";
	        JsonArray refreshUrl = new JsonArray();
	        JsonPrimitive primitive = new JsonPrimitive(url);
	        refreshUrl.add(primitive);
	        
	        when(request.getSession(false)).thenReturn(session);
	        when(request.getParameter("queryId")).thenReturn("12A");
	        when(request.getParameter("cancelQuery")).thenReturn("false");
	        when(session.getAttribute("queryCount")).thenReturn(12l);
	        
	        JsonObject data = new JsonObject();
	        data.add("hi_refresh", null); 
	        data.add("hi_requestedReport", null);
	        
	        Field field = CacheFilter.class.getDeclaredField("cacheHelper");
	        field.setAccessible(true);
	        field.set(cacheFilter, cacheHelper);
	        
	        when(session.getAttribute("refresh")).thenReturn("true");
	        when(request.getParameter("data")).thenReturn(data.toString());
	        when(cacheHelper.prepareCacheFromRequest(cacheManager)).thenThrow(new AccessDeniedException("exception"));
	        when(cacheHelper.processCache(request, response, "reportName", true, cache, cacheManager)).thenReturn(true);
	        
			
			mockedStatic.when(() -> CacheUtils.isCacheEnabled()).thenReturn(true);
			mockedStatic.when(() -> CacheUtils.getRefreshUrl()).thenReturn(refreshUrl);
			mockedStatic.when(() -> CacheUtils.getCacheManager("/Url_Url")).thenReturn(cacheManager);
			String message = "exception";
			ControllerUtils utils = mock(ControllerUtils.class);
			//utils.when(() -> ControllerUtils.accessDenied(request,(HttpServletResponse) response, message)).thenAnswer((Answer<Void>) invocation -> null);
			doNothing().when(utils).accessDenied(request,response, message);
			cacheFilter.doFilter(request, response, chain);
					
		}
	}	
}
