package com.helicalinsight.export;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.lingala.zip4j.core.ZipFile;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RenamedResourceExportTest {
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
	static String fileName = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}

	@Test
	public void exp_1_create_a_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "RenamedResourceExportTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}
	
	@Test
	public void exp_2_rename_a_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "rename");
		map.put("sourceArray", "[[\"RenamedResourceExportTest\",\"RenamedResourceExportTest2\"]]");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("Rename is successful"));
	}

	@Test 
	public void exp_3_exportAnEmptyFolder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
		Map<String, String> map = new HashMap<>();
		String request = "{\"dir\": \"RenamedResourceExportTest\",\"file\" : \"\",\"options\": {\"share\": false,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
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
		Assert.assertEquals("RenamedResourceExportTest2.zip", fileName);		

	}
	@Test 
	public void exp_4_importAnEmptyFolder_onConflict_skip() throws Exception {

		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL,fileName));
		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
				fileStream);
		fileStream.close();
		Map<String, String> map = new HashMap<>();
		String request = "{\"onConflict\" : \"skip\",\"upload\":true,\"options\" :{\"share\" : true,\"users\" : true,\"dataSource\": true,\"schedules\" : true}}";
		map.put("formData", request);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.multipart("/importResource").file(file);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
	}
}
