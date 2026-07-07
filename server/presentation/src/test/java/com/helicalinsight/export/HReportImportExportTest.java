package com.helicalinsight.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.springframework.mock.web.MockMultipartFile;
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
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HReportImportExportTest {
	
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
	GlobalConnectionService globalConnectionService;
	
	@Autowired
	IntegrationTestUtility testUtility;
	
	static String fileName="";

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

	private static String TESTURL = "";
	static String dbName = "";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	@Test
	public void exp_a1_create_a_folder() throws Exception {
		testUtility.createFolder("HReportImportExportTest");
	}
	@Test
	public void exp_a2_create_cache() throws Exception {
		String expand1 = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(expand1);
        String expand2 = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
        testUtility.expand(expand2);
	}	
	
	@Test
	public void exp_a4_createMetadata() throws Exception {
        String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"HReportImportExportTest\",\"uniqueId\":true}";
		testUtility.createMetadata(formData);
	}
	
	@Test
	public void exp_a5_createHReport() throws Exception {
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"subVizType\":\"bar\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]}}],\"activeMark\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":true,\"drillDown\":true,\"drillThrough\":true,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\",\"dateFunctions\":{\"dateTime\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"date\":[{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"time\":[{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}]}},\"metadata\":{\"location\":\"HReportImportExportTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"HReportImportExportTest\"}";
		testUtility.saveReport(formData);
	}

	@Test
	public void exp_a6_deleteHReport() throws Exception {
		testUtility.deleteResource("HReportImportExportTest/report1.hr");
	}
	
	@Test
	public void exp_a7_exportAFolderSharedWithUserHavingMetadataAndDeletedHReport() throws Exception {
			String request = "{\"dir\": \"HReportImportExportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": false}}";
			fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a8_deleteMetadata() throws Exception {
		testUtility.deleteResource("HReportImportExportTest/SaveSelectAll.metadata");
		exp_a7_exportAFolderSharedWithUserHavingMetadataAndDeletedHReport();
	}
	
	@Test
	public void exp_a9_import() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
	}
	
}
