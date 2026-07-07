//package com.helicalinsight.resourcedb;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
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
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class MetadataMultiConnectionsTest {
//
//	MockMvc efwMock;
//	MockMvc mockMvc;
//	@Autowired
//	FilterChainProxy filterChainProxy;
//
//	@Autowired
//	private WebApplicationContext context;
//
//	@Autowired
//	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//	@Autowired
//	HIResourceServiceDB serviceDb;
//	@Autowired
//	HIMetadataResourceServiceDB mdServiceDb;
//
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//
//	@Before
//	@Transactional
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
//				.build();
//		ServletContext servletContext = context.getServletContext();
//		servletContext.setAttribute("filterStatus", "ok");
//		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//	}
//
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//	private static String jdbcUrl = "";
//	private static String firstJdbcId = "";
//	private static String dbName = "";
//	private static String secondJdbcId = "";
//
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			jdbcUrl = "jdbc:derby:"
//					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
//		} else if (os.toLowerCase().contains("windows")) {
//			jdbcUrl = "jdbc:derby:"
//					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
//		}
//	}
//
//	@Test
//	public void md_a0_create_a_folder_to_save_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "MetadataWithMultipleConnections");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//
//	@Test
//	public void md_a1_testFirstDataSourceConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "test");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"FirstDatasource\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The connection test is successful."))
//				.andDo(MockMvcResultHandlers.print());
//	}
//
//	@Test
//	public void md_a2_createFirstDataSourceConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"FirstDatasource\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				//.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				//.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//					//	.value("A new Tomcat data source is created successfully."))
//				.andDo(MockMvcResultHandlers.print()).andReturn();
//		JSONObject resultJson = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject response = resultJson.getJSONObject("response");
//		firstJdbcId = response.getString("dataSourceId");
//	}
//
//	@Test
//	public void md_a3_testSecondDataSourceConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "test");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SecondDatasource\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				//.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					//	.jsonPath("$.response.message").value("The connection test is successful."))
//				.andDo(MockMvcResultHandlers.print());
//	}
//
//	@Test
//	public void md_a4_createSecondDataSourceConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SecondDatasource\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				//.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				//.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//					//	.value("A new Tomcat data source is created successfully."))
//				.andDo(MockMvcResultHandlers.print()).andReturn();
//		JSONObject resultJson = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject response = resultJson.getJSONObject("response");
//		secondJdbcId = response.getString("dataSourceId");
//	}
//	
//	@Test
//    public void md_a5_expand_catalog_schema() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/services");
//        List<String> sourceList = new ArrayList<>();
//        sourceList.add("");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        map.put("formData", "{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//	
//	@Test
//    public void md_a6_expand_catalog_schema_second_ds() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/services");
//        List<String> sourceList = new ArrayList<>();
//        sourceList.add("");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        map.put("formData", "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//
//
//    @Test
//    public void md_a7_expand_table() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        map.put("formData", "{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//    
//    @Test
//    public void md_a8_expand_table_second_ds() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        map.put("formData", "{\"id\":\"" + secondJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"Null\",\"schemas\":[]}]}}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//    @Test
//    public void md_a9_createMetadata() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "update");
//        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"connectionDatabaseId\":\"\",\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"jnbfo\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"connectionDatabaseId\":\"\",\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"uc0qf\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataWithMultipleConnections\",\"metadataReload\":true}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = this.efwMock.perform(builder).andReturn();
//        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//        int status = jsonObject.getInt("status");
//        JSONObject responseObject = jsonObject.getJSONObject("response");
//        System.out.println("First Save ::"+responseObject);
//        String message = responseObject.getString("message");
//        Assert.assertNotNull(responseObject.getJSONArray("data"));
//        Assert.assertEquals(1, status);
//        Assert.assertEquals("Successfully saved metadata file", message);
//        JSONArray  connectionsArray =  responseObject.getJSONObject("metadata").getJSONArray("connections");
//        Assert.assertTrue(connectionsArray.size() == 1);
//    }
//    
//    @Test
//    public void md_b1_saveSecondTime() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "update");
//        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"5mefo\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"p1rik\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SecondDatasource\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataWithMultipleConnections\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = this.efwMock.perform(builder).andReturn();
//        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//        int status = jsonObject.getInt("status");
//        JSONObject responseObject = jsonObject.getJSONObject("response");
//        System.out.println(responseObject);
//        String message = responseObject.getString("message");
//        Assert.assertNotNull(responseObject.getJSONArray("data"));
//        Assert.assertEquals(1, status);
//        Assert.assertEquals("Successfully saved metadata file", message);
//        JSONArray  connectionsArray =  responseObject.getJSONObject("metadata").getJSONArray("connections");
//        Assert.assertTrue(connectionsArray.size() == 1);
//    }
//    
//    @Test
//    public void md_b2_updateMetadata() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "update");
//        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ekqy8\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"fnfdn\",\"classifier\":\"db.workflow\",\"datasourceName\":\"derby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"UpdatedMetadata\",\"location\":\"MetadataWithMultipleConnections\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = this.efwMock.perform(builder).andReturn();
//        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//        int status = jsonObject.getInt("status");
//        JSONObject responseObject = jsonObject.getJSONObject("response");
//        System.out.println(responseObject);
//        String message = responseObject.getString("message");
//        Assert.assertEquals(1, status);
//        Assert.assertEquals("Successfully saved metadata file", message);
//        Assert.assertEquals("SaveSelectAll.metadata", responseObject.getString("uuid"));
//        JSONArray  connectionsArray =  responseObject.getJSONObject("metadata").getJSONArray("connections");
//        Assert.assertTrue(connectionsArray.size() == 1);
//    }
//    
//    
//    @Test
//    public void md_b3_deleteFolder() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/fileSystemOperations");
//        String input = "[\"MetadataWithMultipleConnections\"]";
//        Map<String, String> map = new HashMap<>();
//        map.put("action", "delete");
//        map.put("sourceArray", input);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                .jsonPath("$.response.message").value("Delete operation is successful"));
//    }
//	
//    
//    
//}
