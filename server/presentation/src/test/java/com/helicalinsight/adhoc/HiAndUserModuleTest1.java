//package com.helicalinsight.adhoc;
//
//import java.util.HashMap;
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
//import org.springframework.http.MediaType;
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
//import com.helicalinsight.efw.controller.EFWController;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import net.sf.json.JSONSerializer;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class HiAndUserModuleTest1 {
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
//	private EFWController efwController;
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//	@Test
//	public void hi_a1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "TestFolder");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("TestFolder", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("TestFolder", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_a2_rename_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"TestFolder\",\"Test\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	
//	@Test
//	public void hi_a3_delete_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"TestFolder\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//	//nested folder
//	
//	@Test
//	public void hi_a4_create_a_nestedfolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "MainFolder");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("MainFolder", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("MainFolder", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	
//	
//	@Test
//	public void hi_a5_create_a_subfolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MainFolder\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "SubFolder");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		
//		String path = object.getString("path");
//		Assert.assertEquals("MainFolder/SubFolder", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("SubFolder", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	
//	
//	@Test
//	public void hi_a6_create_a_thirdfolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MainFolder/SubFolder\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "ThirdFolder");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		
//		String path = object.getString("path");
//		Assert.assertEquals("MainFolder/SubFolder/ThirdFolder", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("ThirdFolder", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	
//	@Test
//	public void hi_a7_create_a_forthfolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MainFolder/SubFolder/ThirdFolder\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "FourthFolder");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//
//		String path = object.getString("path");
//		Assert.assertEquals("MainFolder/SubFolder/ThirdFolder/FourthFolder", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("FourthFolder", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	
//	//hierarchy folder
//	@Test
//	public void hi_a8_hierarchy_a_subfolder2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"MainFolder\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "SubFolder2");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		
//		String path = object.getString("path");
//		Assert.assertEquals("MainFolder/SubFolder2", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("SubFolder2", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	
//	@Test
//	public void hi_a9_to_create_metadata_expand_catlog_schema() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData",
//				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void hi_b1_expand_table() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData",
//				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	@Test
//	public void hi_b2_create_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"r9xne\",\"dbId\":\"r9xne\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_save\",\"location\":\"MainFolder/SubFolder\",\"metadataReload\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//			Assert.assertEquals(1, status);
//			Assert.assertEquals("Successfully saved metadata file", message);
//		}
//	}
//
//	@Test
//	public void hi_b3_get_metadata_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "get");
//		map.put("formData",
//				"{\"location\":\"MainFolder/SubFolder\",\"metadataFileName\":\"Metadata_save.metadata\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		String dir = jsonObject.getJSONObject("response").getString("metadataDir");
//		Assert.assertEquals("MainFolder/SubFolder", dir);
//		
//	}
//	@Test
//	public void hi_b4_fetch_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "fetchData");
//		map.put("formData",
//				"{\"location\":\"MainFolder/SubFolder\",\"metadataFileName\":\"Metadata_save.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.geo_cordinates.location\",\"alias\":\"location\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_by\",\"alias\":\"sum_meeting_by\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"sum_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"},{\"column\":\"HIUSER.meeting_details.meeting_by\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_meeting_by\"},{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_travel_cost\"}],\"groupBy\":[{\"column\":\"created_date\",\"custom\":true},{\"column\":\"location\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		
//		
//	}
//	
//	@Test
//	public void hi_b5_save_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "saveReport");
//		map.put("formData",
//				"{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.created_date\",\"label\":\"created_date\",\"id\":\"18bba73a-e9d2-4760-81f4-4430677b8e59\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_date\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_date\"},{\"column\":\"employee_details.employee_id\",\"label\":\"sum_employee_id\",\"id\":\"921aed6e-8c3b-4741-ab37-8e448e1d528a\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_id\"},{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"9ceae406-324f-451a-b057-45cf459e2267\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"tableAlias\":\"geo_cordinates\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"location\"},{\"column\":\"meeting_details.meeting_by\",\"label\":\"sum_meeting_by\",\"id\":\"3db8e4f2-4764-4d7e-a9fc-c1c83b789f92\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_meeting_by\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"meeting_by\"},{\"column\":\"travel_details.travel_cost\",\"label\":\"sum_travel_cost\",\"id\":\"de9854af-7534-4a7c-913d-b651207f544f\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_travel_cost\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"travel_cost\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.created_date\",\"label\":\"created_date\",\"id\":\"18bba73a-e9d2-4760-81f4-4430677b8e59\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"created_date\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"created_date\"},{\"column\":\"employee_details.employee_id\",\"label\":\"sum_employee_id\",\"id\":\"921aed6e-8c3b-4741-ab37-8e448e1d528a\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_id\"},{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"9ceae406-324f-451a-b057-45cf459e2267\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"tableAlias\":\"geo_cordinates\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"location\"},{\"column\":\"meeting_details.meeting_by\",\"label\":\"sum_meeting_by\",\"id\":\"3db8e4f2-4764-4d7e-a9fc-c1c83b789f92\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_meeting_by\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"meeting_by\"},{\"column\":\"travel_details.travel_cost\",\"label\":\"sum_travel_cost\",\"id\":\"de9854af-7534-4a7c-913d-b651207f544f\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_travel_cost\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"travel_cost\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"64a63403-5660-4f4f-8f54-b05c3dad4aaf\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_employee_id\",\"id\":\"921aed6e-8c3b-4741-ab37-8e448e1d528a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_meeting_by\",\"id\":\"3db8e4f2-4764-4d7e-a9fc-c1c83b789f92\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_travel_cost\",\"id\":\"de9854af-7534-4a7c-913d-b651207f544f\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"64a63403-5660-4f4f-8f54-b05c3dad4aaf\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"SyncChart\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"MainFolder/SubFolder\",\"metadataFileName\":\"Metadata_save.metadata\"},\"reportName\":\"Report\",\"location\":\"MainFolder/SubFolder2\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response");
//		String location = object.getString("location");
//		Assert.assertEquals("MainFolder/SubFolder2", location);
//		String reportName = object.getString("uuid");
//		Assert.assertEquals("Report.hr",reportName);
//		JSONObject obj = object.getJSONArray("data").getJSONObject(0);
//		String path = obj.getString("path");
//		Assert.assertEquals("MainFolder/SubFolder2/Report.hr", path);
//		String level = obj.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//		String type = obj.getString("type");
//		Assert.assertEquals("file", type);
//		
//	}
//	
//	
//	//name validation with special character
//	
//	@Test
//	public void hi_b7_folder_name_with_special_charter() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "folder@Test$");
//		map.put("sourceArray",input);
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("folder_Test_", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("folder@Test$", name);
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b8_folder_with_3_specialCharacter() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "A@#$folder%%%");
//		map.put("sourceArray",input);
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("A_folder_", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("A@#$folder%%%", name);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//
//	}
//	@Test
//	public void hi_b9_folder_name_with_lessthan60Character() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "indiaismycountryallindiansarebrotherandsisterandilovemycount");
//		map.put("sourceArray",input);
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("indiaismycountryallindiansarebrotherandsisterandilovemycount", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("indiaismycountryallindiansarebrotherandsisterandilovemycount", name);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//
//	}
//	
//
////=========================================================================================================================
//	/**
//	 * share related folder to share with org,user,role
//	 */
//	static int orgId;
//
//	@Test
//	public void hi_c1_create_organization_testShare() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/organisations");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData", "{\"name\":\"testShare\",\"description\":\"shareFolder\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Organization added successfully"))
//				.andReturn();
//
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		orgId = jsonObject.getJSONObject("response").getInt("id");
//
//	}
//
//	static int roleUserId;
//	static int roleAdminId;
//
//	@Test
//	public void hi_c2_get_roles() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/roles");
//		Map<String, String> map = new HashMap<>();
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object1 = jsonObject.getJSONArray("roles").getJSONObject(0);
//		Assert.assertTrue(object1.containsValue("ROLE_USER"));
//		roleUserId = object1.getInt("id");
//		JSONObject object2 = jsonObject.getJSONArray("roles").getJSONObject(1);
//		Assert.assertTrue(object2.containsValue("ROLE_ADMIN"));
//		roleAdminId = object2.getInt("id");
//
//		System.out.println(roleUserId + "  " + roleAdminId);
//
//	}
//
//	static int userAdminId;
//
//	@Test
//	public void hi_c3_create_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData",
//				"{\"id\":\"\",\"email\":\"shareAdmin@gmail.com\",\"name\":\"shareAdmin\",\"enabled\":true,\"password\":\"shareAdmin\",\"organisation\":"+orgId+"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User created successfully."))
//				.andReturn();
//
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//
//		userAdminId = jsonObject.getJSONObject("response").getInt("id");
//
//	}
//
//	@Test
//	public void hi_c4_role_update_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "update");
//		map.put("formData",
//				"{\"id\":"+userAdminId+",\"name\":\"shareAdmin\",\"email\":\"shareAdmin@gmail.com\",\"enabled\":true,\"roleIds\":["+roleAdminId+","+roleUserId+"],\"password\":\"\"}");
//		map.put("id", "" + userAdminId + "");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User updated successfully. "));
//
//	}
//
//	@Test
//	public void hi_c5_get_users() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/users");
//		Map<String, String> map = new HashMap<>();
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		System.out.println(jsonObject);
//
//	}
//
//	static int userId;
//
//	@Test
//	public void hi_c6_create_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData",
//				"{\"id\":\"\",\"email\":\"shareUser@gmail.com\",\"name\":\"shareUser\",\"enabled\":true,\"password\":\"shareUser\",\"organisation\":"+orgId+"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User created successfully."))
//				.andReturn();
//
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		userId = jsonObject.getJSONObject("response").getInt("id");
//		System.out.println(userId + "  " + userAdminId);
//	}
//
//	@Test
//	public void hi_c7_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("create4share", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	}
//
//	@Test
//	public void hi_c8_share_retriveSharedInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//
//	@Test
//	public void hi_c9_share_with_testShare_readonly() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_d1_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("2",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_d2_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("2",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//		
//	}
//
//
//	@Test
//	public void hi_d3_share_with_testShare_read_write() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":3}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//    @Test
//	public void hi_d4_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("3",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_d5_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_d6_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("3",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_d7_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//
//	@Test
//	public void hi_d8_share_with_testShare_read_write_delete() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":4}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_d9_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("4",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename2", name);
//	}
//	@Test
//	public void hi_e1_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_e2_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("4",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_e3_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//
//	@Test
//	public void hi_e4_delete_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Delete operation is successful"));
//
//	}
//	
//	@Test
//	public void hi_e5_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//	}
//
//	@Test
//	public void hi_e6_share_with_testShare_read_write_delete_share() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":5}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_e7_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_e8_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_e9_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_f1_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	@Test
//	public void hi_f2_share_with_shareUser_read_write_delete_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	 @Test
//	 public void hi_f3_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("5",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4shareRename2", name);
//		}
//	@Test
//	public void hi_f4_delete_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	
//	@Test
//	public void hi_f5_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//
//	@Test
//	public void hi_f6_share_with_testorg_retrive() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//
//	@Test
//	public void hi_f7_share_with_testShare_noaccess() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_f8_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		System.out.println(nameArray);
//	
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//	@Test
//	public void hi_f9_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//
//	// role related
//	
//	
//	@Test
//	public void hi_g1_share_with_role_ROLE_ADMIN_readonly() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":2}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_g2_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("2",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4share", name);
//		}
//	@Test
//	public void hi_g3_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//	@Test
//	public void hi_g4_share_with_role_ROLE_ADMIN_read_write() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":3}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_g5_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("3",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4share", name);
//		}
//	@Test
//	public void hi_g6_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//	@Test
//	public void hi_g7_rename_a_folder_in_ROLE_ADMIN_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4Rename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	
//	@Test
//	public void hi_g8_share_with_role_ROLE_ADMIN_read_write_delete() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":4}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_g9_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("4",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4Rename", name);
//		}
//	@Test
//	public void hi_h1_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//	@Test
//	public void hi_h2_rename_a_folder_in_ROLE_ADMIN_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"createRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	@Test
//	public void hi_h3_delete_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	
//	@Test
//	public void hi_h4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//
//	@Test
//	public void hi_h5_share_with_retrive() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//	@Test
//	public void hi_h6_share_with_role_ROLE_ADMIN_read_write_delete_share() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_h7_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("5",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4share", name);
//		}
//	@Test
//	public void hi_h8_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		Assert.assertTrue(nameArray.isEmpty());
//		
//	}
//	@Test
//	public void hi_h9_rename_a_folder_in_ROLE_ADMIN_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"createRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	@Test
//	public void hi_i1_share_from_ROLE_Admin_to_ROLE_USER() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_i2_getSolutionResources_in_shareUser() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareUser");
//			map.put("password", "shareUser");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("5",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("createRename", name);
//		}
//	@Test
//	public void hi_i3_delete_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	
//	@Test
//	public void hi_i4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//
//	@Test
//	public void hi_i5_share_with_retrive() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//	@Test
//	public void hi_i6_share_with_role_ROLE_ADMIN_noaccess() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":0}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_i7_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			Assert.assertTrue(nameArray.isEmpty());
//		}
//
//// share to ROLE_USER
//	@Test
//	public void hi_i8_share_to_ROLE_USER_read() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":2}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//		
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_i9_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("2",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4share", name);
//		}
//	@Test
//	public void hi_j1_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		System.out.println("j1: "+nameArray);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("2",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//
//		
//	}
//	
//	@Test
//	public void hi_j2_share_with_ROLE_USER_read_write() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":3}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//    @Test
//	public void hi_j3_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("3",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_j4_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_j5_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("3",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_j6_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//
//	@Test
//	public void hi_j7_share_with_ROLE_USER_read_write_delete() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":4}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_j8_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("4",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename2", name);
//	}
//	@Test
//	public void hi_j9_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_k1_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("4",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_k2_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//
//	@Test
//	public void hi_k3_delete_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Delete operation is successful"));
//
//	}
//	
//	@Test
//	public void hi_k4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//	}
//
//	@Test
//	public void hi_k5_share_with_ROLE_USER_read_write_delete_share() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":5}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_k6_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_k7_rename_a_folder_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//    @Test
//	public void hi_k8_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//	@Test
//	public void hi_k9_rename_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//	}
//	@Test
//	public void hi_l1_share_with_ROLE_USER_read_write_delete_ROLE_ADMIN() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	 @Test
//	 public void hi_l2_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("5",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4shareRename2", name);
//		}
//	@Test
//	public void hi_l3_delete_a_folder_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
////	// user share
//
//	@Test
//	public void hi_l4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("create4share", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	}
//	@Test
//	public void hi_l5_share_with_retrive() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//	@Test
//	public void hi_l6_share_with_shareUser_noaccess() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_l7_getSolutionResources_in_shareUser() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			Assert.assertTrue(nameArray.isEmpty());
//		}
//	@Test
//	public void hi_l8_share_with_shareUser_read() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":2}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_l9_getSolutionResources_in_shareUser() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareUser");
//			map.put("password", "shareUser");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			JSONObject object = nameArray.getJSONObject(0);
//			String path = object.getString("path");
//			Assert.assertEquals("create4share", path);
//			String level = object.getString("permissionLevel");
//			Assert.assertEquals("2",level);
//			String type = object.getString("type");
//			Assert.assertEquals("folder", type);
//			String name = object.getString("name");
//			Assert.assertEquals("create4share", name);
//		}
//
//	@Test
//	public void hi_m1_getSolutionResources_in_shareAdmin() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//					.get("/getSolutionResourcesTest");
//			
//			Map<String, String> map = new HashMap<>();
//			map.put("username", "shareAdmin");
//			map.put("password", "shareAdmin");
//			map.put("j_organization", "testShare");
//			
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder)
//					.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//			String string = result.getResponse().getContentAsString();
//			JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//			
//			Assert.assertTrue(nameArray.isEmpty());
//			
//		}
//
//
//
//	@Test
//	public void hi_m2_share_with_shareUser_read_write() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":3}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_m3_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("3",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//
//	@Test
//	public void hi_m4_rename_folder_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4shareRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//
//	}
//
//	@Test
//	public void hi_m5_share_with_shareAdmin_read_write_delete() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":4}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_m6_getSolutionResources_in_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareAdmin");
//		map.put("password", "shareAdmin");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("4",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4shareRename", name);
//	}
//
//	@Test
//	public void hi_m7_rename_folder_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//
//	}
//
//	@Test
//	public void hi_m8_delete_folder_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	//
//
//	@Test
//	public void hi_m9_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//
//	@Test
//	public void hi_n1_share_retrive() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "retrieveSharedInfo");
//		map.put("formData", "{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected create4share is not shared with other users/roles/organizations."));
//
//	}
//
//	@Test
//	public void hi_n2_share_with_shareUser_executeonly() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":1}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//
//	@Test
//	public void hi_n3_share_with_shareUser_read_write_delete_share() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":1}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_n4_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
//	}
//	@Test
//	public void hi_n5_rename_folder_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"create4share\",\"create4\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"));
//
//	}
//	@Test
//	public void hi_n6_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		JSONObject object = nameArray.getJSONObject(0);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		String name = object.getString("name");
//		Assert.assertEquals("create4", name);
//	}
//	
//	@Test
//	public void hi_n7_share_with_shareUser_to_shareAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//
//	}
//	@Test
//	public void hi_n8_getSolutionResources_in_shareUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "shareUser");
//		map.put("password", "shareUser");
//		map.put("j_organization", "testShare");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//
//	
//		Assert.assertTrue(nameArray.isEmpty());
//	}
//	
//	// inheritance
//	
//	@Test
//	public void hi_n9_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "india");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("india", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Gujarat");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Gujarat", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o2_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Maharashatra");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Maharashatra", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o3_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "punjab");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("punjab", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Ahamdabad");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Ahamdabad", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Ahamdabad", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o5_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Gandinagar");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Gandinagar", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Gandinagar", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o6_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Surat");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Surat", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Surat", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o7_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Mumbai");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Mumbai", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Mumbai", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o8_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Pune");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Pune", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Pune", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_o9_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Ratanagiri");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Ratanagiri", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Ratanagiri", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_p1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "amritsar");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/amritsar", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("amritsar", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_p2_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "chandigad");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/chandigad", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("chandigad", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_p3_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "ludiyana");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/ludiyana", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("ludiyana", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	//========---------------
//	
//	@Test
//	public void hi_p4_create_organization_testOrg() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/organisations");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"name\":\"testOrg\",\"description\":\"njklkmnbvgfc\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("Organization added successfully")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		orgId = jsonObject.getJSONObject("response").getInt("id");
//		
//	}
//	
//	
//	@Test
//	public void hi_p5_create_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"id\":\"\",\"email\":\"testUser@gmail.com\",\"name\":\"testUser\",\"enabled\":true,\"password\":\"testUser\",\"organisation\":"+orgId+"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User created successfully.")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		userId = jsonObject.getJSONObject("response").getInt("id");
//		
//	}
//	static int userId2;
//	@Test
//	public void hi_p6_create_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"id\":\"\",\"email\":\"testAdmin@gmail.com\",\"name\":\"testAdmin\",\"enabled\":true,\"password\":\"testAdmin\",\"organisation\":"+orgId+"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User created successfully.")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		userId2 = jsonObject.getJSONObject("response").getInt("id");
//		
//	}
//	
//	@Test
//	public void hi_p7_fetchInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","fetchInfo");
//		map.put("formData","{\"provide\":{\"provideUsers\":\"true\",\"provideRoles\":\"true\",\"provideOrganizations\":\"true\",\"id\":\"all\"}}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}	
//	
//	@Test
//	public void hi_p8_retrieveSharedInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","retrieveSharedInfo");
//		map.put("formData","{\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	@Test
//	public void hi_p9_share_testUser_readonly() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_q1_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		System.out.println("builder=  "+nameArray);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("2", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		JSONObject object = array.getJSONObject(0);
//		String inherit = object.getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path1 = object.getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//
//		
//	}
//	@Test
//	public void hi_q2_share_testUser_readonly_inside_onefolder_noacess() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":0}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india/Maharashatra\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_q3_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("2", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Gujarat", path2);
//		
//	
//	}
//	@Test
//	public void hi_q4_share_testUser_revoke() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"india/Maharashatra\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_q5_share_testUser_read_write() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":3}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_q6_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("3", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
////++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	
//	@Test
//	public void hi_q7_rename_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"india\",\"indiaRename\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Rename is successful"));
//	
//	}
//	
//	
//	@Test
//	public void hi_q8_share_testuser_read_write_delete() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":4}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_q9_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("4", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
//	@Test
//	public void hi_r1_rename_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"india\",\"indiaRename2\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Rename is successful"));
//	
//	}
//	@Test
//	public void hi_r2_share_testuser_read_write_delete_share() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_r3_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("5", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
//	@Test
//	public void hi_r4_rename_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"india\",\"indiaRename3\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Rename is successful"));
//	
//	}
//	@Test
//	public void hi_r5_share_testuser_read_write_delete_share_to_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId2+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_r6_getSolutionResources_in_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testAdmin");
//		map.put("password", "testAdmin");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("5", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
//	@Test
//	public void hi_r7_rename_in_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[[\"india\",\"indiaRename4\"]]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "rename");
//	
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("Rename is successful"));
//	
//	}
//	@Test
//	public void hi_r8_share_to_revoke_from_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"revoke\":{\"user\":[{\"id\":"+userId2+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_r9_share_to_organization_testOrg() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","update");
//		map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":5}]},\"revoke\":{\"user\":[{\"id\":"+userId2+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//		
//	}
//	@Test
//	public void hi_s1_getSolutionResources_in_testUser2() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testAdmin");
//		map.put("password", "testAdmin");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("5", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
//	@Test
//	public void hi_s2_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "testOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("5", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		String path1 = array.getJSONObject(0).getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String inherit = array.getJSONObject(0).getString("inherit");
//		Assert.assertEquals("true", inherit);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//		
//	}
//	
//	@Test
//	public void hi_s3_delete_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"create4share\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//	@Test
//	public void hi_s4_delete_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//}
