package com.helicalinsight.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.springframework.mock.web.MockMultipartFile;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlainJdbcExportImportTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceServiceDB serviceDb;
	@Autowired
	EFWDConnectionService connectionService;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	static String fileName = "";

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

	private static String TESTURL = "";
	private static String jdbcUrl = "";
	private static String firstJdbcId = "";
	private static String firstGroovy = "";
	private static String dbName = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home","helical","Performance","hi","db","SampleTravelData");
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home","helical","Performance","hi","db","SampleTravelData");
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}

	@Test
	public void exp_1_create_a_folder() throws Exception {
		testUtility.createFolder("PlainJDBCDatasource");
	}
	@Test
	public void exp_a2_test_plain_jdbc_DataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"PlainJDBCDatasource\",\"type\":\"sql.jdbc\"}";;
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}

	@Test
	public void exp_a3_create_plain_jdbc_Datasource() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"PlainJDBCDatasource\",\"type\":\"sql.jdbc\"}";
		String response = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstJdbcId = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void exp_a4_test_plain_groovy_DataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"PlainJDBCDatasource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}

	@Test
	public void exp_a5_create_plain_groovy_Datasource() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"PlainJDBCDatasource\",\"type\":\"sql.jdbc.groovy\"}";
		String response = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstGroovy = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void exp_a6_share_plain_jdbc_with_role() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstJdbcId+"\",\"dir\":\"PlainJDBCDatasource\",\"classifier\":\"efwd\",\"share\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	
	@Test
	public void exp_a7_share_groovy_with_role() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstGroovy+"\",\"dir\":\"PlainJDBCDatasource\",\"classifier\":\"efwd\",\"share\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void exp_a8_export_folder_having_plain_jdbc_connections() throws Exception {
		String request = "{\"dir\": \"PlainJDBCDatasource\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a9_import_folder_having_ds() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		Assert.assertNotNull(serviceDb.getResourceByUrl("PlainJDBCDatasource"));
		
	}
	
	@Test
	public void exp_b1_deleteFolder() throws Exception {
		testUtility.deleteResource("PlainJDBCDatasource");
	}
	
	
}
