//package com.helicalinsight.export.enhanced;
//
//import java.io.File;
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
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.admin.model.HIResource;
//import com.helicalinsight.admin.service.HIResourceServiceDB;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.IntegrationTestUtility;
//
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class ExportAllResourcesTest {
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
//	IntegrationTestUtility testUtility;
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
//	public void exp_a1_createTenfolders() throws Exception {
//		testUtility.clearRecycleBin();
//		for(int i=1;i<=10;i++) {
//			testUtility.createFolder("Folder_" + i);
//		}
//		
//		for(int i=1; i<=10;i++) {
//			HIResource folderI =  serviceDb.getResourceByUrl("Folder_"+i, false);
//			Assert.assertNotNull(folderI);
//		}
//	}
//	
//	@Test
//	public void exp_a2_exportFolder() throws Exception {
//		String request = "{\"dir\": \"\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
//		fileName = testUtility.exportResource(request, TESTURL);
//		Assert.assertEquals("AllResources.zip", fileName);
//	}
//	
//	@Test
//	public void exp_a3_deleteAllFolders() throws Exception {
//		for(int i=1;i<=10;i++) {
//			testUtility.deleteResource("Folder_" + i);
//		}
//		testUtility.clearRecycleBin();
//		for(int i=1; i<=10;i++) {
//			HIResource folderI =  serviceDb.getResourceByUrl("Folder_"+i, false);
//			Assert.assertNull(folderI);
//		}
//	}
//	
//	@Test
//	public void exp_a4_importFolder() throws Exception {
//		String request = "{\"destination\":\"\",\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
//		testUtility.importResource(request, TESTURL, fileName);
//	}
//	
//	@Test
//	public void exp_a5_verify() throws Exception {
//		for(int i=1; i<=10;i++) {
//			HIResource folderI =  serviceDb.getResourceByUrl("Folder_"+i, false);
//			Assert.assertNotNull(folderI);
//		}
//	}
//}