package com.helicalinsight.metadata;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataViewUpdateTest {

	MockMvc efwMock;
	MockMvc mockMvc;

	@Autowired
	private FilterChainProxy filterChainProxy;

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

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join(File.separator, "C:", "home", "helical", "Performance", "hi", "db",
					"SampleTravelData");
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
		}
	}

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void md_a1_init() throws Exception {
		testUtility.createFolder("MetadataViewUpdateTest");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
	}

	@Test
	public void md_a2_retrieveViewLabels() throws Exception {

		String normalQuery = testUtility.retrievViewLabel(
				"{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select \\\"dim_id\\\" from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[]}");
		String dynamicQuery = testUtility.retrievViewLabel(
				"{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"groovy\",\"query\":\"if (check(\\\"${filter}.label\\\" ,\\\"travel_date\\\"))\\n {\\ntraveldateFilter = findFilterByLabel(\\\"travel_date\\\")\\ntraveldateFilterValue = traveldateFilter.value\\n//You can write your business logic here.\\n return \\\"select *  from \\\\\\\"travel_details\\\\\\\"  T1 where \\\\\\\"travel_date\\\\\\\" = \\\"+ traveldateFilterValue;\\n }\\n else\\n {\\n  return \\\"select * from  \\\\\\\"travel_details\\\\\\\"\\\"\\n } \",\"viewName\":\"View 2\",\"labels\":[],\"hasStoredProcedure\":false}");
	}

	static String normalQueryViewId = "";
	static String dynamicQueryViewId = "";

	@Test
	public void md_a3_saveView() throws Exception {
		String formData = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select \\\"dim_id\\\" from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true}]}";
		String resp = testUtility.saveView(formData);
		JSONObject object = JSONObject.fromObject(resp);
		normalQueryViewId = object.getJSONObject("response").getString("viewId");

		String formData2 = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"groovy\",\"query\":\"if (check(\\\"${filter}.label\\\" ,\\\"travel_date\\\"))\\n {\\ntraveldateFilter = findFilterByLabel(\\\"travel_date\\\")\\ntraveldateFilterValue = traveldateFilter.value\\n//You can write your business logic here.\\n return \\\"select *  from \\\\\\\"travel_details\\\\\\\"  T1 where \\\\\\\"travel_date\\\\\\\" = \\\"+ traveldateFilterValue;\\n }\\n else\\n {\\n  return \\\"select * from  \\\\\\\"travel_details\\\\\\\"\\\"\\n } \",\"viewName\":\"View 2\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}],\"hasStoredProcedure\":false,\"validate\":true,\"processedQuery\":\"select * from (select * from  \\\"travel_details\\\") foo fetch first 1 rows only\"}";
		String resp2 = testUtility.saveView(formData2);
		JSONObject object2 = JSONObject.fromObject(resp2);
		dynamicQueryViewId = object2.getJSONObject("response").getString("viewId");
	}

	static Map<String, String> columnMap = new HashMap<>();

	static String firstViewId = "";
	static String secondViewId = "";
	static String dbId = "";
	@Test
	public void md_a4_save_metadata() throws Exception {
		String response = testUtility.createMetadata(
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"49oz5\",\"dbId\":\"49oz5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[\""
						+ normalQueryViewId + "\",\"" + dynamicQueryViewId
						+ "\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataViewUpdateTest\",\"metadataReload\":true}");
		dbId = testUtility.getOuterDbId(response);
		JSONObject respObj = JSONObject.fromObject(response).getJSONObject("response");
		JSONObject metadata = respObj.getJSONObject("metadata");
		JSONObject tables = metadata.getJSONObject("tables");
		JSONObject view1 = tables.getJSONObject("View 1");
		firstViewId = view1.getString("id");
		JSONObject view2 = tables.getJSONObject("View 2");
		secondViewId = view2.getString("id");
		String id = view1.getJSONObject("columns").getJSONObject("dim_id").getString("id");
		JSONObject view2Columns = view2.getJSONObject("columns");
		columnMap.put("dim_id", id);
		Iterator<String> keys = view2Columns.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject value = (JSONObject) view2Columns.get(key);
			if (value instanceof JSONObject) {
				String columnId = value.getString("id");
				columnMap.put(key, columnId);
			}
		}
	}
	
	@Test
	public void md_a5_update_views() throws Exception {
		String formData = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}],\"viewId\":\""+firstViewId+"\"}";
		String resp = testUtility.saveView(formData);
		
		JSONObject object = JSONObject.fromObject(resp);
		normalQueryViewId = object.getJSONObject("response").getString("viewId");

		String formData2 = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"groovy\",\"query\":\"if (check(\\\"${filter}.label\\\" ,\\\"travel_date\\\"))\\n {\\ntraveldateFilter = findFilterByLabel(\\\"travel_date\\\")\\ntraveldateFilterValue = traveldateFilter.value\\n return \\\"select *  from \\\\\\\"travel_details\\\\\\\"  T1 where \\\\\\\"travel_date\\\\\\\" = \\\"+ traveldateFilterValue;\\n }\\n else\\n {\\n  return \\\"select * from  \\\\\\\"travel_details\\\\\\\"\\\"\\n } \",\"viewName\":\"View 2\",\"labels\":[{\"name\":\"travel_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"travel_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"travel_type\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_medium\",\"type\":\"text\",\"checked\":true},{\"name\":\"source_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"source\",\"type\":\"text\",\"checked\":true},{\"name\":\"destination_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"destination\",\"type\":\"text\",\"checked\":true},{\"name\":\"travel_cost\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"mode_of_payment\",\"type\":\"text\",\"checked\":true},{\"name\":\"booking_platform\",\"type\":\"text\",\"checked\":true},{\"name\":\"travelled_by\",\"type\":\"numeric\",\"checked\":true}],\"viewId\":\""+secondViewId+"\",\"hasStoredProcedure\":false,\"validate\":true,\"processedQuery\":\"select * from (select * from  \\\"travel_details\\\") foo fetch first 1 rows only\"}";
		String resp2 = testUtility.saveView(formData2);
		JSONObject object2 = JSONObject.fromObject(resp2);
		dynamicQueryViewId = object2.getJSONObject("response").getString("viewId");

	}
	@Test
	public void md_a6_update_metadata() throws Exception {
		String response = testUtility.createMetadata("{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[\""+firstViewId+"\",\""+secondViewId+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataViewUpdateTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}");
		JSONObject respObj = JSONObject.fromObject(response).getJSONObject("response");
		JSONObject metadata = respObj.getJSONObject("metadata");
		JSONObject tables = metadata.getJSONObject("tables");
		JSONObject view1 = tables.getJSONObject("View 1");
		firstViewId = view1.getString("id");
		JSONObject view2 = tables.getJSONObject("View 2");
		secondViewId = view2.getString("id");
		String id = view1.getJSONObject("columns").getJSONObject("dim_id").getString("id");
		JSONObject view2Columns = view2.getJSONObject("columns");
		Assert.assertEquals(columnMap.get("dim_id"),id);
		
		Iterator<String> keys = view2Columns.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject value = (JSONObject) view2Columns.get(key);
			if (value instanceof JSONObject) {
				String columnId = value.getString("id");
				Assert.assertEquals(columnMap.get(key),columnId);
			}
		}
		
		
	}

}
