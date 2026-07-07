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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.exception.ResourceExportException;
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
public class SharedResourceWithOrgUserTest {
	
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
	
	@Qualifier("userDetailsService")
	@Autowired
	UserService userService;
	
	@Autowired
	IntegrationTestUtility testUtility;
	
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
	public void exp_a1_create_a_folder() throws Exception {
		testUtility.createFolder("ShareWithOrgUser");
	}
	
	static String orgId = "";
	@Test
	public void exp_a2_create_rog() throws Exception {
		String formData = "{\"name\":\"ATestOrganization\",\"description\":\"A Test Organization\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		
	}
	
	static String userId = "";
	
	@Test
	public void exp_a3_create_org_user() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\"organisationUser@helical.com\",\"name\":\"organisationUser\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
	}
	
	@Test
	public void exp_a4_share_with_user()  throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":\"3\"}]},\"type\":\"folder\",\"dir\":\"ShareWithOrgUser\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void exp_a5_export_shared_folder() throws Exception {
		String request = "{\"dir\": \"ShareWithOrgUser\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a6_delete_organization() throws Exception {
		testUtility.deleteOrg(orgId);
		testUtility.deleteResource("ShareWithOrgUser");
		testUtility.clearRecycleBin();
		Assert.assertNull(serviceDb.getResourceByUrl("ShareWithOrgUser"));
	}
	
	@Test
	public  void exp_a7_import_folder_shared_with_org_user() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
	}
	
	
	@Test
	public void exp_a8_verify() throws Exception {
		Assert.assertNotNull(serviceDb.getResourceByUrl("ShareWithOrgUser"));
		User user = userService.searchUserByname("organisationUser");
		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getRoles());
		Assert.assertEquals(1,user.getRoles().size());
		Assert.assertNotNull(user.getOrg_id());
	}
	
	
}
	
	
