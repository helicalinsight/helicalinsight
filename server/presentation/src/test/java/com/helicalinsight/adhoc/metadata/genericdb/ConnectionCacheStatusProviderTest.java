package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionCacheStatusProviderTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		ConnectionCacheStatusProvider cacheStatusProvider = new ConnectionCacheStatusProvider();
		boolean threadSafeToCache = cacheStatusProvider.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
	
	
	@Test
	public void ut_a2_test_executeComponent() {
		ConnectionCacheStatusProvider cacheStatusProvider = new ConnectionCacheStatusProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("id", "12");
		formJson.addProperty("dir", "dir");
		
		DatabaseCacheService dbService = mock(DatabaseCacheService.class);
		DataSourceMapping dataSourceMapping = mock(DataSourceMapping.class);
		List<DataSourceMapping> allConnectionEntries = new ArrayList<DataSourceMapping>();
		allConnectionEntries.add(dataSourceMapping);
		when(dbService.isAConnectionCached(any())).thenReturn(true);
		when(dataSourceMapping.getConnectionId()).thenReturn(12);
		when(dataSourceMapping.getDir()).thenReturn("dir");
		when(dbService.getAllConnectionEntries()).thenReturn(allConnectionEntries);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(DatabaseCacheService.class)).thenReturn(dbService);
			
			String executeComponent = cacheStatusProvider.executeComponent(formJson.toString());
			assertFalse(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());
		}
		
		
	}
	
	@Test
	public void ut_a3_test_executeComponent() {
		ConnectionCacheStatusProvider cacheStatusProvider = new ConnectionCacheStatusProvider();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("id", "");
		formJson.addProperty("dir", "dir");
		
		DatabaseCacheService dbService = mock(DatabaseCacheService.class);
		DataSourceMapping dataSourceMapping = mock(DataSourceMapping.class);
		List<DataSourceMapping> allConnectionEntries = new ArrayList<DataSourceMapping>();
		allConnectionEntries.add(dataSourceMapping);
		when(dbService.isAConnectionCached(any())).thenReturn(true);
		when(dataSourceMapping.getConnectionId()).thenReturn(12);
		when(dataSourceMapping.getDir()).thenReturn("dir");
		when(dbService.getAllConnectionEntries()).thenReturn(allConnectionEntries);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(DatabaseCacheService.class)).thenReturn(dbService);
			
			String executeComponent = cacheStatusProvider.executeComponent(formJson.toString());
			assertFalse(JsonParser.parseString(executeComponent).getAsJsonObject().entrySet().isEmpty());
		}
		
		
	}
}
