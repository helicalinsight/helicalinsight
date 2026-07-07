package com.helicalinsight.recyclebin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganisationRecycleBinListTest {

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
	static String uName = "User" + System.currentTimeMillis();
	static String orgId = "";
	
	@Test
	public void user_a1_create_org_and_user() throws Exception {
		testUtility.clearRecycleBin();
		
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\"thetestghost@helical.com\",\"name\":\""+uName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
		Map<Integer, Map<String, Integer>> roleMap = testUtility.getRoleMap();
		Map<String,Integer> roleNameIdMap =  roleMap.get(Integer.valueOf(orgId));
		String attachRoleFormData = "{\"id\":"+userId+",\"name\":\""+uName+"\",\"email\":\"thetestghost@helical.com\",\"enabled\":true,\"roleIds\":["+roleNameIdMap.get("ROLE_ADMIN")+","+roleNameIdMap.get("ROLE_USER")+"],\"password\":\"\"}";
		testUtility.attachRole(attachRoleFormData, userId);
	}
	
	@Test
	public void user_a2_create_folder_with_super_admin() throws Exception {
		testUtility.createFolder("OrganisationRecycleBinListTest");
		testUtility.deleteResource("OrganisationRecycleBinListTest");
	}
	
	@Test
	public void user_a3_create_folder_with_org_admin() throws Exception {
		testUtility.createFolder("OrganisationRecycleBinListTestOrg", uName,"password",orgName);
		
		String input = "[\"OrganisationRecycleBinListTestOrg\"]";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray",input);
		map.put("username", uName);
		map.put("password", "password");
		map.put("j_organization", orgName);
		map.put("Bearer", testUtility.generateAuthToken(uName, "password", orgName));
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful")).andReturn();
	}
	
	
	@Test
	public void user_a4_list_bin_with_org_admin() throws Exception {
		JsonArray array =  testUtility.listRecycleBin(uName,"password",orgName);
		Assert.assertEquals(1, array.size());
		for(JsonElement object : array ) {
			JsonObject json = object.getAsJsonObject();
			Assert.assertEquals(uName, json.get("deletedBy").getAsString());
		}
	}
	
	@Test
	public void user_a5_list_bin_with_super_admin() throws Exception {
		JsonArray array =  testUtility.listRecycleBin();
		Assert.assertEquals(2, array.size());
		Set<String> deletedBySet = new HashSet<>();
		for(JsonElement object : array ) {
			JsonObject json = object.getAsJsonObject();
			deletedBySet.add(json.get("deletedBy").getAsString());
		}
		Assert.assertEquals(2, deletedBySet.size());
	}
	
	@Test
	public void user_a6_clear_bin_of_org_user() throws Exception {
		testUtility.clearRecycleBin(uName,"password",orgName);
		JsonArray array =  testUtility.listRecycleBin(uName,"password",orgName);
		Assert.assertEquals(0, array.size());
		
		JsonArray array2 = testUtility.listRecycleBin();
		Assert.assertEquals(1, array2.size());
	}
	
	@Test
	public void user_a7_cleanup() throws Exception {
		testUtility.clearRecycleBin();
	}
	
}
