package com.helicalinsight.metadata;

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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.FileSystemOperationsController;
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
public class MetadataJoinsOrderTest {
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
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	
	@Test
	public void md_a0_clear_ds_cache() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "shutdown");
		map.put("formData", "{\"ids\":[{\"id\":\"1\",\"baseType\":\"global.jdbc\"}]}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The requested DataSource(s) is/are shutdown successfully. The database cache(if any) entries are also cleared. "));
	}
	
	@Test
	public void md_a1_create_a_folder_to_save_metadata_for_joins() throws Exception {
		testUtility.createFolder("MetadataJoinsOrderTest");
	}
	@Test
	public void md_a2_to_create_metadata_expand_catlog_schema() throws Exception {
		String formData = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(formData);
		String schema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(schema);
	}
	
	//@Test
	public void md_a7_duplicate_table_add_joins_save() throws Exception {
		
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3lr80\",\"dbId\":\"3lr80\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"x863-lqno-p3wr-hyn0-5w/0ft3-9r72-e8px-kx6u-cw/vm4s-9ast-myya-cnol-za\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"travel_details\"},\"refresh\":true}";
		String fetchedColumns = testUtility.fetchColumns(fetchColumns);
		System.out.println(fetchedColumns);
		
	}
	private void checkJoinsOrder(List<String> expected , JsonArray actual) {
		int i =0;
		for(JsonElement object : actual) {
			JsonObject join =   object.getAsJsonObject();
			String[] arr  = expected.get(i).split("##");
			String left = arr[0];
			String right = arr[1];
			
			JsonObject leftTable =  join.getAsJsonObject("left");
			String[] leftDetails = left.split("=");
			Assert.assertEquals(leftDetails[0], leftTable.get("table").getAsString());
			Assert.assertEquals(leftDetails[1], leftTable.get("column").getAsString());
			
			JsonObject rightTable = join.getAsJsonObject("right");
			String[] rightDetails = right.split("=");
			Assert.assertEquals(rightDetails[0], rightTable.get("table").getAsString());
			Assert.assertEquals(rightDetails[1], rightTable.get("column").getAsString());
			i++;
		}
	}
	
	static JsonArray noChangeJoins = null;
	static Map<String,Integer> map= null;
	
	@Test
	public void md_a3_create_joins_without_fetch() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"to6m2\",\"dbId\":\"to6m2\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsOrderTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray joins = response.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("joins");
		noChangeJoins = testUtility.prepareNoChangeJoins(joins);
		map = testUtility.getSingleConnectionMetadataTableMap(responseString);
		dbId = testUtility.getOuterDbId(responseString);
		Assert.assertEquals(5, joins.size());
		List<String> expected = List.of(
				"geo_cordinates=location_id##dimdate=dim_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##travel_details=source_id");
		checkJoinsOrder(expected, joins);
	}
	
	@Test
	public void md_a4_1_swap_joins_saveas() throws Exception {
		
		String firstJoinId = noChangeJoins.get(0).getAsJsonObject().get("id").getAsString();
		String firstJoin = "{\"action\":\"update\",\"type\":\"full\",\"operator\":\"=\",\"id\":\""+firstJoinId+"\",\"left\":{\"column\":\"location_id\",\"tableId\":\""+map.get("geo_cordinates")+"\",\"dbId\":\""+dbId+"\",\"table\":\"geo_cordinates\"},\"right\":{\"column\":\"destination_id\",\"tableId\":\""+map.get("travel_details")+"\",\"dbId\":\""+dbId+"\",\"table\":\"travel_details\"}}";

		String fifthJoinId = noChangeJoins.get(4).getAsJsonObject().get("id").getAsString();
		String fifthJoin = "{\"action\":\"update\",\"type\":\"full\",\"operator\":\"=\",\"id\":\""+fifthJoinId+"\",\"left\":{\"column\":\"location_id\",\"tableId\":\""+map.get("geo_cordinates")+"\",\"dbId\":\""+dbId+"\",\"table\":\"geo_cordinates\"},\"right\":{\"column\":\"source_id\",\"tableId\":\""+map.get("travel_details")+"\",\"dbId\":\""+dbId+"\",\"table\":\"travel_details\"}}";
		noChangeJoins.set(0,JsonParser.parseString(firstJoin));
		noChangeJoins.set(4,JsonParser.parseString(fifthJoin));
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"to6m2\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataJoinsOrderTest\",\"uuid\":\"SaveSelectAll.metadata\",\"newLocation\":\"MetadataJoinsOrderTest\",\"metadataReload\":false}";
		String responseString = testUtility.createMetadata(formData);
		JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray joins = response.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("joins");
		List<String> expected = List.of(
				"geo_cordinates=location_id##travel_details=destination_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##travel_details=source_id");
		Assert.assertEquals(expected.size(), joins.size());
		checkJoinsOrder(expected, joins);
	}
	
	@Test
	public void md_a4_create_joins_with_fetch() throws Exception {
		String joinForm = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String fetchedJoins = testUtility.prepareNoChangeJoins(joinForm);
		JSONArray joins = JSONArray.fromObject(fetchedJoins);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"to6m2\",\"dbId\":\"to6m2\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"SaveSelectAllWithoutFetchJoins\",\"location\":\"MetadataJoinsOrderTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray actual = response.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("joins");
		Assert.assertEquals(5, joins.size());
		List<String> expected = List.of(
				"geo_cordinates=location_id##dimdate=dim_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##travel_details=source_id");
		checkJoinsOrder(expected, actual);
	}
	
	@Test
	public void md_a5_change_joins_order_save() throws Exception {
		String joinForm= "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String fetchedJoins = testUtility.prepareNoChangeJoins(joinForm);
		JSONArray joins = JSONArray.fromObject(fetchedJoins);
		Object firstJoin = joins.get(0);
		Object fifthJoin = joins.get(4); 
		JSONObject firstJoinObj = (JSONObject) firstJoin;
		JSONObject fifthJoinObj = (JSONObject) fifthJoin;
		joins.remove(0);
		joins.remove(3);
		
		joins.add(0,fifthJoinObj);
		joins.add(4,firstJoinObj);
		
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"to6m2\",\"dbId\":\"to6m2\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"SaveSelectAllWithFetchedJoins\",\"location\":\"MetadataJoinsOrderTest\",\"metadataReload\":true}";
		String responseString = testUtility.createMetadata(formData);
		JsonObject response = JsonParser.parseString(responseString).getAsJsonObject();
		JsonArray actual = response.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("joins");
		Assert.assertEquals(5, joins.size());
		List<String> expected = List.of(
				"geo_cordinates=location_id##travel_details=source_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##dimdate=dim_id");
		checkJoinsOrder(expected, actual);
	} 
	
	
	static String dbId;
	@Test
	public void md_a6_fetch_joins_update_swap_and_save() throws Exception {
		String fetchJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}";
		String joins = testUtility.prepareNoChangeJoins(fetchJoins);
		JSONArray joinsArray = JSONArray.fromObject(joins);
		Object firstJoin = joinsArray.get(0);
		Object fifthJoin = joinsArray.get(4); 
		JSONObject joinObj = (JSONObject) fifthJoin;
		JSONObject firstJoinObj = (JSONObject) firstJoin;
		joinObj.put("action", "update");
		joinObj.put("type", "full");
		joinObj.put("operator", "=");
		joinObj.put("left", JSONObject.fromObject("{\"column\":\"location_id\",\"tableId\":\"be534112989b616b194bc59c2fb25a42\",\"dbId\":\"6b2kt\",\"table\":\"geo_cordinates\"}"));
		joinObj.put("right", JSONObject.fromObject("{\"column\":\"source_id\",\"tableId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"dbId\":\"6b2kt\",\"table\":\"travel_details\"}"));
		
		joinsArray.remove(0);
		joinsArray.remove(3);
		
		joinsArray.add(0, joinObj);
		joinsArray.add(4,firstJoinObj);
		
		joins = joinsArray.toString();
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"6b2kt\",\"dbId\":\"6b2kt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataJoinsOrderTest\",\"metadataReload\":true}";
		String responseString =  testUtility.createMetadata(formData);
		dbId = testUtility.getOuterDbId(responseString);
		JsonObject responseObject = JsonParser.parseString(responseString).getAsJsonObject()
				.getAsJsonObject("response");
		
		JsonArray joinsResp= responseObject.getAsJsonObject("metadata").getAsJsonArray("joins");
		
		List<String> expected = List.of(
				"geo_cordinates=location_id##travel_details=source_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##dimdate=dim_id");
		Assert.assertEquals(5, joinsResp.size());
		checkJoinsOrder(expected, joinsResp);
	}
	
	@Test
	public void md_a8_duplicate_table_add_joins_save() throws Exception {
		
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3lr80\",\"dbId\":\"3lr80\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"x863-lqno-p3wr-hyn0-5w/0ft3-9r72-e8px-kx6u-cw/vm4s-9ast-myya-cnol-za\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"travel_details\"},\"refresh\":true}";
		String fetchedColumns = testUtility.fetchColumns(fetchColumns);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"travel_details_1\",\"id\":\"7yr0-80wv-lm1c-5r2q-lm\",\"columns\":[{\"alias\":\"travel_id\",\"connId\":\"3lr80\",\"originalId\":\"0070997a-2f6b-4de5-ab1d-78768d8f3938\",\"id\":\"6a16-dm4g-f06i-glms-za\",\"name\":\"travel_id\"},{\"alias\":\"travel_date\",\"connId\":\"3lr80\",\"originalId\":\"1e408479-d39f-4ef4-a9f2-4d566e139f21\",\"id\":\"bowv-65kq-rv3t-urpw-kw\",\"name\":\"travel_date\"},{\"alias\":\"travel_type\",\"connId\":\"3lr80\",\"originalId\":\"0e9f6967-d392-4bbd-945d-2375d4bf5b37\",\"id\":\"uu7q-n1lo-wk90-jelj-mr\",\"name\":\"travel_type\"},{\"alias\":\"travel_medium\",\"connId\":\"3lr80\",\"originalId\":\"06a03172-6c56-4e41-8046-54878a423f00\",\"id\":\"9j9x-k2c3-9i5x-xfbc-qb\",\"name\":\"travel_medium\"},{\"alias\":\"source_id\",\"connId\":\"3lr80\",\"originalId\":\"4b93126f-979d-4f7c-b922-dc8c06f0c1c9\",\"id\":\"6y50-xkmk-d71t-em3o-x4\",\"name\":\"source_id\"},{\"alias\":\"source\",\"connId\":\"3lr80\",\"originalId\":\"c83e0d66-e69d-4653-abe6-91b3c9a20bc0\",\"id\":\"jagf-4mt0-l5s0-havx-oy\",\"name\":\"source\"},{\"alias\":\"destination_id\",\"connId\":\"3lr80\",\"originalId\":\"454dc14c-bfd0-4bd9-b53a-afa489899aa2\",\"id\":\"uzyc-ssul-opr1-qcwg-yf\",\"name\":\"destination_id\"},{\"alias\":\"destination\",\"connId\":\"3lr80\",\"originalId\":\"fce9601f-5fce-4ba1-a77c-8f3be1889cd9\",\"id\":\"teyr-bvbg-ccmt-11yg-6y\",\"name\":\"destination\"},{\"alias\":\"travel_cost\",\"connId\":\"3lr80\",\"originalId\":\"45d8aaf1-996d-4c46-9490-4d5a38553103\",\"id\":\"b6nu-q4zt-zo0o-ay9n-md\",\"name\":\"travel_cost\"},{\"alias\":\"mode_of_payment\",\"connId\":\"3lr80\",\"originalId\":\"8c34bfdd-7a5f-4952-a261-b2b923842fac\",\"id\":\"hzsu-f3fs-v7ro-lqi4-iz\",\"name\":\"mode_of_payment\"},{\"alias\":\"booking_platform\",\"connId\":\"3lr80\",\"originalId\":\"298e47b3-1cd5-4bca-9581-6e28ea85c60d\",\"id\":\"0ays-ac3f-zxig-07fr-he\",\"name\":\"booking_platform\"},{\"alias\":\"travelled_by\",\"connId\":\"3lr80\",\"originalId\":\"a77bf1fc-81a2-4462-a2f7-8a0d727f44a9\",\"id\":\"k90e-cugs-d36z-2c9g-6g\",\"name\":\"travelled_by\"}],\"connId\":\"3lr80\",\"originalName\":\"travel_details\",\"originalId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"name\":\"travel_details_1\"}],\"column\":[]},\"joins\":[{\"id\":\"45ee06374e9d68c4a841d57c1be69f22_1687519804023\",\"action\":\"noChange\"},{\"id\":\"c113fa06a79370db6feb443c0023f531_1687519804024\",\"action\":\"noChange\"},{\"id\":\"55a5cfab25247c48f9776bf9bd457a3c_1687519792979\",\"action\":\"noChange\"},{\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9_1687519792986\",\"action\":\"noChange\"},{\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22_1687519792987\",\"action\":\"noChange\"},{\"action\":\"add\",\"type\":\"full\",\"operator\":\"=\",\"id\":\"6w90-5i42-skbk-nlqg-q0\",\"left\":{\"column\":\"destination\",\"tableId\":\"7yr0-80wv-lm1c-5r2q-lm\",\"dbId\":\"3lr80\",\"table\":\"travel_details_1\"},\"right\":{\"column\":\"destination_id\",\"tableId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"dbId\":\"3lr80\",\"table\":\"travel_details\"}}],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3lr80\",\"dbId\":\"3lr80\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_duplicate_table_joins_order\",\"location\":\"MetadataJoinsOrderTest\",\"metadataReload\":true}";
		formData = testUtility.addDuplicateTableInFormData(fetchedColumns, formData, "travel_details");
		String responseString =  testUtility.createMetadata(formData);
		JsonObject responseObject = JsonParser.parseString(responseString).getAsJsonObject().getAsJsonObject("response");
		JsonArray joinsResp= responseObject.getAsJsonObject("metadata").getAsJsonArray("joins");
		List<String> expected = List.of(
				"geo_cordinates=location_id##dimdate=dim_id",
				"employee_details=employee_id##meeting_details=meeting_by",
				"employee_details=employee_id##travel_details=travelled_by",
				"geo_cordinates=location_id##travel_details=destination_id",
				"geo_cordinates=location_id##travel_details=source_id",
				"travel_details_1=destination##travel_details=destination_id");
		Assert.assertEquals(6, joinsResp.size());
		checkJoinsOrder(expected, joinsResp);
	}
	
	
	
}
