package com.helicalinsight.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
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
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.lingala.zip4j.core.ZipFile;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NestedFolderExportImportTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceServiceDB serviceDb;
	
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
	
	@Test
	public void exp_1_create_a_folder() throws Exception {
		
		testUtility.createFolder("NestedFolder");
		testUtility.createFolder("Level1",Arrays.asList("NestedFolder"));
		testUtility.createFolder("Level2",Arrays.asList("NestedFolder"));
	}
	
	@Test
	public void exp_2_exportAnEmptyFolder() throws Exception {
		String request = "{\"dir\": \"NestedFolder\",\"file\" : \"\",\"options\": {\"share\": false,\"users\": false,\"dataSource\": false,\"schedules\": false}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_3_deleteParentFolder() throws Exception {
		testUtility.deleteResource("NestedFolder");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_4_importAnEmptyFolder_getOptions() throws Exception {
		String request = "{\"onConflict\" : \"skip\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
	}
	
	@Test
	public  void verify_import() {
		HIResource resource =  serviceDb.getResourceByUrl("NestedFolder");
		Assert.assertNotNull(resource);
		Assert.assertEquals(null,resource.getParentId());
		
		HIResource level1 =  serviceDb.getResourceByUrl("NestedFolder/Level1");
		Assert.assertNotNull(level1);
		Assert.assertEquals(resource.getResourceId(),level1.getParentId());
		
		HIResource level2 =  serviceDb.getResourceByUrl("NestedFolder/Level2");
		Assert.assertNotNull(level2);
		Assert.assertEquals(resource.getResourceId(),level2.getParentId());
	}


}
