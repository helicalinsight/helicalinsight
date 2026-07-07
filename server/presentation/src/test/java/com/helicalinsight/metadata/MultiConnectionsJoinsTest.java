package com.helicalinsight.metadata;

import java.io.File;
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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MultiConnectionsJoinsTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    
    @Autowired
    private IntegrationTestUtility testUtility;


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
	private static String sqliteDbName = "";
	private static String sqliteJdbcUrl = "";
	private static String sqliteJdbcId = "";
	private static final String mysqlDbName = "sampletraveldata";
	private static final String mysqlUrl = "jdbc:mysql://localhost:3306/"+mysqlDbName;
	private static String mysqlJdbcId = "";
	private static String dbIdToChange = "";
	private static String secondJdbcId = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "/home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
			
			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "C:", "home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
		}
	}
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
    static JSONArray noChangeJoins;
 	static String firstDbId = "";
 	static String secondDbId = "";
 	
 	
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
	public void md_a1_createMetadata() throws Exception {
    	
    	// create folder 
    	testUtility.createFolder("MultiConnectionsJoins");
    	// create cache
    	// expand catalog schema
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	// expand table
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    	
    	// second ds : postgres 
    	String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
    	String dsResponse = testUtility.createDatasource(derby);
    	JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
    	secondJdbcId = responseObject.getString("dataSourceId");
    	
    	// create cache
    	// expand catalog schema
    	String secondDbCatalog = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(secondDbCatalog);
    	// expand table
    	String derbyTable = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(derbyTable);
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\"z9e9c\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\"z9e9d\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(10,joinsArray.size());
    	prepareNoChangeJoins(joinsArray);
    	firstDbId = mdResponseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
    	secondDbId = ((JSONObject) mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");

    	
	}
    @Test
    public void md_a2_multi_metadata_second_save() throws Exception {
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+secondDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":false,\"uuid\":\"MultiMetadataJoins.metadata\"}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(10,joinsArray.size());
    }
    

    
 	@Test
 	public void md_a3_multi_metadata_add_cross_join() throws Exception {
    	
 		String crossJoins = "{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"gdmg-2xnc-xfca-av73-21\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\""+firstDbId+"\",\"table\":\"dimdate\"},\"right\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\""+secondDbId+"\",\"table\":\"dimdate\"}}";
 		JSONObject join = JSONObject.fromObject(crossJoins);
 		noChangeJoins.add(join);
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+secondDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"uuid\":\"MultiMetadataJoins.metadata\",\"metadataReload\":false}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	prepareNoChangeJoins(joinsArray);
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(11,joinsArray.size());
 	}
 	static JSONArray deleteJoins = new JSONArray();
 	
 	@Test
 	public void md_a4_multi_metadata_noChange() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+secondDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"uuid\":\"MultiMetadataJoins.metadata\",\"metadataReload\":false}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	for(Object object : joinsArray) {
    		JSONObject eachJoin = (JSONObject) object;
    		JSONObject newJoins = new JSONObject();
    		newJoins.put("id",eachJoin.getString("id"));
    		newJoins.put("action", "delete");
    		deleteJoins.add(newJoins);
    	}
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(11,joinsArray.size());
 	}
 	
 	@Test
 	public void md_a5_multi_metadata_delete_joins() throws Exception {
    	
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+deleteJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\"z9e9c\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\"z9e9d\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"uuid\":\"MultiMetadataJoins.metadata\",\"metadataReload\":false}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(joinsArray.isEmpty());
    	Assert.assertEquals(0,joinsArray.size());
 	}
 	
 	@Test
 	public void md_a6_delete_metadata() throws Exception {
 		testUtility.deleteResource("MultiConnectionsJoins/MultiMetadataJoins.metadata");
 		testUtility.clearRecycleBin();
 	}
 	
 	
 	@Test
 	public void md_a7_add_only_crossJoin() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"ldby-qebz-4v3v-f2od-ny\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"z4lf6\",\"table\":\"dimdate\"},\"right\":{\"column\":\"employee_id\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\"7b9vx\",\"table\":\"employee_details\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z4lf6\",\"dbId\":\"z4lf6\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"7b9vx\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"7b9vx\",\"dbId\":\"7b9vx\",\"datasourceName\":\"Dump\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MetadataCrossJoin\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
    	JSONObject crossJoin = (JSONObject) joinsArray.get(0);
    	prepareNoChangeJoins(joinsArray);
    	String firstTabeDbId = crossJoin.getJSONObject("left").getString("dbId");
    	String secondTableDbId = crossJoin.getJSONObject("right").getString("dbId");
    	Assert.assertNotEquals(firstTabeDbId, secondTableDbId);
 	}
 	
 	@Test
 	public void md_a8_add_only_crossJoin_second_save() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z4lf6\",\"dbId\":\"z4lf6\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"7b9vx\",\"dbId\":\"7b9vx\",\"datasourceName\":\"Dump\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MetadataCrossJoin\",\"location\":\"MultiConnectionsJoins\",\"uuid\":\"MetadataCrossJoin.metadata\",\"metadataReload\":false}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
    	JSONObject crossJoin = (JSONObject) joinsArray.get(0);
    	String firstTabeDbId = crossJoin.getJSONObject("left").getString("dbId");
    	String secondTableDbId = crossJoin.getJSONObject("right").getString("dbId");
    	Assert.assertNotEquals(firstTabeDbId, secondTableDbId);
    	testUtility.deleteResource("MultiConnectionsJoins/MetadataCrossJoin.metadata");
 	}
 	
 	@Test
 	public void md_a9_create_metadata_with_only_first_connection_joins() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"dw2n-0a33-8ekb-jxtt-oz\",\"left\":{\"column\":\"created_date\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"3u2ij\",\"table\":\"dimdate\",\"alias\":\"dimdate.created_date\"},\"right\":{\"column\":\"date_key\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"3u2ij\",\"table\":\"dimdate\",\"alias\":\"dimdate.date_key\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3u2ij\",\"dbId\":\"3u2ij\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"9zztc\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"9zztc\",\"dbId\":\"9zztc\",\"datasourceName\":\"AnotherDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
    	testUtility.deleteResource("MultiConnectionsJoins/MultiMetadataJoins.metadata");
    	testUtility.clearRecycleBin();
    
 	}
 	
 	@Test
 	public void md_b1_create_metadata_with_only_second_connection_joins() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"t5e8-sxwp-p8oh-o7ed-e4\",\"left\":{\"column\":\"employee_id\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\"9fvm9\",\"table\":\"employee_details\",\"alias\":\"employee_details.employee_id\"},\"right\":{\"column\":\"employee_name\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\"9fvm9\",\"table\":\"employee_details\",\"alias\":\"employee_details.employee_name\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"h1oai\",\"dbId\":\"h1oai\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"9fvm9\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"9fvm9\",\"dbId\":\"9fvm9\",\"datasourceName\":\"AnotherDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadataJoins2\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
 	}
 	
 	
 	@Test
 	public void md_b2_create_metadata() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"add\",\"type\":\"inner\",\"operator\":\"=\",\"id\":\"lugf-93ep-j6c4-6ib8-g2\",\"left\":{\"column\":\"dim_id\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"dbId\":\"czfsb\",\"table\":\"dimdate\",\"alias\":\"dimdate.dim_id\"},\"right\":{\"column\":\"address\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"dbId\":\"9uypk\",\"table\":\"employee_details\",\"alias\":\"employee_details.address\"}}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"czfsb\",\"dbId\":\"czfsb\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"9uypk\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"9uypk\",\"dbId\":\"9uypk\",\"datasourceName\":\"AnotherDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MetadataCrossJoin\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":true}";
 		String mdResponse = testUtility.createMetadata(metadata);
 		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
 		JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
    	prepareNoChangeJoins(joinsArray);
    	firstDbId = mdResponseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
    	secondDbId = ((JSONObject) mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");
 	}
 	
 	@Test
 	public void md_b3_create_metadata_saveAs() throws Exception {
 		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+noChangeJoins+",\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"8123\",\"joinsFetched\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+secondDbId+"\",\"datasourceName\":\"AnotherDerby\",\"connId\":\"8124\",\"joinsFetched\":true,\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MetadataCrossJoin2\",\"location\":\"MultiConnectionsJoins\",\"metadataReload\":false,\"uuid\":\"MetadataCrossJoin.metadata\",\"newLocation\":\"MultiConnectionsJoins\"}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	JSONArray joinsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("joins");
    	Assert.assertTrue(!joinsArray.isEmpty());
    	Assert.assertEquals(1,joinsArray.size());
 	}
 	
 	@Test
 	public void md_b4_create_sqlite_ds() throws Exception {
 		String sqlite = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.sqlite.JDBC\",\"name\":\"SqliteSampleTravelData\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""+sqliteDbName+"\",\"jdbcUrl\":\""+sqliteJdbcUrl+"\"}";
 		String response = testUtility.createDatasource(sqlite);
 		sqliteJdbcId = JacksonUtility.fromObject(response).with("response").get("dataSourceId").asText();
 	}
 	// TODO : Revisit this.
