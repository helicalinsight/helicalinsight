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
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
//
//public class HiAndUserModuleTest3 {
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
//	@Autowired
//	private EFWController efwController;
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//
//	@Test
//	public void hi_a1_create_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "create4share");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("create4share", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("create4share", name);
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
//	public void hi_a2_to_create_metadata_expand_catlog_schema() throws Exception {
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
//	public void hi_a3_expand_table() throws Exception {
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
//	static String metadataId;
//	@Test
//	public void hi_a4_create_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"45ee06374e9d68c4a841d57c1be69f22_1676454002087\",\"action\":\"noChange\"},{\"id\":\"c113fa06a79370db6feb443c0023f531_1676454002087\",\"action\":\"noChange\"},{\"id\":\"55a5cfab25247c48f9776bf9bd457a3c_1676454002087\",\"action\":\"noChange\"},{\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9_1676454002087\",\"action\":\"noChange\"},{\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22_1676454002087\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"q73r2\",\"dbId\":\"q73r2\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataSave\",\"location\":\"create4share\",\"metadataReload\":true}");
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
//		JSONObject object = responseObject.getJSONObject("metadata").getJSONObject("dataSource");
//		metadataId = object.getString("id");
//		
//		
//	}
//
//	@Test
//	public void hi_a5_get_metadata_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "get");
//		map.put("formData",
//				"{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		
//	}
//	@Test
//	public void hi_a6_fetch_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "fetchData");
//		map.put("formData",
//				"{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		
//		
//	}
//	
//	@Test
//	public void hi_a7_save_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "saveReport");
//		map.put("formData",
//				"{\"isHrReport\":true,\"columns\":[{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"15a1d579-a3f9-47a1-becd-82ddda3164e8\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"meeting_details.client_name\",\"label\":\"client_name\",\"id\":\"42b7124e-5885-414b-8ca5-f1ab607caa81\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"client_name\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"client_name\"},{\"column\":\"employee_details.employee_id\",\"label\":\"sum_employee_id\",\"id\":\"2b3f47e2-3e7d-4cec-b5c1-43b1717b1707\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_id\"}],\"state\":{\"fields\":[{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"15a1d579-a3f9-47a1-becd-82ddda3164e8\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"meeting_details.client_name\",\"label\":\"client_name\",\"id\":\"42b7124e-5885-414b-8ca5-f1ab607caa81\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"client_name\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"client_name\"},{\"column\":\"employee_details.employee_id\",\"label\":\"sum_employee_id\",\"id\":\"2b3f47e2-3e7d-4cec-b5c1-43b1717b1707\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_id\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"285d8b20-4c7c-41e6-ade1-14e7482dc068\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_employee_id\",\"id\":\"2b3f47e2-3e7d-4cec-b5c1-43b1717b1707\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"285d8b20-4c7c-41e6-ade1-14e7482dc068\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"SyncChart\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\"},\"reportName\":\"saveReport\",\"location\":\"create4share\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response");
//		String location = object.getString("location");
//		Assert.assertEquals("create4share", location);
//		String reportName = object.getString("uuid");
//		Assert.assertEquals("saveReport.hr",reportName);
//		JSONObject obj = object.getJSONArray("data").getJSONObject(0);
//		String path = obj.getString("path");
//		Assert.assertEquals("create4share/saveReport.hr", path);
//		String level = obj.getString("permissionLevel");
//		Assert.assertEquals("5", level);
//		String type = obj.getString("type");
//		Assert.assertEquals("file", type);
//		
//	}
//	@Test
//	public void hi_a8_fetch_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "fetchData");
//		map.put("formData",
//				"{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		
//		
//	}
//	
//	@Test
//	public void hi_a9_schedule_weekly_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"pdf\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"<p>report</p>\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302201009");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[\"Sunday\"],\"Frequency\":\"Weekly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2023-03-24\",\"EndDate\":\"2023-02-20\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"11:40:25\",\"ScheduledEndTime\":\"10:25:25\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//		
//	}
//	@Test
//	public void hi_b1_schedule_daily_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"pdf\",\"csv\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302201052");
//		map.put("adhocFormData", "{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"8f2f78ce-7763-411a-a48e-a941b3da4887\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Daily\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2023-03-24\",\"EndDate\":\"2023-02-20\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"12:06:53\",\"ScheduledEndTime\":\"11:07:53\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//		
//	}
//	
//	@Test
//	public void hi_b2_schedule_monthly_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"pdf\",\"csv\",\"xls\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302201117");
//		map.put("adhocFormData", "{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"8f2f78ce-7763-411a-a48e-a941b3da4887\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Monthly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2023-03-24\",\"EndDate\":\"2023-02-20\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"11:31:50\",\"ScheduledEndTime\":\"11:32:50\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//		
//	}
//
//	@Test
//	public void hi_b3_schedule_yearly_report() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"csv\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302201121");
//		map.put("adhocFormData", "{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"8f2f78ce-7763-411a-a48e-a941b3da4887\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Yearly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2023-03-24\",\"EndDate\":\"2023-02-20\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"11:36:03\",\"ScheduledEndTime\":\"11:37:03\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//		
//	}
//	
//	@Test
//	public void hi_b4_getListOFSchedule() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "monitor");
//		map.put("serviceType", "scheduling");
//		map.put("service", "schedule");
//		map.put("formData","{\"action\":\"list\"}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response");
//		
//		JSONArray array = object.getJSONArray("scheduledList");
//		JSONObject scheduleObject = array.getJSONObject(0);
//		Assert.assertTrue(scheduleObject.containsValue("Weekly"));
//		scheduleObject = array.getJSONObject(1);
//		Assert.assertTrue(scheduleObject.containsValue("Daily"));
//		scheduleObject = array.getJSONObject(2);
//		Assert.assertTrue(scheduleObject.containsValue("Monthly"));
//		scheduleObject = array.getJSONObject(3);
//		Assert.assertTrue(scheduleObject.containsValue("Yearly"));
//		
//	}
//	
//	static int orgId;
//	@Test
//	public void hi_b5_create_organization_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/organisations");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"name\":\"xyzOrg\",\"description\":\"xyz\"}");
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
//		
//		
//	}
//	static int roleUserId;
//	static int roleAdminId;
//	@Test
//    public void hi_b6_get_roles() throws Exception {
//    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/admin/roles");
//    	Map<String, String> map = new HashMap<>();
//    	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object1 = jsonObject.getJSONArray("roles").getJSONObject(0);
//		Assert.assertTrue(object1.containsValue("ROLE_USER"));
//		roleUserId = object1.getInt("id");
//		JSONObject object2 = jsonObject.getJSONArray("roles").getJSONObject(1);
//		Assert.assertTrue(object2.containsValue("ROLE_ADMIN"));
//		roleAdminId = object2.getInt("id");
//		
//    }
//    
//	
//	static int userAdminId;
//	@Test
//	public void hi_b7_create_testAdmin() throws Exception {
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
//		
//		userAdminId = jsonObject.getJSONObject("response").getInt("id");
//		
//
//	}
//
//	@Test
//	public void hi_b8_role_update_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "update");
//		map.put("formData", "{\"id\":"+userAdminId+",\"name\":\"testAdmin\",\"email\":\"testAdmin@gmail.com\",\"enabled\":true,\"roleIds\":["+roleUserId+","+roleAdminId+"],\"password\":\"\"}");
//		map.put("id",""+userAdminId+"");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User updated successfully. ")).andReturn();
//		
//	}
//	@Test
//    public void hi_b9_get_users() throws Exception {
//    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/admin/users");
//    	Map<String, String> map = new HashMap<>();
//    	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		
//		
//		
//    }
//	
//	static int userId;
//	@Test
//	public void hi_c1_create_testUser() throws Exception {
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
//	@Test
//	public void hi_c2_retrieveSharedInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","retrieveSharedInfo");
//		map.put("formData","{\"classifier\":\"global\",\"id\":\"1\",\"type\":\"dataSource\",\"dataSourceProvider\":\"tomcat\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	@Test
//	public void hi_c3_share_datasource_to_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData","{\"classifier\":\"global\",\"id\":\"1\",\"type\":\"dataSource\",\"dataSourceProvider\":\"tomcat\",\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":5}]}}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
//	
//		
//	}
//	@Test
//	public void hi_c4_retrieveSharedInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","retrieveSharedInfo");
//		map.put("formData","{\"type\":\"folder\",\"dir\":\"create4share\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	@Test
//	public void hi_c5_share_folder_to_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service", "update");
//		map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"create4share\"}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//		
//	}
//	@Test
//	public void hi_c6_getSolutionResources_in_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testAdmin");
//		map.put("password", "testAdmin");
//		map.put("j_organization", "xyzOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0).getJSONArray("children").getJSONObject(4);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share/saveReport.hr",path);
//		String permissionLevel = object.getString("permissionLevel");
//		Assert.assertEquals("5",permissionLevel);
//		Assert.assertTrue(object.getBoolean("isVisible"));
//		String inherit = object.getString("inherit");
//		Assert.assertEquals("true",inherit);
//		
//		JSONObject object1 = nameArray.getJSONObject(0).getJSONArray("children").getJSONObject(5);
//		String path1 = object1.getString("path");
//		Assert.assertEquals("create4share/MetadataSave.metadata",path1);
//		String permissionLevel1 = object1.getString("permissionLevel");
//		Assert.assertEquals("5",permissionLevel1);
//		Assert.assertTrue(object1.getBoolean("isVisible"));
//		String inherit1 = object1.getString("inherit");
//		Assert.assertEquals("true",inherit1);
//		
//		
//	}
//	
//	@Test
//	public void hi_c7_getSolutionResources_in_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "testUser");
//		map.put("password", "testUser");
//		map.put("j_organization", "xyzOrg");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		JSONObject object = nameArray.getJSONObject(0).getJSONArray("children").getJSONObject(4);
//		String path = object.getString("path");
//		Assert.assertEquals("create4share/saveReport.hr",path);
//		String permissionLevel = object.getString("permissionLevel");
//		Assert.assertEquals("5",permissionLevel);
//		Assert.assertTrue(object.getBoolean("isVisible"));
//		String inherit = object.getString("inherit");
//		Assert.assertEquals("true",inherit);
//		
//		JSONObject object1 = nameArray.getJSONObject(0).getJSONArray("children").getJSONObject(5);
//		String path1 = object1.getString("path");
//		Assert.assertEquals("create4share/MetadataSave.metadata",path1);
//		String permissionLevel1 = object1.getString("permissionLevel");
//		Assert.assertEquals("5",permissionLevel1);
//		Assert.assertTrue(object1.getBoolean("isVisible"));
//		String inherit1 = object1.getString("inherit");
//		Assert.assertEquals("true",inherit1);
//		
//		
//	}
//	@Test
//	public void hi_c8_get_report_in_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "getReport");
//		map.put("formData","{\"dir\":\"create4share\",\"file\":\"saveReport.hr\"}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONObject("canvas").getJSONArray("columns");
//		JSONObject object = jsonArray.getJSONObject(0);
//		String column = object.getString("column");
//		Assert.assertEquals("travel_details.booking_platform",column);
//		JSONObject object2 = jsonArray.getJSONObject(1);
//		String column1 = object2.getString("column");
//		Assert.assertEquals("meeting_details.client_name",column1);
//		JSONObject object3 = jsonArray.getJSONObject(2);
//		String column2 = object3.getString("column");
//		Assert.assertEquals("employee_details.employee_id",column2);
//		
//	}
//	@Test
//	public void hi_c9_fetchData_in_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "report");
//		map.put("service", "fetchData");
//		map.put("formData","{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0);
//		JSONObject object = obj.getJSONObject("1");
//		String name1 = object.getString("name");
//		Assert.assertEquals("booking_platform",name1);
//		JSONObject object2 = obj.getJSONObject("2");
//		String name2 = object2.getString("name");
//		Assert.assertEquals("client_name",name2);
//		JSONObject object3 = obj.getJSONObject("3");
//		String name3 = object3.getString("name");
//		Assert.assertEquals("sum_employee_id",name3);
//		
//	}
//	
//	@Test
//	public void hi_d1_schedule_daily_report_in_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"pdf\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"<p>daily</p>\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302221859");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Daily\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2023-03-25\",\"EndDate\":\"2023-02-25\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"11:55:31\",\"ScheduledEndTime\":\"11:56:31\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//	}
//	@Test
//	public void hi_d2_schedule_weekly_report_in_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"csv\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"<p>testtest</p>\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302221159");
//		map.put("adhocFormData","{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"bfc66d98-2b53-4f8e-8594-0c946986f446\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[\"Monday\"],\"Frequency\":\"Weekly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":2,\"StartDate\":\"2023-03-25\",\"EndDate\":\"2023-02-25\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"12:13:00\",\"ScheduledEndTime\":\"12:14:00\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//	}
//	
//	@Test
//	public void hi_d3_schedule_monthly_report_in_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"xls\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302221204");
//		map.put("adhocFormData","{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"bfc66d98-2b53-4f8e-8594-0c946986f446\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Monthly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":3,\"StartDate\":\"2023-03-25\",\"EndDate\":\"2023-02-22\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"12:19:08\",\"ScheduledEndTime\":\"12:20:08\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//	}
//	@Test
//	public void hi_d4_schedule_yearly_report_in_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport.html");
//		Map<String, String> map = new HashMap<>();
//		map.put("command", "add");
//		map.put("reportDirectory", "create4share");
//		map.put("reportFile", "saveReport.hr");
//		map.put("location","create4share");
//		map.put("EmailSettings","{\"Formats\":[\"pdf\",\"csv\",\"xls\"],\"Recipients\":[\"amar@hi.com\"],\"Zip\":false,\"Subject\":\"test\",\"Body\":\"\"}");
//		map.put("isActive","true");
//		map.put("reportParameters","{\"mode\":\"dashboard\"}");
//		map.put("reportName","saveReport 2302221208");
//		map.put("adhocFormData","{\"location\":\"create4share\",\"metadataFileName\":\"MetadataSave.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.client_name\",\"alias\":\"client_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_id\",\"alias\":\"sum_employee_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.employee_details.employee_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_employee_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"client_name\",\"custom\":true}]},\"limitBy\":1000,\"prependTableNameToAlias\":false,\"requestId\":\"bfc66d98-2b53-4f8e-8594-0c946986f446\",\"isAdhoc\":true,\"requestType\":\"adhoc\",\"serviceType\":\"report\",\"service\":\"fetchData\"}");
//		map.put("ScheduleOptions","{\"DaysofWeek\":[],\"Frequency\":\"Yearly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":2,\"StartDate\":\"2023-03-25\",\"EndDate\":\"2023-02-25\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"12:22:50\",\"ScheduledEndTime\":\"12:23:50\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		JSONObject object = jsonObject.getJSONObject("response");
//		String message = object.getString("message");
//		Assert.assertEquals("Successfully scheduled the report", message);
//		
//	}
//	@Test
//	public void hi_d5_getListOFSchedule() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		
//		map.put("type", "monitor");
//		map.put("serviceType", "scheduling");
//		map.put("service", "schedule");
//		map.put("formData","{\"action\":\"list\"}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response");
//		
//		JSONArray array = object.getJSONArray("scheduledList");
//		JSONObject scheduleObject = array.getJSONObject(4);
//		String reportName = scheduleObject.getString("scheduledSaveReportName");
//		Assert.assertEquals("saveReport 2302221859", reportName);
//		Assert.assertTrue(scheduleObject.containsValue("Daily"));
//		
//		scheduleObject = array.getJSONObject(5);
//		reportName = scheduleObject.getString("scheduledSaveReportName");
//		Assert.assertEquals("saveReport 2302221159", reportName);
//		Assert.assertTrue(scheduleObject.containsValue("Weekly"));
//		JSONArray day = scheduleObject.getJSONArray("daysofWeek"); 
//		JSONArray dayArray=new JSONArray();
//		dayArray.add("Monday");
//		Assert.assertEquals(dayArray,day);
//		
//		scheduleObject = array.getJSONObject(6);
//		reportName = scheduleObject.getString("scheduledSaveReportName");
//		Assert.assertEquals("saveReport 2302221204", reportName);
//		Assert.assertTrue(scheduleObject.containsValue("Monthly"));
//		
//		scheduleObject = array.getJSONObject(7);
//		reportName = scheduleObject.getString("scheduledSaveReportName");
//		Assert.assertEquals("saveReport 2302221208", reportName);
//		Assert.assertTrue(scheduleObject.containsValue("Yearly"));
//		
//	}
//	@Test
//	public void hi_d6_delete_org() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/organisations");
//	
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//
//		map.put("id", ""+orgId+"");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Organization deleted successfully "));
//	}
//	@Test
//	public void hi_d7_delete_folder() throws Exception {
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
//	}
