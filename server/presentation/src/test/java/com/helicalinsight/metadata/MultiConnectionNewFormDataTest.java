/*
package com.helicalinsight.metadata;

import java.io.File;

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
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MultiConnectionNewFormDataTest {


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
//	private static String postgresPort = "5432";
//	private static String postgresDbName="SampleTravelData";
//	private static String postgresUrl = "jdbc:postgresql://"+postgresIp+":"+postgresPort+"/"+postgresDbName;
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
    
    private String firstDbId = "";
    
    @Test
	public void md_a1_createMetadata() throws Exception {
    	
    	// create folder 
    	testUtility.createFolder("MultiConNewFormDataTest");
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
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\"z9e9c\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\"z9e9d\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	dbIdToChange = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");
    	firstDbId = testUtility.getOuterDbId(mdResponse);
	}
    
    static String tableIdToRemove = "";
    static String secondTableIdToRemove = "";
    
	@Test
   	public void md_a2_second_save() throws Exception {
   		
   		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+dbIdToChange+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"MultiMetadata.metadata\"}";
   		String mdResponse = testUtility.createMetadata(formData);
   		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
   		
   		JSONArray connectionsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("connections");
   		tableIdToRemove = mdResponseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate").getString("id");
   		Assert.assertEquals(1,connectionsArray.size());
   		Assert.assertEquals(dbIdToChange, ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId"));
   		secondTableIdToRemove = ((JSONObject) connectionsArray.get(0)).getJSONObject("tables").getJSONObject("travel_details").getString("id");
   	}
	@Test
   	public void md_a3_second_save_remove_tables() throws Exception {
   		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[\""+tableIdToRemove+"\"],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+dbIdToChange+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[\""+secondTableIdToRemove+"\"],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"MultiMetadata.metadata\"}";
   		String mdResponse = testUtility.createMetadata(formData);
   		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
   		JSONArray connectionsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("connections");
   		Assert.assertEquals(1,connectionsArray.size());
   		// first ds tables
   		Assert.assertEquals(0,mdResponseObject.getJSONObject("metadata").getJSONArray("sets").size());
   		// second ds tables
   		(( JSONObject )connectionsArray.get(0)).getJSONObject("tables");
   		Assert.assertEquals(0,(( JSONObject )connectionsArray.get(0)).getJSONArray("sets").size());
	}
	static String thirdDbId = "";
	@Test
	public void md_a4_addConnectionInEditMode() throws Exception {
		String sqlite = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.sqlite.JDBC\",\"name\":\"Sqlite\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""+sqliteDbName+"\",\"jdbcUrl\":\""+sqliteJdbcUrl+"\"}";
		String dsResponse = testUtility.createDatasource(sqlite);
		JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
    	sqliteJdbcId = responseObject.getString("dataSourceId");
		String schema = "{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(schema);
		String table = "{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"catalog\":\"Null\",\"schemas\":[]}]}}";
		testUtility.expand(table);
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[\"8hnnl\"]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9d\",\"dbId\":\""+dbIdToChange+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[\"\"],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}},{\"database\":\"\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"\",\"connId\":\"8hnnl\",\"dbId\":\"8hnnl\",\"datasourceName\":\"SampleTravelData\",\"database\":\"\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"f2ff93c37589ef57f40dcb15fda6d7ea\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"MultiMetadata.metadata\"}";
		String mdResponse = testUtility.createMetadata(metadata);
		JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
		JSONArray connectionsArray = mdResponseObject.getJSONObject("metadata").getJSONArray("connections");
		thirdDbId = ((JSONObject) connectionsArray.get(1)).getJSONObject("dataSource").getString("dbId");
		Assert.assertEquals(2, connectionsArray.size());
   		// first ds tables
   		Assert.assertEquals(0,mdResponseObject.getJSONObject("metadata").getJSONArray("sets").size());
   		// second ds tables
   		(( JSONObject )connectionsArray.get(0)).getJSONObject("tables");
   		Assert.assertEquals(0,(( JSONObject )connectionsArray.get(0)).getJSONArray("sets").size());
   		// third ds tables
   		Assert.assertEquals(1,(( JSONObject )connectionsArray.get(1)).getJSONArray("sets").size());
   		
	}
	
	static String idToRemove = "";
	static String newSqliteId = "";
  	@Test
  	public void md_a5_changeDatasource() throws Exception {
  		
  		String sqlite = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.sqlite.JDBC\",\"name\":\"Sqlite\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""+sqliteDbName+"\",\"jdbcUrl\":\""+sqliteJdbcUrl+"\"}";
		String dsResponse = testUtility.createDatasource(sqlite);
		JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
    	 newSqliteId = responseObject.getString("dataSourceId");
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[\""+dbIdToChange+"\"]},\"connections\":[{\"database\":\"public\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"changed\":true,\"id\":\""+newSqliteId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"\",\"connId\":\"v19q8\",\"dbId\":\""+dbIdToChange+"\",\"datasourceName\":\"SampleTravelData\",\"database\":\"\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}},{\"database\":\"\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"\",\"connId\":\"8hnnl\",\"dbId\":\""+thirdDbId+"\",\"datasourceName\":\"SampleTravelData\",\"database\":\"\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"MultiMetadata.metadata\"}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	Assert.assertNotEquals(secondJdbcId, ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("id"));
    	Assert.assertNotEquals(secondJdbcId, ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("id"));
    	idToRemove = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");
    
  	}
  	
 	@Test
  	public void md_a6_removeConnection() throws Exception {
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z9e9c\",\"dbId\":\""+firstDbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[\""+idToRemove+"\"]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"public\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+newSqliteId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"\",\"connId\":\"v19q8\",\"dbId\":\""+dbIdToChange+"\",\"datasourceName\":\"SampleTravelData\",\"database\":\"\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}},{\"database\":\"\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+sqliteJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"\",\"connId\":\"8hnnl\",\"dbId\":\""+thirdDbId+"\",\"datasourceName\":\"SampleTravelData\",\"database\":\"\",\"databaseType\":\"Sqlite\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"MultiMetadata\",\"location\":\"MultiConNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"MultiMetadata.metadata\"}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	Assert.assertEquals(1,mdResponseObject.getJSONObject("metadata").getJSONArray("connections").size());
    	testUtility.deleteResource("MultiConNewFormDataTest");
  	}
 

    
}
*/
