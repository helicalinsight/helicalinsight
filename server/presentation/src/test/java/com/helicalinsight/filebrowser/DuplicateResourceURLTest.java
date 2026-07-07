package com.helicalinsight.filebrowser;



import static org.junit.Assert.assertNotNull;

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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DuplicateResourceURLTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	private HIResourceServiceDB serviceDB;

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
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	@Test
	public void md_1_create_multiple_folders_with_same_name() throws Exception {
		testUtility.createFolder("DuplicateResourceURLTest");
		String response = testUtility.createFolder("duplicateresourceurltest");
		testUtility.createFolder("DuplicateresourceURLTest");
		testUtility.createFolder("duplicateResourceURLTest");
		testUtility.createFolder("DuplicateResourceurlTest");
		
		ObjectNode responseNode = JacksonUtility.fromObject(response).with("response").with("data");
		Assert.assertEquals("duplicateresourceurltest_01", responseNode.get("path").asText());
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest"));
		assertNotNull(serviceDB.getResourceByUrl("duplicateresourceurltest_01"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateresourceURLTest_02"));
		assertNotNull(serviceDB.getResourceByUrl("duplicateResourceURLTest_03"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceurlTest_04"));
	}
	
/*	@Test
	public void md_a2_create_multiple_metadatas_with_same_name() throws Exception {
		
		String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(defaultCatalog);
		String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(defaultTable);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"DuplicateResourceURLTest\",\"fileName\":\"Metadata_1\",\"uniqueId\":true}";
		testUtility.createMetadata(formData);
		testUtility.createMetadata(formData);
		testUtility.createMetadata(formData);
		testUtility.createMetadata(formData);
		testUtility.createMetadata(formData);
		
		
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/Metadata_1.metadata"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/Metadata_1_01.metadata"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/Metadata_1_02.metadata"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/Metadata_1_03.metadata"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/Metadata_1_04.metadata"));
		
	}
	
	@Test
	public void md_a3_create_multiple_reports_with_same_name() throws Exception {
		
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\"DuplicateResourceURLTest\",\"metadataFileName\":\"Metadata_1.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"DuplicateResourceURLTest\"}";
		testUtility.saveReport(formData);
		testUtility.saveReport(formData);
		testUtility.saveReport(formData);
		testUtility.saveReport(formData);
		testUtility.saveReport(formData);
		
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/report1.hr"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/report1_01.hr"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/report1_02.hr"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/report1_03.hr"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/report1_04.hr"));
		
		
	}
	
	@Test
	public void md_a4_create_multiple_dashboards_with_same_name() throws Exception {
		
		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-MHziG\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-MHziG\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"report1\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DuplicateResourceURLTest/report1.hr\",\"name\":\"report1.hr\",\"title\":\"report1\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"688948b4-a6f9-43b8-a5b8-514ba4f67dd1\",\"lastModified\":1661529662920},{\"id\":\"item-6fsng\",\"compType\":\"dashboard-designer-component\",\"isGrouped\":false,\"initialPosition\":{\"x\":2,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-6fsng\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":true,\"title\":\"report2\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"griditemsettings\",\"values\":{\"export\":false,\"edit\":false,\"maximize\":false}}],\"reportInfo\":{\"file\":{\"path\":\"DuplicateResourceURLTest/report1.hr\",\"name\":\"report2.hr\",\"title\":\"report2\"},\"mode\":\"dashboard\",\"filters\":[],\"component\":\"hreport\",\"extension\":\"hr\"},\"filters\":[],\"listeners\":[],\"reportId\":\"b3aff96a-940a-459a-b640-0ee1428b91b9\",\"lastModified\":1661529753065}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[{\"key\":\"breakpoints\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":1200,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":996,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":768,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":480,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":240,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"columns\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":12,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":12,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":6,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":4,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":2,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"grid\",\"values\":{\"autoSize\":true,\"compactType\":null,\"rowHeight\":100,\"isDroppable\":false,\"preventCollision\":true,\"measureBeforeMount\":false,\"isDraggable\":true,\"isResizable\":true}},{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":0,\"yOffset\":0,\"blur\":0,\"spread\":0,\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}}],\"layout\":[{\"w\":2,\"h\":2,\"x\":0,\"y\":0,\"i\":\"item-MHziG\",\"moved\":false,\"static\":false},{\"w\":2,\"h\":2,\"x\":2,\"y\":0,\"i\":\"item-6fsng\",\"moved\":false,\"static\":false}],\"designerSettings\":[{\"key\":\"parameters\",\"values\":{\"enable\":true,\"orientation\":\"right\",\"enableApplyButton\":true}}],\"parameterDrawerStatus\":false}},\"dir\":\"DuplicateResourceURLTest\",\"fileName\":\"dashboard\"}";
		testUtility.createDesign(formData);
		testUtility.createDesign(formData);
		testUtility.createDesign(formData);
		testUtility.createDesign(formData);
		testUtility.createDesign(formData);
		
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/dashboard.efwdd"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/dashboard_01.efwdd"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/dashboard_02.efwdd"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/dashboard_03.efwdd"));
		assertNotNull(serviceDB.getResourceByUrl("DuplicateResourceURLTest/dashboard_04.efwdd"));
	}
	
	*/
}
