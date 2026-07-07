//package com.helicalinsight.export;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
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
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.JsonNodeFactory;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.model.HIResourceSecurityDB;
//import com.helicalinsight.admin.model.Organization;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.admin.service.OrganizationService;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.lingala.zip4j.core.ZipFile;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class ResourceExportPhase2Test {
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
//
//	@Autowired
//	OrganizationService orgService;
//
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//	static String fileName = "";
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
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
//		} else if (os.toLowerCase().contains("windows")) {
//			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
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
//		map.put("folderName", "ResourceExportTestPhase2");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//
//	@Test
//	public void exp_a2_share_folder_with_user() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("service", "update");
//		map.put("serviceType", "share");
//		map.put("type", "core");
//		map.put("formData",
//				"{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]},\"type\":\"folder\",\"dir\":\"ResourceExportTestPhase2\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//	}
//
//	@Test
//	public void exp_a3_exportAnEmptyFolderSharedWithUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"ResourceExportTestPhase2\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
//		map.put("formData", request);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = efwMock.perform(builder).andReturn();
//		String header = result.getResponse().getHeader("Content-Disposition");
//		fileName = header.substring(header.indexOf("=")+1);
//		fileName = fileName.substring(1,fileName.length()-1);
//		
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
//
//	}
//
//	@Test
//	public void exp_a4_importAnFolderSharedWithUser_onConflict_update() throws Exception {
//
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
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
//	//@Test
//	public void exp_a5_verify_import_onConflic_update() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		List<HIResourceSecurityDB> shareList = serviceDb.getAllResourceSecurity();
//		Assert.assertTrue(shareList.size() == 1);
//		HIResourceSecurityDB share = shareList.get(0);
//		Assert.assertTrue(share.getPermission() == 2);
//		Assert.assertNotNull(share.getUserId());
//		Assert.assertTrue(share.getUserId().getId() == 2);
//	}
//
//	public void deleteFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"ResourceExportTestPhase2\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	@Test
//	public void exp_a6_importAnEmptyFolderSharedWithUser_createNew() throws Exception {
//		deleteFolder();
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL,fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
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
//
//	//@Test
//	public void exp_a7_verify_import_createNew() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		List<HIResourceSecurityDB> shareList = serviceDb.getAllResourceSecurity();
//		Assert.assertTrue(shareList.size() == 1);
//		HIResourceSecurityDB share = shareList.get(0);
//		Assert.assertTrue(share.getPermission() == 2);
//		Assert.assertNotNull(share.getUserId());
//		Assert.assertTrue(share.getUserId().getId() == 2);
//	}
//
//	public void deleteExportedFile() throws Exception {
//		File file = new File(String.join(File.separator, TESTURL, fileName));
//		org.apache.commons.io.FileUtils.forceDelete(file);
//	}
//
//	public void create_new_env_() throws Exception {
//		deleteFolder();
//		deleteExportedFile();
//		exp_a1_create_a_folder();
//	}
//
//	@Test
//	public void exp_a8_share_folder_with_role() throws Exception {
//		create_new_env_();
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("service", "update");
//		map.put("serviceType", "share");
//		map.put("type", "core");
//		map.put("formData",
//				"{\"share\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]},\"type\":\"folder\",\"dir\":\"ResourceExportTestPhase2\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//	}
//
//	@Test
//	public void exp_a9_exportAnEmptyFolderSharedWithRole() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"ResourceExportTestPhase2\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
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
//
//	}
//
//	@Test
//	public void exp_b1_importAnEmptyFolderSharedWithRole_onConflict_update() throws Exception {
//
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
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
//	
//	//@Test
//	public void exp_b2_verify_import_onConflic_update() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		List<HIResourceSecurityDB> shareList = serviceDb.getAllResourceSecurity();
//		Assert.assertTrue(shareList.size() == 1);
//		HIResourceSecurityDB share = shareList.get(0);
//		Assert.assertTrue(share.getPermission() == 2);
//		Assert.assertNotNull(share.getRoleId());
//		Assert.assertTrue(share.getRoleId().getId() == 2);
//	}
//
//	@Test
//	public void exp_b3_importAnEmptyFolderSharedWithRole_createNew() throws Exception {
//		deleteFolder();
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
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
//	
//	//@Test
//	public void exp_b4_verify_import_createNew() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		List<HIResourceSecurityDB> shareList = serviceDb.getAllResourceSecurity();
//		Assert.assertTrue(shareList.size() == 1);
//		HIResourceSecurityDB share = shareList.get(0);
//		Assert.assertTrue(share.getPermission() == 2);
//		Assert.assertNotNull(share.getRoleId());
//		Assert.assertTrue(share.getRoleId().getId() == 2);
//	}
//
//	@Test
//	public void exp_b5_share_folder_with_multiple_users_and_roles() throws Exception {
//		create_new_env_();
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("service", "update");
//		map.put("serviceType", "share");
//		map.put("type", "core");
//		map.put("formData",
//				"{\"type\":\"folder\",\"dir\":\"ResourceExportTestPhase2\",\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"},{\"id\":\"3\",\"permission\":\"2\"}],\"role\":[{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"4\",\"permission\":\"2\"}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//	}
//
//	@Test
//	public void exp_b6_exportAnEmptyFolderSharedWithMultiple_roles_and_users() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"ResourceExportTestPhase2\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
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
//
//	}
//
//	@Test
//	public void exp_b7_importAnEmptyFolderSharedWith_multiple_Roles_and_users_onConflict_update() throws Exception {
//
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
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
//	
//	//@Test
//	public void exp_b8_verify_import_onConflic_update() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		List<HIResourceSecurityDB> shareList = serviceDb.getAllResourceSecurity();
//		Assert.assertTrue(shareList.size() == 4);
//	}
//
//	@Test
//	public void exp_b9_importAnEmptyFolderSharedWithMultiple_Users_and_roles_createNew() throws Exception {
//		deleteFolder();
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
//		map.put("formData", request);
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.fileUpload("/importResource").file(file);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		efwMock.perform(builder).andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
//	}
//
//	
//	//@Test
//	public void exp_c1_verify_import_createNew() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		Assert.assertTrue(serviceDb.getAllResourceSecurity().size() == 4);
//	}
//
//	public void createOrg() {
//		Organization org = new Organization();
//		org.setOrg_name("TestOrg");
//		org.setOrg_desc("A Test Organization");
//		orgService.add(org);
//	}
//
//	@Test
//	public void exp_c2_share_folder_with_org() throws Exception {
//		create_new_env_();
//		createOrg();
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("service", "update");
//		map.put("serviceType", "share");
//		map.put("type", "core");
//		map.put("formData",
//				"{\"share\":{\"organization\":[{\"id\":1,\"permission\":1}]},\"type\":\"folder\",\"dir\":\"ResourceExportTestPhase2\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("The selected folder privileges are updated successfully."));
//	}
//
//	@Test
//	public void exp_c3_exportAnEmptyFolderSharedWithOrg() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"ResourceExportTestPhase2\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
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
//
//	//@Test
//	public void exp_c4_importAnEmptyFolderSharedWithOrg() throws Exception {
//		deleteFolder();
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : false,\"dataSource\": false,\"schedules\" : false}}";
//		map.put("formData", request);
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.fileUpload("/importResource").file(file);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		efwMock.perform(builder).andDo(MockMvcResultHandlers.print())
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
//	}
//
//	
//	//@Test
//	public void exp_c5_verify_import_org() {
//		Assert.assertTrue(serviceDb.getAllHIResources().size() == 1);
//		Assert.assertTrue(serviceDb.getAllResourceSecurity().size() == 1);
//	}
//
//	@Test
//	public void exp_c6_clean() throws Exception {
//		deleteExportedFile();
//		deleteFolder();
//	}
//	
//
//}
