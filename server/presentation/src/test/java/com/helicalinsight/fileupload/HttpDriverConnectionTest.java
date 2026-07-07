// package com.helicalinsight.fileupload;

// import java.io.File;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import javax.servlet.ServletContext;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.FixMethodOrder;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.MethodSorters;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.web.FilterChainProxy;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
// import org.springframework.test.context.web.WebAppConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;

// import com.helicalinsight.adhoc.FileSystemOperationsController;
// import com.helicalinsight.admin.dao.HIResourceDBDAO;
// import com.helicalinsight.efw.controller.DataSourceController;
// import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
// import com.helicalinsight.test.utility.IntegrationTestUtility;
// import com.helicalinsight.test.utility.TestUtility;

// import net.sf.json.JSONArray;
// import net.sf.json.JSONObject;

// @WebAppConfiguration
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
// 		"classpath:spring-security.xml" })
// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
// public class HttpDriverConnectionTest {

// 	MockMvc efwMock;
// 	MockMvc mockMvc;
// 	MockMvc dsMock;
// 	@Autowired
// 	FilterChainProxy filterChainProxy;

// 	@Autowired
// 	private WebApplicationContext context;

// 	@Autowired
// 	private FileSystemOperationsController fileSystemOperationsController;

// 	@Autowired
// 	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

// 	@Autowired
// 	HIResourceDBDAO serviceDao;
// 	@Autowired
// 	private DataSourceController dataSourceController;

// 	@Autowired
// 	IntegrationTestUtility testUtility;

// 	@Bean
// 	public FileSystemOperationsController fileSystemOperationsController() {
// 		return new FileSystemOperationsController();
// 	}

// 	static String dataSourceId = "";

// 	@Before
// 	public void setup() {
// 		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
// 				.build();
// 		ServletContext servletContext = context.getServletContext();
// 		servletContext.setAttribute("filterStatus", "ok");
// 		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
// 				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
// 		this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
// 	}

// 	private static String dbName = "";
// 	private static String jdbcUrl = "";
// 	static {
// 		String os = System.getProperty("os.name");
// 		if (os.toLowerCase().contains("linux")) {
// 			dbName = String.join(File.separator, "/home", "helical", "Performance", "HITest", "hiee");
// 			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");

// 		} else if (os.toLowerCase().contains("windows")) {
// 			dbName = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest", "hiee");
// 			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
// 		}
// 	}

// //	@Test
// //	public void a1_testDataSourceConnection() throws Exception {
// //		String formData = "{classifier: \"global\",dataSourceProvider: \"tomcat\",driverName: \"com.helical.HttpDriver\",name: \"APItest_1\",userName: \"\",password: \"\",jdbcUrl: \"jdbc:https://dummyjson.com/products\",\"extraOptions\": {\"config\": {\"name\": \"products\",\"url\": \"https://dummyjson.com/products\",\"strategy\": \"in-memory\",\"persistentLocation\": \"\",\"authType\": \"\",\"headers\": {\"Authorization\": \"\"},\"queryParams\": {},\"params\": [],\"postBody\": {},\"dataPath\": \"products\",\"method\": \"\",\"requireTail\": false,\"username\": \"\",\"password\": \"\",\"timeout\": 1000},\"script\":\"import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.node.ObjectNode;import java.util.*;StringsetTableName(){return \\\"postman_test\\\";}\"}}";
// //		testUtility.testDataSource(formData);
// //	}

// 	@Test
// 	public void a2_createDataSourceConnection() throws Exception {

// 		String formData = "{classifier: \"global\",dataSourceProvider: \"tomcat\",driverName: \"com.helical.HttpDriver\",name: \"APItest_1\",userName: \"\",password: \"\",jdbcUrl: \"jdbc:https://dummyjson.com/products\",\"extraOptions\": {\"config\": {\"name\": \"products\",\"url\": \"https://dummyjson.com/products\",\"strategy\": \"in-memory\",\"persistentLocation\": \"\",\"authType\": \"\",\"headers\": {\"Authorization\": \"\"},\"queryParams\": {},\"params\": [],\"postBody\": {},\"dataPath\": \"products\",\"method\": \"\",\"requireTail\": false,\"username\": \"\",\"password\": \"\",\"timeout\": 1000},\"script\":\"import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.node.ObjectNode;import java.util.*;String setTableName(){return \\\"postman_test\\\";}\"}}";
// 		String responseString = testUtility.createDatasource(formData);
// 		JSONObject resultJson = JSONObject.fromObject(responseString);
// 		JSONObject response = resultJson.getJSONObject("response");
// 		dataSourceId = response.getString("dataSourceId");
// 	}

// 	@Test
// 	public void a3_create_a_folder_to_save_metadata() throws Exception {
// 		testUtility.createFolder("HttpDriverConnectionTest");
// 	}

// 	@Test
// 	public void a4_expand_catalog_schema() throws Exception {
		
// 		String formData = "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
// 		testUtility.expand(formData);
// 	}
	
// }