// 	@Test
 	public void md_b5_fetchJoins() throws Exception {
 		
 		// fetchScemas
 		String derbySchema = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
 		testUtility.expand(derbySchema);
 		// fetchTables
 		String derbyTables = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
 		testUtility.expand(derbyTables);
 		
 		
 		String sqliteSchema = "{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
 		String sqliteTables = "{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"Null\",\"schemas\":[]}]}}";
 		
 		testUtility.expand(sqliteSchema);
 		testUtility.expand(sqliteTables);
 		
 		String derby_dimdate_columns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"l6z12\",\"dbId\":\"l6z12\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"rtbm-gyt4-iugo-p84z-bt/fdlk-6g1v-sn0t-9wk6-cj/iyyp-sg09-991e-llx3-iv\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
 		String sqlite_dimdate_columns = "{\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"hwpa2\",\"dbId\":\"hwpa2\",\"classifier\":\"db.workflow\",\"dsKeyPath\":\"un0w-skdr-htx5-mbrx-af/agqw-u4mu-t3dg-qq97-le\",\"driverType\":\"Sqlite\",\"database\":\"\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"\",\"table\":\"dimdate\"},\"refresh\":true}";
 		
 		testUtility.fetchColumns(derby_dimdate_columns);
 		testUtility.fetchColumns(sqlite_dimdate_columns);
 		
 		String derbyJoins = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\",\"dimdate_1\"]}}";
 		String sqliteJoins = "{\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\",\"dimdate_2\"]}}";
 		String sqliteResponse = testUtility.fetchJoins(sqliteJoins);
 		
 		String derbyResponse = testUtility.fetchJoins(derbyJoins);
 		
 		JSONArray derbyJoinsArr = JSONArray.fromObject(JSONObject.fromObject(derbyResponse).getJSONObject("response").getJSONArray("joins"));
 		JSONArray sqliteJoinsArr = JSONArray.fromObject(JSONObject.fromObject(sqliteResponse).getJSONObject("response").getJSONArray("joins"));
 		
 		Map<String,String> derbyTableIdNameMap = new HashMap<>();
 		
 		derbyTableIdNameMap.put("employee_details", "4e1fd245f4d13b77be423a43f01d80b2");
 		derbyTableIdNameMap.put("travel_details", "8a28627d07d04ef096d9935f12e0c7e9");
 		derbyTableIdNameMap.put("geo_cordinates", "be534112989b616b194bc59c2fb25a42");
 		derbyTableIdNameMap.put("dimdate", "4ac5d9f68b58bd7c0d179146e46795be");
 		derbyTableIdNameMap.put("meeting_details", "9645c648a1c0dbeec1287aaf1e996db3");
 		
 		Map<String,String> sqliteTableNameMap = new HashMap<>();
 		
 		sqliteTableNameMap.put("employee_details", "b161910cbebfd353351a6c0b46e6a02e");
 		sqliteTableNameMap.put("travel_details", "21e1b86ae9680d0fc197ed543c3e37eb");
 		sqliteTableNameMap.put("geo_cordinates", "f2ff93c37589ef57f40dcb15fda6d7ea");
 		sqliteTableNameMap.put("dimdate", "d324e793296ff76020c708f1c8fbb704");
 		sqliteTableNameMap.put("meeting_details", "025fbfb381cb17d4519363c3585626fb");
 		
 		for(Object object : derbyJoinsArr) {
 			JSONObject eachJoin  = (JSONObject) object;
 			JSONObject left = eachJoin.getJSONObject("left");
 			JSONObject right = eachJoin.getJSONObject("right");
 			Assert.assertTrue(left.containsKey("tableId"));
 			Assert.assertTrue(right.containsKey("tableId"));
 			
 			String leftTableName = left.getString("table");
 			String rightTableName = right.getString("table");
 			String leftId = derbyTableIdNameMap.get(leftTableName);
 			String rightId = derbyTableIdNameMap.get(rightTableName);
 			Assert.assertEquals(leftId, left.getString("tableId"));
 			Assert.assertEquals(rightId, right.getString("tableId"));
 		}
 		
 		for(Object object : sqliteJoinsArr) {
 			JSONObject eachJoin  = (JSONObject) object;
 			JSONObject left = eachJoin.getJSONObject("left");
 			JSONObject right = eachJoin.getJSONObject("right");
 			Assert.assertTrue(left.containsKey("tableId"));
 			Assert.assertTrue(right.containsKey("tableId"));
 			
 			String leftTableName = left.getString("table");
 			String rightTableName = right.getString("table");
 			String leftId = sqliteTableNameMap.get(leftTableName);
 			String rightId = sqliteTableNameMap.get(rightTableName);
 			Assert.assertEquals(leftId, left.getString("tableId"));
 			Assert.assertEquals(rightId, right.getString("tableId"));
 		}
 		
 		
 		
 		
 		
 		
 	}
 	
 	
 	@Test
 	public void md_x_delete_folder() throws Exception {
 		testUtility.deleteResource("MultiConnectionsJoins");
 	}
 	

    
}
    

