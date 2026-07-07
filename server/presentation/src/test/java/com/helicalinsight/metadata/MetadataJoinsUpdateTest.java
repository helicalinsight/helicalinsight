package com.helicalinsight.metadata;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataJoinsUpdateTest {
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
		testUtility.createFolder("MetadataJoinsUpdateTest");
		String schema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(schema);
		String table ="{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(table);
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
	static Map<String,Integer> tableIds = null;
	static JSONArray joins = null;
	@Test
	public void md_a2_create_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"dbId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataJoinsUpdateTest\",\"metadataReload\":true}";
		String responseString =  testUtility.createMetadata(formData);
		dbId = testUtility.getOuterDbId(responseString);
		JSONObject responseObject = JSONObject.fromObject(responseString).getJSONObject("response");
		joins= responseObject.getJSONObject("metadata").getJSONArray("joins");
		Assert.assertNotNull(joins);
		Assert.assertEquals(5, joins.size());
		tableIds = testUtility.getSingleConnectionMetadataTableMap(responseString);
	}
	
	@Test
	public void md_a3_updateMetadata() throws Exception {
		JSONObject update =  null;
		
		for(Object join : joins) {
			JSONObject joinOb = (JSONObject) join;
			if (joinOb.getJSONObject("left").getString("table").equalsIgnoreCase("dimdate")
					|| joinOb.getJSONObject("right").getString("table").equalsIgnoreCase("dimdate")) {
				update = joinOb;
			}
			else {
				joinOb.discard("type");
				joinOb.discard("operator");
				joinOb.discard("left");
				joinOb.discard("right");
				joinOb.put("action", "noChange");
			}
		}
		
		Integer geo = tableIds.get("geo_cordinates");
		Integer emp = tableIds.get("employee_details");
		Integer dim = tableIds.get("dimdate");
		String joinId = update.getString("id");
		String updatedJoin ="{\"action\":\"update\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\""+joinId+"\",\"left\":{\"column\":\"location_id\",\"tableId\":\""+geo+"\",\"dbId\":\""+dbId+"\",\"table\":\"geo_cordinates\"},\"right\":{\"column\":\"employee_name\",\"tableId\":\""+emp+"\",\"dbId\":\""+dbId+"\",\"table\":\"employee_details\"}}";
		joins.remove(update);
		joins.add(JSONObject.fromObject(updatedJoin));
		String updateMetadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"connId\":\"5\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[\""+dim+"\"],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataJoinsUpdateTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}";
		String response = testUtility.createMetadata(updateMetadata);
		JSONObject responseObject = JSONObject.fromObject(response).getJSONObject("response");
		JSONArray joinsResp= responseObject.getJSONObject("metadata").getJSONArray("joins");
		prepareNoChangeJoins(joinsResp);
		Assert.assertNotNull(joinsResp);
		Assert.assertEquals(5, joinsResp.size());
	}
}
