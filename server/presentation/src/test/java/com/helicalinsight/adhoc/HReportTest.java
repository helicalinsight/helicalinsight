package com.helicalinsight.adhoc;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
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
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class HReportTest {
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	IntegrationTestUtility testutility;

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
	public void hr_a1_create_a_folder() throws Exception {
		testutility.createFolder("HReportTest");
	}
	@Test
	public void hr_a2_create_cache() throws Exception {
		String expandSchema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testutility.expand(expandSchema);
		String expandTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testutility.expand(expandTable);

	}

	static String dbid;
	@Test
	public void hr_a3_create_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"nu7uq\",\"dbId\":\"nu7uq\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":true}";
		String response = testutility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("dataSource");
		dbid = object.getString("dbId");

	}

	@Test
	public void hr_a4_report_fetchData() throws Exception {
		String fetchData = "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.dim_id\",\"alias\":\"sum_dim_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.dimdate.fiscal_month_name\",\"alias\":\"fiscal_month_name\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.dimdate.dim_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_dim_id\"}],\"groupBy\":[{\"column\":\"fiscal_month_name\",\"custom\":true},{\"column\":\"address\",\"custom\":true},{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
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
	public void hr_a5_report_saveReport() throws Exception {
		String saveReport = "{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\"},\"reportName\":\"Report_HReportTest\",\"location\":\"HReportTest\"}";
		testutility.saveReport(saveReport);
	}


	@Test
	public void hr_a6_report_saveAs_report() throws Exception {
		String saveAsReport = "{\"isHrReport\":true,\"columns\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"state\":{\"fields\":[{\"column\":\"dimdate.dim_id\",\"label\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_dim_id\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"dim_id\"},{\"column\":\"dimdate.fiscal_month_name\",\"label\":\"fiscal_month_name\",\"id\":\"cc43fb2f-4e1c-4828-9b94-70f6ea4af58d\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"fiscal_month_name\",\"isNormalTable\":true,\"tableAlias\":\"dimdate\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"fiscal_month_name\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"d234a5d2-1dd4-47af-bad8-a70124656c08\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"employee_details.employee_name\",\"label\":\"employee_name\",\"id\":\"e5d7fb2c-b04c-46f4-9de1-3dc08b0e4441\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"employee_name\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"employee_name\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_dim_id\",\"id\":\"d32bd8e2-8e71-4473-8938-885ab505d814\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\"},\"reportName\":\"Report_HReportTest2\",\"location\":\"HReportTest\"}";
		String response = testutility.saveReport(saveAsReport);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		String name = object.getString("uuid");
		Assert.assertEquals("Report_HReportTest2.hr", name);


	}

	@Test
	public void hr_a7_report_gnerate_query() throws Exception {
		String generateQuery = "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		String query = object.getString("query");
		Assert.assertTrue(!StringUtils.isBlank(query));
	}

	@Test
	public void hr_b1_report_edit_report() throws Exception {
		String editReport = "{\"isHrReport\":true,\"columns\":[{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"814072f7-c6cf-49e6-88ce-f85106049809\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"travel_details.destination_id\",\"label\":\"sum_destination_id\",\"id\":\"b68bcdb1-eb11-4c7f-9397-a1993fce3c35\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_destination_id\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"destination_id\"}],\"state\":{\"fields\":[{\"column\":\"travel_details.booking_platform\",\"label\":\"booking_platform\",\"id\":\"814072f7-c6cf-49e6-88ce-f85106049809\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"travel_details.destination_id\",\"label\":\"sum_destination_id\",\"id\":\"b68bcdb1-eb11-4c7f-9397-a1993fce3c35\",\"type\":{\"backendDataType\":\"java.lang.Integer\",\"dataType\":\"numeric\"},\"autogen_alias\":\"sum_destination_id\",\"isNormalTable\":true,\"tableAlias\":\"travel_details\",\"aggregate\":[\"db.generic.aggregate.sum\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"destination_id\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_destination_id\",\"id\":\"b68bcdb1-eb11-4c7f-9397-a1993fce3c35\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"0e932762-9257-4789-b72b-c4f25e42ed50\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\"},\"reportName\":\"Report_HReportTest2\",\"location\":\"HReportTest\",\"uuid\":\"Report_HReportTest2.hr\"}";
		String response = testutility.saveReport(editReport);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		String name = object.getString("uuid");
		Assert.assertEquals("Report_HReportTest2.hr", name);

	}

	@Test
	public void hr_b2_report_filter_report() throws Exception {
		String fetchData = "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.destination_id\",\"alias\":\"sum_destination_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.destination_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_destination_id\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Makemytrip')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String platform = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", platform);

	}

	@Test
	public void hr_b3_report_filter_search() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_medium\",\"alias\":\"display\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.travel_medium\",\"alias\":\"value\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"display\",\"custom\":true},{\"column\":\"value\",\"custom\":true}],\"orderBy\":[{\"alias\":\"display\",\"order\":\"asc\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Cab%'\"],\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"column\":\"HIUSER.travel_details.travel_medium\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":\"full\",\"prependTableNameToAlias\":false,\"distinctResults\":true}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String display = object.getString("display");
		Assert.assertEquals("Cab", display);

	}

	@Test
	public void hr_b4_report_datatype_string() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = object.getString("name");
		String type = object.getString("type");
		Assert.assertEquals("booking_platform", name);
		Assert.assertEquals("text", type);

	}

	@Test
	public void hr_b5_report_datatype_number() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.destination_id\",\"alias\":\"sum_destination_id\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.destination_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_destination_id\"}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = object.getString("name");
		String type = object.getString("type");
		Assert.assertEquals("sum_destination_id", name);
		Assert.assertEquals("numeric", type);

	}

	@Test
	public void hr_b6_report_datatype_dateTime() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_date\",\"alias\":\"travel_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"travel_date\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = object.getString("name");
		String type = object.getString("type");
		Assert.assertEquals("travel_date", name);
		Assert.assertEquals("dateTime", type);

	}

	@Test
	public void hr_b7_report_datatype_date() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.dimdate.fiscal_year\",\"alias\":\"fiscal_year\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"fiscal_year\",\"custom\":true}]},\"filters\":[{\"values\":[\"'A%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = object.getString("name");
		String type = object.getString("type");
		Assert.assertEquals("fiscal_year", name);
		Assert.assertEquals("date", type);

	}

	@Test
	public void hr_b8_report_custom_filter_contains() throws Exception {
		/**
		 * contains = my
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%my%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);


	}
	@Test
	public void hr_b9_report_custom_filter_does_not_contains() throws Exception {
		/**
		 * does not contains = my
		 */
		String fetchDta =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%my%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchDta);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Website", name1);


	}

	@Test
	public void hr_c1_report_custom_filter_custom() throws Exception {
		/**
		 * condition : !=,
		 * value = Agent
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Agent'\"],\"mode\":\"custom\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"!=\",\"encloseInQuotes\":true,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Website", name1);


	}

	@Test
	public void hr_c2_report_custom_filter_does_not_ends_with() throws Exception {
		/**
		 * does not ends with : nt
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%nt'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Website", name1);


	}

	@Test
	public void hr_c3_report_custom_filter_does_not_starts_with() throws Exception {
		/**
		 * does not starts with : A
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'A%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Website", name1);


	}
	@Test
	public void hr_c4_report_custom_filter_ends_with() throws Exception {
		/**
		 * ends with : trip
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%trip'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);


	}


	@Test
	public void hr_c5_report_custom_filter_equals() throws Exception {
		/**
		 * equals : Agent
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"Agent\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"EQUALS\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);


	}

	@Test
	public void hr_c6_report_custom_filter_is_not_null() throws Exception {
		/**
		 * is not null
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"IS NOT NULL\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);

		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name1);
		JSONObject object11 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(2);
		String name11 = object11.getString("booking_platform");
		Assert.assertEquals("Website", name11);

	}


	@Test
	public void hr_c7_report_custom_filter_is_null() throws Exception {
		/**
		 * is null
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"IS NULL\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONArray array = jsonObject.getJSONObject("response").getJSONArray("data");
		JSONArray jsonArray = new JSONArray();
		Assert.assertEquals(jsonArray, array);



	}

	@Test
	public void hr_c8_report_custom_filter_is_one_of() throws Exception {
		/**
		 * is one of  :  Makemytrip , Website
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Makemytrip','Website')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name1);
		JSONObject object11 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name11 = object11.getString("booking_platform");
		Assert.assertEquals("Website", name11);

	}

	@Test
	public void hr_c9_report_custom_filter_is_not_one_of() throws Exception {
		/**
		 * is not one of  :  Makemytrip , Website
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Makemytrip','Website')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" NOT IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Agent", name1);

	}


	@Test
	public void hr_d1_report_custom_filter_not_equals() throws Exception {
		/**
		 * not equals  :  Agent
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"Agent\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"<>\",\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name1);
		JSONObject object11 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name11 = object11.getString("booking_platform");
		Assert.assertEquals("Website", name11);


	}

	@Test
	public void hr_d2_report_custom_filter_starts_with() throws Exception {
		/**
		 * starts with  :  Web
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Web%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);

		JSONObject object11 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name11 = object11.getString("booking_platform");
		Assert.assertEquals("Website", name11);


	}

	//----------============================----------------======================================================================================

	@Test
	public void hr_d3_report_two_table_custom_filter_contains() throws Exception {
		/**
		 * contains = Agent,
		 * days = 01(equals)
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"meeting_date\",\"custom\":true},{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Agent%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[1],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.day\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"EQUALS\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);

		String date = object.getString("meeting_date");
		Assert.assertEquals("2015-01-01 16:59:00.0", date);
	}
	@Test
	public void hr_d4_report_two_table_custom_filter_does_not_contains() throws Exception {
		/**
		 * does not contains = Agent
		 * month = feb(equals)
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"meeting_date\",\"custom\":true},{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Agent%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[2],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.month\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"EQUALS\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		String date = object.getString("meeting_date");
		Assert.assertEquals("Makemytrip", name);
		Assert.assertEquals("2015-02-01 16:52:00.0", date);
		JSONObject object1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = object1.getString("booking_platform");
		Assert.assertEquals("Website", name1);
		String date1 = object1.getString("meeting_date");
		Assert.assertEquals("2015-02-01 16:52:00.0", date1);

	}

	@Test
	public void hr_d5_report_two_table_custom_filter_customs() throws Exception {
		/**
		 * condition = Makemytrip
		 * condition != 2015-01-01 16:59:00.0
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'2015-01-01 16:59:00.0'\"],\"mode\":\"custom\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"!=\",\"encloseInQuotes\":true,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'Makemytrip'\"],\"mode\":\"custom\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"=\",\"encloseInQuotes\":true,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);


	}

	@Test
	public void hr_d6_report_two_table_custom_filter_customs() throws Exception {
		/**
		 * condition = Makemytrip
		 * condition != 2015-01-01 16:59:00.0
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'2015-01-01 16:59:00.0'\"],\"mode\":\"custom\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"!=\",\"encloseInQuotes\":true,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'Makemytrip'\"],\"mode\":\"custom\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"=\",\"encloseInQuotes\":true,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);


	}
	@Test
	public void hr_d7_report_two_table_custom_filter_range_notendswith() throws Exception {
		/**
		 *not ends with = trip
		 * range = 2015-02-01 15:13:27  <-> 2015-03-01 15:13:27
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"2015-02-01 15:13:27.0\",\"2015-03-01 15:13:27.0\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"encloseInQuotes\":true,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"IN_RANGE\"},{\"values\":[\"'%trip'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";

		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);


	}
	@Test
	public void hr_d8_report_two_table_custom_filter_between_notstartswith() throws Exception {
		/**
		 * not starts with = A
		 * between = 2015-02-01 15:13:27  <-> 2023-07-01 15:13:27
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'2015-05-01 15:20:31.0' AND '2023-07-01 15:20:31.0'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"BETWEEN\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'A%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"not like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);


	}

	@Test
	public void hr_d9_report_two_table_custom_filter_isnotnull_endswith() throws Exception {
		/**
		 * ends with = t
		 * is not null
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"IS NOT NULL\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'%t'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);


	}

	@Test
	public void hr_e1_report_two_table_custom_filter_isnull_equals() throws Exception {
		/**
		 * equals = Agent
		 * is null
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"IS NULL\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"Agent\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"EQUALS\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONArray array = jsonObject.getJSONObject("response").getJSONArray("data");
		JSONArray json =  new JSONArray();
		Assert.assertEquals(json, array);


	}

	@Test
	public void hr_e2_report_two_table_custom_filter_quarter_isoneof() throws Exception {
		/**
		 * quarter = 4
		 * is one of = Agent
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"4)\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.quarter\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'Agent')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = object.getString("booking_platform");
		Assert.assertEquals("Agent", name);


	}

	@Test
	public void hr_e3_report_two_table_custom_filter_isnotoneof_greaterthan() throws Exception {
		/**
		 * greater than year = 2015
		 * is not one of = Makemytrip, website
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[2015],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\">\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.year\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'Makemytrip','Website')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" NOT IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONArray array = jsonObject.getJSONObject("response").getJSONArray("data");
		JSONArray json = new JSONArray();
		Assert.assertEquals(json, array);


	}

	@Test
	public void hr_e4_report_two_table_custom_filter_greater_equal_not_equals() throws Exception {
		/**
		 * >= 2015
		 * != makemytrip
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[2015],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\">=\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.year\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"Makemytrip\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"<>\",\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);

	}


	@Test
	public void hr_e5_report_two_table_custom_filter_islessthan_startswith() throws Exception {
		/**
		 * <20 hours
		 * starts with = A
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'A%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[20],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"<\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.hour\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);


	}

	@Test
	public void hr_e6_report_two_table_custom_filter_islessthanorequal() throws Exception {
		/**
		 * <= 3 min
		 * ALL
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'_all_')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[3],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\"<=\",\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.minute\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);
		String dateTime = obj.getString("meeting_date");
		Assert.assertEquals("2015-01-09 16:01:00.0", dateTime);

	}
	@Test
	public void hr_e7_report_two_table_custom_filter_isnotoneof() throws Exception {
		/**
		 * seconds
		 * aLL
		 */
		String fetchData =  "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'_all_')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"0)\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.Integer\",\"customCondition\":\" NOT IN (\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.dateTime.second\",\"dataType\":\"numeric\",\"parameters\":{\"datetime\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONArray arr = jsonObject.getJSONObject("response").getJSONArray("data");
		JSONArray json = new JSONArray();
		Assert.assertEquals(json,arr);


	}


	@Test
	public void hr_e8_report_two_table_custom_filter_notequals() throws Exception {
		/**
		 * not equals = 2015-01-01 16:59:00.0

		 * Agent
		 */
		String fetchData = "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Agent%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"2015-01-01 16:59:00.0\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"<>\",\"alias\":\"meeting_date\",\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);
		String dateTime = obj.getString("meeting_date");
		Assert.assertEquals("2015-01-02 13:50:00.0", dateTime);
	}



	@Test
	public void hr_e9_report_two_table_custom_filter_notinrange() throws Exception {
		/**
		 * not in range = 2023-02-01 17:46:01 ->2023-02-01 17:46:01

		 * Agent
		 */
		String fetchData = "{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Agent%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"2023-02-01\",\"2023-02-01\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"encloseInQuotes\":true,\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.typeConversion.todate\",\"dataType\":\"date\",\"parameters\":{\"column\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"NOT_IN_RANGE\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);
		String dateTime = obj.getString("meeting_date");
		Assert.assertEquals("2015-01-01 16:59:00.0", dateTime);
	}


	@Test
	public void hr_f1_report_two_table_custom_filter_notbetween() throws Exception {
		/**
		 * not in between =  2015-02-01 -> 2023-01-01
		 * Agent
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.meeting_date\",\"alias\":\"meeting_date\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"meeting_date\",\"custom\":true}]},\"filters\":[{\"values\":[\"'%Agent%'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"like\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"column\":\"HIUSER.travel_details.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"},{\"values\":[\"'2015-02-01' AND '2023-01-01'\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\"NOT BETWEEN\",\"encloseInQuotes\":false,\"alias\":\"meeting_date\",\"databaseFunction\":{\"functionName\":\"sql.typeConversion.todate\",\"dataType\":\"date\",\"parameters\":{\"column\":\"meeting_details.meeting_date\"}},\"label\":\"meeting_date\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_date\",\"id\":1,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} AND ${1} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);
		String dateTime = obj.getString("meeting_date");
		Assert.assertEquals("2015-01-01 16:59:00.0", dateTime);
	}


	@Test
	public void hr_f2_report_two_table_avg() throws Exception {
		/**
		 * Avg = total cost
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"avg_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.avg\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.avg\",\"alias\":\"avg_travel_cost\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int avg = obj.getInt("avg_travel_cost");
		Assert.assertEquals(11928, avg);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Ahmed Haider", name);



	}

	@Test
	public void hr_f3_report_two_table_count() throws Exception {
		/**
		 * count = travel cost
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"count_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.count\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.count\",\"alias\":\"count_travel_cost\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int count = obj.getInt("count_travel_cost");
		Assert.assertEquals(7, count);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Ahmed Haider", name);
	}

	@Test
	public void hr_f4_report_two_table_distinct() throws Exception {
		/**
		 * distinct = distinct travel cost
		 * employee_name
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"distinct_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.distinct\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.distinct\",\"alias\":\"distinct_travel_cost\"}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int distinct = obj.getInt("distinct_travel_cost");
		Assert.assertEquals(300, distinct);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Bradley Smith", name);
	}


	@Test
	public void hr_f5_report_two_table_max() throws Exception {
		/**
		 * max = max travel cost
		 * employee_name
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"max_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.max\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.max\",\"alias\":\"max_travel_cost\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int max = obj.getInt("max_travel_cost");
		Assert.assertEquals(68000, max);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Ahmed Haider", name);
	}



	@Test
	public void hr_f6_report_two_table_min() throws Exception {
		/**
		 * min = min travel cost
		 * employee_name
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"min_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.min\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.min\",\"alias\":\"min_travel_cost\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int min = obj.getInt("min_travel_cost");
		Assert.assertEquals(500, min);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Ahmed Haider", name);
	}

	@Test
	public void hr_f7_report_two_table_sum() throws Exception {
		/**
		 * sum = sum travel cost
		 * employee_name
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"alias\":\"sum_travel_cost\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.employee_name\",\"alias\":\"employee_name\",\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.travel_details.travel_cost\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_travel_cost\"}],\"groupBy\":[{\"column\":\"employee_name\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		int sum = obj.getInt("sum_travel_cost");
		Assert.assertEquals(83500, sum);
		String name = obj.getString("employee_name");
		Assert.assertEquals("Ahmed Haider", name);
	}

	//==================================================$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$44==============================================================

	static String viewId;
	@Test
	public void hr_f8_create_view() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");
		map.put("formData","{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"travel_details\\\"\",\"viewName\":\"View 1\",\"labels\":[]}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
	}


	@Test
	public void hr_f9_save_view() throws Exception {
		String saveView = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"travel_details\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}]}";
		String response = testutility.saveView(saveView);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		viewId = object.getString("viewId");
	}





	static String viewIdAfterSave = "";
	@Test
	public void hr_g1_edit_metadata_with_view() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"7880\",\"action\":\"noChange\"},{\"id\":\"7881\",\"action\":\"noChange\"},{\"id\":\"7882\",\"action\":\"noChange\"},{\"id\":\"7883\",\"action\":\"noChange\"},{\"id\":\"7884\",\"action\":\"noChange\"},{\"id\":\"7885\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbid+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7208\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[\""+viewId+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\",\"uniqueId\":true}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject tables = responseObject.getJSONObject("metadata").getJSONObject("tables");
		Assert.assertTrue(tables.containsKey("View 1"));
		JSONObject view1 = tables.getJSONObject("View 1");
		viewIdAfterSave = view1.getString("id");

	}

	@Test
	public void hr_g3_report_view_table_column() throws Exception {
		/**
		 * viewfortest=travel_detials
		 *column = booking_platform
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 1.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);
	}

	@Test
	public void hr_g4_report_view_table_alias() throws Exception {
		/**
		 * view=travel_detials
		 *column = booking_platform alias bk
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 1.booking_platform\",\"alias\":\"bk\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"bk\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = obj.getString("name");
		Assert.assertEquals("bk", name);
	}

	@Test
	public void hr_g5_report_two_column_with_view() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.meeting_details.cancellation_reason\",\"alias\":\"cancellation_reason\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"cancellation_reason\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = obj.getString("name");
		Assert.assertEquals("booking_platform", name);
		JSONObject obj2 = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("2");
		String name2 = obj2.getString("name");
		Assert.assertEquals("cancellation_reason", name2);
	}




	//=========================
	static String joinId;
	@Test
	public void hr_g6_edit_metadata_with_view_join() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"lvcm-krjh-gd6y-rblt-x8\",\"left\":{\"column\":\"travel_id\",\"tableId\":\""+viewIdAfterSave+"\",\"dbId\":\""+dbid+"\",\"table\":\"View 1\"},\"right\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\""+dbid+"\",\"table\":\"dimdate\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbid+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbid+"\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\",\"uniqueId\":true}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject jsonObject = JSONObject.fromObject(response);
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
		JSONArray joinsArray = responseObject.getJSONObject("metadata").getJSONArray("joins");
		Assert.assertEquals(6, joinsArray.size());

	}

	@Test
	public void hr_g7_report_three_column_with_view() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 1.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.dimdate.created_date\",\"alias\":\"created_date\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true},{\"column\":\"created_date\",\"custom\":true},{\"column\":\"address\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = obj.getString("name");
		Assert.assertEquals("booking_platform", name);
		JSONObject obj2 = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("2");
		String name2 = obj2.getString("name");
		Assert.assertEquals("created_date", name2);
		JSONObject obj3 = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("3");
		String name3 = obj3.getString("name");
		Assert.assertEquals("address", name3);

	}

	@Test
	public void hr_g8_dynamic_query_vaildate() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");
		map.put("formData",
				"{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"groovy\",\"query\":\"def q = \\\"select * from \\\\\\\"travel_details\\\\\\\"\\\"\\nif(check(\\\"${filter}.label\\\" , \\\"booking\\\")){\\n    def f = findFilterByLabel(\\\"booking\\\")\\n    return q+\\\" where \\\\\\\"booking_platform\\\\\\\" = $f.value \\\"\\n}\\nelse{\\n    return q\\n}\",\"viewName\":\"View 2\",\"labels\":[],\"hasStoredProcedure\":false}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		String query = object.getString("processedQuery");
		//Assert.assertEquals("select * from (select * from \"travel_details\") foo FETCH FIRST 10 ROWS ONLY", query);
	}

	@Test
	public void hr_g9_dynamic_query_excute() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");
		map.put("formData",
				"{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"groovy\",\"query\":\"def q = \\\"select * from \\\\\\\"travel_details\\\\\\\"\\\"\\nif(check(\\\"${filter}.label\\\" , \\\"booking\\\")){\\n    def f = findFilterByLabel(\\\"booking\\\")\\n    return q+\\\" where \\\\\\\"booking_platform\\\\\\\" = $f.value \\\"\\n}\\nelse{\\n    return q\\n}\",\"viewName\":\"View 2\",\"labels\":[],\"hasStoredProcedure\":false,\"validate\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();

		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject object = jsonObject.getJSONObject("response");
		String query = object.getString("processedQuery");
		//Assert.assertEquals("select * from (select * from \"travel_details\") foo fetch first 10 rows only", query);
	}

	@Test
	public void hr_h1_Dynamic_query_saveview() throws Exception {
		String savView =
				"{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"groovy\",\"query\":\"def q = \\\"select * from \\\\\\\"travel_details\\\\\\\"\\\"\\nif(check(\\\"${filter}.label\\\" , \\\"booking\\\")){\\n    def f = findFilterByLabel(\\\"booking\\\")\\n    return q+\\\" where \\\\\\\"booking_platform\\\\\\\" = $f.value \\\"\\n}\\nelse{\\n    return q\\n}\",\"viewName\":\"View 2\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}],\"hasStoredProcedure\":false,\"validate\":true,\"processedQuery\":\"select * from (select * from \\\"travel_details\\\") foo fetch first 1 rows only\"}";
		String response = testutility.saveView(savView);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		viewId = object.getString("viewId");
	}



	@Test
	public void hr_h2_edit_metadata_with_view2() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"8105\",\"action\":\"noChange\"},{\"id\":\"8106\",\"action\":\"noChange\"},{\"id\":\"8107\",\"action\":\"noChange\"},{\"id\":\"8108\",\"action\":\"noChange\"},{\"id\":\"8109\",\"action\":\"noChange\"},{\"id\":\"8110\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbid+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[\""+viewId+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\",\"uniqueId\":true}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject tables = responseObject.getJSONObject("metadata").getJSONObject("tables");
		Assert.assertTrue(tables.containsKey("View 2"));
		viewIdAfterSave = tables.getJSONObject("View 2").getString("id");

	}


	@Test
	public void hr_h4_report_generated_query() throws Exception {
		String generateQuery =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 2.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		String query = object.getString("query");
		//Assert.assertEquals("select \n\t\"View 2\".\"booking_platform\" as \"booking_platform\" \nfrom\n\t(select * from \"travel_details\")  \"View 2\" \ngroup by\n\t\"View 2\".\"booking_platform\" FETCH FIRST 10 ROWS ONLY",query);
	}
	@Test
	public void hr_h5_report_view2_table_column() throws Exception {
		/**
		 * view2
		 *column = booking_platform without filter or condition
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 2.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);

		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Agent", name);

		JSONObject obj1 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(1);
		String name1 = obj1.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name1);

		JSONObject obj11 = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(2);
		String name11 = obj11.getString("booking_platform");
		Assert.assertEquals("Website", name11);

	}

	@Test
	public void hr_h6_report_view2_table_column_condition() throws Exception {
		/**
		 * view2
		 *column = booking_platform ,makemytrip
		 *
		 */
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 2.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Makemytrip')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.View 2.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);
	}
	@Test
	public void hr_h7_report_generated_query_condition() throws Exception {
		String generateQuery =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 2.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"'Makemytrip')\"],\"mode\":\"auto\",\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"alias\":\"booking_platform\",\"label\":\"booking_platform\",\"isCustomValue\":true,\"column\":\"HIUSER.View 2.booking_platform\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject object = jsonObject.getJSONObject("response");
		String query = object.getString("query");
		Assert.assertEquals("select \n\t\"View 2\".\"booking_platform\" as \"booking_platform\" \nfrom\n\t(select * from \"travel_details\")  \"View 2\" \nwhere\n\t( \"View 2\".\"booking_platform\"  IN ( 'Makemytrip') ) \ngroup by\n\t\"View 2\".\"booking_platform\" FETCH FIRST 10 ROWS ONLY",query);
	}

	/*	@Test
		public void hr_h8_edit_metadata_with_view2_join() throws Exception {
			String editMetadata =
					"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"rgc4-iv1a-x3fl-sy69-e9\",\"left\":{\"column\":\"travel_date\",\"tableId\":\""+viewIdAfterSave+"\",\"dbId\":\""+dbid+"\",\"table\":\"View 2\"},\"right\":{\"column\":\"employee_name\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\""+dbid+"\",\"table\":\"employee_details\"}},{\"id\":\"8123\",\"action\":\"noChange\"},{\"id\":\"8124\",\"action\":\"noChange\"},{\"id\":\"8125\",\"action\":\"noChange\"},{\"id\":\"8126\",\"action\":\"noChange\"},{\"id\":\"8127\",\"action\":\"noChange\"},{\"id\":\"8128\",\"action\":\"noChange\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbid+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\",\"uniqueId\":true}";
			String response = testutility.createMetadata(editMetadata);
			JSONObject jsonObject = JSONObject.fromObject(response);
			JSONObject responseObject = jsonObject.getJSONObject("response");
			JSONArray jsonArray = responseObject.getJSONObject("metadata").getJSONArray("joins");
			Assert.assertEquals(7, jsonArray.size());
		}*/

	@Test
	public void hr_h9_metadata_validate_security() throws Exception {
		String evaluateSecurity =
				"{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
		testutility.validateSecurity(evaluateSecurity);

	}
	static String expressionId;
	@Test
	public void hr_i1_metadata_apply_security() throws Exception {
		String saveSecurity =
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"securityForView2\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+viewIdAfterSave+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
		String response = testutility.saveSecurity(saveSecurity);
		JSONObject object = JSONObject.fromObject(response);
		expressionId = object.getJSONObject("response").getString("expressionId");

	}

	//@Test
	public void hr_i2_1_metadata_save_security_condition_if() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"8169\",\"action\":\"noChange\"},{\"id\":\"8170\",\"action\":\"noChange\"},{\"id\":\"8171\",\"action\":\"noChange\"},{\"id\":\"8172\",\"action\":\"noChange\"},{\"id\":\"8173\",\"action\":\"noChange\"},{\"id\":\"8174\",\"action\":\"noChange\"},{\"id\":\"8175\",\"action\":\"noChange\"}],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"7208\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\"}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject object = JSONObject.fromObject(response);
		String message = object.getJSONObject("response").getString("message");
		Assert.assertEquals("Successfully saved metadata file", message);
		JSONObject table = object.getJSONObject("response").getJSONObject("tables");
		Assert.assertFalse(table.containsKey("View 2"));

	}

	@Test
	public void hr_i3_report_generate_two_columns_diff_table() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 1.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.geo_cordinates.latitude\",\"alias\":\"sum_latitude\",\"aggregate\":true,\"aggregateList\":[\"db.generic.aggregate.sum\"],\"floatingType\":\"discrete\"}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.geo_cordinates.latitude\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_latitude\"}],\"groupBy\":[{\"column\":\"booking_platform\",\"custom\":true}]},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("1");
		String name = obj.getString("name");
		Assert.assertEquals("booking_platform", name);
		JSONObject obj2 = jsonObject.getJSONObject("response").getJSONArray("metadata").getJSONObject(0).getJSONObject("2");
		String name2 = obj2.getString("name");
		Assert.assertEquals("sum_latitude", name2);
	}

	@Test
	public void hr_i4_groovy_metadata_security_validate() throws Exception {
		String evaluateSecurity =
				"{\"executionType\":\"groovy\",\"data\":{\"condition\":\"\\n\\t\\t   import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n                def evalCondition() {\\n                        String userName = GroovyUsersSession.getValue('${user}.name');\\n                        if (userName.equalsIgnoreCase(\\\"'hiadmin'\\\")) {\\n                            return true\\n                        } else {\\n                            return false\\n                        }        \\n                }\"}}";
		testutility.validateSecurity(evaluateSecurity);

	}

	@Test
	public void hr_i5_groovy_metadata_security_apply() throws Exception {
		String saveSecurity =
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"securityOnView2\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"groovy\",\"on\":[\""+viewIdAfterSave+"\"],\"condition\":\"\\n\\t\\t   import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n                def evalCondition() {\\n                        String userName = GroovyUsersSession.getValue('${user}.name');\\n                        if (userName.equalsIgnoreCase(\\\"'hiadmin'\\\")) {\\n                            return true\\n                        } else {\\n                            return false\\n                        }        \\n                }\",\"action\":\"add\"}]}";
		String response = testutility.saveSecurity(saveSecurity);
		JSONObject jsonObject = JSONObject.fromObject(response);
		expressionId = jsonObject.getJSONObject("response").getString("expressionId");


	}

	@Test
	public void hr_i6_metadata_save_security_groovy() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"id\":\"8237\",\"action\":\"noChange\"},{\"id\":\"8238\",\"action\":\"noChange\"},{\"id\":\"8239\",\"action\":\"noChange\"},{\"id\":\"8240\",\"action\":\"noChange\"},{\"id\":\"8241\",\"action\":\"noChange\"},{\"id\":\"8242\",\"action\":\"noChange\"},{\"id\":\"8243\",\"action\":\"noChange\"}],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbid+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_HReportTest\",\"location\":\"HReportTest\",\"metadataReload\":false,\"uuid\":\"Metadata_HReportTest.metadata\"}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject table = jsonObject.getJSONObject("response").getJSONObject("tables");
		Assert.assertFalse(table.containsKey("View 2"));
	}


	//@Test
	public void hr_i8_report_generate_two_columns_diff_table_one_with_view1() throws Exception {
		String fetchData =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\",\"columns\":[{\"column\":\"HIUSER.View 1.booking_platform\",\"alias\":\"booking_platform\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"},{\"column\":\"HIUSER.geo_cordinates.latitude\",\"alias\":\"latitude\",\"floatingType\":\"discrete\"}],\"functions\":{},\"limitBy\":10,\"prependTableNameToAlias\":false}";
		String response = testutility.fetchData(fetchData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject obj = jsonObject.getJSONObject("response").getJSONArray("data").getJSONObject(0);
		String name = obj.getString("booking_platform");
		Assert.assertEquals("Makemytrip", name);
		String address = obj.getString("address");
		Assert.assertEquals("Noida", address);
		int latitude = obj.getInt("latitude");
		Assert.assertEquals(27, latitude);
	}
	@Test
	public void hr_i9_save_report() throws Exception {
		String saveReport =
				"{\"isHrReport\":true,\"columns\":[{\"column\":\"View 1.booking_platform\",\"label\":\"booking_platform\",\"id\":\"109d19b8-ef72-43cc-ad0c-c5dbde934e3e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":false,\"tableAlias\":\"View 1\",\"groupBy\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"62bcd160-1217-4e8c-a7a5-c1bc36a19dc6\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"geo_cordinates.latitude\",\"label\":\"sum_latitude\",\"id\":\"c5d0c9f4-49fd-424f-a509-948121aab7a9\",\"type\":{\"backendDataType\":\"java.lang.Double\",\"dataType\":\"numeric\"},\"autogen_alias\":\"latitude\",\"isNormalTable\":true,\"tableAlias\":\"geo_cordinates\",\"aggregate\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"latitude\"}],\"state\":{\"fields\":[{\"column\":\"View 1.booking_platform\",\"label\":\"booking_platform\",\"id\":\"109d19b8-ef72-43cc-ad0c-c5dbde934e3e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"booking_platform\",\"isNormalTable\":false,\"tableAlias\":\"View 1\",\"groupBy\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"booking_platform\"},{\"column\":\"employee_details.address\",\"label\":\"address\",\"id\":\"62bcd160-1217-4e8c-a7a5-c1bc36a19dc6\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"address\",\"isNormalTable\":true,\"tableAlias\":\"employee_details\",\"groupBy\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"address\"},{\"column\":\"geo_cordinates.latitude\",\"label\":\"sum_latitude\",\"id\":\"c5d0c9f4-49fd-424f-a509-948121aab7a9\",\"type\":{\"backendDataType\":\"java.lang.Double\",\"dataType\":\"numeric\"},\"autogen_alias\":\"latitude\",\"isNormalTable\":true,\"tableAlias\":\"geo_cordinates\",\"aggregate\":[],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"row\",\"floatingType\":\"\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false,\"metaDataAlias\":\"latitude\"}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"e34a61ef-6b75-4706-b08b-d5d9e9bf5333\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}},{\"value\":\"sum_latitude\",\"id\":\"c5d0c9f4-49fd-424f-a509-948121aab7a9\",\"subVizType\":\"\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]},\"detail\":{\"fields\":[]}}],\"activeMark\":\"e34a61ef-6b75-4706-b08b-d5d9e9bf5333\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"pre-execution\",\"value\":\"\",\"title\":\"Pre Execution\"},{\"id\":\"pre-fetch\",\"value\":\"\",\"title\":\"Pre Fetch\"},{\"id\":\"post-fetch\",\"value\":\"\",\"title\":\"Post Fetch\"},{\"id\":\"post-execution\",\"value\":\"\",\"title\":\"Post Execution\"}],\"selectedScript\":\"pre-execution\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":false,\"drillDown\":false,\"drillThrough\":false,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"subTotals\",\"label\":\"Row Sub Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":32,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":24,\"fontColor\":{\"a\":1,\"b\":0,\"g\":0,\"r\":0},\"alignment\":\"center\",\"position\":\"top\"},\"format\":{\"formatFields\":[],\"formatDatatype\":\"\",\"activeFieldId\":\"\"},\"cache\":{\"isCacheEnabled\":false,\"interval\":\"00:00:01\"},\"bar\":{\"barType\":\"stacked\"},\"legend\":{\"legendPosition\":\"right\"},\"formatColor\":{\"defaultColor\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1},\"showAll\":false,\"dataColors\":[],\"formatColorStyle\":\"\",\"formatColorField\":\"\",\"minimum\":{\"r\":183,\"g\":192,\"b\":232,\"a\":1},\"maximum\":{\"r\":84,\"g\":108,\"b\":230,\"a\":1}}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\"},\"classifier\":\"db.generic\",\"metadata\":{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_HReportTest.metadata\"},\"reportName\":\"HReport_save\",\"location\":\"HReportTest\"}";
		String response = testutility.saveReport(saveReport);
		JSONObject jsonObject = JSONObject.fromObject(response);
		String msg = jsonObject.getJSONObject("response").getString("message");
		Assert.assertEquals("Successfully saved report file", msg);
	}
	@Test
	public void hr_j1_delete_report() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		String input = "[\"HReportTest/HReport_save.hr\"]";
		map.put("sourceArray", input);
		map.put("action", "delete");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"))
				.andReturn();

	}
	@Test
	public void hr_j2_metadata_save_forFilter_Expression() throws Exception {
		String editMetadata =
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"By125fZTs\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"Metadata_test\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"HReportTest\",\"uniqueId\":true}";
		String response = testutility.createMetadata(editMetadata);
		JSONObject jsonObject = JSONObject.fromObject(response);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertEquals("Successfully saved metadata file", message);

	}

	@Test
	public void hr_j4_generate_query_filter_expression() throws Exception {
		String generateQuery =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_test.metadata\",\"metadataName\":\"Metadata_test\",\"metadataDir\":\"HReportTest\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"travel_details_booking_platform\"}],\"functions\":{\"groupBy\":[{\"column\":\"travel_details_booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"Agent\"],\"mode\":\"auto\",\"dataType\":\"java.lang.String\",\"orderBy\":\"\",\"isFilterEditable\":false,\"encloseInQuotes\":false,\"dateTimeToggle\":false,\"label\":\"travel_details_booking_platform\",\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"HIUSER.travel_details.booking_platform\",\"rangeValuesType\":\"\",\"id\":0,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true}],\"customFilterExpression\":\"${0}\",\"limitBy\":1000,\"prependTableNameToAlias\":true}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject object = JSONObject.fromObject(response);
		String query = object.getJSONObject("response").getString("query");
		Assert.assertEquals("select \n\t\"HIUSER\".\"travel_details\".\"booking_platform\" as \"travel_details_booking_platform\" \nfrom\n\t\"HIUSER\".\"travel_details\" \nwhere\n\t(\"HIUSER\".\"travel_details\".\"booking_platform\" = 'Agent') \ngroup by\n\t\"HIUSER\".\"travel_details\".\"booking_platform\" FETCH FIRST 1000 ROWS ONLY", query);
	}

	@Test
	public void hr_j5_generate_query_filter_expression_column_and_value() throws Exception {
		String generateQuery =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_test.metadata\",\"metadataName\":\"Metadata_test\",\"metadataDir\":\"HReportTest\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"travel_details_booking_platform\"}],\"functions\":{\"groupBy\":[{\"column\":\"travel_details_booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"Agent\"],\"mode\":\"auto\",\"dataType\":\"java.lang.String\",\"orderBy\":\"\",\"isFilterEditable\":false,\"encloseInQuotes\":false,\"dateTimeToggle\":false,\"label\":\"travel_details_booking_platform\",\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"HIUSER.travel_details.booking_platform\",\"rangeValuesType\":\"\",\"id\":0,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true},{\"values\":[\"25\"],\"mode\":\"auto\",\"dataType\":\"java.lang.String\",\"orderBy\":\"\",\"isFilterEditable\":false,\"dateTimeToggle\":false,\"label\":\"travel_details_booking_platform_1\",\"isCustomValue\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"HIUSER.travel_details.booking_platform\",\"rangeValuesType\":\"\",\"id\":1,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true}],\"customFilterExpression\":\"${0}.column AND ${1}.value\",\"limitBy\":1000,\"prependTableNameToAlias\":true}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject object = JSONObject.fromObject(response);
		String query = object.getJSONObject("response").getString("query");
		Assert.assertEquals("select \n\t\"HIUSER\".\"travel_details\".\"booking_platform\" as \"travel_details_booking_platform\" \nfrom\n\t\"HIUSER\".\"travel_details\" \nwhere\n\t(\"HIUSER\".\"travel_details\".\"booking_platform\" AND 25) \ngroup by\n\t\"HIUSER\".\"travel_details\".\"booking_platform\" FETCH FIRST 1000 ROWS ONLY",query);
	}

	//@Test
	public void hr_j6_generate_query_filter_expression_column_and_value_having() throws Exception {
		String generateQuery =
				"{\"location\":\"HReportTest\",\"metadataFileName\":\"Metadata_test.metadata\",\"metadataName\":\"Metadata_test\",\"metadataDir\":\"HReportTest\",\"columns\":[{\"column\":\"HIUSER.travel_details.booking_platform\",\"alias\":\"travel_details_booking_platform\"},{\"column\":\"HIUSER.meeting_details.meeting_id\",\"alias\":\"sum_meeting_id\",\"aggregate\":true}],\"functions\":{\"aggregate\":[{\"column\":\"HIUSER.meeting_details.meeting_id\",\"function\":\"db.generic.aggregate.sum\",\"alias\":\"sum_meeting_id\"}],\"groupBy\":[{\"column\":\"travel_details_booking_platform\",\"custom\":true}]},\"filters\":[{\"values\":[\"Agent\"],\"mode\":\"auto\",\"dataType\":\"java.lang.String\",\"orderBy\":\"\",\"isFilterEditable\":false,\"encloseInQuotes\":false,\"dateTimeToggle\":false,\"label\":\"travel_details_booking_platform\",\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"HIUSER.travel_details.booking_platform\",\"rangeValuesType\":\"\",\"id\":0,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true},{\"values\":[\"25\"],\"mode\":\"auto\",\"dataType\":\"java.lang.String\",\"orderBy\":\"\",\"isFilterEditable\":false,\"dateTimeToggle\":false,\"label\":\"travel_details_booking_platform_1\",\"isCustomValue\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"column\":\"HIUSER.travel_details.booking_platform\",\"rangeValuesType\":\"\",\"id\":1,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true}],\"having\":[{\"values\":[\"800\"],\"mode\":\"auto\",\"dataType\":\"java.lang.Integer\",\"orderBy\":\"\",\"isFilterEditable\":false,\"dateTimeToggle\":false,\"label\":\"sum_meeting_id\",\"isCustomValue\":true,\"column\":\"HIUSER.meeting_details.meeting_id\",\"function\":\"db.generic.aggregate.sum\",\"rangeValuesType\":\"\",\"id\":2,\"condition\":\"EQUALS\",\"valuesRange\":{},\"rangeSelectionToggole\":true}],\"customFilterExpression\":\"${0}.column AND ${1}.value\",\"customHavingExpression\":\"${0}\",\"limitBy\":1000,\"prependTableNameToAlias\":true}";
		String response = testutility.generateQuery(generateQuery);
		JSONObject object = JSONObject.fromObject(response);
		String query = object.getJSONObject("response").getString("query");
		Assert.assertEquals("select \n\t\"HIUSER\".\"travel_details\".\"booking_platform\" as \"travel_details_booking_platform\",\n \tsum(\"HIUSER\".\"meeting_details\".\"meeting_id\") as \"sum_meeting_id\" \nfrom\n\t\"HIUSER\".\"travel_details\" \n\tinner join \"HIUSER\".\"employee_details\" on (\"HIUSER\".\"employee_details\".\"employee_id\" = \"HIUSER\".\"travel_details\".\"travelled_by\") \n\tinner join \"HIUSER\".\"meeting_details\" on (\"HIUSER\".\"employee_details\".\"employee_id\" = \"HIUSER\".\"meeting_details\".\"meeting_by\") \nwhere\n\t(\"HIUSER\".\"travel_details\".\"booking_platform\" AND 25) \ngroup by\n\t\"HIUSER\".\"travel_details\".\"booking_platform\" \nhaving\n\t(sum(\"HIUSER\".\"meeting_details\".\"meeting_id\") = 800) FETCH FIRST 1000 ROWS ONLY",query);
	}

	//@Test
	public void hr_j7_cleanResources() throws Exception {
		testutility.deleteResource("HReportTest");
	}
}
