package com.helicalinsight.recyclebin;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganisationRecycleBinDeletePhase4Test {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;
	
	@Autowired
	private HIRecycleBinService rbinService;
	
	@Bean
	public AdminController adminController() {
		return new AdminController();
	}
	
	@Autowired
	private AdminController adminController;
	
	@Autowired
	private OrganizationService orgService;
	
	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
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
	
	static String userOneId = null;
	static String userTwoId = null;
	
	static String orgName = "Org"+System.currentTimeMillis();
	static String userOneName = "User1"+ System.currentTimeMillis();
	static String userTwoName = "User2"+ System.currentTimeMillis();
	static String orgId = "";
	
	@Test
	public void user_a0_create_org() throws Exception {
		testUtility.clearRecycleBin();
		String formData = "{\"name\":\"Deleted_"+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		String id = JacksonUtility.fromObject(response).with("response").get("id").asText();
		testUtility.deleteOrg(id);
	}
	
	
	@Test
	public void user_a1_create_org_and_2_users() throws Exception {
		
		
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\""+userOneName+"@test.com\",\"name\":\""+userOneName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userOneId = userResponseObj.getJSONObject("response").getString("id");
		Map<Integer,Map<String,Integer>> orgRoleMap = testUtility.getRoleMap();
		Map<String,Integer> roleNameIdMap = orgRoleMap.get(Integer.valueOf(orgId));
		int adminRoleId = roleNameIdMap.get("ROLE_ADMIN");
		int userRoleId = roleNameIdMap.get("ROLE_USER");
		String attachRole = "{\"id\":"+userOneId+",\"name\":\""+userOneName+"\",\"email\":\"test@helical.com\",\"enabled\":true,\"roleIds\":["+adminRoleId+","+userRoleId+"],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userOneId);
		
		String user2FormData = "{\"id\":\"\",\"email\":\""+userTwoName+"@test.com\",\"name\":\""+userTwoName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String user2Response = testUtility.createUser(user2FormData);
		JSONObject user2ResponseObj = JSONObject.fromObject(user2Response);
		userTwoId = user2ResponseObj.getJSONObject("response").getString("id");
	}
	
	
	@Test
	public void user_a2_createAndDeleteFolder() throws Exception {
		testUtility.createFolder("UserTestFolder2", userOneName, "password",orgName);
		testUtility.deleteResource("UserTestFolder2");
	}
	
	static User hiuser = null;
	
	@Test
	public void user_a3_deleteUser() throws Exception {
		testUtility.deleteUser(userTwoId);
		hiuser = userService.findUserByNameNorgNull("hiuser", false);
		testUtility.deleteUser(""+hiuser.getId());
	}

	@Test
	public void user_a4_list_recycleBin_with_org_admin() throws Exception  {
		JsonArray array =  testUtility.listRecycleBin(userOneName,"password",orgName);
		Assert.assertEquals(2, array.size());
	}
	
	@Test
	public void user_a5_list_recycleBin_with_super_admin() throws Exception  {
		JsonArray array =  testUtility.listRecycleBin();
		Assert.assertEquals(4, array.size());
	}
	@Test
	public void user_a6_restore_hiuser() throws Exception {
		Long binId = rbinService.findHIRecycleBinByUserId(hiuser.getId()).getId();
		String formData = "{\"action\":\"restore\",\"recycleBinIds\":[" + binId + "]}";
		String response = testUtility.restore(formData);
	}
	
}
