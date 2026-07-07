//package com.helicalinsight.resourcedb;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
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
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
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
//public class MetadataMultiConnectionSwitchOrder {
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
//
//	private static String jdbcUrl = "";
//	private static String firstJdbcId = "";
//	private static String dbName = "";
//	private static String sqliteJdbcUrl = "";
//	private static String sqliteJdbcId = "";
//	private static String sqliteDbName = "";
//	private static final String mysqlDbName = "sampletraveldata";
//	private static final String mysqlUrl = "jdbc:mysql://localhost:3306/"+mysqlDbName;
//	private static String mysqlJdbcId ="";
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			jdbcUrl = "jdbc:derby:"
//					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			sqliteDbName = String.join("/", "/home", "helical", "Sqlite", "SampleTravelData.db");
//			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
//		} else if (os.toLowerCase().contains("windows")) {
//			jdbcUrl = "jdbc:derby:"
//					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
//			sqliteDbName = String.join("/", "C:", "home", "helical", "Sqlite", "SampleTravelData.db");
//			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
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
//		map.put("folderName", "MetadataOrderSwitchTest");
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
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"DynamicDataSourceDerby\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The connection test is successful."));
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
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"DynamicDataSourceDerby\",\"userName\":\"\",\"password\":\"\",\"database\":\""
//				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("A new Tomcat data source is created successfully.")).andReturn();
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
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.sqlite.JDBC\",\"name\":\"Sqlite\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""
//				+ sqliteDbName + "\",\"jdbcUrl\":\"" + sqliteJdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful."));
//	}
//
//	@Test
//	public void md_a4_createSecondDatasource() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.sqlite.JDBC\",\"name\":\"Sqlite\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""
//				+ sqliteDbName + "\",\"jdbcUrl\":\"" + sqliteJdbcUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("A new Tomcat data source is created successfully."))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		sqliteJdbcId = responseObject.getString("dataSourceId");
//
//	}
//
//	@Test
//	public void md_a5_expand_catalog_schema() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\"" + firstJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void md_a6_expand_table() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\"" + firstJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void md_a7_expand_second_ds_catalog_schema() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\"" + sqliteJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void md_a8_expand_second_ds_table() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\"" + sqliteJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"Null\",\"schemas\":[]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	
//	
//	@Test
//	public void md_a9_createMetadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ga5c7\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"Sqlite\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"lstqs\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Sqlite\",\"database\":\"Sqlite\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"b161910cbebfd353351a6c0b46e6a02e\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataOrderSwitchTest\",\"metadataReload\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		//System.out.println("Cross Joins : "+responseObject);
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		
//	}
//	
//	@Test
//	public void md_b1_secondSave() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"sync\":false,\"id\":\""+sqliteJdbcId+"\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"connId\":\"d76d3ad2-5d5d-453f-b0af-d6dc741f7bcb\",\"classifier\":\"db.generic\",\"driver\":{\"data\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\"},\"dataSourceProvider\":\"tomcat\",\"type\":\"dynamicDataSource\",\"permissionLevel\":5,\"driver\":\"org.sqlite.JDBC\",\"name\":\"SqlIteConnection\",\"classifier\":\"global\",\"dataSourceType\":\"Managed DataSource\"}},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"f8zak\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataOrderSwitchTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		//System.out.println("Cross Joins : "+responseObject);
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		System.out.println("Second Save : "+responseObject);
//		
//	}
//	
//	@Test
//	public void md_b2_secondSave_addTable_inSecond_ds() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"sync\":false,\"id\":\""+sqliteJdbcId+"\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"connId\":\"d76d3ad2-5d5d-453f-b0af-d6dc741f7bcb\",\"classifier\":\"db.generic\",\"driver\":{\"data\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\"},\"dataSourceProvider\":\"tomcat\",\"type\":\"dynamicDataSource\",\"permissionLevel\":5,\"driver\":\"org.sqlite.JDBC\",\"name\":\"SqlIteConnection\",\"classifier\":\"global\",\"dataSourceType\":\"Managed DataSource\"}},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"f8zak\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataOrderSwitchTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		//System.out.println("Cross Joins : "+responseObject);
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		System.out.println("Add Table : "+responseObject);
//		
//	}
//	
//	//@Test //Mysql
//	public void md_b3_createThirdDataSourceConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"com.mysql.cj.jdbc.Driver\",\"name\":\"MysqlDynamicConnection\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""
//				+ mysqlDbName + "\",\"jdbcUrl\":\"" + mysqlUrl + "\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//				.jsonPath("$.response.message").value("A new Tomcat data source is created successfully."))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		JSONObject data = responseObject.getJSONObject("data");
//		mysqlJdbcId = data.getString("id");
//	}
//	
//	//@Test
//	public void md_b4_mysql_expand_catalog_schema() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\"" + mysqlJdbcId
//				+ "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	
//	//@Test
//	public void md_b5__mysql_expand_table() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"id\":\""+mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"SampleTravelData\",\"schemas\":[]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	
//	//@Test
//	public void md_b6_metadata_with_3connections() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ecpxm\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"SampleTravelData\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"SampleTravelData\",\"schema\":\"\",\"connId\":\"ule5s\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Mysql\",\"database\":\"SampleTravelData\",\"databaseType\":\"Mysql\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"152371825108bf241d5e58d460282bf0\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}},{\"database\":\"Sqllite\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"dlqn7\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Sqllite\",\"database\":\"Sqllite\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"f2ff93c37589ef57f40dcb15fda6d7ea\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MetadataWith3Connections\",\"location\":\"MetadataOrderSwitchTest\",\"metadataReload\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		//System.out.println("Cross Joins : "+responseObject);
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 2);
//		System.out.println(responseObject);
//		
//	}
//	
//	//@Test
//	public void md_b7_secondSave_addNewConnection() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"sync\":false,\"id\":\""+sqliteJdbcId+"\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"connId\":\"d76d3ad2-5d5d-453f-b0af-d6dc741f7bcb\",\"classifier\":\"db.generic\",\"driver\":{\"data\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\"},\"dataSourceProvider\":\"tomcat\",\"type\":\"dynamicDataSource\",\"permissionLevel\":5,\"driver\":\"org.sqlite.JDBC\",\"name\":\"SqlIteConnection\",\"classifier\":\"global\",\"dataSourceType\":\"Managed DataSource\"}},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"f8zak\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}},{\"database\":\"SampleTravelData\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+mysqlJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"SampleTravelData\",\"schema\":\"\",\"connId\":\"ule5s\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Mysql\",\"database\":\"SampleTravelData\",\"databaseType\":\"Mysql\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"152371825108bf241d5e58d460282bf0\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataOrderSwitchTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		//System.out.println("Cross Joins : "+responseObject);
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		System.out.println(responseObject);
//		Assert.assertTrue(connectionsArray.size() == 2);
//	}
//	
//	
//
//	@Test
//	public void md_b7_deleteFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MetadataOrderSwitchTest\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//}
