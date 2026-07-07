package com.helicalinsight.adhoc;


import java.io.UnsupportedEncodingException;
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
public class MetadataJoinsTest {
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
	public void md_a1_create_a_folder_to_save_metadata_for_joins() throws Exception {
		testUtility.createFolder("MetadataJoinsTest");
	}
	@Test
	public void md_a2_to_create_metadata_expand_catlog_schema() throws Exception {
		String formData = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(formData);
	}
	
	@Test
	public void md_a3_expand_table() throws Exception {

		String formData ="{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(formData);
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
	@Test
	public void md_a4_create_metadata() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String joins = testUtility.prepareNoChangeJoins(fetchJoins);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"dbId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString =  testUtility.createMetadata(formData);
		dbId = testUtility.getOuterDbId(responseString);
		JSONObject responseObject = JSONObject.fromObject(responseString).getJSONObject("response");
		JSONArray joinsResp= responseObject.getJSONObject("metadata").getJSONArray("joins");
		Assert.assertNotNull(joinsResp);
		Assert.assertEquals(5, joinsResp.size());
	}
	
	@Test
	public void md_a5_save_metadata_after_fetching_joins() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\"]}}";
		String joins = testUtility.prepareNoChangeJoins(fetchJoins);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ij381\",\"dbId\":\"ij381\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_2\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString =  testUtility.createMetadata(formData);
		JSONArray resultJoins = JSONObject.fromObject(responseString).getJSONObject("response").getJSONObject("metadata").getJSONArray("joins");
		Assert.assertNotNull(resultJoins);
		Assert.assertTrue(!resultJoins.isEmpty());
		Assert.assertEquals(1, resultJoins.size());
	}
	
	@Test
	public void md_a6_add_joins_save_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"n112-bru5-13yf-skmg-bo\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"84rox\",\"table\":\"dimdate\"},\"right\":{\"column\":\"fiscal_year\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"84rox\",\"table\":\"dimdate\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"84rox\",\"dbId\":\"84rox\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"MetadataManualJoins\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		dbId = testUtility.getOuterDbId(responseString);
		String joinToRemove =  ((JSONObject)JSONObject.fromObject(responseString).getJSONObject("response").getJSONObject("metadata").getJSONArray("joins").get(0)).getString("id");
		String removeJoinFormData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"delete\",\"id\":\""+joinToRemove+"\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"84rox\",\"dbId\":\"84rox\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"MetadataManualJoins\",\"location\":\"MetadataJoinsTest\",\"uuid\":\"MetadataManualJoins.metadata\",\"metadataReload\":false}";
		String response = testUtility.createMetadata(removeJoinFormData);
		JSONArray resultJoins = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("metadata").getJSONArray("joins");
		Assert.assertNotNull(resultJoins);
		Assert.assertTrue(resultJoins.isEmpty());
		Assert.assertEquals(0, resultJoins.size());
	}
	
	
	@Test
	public void md_a7_reorder_Joins_save_metadata() throws Exception {
	
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"acc5-ar6c-16p8-4lzo-8w\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"zjnbr\",\"table\":\"dimdate\"},\"right\":{\"column\":\"employee_id\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\"zjnbr\",\"table\":\"employee_details\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"zjnbr\",\"dbId\":\"zjnbr\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"JoinsSwapTest\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(responseString).getJSONObject("response");
		JSONObject join = (JSONObject) responseObject.getJSONObject("metadata").getJSONArray("joins").get(0);
		String joinId = join.getString("id");
		Assert.assertEquals("dimdate", join.getJSONObject("left").getString("table"));
		Assert.assertEquals("dim_id", join.getJSONObject("left").getString("column"));
		Assert.assertEquals("employee_details", join.getJSONObject("right").getString("table"));
		Assert.assertEquals("employee_id", join.getJSONObject("right").getString("column"));
		dbId = responseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
		
		String SwapformData  = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"update\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\""+joinId+"\",\"left\":{\"column\":\"employee_id\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\""+dbId+"\",\"table\":\"employee_details\"},\"right\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\""+dbId+"\",\"table\":\"dimdate\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"7008\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"JoinsSwapTest\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":false,\"uuid\":\"JoinsSwapTest.metadata\",\"uniqueId\":true}";
		String swapResponseString = testUtility.createMetadata(SwapformData);
		JSONObject swapResponse = JSONObject.fromObject(swapResponseString).getJSONObject("response");
		JSONObject swapedJoins = (JSONObject) swapResponse.getJSONObject("metadata").getJSONArray("joins").get(0);
		Assert.assertEquals("dimdate", swapedJoins.getJSONObject("right").getString("table"));
		Assert.assertEquals("dim_id", swapedJoins.getJSONObject("right").getString("column"));
		Assert.assertEquals("employee_details", swapedJoins.getJSONObject("left").getString("table"));
		Assert.assertEquals("employee_id", swapedJoins.getJSONObject("left").getString("column"));
	}
	
	@Test
	public void md_a8_delete_metadata() throws Exception {
		String mdName = "MetadataJoinsTest/Metadata_1.metadata";
		testUtility.deleteResource(mdName);
	}
	
	// BUG 5956
	
	@Test
	public void md_a9_create_metadata_delete_default_joins() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String joins = testUtility.prepareDeleteJoins(fetchJoins);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"dbId\":\"6b2kt\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"DefaultJoinsDelete\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(responseString).getJSONObject("response");
		Assert.assertTrue(responseObject.getJSONObject("metadata").has("joins"));
		Assert.assertTrue(responseObject.getJSONObject("metadata").getJSONArray("joins").isEmpty());
		testUtility.deleteResource("MetadataJoinsTest/DefaultJoinsDelete.metadata");
	}
	
	@Test
	public void md_b1_create_metadata_update_default_join() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		JSONObject joinToUpdate = new JSONObject();
		String response = testUtility.fetchJoins(fetchJoins);
		JSONObject responseJson = JSONObject.fromObject(response);
		
		JSONArray arrayJoins = responseJson.getJSONObject("response").getJSONArray("joins");
		
		for( Object obj : arrayJoins) {
			JSONObject join = (JSONObject) obj;
			if( join.getString("id").startsWith("45ee06374e9d68c4a841d57c1be69f22")) {
				joinToUpdate = join;
				arrayJoins.remove(join);
				break;
			}
		}
		prepareNoChangeJoins(arrayJoins);
		joinToUpdate.put("action", "update");
		joinToUpdate.replace("type", "full");
		joinToUpdate.getJSONObject("left").replace("dbId", "6b2kt");
		joinToUpdate.getJSONObject("right").replace("dbId", "6b2kt");
		noChangeJoins.add(joinToUpdate);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"dbId\":\"6b2kt\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"DefaultJoinsUpdate\",\"location\":\"MetadataJoinsTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(responseString).getJSONObject("response");
		arrayJoins = responseObject.getJSONObject("metadata").getJSONArray("joins");
		Assert.assertTrue(responseObject.getJSONObject("metadata").has("joins"));
		Assert.assertFalse(arrayJoins.isEmpty());
		Assert.assertEquals(5,arrayJoins.size());
		int fullCount = 0;
		for( Object obj : arrayJoins) {
			JSONObject join = (JSONObject) obj;
			String type = join.getString("type");
			if("full".equalsIgnoreCase(type)) {
				fullCount++ ;
			}
		}
		Assert.assertEquals(1, fullCount);
	}
	
	//@Test
	public void md_b2_delete_folder() throws Exception {
		testUtility.deleteResource("MetadataJoinsTest");
	}
	
}
