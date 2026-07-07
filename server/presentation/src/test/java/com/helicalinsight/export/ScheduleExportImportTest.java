package com.helicalinsight.export;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.IScheduleService;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScheduleExportImportTest {
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceServiceDB serviceDb;

	@Autowired
	OrganizationService orgService;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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
	private FileSystemOperationsController fileSystemOperationsController;
	static String fileName = "";
	private static String TESTURL = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}

	@Test
	public void exp_a1_init() throws Exception {
		testUtility.clearRecycleBin();
		testUtility.createFolder("ScheduleTest");
		testUtility.createFolder("HReport", List.of("ScheduleTest"));
		String catalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		String table = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(catalog);
		testUtility.expand(table);
	}

	@Test
	public void exp_a2_createMetadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"ScheduleTest\",\"metadataReload\":true}";
		testUtility.createMetadata(formData);
	}

	static String secondReportName = "";
	@Test
	public void exp_a3_create__two_reports() throws Exception {
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\"ScheduleTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"ScheduleTest/HReport\"}";
		testUtility.saveReport(formData);
		
		String formData2 = "{\"isHrReport\":true,\"columns\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\"ScheduleTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"ScheduleTest/HReport\"}";
		String response = testUtility.saveReport(formData2);
		secondReportName=JSONObject.fromObject(response).getJSONObject("response").getString("uuid");
	}
	
	@Test
	public void exp_a4_schedule_for_report() throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport");
		Map<String, String> map = new HashMap<>();
		map.put("command", "add");
		map.put("reportDirectory", "ScheduleTest/HReport");
		map.put("reportFile", "report1.hr");
		map.put("location", "ScheduleTest/HReport");
		map.put("EmailSettings", "{\"Formats\":[\"pdf\"],\"Recipients\":[\"mahesh.chary@helicaltech.com\"],\"Zip\":false,\"Subject\":\"Test\"}");
		map.put("isActive", "true");
		map.put("reportParameters", "{}");
		map.put("reportName", "report1");
		map.put("ScheduleOptions", "{\"DaysofWeek\":[\"Monday\"],\"Frequency\":\"Weekly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2050-09-06\",\"EndDate\":\"2050-09-06\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"18:23:21\",\"ScheduledEndTime\":\"18:24:21\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		
		
		// 2x 
		efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
				.jsonPath("$.response.message").value("Successfully scheduled the report"));
		
		efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("Successfully scheduled the report"));
	}
	
	static String url = "";
	
	@Test
	public void exp_a5_schedule_for_report() throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/saveReport");
		Map<String, String> map = new HashMap<>();
		map.put("command", "add");
		map.put("reportDirectory", "ScheduleTest/HReport");
		map.put("reportFile", secondReportName);
		map.put("location", "ScheduleTest/HReport");
		map.put("EmailSettings", "{\"Formats\":[\"pdf\"],\"Recipients\":[\"mahesh.chary@helicaltech.com\"],\"Zip\":false,\"Subject\":\"Test\"}");
		map.put("isActive", "true");
		map.put("reportParameters", "{}");
		map.put("reportName", "report1");
		map.put("ScheduleOptions", "{\"DaysofWeek\":[\"Monday\"],\"Frequency\":\"Weekly\",\"RepeatBy\":\"dayOfTheMonth\",\"RepeatsEvery\":1,\"StartDate\":\"2050-09-06\",\"EndDate\":\"2050-09-06\",\"endsRadio\":\"Never\",\"timeZone\":\"Asia/Calcutta\",\"EndAfterExecutions\":35,\"dateFormat\":\"DD/MM/YYYY hh:mm A\",\"ScheduledTime\":\"18:23:21\",\"ScheduledEndTime\":\"18:24:21\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		
		// 2x
		
		efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("Successfully scheduled the report"));
		
		MvcResult result =  efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("Successfully scheduled the report"))
				.andReturn();
		
		JSONObject responseObj = (JSONObject) JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response").getJSONArray("data").get(0);
		url = responseObj.getString("path");
		String fileName = FilenameUtils.getName(url);
		String folderName = FilenameUtils.getPathNoEndSeparator(url);
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"5\"}]},\"type\":\"file\",\"dir\":\""+folderName+"\",\"file\":\""+fileName+"\"}";
		testUtility.shareResource(formData);
		
	
	}
	
	
	@Test
	public void exp_a6_export_a_scheduled_report() throws Exception {
		String request = "{\"dir\": \"ScheduleTest/HReport\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a7_delete() throws Exception {
		testUtility.deleteResource("ScheduleTest");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_a8_import_scheduled_report() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		
		HIResource report1 =  serviceDb.getResourceByUrl("ScheduleTest/HReport/report1.hr");
		Assert.assertNotNull(report1);
		
		IScheduleService scheduleService  = (IScheduleService) ApplicationContextAccessor.getBean("schedulesServiceImpl");
		List<Long> ids =  scheduleService.findAllSchedulesByResourceId(report1.getResourceId());
		Assert.assertNotNull(ids);
		Assert.assertNotEquals(0, ids.size());
		
		HIResource report2 =  serviceDb.getResourceByUrl("ScheduleTest/HReport/"+secondReportName);
		Assert.assertNotNull(report2);
		List<Long> ids2 =  scheduleService.findAllSchedulesByResourceId(report2.getResourceId());
		Assert.assertNotNull(ids2);
		Assert.assertNotEquals(0, ids2.size());
		
		HIResource efwsrFile =  serviceDb.getResourceByUrl(url);
		List<HIResourceSecurityDB> resourceSecurity =  serviceDb.getHIResourceSecurityByResourceId(efwsrFile.getResourceId());
		Assert.assertNotNull(resourceSecurity);
		Assert.assertTrue(!resourceSecurity.isEmpty());
		Assert.assertEquals(1,resourceSecurity.size());
	}
	
	@Test
	public void exp_a9_createDestinationFolder() throws Exception {
		testUtility.createFolder("DestinationFolderForSchedule");
	}
	
	@Test
	public void exp_b1_importInDestinationFolder() throws Exception {
		String request = "{\"destination\":\"DestinationFolderForSchedule\"," +
				"\"onConflict\" : \"update\",\"upload\":true,\"options\" " +
				":{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		
		HIResource report1 =  serviceDb.getResourceByUrl("DestinationFolderForSchedule/ScheduleTest/HReport/report1.hr");
		Assert.assertNotNull(report1);
		
		IScheduleService scheduleService  = (IScheduleService) ApplicationContextAccessor.getBean("schedulesServiceImpl");
		List<Long> ids =  scheduleService.findAllSchedulesByResourceId(report1.getResourceId());
		Assert.assertNotNull(ids);
		Assert.assertNotEquals(0, ids.size());
		SchedulesService service =  ApplicationContextAccessor.getBean(SchedulesService.class);
		for(Long id : ids ) {
		   Schedules schedule = 	service.getSchedule(id);
		   //Assert.assertTrue("Asia/Calcutta".equalsIgnoreCase(schedule.getTimeZone()) || "Asia/Kolkata".equalsIgnoreCase(schedule.getTimeZone()));
		}
		
		HIResource report2 =  serviceDb.getResourceByUrl("DestinationFolderForSchedule/ScheduleTest/HReport/"+secondReportName);
		Assert.assertNotNull(report2);
		List<Long> ids2 =  scheduleService.findAllSchedulesByResourceId(report2.getResourceId());
		Assert.assertNotNull(ids2);
		Assert.assertNotEquals(0, ids2.size());
		
		
		HIResource efwsrFile =  serviceDb.getResourceByUrl("DestinationFolderForSchedule/"+url);
		List<HIResourceSecurityDB> resourceSecurity =  serviceDb.getHIResourceSecurityByResourceId(efwsrFile.getResourceId());
		Assert.assertNotNull(resourceSecurity);
		Assert.assertTrue(!resourceSecurity.isEmpty());
		Assert.assertEquals(1,resourceSecurity.size());
	}
}
