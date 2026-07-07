//package com.helicalinsight.core.datasource;
//
//import java.io.File;
//import java.util.ArrayList; 
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//
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
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.dao.HIResourceDBDAO;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.admin.utils.JacksonUtility;
//import com.helicalinsight.efw.controller.DataSourceController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//        "classpath:spring-security.xml"})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class DataSourceTest {
//
//
//    MockMvc efwMock;
//    MockMvc mockMvc;
//    MockMvc dsMock;
//    @Autowired
//    FilterChainProxy filterChainProxy;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private FileSystemOperationsController fileSystemOperationsController;
//
//    @Autowired
//    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//    @Autowired
//    HIResourceDBDAO serviceDao;
//    @Autowired
//	private DataSourceController dataSourceController;
//    
//    @Autowired
//    IntegrationTestUtility testUtility;
//
//
//    @Bean
//    public FileSystemOperationsController fileSystemOperationsController() {
//        return new FileSystemOperationsController();
//    }
//
//    static String dataSourceId = "";
//
//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
//                .build();
//        ServletContext servletContext = context.getServletContext();
//        servletContext.setAttribute("filterStatus", "ok");
//        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//                .addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
//    }
//    
//	private static String dbName = "";
//	private static String jdbcUrl = "";
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			dbName = String.join(File.separator, "/home", "helical", "Performance", "HITest","hiee");
//			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "HITest","hiee");
//
//			} else if (os.toLowerCase().contains("windows")) {
//			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "HITest","hiee");
//			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "HITest","hiee");
//		}
//	}
//	
//	@Test
//	public void a0_default_connection_permission_level_test() throws Exception {
//		
//		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/listDataSources");
//	        Map<String, String> map = new HashMap<>();
//	        map.put("type", "global.jdbc");
//	        map.put("name", "Managed DataSource");
//	        map.put("classifier", "global");
//	        map.put("categoryName", "advanced");
//	        map.put("categoryType", "advanced");
//	        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	        MvcResult result = this.dsMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	        JSONObject responseNode = JSONObject.fromObject(result.getResponse().getContentAsString());
//	        JSONArray array = responseNode.getJSONArray("dataSources");
//	        Assert.assertNotNull(array);
//	        Assert.assertTrue(array.size() >  0);
//	        for(Object object : array) {
//	        	JSONObject connection = (JSONObject) object;
//	        	Assert.assertTrue(connection.containsKey("baseType"));
//	        	if(connection.getJSONObject("data").getString("id").equals("1")) {
//	        		Assert.assertEquals("5", connection.getString("permissionLevel"));
//	        		break;
//	        	}
//	        }
//	        
//	}
//
//    @Test
//    public void a1_testDataSourceConnection() throws Exception {
//
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "core");
//        map.put("serviceType", "dataSource");
//        map.put("service", "test");
//        String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                .jsonPath("$.response.message").value("The connection test is successful."));
//    }
//
//    @Test
//    public void a2_createDataSourceConnection() throws Exception {
//
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "core");
//        map.put("serviceType", "dataSource");
//        map.put("service", "write");
//        String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//                        .value("A new Tomcat data source is created successfully."))
//                .andReturn();
//        JSONObject resultJson = JSONObject.fromObject(result.getResponse().getContentAsString());
//        JSONObject response = resultJson.getJSONObject("response");
//        dataSourceId = response.getString("dataSourceId");
//    }
//
//
//    @Test
//    public void a3_create_a_folder_to_save_metadata() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/fileSystemOperations");
//        List<String> sourceList = new ArrayList<>();
//        sourceList.add("");
//        Map<String, String> map = new HashMap<>();
//        map.put("action", "newFolder");
//        map.put("folderName", "MetadataTest");
//        map.put("sourceArray", sourceList.toString());
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                .jsonPath("$.response.message").value("A new folder is created successfully"));
//    }
//
//    @Test
//    public void a4_expand_catalog_schema() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        List<String> sourceList = new ArrayList<>();
//        sourceList.add("");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        String formData = "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//
//    @Test
//    public void a5_expand_table() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "metadataWorkflow");
//        map.put("formData",
//                "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//    }
//
//    @Test
//    public void a6_createMetadata() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "metadata");
//        map.put("service", "update");
//        map.put("formData",
//                "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"cv7i3\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelData\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"ed031d3decbf73c096c71a81e8e828b1\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataTest\",\"metadataReload\":true}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = this.efwMock.perform(builder).andReturn();
//        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//        int status = jsonObject.getInt("status");
//        JSONObject responseObject = jsonObject.getJSONObject("response");
//        String message = responseObject.getString("message");
//        Assert.assertTrue(responseObject.has("data"));
//        JSONArray array = responseObject.getJSONArray("data");
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject data = array.getJSONObject(i);
//            Assert.assertTrue(data.containsKey("lastModified"));
//            Assert.assertTrue(data.containsKey("type"));
//            Assert.assertTrue(data.containsKey("options"));
//            Assert.assertTrue(data.containsKey("extension"));
//            Assert.assertTrue(data.containsKey("path"));
//            Assert.assertTrue(data.containsKey("permissionLevel"));
//            Assert.assertTrue(data.containsKey("name"));
//            Assert.assertTrue(data.containsKey("title"));
//            Assert.assertEquals(1, status);
//            Assert.assertEquals("Successfully saved metadata file", message);
//        }
//    }
//
//    @Test
//    public void a7_createReport() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "adhoc");
//        map.put("serviceType", "report");
//        map.put("service", "saveReport");
//        String formData = "{\"isHrReport\":true,\"columns\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"state\":{\"fields\":[{\"column\":\"H_USERS.EMAILADDRESS\",\"label\":\"EMAILADDRESS\",\"id\":\"d2a6d3aa-8f2e-46f7-be32-78421e51a34e\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"EMAILADDRESS\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ENABLED\",\"label\":\"ENABLED\",\"id\":\"f5592421-4ef3-4780-83a8-190d6566acf4\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ENABLED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.ISEXTERNALLYAUTHENTICATED\",\"label\":\"ISEXTERNALLYAUTHENTICATED\",\"id\":\"0d1a1127-83a6-4154-8181-fea67c6816b8\",\"type\":{\"backendDataType\":\"java.lang.Boolean\",\"dataType\":\"boolean\"},\"autogen_alias\":\"ISEXTERNALLYAUTHENTICATED\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.PASSWORD\",\"label\":\"PASSWORD\",\"id\":\"dd4e5604-8688-45f3-91a1-1dd8d38e77a1\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"PASSWORD\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false},{\"column\":\"H_USERS.USERNAME\",\"label\":\"USERNAME\",\"id\":\"74319a96-56bd-4786-a557-0fcf6e437aec\",\"type\":{\"backendDataType\":\"java.lang.String\",\"dataType\":\"text\"},\"autogen_alias\":\"USERNAME\",\"isNormalTable\":true,\"groupBy\":[\"db.generic.groupBy.group\"],\"orderByColumn\":false,\"showOrderByColumn\":false,\"addedAs\":\"column\",\"floatingType\":\"discrete\",\"functionsDefinition\":\"\",\"applyBeforeAggregate\":false,\"hiddenIncludeInResultSet\":false}],\"filters\":[],\"marksList\":[{\"value\":\"_all_\",\"id\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"subVizType\":\"bar\",\"color\":{\"fields\":[]},\"size\":{\"fields\":[]},\"label\":{\"fields\":[]},\"tooltip\":{\"fields\":[]},\"shape\":{\"fields\":[]}}],\"activeMark\":\"a68455c8-8e9e-4672-8d74-8098aaf1107c\",\"activeTool\":\"1\",\"scripts\":[{\"id\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"value\":\"\"}],\"selectedScript\":\"hdi-custom-script-25b19b00-04e6-4b25-a03e-06f67334127b\",\"styles\":\"\",\"options\":{\"limitBy\":1000,\"sample\":\"sample\",\"prependTableNameToAlias\":false},\"interactiveMode\":true,\"drillDown\":true,\"drillThrough\":true,\"drillDownList\":[],\"currentDrillDown\":\"\",\"drillThroughList\":[],\"toolbarConfig\":{\"selectable\":false},\"selectedType\":\"Table\",\"customStyles\":\"\",\"customScripts\":[],\"analytics\":[{\"value\":false,\"key\":\"rowSubTotals\",\"label\":\"Row Sub Totals\"},{\"value\":false,\"key\":\"columnSubTotals\",\"label\":\"Column Sub Totals\"},{\"value\":false,\"key\":\"rowGrandTotals\",\"label\":\"Row Grand Totals\"},{\"value\":false,\"key\":\"columnGrandTotals\",\"label\":\"Column Grand Totals\"}],\"properties\":{\"title\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"},\"subTitle\":{\"show\":false,\"value\":\"\",\"padding\":0,\"fontSize\":12,\"fontColor\":\"#000000\",\"alignment\":\"left\",\"position\":\"top\"}},\"showHiddenColumns\":false,\"showHiddenRows\":false,\"database\":\"HIUSER\",\"dateFunctions\":{\"dateTime\":[{\"label\":\"Date\",\"part\":\"date\",\"key\":\"sql.typeConversion.todate\",\"returns\":\"date\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Time\",\"part\":\"time\",\"key\":\"sql.typeConversion.totime\",\"returns\":\"time\",\"parameters\":[{\"name\":\"column\"}]},{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"date\":[{\"label\":\"Years\",\"part\":\"year\",\"key\":\"sql.dateTime.year\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Quarters\",\"part\":\"quarter\",\"key\":\"sql.dateTime.quarter\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Months\",\"part\":\"month\",\"key\":\"sql.dateTime.month\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Days\",\"part\":\"day\",\"key\":\"sql.dateTime.day\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}],\"time\":[{\"label\":\"Hours\",\"part\":\"hour\",\"key\":\"sql.dateTime.hour\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Minutes\",\"part\":\"minute\",\"key\":\"sql.dateTime.minute\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Seconds\",\"part\":\"second\",\"key\":\"sql.dateTime.second\",\"returns\":\"numeric\",\"parameters\":[{\"name\":\"datetime\"}]},{\"label\":\"Individual\",\"part\":\"individual\",\"key\":\"individual\",\"parameters\":[{\"name\":\"datetime\"}]}]}},\"metadata\":{\"location\":\"MetadataTest\",\"metadataFileName\":\"SaveSelectAll.metadata\"},\"classifier\":\"db.generic\",\"reportName\":\"report1\",\"location\":\"MetadataTest\"}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result = efwMock.perform(builder).andReturn();
//        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//        int status = jsonObject.getInt("status");
//        JSONObject responseObject = jsonObject.getJSONObject("response");
//        Assert.assertTrue(responseObject.has("data"));
//        JSONArray array = responseObject.getJSONArray("data");
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject data = array.getJSONObject(i);
//            Assert.assertTrue(data.containsKey("lastModified"));
//            Assert.assertTrue(data.containsKey("type"));
//            Assert.assertTrue(data.containsKey("options"));
//            Assert.assertTrue(data.containsKey("extension"));
//            Assert.assertTrue(data.containsKey("path"));
//            Assert.assertTrue(data.containsKey("permissionLevel"));
//            Assert.assertTrue(data.containsKey("name"));
//            Assert.assertTrue(data.containsKey("title"));
//            Assert.assertEquals(1, status);
//        }
//    }
//
//    @Test
//    public void a8_deleteDatasource() throws Exception {
//        List<HIResource> resourceList = this.serviceDao.getAllHIResources();
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "core");
//        map.put("serviceType", "dataSource");
//        map.put("service", "delete");
//        String formData = "{\"classifier\":\"global\",\"id\":\"" + dataSourceId + "\",\"type\":\"simple\",\"dataSourceProvider\":\"tomcat\"}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//                        .value("The datasource " + dataSourceId + " have been deleted successfully"));
//        testUtility.clearRecycleBin();
//    }
//
//
//    @Test
//    public void a9_deleteFolder() throws Exception {
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//                .post("/fileSystemOperations");
//        String input = "[\"MetadataTest\"]";
//        Map<String, String> map = new HashMap<>();
//        map.put("action", "delete");
//        map.put("sourceArray", input);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//                MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//        testUtility.clearRecycleBin();
//    }
//
//    @Test
//    public void b1_verifyIfResourceExists() {
//        List<HIResource> resourceList = this.serviceDao.getAllHIResources();
//        Assert.assertNotNull(resourceList);
//    }
//
//    @Test
//    public void b2_emptyDataSourceTest() throws Exception {
//        a2_createDataSourceConnection();
//        a8_deleteDatasource();
//    }
//
//    @Test
//    public void b3_cascadeDeleteTest() throws Exception {
//        a2_createDataSourceConnection();
//        a3_create_a_folder_to_save_metadata();
//        a4_expand_catalog_schema();
//        a5_expand_table();
//        a6_createMetadata();
//        a7_createReport();
//        deleteDatasource();
//        a9_deleteFolder();
//        b1_verifyIfResourceExists();
//    }
//
//    @Test
//    public void b4_emptyDataSource_cascadeDelete() throws Exception {
//        a2_createDataSourceConnection();
//        deleteDatasource();
//    }
//
//    public void deleteDatasource() throws Exception {
//
//        List<HIResource> resourceList = this.serviceDao.getAllHIResources();
//        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "core");
//        map.put("serviceType", "dataSource");
//        map.put("service", "delete");
//        String formData = "{\"classifier\":\"global\",\"id\":\"" + dataSourceId + "\",\"type\":\"cascade\",\"dataSourceProvider\":\"tomcat\"}";
//        map.put("formData", formData);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//                        .value("The datasource " + dataSourceId + " have been deleted successfully."));
//        testUtility.clearRecycleBin();
//    }
//
//
//}
