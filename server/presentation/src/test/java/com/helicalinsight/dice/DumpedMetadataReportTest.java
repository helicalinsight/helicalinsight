//package com.helicalinsight.dice;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import javax.transaction.Transactional;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.utils.JacksonUtility;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.spark.SparkExecutionCommands;
//import com.helicalinsight.spark.SparkServiceComponent;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//        "classpath:spring-security.xml"})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DumpedMetadataReportTest {
//	
//	
//	
//	    MockMvc efwMock;
//	    MockMvc mockMvc;
//	    @Autowired
//	    FilterChainProxy filterChainProxy;
//	    
//	    @Autowired
//	    private IntegrationTestUtility testUtility;
//	   
//
//	    @Autowired
//	    private WebApplicationContext context;
//
//
//	    @Autowired
//	    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//
//	    @Bean
//	    public FileSystemOperationsController fileSystemOperationsController() {
//	        return new FileSystemOperationsController();
//	    }
//	    
//	    private static final String mysqlDbName = "sampletraveldata";
//		private static final String mysqlUrl = "jdbc:mysql://localhost:3306/"+mysqlDbName;
//		private static final String mariaDburl = "jdbc:mariadb://localhost:3307/"+mysqlDbName;
//		private static String mysqlJdbcId ="";
//		private static String mariaJdbcId = "";
//		
//	    @Before
//	    @Transactional
//	    public void setup() {
//	        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
//	        ServletContext servletContext = context.getServletContext();
//	        servletContext.setAttribute("filterStatus", "ok");
//	        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//	        loadRepository();
//	    }
//	    
//	    
//	    public void loadRepository() {
//	    	
//	    	try {
//				Constructor<?> constructor = Class.forName("com.helicalinsight.efw.framework.RepositoryLoader").getDeclaredConstructor();
//				constructor.setAccessible(true);
//				Object b = constructor.newInstance();
//				Method loadMethod = b.getClass().getMethod("load");
//				loadMethod.setAccessible(true);
//				loadMethod.invoke(b);
//	    	
//	    	} catch (Exception e) {
//				e.printStackTrace();
//			} 
//	    }
//	    
//	    
//	    
//	    @Autowired
//	    private EfwServicesController efwServicesController;
//
//	    @Autowired
//	    private FileSystemOperationsController fileSystemOperationsController;
//	    
//	  
//		
//		static String created_date_id = "8";
//		static String created_time_id = "47";
//
//		
////		@Test
//		public void dice_a0_prepare() throws Exception {
//			
//			
//			
//			String mysqlFormData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"com.mysql.cj.jdbc.Driver\",\"name\":\"MysqlDynamicConnection\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""
//					+ mysqlDbName + "\",\"jdbcUrl\":\"" + mysqlUrl + "\"}";
//
//			String mariaformData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.mariadb.jdbc.Driver\",\"name\":\"MysqlDynamicConnection\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""
//					+ mysqlDbName + "\",\"jdbcUrl\":\"" + mariaDburl + "\"}";
//			String mysqlResponse = testUtility.createDatasource(mysqlFormData);
//			String mariaResponse = testUtility.createDatasource(mariaformData);
//			JSONObject jsonObject = JSONObject.fromObject(mysqlResponse);
//			JSONObject responseObject = jsonObject.getJSONObject("response");
//			JSONObject data = responseObject.getJSONObject("data");
//			JSONObject mariajsonObject = JSONObject.fromObject(mariaResponse);
//			JSONObject mariaResponseObject = mariajsonObject.getJSONObject("response");
//			JSONObject mariaData = mariaResponseObject.getJSONObject("data");
//			mysqlJdbcId = data.getString("id");
//			mariaJdbcId = mariaData.getString("id");
//			
//			
//
//			testUtility.expand("{\"id\":\""+ mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//			testUtility.expand("{\"id\":\""+ mariaJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//			
//			testUtility.expand("{\"id\":\""+mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"SampleTravelData\",\"schemas\":[]}]}}");
//			testUtility.expand("{\"id\":\""+mariaJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"SampleTravelData\",\"schemas\":[]}]}}");
//			
//			testUtility.createFolder("DumpedMetadataReportTest");
//			
//			
//
//			String response = testUtility.createMetadata("{\"database\":\"SampleTravelData\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\""+mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"SampleTravelData\",\"schema\":\"\",\"connId\":\"m1bbn\",\"dbId\":\"m1bbn\",\"datasourceName\":\"MYSQlSampleDS\",\"database\":\"SampleTravelData\",\"databaseType\":\"Mysql\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"9d13652ec6bfac5f3776d2c344a6ed69\",\"152371825108bf241d5e58d460282bf0\",\"16639182c9f9434f6c23adc92c13ca91\",\"28527591b9b87216cf89e68720df9c87\",\"0d08fba10235e4dea8cb57fd92e29e1d\"],\"columns\":[],\"views\":[],\"connections\":[\"bicdy\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"SampleTravelData\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+mariaJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"SampleTravelData\",\"schema\":\"\",\"connId\":\"bicdy\",\"dbId\":\"bicdy\",\"datasourceName\":\"MariadbDs\",\"database\":\"SampleTravelData\",\"databaseType\":\"Mariadb\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"9d13652ec6bfac5f3776d2c344a6ed69\",\"152371825108bf241d5e58d460282bf0\",\"16639182c9f9434f6c23adc92c13ca91\",\"28527591b9b87216cf89e68720df9c87\",\"0d08fba10235e4dea8cb57fd92e29e1d\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[{\"id\":\"9d13652ec6bfac5f3776d2c344a6ed69\",\"alias\":\"dimdate_1\",\"connId\":\"bicdy\"},{\"id\":\"152371825108bf241d5e58d460282bf0\",\"alias\":\"employee_details_1\",\"connId\":\"bicdy\"},{\"id\":\"16639182c9f9434f6c23adc92c13ca91\",\"alias\":\"geo_cordinates_1\",\"connId\":\"bicdy\"},{\"id\":\"28527591b9b87216cf89e68720df9c87\",\"alias\":\"meeting_details_1\",\"connId\":\"bicdy\"},{\"id\":\"0d08fba10235e4dea8cb57fd92e29e1d\",\"alias\":\"travel_details_1\",\"connId\":\"bicdy\"}],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DumpedMetadataReportTest\",\"metadataReload\":true}");
//			JSONObject mdObject = JSONObject.fromObject(response).getJSONObject("response");
//			
//			JSONObject dimdate_1 = mdObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate");
//			JSONObject created_date = dimdate_1.getJSONObject("columns").getJSONObject("created_date");
//			created_date_id = created_date.getString("id");
//			System.out.println("created_date_id : "+created_date_id);
//			JSONObject dimdate_2 = ((JSONObject) mdObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("tables").getJSONObject("dimdate");
//			JSONObject created_time =  dimdate_2.getJSONObject("columns").getJSONObject("created_time");
//			created_time_id = created_time.getString("id");
//			System.out.println("created_time_id : "+created_time_id);
//			
//		}
//		
//		
//		
//		@Test
//		public void dice_a1_startDice() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "monitor");
//			map.put("serviceType", "system");
//			map.put("service", "management");
//			map.put("formData", "{\"command\":\"START_COMPUTATION\"}");
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//					.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//							.jsonPath("$.response.message").value("Computation Started successfully"));
//		}
//		static long processId ;
//		
////		@Test
//		public void dice_a2_dumpMetadata() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "adhoc");
//			map.put("serviceType", "metadata");
//			map.put("service", "dumpCube");
//			map.put("formData", "{\"location\":\"DumpedMetadataReportTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"dumpType\":\"deep\"}");
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//					.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//			ObjectNode node =  JacksonUtility.fromObject(result.getResponse().getContentAsString());
//			ObjectNode data = node.with("response").with("data");
//			processId = data.get("id").asLong();
//		}
//		@Test
//		public void dice_a7_1_fetchData() throws Exception {
//			
//			String form = "{\"location\":\"DumpedMetadataReportTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"columns\":[{\"column\":{\"name\":\"SampleTravelData.dimdate.created_date\",\"id\":\""+created_date_id+"\"},\"alias\":\"created_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
//			String query = testUtility.generateQuery(form);
////			String formData = "{\"location\":\"DumpedMetadataReportTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"columns\":[{\"column\":\"SampleTravelData.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\",\"id\":\""+created_date_id+"\"},{\"column\":\"SampleTravelData.dimdate.created_time\",\"alias\":\"created_time\",\"floatingType\":\"discrete\",\"id\":\""+created_time_id+"\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true},{\"column\":\"created_time\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
////			String response = testUtility.fetchData(formData);
//			System.out.println(query);
//			
//		}
//		
////		@Test
//		public void dice_a7_generateQuery() throws Exception {
//			
//			String formData = "{\"location\":\"DumpedMetadataReportTest\",\"metadataFileName\":\"Metadata_1.metadata\",\"columns\":[{\"column\":\"SampleTravelData.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\",\"id\":\""+created_date_id+"\"},{\"column\":\"SampleTravelData.dimdate.created_time\",\"alias\":\"created_time\",\"floatingType\":\"discrete\",\"id\":\""+created_time_id+"\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true},{\"column\":\"created_time\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
//			String response = testUtility.fetchData(formData);
//			System.out.println(response);
//			
//		}
//		
////		@Test
//		public void dice_a8_deleteDump() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "adhoc");
//			map.put("serviceType", "metadata");
//			map.put("service", "deleteDump");
//			map.put("formData", "{\"id\":\""+processId+"\",\"type\":\"metadata\"}");
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
//					MockMvcResultMatchers.jsonPath("$.response.message").value("Dump deleted successfully.")).andDo(MockMvcResultHandlers.print());
//		}
//
//		@Test
//		public void dice_a9_diceStop() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "monitor");
//			map.put("serviceType", "system");
//			map.put("service", "management");
//			map.put("formData", "{\"command\":\"STOP_COMPUTATION\"}");
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
//					MockMvcResultMatchers.jsonPath("$.response.message").value("Computation Stopped successfully"));
//		}
////		
////		@Test
////		public void dice_b4_deleteFolder() throws Exception {
////			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
////					.post("/fileSystemOperations");
////			String input = "[\"MetadataDumpTest\"]";
////			Map<String, String> map = new HashMap<>();
////			map.put("action", "delete");
////			map.put("sourceArray", input);
////			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
////			this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
////					.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
////							MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
////		}
////	    
//	   
//
//}
