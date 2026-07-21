//package com.helicalinsight.adhoc.designer;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.mockito.MockedStatic;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.helicalinsight.efw.ApplicationProperties;
//import com.helicalinsight.efw.utility.JaxbUtils;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DashboardDesignerFetchHandlerTest {
//
//	@Test
//	public void ut_a1_test_executeComponent() {
//		DashboardDesignerFetchHandler dashboardDesignerFetchHandler = new DashboardDesignerFetchHandler();
//		JsonObject formData = new JsonObject();
//		formData.add("file", null);
//		dashboardDesignerFetchHandler.executeComponent(formData.toString());
//		
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void ut_a2_test_executeComponent() {
//		DashboardDesignerFetchHandler dashboardDesignerFetchHandler = new DashboardDesignerFetchHandler();
//		JsonObject formData = new JsonObject();
//		formData.addProperty("dir", "dir");
//		formData.addProperty("file", "file");
//		
//		dashboardDesignerFetchHandler.executeComponent(formData.toString());
//			
//		
//	}
//	
//	@Test
//	public void ut_a3_test_executeComponent() throws IOException {
//		DashboardDesignerFetchHandler dashboardDesignerFetchHandler = new DashboardDesignerFetchHandler();
//		EfwDashboardDesigner dashboardDesigner = mock(EfwDashboardDesigner.class);
//		JsonObject formData = new JsonObject();
//		formData.addProperty("dir", "System");
//		formData.addProperty("file", "file.txt");
//		String path = ApplicationProperties.INSTANCE.getSolutionDirectory() + File.separator+"file.txt";
//		File file = new File(path);
//		file.createNewFile();
//		
//		try(MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)){
//			mockedStatic.when(()-> JaxbUtils.unMarshal(any(), any())).thenReturn(dashboardDesigner);
//			String executeComponent = dashboardDesignerFetchHandler.executeComponent(formData.toString());
//			assertTrue(JsonParser.parseString(executeComponent).getAsJsonObject().has("reportName"));
//			
//		}finally {
//			file.delete();
//		}
//		
//		
//	}
//	@Test
//	public void ut_a4_test_isThreadsafeToCache() {
//		DashboardDesignerFetchHandler dashboardDesignerFetchHandler = new DashboardDesignerFetchHandler();
//		boolean threadSafeToCache = dashboardDesignerFetchHandler.isThreadSafeToCache();
//		assertTrue(threadSafeToCache);
//	}
//
//}
