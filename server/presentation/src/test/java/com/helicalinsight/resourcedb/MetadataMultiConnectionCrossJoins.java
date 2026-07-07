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
//import org.apache.commons.lang.StringUtils;
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
//public class MetadataMultiConnectionCrossJoins {
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
//
//	private static String jdbcUrl = "";
//	private static String firstJdbcId = "";
//	private static String dbName = "";
//	private static String sqliteJdbcUrl = "";
//	private static String sqliteJdbcId = "";
//	private static String sqliteDbName = "";
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
//		map.put("folderName", "MetadataJoinsTest");
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
//						.value("A new Tomcat data source is created successfully."))
//				.andReturn();
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
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
//				.andReturn();
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
//	@Test
//	public void md_a9_fetchFirstTableColumns() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "fetchColumns");
//		map.put("formData", "{\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"57jjn\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"2ply-f8zk-4aw2-yenr-wm/giwf-1zfm-1y66-d8qp-e3/549c-so5p-8bu4-gs0b-jr\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	
//	@Test
//	public void md_b1_fetchSecondTableColumns() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "fetchColumns");
//		map.put("formData", "{\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"m55b6\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"z9bb-yw47-wfvg-w7gj-24/rg2q-l16m-y3wx-oerf-vk\",\"driverType\":\"Sqlite\",\"database\":\"Sqlite\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"\",\"table\":\"employee_details\"},\"refresh\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	
//	static String joinId = "";
//	
//	@Test
//	public void md_b2_createMetadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"2r6aw\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"joins\":[{\"type\":\"inner\",\"operator\":\"=\",\"left\":{\"table\":\"dimdate\",\"column\":\"dim_id\",\"alias\":\"dimdate.dim_id\",\"dbId\":\"random1\"},\"right\":{\"table\":\"employee_details\",\"column\":\"employee_id\",\"alias\":\"employee_details.employee_id\",\"dbId\":\"random2\"},\"key\":\"sr9m-q2k3-5g70-hznu-xj\",\"uuid\":\"sr9m-q2k3-5g70-hznu-xj\",\"action\":\"add\",\"index\":1}],\"connections\":[{\"database\":\"SqliteSampleDS\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"nn3tj\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SqliteSampleDS\",\"database\":\"SqliteSampleDS\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"b161910cbebfd353351a6c0b46e6a02e\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		Assert.assertTrue(responseObject.getJSONObject("metadata").containsKey("joins"));
//		JSONObject joinObject = responseObject.getJSONObject("metadata").getJSONArray("joins").getJSONObject(0);
//		joinId = joinObject.getString("id");
//		Assert.assertTrue(responseObject.getJSONObject("metadata").getJSONObject("dataSource").containsKey("dbId"));
//		Assert.assertTrue(StringUtils.isNotBlank(joinId));
//		Assert.assertTrue(joinObject.getJSONObject("left").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("left").getString("dbId")));
//		Assert.assertTrue(joinObject.getJSONObject("left").containsKey("tableId") && StringUtils.isNotBlank(joinObject.getJSONObject("left").getString("tableId")));
//		Assert.assertTrue(joinObject.getJSONObject("right").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("right").getString("dbId")));
//		Assert.assertTrue(joinObject.getJSONObject("right").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("right").getString("dbId")));
//		
//	}
//	
//	
//
//	
//	@Test
//	public void md_b3_noChange_cross_joins() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"SqliteDs\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\""+joinId+"\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"9nrxv\",\"classifier\":\"db.workflow\",\"database\":\"SqliteDs\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"sampletraveldata\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"sampletraveldata\",\"schema\":\"\",\"connId\":\"nsvb9\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Mysql\",\"database\":\"sampletraveldata\",\"databaseType\":\"Mysql Cj\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		Assert.assertTrue(responseObject.getJSONObject("metadata").containsKey("joins"));
//		JSONObject joinObject = responseObject.getJSONObject("metadata").getJSONArray("joins").getJSONObject(0);
//		joinId = joinObject.getString("id");
//		Assert.assertTrue(StringUtils.isNotBlank(joinId));
//		Assert.assertTrue(joinObject.getJSONObject("left").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("left").getString("dbId")));
//		Assert.assertTrue(joinObject.getJSONObject("left").containsKey("tableId") && StringUtils.isNotBlank(joinObject.getJSONObject("left").getString("tableId")));
//		Assert.assertTrue(joinObject.getJSONObject("right").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("right").getString("dbId")));
//		Assert.assertTrue(joinObject.getJSONObject("right").containsKey("dbId") && StringUtils.isNotBlank(joinObject.getJSONObject("right").getString("dbId")));
//		
//	}
//	
//	@Test
//	public void md_b4_update_cross_joins() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"SqliteDs\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"type\":\"inner\",\"operator\":\"=\",\"left\":{\"table\":\"dimdate\",\"column\":\"dim_id\",\"alias\":\"dimdate.dim_id\",\"dbId\":\"random1\"},\"right\":{\"table\":\"employee_details\",\"column\":\"employee_name\",\"alias\":\"employee_details.employee_name\",\"dbId\":\"random2\"},\"key\":\"4zzy-ar0a-lp4m-morc-cq\",\"uuid\":\"4zzy-ar0a-lp4m-morc-cq\",\"action\":\"add\",\"index\":1},{\"id\":\""+joinId+"\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"9nrxv\",\"classifier\":\"db.workflow\",\"database\":\"SqliteDs\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"sampletraveldata\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"sampletraveldata\",\"schema\":\"\",\"connId\":\"nsvb9\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Mysql\",\"database\":\"sampletraveldata\",\"connectionDatabaseId\":\"\",\"databaseType\":\"Mysql Cj\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		Assert.assertNotNull(responseObject.getJSONObject("metadata").getJSONArray("joins"));
//		Assert.assertTrue(responseObject.getJSONObject("metadata").getJSONArray("joins").size() == 2);
//	}
//	
//	@Test
//	public void md_b6_delete_cross_joins() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"SqliteDs\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\""+joinId+"\",\"action\":\"delete\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"9nrxv\",\"classifier\":\"db.workflow\",\"database\":\"SqliteDs\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"sampletraveldata\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"nsvb9\",\"classifier\":\"db.workflow\",\"datasourceName\":\"Derby\",\"dsKeyPath\":\"rmq6-h9jz-wlxy-28k8-2i/tas5-eanl-77uh-d5g5-0i/g3sm-3yum-ojvp-uui9-sc\",\"driverType\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//		JSONArray connectionsArray = responseObject.getJSONObject("metadata").getJSONArray("connections");
//		Assert.assertTrue(connectionsArray.size() == 1);
//		Assert.assertNotNull(responseObject.getJSONObject("metadata").getJSONArray("joins"));
//		Assert.assertTrue(responseObject.getJSONObject("metadata").getJSONArray("joins").size() == 1);
//	}
//	
//
//	@Test
//	public void md_b7_deleteFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MetadataJoinsTest\"]";
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
