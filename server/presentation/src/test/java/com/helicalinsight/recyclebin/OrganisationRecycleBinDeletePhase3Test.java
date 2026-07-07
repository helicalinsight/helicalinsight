package com.helicalinsight.recyclebin;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganisationRecycleBinDeletePhase3Test {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;
	
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
	
	static String userId = null;
	
	static String orgName = "Org"+System.currentTimeMillis();
	static String userName = "User"+ System.currentTimeMillis();
	static String orgId = "";
	
	/**
	 * 1. Create User with Organization
	 * 2. Login with the new user
	 * 3. Create Folder
	 * 4. Delete Folder
	 * 5. Logout from the current user
	 * 6. Delete Organization
	 * 7. Remove Organization from RecycleBin
	 * 8. Verify db
	 */
	
	@Test
	public void user_a1_create_org_and_user() throws Exception {
		
		testUtility.clearRecycleBin();
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@test.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
	}
	
	@Test
	public void user_a2_createAndFolder() throws Exception {
		testUtility.createFolder("UserTestFolder", userName, "password",orgName);
		testUtility.deleteResource("UserTestFolder");
	}
	
	@Test
	public void user_a3_deleteOrganization() throws Exception {
		testUtility.deleteOrg(orgId);
	}
	
	@Test
	public void user_a4_deleteBin() throws Exception {
		JsonArray array =  testUtility.listRecycleBin();
		String binId = "";
		for( JsonElement obj : array ) {
			JsonObject json = obj.getAsJsonObject();
			if("Organizations".equalsIgnoreCase(json.get("recycleBinType").getAsString())) {
				binId = json.get("recycleBinId").getAsString();
				break;
			}
		}
		String deleteBin = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"],\"force\":true}";
		String response = testUtility.recycleBinAction(deleteBin);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", message);
	}
	
	@Test
	public void user_a5_verifyOrganisation() throws Exception {
		Organization org =  orgService.getOrganization(orgName);
		Assert.assertNull(org);
	}
	
	@Test(expected = EfwServiceException.class)
	public void user_a6_verifyUser() throws Exception {
		userService.findUser(Integer.valueOf(userId));
	}
	
	@Test
	public void user_a7_verifyFolder() throws Exception {
		Assert.assertNull(serviceDb.getResourceByUrl("UserTestFolder"));
	}
	
	@Test
	public void user_a8_clean() throws Exception {
		testUtility.clearRecycleBin();
	}
}
