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

import com.helicalinsight.admin.dao.HIResourceMappingDao;
import com.helicalinsight.admin.model.HIResourceMapping;
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

public class DashboardDesignerDelete7961Test {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private HIResourceMappingDao hiResourceConstituentMappingDao;
	
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
		map.put("folderName", "DashboardDesignerDeleteTest");
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
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"0e14l\",\"dbId\":\"0e14l\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_DashboardDesignerDeleteTest\",\"location\":\"DashboardDesignerDeleteTest\",\"metadataReload\":true}");
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
	public void dd_a5_report_saveReport() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		map.put("formData", "{\"isHrReport\":true,\"columns\":[{\"column\":\"employee_details.address\",\"columnID\":\"1564\",\"label\":\"address\",\"id\":\"2a94b10a-643c-4b00-8458-539f55bab0e6\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"address\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.age\",\"columnID\":\"1563\",\"label\":\"sum_age\",\"id\":\"faa06b3c-e6e1-4696-aa89-735bccc5823a\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_age\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"age\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_id\",\"columnID\":\"1561\",\"label\":\"sum_employee_id\",\"id\":\"f33cf925-9f8f-4dde-bd01-f6df078ab710\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"state\":{\"metadataLoading\":false,\"hreportLoading\":false,\"fields\":[{\"column\":\"employee_details.address\",\"columnID\":\"1564\",\"label\":\"address\",\"id\":\"2a94b10a-643c-4b00-8458-539f55bab0e6\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"address\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.age\",\"columnID\":\"1563\",\"label\":\"sum_age\",\"id\":\"faa06b3c-e6e1-4696-aa89-735bccc5823a\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_age\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"age\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_id\",\"columnID\":\"1561\",\"label\":\"sum_employee_id\",\"id\":\"f33cf925-9f8f-4dde-bd01-f6df078ab710\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"7613894e-1fb2-4d59-b5d1-c226b2d51d98\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_age\",\"id\":\"faa06b3c-e6e1-4696-aa89-735bccc5823a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_employee_id\",\"id\":\"f33cf925-9f8f-4dde-bd01-f6df078ab710\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"7613894e-1fb2-4d59-b5d1-c226b2d51d98\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"stylesId\":\"hi-report-c3d4469e\",\"savedStyles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"format\":{\"formatFields\":[{\"id\":\"faa06b3c-e6e1-4696-aa89-735bccc5823a\",\"values\":{\"thousandSperator\":true,\"decimalPlace\":2,\"prefix\":\"\",\"suffix\":\"\",\"displayUnits\":\"None\",\"percentage\":false,\"numberCustom\":\"\",\"apply\":[\"pane\",\"tooltip\",\"label\",\"axis\",\"actions\",\"legend\"],\"isApplyClicked\":true,\"autoFormatting\":true}},{\"id\":\"f33cf925-9f8f-4dde-bd01-f6df078ab710\",\"values\":{\"thousandSperator\":true,\"decimalPlace\":2,\"prefix\":\"\",\"suffix\":\"\",\"displayUnits\":\"None\",\"percentage\":false,\"numberCustom\":\"\",\"apply\":[\"pane\",\"tooltip\",\"label\",\"axis\",\"actions\",\"legend\"],\"isApplyClicked\":true,\"autoFormatting\":true}}],\"formatDatatype\":\"\",\"activeFieldId\":\"\",\"showAll\":false}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"geoJsonData\":{},\"isAborted\":false,\"referenceLineList\":[{\"display\":\"All\",\"id\":\"aee7539f-6e8c-4b27-b5a6-27ee4d386fb9\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true},{\"display\":\"sum_age\",\"id\":\"faa06b3c-e6e1-4696-aa89-735bccc5823a\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true},{\"display\":\"sum_employee_id\",\"id\":\"f33cf925-9f8f-4dde-bd01-f6df078ab710\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true}],\"customChart\":{\"selected\":false,\"drawer\":false,\"code\":\"\",\"applied\":false},\"tableRecordsPerPage\":10,\"measures\":{\"enable\":false,\"fields\":[{\"column\":\"frontend_custom_measure_name\",\"alias\":\"Measure_Name\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"text\"}},{\"column\":\"frontend_custom_measure_value\",\"alias\":\"Measure_Value\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"numeric\"}}]},\"reportModal\":false,\"chartColorPalette\":{\"Custom Colors\":{}},\"database\":\"HIUSER\",\"appliedDbfs\":{}},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"DashboardDesignerDeleteTest\",\"metadataFileName\":\"Metadata_DashboardDesignerDeleteTest.metadata\"},\"reportName\":\"Report_1\",\"location\":\"DashboardDesignerDeleteTest\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		
		String name = object.getString("uuid");
		Assert.assertEquals("Report_1.hr", name);
		
		
	}
	@Test
	public void dd_a6_report_saveReport2() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		map.put("formData", "{\"isHrReport\":true,\"columns\":[{\"column\":\"employee_details.address\",\"columnID\":\"1564\",\"label\":\"address\",\"id\":\"cc0d39f1-444e-439d-a40f-f78a4671b11f\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"address\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.age\",\"columnID\":\"1563\",\"label\":\"sum_age\",\"id\":\"06a4a439-ac35-40d3-ade8-ac8f47c48462\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_age\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"age\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_id\",\"columnID\":\"1561\",\"label\":\"sum_employee_id\",\"id\":\"6ba7f076-1d70-41af-8496-eacd1e49419f\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_name\",\"columnID\":\"1562\",\"label\":\"employee_name\",\"id\":\"91df6c1b-cc4d-4735-9557-6303c7a0e25d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_name\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"state\":{\"metadataLoading\":false,\"hreportLoading\":false,\"fields\":[{\"column\":\"employee_details.address\",\"columnID\":\"1564\",\"label\":\"address\",\"id\":\"cc0d39f1-444e-439d-a40f-f78a4671b11f\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"address\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.age\",\"columnID\":\"1563\",\"label\":\"sum_age\",\"id\":\"06a4a439-ac35-40d3-ade8-ac8f47c48462\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_age\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"age\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_id\",\"columnID\":\"1561\",\"label\":\"sum_employee_id\",\"id\":\"6ba7f076-1d70-41af-8496-eacd1e49419f\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_employee_id\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_id\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false},{\"column\":\"employee_details.employee_name\",\"columnID\":\"1562\",\"label\":\"employee_name\",\"id\":\"91df6c1b-cc4d-4735-9557-6303c7a0e25d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"hidden\":false,\"metaDataAlias\":\"employee_name\",\"databaseName\":\"HIUSER\",\"geographicType\":\"\",\"isView\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"7613894e-1fb2-4d59-b5d1-c226b2d51d98\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_age\",\"id\":\"06a4a439-ac35-40d3-ade8-ac8f47c48462\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_employee_id\",\"id\":\"6ba7f076-1d70-41af-8496-eacd1e49419f\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"7613894e-1fb2-4d59-b5d1-c226b2d51d98\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"stylesId\":\"hi-report-bd1bd99c\",\"savedStyles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"format\":{\"formatFields\":[{\"id\":\"06a4a439-ac35-40d3-ade8-ac8f47c48462\",\"values\":{\"thousandSperator\":true,\"decimalPlace\":2,\"prefix\":\"\",\"suffix\":\"\",\"displayUnits\":\"None\",\"percentage\":false,\"numberCustom\":\"\",\"apply\":[\"pane\",\"tooltip\",\"label\",\"axis\",\"actions\",\"legend\"],\"isApplyClicked\":true,\"autoFormatting\":true}},{\"id\":\"6ba7f076-1d70-41af-8496-eacd1e49419f\",\"values\":{\"thousandSperator\":true,\"decimalPlace\":2,\"prefix\":\"\",\"suffix\":\"\",\"displayUnits\":\"None\",\"percentage\":false,\"numberCustom\":\"\",\"apply\":[\"pane\",\"tooltip\",\"label\",\"axis\",\"actions\",\"legend\"],\"isApplyClicked\":true,\"autoFormatting\":true}}],\"formatDatatype\":\"\",\"activeFieldId\":\"\",\"showAll\":false}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"geoJsonData\":{},\"isAborted\":false,\"referenceLineList\":[{\"display\":\"All\",\"id\":\"aee7539f-6e8c-4b27-b5a6-27ee4d386fb9\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true},{\"display\":\"sum_age\",\"id\":\"06a4a439-ac35-40d3-ade8-ac8f47c48462\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true},{\"display\":\"sum_employee_id\",\"id\":\"6ba7f076-1d70-41af-8496-eacd1e49419f\",\"referenceType\":\"Line\",\"value\":\"\",\"enabled\":false,\"isStatic\":true}],\"customChart\":{\"selected\":false,\"drawer\":false,\"code\":\"\",\"applied\":false},\"tableRecordsPerPage\":10,\"measures\":{\"enable\":false,\"fields\":[{\"column\":\"frontend_custom_measure_name\",\"alias\":\"Measure_Name\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"text\"}},{\"column\":\"frontend_custom_measure_value\",\"alias\":\"Measure_Value\",\"genre\":\"custom-formula\",\"custom_frontend_field\":true,\"type\":{\"dataType\":\"numeric\"}}]},\"reportModal\":false,\"chartColorPalette\":{\"Custom Colors\":{}},\"database\":\"HIUSER\",\"appliedDbfs\":{}},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"DashboardDesignerDeleteTest\",\"metadataFileName\":\"Metadata_DashboardDesignerDeleteTest.metadata\"},\"reportName\":\"Report_2\",\"location\":\"DashboardDesignerDeleteTest\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Successfully saved report file"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		
		String name = object.getString("uuid");
		Assert.assertEquals("Report_2.hr", name);
		
		
	}

	
	
	
	

	private static int designerId;
	
	@Test
	
	public void dd_a7_save_dashboard_desiginer() throws Exception {
		
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData","{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-aaHD4\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"i\":\"item-aaHD4\"},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"title\":\"Report_1\"}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerDeleteTest/Report_1.hr\",\"name\":\"Report_1.hr\",\"title\":\"Report_1\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\",\"resourceId\":129},\"filters\":[],\"listeners\":[],\"reportId\":\"36657100-f10c-4758-87c7-43822c78fcb7\",\"lastModified\":1743314913621},{\"id\":\"item-bisdN\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":2,\"i\":\"item-bisdN\"},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"title\":\"Report_2\"}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerDeleteTest/Report_2.hr\",\"name\":\"Report_2.hr\",\"title\":\"Report_2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\",\"resourceId\":130},\"filters\":[],\"listeners\":[],\"reportId\":\"0a3c7b0b-2c96-4a6d-b539-4f9c6f482146\",\"lastModified\":1743315365055}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[{\"w\":2,\"h\":2,\"x\":5,\"y\":1,\"i\":\"item-aaHD4\",\"moved\":false,\"static\":false},{\"w\":2,\"h\":2,\"x\":8,\"y\":1,\"i\":\"item-bisdN\",\"moved\":false,\"static\":false}],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\"DashboardDesignerDeleteTest\",\"fileName\":\"Dashboard_1\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Design is saved successfully"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseJson = jsonObject.getJSONObject("response");
		
		JSONArray dataArray = responseJson.getJSONArray("data");
		JSONObject efwddJson = dataArray.getJSONObject(0);
		designerId = efwddJson.getInt("resourceId");
		System.out.println("----------------------designerId------------------------"+designerId);
	
	}
	
	@Test
@Transactional
public void dd_a8_verify_entries_in_Dashboard() throws Exception {
    List<HIResourceMapping> byId = hiResourceConstituentMappingDao.findByParentId(designerId);
    System.out.println("----------------------designerId------------------------"+designerId);
    // Add logging to debug the issue
    System.out.println("Designer ID: " + designerId);
    System.out.println("Number of entries found: " + byId.size());
    for (HIResourceMapping resource : byId) {
        System.out.println("Resource ID: " + resource.getId());
      
    }
    
    Assert.assertNotNull(byId);
    Assert.assertEquals(2, byId.size());
}


	
	@Test
	public void dd_a9_remove_a_report_and_save_designer() throws Exception {
		
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData", "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-bisdN\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":2,\"i\":\"item-bisdN\"},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"title\":\"Report_2\"}}],\"reportInfo\":{\"file\":{\"path\":\"DashboardDesignerDeleteTest/Report_2.hr\",\"name\":\"Report_2.hr\",\"title\":\"Report_2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\",\"resourceId\":130},\"filters\":[],\"listeners\":[],\"reportId\":\"0a3c7b0b-2c96-4a6d-b539-4f9c6f482146\",\"lastModified\":1743315365055}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[{\"w\":2,\"h\":2,\"x\":5,\"y\":1,\"i\":\"item-aaHD4\",\"moved\":false,\"static\":false},{\"w\":2,\"h\":2,\"x\":8,\"y\":1,\"i\":\"item-bisdN\",\"moved\":false,\"static\":false}],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\"DashboardDesignerDeleteTest\",\"fileName\":\"Dashboard_1\",\"uuid\":\"Dashboard_1\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Design is edited successfully"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseJson = jsonObject.getJSONObject("response");
		

	}
	
	@Test
	@Transactional
	public void dd_b1_verify_entries_in_Dashboard() throws Exception {
	    List<HIResourceMapping> byId = hiResourceConstituentMappingDao.findByParentId(designerId);
	    System.out.println("Designer ID: " + designerId);
	    System.out.println("Number of entries found: " + byId.size());
	    Assert.assertNotNull(byId);
	    Assert.assertEquals(1, byId.size());
	}

	
	@Test
	public void dd_b2_remove_second_report_and_save_designer() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		map.put("formData","{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[{\"w\":2,\"h\":2,\"x\":5,\"y\":1,\"i\":\"item-aaHD4\",\"moved\":false,\"static\":false},{\"w\":2,\"h\":2,\"x\":8,\"y\":1,\"i\":\"item-bisdN\",\"moved\":false,\"static\":false}],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\"DashboardDesignerDeleteTest\",\"fileName\":\"Dashboard_1\",\"uuid\":\"Dashboard_1\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Design is edited successfully"))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseJson = jsonObject.getJSONObject("response");
		
	}
	@Test
	@Transactional
	public void dd_b3_verify_entries_in_Dashboard() throws Exception {
	    List<HIResourceMapping> byId = hiResourceConstituentMappingDao.findByParentId(designerId);
	  
	    System.out.println("Designer ID: " + designerId);
	    System.out.println("Number of entries found: " + byId.size());
	    Assert.assertNotNull(byId);
	    Assert.assertEquals(0, byId.size());
	}	
	
	

	

}
