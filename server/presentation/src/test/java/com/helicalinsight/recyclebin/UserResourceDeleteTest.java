package com.helicalinsight.recyclebin;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserResourceDeleteTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;
	
	@Bean
	public AdminController adminController() {
		return new AdminController();
	}
	
	@Autowired
	private AdminController adminController;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
		}
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController,this.adminController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	static Integer userId = null;
	
	@Test 
	public void user_a0_clearRecycleBin() throws Exception {
		testUtilitiy.clearRecycleBin();
	}
	
	
	@Test
	public void user_a1_create_user_and_attach_admin_role() throws Exception {
			String formData = "{\"id\":\"\",\"email\":\"randomUser@helical.com\",\"name\":\"randomUser\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
			String responseString = testUtilitiy.createUser(formData);
			JSONObject object = JSONObject.fromObject(responseString);
			JSONObject response = (JSONObject) object.get("response");
			userId = response.getInt("id");
			
			String attachRole = "{\"id\":"+userId+",\"name\":\"randomUser\",\"email\":\"randomUser@helical.com\",\"enabled\":true,\"roleIds\":[2,1],\"password\":\"\"}";
			testUtilitiy.attachRole(attachRole,""+userId);
	}
	
	static String dsId = "";
	/*
	@Test
	public void user_a2_create_datasource_with_new_user() throws Exception  {
		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelDataDerby\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"username\":\"randomUser\",\"password\":\"password\"}";
		String response = testUtilitiy.createDatasource(formData);
		JSONObject responseObject = JSONObject.fromObject(response).getJSONObject("response");
    	dsId = responseObject.getString("dataSourceId");
	}*/
	
/*	@Test
	public void user_a3_create_cache() throws Exception {
		String defaultCatalog = "{\"id\":\""+dsId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true},\"username\":\"hiuser\",\"password\":\"hiuser\"}";
		testUtilitiy.expand(defaultCatalog);
		String defaultTable = "{\"id\":\""+dsId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]},\"username\":\"hiusr\",\"password\":\"hiuser\"}";
		testUtilitiy.expand(defaultTable);
	} 
	
	@Test
	public void user_a4_create_metadata() throws Exception {
		testUtilitiy.createFolder("UserResourcesDeleteTest","randomUser","password");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\""+dsId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"45hhj\",\"dbId\":\"45hhj\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"UserResourcesDeleteMetadata\",\"location\":\"UserResourcesDeleteTest\",\"metadataReload\":true,\"username\":\"randomUser\",\"password\":\"password\"}";
		testUtilitiy.createMetadata(formData);
	}*/
	
	/*@Test
	public void user_a5_create_report() throws Exception {
		String saveReport = "{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"UserResourcesDeleteTest\",\"metadataFileName\":\"UserResourcesDeleteMetadata.metadata\"},\"reportName\":\"UserResourcesDeleteTestReport\",\"location\":\"UserResourcesDeleteTest\",\"username\":\"randomUser\",\"password\":\"password\"}";
		testUtilitiy.saveReport(saveReport);
	}*/
	
//	@Test
//	public void user_a6_deleteFolder() throws Exception {
//		String input = "[\"UserResourcesDeleteTest\"]";
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray",input);
//		map.put("username", "randomUser");
//		map.put("password", "password");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful")).andReturn();
//
//	}
	
	/*@Test
	public void user_a7_fetchDetailsOfRecycleBInItemFromAdmin() throws Exception {
		String binId = testUtilitiy.getRecycleBinIdByResourceName("UserResourcesDeleteTest");
		String formData="{\"action\":\"fetchDetails\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtilitiy.recycleBinAction(formData);
		JSONArray resourceArray =  JSONObject.fromObject(response).getJSONObject("response").getJSONObject(binId)
				 .getJSONObject("data").getJSONArray("resources");
		Assert.assertFalse(resourceArray.isEmpty());
	}*/
	
	@Test
	public void user_a8_restore_all_resources() throws Exception {
		JsonArray array = testUtilitiy.listRecycleBin();
		for( JsonElement object : array ) {
			JsonObject item =  object.getAsJsonObject();
			String formData="{\"action\":\"fetchDetails\",\"recycleBinIds\":["+item.get("recycleBinId").getAsString()+"]}";
			testUtilitiy.recycleBinAction(formData);
		}
	}
	
	
	@Test
	public void user_a9_delete_user() throws Exception {
		testUtilitiy.deleteUser(""+userId);
	}
	
	@Test 
	public void user_b1_clearRecycleBin() throws Exception {
		testUtilitiy.clearRecycleBin();
	}
	
	@Test
	public void user_b2_verify() throws Exception {
		HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
		GlobalConnectionService gService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
		Assert.assertTrue(serviceDB.getHIResourceByCreatedBy(userId).isEmpty());
		Assert.assertTrue(gService.findConnectionsByCreatedBy(""+userId).isEmpty());
	}
	
	
}
