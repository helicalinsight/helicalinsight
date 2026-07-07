package com.helicalinsight.filebrowser;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MetadataSaveEditDataTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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
	public void md_1_create_a_folder_to_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "MetadataTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void md_3_expand_catalog_schema() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_4_expand_table() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_5_createMetadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
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
			System.out.println("Data : "+data);
			Assert.assertTrue(data.containsKey("lastModified"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("options"));
			Assert.assertTrue(data.containsKey("extension"));
			Assert.assertTrue(data.containsKey("path"));
			Assert.assertTrue(data.containsKey("permissionLevel"));
			Assert.assertTrue(data.containsKey("name"));
			Assert.assertTrue(data.containsKey("title"));
			Assert.assertEquals(1, status);
			Assert.assertEquals("Successfully saved metadata file", message);
		}
	}

	@Test
	public void md_7_editMetadata_change_table_column_alias() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.generic\",\"catSchemaPredicted\":false,\"connId\":\"rkPecn3S5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[{\"id\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"alias\":\"travel_details_alias\",\"connId\":\"rkPecn3S5\"}],\"columns\":[{\"alias\":\"destination_alias\",\"columnId\":\"1c816105-70b2-42cc-9459-ed52f52974bc\",\"tableId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"connId\":\"rkPecn3S5\"}]},\"metadataReload\":false,\"location\":\"MetadataTest\",\"uuid\":\"SaveSelectAll.metadata\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
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
			String message = responseObject.getString("message");
			Assert.assertEquals(1, status);
			Assert.assertEquals("Successfully saved metadata file", message);
		}
	}

	@Test
	public void md_8_createReport() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "saveReport");
		String formData = "{\"columns\":[{\"floatingType\":\"discrete\",\"hiddenIncludeInResultSet\":false,\"functionsDefinition\":\"\",\"addedAs\":\"column\",\"applyBeforeAggregate\":false,\"autogen_alias\":\"created_date\",\"label\":\"created_date\",\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"dimdate.created_date\",\"type\":{\"dataType\":\"text\",\"backendDatatype\":\"java.lang.String\"},\"id\":\"lum1irzii5c\",\"orderByColumn\":false,\"isNormalTable\":true,\"showOrderByColumn\":false}],\"state\":{\"columns\":[{\"floatingType\":\"discrete\",\"hiddenIncludeInResultSet\":false,\"functionsDefinition\":\"\",\"addedAs\":\"column\",\"applyBeforeAggregate\":false,\"autogen_alias\":\"created_date\",\"label\":\"created_date\",\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"dimdate.created_date\",\"type\":{\"dataType\":\"text\",\"backendDatatype\":\"java.lang.String\"},\"id\":\"lum1irzii5c\",\"orderByColumn\":false,\"isNormalTable\":true,\"showOrderByColumn\":false}],\"dateFunctions\":{\"dateTime\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"date\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"time\":[{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}]},\"filters\":[],\"customFilterExpression\":\"\",\"customHavingExpression\":\"\",\"customFilterExpressionObj\":{},\"activeTool\":0,\"customHavingExpressionObj\":{},\"havingExpressionIndexs\":{},\"filterExpressionIndexs\":{},\"options\":{\"limitBy\":1000,\"prependTableNameToAlias\":false},\"visualisation\":{\"chartGroup\":\"\",\"selectedType\":\"Table\",\"settings\":{\"script\":null,\"vizscriptsEditMultipleMode\":false},\"vizSelectedScripts\":[],\"reportData\":{}},\"scripts\":[],\"styles\":\"\",\"customStyles\":\"\",\"customScripts\":[],\"isReportGenerated\":true,\"marks\":[{\"value\":\"_all_\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]}}],\"interactiveMode\":false,\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"drillDown\":false,\"drillThrough\":false,\"drillThroughList\":[],\"drillDownList\":[],\"currentDrillDown\":\"\",\"toolbarConfig\":{\"selectable\":false},\"properties\":{\"titleConfig\":{\"position\":\"top\",\"align\":\"left\",\"title\":\"\",\"showTitle\":false,\"padding\":4,\"color\":\"#000\"},\"subTitleConfig\":{\"position\":\"top\",\"align\":\"right\",\"subtitle\":\"\",\"showSubtitle\":false,\"padding\":4,\"color\":\"#000\"}}},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"MetadataTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"location\":\"MetadataTest\",\"reportName\":\"report_1\",\"isHrReport\":true}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
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
			Assert.assertEquals(1, status);
		}
	}

	// Designer BugId: 4507 , 4752
	@Test
	public void md_9_createDesign() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "dashboard");
		map.put("serviceType", "efwdd");
		map.put("service", "designer");
		String formData = "{\"htmlString\":\"\",\"state\":{\"variables\":[],\"components\":{\"ob7qfjwvze\":{\"metadata\":{\"dir\":\"MetadataTest\",\"name\":\"report_1\"},\"type\":\"dashboard-component\",\"options\":{\"dir\":\"1650373059932\",\"file\":\"c5c702ed-0e06-46e0-ae3c-56628126a210.hr\",\"ext\":\"hr\",\"compType\":\"hreport\",\"uid\":\"ob7qfjwvze\",\"iframe\":false},\"uid\":\"ob7qfjwvze\",\"name\":\"ob7qfjwvze\",\"label\":\"report_1\",\"executeAtStart\":true,\"gs_attr\":{\"x\":0,\"y\":0,\"height\":16,\"width\":33}}},\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"ob7qfjwvze\":{\"header\":true,\"minimize\":false,\"maximize\":false,\"export\":false,\"editReport\":false}},\"gridWidthOption\":100,\"toggleIframes\":false},\"dir\":\"MetadataTest\",\"fileName\":\"design\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
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
			String message = responseObject.getString("message");
			Assert.assertEquals(1, status);
			Assert.assertEquals("Design is saved successfully", message);
		}

	}
	
	    //  BugId: 4837
		@Test
		public void md_9_1_createDesign_with_space() throws Exception {
			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
			Map<String, String> map = new HashMap<>();
			map.put("type", "dashboard");
			map.put("serviceType", "efwdd");
			map.put("service", "designer");
			String formData = "{\"htmlString\":\"\",\"state\":{\"variables\":[],\"components\":{\"ob7qfjwvze\":{\"metadata\":{\"dir\":\"MetadataTest\",\"name\":\"report_1\"},\"type\":\"dashboard-component\",\"options\":{\"dir\":\"1650373059932\",\"file\":\"c5c702ed-0e06-46e0-ae3c-56628126a210.hr\",\"ext\":\"hr\",\"compType\":\"hreport\",\"uid\":\"ob7qfjwvze\",\"iframe\":false},\"uid\":\"ob7qfjwvze\",\"name\":\"ob7qfjwvze\",\"label\":\"report_1\",\"executeAtStart\":true,\"gs_attr\":{\"x\":0,\"y\":0,\"height\":16,\"width\":33}}},\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"ob7qfjwvze\":{\"header\":true,\"minimize\":false,\"maximize\":false,\"export\":false,\"editReport\":false}},\"gridWidthOption\":100,\"toggleIframes\":false},\"dir\":\"MetadataTest\",\"fileName\":\"Sample  Design\"}";
			map.put("formData", formData);
			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
			MvcResult result = efwMock.perform(builder).andReturn();
			JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
			int status = jsonObject.getInt("status");
			JSONObject responseObject = jsonObject.getJSONObject("response");
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
				Assert.assertTrue(data.getString("title").contains(" "));
				String message = responseObject.getString("message");
				Assert.assertEquals(1, status);
				Assert.assertEquals("Design is saved successfully", message);
			}

		}
	
	
	

	@Test
	public void md_ten_deleteFolder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"MetadataTest\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
		testUtility.clearRecycleBin();
	}

}
