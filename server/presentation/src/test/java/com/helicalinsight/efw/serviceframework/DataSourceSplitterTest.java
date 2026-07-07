// TODO : Configuration issue.
//package com.helicalinsight.efw.serviceframework;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.helicalinsight.efw.exceptions.EfwServiceException;
//
//import net.sf.json.JSONObject;
//
//public class DataSourceSplitterTest {
//
//	@Test
//	public void testprepareFormDataList() {
//		DataSourceSplitter splitter = new DataSourceSplitter();
//		splitter.prepareFormDataList();
//	}
//	
//
//	@Test
//	public void testgetCacheObject() {
//		DataSourceSplitter splitter = new DataSourceSplitter();
//		splitter.getCacheObject();
//	}
//	
//	//ServiceManager 
//	@Test
//	public void testclearAll() {
//		ServiceManager manager = new ServiceManager();
//		manager.clearAll();
//	}
//	
//	@Test(expected = EfwServiceException.class)
//	public void testabortResult() {
//		ServiceManager manager = new ServiceManager();
//		JsonObject formJson =new JsonObject();
//		List<String> list = new ArrayList<>();
//		list.add("11");
//		list.add("12");
//		list.add("13");
//		list.add("14");
//		list.add("15");
//		Gson gson = new GsonBuilder().create();
//		formJson.addProperty("requestIdToAbort", gson.toJson(list));
//		manager.abortResult(formJson);
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testgetResult() {
//		ServiceManager manager = new ServiceManager();
//		 JsonObject formJson = new JsonObject();
//		 formJson.addProperty("serviceId", "1");
//		 formJson.addProperty("pageNumber", 10);
//		manager.getResult(null, null, null, formJson, null);
//		
//	}
//	
//}
