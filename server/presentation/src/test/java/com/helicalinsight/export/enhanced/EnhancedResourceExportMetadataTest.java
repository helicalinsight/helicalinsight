package com.helicalinsight.export.enhanced;

import java.io.File;
import java.util.List;

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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnhancedResourceExportMetadataTest {
	
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
	IntegrationTestUtility testUtility;
	
	static String fileName = "";

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
		testUtility.createFolder("EnhancedResourceExportMetadataTest");
		testUtility.createFolder("DestinationImportFolder");
	}
	
	
	@Test
	public void exp_a2_createMetadata() throws Exception {
		String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(defaultCatalog);
		String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(defaultTable);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"EnhancedResourceExportMetadataTest\",\"fileName\":\"Metadata_1\",\"uniqueId\":true}";
		testUtility.createMetadata(formData);
	}
	
	@Test
	public void exp_a3_shareFolder() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"}]},\"type\":\"folder\",\"dir\":\"EnhancedResourceExportMetadataTest\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void exp_a4_shareMetadataWithHuser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"}]},\"type\":\"file\",\"dir\":\"EnhancedResourceExportMetadataTest\",\"file\" :\"Metadata_1.metadata\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void exp_a5_createHReport() throws Exception {
        String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"subVizType\":\"bar\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]}}],\"activeMark\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":true,\"drillDown\":true,\"drillThrough\":true,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\",\"dateFunctions\":{\"dateTime\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"date\":[{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"time\":[{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}]}},\"metadata\":{\"location\":\"EnhancedResourceExportMetadataTest\",\"metadataFileName\":\"Metadata_1.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"EnhancedResourceExportMetadataTest\"}";
		testUtility.saveReport(formData);
	}
	
	@Test
	public void exp_a6_shareReportWithHiuser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"}]},\"type\":\"file\",\"dir\":\"EnhancedResourceExportMetadataTest\" , \"file\": \"report1.hr\"}";
		testUtility.shareResource(formData);
	}
	
	
	@Test
	public void exp_a7_createDesigner() throws Exception {
		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-MHziG\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-MHziG\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"report1\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"EnhancedResourceExportMetadataTest/report1.hr\",\"name\":\"report1.hr\",\"title\":\"report1\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"688948b4-a6f9-43b8-a5b8-514ba4f67dd1\",\"lastModified\":1661529662920},{\"id\":\"item-6fsng\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"initialPosition\":{\"x\":2,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-6fsng\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"report2\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"EnhancedResourceExportMetadataTest/report1.hr\",\"name\":\"report1.hr\",\"title\":\"report1\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"b3aff96a-940a-459a-b640-0ee1428b91b9\",\"lastModified\":1661529753065}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[{\"key\":\"breakpoints\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":1200,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":996,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":768,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":480,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":240,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"columns\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":12,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":12,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":6,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":4,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":2,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"grid\",\"values\":{\"autoSize\":true,\"compactType\":null,\"rowHeight\":100,\"isDroppable\":false,\"preventCollision\":true,\"measureBeforeMount\":false,\"isDraggable\":true,\"isResizable\":true}},{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":0,\"yOffset\":0,\"blur\":0,\"spread\":0,\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}}],\"layout\":[{\"w\":2,\"h\":2,\"x\":0,\"y\":0,\"i\":\"item-MHziG\",\"moved\":false,\"static\":false},{\"w\":2,\"h\":2,\"x\":2,\"y\":0,\"i\":\"item-6fsng\",\"moved\":false,\"static\":false}],\"designerSettings\":[{\"key\":\"parameters\",\"values\":{\"enable\":true,\"orientation\":\"right\",\"enableApplyButton\":true}}],\"parameterDrawerStatus\":false}},\"dir\":\"EnhancedResourceExportMetadataTest\",\"fileName\":\"dashboard\"}";
		testUtility.createDesign(formData);
	}
	
	@Test
	public void exp_a8_shareDesignerWithHiuser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"}]},\"type\":\"file\",\"dir\":\"EnhancedResourceExportMetadataTest\",\"file\":\"dashboard.efwdd\"}";
		testUtility.shareResource(formData);
	}
	
	
	@Test
	public void exp_a9_exportFolder() throws Exception {
		String request = "{\"dir\": \"EnhancedResourceExportMetadataTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_b1_importFolder() throws Exception {
		String request = "{\"destination\":\"DestinationImportFolder\",\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		HIResource resource1 =  serviceDb.getResourceByUrl("DestinationImportFolder/EnhancedResourceExportMetadataTest");
		Assert.assertNotNull(resource1);
		Assert.assertNotNull(resource1.getParentId());
		
		List<HIResourceSecurityDB> securityList =  serviceDb.getHIResourceSecurityByResourceId(resource1.getResourceId());
		Assert.assertTrue(!securityList.isEmpty());
		HIResource metadata = serviceDb.getResourceByUrl("DestinationImportFolder/EnhancedResourceExportMetadataTest/Metadata_1.metadata");
		Assert.assertNotNull(metadata);
		Assert.assertNotNull(metadata.getParentId());
		Assert.assertEquals(metadata.getParentId(),resource1.getResourceId());
		List<HIResourceSecurityDB> mdSecurityList =  serviceDb.getHIResourceSecurityByResourceId(metadata.getResourceId());
		Assert.assertTrue(!mdSecurityList.isEmpty());
		
		HIResource report = serviceDb.getResourceByUrl("DestinationImportFolder/EnhancedResourceExportMetadataTest/report1.hr");
		Assert.assertNotNull(report);
		Assert.assertNotNull(report.getParentId());
		Assert.assertEquals(report.getParentId(),resource1.getResourceId());
		List<HIResourceSecurityDB> reportSecurityList =  serviceDb.getHIResourceSecurityByResourceId(report.getResourceId());
		Assert.assertTrue(!reportSecurityList.isEmpty());
		
		
		HIResource design = serviceDb.getResourceByUrl("DestinationImportFolder/EnhancedResourceExportMetadataTest/dashboard.efwdd");
		Assert.assertNotNull(design);
		Assert.assertNotNull(design.getParentId());
		Assert.assertEquals(design.getParentId(),resource1.getResourceId());
		List<HIResourceSecurityDB> designSecurityList =  serviceDb.getHIResourceSecurityByResourceId(design.getResourceId());
		Assert.assertTrue(!designSecurityList.isEmpty());
	}
	
	@Test
	public void exp_b2_importFolder_again() throws Exception {
		exp_b1_importFolder();
	}
	
	@Test
	public void exp_b3_delete() throws Exception {
		testUtility.deleteResource("DestinationImportFolder");
		testUtility.clearRecycleBin();
	}
}