//package com.helicalinsight.core.datasource;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
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
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.model.HIEfwdConnection;
//import com.helicalinsight.datasource.service.EFWDConnectionService;
//import com.helicalinsight.efw.controller.DataSourceController;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.efw.exceptions.EfwServiceException;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//        "classpath:spring-security.xml"})
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class EfwdRBItemRootFetchTest {
//	
//    MockMvc efwMock;
//    MockMvc mockMvc;
//    MockMvc dsMock;
//    
//    @Autowired
//    private FilterChainProxy filterChainProxy;
//    
//	@Autowired
//	private DataSourceController dataSourceController;
//
//    @Autowired
//    private WebApplicationContext context;
//    
//    @Autowired
//    private EFWDConnectionService efwdService;
//
//    @Autowired
//    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//
//    @Bean
//    public FileSystemOperationsController fileSystemOperationsController() {
//        return new FileSystemOperationsController();
//    }
//        
//
//    @Before
//    @Transactional
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
//        ServletContext servletContext = context.getServletContext();
//        servletContext.setAttribute("filterStatus", "ok");
//        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
//    }
//
//    @Autowired
//    private FileSystemOperationsController fileSystemOperationsController;
//    
//    @Autowired
//    private IntegrationTestUtility testUtility;  
//    
//    private static String dbName = "";
//	private static String jdbcUrl = "";
//	private static String plainConnectionId = "";
//	private static String derbyConnectionId = "";
//	private static String groovyPlainConnectionId = "";
//	private static String groovyManagedConnectionId = "";
//	
//	private static List<String> dataSourceIds=new ArrayList<>();
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
//
//			} else if (os.toLowerCase().contains("windows")) {
//			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
//		}
//	}
//
//    @Test
//    public void rb_a1_efwd_createNestedFolders() throws Exception {
//    	testUtility.clearRecycleBin();
//    	testUtility.createFolder("EfwdRBItemRootFetchTest");
//    	testUtility.createFolder("EfwdRBItemRootFetchTest1", List.of("EfwdRBItemRootFetchTest"));
//    }
//    
//    @Test
//    public void rb_a2_efwd_createDerbyDS() throws Exception {
//        String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
//        String output=testUtility.createDatasource(formData);
//        JSONObject jsonObject = JSONObject.fromObject(output);
//        derbyConnectionId =  jsonObject.getJSONObject("response").getString("dataSourceId");
//    }
//    
//    @Test
//    public void rb_a3_createPlainJdbcConnection() throws Exception{
//    	String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\",\"type\":\"sql.jdbc\"}";
//    	String output=testUtility.createPlainDatasource(formData);
//    	JSONObject jsonObject = JSONObject.fromObject(output);
//    	plainConnectionId = jsonObject.getJSONObject("response").getString("dataSourceId");
//    }
//    
//    @Test
//    public void rb_a4_createGroovyPlainJdbcConnection() throws Exception{
//    	String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-groovy-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\"+dbName+\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\",\"type\":\"sql.jdbc.groovy\"}";
//    	String output=testUtility.createPlainDatasource(formData);
//    	JSONObject jsonObject = JSONObject.fromObject(output);
//    	groovyPlainConnectionId =  jsonObject.getJSONObject("response").getString("dataSourceId");
//    }
//    
//    @Test
//    public void rb_a5_createGroovyManagedConnection() throws Exception{
//    	String formData="{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"sample-groovy-managed\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\"}";
//    	String output=testUtility.createPlainDatasource(formData);
//    	JSONObject jsonObject = JSONObject.fromObject(output);
//    	groovyManagedConnectionId =  jsonObject.getJSONObject("response").getString("dataSourceId");
//    }
//    
//    @Test
//    public void rb_a6_deletEfwConnectionsOneByOne() throws Exception {
//    	String formData1="{\"classifier\":\"efwd\",\"id\":"+plainConnectionId+",\"type\":\"simple\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\"}";
//    	String formData2="{\"classifier\":\"efwd\",\"id\":"+groovyPlainConnectionId+",\"type\":\"simple\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\"}";
//    	String formData3="{\"classifier\":\"efwd\",\"id\":"+groovyManagedConnectionId+",\"type\":\"simple\",\"directory\":\"EfwdRBItemRootFetchTest/EfwdRBItemRootFetchTest1\",\"driver\":null}";
//    	testUtility.deleteDatasource(formData1);
//    	testUtility.deleteDatasource(formData2);
//    	testUtility.deleteDatasource(formData3);
//    }
//    
//    @Test(expected = EfwServiceException.class)
//    public void rb_a7_fetchPlainConnections() throws Exception {
//    	efwdService.findConnectionById(plainConnectionId);
//    }
//    
//    // There could be some other connection from other test cases..
//    @Test(expected = EfwServiceException.class)
//    public void rb_a8_fetchGroovyPlainConns() throws Exception{
//    	efwdService.findConnectionById(groovyPlainConnectionId);
//    }
//    
//    @Test(expected =  EfwServiceException.class)
//    public void rb_a9_fetchGroovyManagedConns()throws Exception{
//    	efwdService.findConnectionById(groovyManagedConnectionId);
//    }
//	
//	@Test
//	public void rb_b1_deleteTopLevelRootDirectory() throws Exception {
//		testUtility.deleteResource("EfwdRBItemRootFetchTest");
//	}
//	
//	@Test
//	public void rb_b2_fetchRootContentsFromRB() throws Exception {
//		String binId = testUtility.getRecycleBinIdByResourceName("EfwdRBItemRootFetchTest");
//		String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":["+binId+"]}";
//		String response = testUtility.recycleBinAction(formData);
//		JSONArray resources =  JSONObject.fromObject(response).getJSONObject("response").getJSONObject(binId).getJSONObject("data").getJSONArray("resources");
//		JSONArray dataSources =  JSONObject.fromObject(response).getJSONObject("response").getJSONObject(binId).getJSONObject("data").getJSONArray("dataSources");
//		for(Object obj:resources) {
//			JSONObject jobj=(JSONObject)obj;
//			Assert.assertTrue(jobj.getBoolean("deleted"));
//		}
//		for(Object obj:dataSources) {
//			JSONObject jobj=(JSONObject)obj;
//			Assert.assertTrue(jobj.getBoolean("deleted"));
//		}
//	}
//	
//	@Test
//	public void rb_b3_cleanRB() throws Exception{
//		testUtility.clearRecycleBin();
//	}
//
//}
