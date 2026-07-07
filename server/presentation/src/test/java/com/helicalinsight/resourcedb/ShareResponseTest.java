package com.helicalinsight.resourcedb;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShareResponseTest {

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
	HIMetadataResourceServiceDB mdServiceDb;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	

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
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String jdbcUrl = "";
	private static String firstJdbcId = "";
	private static String dbName = "";

	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}
	
	@Test
	public void share_a1_create_folder_and_share_with_hiuser() throws Exception {
		testUtility.createFolder("ShareThisFolderWithHiUser");
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"ShareThisFolderWithHiUser\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void share_a2_verify_from_hiuser() throws Exception {
		String formData = "{\"username\":\"hiuser\",\"password\":\"hiuser\",\"type\":\"folder\",\"dir\":\"ShareThisFolderWithHiUser\"}";
		formData = testUtility.addTokenInFormData(formData);
		String response = testUtility.retriveShareInfo(formData);
		ArrayNode userArray = JacksonUtility.fromObject(response).with("response").withArray("user");
		Assert.assertTrue(userArray.isEmpty());
		
		String fetchInfoFormData = "{\"username\":\"hiuser\",\"password\":\"hiuser\",\"provide\":{\"provideUsers\":\"true\",\"provideRoles\":\"true\",\"provideOrganizations\":\"true\",\"id\":\"all\"}}";
		fetchInfoFormData = testUtility.addTokenInFormData(fetchInfoFormData);
		String fetchInfo = testUtility.fetchInfo(fetchInfoFormData);
		ArrayNode userList = JacksonUtility.fromObject(fetchInfo).with("response")
				.with("allUsers")
				.withArray("users");
		Assert.assertNotNull(userList);
		Assert.assertTrue(!userList.isEmpty());
		for(JsonNode node : userList ) {
			ObjectNode user = (ObjectNode) node;
			String name = user.get("name").asText();
			Assert.assertNotEquals("hiuser", name);
		}
	}
	
	@Test
	public void share_a3_verify_from_hiadmin() throws Exception {
		String formData = "{\"type\":\"folder\",\"dir\":\"ShareThisFolderWithHiUser\"}";
		String response = testUtility.retriveShareInfo(formData);
		ArrayNode userArray = JacksonUtility.fromObject(response).with("response").withArray("user");
		Assert.assertTrue(!userArray.isEmpty());

		String fetchInfoFormData = "{\"provide\":{\"provideUsers\":\"true\",\"provideRoles\":\"true\",\"provideOrganizations\":\"true\",\"id\":\"all\"}}";
		String fetchInfo = testUtility.fetchInfo(fetchInfoFormData);
		
		ArrayNode userList = JacksonUtility.fromObject(fetchInfo).with("response")
				.with("allUsers")
				.withArray("users");
		Assert.assertNotNull(userList);
		Assert.assertTrue(!userList.isEmpty());
		for(JsonNode node : userList ) {
			ObjectNode user = (ObjectNode) node;
			String name = user.get("name").asText();
			Assert.assertNotEquals("hiadmin", name);
		}
	}
	
	static String orgName = "Org_" + System.currentTimeMillis();
	static String orgId = "";
	static String userId = "";
	static String userName = "User_" + System.currentTimeMillis();
	
	@Test
	public void share_a4_createOrgUser() throws Exception {
		
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@helical.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
		
		String adminFormData = "{\"id\":\"\",\"email\":\""+userName+"_admin@helical.com\",\"name\":\""+userName+"_admin\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String adminResponse = testUtility.createUser(adminFormData);
		JSONObject adminResponseObject = JSONObject.fromObject(adminResponse);
		String adminId  = adminResponseObject.getJSONObject("response").getString("id");
		Map<Integer,Map<String,Integer>> allRoleMap =  testUtility.getRoleMap();
		Map<String,Integer> currentOrgRoles = allRoleMap.get(Integer.valueOf(orgId));
		String attachRole = "{\"id\":"+adminId+",\"name\":\""+userName+"_admin\",\"email\":\""+userName+"_admin@helical.com\",\"enabled\":true,\"roleIds\":["+currentOrgRoles.get("ROLE_ADMIN")+","+currentOrgRoles.get("ROLE_USER")+"],\"password\":\"\"}";
		testUtility.attachRole(attachRole, adminId);
		
	}
	
	@Test
	public void share_a5_shareFolderWithOrgUser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"ShareThisFolderWithHiUser\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void share_a6_verify_from_org_user() throws Exception {
		String formData = "{\"username\":\""+userName+"\",\"j_organization\":\""+orgName+"\",\"password\":\"password\",\"type\":\"folder\",\"dir\":\"ShareThisFolderWithHiUser\"}";
		formData = testUtility.addTokenInFormData(formData);
		String response = testUtility.retriveShareInfo(formData);
		ArrayNode userArray = JacksonUtility.fromObject(response).with("response").withArray("user");
		Assert.assertTrue(!userArray.isEmpty());
		
		String fetchInfoFormData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"provide\":{\"provideUsers\":\"true\",\"provideRoles\":\"true\",\"provideOrganizations\":\"true\"}}";
		fetchInfoFormData = testUtility.addTokenInFormData(fetchInfoFormData);
		String fetchInfo = testUtility.fetchInfo(fetchInfoFormData);
		ArrayNode userList = JacksonUtility.fromObject(fetchInfo).with("response")
				.with("allUsers")
				.withArray("users");
		
		int userCount = JacksonUtility.fromObject(fetchInfo).with("response")
				.with("allUsers").get("total").asInt();
		Assert.assertEquals(1,userCount);
		Assert.assertNotNull(userList);
		Assert.assertTrue(!userList.isEmpty());
		for(JsonNode node : userList ) {
			ObjectNode user = (ObjectNode) node;
			String name = user.get("name").asText();
			Assert.assertNotEquals(userName, name);
		}
	}
}
