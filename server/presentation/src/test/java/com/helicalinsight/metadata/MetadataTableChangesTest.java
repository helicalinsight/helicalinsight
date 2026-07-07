package com.helicalinsight.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataTableChangesTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    
    @Autowired
    IntegrationTestUtility testUtility;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }
    
    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
    }

    private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join(File.separator, "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
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

    @Test
    public void md_a1_create_a_folder_to_save_metadata() throws Exception {
    	testUtility.createFolder("MetadataTableChangesTest");
    }
    
    @Test
    public void md_a2_create_cache() throws Exception {
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    }
    static JSONArray noChangeJoins ;
    static JSONArray tableIds;
    @Test
    public void md_a3_create_metadata() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTableChangesTest\",\"fileName\":\"SaveSelectAll\",\"uniqueId\":true}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject metadata = responseObject.getJSONObject("response").getJSONObject("metadata"); 
    	JSONArray joins =  metadata.getJSONArray("joins");
    	prepareNoChangeJoins(joins);
    	prepareTableIds(metadata);
    }
    
	private void prepareTableIds(JSONObject metadata) {
		tableIds = new JSONArray();
		JSONObject tables = metadata.getJSONObject("tables");
		Set<String> tb1 = tables.keySet();
		for (String key : tb1) {
			JSONObject tableObj = tables.getJSONObject(key);
			String id = tableObj.getString("id");
			tableIds.add(id);
		}
	}

	@Test
    public void md_a4_saveas_metadata() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":"+tableIds+",\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":false,\"location\":\"MetadataTableChangesTest\",\"fileName\":\"SaveSelectAll2\",\"uniqueId\":true,\"uuid\":\"SaveSelectAll.metadata\",\"newLocation\":\"MetadataTableChangesTest\"}";
    	testUtility.createMetadata(formData);
    	testUtility.deleteResource("MetadataTableChangesTest/SaveSelectAll2.metadata");
    }
	
	@Test
    public void md_a5_edit_metadata_add2Tables() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"3\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"3\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject metadata = responseObject.getJSONObject("response").getJSONObject("metadata"); 
    	prepareTableIds(metadata);
    	Assert.assertEquals(4, tableIds.size());
    }
	
	@Test
    public void md_a6_edit_metadata_changeOne_removeOne() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"3\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"3\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":["+tableIds.get(2)+"],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[{\"id\":"+tableIds.get(0)+",\"alias\":\"changed\",\"connId\":\"3\"}],\"columns\":[],\"connections\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject metadata = responseObject.getJSONObject("response").getJSONObject("metadata"); 
    	prepareTableIds(metadata);
    	Assert.assertEquals(3, tableIds.size());
    	testUtility.deleteResource("MetadataTableChangesTest/SaveSelectAll.metadata");
    }
	
	@Test
    public void md_a7_createMetadata_duplicate_table() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"is6u-bule-ufuv-6bxa-nl\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"34r00\",\"originalId\":\"a3e045b1-6bb2-488d-8229-7ad30ecb6b8c\",\"originalName\":\"dim_id\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"34r00\",\"originalId\":\"7ff2cd8e-38dd-4ebf-b675-e681f7da87b2\",\"originalName\":\"fiscal_year\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"34r00\",\"originalId\":\"bdd00e2d-65b7-47fd-bbe9-f2ab2f8f1cf1\",\"originalName\":\"modified_date\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"34r00\",\"originalId\":\"60db433b-9b3d-4779-a81a-09eafd75c555\",\"originalName\":\"date_key\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"34r00\",\"originalId\":\"d31dc2eb-78c1-4176-80dc-37388700456c\",\"originalName\":\"day_number\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"34r00\",\"originalId\":\"45a6ffa8-4fb7-45e1-a6fa-2d264c4d1c12\",\"originalName\":\"fiscal_month_name\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"34r00\",\"originalId\":\"1455f23d-5e63-45e3-bf2d-8c992c2f39b7\",\"originalName\":\"fiscal_month_label\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"34r00\",\"originalId\":\"c076511f-c66a-4594-9289-54c4537d5081\",\"originalName\":\"created_date\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"34r00\",\"originalId\":\"5cd49517-3d85-498b-b4de-fa26ea10ddeb\",\"originalName\":\"created_time\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"34r00\",\"originalId\":\"18f90709-2d63-4192-aa85-6dd728e2cf8e\",\"originalName\":\"rating\",\"name\":\"rating\"}],\"connId\":\"34r00\",\"originalName\":\"dimdate\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"34r00\",\"dbId\":\"34r00\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataDuplicateTable\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":true}";
    	String columns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"1x2n6\",\"dbId\":\"1x2n6\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"7iu1-yaee-9nek-sorh-3w/st0y-fdsf-n5m3-mwfc-23/sddz-il3i-2epn-mxbu-pe\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
    	String fetchColumns = testUtility.fetchColumns(columns);
    	formData = testUtility.addDuplicateColumnsInFormData(fetchColumns, formData,"dimdate");
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject metadata = responseObject.getJSONObject("response").getJSONObject("metadata"); 
    	prepareTableIds(metadata);
    	Assert.assertEquals(2, tableIds.size());
    	testUtility.deleteResource("MetadataTableChangesTest/MetadataDuplicateTable.metadata");
    }
	
	static String colId = "";
	static String tbId = "";
	
	@Test
    public void md_a8_createMetadata_duplicate_column() throws Exception {
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"8tvu7\",\"dbId\":\"8tvu7\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"9wem-8uo7-mazf-a8d4-g4/5j68-mpdb-zok5-88sj-m1/7ios-z5e6-9sz4-bofp-28\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
		String fetchColsResponse = testUtility.fetchColumns(fetchColumns);
		JSONObject fetchObject = JSONObject.fromObject(fetchColsResponse);
		JSONObject dimdate = fetchObject.getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("table")
				.getJSONObject("dimdate");
		tbId = 	dimdate.getString("id");
		colId = dimdate.getJSONObject("columns").getJSONObject("dim_id").getString("id");
		
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"id\":\"cp70-jsbp-ra2c-lpah-zc\",\"originalId\":\""+colId+"\",\"connId\":\"phofh\",\"tableId\":\""+tbId+"\"}]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"phofh\",\"dbId\":\"phofh\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataDuplicateColumn\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":true}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject dimdateInResponse = responseObject.getJSONObject("response").getJSONObject("metadata")
    			.getJSONObject("tables").getJSONObject("dimdate");
    	Assert.assertTrue(dimdateInResponse.getJSONObject("columns").containsKey("dim_id_1"));
    	tbId = dimdateInResponse.getString("id");
    	colId = dimdateInResponse.getJSONObject("columns").getJSONObject("date_key").getString("id");
    }
	
	@Test
    public void md_a9_editMetadata_add_duplicate_column() throws Exception {
		
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"date_key_1\",\"name\":\"date_key_1\",\"id\":\"h8aq-ifyc-e0t6-h3s7-41\",\"originalId\":\""+colId+"\",\"connId\":\"9150\",\"tableId\":\""+tbId+"\"}]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"9150\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"9150\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataDuplicateColumn\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"MetadataDuplicateColumn.metadata\",\"uniqueId\":true}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject dimdateInResponse = responseObject.getJSONObject("response").getJSONObject("metadata")
    			.getJSONObject("tables").getJSONObject("dimdate");
    	Assert.assertTrue(dimdateInResponse.getJSONObject("columns").containsKey("date_key_1"));
    }
	
	@Test
    public void md_b1_add_duplicate_column_saveas() throws Exception {
		
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"9150\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"9150\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\""+tbId+"\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataDuplicateColumnSaveAs\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"MetadataDuplicateColumn.metadata\",\"newLocation\":\"MetadataTableChangesTest\"}";
    	String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject dimdateInResponse = responseObject.getJSONObject("response").getJSONObject("metadata")
    			.getJSONObject("tables").getJSONObject("dimdate");
    	Assert.assertTrue(dimdateInResponse.getJSONObject("columns").containsKey("date_key_1"));
    	testUtility.deleteResource("MetadataTableChangesTest/MetadataDuplicateColumn.metadata");
    }
	
	
	static String viewId = "";
	@Test
	public void md_b2_add_view_createMetadata() throws Exception {
		
		String createView = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[]}";
		String viewResponse = testUtility.retrievViewLabel(createView);
		JSONObject labels = JSONObject.fromObject(viewResponse);
		JSONArray labelsArr = labels.getJSONObject("response").getJSONArray("labels"); 
		String saveView = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":"+labelsArr+"}";
    	String saveViewResponse = testUtility.saveView(saveView);
		viewId = JSONObject.fromObject(saveViewResponse).getJSONObject("response").getString("viewId");
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"qhay6\",\"dbId\":\"qhay6\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[\""+viewId+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataView\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":true}";
		String response = testUtility.createMetadata(formData);
    	JSONObject responseObject = JSONObject.fromObject(response);
    	JSONObject responseObj = JSONObject.fromObject(responseObject);
    	JSONObject tables =  responseObj.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables");
    	Assert.assertTrue(tables.containsKey("dimdate"));
    	Assert.assertTrue(tables.containsKey("View 1"));
    	viewId = tables.getJSONObject("View 1").getString("id"); 
	}	
	
	@Test
	public void md_b3_edit_view_updateMetadata() throws Exception {
		String updateView = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true}],\"viewId\":\""+viewId+"\"}";
    	testUtility.saveView(updateView);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"9208\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"9208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[\""+viewId+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataView\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"MetadataView.metadata\",\"uniqueId\":true}";
		String response = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(response);
		JSONObject tables = responseObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables");
		Assert.assertTrue(tables.containsKey("View 1"));
		Assert.assertTrue(tables.containsKey("dimdate"));
	}
	static String view2 = "";
	@Test
	public void md_b4_add_view_updateMetadata() throws Exception {
		String executeQuery = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.generic\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 2\",\"labels\":[]}";
		String labelsString = testUtility.retrievViewLabel(executeQuery);
		JSONArray labelsArray = JSONObject.fromObject(labelsString).getJSONObject("response").getJSONArray("labels");
		String saveView = "{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 2\",\"labels\":"+labelsArray+"}";
    	String saveViewResponse = testUtility.saveView(saveView);
    	view2 = JSONObject.fromObject(saveViewResponse).getJSONObject("response").getString("viewId");
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"9208\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"9208\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[\""+view2+"\"],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataView\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":false,\"uuid\":\"MetadataView.metadata\",\"uniqueId\":true}";
    	String mdResponse = testUtility.createMetadata(formData);
    	JSONObject mdResponseObj = JSONObject.fromObject(mdResponse);
    	JSONObject tables = mdResponseObj.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables");
    	Assert.assertTrue(tables.containsKey("View 2"));
    	Assert.assertTrue(tables.containsKey("View 1")); 
    	Assert.assertTrue(tables.containsKey("dimdate"));
	}
	
	
	@Test
	public void md_b5_remove_column_createMetadata() throws Exception {
		
		String fetchColoumnsFormData = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"1x2n6\",\"dbId\":\"1x2n6\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"7iu1-yaee-9nek-sorh-3w/st0y-fdsf-n5m3-mwfc-23/sddz-il3i-2epn-mxbu-pe\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
    	String fetchColumns = testUtility.fetchColumns(fetchColoumnsFormData);
    	JSONObject fetchedColumns = JSONObject.fromObject(fetchColumns);
    	JSONObject dimdate = fetchedColumns.getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("table")
				.getJSONObject("dimdate");
    	colId = dimdate.getJSONObject("columns").getJSONObject("dim_id").getString("id");
    	String mdFormData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g35tt\",\"dbId\":\"g35tt\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[\""+colId+"\"],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataRemoveColumn\",\"location\":\"MetadataTableChangesTest\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(mdFormData);
		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse);
		JSONObject dimdateRes = mdResponseObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate");
		Assert.assertFalse(dimdateRes.getJSONObject("columns").containsKey("dim_id"));
		colId = dimdateRes.getJSONObject("columns").getJSONObject("date_key").getString("id");
	}
	
	@Test
	public void md_b6_remove_column_updateMetadata() throws Exception {
    	String mdFormData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g35tt\",\"dbId\":\"g35tt\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[\""+colId+"\"],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"MetadataRemoveColumn\",\"location\":\"MetadataTableChangesTest\",\"uuid\":\"MetadataRemoveColumn.metadata\",\"metadataReload\":false}";
    	String mdResponse = testUtility.createMetadata(mdFormData);
		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse);
		JSONObject dimdateRes = mdResponseObject.getJSONObject("response").getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate");
		Assert.assertFalse(dimdateRes.getJSONObject("columns").containsKey("date_key"));
	}
	
}