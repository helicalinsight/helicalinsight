package com.helicalinsight.adhoc;

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

import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDuplicateTableJoinsTest {
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

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
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	
	@Test
	public void md_a1_init() throws Exception {
		testUtility.createFolder("MetadataDuplicateTableAndColumnJoinsTest");
		String formData = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(formData);
		String formData2 ="{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(formData2);
	}
	
	static JSONArray noChangeJoins = null;
	private void  prepareNoChangeJoins(JSONArray joinsArray) {
 		noChangeJoins = new JSONArray();
 		for(Object object : joinsArray) {
 			JSONObject eachJoin = (JSONObject) object;
 			JSONObject newJoins = new JSONObject();
 			newJoins.put("id",eachJoin.getString("id"));
 			newJoins.put("action", "noChange");
 			noChangeJoins.add(newJoins);
 		}
 	}
	
	static String dbId;
	static String employeeDetailsId = "";
	@Test
	public void md_a2_create_metadata() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String joins = testUtility.prepareNoChangeJoins(fetchJoins);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"dbId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataDuplicateTableAndColumnJoinsTest\",\"metadataReload\":true}";
		String responseString =  testUtility.createMetadata(formData);
		dbId = testUtility.getOuterDbId(responseString);
		employeeDetailsId = JSONObject.fromObject(responseString)
				.getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("tables")
				.getJSONObject("employee_details")
				.getString("id");
	}
	
	@Test
	public void md_a3_duplicate_table_column_addJoin() throws Exception {
		String fetchCols = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"dbId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"aqs3-v4v2-jse9-20ss-w2/a8kg-cp33-qc2c-s6y3-za/00ii-vg0v-5xo4-8yor-g0\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
		String response = testUtility.fetchColumns(fetchCols);
		String dimIdId  = JSONObject.fromObject(response).getJSONObject("response")
				.getJSONObject("metadata").getJSONObject("table")
				.getJSONObject("dimdate")
				.getJSONObject("columns")
				.getJSONObject("dim_id").getString("id");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"id\":\"i8l8-s1we-561i-gir8-mn\",\"originalId\":\""+dimIdId+"\",\"connId\":\""+dbId+"\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"duplicate\":true}]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"w0v9-93pj-yg2h-69yi-uj\",\"left\":{\"column\":\"dim_id_1\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\""+dbId+"\",\"table\":\"dimdate\"},\"right\":{\"column\":\"employee_id\",\"tableId\":\""+employeeDetailsId+"\",\"dbId\":\""+dbId+"\",\"table\":\"employee_details\"}}],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"connId\":\""+dbId+"\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataDuplicateTableAndColumnJoinsTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}";
		String mdResponse = testUtility.createMetadata(formData);
		JSONArray joins = JSONObject.fromObject(mdResponse).getJSONObject("response")
				.getJSONObject("metadata").getJSONArray("joins");
		Assert.assertEquals(1,joins.size());
	}
	
	
}
