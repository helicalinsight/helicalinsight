package com.helicalinsight.admin.controller;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
import org.springframework.web.util.NestedServletException;

import com.google.gson.JsonArray;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletException;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
public class AdminControllerTest {

	MockMvc mockMvc;

	@Bean
	public AdminController adminController() {
		return new AdminController();
	}

	@Autowired
	FilterChainProxy security;

	@Autowired
	private AdminController adminController;

	private static Integer userId;

	private static Integer orgId;

	private static Integer profileId;

	private static Integer roleId;

	private static Integer userWithoutOrgId;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.adminController).addFilters(security).build();
	}

	@Test
	public void testStepaGetAllOrganization() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("limit", 5);
		formData.accumulate("offset", 0);
		formData.accumulate("searchOn", "name");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepbAddOrganisation() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "org1");
		formData.accumulate("description", "test organisation");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		orgId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("Organization added successfully", message);

	}

	@Test(expected = ServletException.class)
	public void testStepcAddDuplicateOrganisation() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "org1");
		formData.accumulate("description", "test organisation");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepdGetRole() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("limit", 5);
		formData.accumulate("offset", 0);
		formData.accumulate("searchOn", "name");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepeCreateRole() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "role2");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		roleId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("Role added successfully", message);
	}

	@Test(expected = ServletException.class)
	public void testStepfCreateRoleWithoutOrg() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "role2");
		formData.accumulate("organisation", "");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Role added successfully"));
	}

	@Test
	public void testStepgGetUser() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("limit", 5);
		formData.accumulate("offset", 0);
		formData.accumulate("searchOn", "user");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStephAddUser() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		userId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("User created successfully.", message);
	}

	@Test(expected = ServletException.class)
	public void testStepiAddDuplicateUser() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepjAddUserWithoutOrganizationValue() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "testWithoutOrgId");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", "");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);


		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		userWithoutOrgId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("User created successfully.", message);

//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User created successfully."));
	}

	@Test(expected = ServletException.class)
	public void testStepkAddUserWithoutOrganization() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test1");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "true");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test(expected = ServletException.class)
	public void testSteplAddUserWithOrganizationNotExist() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "false");
		formData.accumulate("organisation", "123");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testStepmUpdateUser() throws Exception {
		JSONObject formData = new JSONObject();
		JsonArray roleArray = new JsonArray();
		roleArray.add(roleId.toString());
		formData.accumulate("password", "password");
		formData.accumulate("email", "test@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());
		formData.accumulate("roleId", roleArray.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("id", userId.toString());
		map.put("action", "update");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User updated successfully. "));
	}



	@Test
	public void testzStepnUpdateUserRole() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId.toString());
		map.put("roleIds[]", ""+roleId);
		map.put("action", "userRoles");
		map.put("organisation", "Null");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Role updated successfully"));
	}




	@Test
	public void testStepnAddProfile() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test");
		formData.accumulate("value", "test");
		formData.accumulate("id", userId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);


		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		profileId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("Profile added successfully.", message);
	}

	@Test(expected = ServletException.class)
	public void testStepoAddDuplicateProfile() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test");
		formData.accumulate("value", "test");
		formData.accumulate("id", userId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Profile added successfully."));
	}

	@Test
	public void testSteppUpdateProfile() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test2");
		formData.accumulate("value", "test2");
		formData.accumulate("id", userId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("id", profileId.toString());
		map.put("action", "update");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Profile updated successfully"));
	}

	@Test
	public void testStepqGetProfile() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("userId", userId.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testSteprDeleteProfile() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("action","delete");
		map.put("id", profileId.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Profile successfully deleted"));
	}

	@Test
	public void testStepsdeleteUser() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("id", userId.toString());
		map.put("action", "delete");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User deleted successfully"));
	}

	@Test(expected = ServletException.class)
	public void testSteptdeleteLoggedInUser() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("id", "1");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepuDeleteRole() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("id", roleId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "delete");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Role deleted successfully"));
	}



	@Test(expected = ServletException.class)
	public void testStepvDeleteLoggedInUserRole() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("id", "1");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "delete");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testStepwDeleteOrganisation() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("id", orgId.toString());
		map.put("action", "delete");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
				.jsonPath("$.response.message").value("Organization deleted successfully "));
	}

	@Test
	public void testStepxDeleteOrgWithOtherUser() throws Exception {
		RequestBuilder builder = MockMvcRequestBuilders.get("/admin/organisations").param("action", "delete")
				.param("id", "1").param("username", "hari").param("password", "harisingh");
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isFound());
	}


	@Test
	public void testStepsdeleteUserWitoutOrgId() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("id", userWithoutOrgId.toString());
		map.put("action", "delete");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User deleted successfully"));
	}

	@Test(expected = ServletException.class)
	public void z1_userWithSpecialCharacter() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test(");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test$@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		userId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(0, status);
		String message = response.getString("message");
		Assert.assertEquals("Username can only use A-Z, a-z, 0-9, -, _,—, ', &, . , @, $, +, !, / and can have spaces.", message);
	}	
	@Test(expected = ServletException.class)
	public void z2_orgWithSpecialChars() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "org1_(");
		formData.accumulate("description", "test organisation");

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		orgId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(0, status);
		String message = response.getString("message");
		Assert.assertEquals("Organization name can only use A-Z, a-z, 0-9, . and _ and cannot have spaces", message);

	}
	@Test(expected = ServletException.class)
	public void z3_testStepeCreateRole() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "role2`)");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/roles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		roleId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(0, status);
		String message = response.getString("message");
		Assert.assertEquals("Role name can only use A-Z, a-z, 0-9, ., @ , $, +, !, /, & and _ and can have spaces.", message);
	}

	@Test(expected = ServletException.class)
	public void z4_testStepnAddProfile() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test/");
		formData.accumulate("value", "test");
		formData.accumulate("id", userId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/admin/profiles");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		profileId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(0, status);
		String message = response.getString("message");
		Assert.assertEquals("Profile name can only use A-Z, a-z, @, $, 0-9,,, . and _ and spaces.", message);
	}
	@Test(expected = ServletException.class)
	public void z5_userWithinValidEmail() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "test_fake_user");
		formData.accumulate("password", "password");
		formData.accumulate("email", "test$@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);

		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		userId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(0, status);
		String message = response.getString("message");
		Assert.assertEquals("Invalid Email address.", message);
	}


}
