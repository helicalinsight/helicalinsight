package com.helicalinsight.adhoc;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class HReportMetadataTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;

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

	/**
	 * 0. Clean 
	 * 1. Create Folder and Metadata with default datasource 
	 * 2. Create Report 
	 * 3. Delete Metadata 
	 * 4. Do getReport api call -> should throw error 
	 * 5.Do getReportForEdit api call -> should get report details. 
	 * 6. Clean
	 */

	@Test
	public void md_a1_create_a_folder() throws Exception {
		testUtility.clearRecycleBin();
		testUtility.createFolder("HReportMetadataTest");
	}

	@Test
	public void md_a2_createCache() throws Exception {
		String cat = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(cat);
		String schema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(schema);
	}

	@Test
	public void md_a3_createMetadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"HReportMetadataTest\",\"uniqueId\":true}";
		testUtility.createMetadata(formData);
	}

	@Test
	public void md_a4_createReport() throws Exception {
		
		String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"geo_cordinates.location\",\"label\":\"location\",\"id\":\"868f1836-e9be-4abd-b67f-3bc3e6099d21\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"location\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"geo_cordinates.location_id\",\"label\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_location_id\",\"isNormalTable\":true,\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_location_id\",\"id\":\"32082f0a-8699-4522-b3a3-a7115b3b2af8\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"ea0ccea9-ad93-471a-9ba3-7c951b4bd87a\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-c305e020-c816-49ce-b315-d089080523e5\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":{\"a\":0,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"metadata\":{\"location\":\"HReportMetadataTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"HReportMetadataTest\"}";
		testUtility.saveReport(formData);
		String getReport = "{\"dir\":\"HReportMetadataTest\",\"file\":\"report1.hr\"}";
		String response = testUtility.getReportForEdit(getReport);
		JSONObject metadata = JSONObject.fromObject(response)
				.getJSONObject("response")
				.getJSONObject("data").getJSONObject("metadata").getJSONObject("data");
		Assert.assertNotNull(metadata);
	}

	@Test
	public void md_a5_deleteMetadata() throws Exception {
		testUtility.deleteResource("HReportMetadataTest/SaveSelectAll.metadata");
	}

	@Test
	public void md_a6_openReport_shouldThrowError() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "report");
		map.put("service", "getReport");
		map.put("formData", "{\"dir\":\"HReportMetadataTest\",\"file\":\"report1.hr\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject response = JSONObject.fromObject(result.getResponse().getContentAsString())
				.getJSONObject("response");
		Assert.assertEquals("Error: EfwServiceException: Resource does not exist.", response.getString("message"));
	}

	@Test
	public void md_a7_editReport_shouldReportDetails() throws Exception {
		String formData = "{\"dir\":\"HReportMetadataTest\",\"file\":\"report1.hr\"}";
		String response = testUtility.getReportForEdit(formData);
		JSONObject responseObj = JSONObject.fromObject(response).getJSONObject("response");
		Assert.assertNotNull(responseObj.getJSONObject("data"));
	}

	@Test
	public void md_a8_deleteFolder() throws Exception {
		testUtility.deleteResource("HReportMetadataTest");
	}

	@Test
	public void md_a9_clean() throws Exception {
		testUtility.clearRecycleBin();
	}

}
