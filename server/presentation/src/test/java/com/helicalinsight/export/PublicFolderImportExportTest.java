package com.helicalinsight.export;

import java.io.File;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PublicFolderImportExportTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;
	
	
	@Autowired
	private HIResourceServiceDB serviceDb;
	
	
	@Bean
	public AdminController adminController() {
		return new AdminController();
	}
	
	@Autowired
	private AdminController adminController;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	static String fileName = "";
	private static String TESTURL = "";
	
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	
	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController,this.adminController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	
	/**
	 * Test Class Summary
	 * 
	 * 1. Create Public Folder
	 * 2. Export
	 * 3. Delete Public Folder Permanently
	 * 4. Import Exported File
	 * 5. Verify
	 */
	
	@Test
	public void exp_a1_createPublicFolder() throws Exception {
		
		testUtility.clearRecycleBin();
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "ImportExportPublicFolderTest");
        map.put("sourceArray", sourceList.toString());
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("A new folder is created successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.permissionLevel").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.public").value(true));
	
        HIResource resource =  serviceDb.getResourceByUrl("ImportExportPublicFolderTest");
		Assert.assertNotNull(resource);
		Assert.assertNull(resource.getCreatedBy());
		Assert.assertNull(resource.getHiResourceFolder().getCreatedBy());
	
	}
	
	@Test
	public void exp_a2_exportPublicFolder() throws Exception {
		String request = "{\"dir\": \"ImportExportPublicFolderTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName=testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a3_deletePublicFolder() throws Exception {
		testUtility.deleteResource("ImportExportPublicFolderTest");
		testUtility.clearRecycleBin();
		Assert.assertEquals(0,testUtility.listRecycleBin().size());
	}
	
	@Test
	public void exp_a4_importFolder() throws Exception {
		String formData = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(formData, TESTURL, fileName);
	}
	
	@Test
	public void exp_a5_verify() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("ImportExportPublicFolderTest");
		Assert.assertNotNull(resource);
		Assert.assertNull(resource.getCreatedBy());
		Assert.assertNull(resource.getHiResourceFolder().getCreatedBy());
	}
	
}
