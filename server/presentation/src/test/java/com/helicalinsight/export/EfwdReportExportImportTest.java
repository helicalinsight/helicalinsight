//package com.helicalinsight.export;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
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
//import org.springframework.mock.web.MockMultipartFile;
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
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.datasource.service.EFWDConnectionService;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.lingala.zip4j.core.ZipFile;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class EfwdReportExportImportTest {
//
//	MockMvc efwMock;
//	MockMvc mockMvc;
//	@Autowired
//	FilterChainProxy filterChainProxy;
//
//	@Autowired
//	private WebApplicationContext context;
//
//	@Autowired
//	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//	@Autowired
//	HIResourceServiceDB serviceDb;
//	@Autowired
//	EFWDConnectionService connectionService;
//
//	static String fileName = "";
//
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//
//	@Before
//	@Transactional
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
//				.build();
//		ServletContext servletContext = context.getServletContext();
//		servletContext.setAttribute("filterStatus", "ok");
//		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//	}
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//
//	private static String TESTURL = "";
//	private static String jdbcUrl = "";
//	private static String dbName = "";
//	private static String firstJdbcId = "";
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
//			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");
//
//		} else if (os.toLowerCase().contains("windows")) {
//			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
//			dbName = String.join(File.separator, "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
//			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
//		}
//	}
//
//	@Test
//	public void exp_a1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "EfwdMetadataExportTest");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//	
//	
//	@Test
//	public void exp_a2_createGroovyManagedConnection() throws Exception {
//
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"EfwdMetadataExportTest\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("The data source has been saved successfully."))
//				.andReturn();
//
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		JSONObject data = responseObject.getJSONObject("data");
//		firstJdbcId = data.getString("id");
//	}
//	
//	@Test
//	public void exp_a3_expand_catalog_schema() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"dir\":\"EfwdMetadataExportTest\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void exp_a4_expand_table() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData", "{\"dir\":\"EfwdMetadataExportTest\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void exp_a5_createMetadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"EfwdMetadataExportTest\",\"connId\":\"74vc2\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"EfwdMetadataExportTest\",\"metadataReload\":true}";
//		map.put("formData",formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//	
//	@Test
//	public void exp_a6_export_folder_having_efwd_connections() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"EfwdMetadataExportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": true,\"dataSource\": true,\"schedules\": true}}";
//		map.put("formData", request);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = efwMock.perform(builder).andReturn();
//		String header = result.getResponse().getHeader("Content-Disposition");
//		fileName = header.substring(header.indexOf("=")+1);
//		fileName = fileName.substring(1,fileName.length()-1);
//		byte[] bytes = result.getResponse().getContentAsByteArray();
//		try (FileOutputStream outputStream = new FileOutputStream(String.join(File.separator, TESTURL, fileName))) {
//			outputStream.write(bytes);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		ZipFile zipFile = new ZipFile(String.join("/", TESTURL,fileName));
//		Assert.assertTrue(zipFile.isValidZipFile());
//		Assert.assertNotNull(bytes);
//		Assert.assertTrue(bytes.length > 0);
//	}
//	@Test
//	public void exp_a7_deleteFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"EfwdMetadataExportTest\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//	@Test
//	public void exp_a8_import_folder_having_efwd_connections() throws Exception {
//		
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
//		map.put("formData", request);
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.fileUpload("/importResource").file(file);
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		efwMock.perform(builder).andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
//	}
//
//}
