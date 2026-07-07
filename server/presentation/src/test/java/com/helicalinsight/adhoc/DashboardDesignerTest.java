package com.helicalinsight.adhoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class DashboardDesignerTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void dd_a1_create_a_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "DashboardDesignerTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}
	@Test
	public void dd_a2_to_create_metadata_expand_catlog_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	@Test
	public void dd_a3_expand_table() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	static String id;
	static String dbid;
	@Test
	public void dd_a4_create_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"nu7uq\",\"dbId\":\"nu7uq\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_DashboardDesignerTest\",\"location\":\"DashboardDesignerTest\",\"metadataReload\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertTrue(responseObject.has("data"));
		JSONArray array = responseObject.getJSONArray("data");
		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			Assert.assertTrue(data.containsKey("lastModified"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("options"));
			Assert.assertTrue(data.containsKey("extension"));
			Assert.assertTrue(data.containsKey("path"));
			Assert.assertTrue(data.containsKey("permissionLevel"));
			Assert.assertTrue(data.containsKey("name"));
			Assert.assertTrue(data.containsKey("title"));
			
		}
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("dataSource");
		id = object.getString("id");
		dbid = object.getString("dbId");
	}
	@Test
	public void dd_a5_get_metadata() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "get");
		map.put("formData",
				"{\"location\":\"DashboardDesignerTest\",\"metadataFileName\":\"Metadata_DashboardDesignerTest.metadata\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		String metadataName = object.getString("metadataName");
		String dir = object.getString("metadataDir");
		Assert.assertEquals("Metadata_DashboardDesignerTest",metadataName);
		Assert.assertEquals("DashboardDesignerTest",dir);
	}
	@Test
	public void dd_a6_report_fetchData() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "fetchData");
		map.put("formData",
				"{\"location\":\"DashboardDesignerTest\",\"metadataFileName\":\"Metadata_DashboardDesignerTest.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.dim_id\",\"alias\":\"sum_dim_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.dimdate.fiscal_month_name\",\"alias\":\"fiscal_month_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.dimdate.dim_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_dim_id\"}],\"groupBy\":[{\"column\":\"fiscal_month_name\",\"custom\":true},{\"column\":\"address\",\"custom\":true},{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0);
		
		JSONObject obj1 = object.getJSONObject("1");
		String name1 = obj1.getString("name");
		Assert.assertEquals("sum_dim_id", name1);
		
		JSONObject obj2 = object.getJSONObject("2");
		String name2 = obj2.getString("name");
		Assert.assertEquals("fiscal_month_name", name2);
		
		JSONObject obj3 = object.getJSONObject("3");
		String name3 = obj3.getString("name");
		Assert.assertEquals("address", name3);
		
		JSONObject obj4 = object.getJSONObject("4");
		String name4 = obj4.getString("name");
		Assert.assertEquals("employee_name", name4);
	
	}

	@Test
	public void dd_a7_report_saveReport() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		map.put("formData",
				"{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"DashboardDesignerTest\",\"metadataFileName\":\"Metadata_DashboardDesignerTest.metadata\"},\"reportName\":\"Report_DashboardDesignerTest\",\"location\":\"DashboardDesignerTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		
		String name = object.getString("uuid");
		Assert.assertEquals("Report_DashboardDesignerTest.hr", name);
		
		
	}
	@Test
	public void dd_a8_report_saveReport2() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		map.put("formData",
				"{\"isHrReport\":true,\"columns\":[{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"ccb0e15e-3558-4050-a91c-efe695e69ebc\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"},{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"b1b6ed46-2292-4d6d-aae6-318f4603ddfb\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"View 1.destination\",\"label\":\"destination\",\"id\":\"b3cf1221-2e99-4e66-88b2-ccc56b83b002\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"destination\",\"isNormalTable\":false,\"tableAlias\":\"View 1\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"destination\"},{\"column\":\"meeting_details.client_name\",\"label\":\"client_name\",\"id\":\"52ab01d5-2c91-4f00-a8e5-e09e01cccd18\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"client_name\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"client_name\"}],\"state\":{\"fields\":[{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"ccb0e15e-3558-4050-a91c-efe695e69ebc\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"},{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"b1b6ed46-2292-4d6d-aae6-318f4603ddfb\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"View 1.destination\",\"label\":\"destination\",\"id\":\"b3cf1221-2e99-4e66-88b2-ccc56b83b002\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"destination\",\"isNormalTable\":false,\"tableAlias\":\"View 1\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"destination\"},{\"column\":\"meeting_details.client_name\",\"label\":\"client_name\",\"id\":\"52ab01d5-2c91-4f00-a8e5-e09e01cccd18\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"client_name\",\"isNormalTable\":true,\"tableAlias\":\"meeting_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"client_name\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"31c59902-e4b7-471b-b770-aba926975b2d\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"31c59902-e4b7-471b-b770-aba926975b2d\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"SyncChart\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"DashboardDesignerTest\",\"metadataFileName\":\"Metadata_DashboardDesignerTest.metadata\"},\"reportName\":\"Report2\",\"location\":\"DashboardDesignerTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		
		String name = object.getString("uuid");
		Assert.assertEquals("Report2.hr", name);
		
		
	}

	@Test
	public void dd_a9_get_report() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "getReport");
		map.put("formData",
				"{\"dir\":\"DashboardDesignerTest\",\"file\":\"Report_DashboardDesignerTest.hr\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		Assert.assertTrue(object.containsKey("data"));
		Assert.assertTrue(object.getJSONObject("data").containsKey("canvas"));
			
	}
	
	
	

	@Test
	public void dd_b1_get_report2() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "getReport");
		map.put("formData",
				"{\"dir\":\"DashboardDesignerTest\",\"file\":\"Report2.hr\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		Assert.assertTrue(object.containsKey("data"));
		Assert.assertTrue(object.getJSONObject("data").containsKey("canvas"));
	}
	
	@Test
	public void dd_b2_save_dashboard_desiginer() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData",
				"{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-JVDju\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-JVDju\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"Report_DashboardDesignerTest\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerTest/Report_DashboardDesignerTest.hr\",\"name\":\"Report_DashboardDesignerTest.hr\",\"title\":\"Report_DashboardDesignerTest\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"03caefb1-7a83-475e-899c-dcb8efc5f568\"},{\"id\":\"item-8FejN\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-8FejN\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"Report2\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerTest/Report2.hr\",\"name\":\"Report2.hr\",\"title\":\"Report2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"58820f7a-fc33-4509-85f4-79f5a8e921ac\",\"lastModified\":1675757755935}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[{\"key\":\"breakpoints\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":1200,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":996,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":768,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":480,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":240,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"columns\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":12,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":12,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":6,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":4,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":2,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"grid\",\"values\":{\"autoSize\":true,\"compactType\":null,\"rowHeight\":100,\"isDroppable\":false,\"preventCollision\":true,\"measureBeforeMount\":false,\"isDraggable\":true,\"isResizable\":true}},{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"<p><br></p>\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":0,\"yOffset\":0,\"blur\":0,\"spread\":0,\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}}],\"layout\":[{\"w\":3,\"h\":3,\"x\":2,\"y\":0,\"i\":\"item-JVDju\",\"moved\":false,\"static\":false},{\"w\":3,\"h\":3,\"x\":5,\"y\":0,\"i\":\"item-8FejN\",\"moved\":false,\"static\":false}],\"designerSettings\":[{\"key\":\"parameters\",\"values\":{\"enable\":true,\"orientation\":\"right\",\"enableApplyButton\":true}}],\"parameterDrawerStatus\":false}},\"dir\":\"DashboardDesignerTest\",\"fileName\":\"dashboardSave\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Design is saved successfully"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		String name = jsonObject.getJSONObject("response").getString("uuid");
		Assert.assertEquals("dashboardSave",name);
			
	}
	
	@Test
	public void dd_b3_edit_dashboard_desiginer() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData",
				"{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-JVDju\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-JVDju\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"Report_DashboardDesignerTest\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerTest/Report_DashboardDesignerTest.hr\",\"name\":\"Report_DashboardDesignerTest.hr\",\"title\":\"Report_DashboardDesignerTest\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"03caefb1-7a83-475e-899c-dcb8efc5f568\"},{\"id\":\"item-8FejN\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-8FejN\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"Report2\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerTest/Report2.hr\",\"name\":\"Report2.hr\",\"title\":\"Report2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"58820f7a-fc33-4509-85f4-79f5a8e921ac\",\"lastModified\":1675757755935},{\"id\":\"item-bSEHK\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-bSEHK\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"Report_DashboardDesignerTest2\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerTest/Report_DashboardDesignerTest2.hr\",\"name\":\"Report_DashboardDesignerTest2.hr\",\"title\":\"Report_DashboardDesignerTest2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"94ad81e3-08ce-46ac-8bcd-459e9676bca0\",\"lastModified\":1675760673534}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[{\"key\":\"breakpoints\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":1200,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":996,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":768,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":480,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":240,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"columns\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":12,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":12,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":6,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":4,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":2,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"grid\",\"values\":{\"autoSize\":true,\"compactType\":null,\"rowHeight\":100,\"isDroppable\":false,\"preventCollision\":true,\"measureBeforeMount\":false,\"isDraggable\":true,\"isResizable\":true}},{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"<p><br></p>\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":0,\"yOffset\":0,\"blur\":0,\"spread\":0,\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\",\"opacity\":100,\"backgroundSize\":\"auto\",\"backgroundRepeat\":\"initial\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"borderPosition\":\"border\",\"borderRadius\":0}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}}],\"layout\":[{\"w\":3,\"h\":3,\"x\":2,\"y\":0,\"i\":\"item-JVDju\",\"moved\":false,\"static\":false},{\"w\":3,\"h\":3,\"x\":5,\"y\":0,\"i\":\"item-8FejN\",\"moved\":false,\"static\":false},{\"w\":3,\"h\":3,\"x\":8,\"y\":0,\"i\":\"item-bSEHK\",\"moved\":false,\"static\":false}],\"designerSettings\":[{\"key\":\"parameters\",\"values\":{\"enable\":true,\"orientation\":\"right\",\"enableApplyButton\":true}}],\"parameterDrawerStatus\":false}},\"dir\":\"DashboardDesignerTest\",\"fileName\":\"dashboardSave\",\"uuid\":\"dashboardSave\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Design is edited successfully"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		String name = jsonObject.getJSONObject("response").getString("uuid");
		Assert.assertEquals("dashboardSave",name);
			
	}
	
	@Test
	public void dd_b4_delete_dashboard_desiginer() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		String input = "[\"DashboardDesignerTest/dashboardSave.efwdd\"]";
		map.put("sourceArray", input);
		map.put("action", "delete");
		
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"))
				.andReturn();
		
			
	}

}
