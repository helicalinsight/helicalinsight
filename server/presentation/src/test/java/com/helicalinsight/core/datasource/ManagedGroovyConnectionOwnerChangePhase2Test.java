/*
package com.helicalinsight.core.datasource;

import java.util.HashMap;
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManagedGroovyConnectionOwnerChangePhase2Test {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;


	@Autowired
	private AdminController adminController;
	
	@Autowired
	private HIResourceServiceDB serviceDb;


	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}
	
	*/
/**
	 * Test Summary
	 * 
	 * 1. Create a folder to save datasource and another folder to save metadata and report
	 * 2. Create Metadata and HReport 
	 * 3. Change owner of datasource folder
	 * 4. Access metadata
	 * 5. Access report
	 *//*


	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController,this.adminController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}


	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	private static String firstJdbcId = "";

	
	@Test
	public void ds_a1_create_folder() throws Exception {
		testUtility.createFolder("ManagedGroovyConnectionOwnerChangePhase2Test");
		testUtility.createFolder("ManagedGroovyConnectionOwnerChangeDatasources");
	}
	

	@Test
	public void ds_a2_createDataSourceConnection() throws Exception {

		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"ManagedGroovyConnectionOwnerChangeDatasources\"}";
		String response = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject data = responseObject.getJSONObject("data");
		firstJdbcId = data.getString("id");
	}
	static String userName = "user_" + System.currentTimeMillis();
	static Integer roleId;
	static String  userId;
	
	@Test
	public void ds_a3_createUser() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\""+userName+"@temp.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\""+userName+"\",\"email\":\""+userName+"@helical.com\",\"enabled\":true,\"roleIds\":[2,1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	
	@Test
	public void ds_a4_createMetadata() throws Exception {
		testUtility.expand("{\"dir\":\"ManagedGroovyConnectionOwnerChangeDatasources\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand("{\"dir\":\"ManagedGroovyConnectionOwnerChangeDatasources\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"sql.jdbc.groovy.managed\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"ManagedGroovyConnectionOwnerChangeDatasources\",\"connId\":\"74vc2\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"ManagedGroovyConnectionOwnerChangePhase2Test\",\"metadataReload\":true}";
		testUtility.createMetadata(formData);
	}
	
	@Test
	public void ds_a5_createReport() throws Exception {
        String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"subVizType\":\"bar\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]}}],\"activeMark\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":true,\"drillDown\":true,\"drillThrough\":true,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\",\"dateFunctions\":{\"dateTime\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"date\":[{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"time\":[{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}]}},\"metadata\":{\"location\":\"ManagedGroovyConnectionOwnerChangePhase2Test\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"ManagedGroovyConnectionOwnerChangePhase2Test\"}";
		testUtility.saveReport(formData);
	}
	
	@Test
	public void ds_a6_changeOwnerOfFolder() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("ManagedGroovyConnectionOwnerChangeDatasources");
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\""+userId+"\"}";
		testUtility.changeOwner(formData);
		HIResource resource_1 =  serviceDb.getResourceByUrl("ManagedGroovyConnectionOwnerChangeDatasources");
		Assert.assertEquals(userId,""+resource_1.getCreatedBy());
	}
	
	@Test
	public void ds_a7_getReportForEdit_shouldReturnMetadata() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		String formData = "{\"dir\":\"ManagedGroovyConnectionOwnerChangePhase2Test\",\"file\":\"report1.hr\"}";
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "getReportForEdit");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
	
		ObjectNode json = JacksonUtility.fromObject(result.getResponse().getContentAsString());
		Assert.assertTrue(json.with("response").has("data"));
	}
	
	@Test
	public void ds_a8_getMetadataForEdit_shouldReturnData() throws Exception  {
		String mdFormData = "{\"location\":\"ManagedGroovyConnectionOwnerChangePhase2Test\",\"metadataFileName\":\"SaveSelectAll.metadata\"}";
		String response = testUtility.getMetadataForEdit(mdFormData);
		ObjectNode json = JacksonUtility.fromObject(response);
		Assert.assertEquals(1, json.get("status").asInt());
	}
	
	
	@Test
	public void ds_a9_getReport_should_throw_error() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		String formData = "{\"location\":\"ManagedGroovyConnectionOwnerChangePhase2Test\",\"metadataFileName\":\"SaveSelectAll.metadata\",\"columns\":[{\"column\":{\"name\":\"HIUSER.dimdate.created_date\",\"id\":\"146\"},\"alias\":\"created_date\",\"floatingType\":\"discrete\"},{\"column\":{\"name\":\"HIUSER.dimdate.created_time\",\"id\":\"147\"},\"alias\":\"created_time\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"created_date\",\"custom\":true},{\"column\":\"created_time\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "fetchData");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(result -> {
			String message = JacksonUtility.fromObject(result.getResponse().getContentAsString()).with("response")
			.get("message").asText();
			Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
			
		});
	}
	
	@Test
	public void ds_b1_deleteConnection_should_throw_exception() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		map.put("formData", "{\"id\":"+firstJdbcId+",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"directory\":\"ManagedGroovyConnectionOwnerChangeDatasources\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(result -> {
			String message = JacksonUtility.fromObject(result.getResponse().getContentAsString()).with("response")
					.get("message").asText();
					Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
		});
	}
	
	@Test
	public void ds_b2_shareConnection_should_throw_exception() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
    	String formData = "{\"type\":\"dataSource\",\"dir\":\"ManagedGroovyConnectionOwnerChangeDatasources\",\"id\":\""+firstJdbcId+"\",\"classifier\":\"efwd\",\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":\"5\"}]}}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(result -> {
			String message = JacksonUtility.fromObject(result.getResponse().getContentAsString()).with("response")
					.get("message").asText();
					Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
		});

	}
}
*/
