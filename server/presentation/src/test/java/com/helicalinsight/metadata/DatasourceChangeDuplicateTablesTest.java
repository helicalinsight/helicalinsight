//package com.helicalinsight.metadata;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import javax.transaction.Transactional;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//        "classpath:spring-security.xml"})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DatasourceChangeDuplicateTablesTest {
//
//
//    MockMvc efwMock;
//    MockMvc mockMvc;
//    
//    @Autowired
//    private FilterChainProxy filterChainProxy;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//    
//    @Autowired
//    private IntegrationTestUtility testUtility;
//
//
//    @Bean
//    public FileSystemOperationsController fileSystemOperationsController() {
//        return new FileSystemOperationsController();
//    }
//    
//    @Before
//    @Transactional
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
//        ServletContext servletContext = context.getServletContext();
//        servletContext.setAttribute("filterStatus", "ok");
//        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//    }
//    private static String dbName = "";
//	private static String jdbcUrl = "";
//	private static String sqliteDbName = "";
//	private static String sqliteJdbcUrl = "";
//	private static String sqliteJdbcId = "";
//	private static String dbIdToChange = "";
//	private static String secondJdbcId = "";
//	private static String thirdJdbcId = "";
//	
//	
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
//			sqliteDbName = String.join("/", "/home", "helical", "Sqlite", "SampleTravelData.db");
//			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
//			
//			} else if (os.toLowerCase().contains("windows")) {
//			dbName = String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
//			sqliteDbName = String.join("/", "C:", "home", "helical", "Sqlite", "SampleTravelData.db");
//			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
//		}
//	}
//	
//    @Autowired
//    private EfwServicesController efwServicesController;
//
//    @Autowired
//    private FileSystemOperationsController fileSystemOperationsController;
//    
//    
//    
//    @Test
//    public void md_a0_create_cache() throws Exception {
//    	// create folder 
//    	testUtility.createFolder("DatasourceChangeDuplicateTablesTest");
//    	// create cache
//    	// expand catalog schema
//    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
//    	testUtility.expand(defaultCatalog);
//    	// expand table
//    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
//    	testUtility.expand(defaultTable);
//    	
//    	// second ds : postgres 
//    	String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
//    	String dsResponse = testUtility.createDatasource(derby);
//    	String dbResponse2 = testUtility.createDatasource(derby);
//    	JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
//    	secondJdbcId = responseObject.getString("dataSourceId");
//    	JSONObject responseObject2 = JSONObject.fromObject(dbResponse2).getJSONObject("response");
//    	thirdJdbcId = responseObject2.getString("dataSourceId");
//    	// create cache
//    	// expand catalog schema
//    	String secondDbCatalog = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
//    	testUtility.expand(secondDbCatalog);
//    	// expand table
//    	String derbyTable = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
//    	testUtility.expand(derbyTable);
//    	
//    	// expand catalog schema
//    	String thirdDb = "{\"id\":\""+thirdJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
//    	testUtility.expand(thirdDb);
//    	// expand table
//    	String thirdTables = "{\"id\":\""+thirdJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
//    	testUtility.expand(thirdTables);
//    }
//    
//    
//    static String dimdate_id ="";
//    static String travel_details_id = "";
//    static Map<String,JSONObject>  columnMap = new HashMap<>();
//    static Map<String,JSONObject>  columnMap2 = new HashMap<>();
//    static String dbId1 ="";
//    static String dbId2 = "";
//    
//    @Test
//	public void md_a1_createMetadata() throws Exception {
//    	
//    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ntxrj\",\"dbId\":\"ntxrj\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"h06ca\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"h06ca\",\"dbId\":\"h06ca\",\"datasourceName\":\"AnotherDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DatasourceChangeDuplicateTablesTest\",\"metadataReload\":true}";
//    	String mdResponse = testUtility.createMetadata(metadata);
//    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
//    	
//    	JSONObject dimdate =  mdResponseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate");
//    	dimdate_id = dimdate.getString("id");
//    	JSONObject travelDetails = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("tables").getJSONObject("travel_details");
//    	travel_details_id = travelDetails.getString("id");
//    	dbIdToChange = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");
//    	JSONObject columns = dimdate.getJSONObject("columns");
//    	columnMap =  new ObjectMapper().readValue(columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});
//    	dbId1 = testUtility.getOuterDbId(mdResponse);
//    	dbId2 = dbIdToChange;
//    	JSONObject travel_columns = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("tables").getJSONObject("travel_details").getJSONObject("columns");
//    	columnMap2 =  new ObjectMapper().readValue(travel_columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});
//    }
//    
//    
//    @Test
//    public void md_a2_change_datasource()  throws Exception {
//    	
//    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"ac0h-ke9s-w4l6-l418-1v\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"13509\",\"originalId\":\"43695\",\"id\":\"wpem-dofi-jgw5-d7nf-xn\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"13509\",\"originalId\":\"43696\",\"id\":\"72j3-9rq0-9svb-b0rp-5o\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"13509\",\"originalId\":\"43697\",\"id\":\"s1kp-18tp-jist-1y5y-uk\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"13509\",\"originalId\":\"43698\",\"id\":\"vwqa-jbs8-xwx0-j7ml-e2\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"13509\",\"originalId\":\"43699\",\"id\":\"1dw3-g88z-z9lb-tylx-qo\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"13509\",\"originalId\":\"43700\",\"id\":\"nvbz-uweo-3kfs-dhl2-6q\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"13509\",\"originalId\":\"43701\",\"id\":\"pvzh-t2z0-odpx-fqms-gz\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"13509\",\"originalId\":\"43702\",\"id\":\"8cpc-jpuo-39p7-7kew-ji\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"13509\",\"originalId\":\"43703\",\"id\":\"gpfj-lrnh-kmxb-ws4h-9j\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"13509\",\"originalId\":\"43704\",\"id\":\"y65i-4xz7-arm0-ypbu-pz\",\"name\":\"rating\"}],\"connId\":\"13509\",\"originalName\":\"dimdate\",\"originalId\":\"14925\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"13509\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"13509\",\"oldDbId\":\"ntxrj\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[\"13510\"]},\"connections\":[{\"classifier\":\"global\",\"duplicate\":{\"table\":[{\"alias\":\"travel_details_1\",\"id\":\"zgfy-r5uo-2sz4-577p-sp\",\"columns\":[{\"alias\":\"travel_id\",\"connId\":\"13510\",\"originalId\":\"43705\",\"id\":\"1jle-svgy-312e-noij-4p\",\"name\":\"travel_id\"},{\"alias\":\"travel_date\",\"connId\":\"13510\",\"originalId\":\"43706\",\"id\":\"cdua-zi89-rbsc-dqwf-qr\",\"name\":\"travel_date\"},{\"alias\":\"travel_type\",\"connId\":\"13510\",\"originalId\":\"43707\",\"id\":\"62zl-c9u8-m0m9-5pyl-91\",\"name\":\"travel_type\"},{\"alias\":\"travel_medium\",\"connId\":\"13510\",\"originalId\":\"43708\",\"id\":\"wh5t-hrxi-hofg-p955-5f\",\"name\":\"travel_medium\"},{\"alias\":\"source_id\",\"connId\":\"13510\",\"originalId\":\"43709\",\"id\":\"b19o-2hso-we5a-nlnt-8x\",\"name\":\"source_id\"},{\"alias\":\"source\",\"connId\":\"13510\",\"originalId\":\"43710\",\"id\":\"2fy2-grjx-c7ut-vbqp-p2\",\"name\":\"source\"},{\"alias\":\"destination_id\",\"connId\":\"13510\",\"originalId\":\"43711\",\"id\":\"avfl-wv2j-s54c-lboj-9x\",\"name\":\"destination_id\"},{\"alias\":\"destination\",\"connId\":\"13510\",\"originalId\":\"43712\",\"id\":\"fkua-zxjv-x4in-7so9-13\",\"name\":\"destination\"},{\"alias\":\"travel_cost\",\"connId\":\"13510\",\"originalId\":\"43713\",\"id\":\"6mea-nd60-paft-b20w-oq\",\"name\":\"travel_cost\"},{\"alias\":\"mode_of_payment\",\"connId\":\"13510\",\"originalId\":\"43714\",\"id\":\"y8iq-815o-bppv-y62h-om\",\"name\":\"mode_of_payment\"},{\"alias\":\"booking_platform\",\"connId\":\"13510\",\"originalId\":\"43715\",\"id\":\"5728-pi31-kmti-7srs-f7\",\"name\":\"booking_platform\"},{\"alias\":\"travelled_by\",\"connId\":\"13510\",\"originalId\":\"43716\",\"id\":\"711s-qq6y-igz7-0spx-w4\",\"name\":\"travelled_by\"}],\"connId\":\"13510\",\"originalName\":\"travel_details\",\"originalId\":\"14926\",\"name\":\"travel_details_1\"}],\"column\":[]},\"dataSource\":{\"id\":\"1000\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"13510\",\"datasourceName\":\"hiee\",\"connId\":\"13510\",\"oldDbId\":\"h06ca\",\"changed\":true,\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DatasourceChangeDuplicateTablesTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}";
//    	
//    	JSONObject duplicate_dimdate = (JSONObject)JSONObject.fromObject(formData).getJSONObject("duplicate").getJSONArray("table").get(0);
//		JSONArray tableColumns = duplicate_dimdate.getJSONArray("columns");
//		duplicate_dimdate.put("originalId", dimdate_id);
//		for( Object object : tableColumns) {
//			JSONObject eachObject = (JSONObject) object;
//			JSONObject columnObj = columnMap.getOrDefault(eachObject.getString("alias"),null);
//			if ( columnObj != null) {
//				eachObject.replace("originalId", columnObj.getString("id"));
//				eachObject.replace("connId", dbId1);
//			}
//		}
//		
//		
//		
//		JSONObject duplicate_travel_details = (JSONObject) ((JSONObject) JSONObject.fromObject(formData).getJSONArray("connections").get(0)).getJSONObject("duplicate").getJSONArray("table").get(0);
//		duplicate_travel_details.put("originalId", travel_details_id);
//		JSONArray travel_details_columns = duplicate_travel_details.getJSONArray("columns");
//		duplicate_dimdate.put("originalId", dimdate_id);
//		for( Object object : travel_details_columns) {
//			JSONObject eachObject = (JSONObject) object;
//			JSONObject columnObj = columnMap2.getOrDefault(eachObject.getString("alias"),null);
//			if ( columnObj != null) {
//				eachObject.replace("originalId", columnObj.getString("id"));
//				eachObject.replace("connId", dbId2);
//			}
//		}
//		
//		formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":["+duplicate_dimdate+"],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId1+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"13509\",\"oldDbId\":\"ntxrj\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[\""+dbIdToChange+"\"]},\"connections\":[{\"classifier\":\"global\",\"duplicate\":{\"table\":["+duplicate_travel_details+"],\"column\":[]},\"dataSource\":{\"id\":\""+thirdJdbcId+"\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId2+"\",\"datasourceName\":\"hiee\",\"connId\":\"13510\",\"oldDbId\":\"h06ca\",\"changed\":true,\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DatasourceChangeDuplicateTablesTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}";
//		
//		String dbResponse =  testUtility.createMetadata(formData);
//		JSONObject tables = JSONObject.fromObject(dbResponse).getJSONObject("response").getJSONObject("metadata").getJSONObject("tables");
//		Assert.assertTrue(tables.containsKey("dimdate_1"));
//		Assert.assertTrue(tables.containsKey("dimdate"));
//		
//		JSONObject tables2 = ((JSONObject) JSONObject.fromObject(dbResponse).getJSONObject("response").getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("tables");
//		Assert.assertTrue(tables2.containsKey("travel_details_1"));
//		Assert.assertTrue(tables2.containsKey("travel_details"));
//		
//    }
//    
//    
//}