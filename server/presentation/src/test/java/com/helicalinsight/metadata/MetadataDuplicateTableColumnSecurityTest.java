package com.helicalinsight.metadata;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class MetadataDuplicateTableColumnSecurityTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	IntegrationTestUtility testUtility;

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
	public void md_a1_prepare() throws Exception {
		testUtility.createFolder("MetadataSecurityOnDuplicates");
		testUtility.expand("{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand("{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
	}

	static String expressionId = "";

	@Test
	public void md_a2_create_security() throws Exception {
		String response = testUtility.saveSecurity(
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dimdate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_askmv\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		
		String formData = 
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""
						+ expressionId
						+ "\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"askmv\",\"dbId\":\"askmv\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_1\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":true}";
		testUtility.createMetadata(formData);
		String getSecurity = "{\"metadataFileName\":\"Metadata_1.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		testUtility.getSecurity(getSecurity);
	}

	static Map<String,JSONObject>  columnMap = new HashMap<>();
	static String dbId = "";
	static String tableId = "";
	@Test
	public void md_a3_create_security_for_duplicate_table_create_mode() throws Exception {
		String fetchedColumns = testUtility.fetchColumns("{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"op4l4\",\"dbId\":\"op4l4\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"olgg-5304-d60l-ewsh-u5/btk6-i6st-vp3l-rfie-py/83xw-3pxp-w0es-1rme-9o\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"travel_details\"},\"refresh\":true}");
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_duplicated_travel_details\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"3k8c-ybhh-q7pk-pd5w-9c_op4l4\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"travel_details_1\",\"id\":\"3k8c-ybhh-q7pk-pd5w-9c\",\"columns\":[{\"alias\":\"travel_id\",\"connId\":\"op4l4\",\"originalId\":\"52f8e8d1-9703-4e40-8bfb-20d44857ed92\",\"name\":\"travel_id\"},{\"alias\":\"travel_date\",\"connId\":\"op4l4\",\"originalId\":\"ec83d13c-d785-4f46-aa91-7bbfa3fcfc82\",\"name\":\"travel_date\"},{\"alias\":\"travel_type\",\"connId\":\"op4l4\",\"originalId\":\"d5a4c446-1c00-44e6-b9e0-ec3112013303\",\"name\":\"travel_type\"},{\"alias\":\"travel_medium\",\"connId\":\"op4l4\",\"originalId\":\"867fe506-0a32-4209-ab7e-6bfc2bc90ab2\",\"name\":\"travel_medium\"},{\"alias\":\"source_id\",\"connId\":\"op4l4\",\"originalId\":\"31968a44-dc7d-491b-af6f-a2e65aff2779\",\"name\":\"source_id\"},{\"alias\":\"source\",\"connId\":\"op4l4\",\"originalId\":\"26bae044-db9d-4039-a48d-d58a2e8e5da6\",\"name\":\"source\"},{\"alias\":\"destination_id\",\"connId\":\"op4l4\",\"originalId\":\"c94b0fcd-6736-4245-ab98-2f4c55974400\",\"name\":\"destination_id\"},{\"alias\":\"destination\",\"connId\":\"op4l4\",\"originalId\":\"f7d2ec31-ae39-42ee-9e72-0b0e5c1ca36f\",\"name\":\"destination\"},{\"alias\":\"travel_cost\",\"connId\":\"op4l4\",\"originalId\":\"83d06864-6da8-4e5e-bec2-d9de7d649477\",\"name\":\"travel_cost\"},{\"alias\":\"mode_of_payment\",\"connId\":\"op4l4\",\"originalId\":\"a1af36e5-0812-45f4-8847-fc81b36306a3\",\"name\":\"mode_of_payment\"},{\"alias\":\"booking_platform\",\"connId\":\"op4l4\",\"originalId\":\"3548dd28-bce7-420d-b1a9-983857428295\",\"name\":\"booking_platform\"},{\"alias\":\"travelled_by\",\"connId\":\"op4l4\",\"originalId\":\"fea54ba4-87a0-4791-a561-c00222e02741\",\"name\":\"travelled_by\"}],\"connId\":\"op4l4\",\"originalName\":\"travel_details\",\"originalId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"name\":\"travel_details_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"op4l4\",\"dbId\":\"op4l4\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_2\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":true}";
		formData = testUtility.addDuplicateTableInFormData(fetchedColumns, formData, "travel_details");
		String str =  testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(str);
		JsonObject columns = testUtility.getColumns(JsonParser.parseString(str).getAsJsonObject(), "dimdate");
		tableId = responseObject.getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("tables")
				.getJSONObject("dimdate")
				.getString("id");
		dbId = testUtility.getOuterDbId(responseObject.toString());
		columnMap =  new ObjectMapper().readValue(columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});
		String getSecurity = "{\"metadataFileName\":\"Metadata_2.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		testUtility.getSecurity(getSecurity);
	}
	
	static JsonArray tableIds;
	static JsonArray noChangeJoins;
	@Test
	public void md_a4_create_security_for_duplicated_table_edit_mode() throws Exception {
		String on = "8vd3-qoof-em51-8yy3-38_"+dbId;
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_duplicated_dimdate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+on+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		
		String duplicatedTable = "{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"8vd3-qoof-em51-8yy3-38\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"11213\",\"originalId\":\"37395\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"11213\",\"originalId\":\"37396\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"11213\",\"originalId\":\"37397\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"11213\",\"originalId\":\"37398\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"11213\",\"originalId\":\"37399\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"11213\",\"originalId\":\"37400\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"11213\",\"originalId\":\"37401\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"11213\",\"originalId\":\"37402\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"11213\",\"originalId\":\"37403\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"11213\",\"originalId\":\"37404\",\"name\":\"rating\"}],\"connId\":\"11213\",\"originalName\":\"dimdate\",\"originalId\":\"12541\",\"name\":\"dimdate_1\"}],\"column\":[]}";
		
		JSONObject duplicate = JSONObject.fromObject(duplicatedTable);
		JSONArray tableColumns = ((JSONObject) duplicate.getJSONArray("table").get(0)).getJSONArray("columns");
		
		for( Object object : tableColumns) {
			JSONObject eachObject = (JSONObject) object;
			JSONObject columnObj = columnMap.getOrDefault(eachObject.getString("alias"),null);
			if ( columnObj != null) {
				eachObject.replace("originalId", columnObj.getString("id"));
				eachObject.replace("connId", dbId);
			}
		}
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"8vd3-qoof-em51-8yy3-38\",\"columns\":"+tableColumns+",\"connId\":\""+dbId+"\",\"originalName\":\"dimdate\",\"originalId\":\""+tableId+"\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_2\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":false,\"uuid\":\"Metadata_2.metadata\"}";
		String res = testUtility.createMetadata(formData);
		JsonObject responseObject = JsonParser.parseString(res).getAsJsonObject();
		JsonObject metadata = responseObject.getAsJsonObject("response").getAsJsonObject("metadata");
		JsonObject tableString = metadata.getAsJsonObject("tables");
		tableIds = testUtility.prepareTableIds(tableString.toString());
		noChangeJoins = testUtility.prepareNoChangeJoins(metadata.getAsJsonArray("joins"));
		JsonObject columns = testUtility.getColumns(responseObject, "geo_cordinates");
		tableId = metadata
				.getAsJsonObject("tables")
				.getAsJsonObject("geo_cordinates")
				.get("id").getAsString();
		columnMap =  new ObjectMapper().readValue(columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});
		
		String getSecurity = "{\"metadataFileName\":\"Metadata_2.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(2, exprArr.size());
	}
	
	@Test
	public void md_a5_create_security_for_duplicated_table_saveas_mode() throws Exception {
		String on = "lb3i-hppn-ibqp-bo91-9o_"+dbId;
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_duplicated_geo_cordinates\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+on+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		
		String duplicatedTable = "{\"table\":[{\"alias\":\"geo_cordinates_1\",\"id\":\"lb3i-hppn-ibqp-bo91-9o\",\"columns\":[{\"alias\":\"location_id\",\"connId\":\"11312\",\"originalId\":\"37645\",\"name\":\"location_id\"},{\"alias\":\"location\",\"connId\":\"11312\",\"originalId\":\"37646\",\"name\":\"location\"},{\"alias\":\"latitude\",\"connId\":\"11312\",\"originalId\":\"37647\",\"name\":\"latitude\"},{\"alias\":\"longitude\",\"connId\":\"11312\",\"originalId\":\"37648\",\"name\":\"longitude\"}],\"connId\":\"11312\",\"originalName\":\"geo_cordinates\",\"originalId\":\"12647\",\"name\":\"geo_cordinates_1\"}],\"column\":[]}";
		JSONObject duplicate = JSONObject.fromObject(duplicatedTable);
		JSONArray tableColumns = ((JSONObject) duplicate.getJSONArray("table").get(0)).getJSONArray("columns");
		for( Object object : tableColumns) {
			JSONObject eachObject = (JSONObject) object;
			JSONObject columnObj = columnMap.getOrDefault(eachObject.getString("alias"),null);
			if ( columnObj != null) {
				eachObject.replace("originalId", columnObj.getString("id"));
				eachObject.replace("connId", dbId);
			}
		}
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[{\"alias\":\"geo_cordinates_1\",\"id\":\"lb3i-hppn-ibqp-bo91-9o\",\"columns\":"+tableColumns+",\"connId\":\""+dbId+"\",\"originalName\":\"geo_corindates\",\"originalId\":\""+tableId+"\",\"name\":\"geo_corindates_1\"}],\"column\":[]},\"joins\":"+noChangeJoins+",\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":"+tableIds+",\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_3\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":false,\"uuid\":\"Metadata_2.metadata\",\"newLocation\":\"MetadataSecurityOnDuplicates\"}";
		testUtility.createMetadata(formData);
		String getSecurity = "{\"metadataFileName\":\"Metadata_3.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(3, exprArr.size());
	}
	
	static String orgColId = "";
	
	@Test
	public void md_a6_create_security_for_duplicated_column_of_original_table_create_mode() throws Exception {
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"opa93\",\"dbId\":\"opa93\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"c31n-8m0d-ghm1-i85g-75/6nge-4l0q-fwva-npyw-nr/es5q-yund-xubb-wwvk-hf\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
		String fetchedColumns = testUtility.fetchColumns(fetchColumns);
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dim_id\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"2nw5-mltn-h2sm-atdg-ge_opa93\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"id\":\"2nw5-mltn-h2sm-atdg-ge\",\"originalId\":\"daac9be5-db88-4dd4-9ffb-31fadb1a7579\",\"connId\":\"opa93\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"opa93\",\"dbId\":\"opa93\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_4\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":true}";
		
		JSONObject formJson = JSONObject.fromObject(formData);
		
		JSONObject duplicateCol = (JSONObject) formJson.getJSONObject("duplicate").getJSONArray("column").get(0);
		JSONObject dimIdFromCache =  JSONObject.fromObject(fetchedColumns)
						.getJSONObject("response")
						.getJSONObject("metadata")
						.getJSONObject("table")
						.getJSONObject("dimdate")
						.getJSONObject("columns")
						.getJSONObject("dim_id");
		
		String originalId = dimIdFromCache.getString("id");
		duplicateCol.replace("originalId", originalId);
		String mdResp = testUtility.createMetadata(formJson.toString());
		
		JSONObject metaResp = JSONObject.fromObject(mdResp).getJSONObject("response").getJSONObject("metadata");
		
		JSONObject dimdate =  metaResp.getJSONObject("tables").getJSONObject("dimdate");
		tableId = dimdate.getString("id");
		orgColId = dimdate.getJSONObject("columns").getJSONObject("rating").getString("id");
		dbId = testUtility.getOuterDbId(mdResp);
		String getSecurity = "{\"metadataFileName\":\"Metadata_4.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(1, exprArr.size());
	}
	
	
	@Test
	public void md_a7_create_security_for_duplicated_column_of_original_table_edit_mode() throws Exception {
		
		String on = "0nex-e826-e3ei-4qho-ll_"+dbId;
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_rating\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+on+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"rating_alias\",\"name\":\"rating_1\",\"id\":\"0nex-e826-e3ei-4qho-ll\",\"originalId\":\""+orgColId+"\",\"connId\":\""+dbId+"\",\"tableId\":\""+tableId+"\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_4\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":false,\"uuid\":\"Metadata_4.metadata\",\"uniqueId\":true}";
		JSONObject formJson = JSONObject.fromObject(formData);
		String saveResponse = testUtility.createMetadata(formJson.toString());
		
		JSONObject dimdate = JSONObject.fromObject(saveResponse).getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("tables")
				.getJSONObject("dimdate");
		
		String rating_alias = dimdate.getJSONObject("columns").getJSONObject("rating_1").getString("alias");
		Assert.assertEquals("rating_alias", rating_alias);
		
		String getSecurity = "{\"metadataFileName\":\"Metadata_4.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(2, exprArr.size());
	}
	
	
	@Test
	public void md_a8_create_security_for_duplicated_column_of_duplicate_table_create_mode() throws Exception {
		
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"opa93\",\"dbId\":\"opa93\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"c31n-8m0d-ghm1-i85g-75/6nge-4l0q-fwva-npyw-nr/es5q-yund-xubb-wwvk-hf\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
		String fetchedColumns = testUtility.fetchColumns(fetchColumns);
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dim_id_1\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"ptgh-53zj-71bx-hk7q-qn_t0nzh\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"kaam-oolv-1sem-lsm5-uw\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"t0nzh\",\"originalId\":\"daac9be5-db88-4dd4-9ffb-31fadb1a7579\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"t0nzh\",\"originalId\":\"390cd762-17d9-4648-a977-77ad87cd08fb\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"t0nzh\",\"originalId\":\"d005b4e4-47f6-424c-8cce-edd7e0fa4909\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"t0nzh\",\"originalId\":\"8ed84a3e-0c3d-49cc-b16a-4f5543d5e3a9\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"t0nzh\",\"originalId\":\"06f934c8-c325-48f9-b187-f25c1b4d2360\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"t0nzh\",\"originalId\":\"cdf780f0-842a-4a9a-8476-7d492b73047e\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"t0nzh\",\"originalId\":\"e632a468-d5b7-441b-9674-67779af272bd\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"t0nzh\",\"originalId\":\"10df7a8f-25f0-44ce-833f-adcaeb1dfb93\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"t0nzh\",\"originalId\":\"7b4864f9-f285-4c0c-b77c-4107cba85a6e\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"t0nzh\",\"originalId\":\"0a56d7f2-69d4-4175-afe4-dd3d797d2988\",\"name\":\"rating\"},{\"alias\":\"dim_id_1\",\"connId\":\"t0nzh\",\"originalId\":\"daac9be5-db88-4dd4-9ffb-31fadb1a7579\",\"name\":\"dim_id_1\"}],\"connId\":\"t0nzh\",\"originalName\":\"dimdate\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"name\":\"dimdate_1\"}],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"id\":\"ptgh-53zj-71bx-hk7q-qn\",\"originalId\":\"daac9be5-db88-4dd4-9ffb-31fadb1a7579\",\"connId\":\"t0nzh\",\"tableId\":\"kaam-oolv-1sem-lsm5-uw\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"t0nzh\",\"dbId\":\"t0nzh\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_5\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":true}";
		
		formData = testUtility.addDuplicateTableInFormData(fetchedColumns,formData, "dimdate");
		
		JSONObject formJson = JSONObject.fromObject(formData);
		
		JSONObject duplicateCol = (JSONObject) formJson.getJSONObject("duplicate").getJSONArray("column").get(0);
		JSONObject dimIdFromCache =  JSONObject.fromObject(fetchedColumns)
						.getJSONObject("response")
						.getJSONObject("metadata")
						.getJSONObject("table")
						.getJSONObject("dimdate")
						.getJSONObject("columns")
						.getJSONObject("dim_id");
		
		String originalId = dimIdFromCache.getString("id");
		duplicateCol.replace("originalId", originalId);
		String mdResp = testUtility.createMetadata(formJson.toString());
		JSONObject metaResp = JSONObject.fromObject(mdResp).getJSONObject("response").getJSONObject("metadata");
		JSONObject dimdate =  metaResp.getJSONObject("tables").getJSONObject("dimdate_1");
		tableId = dimdate.getString("id");
		orgColId = dimdate.getJSONObject("columns").getJSONObject("rating").getString("id");
		dbId = testUtility.getOuterDbId(mdResp);
		String getSecurity = "{\"metadataFileName\":\"Metadata_5.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(1, exprArr.size());

	}
	
	
	@Test
	public void md_a9_create_security_for_duplicated_column_of_duplicate_table_edit_mode() throws Exception {
		String on = "fpdq-cmb8-i52b-wcew-kk_"+dbId;
		String response = testUtility.saveSecurity("{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_rating_1\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+on+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObj = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObj.containsKey("expressionId"));
		expressionId = responseObj.getString("expressionId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"rating_1\",\"name\":\"rating_1\",\"id\":\"fpdq-cmb8-i52b-wcew-kk\",\"originalId\":\""+orgColId+"\",\"connId\":\""+dbId+"\",\"tableId\":\""+tableId+"\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"fileName\":\"Metadata_5\",\"location\":\"MetadataSecurityOnDuplicates\",\"metadataReload\":false,\"uuid\":\"Metadata_5.metadata\",\"uniqueId\":true}";
		JSONObject formJson = JSONObject.fromObject(formData);
		testUtility.createMetadata(formJson.toString());
		String getSecurity = "{\"metadataFileName\":\"Metadata_5.metadata\",\"location\":\"MetadataSecurityOnDuplicates\"}";
		String securityResponse = testUtility.getSecurity(getSecurity);
		JSONArray exprArr = JSONObject.fromObject(securityResponse).getJSONObject("response").getJSONArray("expressions");
		Assert.assertEquals(2, exprArr.size());
	}
	
	@Test
	public void z_clean() throws Exception {
		testUtility.deleteResource("MetadataSecurityOnDuplicates");
	}
	
	
}