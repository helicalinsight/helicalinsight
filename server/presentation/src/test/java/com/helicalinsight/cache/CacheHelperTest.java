package com.helicalinsight.cache;

import com.google.gson.JsonObject;
import com.helicalinsight.cache.manager.CacheManager;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.efw.utility.SplitterUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CacheHelperTest {

    @InjectMocks
    private CacheHelper cacheHelper;

    @Mock
    private CacheService cacheService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPrepareCache_WithQuery() {
        when(cacheManager.getConnectionFilePath()).thenReturn("path");
        when(cacheManager.getConnectionId()).thenReturn(1L);
        when(cacheManager.getConnectionType(1L)).thenReturn("type");
        when(cacheManager.getMapId()).thenReturn(100);
        when(cacheManager.getQuery("type")).thenReturn("select *");

        Cache cache = cacheHelper.prepareCacheFromRequest(cacheManager);

        assertNotNull(cache.getQuery());
    }

    @Test
    public void testPrepareCache_NoQuery() {
        when(cacheManager.getConnectionType(anyLong())).thenReturn("type");
        when(cacheManager.getQuery("type")).thenReturn(null);

        Cache cache = cacheHelper.prepareCacheFromRequest(cacheManager);

        assertNull(cache.getQuery());
    }

    @Test
    public void testDesignCacheKeyFor_returnsNullWhenCacheNotFound() {
        Cache requestCache = mock(Cache.class);
        when(cacheService.findUniqueCache(requestCache)).thenReturn(null);

        assertNull(cacheHelper.designCacheKeyFor(requestCache));
    }

    @Test
    public void testDesignCacheKeyFor_returnsNullWhenCacheFilePathMissing() {
        Cache requestCache = mock(Cache.class);
        Cache cacheModel = new Cache();
        when(cacheService.findUniqueCache(requestCache)).thenReturn(cacheModel);

        assertNull(cacheHelper.designCacheKeyFor(requestCache));
    }

    @Test
    public void testDesignCacheKeyFor_returnsPreparedServiceId() {
        Cache requestCache = mock(Cache.class);
        Cache cacheModel = new Cache();
        cacheModel.setCacheFilePath("path/to/cache");
        when(cacheService.findUniqueCache(requestCache)).thenReturn(cacheModel);

        String designCacheKey = cacheHelper.designCacheKeyFor(requestCache);

        assertEquals(SplitterUtils.prepareServiceId(cacheModel.toString()), designCacheKey);
    }

    @Test
    public void testProcessCache_QueryNull() {
        Cache requestCache = mock(Cache.class);
        when(requestCache.getQuery()).thenReturn(null);

        Boolean result = cacheHelper.processCache(
                request, response, "report",
                false, requestCache, cacheManager);

        assertFalse(result);
    }


//    @Test
    public void testProcessCache_CacheHit_NotExpired() throws IOException {

        Cache requestCache = mock(Cache.class);
        Cache cacheModel = mock(Cache.class);

        when(requestCache.getQuery()).thenReturn("abc");
        when(cacheService.findUniqueCache(requestCache)).thenReturn(cacheModel);

        when(cacheModel.getCacheFilePath()).thenReturn("file.cache");
        when(cacheModel.getNoOfRecords()).thenReturn(5);
        when(cacheModel.getCacheExpiryTime())
                .thenReturn(new Date(System.currentTimeMillis() + 10000));
        when(cacheModel.getCacheFileTimeStamp()).thenReturn(new Date());

        // Make request/response HTTP
        HttpServletRequest httpReq = mock(HttpServletRequest.class);
        HttpServletResponse httpRes = mock(HttpServletResponse.class);

        try (MockedStatic<CacheUtils> mocked = mockStatic(CacheUtils.class)) {

            mocked.when(CacheUtils::isCacheEnabled).thenReturn(true);
            mocked.when(CacheUtils::getCacheDirectory).thenReturn("dir");

            // Default branch of serveCacheFile()
            when(cacheManager.readFileContent(anyString()))
                    .thenReturn(new JsonObject());

            when(cacheManager.serveCachedContent(
                    any(HttpServletRequest.class),
                    any(HttpServletResponse.class),
                    any()))
                    .thenReturn(true);

            Boolean result = cacheHelper.processCache(
                    httpReq, httpRes,
                    "report",
                    false,
                    requestCache,
                    cacheManager);

            assertTrue(result);
        }
    }


//    @Test
    public void testProcessCache_SynchronousBranch() throws IOException {

        Cache requestCache = mock(Cache.class);

        when(requestCache.getQuery()).thenReturn("encodedQuery");

        // Force cache miss so process() is called
        when(cacheService.findUniqueCache(requestCache)).thenReturn(null);

        when(cacheManager.getDirectory()).thenReturn("dir");

        String encoded = Base64.getEncoder()
                .encodeToString("select".getBytes());
        when(requestCache.getQuery()).thenReturn(encoded);
        when(requestCache.getCacheFilePath()).thenReturn("file.cache");
        when(requestCache.getCacheFileTimeStamp()).thenReturn(new Date());

        when(cacheManager.getDataFromDatabase(anyString()))
                .thenReturn(new JsonObject());

        doNothing().when(cacheManager)
                .saveToDisk(any(), anyString(), any());

        when(cacheManager.readFileContent(anyString()))
                .thenReturn(new JsonObject());

        when(cacheManager.serveCachedContent(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                any()))
                .thenReturn(true);

        try (MockedStatic<CacheUtils> mocked = mockStatic(CacheUtils.class)) {

            mocked.when(CacheUtils::isCacheEnabled).thenReturn(true);
            mocked.when(CacheUtils::getCacheDirectory).thenReturn("solutionDir");
            mocked.when(CacheUtils::isThreadingEnabled).thenReturn(false);

            Boolean result = cacheHelper.processCache(
                    mock(HttpServletRequest.class),
                    mock(HttpServletResponse.class),
                    "report",
                    false,
                    requestCache,
                    cacheManager);

            assertTrue(result);
        }
    }



    // ==============================
    // process() jsonData null branch
    // ==============================

    @Test
    public void testProcessCache_JsonDataNull() throws Exception {
        Cache requestCache = mock(Cache.class);

        String encoded = Base64.getEncoder().encodeToString("select".getBytes());
        when(requestCache.getQuery()).thenReturn(encoded);

        // Force cache miss so process() will be called
        when(cacheService.findUniqueCache(requestCache)).thenReturn(null);

        // Mock database to return null (triggers process() jsonData null branch)
        when(cacheManager.getDataFromDatabase(anyString())).thenReturn(null);

        try (MockedStatic<CacheUtils> mocked = mockStatic(CacheUtils.class)) {
            mocked.when(CacheUtils::isCacheEnabled).thenReturn(true);
            mocked.when(CacheUtils::isThreadingEnabled).thenReturn(false);
            mocked.when(CacheUtils::getCacheDirectory).thenReturn("dir");

            Boolean result = cacheHelper.processCache(
                    mock(HttpServletRequest.class),
                    mock(HttpServletResponse.class),
                    "report",
                    false,
                    requestCache,
                    cacheManager
            );

            assertFalse(result);
        }
    }


    // ==============================
    // serveCacheFile (private) coverage
    // ==============================

//    @Test
    public void testServeCacheFile_DefaultBranch() throws Exception {
    	
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	
        when(cacheManager.readFileContent(anyString()))
                .thenReturn(new JsonObject());

        Method method = CacheHelper.class
                .getDeclaredMethod("serveCacheFile",
                		ServletResponse.class,
                        String.class,
                        ServletRequest.class,
                        Date.class,
                        CacheManager.class);

        method.setAccessible(true);

        Boolean result = (Boolean) method.invoke(
                cacheHelper,
                 response,
                "file",
                 request,
                new Date(),
                cacheManager
        );

        assertTrue(result);
    }
}
