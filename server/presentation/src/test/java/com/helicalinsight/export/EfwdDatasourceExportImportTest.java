package com.helicalinsight.export;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.dto.EFWDConnGroovyDTO;
import com.helicalinsight.admin.dto.EFWDConnSqlJDBCDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwdDatasourceExportImportTest {

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
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String TESTURL = "";
	private static String jdbcUrl = "";
	private static String dbName = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");

		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest");
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
		}
	}

	@Test
	public void exp_a1_create_a_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "AllDatasources");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}
	
	@Test
	public void exp_a2_createPlainJdbcConnection() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\""+dbName+"\",\"directory\":\"AllDatasources\",\"type\":\"sql.jdbc\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists());

	
	}
	@Test
	public void exp_a3_createGroovyConnection() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"com.mysql.jdbc.Driver\\\");\\n        responseJson.put(\\\"url\\\", \\\"jdbc:mysql://localhost:3306/\\\" + userName);\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource\",\"userName\":\"root\",\"password\":\"root\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"AllDatasources\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists());
	}
	
	@Test
	public void exp_a4_createGroovyManagedConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"AllDatasources\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The data source has been saved successfully."));
	}
	
	@Test
	public void exp_a5_export_folder_having_efwd_connections() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
		Map<String, String> map = new HashMap<>();
		String request = "{\"dir\": \"AllDatasources\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": true,\"dataSource\": true,\"schedules\": true}}";
		map.put("formData", request);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		String header = result.getResponse().getHeader("Content-Disposition");
		fileName = header.substring(header.indexOf("=")+1);
		fileName = fileName.substring(1,fileName.length()-1);
		byte[] bytes = result.getResponse().getContentAsByteArray();
		try (FileOutputStream outputStream = new FileOutputStream(String.join(File.separator, TESTURL, fileName))) {
			outputStream.write(bytes);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ZipFile zipFile = new ZipFile(String.join("/", TESTURL,fileName));
		Assert.assertTrue(zipFile.isValidZipFile());
		Assert.assertNotNull(bytes);
		Assert.assertTrue(bytes.length > 0);
	}
	//@Test
	public void exp_a7_deleteFolder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"AllDatasources\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}
	@Test
	public void exp_a8_import_folder_having_efwd_connections() throws Exception {
		
		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
				fileStream);
		fileStream.close();
		Map<String, String> map = new HashMap<>();
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		map.put("formData", request);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart("/importResource").file(file);

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
	}
	
	
	@Test
	public void exp_a9_import_folder_having_efwd_connections_at_destination() throws Exception {
		testUtility.createFolder("EfwdDatasourceDestinationFolder");
		String request = "{\"destination\":\"EfwdDatasourceDestinationFolder\",\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		HIResource resource =  serviceDb.getResourceByUrl("EfwdDatasourceDestinationFolder/AllDatasources");
		List<EFWDConnSqlJDBCDTO> findConnectionByParentId = connectionService.findConnectionByParentId(resource.getResourceId());
		Assert.assertNotNull(findConnectionByParentId);
		Assert.assertTrue(!findConnectionByParentId.isEmpty());
		
		List<EFWDConnGroovyDTO> findGroovyByParentId = connectionService.findGroovyByParentId(resource.getResourceId());
		Assert.assertNotNull(findGroovyByParentId);
		Assert.assertTrue(!findGroovyByParentId.isEmpty());
	}
	

}
