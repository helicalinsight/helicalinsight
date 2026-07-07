package com.helicalinsight.export.enhanced;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.compress.utils.FileNameUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.utils.ZipUtils;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExportedResourcesOverrideTest {
	
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
	IntegrationTestUtility testUtility;
	
	@Autowired
	private ZipUtils zipUtils;
	
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
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	/**
	 * 
	 * 1. Create  Folder structure
	 * 	
	 * 			Folder
	 * 				Test
	 * 				Test 2
	 * 					Test
	 * 
	 * 
	 */
	
	@Test
	public void exp_a1_createFolder() throws Exception {
		testUtility.clearRecycleBin();
		testUtility.createFolder("ExportedResourcesOverrideTest");
		testUtility.createFolder("Test",List.of("ExportedResourcesOverrideTest"));
		testUtility.createFolder("Test2",List.of("ExportedResourcesOverrideTest"));
		testUtility.createFolder("Test",List.of("ExportedResourcesOverrideTest/Test2"));
		//Verify 
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test2"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test2/Test"));
	}
	
	@Test
	public void exp_a2_exportFolder() throws Exception {
		String request = "{\"dir\": \"ExportedResourcesOverrideTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
		Assert.assertEquals("ExportedResourcesOverrideTest.zip", fileName);
		
	}
	
	@Test
	public void exp_a3_verifyExported_resource() throws Exception {
		String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
		String timeStamp =  String.valueOf(System.currentTimeMillis());
		File temp = new File(String.join(File.separator, TEMPDIR, timeStamp));
		temp.mkdir();
		String tempAbsolutePath = temp.getAbsolutePath().strip();
		File file = Path.of(TESTURL,fileName).toFile();
		Path path = Paths.get(String.join(File.separator,tempAbsolutePath, fileName));
		Files.copy(new FileInputStream(file), path);
		zipUtils.extract(tempAbsolutePath, fileName);
		File resourcesDir = Path.of(tempAbsolutePath,FileNameUtils.getBaseName(fileName),"resources","ExportedResourcesOverrideTest").toFile();
		if(resourcesDir.isDirectory()) {
			Assert.assertEquals(3, resourcesDir.listFiles().length);
		}
		
		File resourcesDir2 = Path.of(tempAbsolutePath,FileNameUtils.getBaseName(fileName),"resources","ExportedResourcesOverrideTest","Test2").toFile();
		if(resourcesDir2.isDirectory()) {
			Assert.assertEquals(1, resourcesDir2.listFiles().length);
		}
	}
	
	@Test
	public void exp_a4_delete_resources() throws Exception {
		testUtility.deleteResource("ExportedResourcesOverrideTest");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_a5_import_resources() throws Exception {
		String request = "{\"destination\":\"\",\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test2"));
		Assert.assertNotNull(serviceDb.getResourceByUrl("ExportedResourcesOverrideTest/Test2/Test"));
	}
}