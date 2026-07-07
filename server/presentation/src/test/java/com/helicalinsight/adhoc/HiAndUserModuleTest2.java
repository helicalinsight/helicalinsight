//package com.helicalinsight.adhoc;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import javax.transaction.Transactional;
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
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.efw.controller.EFWController;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import net.sf.json.JSONSerializer;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//
//public class HiAndUserModuleTest2 {
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
//	@Autowired
//	private EFWController efwController;
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//	
//	
//	static int orgId;
//	
//	@Test
//	public void hi_a1_create_organization_xyzOrg() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/organisations");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"name\":\"xyzOrg\",\"description\":\"xyz\"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("Organization added successfully")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		orgId = jsonObject.getJSONObject("response").getInt("id");
//		
//		
//		
//	}
//	static int roleUserId;
//	static int roleAdminId;
//	@Test
//    public void hi_a2_get_roles() throws Exception {
//    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/admin/roles");
//    	Map<String, String> map = new HashMap<>();
//    	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object1 = jsonObject.getJSONArray("roles").getJSONObject(0);
//		Assert.assertTrue(object1.containsValue("ROLE_USER"));
//		roleUserId = object1.getInt("id");
//		JSONObject object2 = jsonObject.getJSONArray("roles").getJSONObject(1);
//		Assert.assertTrue(object2.containsValue("ROLE_ADMIN"));
//		roleAdminId = object2.getInt("id");
//		
//		System.out.println(roleUserId+"  "+roleAdminId);
//		
//    }
//    
//	
//	static int userAdminId;
//	@Test
//	public void hi_a3_create_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"id\":\"\",\"email\":\"testAdmin@gmail.com\",\"name\":\"testAdmin\",\"enabled\":true,\"password\":\"testAdmin\",\"organisation\":"+orgId+"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User created successfully.")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		
//		userAdminId = jsonObject.getJSONObject("response").getInt("id");
//		
//
//	}
//
//	@Test
//	public void hi_a4_role_update_testAdmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "update");
//		map.put("formData", "{\"id\":"+userAdminId+",\"name\":\"testAdmin\",\"email\":\"testAdmin@gmail.com\",\"enabled\":true,\"roleIds\":["+roleUserId+","+roleAdminId+"],\"password\":\"\"}");
//		map.put("id",""+userAdminId+"");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User updated successfully. ")).andReturn();
//		
//	}
//	@Test
//    public void hi_a5_get_users() throws Exception {
//    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/admin/users");
//    	Map<String, String> map = new HashMap<>();
//    	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		System.out.println(jsonObject);
//		
//    }
//	
//	static int userId;
//	@Test
//	public void hi_a6_create_testUser() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/admin/users");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "add");
//		map.put("formData","{\"id\":\"\",\"email\":\"testUser@gmail.com\",\"name\":\"testUser\",\"enabled\":true,\"password\":\"testUser\",\"organisation\":"+orgId+"}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
//						.value("User created successfully.")).andReturn();
//		
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		userId = jsonObject.getJSONObject("response").getInt("id");
//		System.out.println(userId+"  "+userAdminId);
//	}
//	
//	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//	
//	@Test
//	public void hi_a7_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "india");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("india", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_a8_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Gujarat");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Gujarat", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_a9_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Maharashatra");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Maharashatra", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "punjab");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("punjab", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b2_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Ahamdabad");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Ahamdabad", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Ahamdabad", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b3_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Gandinagar");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Gandinagar", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Gandinagar", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b4_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Gujarat\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Surat");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Gujarat/Surat", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Surat", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b5_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Mumbai");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Mumbai", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Mumbai", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b6_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Pune");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Pune", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Pune", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b7_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/Maharashatra\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Ratanagiri");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/Maharashatra/Ratanagiri", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("Ratanagiri", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b8_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "amritsar");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/amritsar", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("amritsar", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_b9_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "chandigad");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/chandigad", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("chandigad", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_c1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"india/punjab\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "ludiyana");
//		map.put("sourceArray",input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//		String path = object.getString("path");
//		Assert.assertEquals("india/punjab/ludiyana", path);
//		
//		String level = object.getString("permissionLevel");
//		Assert.assertEquals("5",level);
//		
//		String name = object.getString("name");
//		Assert.assertEquals("ludiyana", name);
//		
//		
//		String type = object.getString("type");
//		Assert.assertEquals("folder", type);
//		
//		String selectable = object.getJSONObject("options").getString("selectable");
//		Assert.assertEquals("true", selectable);
//	}
//	@Test
//	public void hi_c2_getSolutionResources_in_hiadmin() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.get("/getSolutionResourcesTest");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("username", "hiadmin");
//		map.put("password", "hiadmin");
//		
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder)
//				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//		String string = result.getResponse().getContentAsString();
//		JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//		
//		String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//		Assert.assertEquals("5", permissionLevel);
//		JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//		JSONObject object = array.getJSONObject(0);
//		
//		String path1 = object.getString("path");
//		Assert.assertEquals("india/punjab", path1);
//		String path2 = array.getJSONObject(1).getString("path");
//		Assert.assertEquals("india/Maharashatra", path2);
//		String path3 = array.getJSONObject(2).getString("path");
//		Assert.assertEquals("india/Gujarat", path3);
//
//		
//	}
//
//    @Test
//	public void hi_c3_fetchInfo() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/services");
//		
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "share");
//		map.put("service","fetchInfo");
//		map.put("formData","{\"provide\":{\"provideUsers\":\"true\",\"provideRoles\":\"true\",\"provideOrganizations\":\"true\",\"id\":\"all\"}}");
//	
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}	
//
//@Test
//public void hi_c4_retrieveSharedInfo() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","retrieveSharedInfo");
//	map.put("formData","{\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//}
//@Test
//public void hi_c5_share_to_roleAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_c6_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("2", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
////@Test
//public void hi_c7_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	System.out.println("Test2: c7: "+ nameArray);
//	Assert.assertTrue(nameArray.isEmpty());
//	
//}
//@Test
//public void hi_c8_share_to_roleAdmin_noacess_for_oneofFolder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"india/Gujarat\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//	
//@Test
//public void hi_c9_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("2", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//}	
//
//@Test
//public void hi_d1_revoke_to_roleAdmin_noacess_of_oneofFolder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"india/Gujarat\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_d2_share_to_roleAdmin_read_write() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":3}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_d3_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("3", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_d4_rename_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}
//
//@Test
//public void hi_d5_share_to_roleAdmin_read_write_delete() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":4}]},\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_d6_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("4", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_d7_rename_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename2\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}	  
//@Test
//public void hi_d8_delete_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "delete");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Delete operation is successful"));
//
//}
////@Test
//public void hi_d9_getSolutionResources_in_hiadmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "hiadmin");
//	map.put("password", "hiadmin");
//	
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	Assert.assertTrue(nameArray.isEmpty());
//	
//}
////$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$44
//@Test
//public void hi_e1_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "india");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("india", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e2_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Gujarat");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Gujarat", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Gujarat", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e3_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Maharashatra");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Maharashatra", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Maharashatra", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e4_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "punjab");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/punjab", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("punjab", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e5_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Gujarat\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Ahamdabad");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Gujarat/Ahamdabad", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Ahamdabad", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e6_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Gujarat\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Gandinagar");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Gujarat/Gandinagar", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Gandinagar", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e7_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Gujarat\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Surat");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Gujarat/Surat", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Surat", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e8_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Maharashatra\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Mumbai");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Maharashatra/Mumbai", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Mumbai", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_e9_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Maharashatra\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Pune");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Maharashatra/Pune", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Pune", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_f1_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/Maharashatra\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "Ratanagiri");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/Maharashatra/Ratanagiri", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("Ratanagiri", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_f2_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/punjab\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "amritsar");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/punjab/amritsar", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("amritsar", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_f3_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/punjab\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "chandigad");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/punjab/chandigad", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("chandigad", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//@Test
//public void hi_f4_create_a_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india/punjab\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "newFolder");
//	map.put("folderName", "ludiyana");
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
//	JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//	JSONObject object = jsonObject.getJSONObject("response").getJSONObject("data");
//	String path = object.getString("path");
//	Assert.assertEquals("india/punjab/ludiyana", path);
//	
//	String level = object.getString("permissionLevel");
//	Assert.assertEquals("5",level);
//	
//	String name = object.getString("name");
//	Assert.assertEquals("ludiyana", name);
//	
//	
//	String type = object.getString("type");
//	Assert.assertEquals("folder", type);
//	
//	String selectable = object.getJSONObject("options").getString("selectable");
//	Assert.assertEquals("true", selectable);
//}
//
//
//@Test
//public void hi_f5_retrieveSharedInfo() throws Exception {
//MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//		.post("/services");
//
//Map<String, String> map = new HashMap<>();
//map.put("type", "core");
//map.put("serviceType", "share");
//map.put("service","retrieveSharedInfo");
//map.put("formData","{\"type\":\"folder\",\"dir\":\"india\"}");
//
//RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//}
//@Test
//public void hi_f6_share_to_roleAdmin_read_write_delete_share() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_f7_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_f8_rename_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename##\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}	  
//@Test
//public void hi_f9_share_from_testAdmin_to_testUser_read_write_delete_share() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"user\":[{\"id\":"+userId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_g1_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//
//@Test
//public void hi_g2_revoke_from_RoleAdmin_to_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}],\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_g3_share_to_RoleAdmin_and_RoleUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5},{\"id\":"+roleUserId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_g4_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_g5_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_g6_share_to_RoleUserOnly() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"revoke\":{\"role\":[{\"id\":"+roleAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_g7_getSolutionResources_in_testUser_as_roleuser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_g8_getSolutionResources_in_testAdmin_as_roleuser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_g9_rename_in_testUser_as_roleUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename5\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}
//
//@Test
//public void hi_h1_share_to_roleUser_to_roleAdmin_read_write_delete_share() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_h2_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_h3_share_to_roleUser_read_write_delete() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":4}]},\"revoke\":{\"user\":[{\"id\":"+userAdminId+",\"permission\":5}],\"role\":[{\"id\":"+roleUserId+",\"permission\":5}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_h4_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("4", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_h5_rename_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename123\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}	
//@Test
//public void hi_h6_share_to_roleUser_read_write() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":3}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_h7_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("3", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_h8_rename_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[[\"india\",\"indiaRename\"]]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "rename");
//
//	map.put("sourceArray",input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("Rename is successful"));
//
//}	
//@Test
//public void hi_h9_share_to_roleUser_read() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":2}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_i1_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("2", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_i2_share_to_roleUser_noaccess() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":0}]},\"revoke\":{\"role\":[{\"id\":"+roleUserId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
////@Test
//public void hi_i3_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	Assert.assertTrue(nameArray.isEmpty());
//	
//}
//@Test
//public void hi_i4_share_to_orgnization_xyzOrg_noaccess() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"organization\":[{\"id\":"+roleUserId+",\"permission\":0}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}],\"role\":[{\"id\":"+roleUserId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
////@Test
//public void hi_i5_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	Assert.assertTrue(nameArray.isEmpty());
//	
//}
////@Test
//public void hi_i6_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	Assert.assertTrue(nameArray.isEmpty());
//	
//		
//}
//@Test
//public void hi_i7_share_to_orgnization_xyzOrg_read() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":2}]},\"revoke\":{\"user\":[{\"id\":"+userId+",\"permission\":5}],\"role\":[{\"id\":"+roleUserId+",\"permission\":0}],\"organization\":[{\"id\":"+orgId+",\"permission\":0}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_i8_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("2", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_i9_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("2", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j1_share_to_orgnization_xyzOrg_read_write() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":3}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_j2_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("3", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j3_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("3", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j4_share_to_orgnization_xyzOrg_read_write_delete() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":4}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":3}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_j5_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("4", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j6_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("4", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j7_share_to_orgnization_xyzOrg_read_write_delete_share() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/services");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("type", "core");
//	map.put("serviceType", "share");
//	map.put("service","update");
//	map.put("formData","{\"share\":{\"organization\":[{\"id\":"+orgId+",\"permission\":5}]},\"revoke\":{\"organization\":[{\"id\":"+orgId+",\"permission\":4}]},\"type\":\"folder\",\"dir\":\"india\"}");
//
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
//	
//}
//@Test
//public void hi_j8_getSolutionResources_in_testUser() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testUser");
//	map.put("password", "testUser");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_j9_getSolutionResources_in_testAdmin() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.get("/getSolutionResourcesTest");
//	
//	Map<String, String> map = new HashMap<>();
//	map.put("username", "testAdmin");
//	map.put("password", "testAdmin");
//	map.put("j_organization", "xyzOrg");
//	
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	MvcResult result = this.efwMock.perform(builder)
//			.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
//	String string = result.getResponse().getContentAsString();
//	JSONArray nameArray = (JSONArray) JSONSerializer.toJSON(string);
//	
//	String permissionLevel = nameArray.getJSONObject(0).getString("permissionLevel");
//	Assert.assertEquals("5", permissionLevel);
//	
//	JSONArray array = nameArray.getJSONObject(0).getJSONArray("children");
//	String path1 = array.getJSONObject(0).getString("path");
//	Assert.assertEquals("india/punjab", path1);
//	String inherit1 = array.getJSONObject(0).getString("inherit");
//	Assert.assertEquals("true", inherit1);
//	
//	String path2 = array.getJSONObject(1).getString("path");
//	Assert.assertEquals("india/Maharashatra", path2);
//	String inherit2 = array.getJSONObject(1).getString("inherit");
//	Assert.assertEquals("true", inherit2);
//	
//	String path3 = array.getJSONObject(2).getString("path");
//	Assert.assertEquals("india/Gujarat", path3);
//	String inherit3 = array.getJSONObject(2).getString("inherit");
//	Assert.assertEquals("true", inherit3);
//	
//}
//@Test
//public void hi_k1_delete_folder() throws Exception {
//	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//			.post("/fileSystemOperations");
//	String input = "[\"india\"]";
//	Map<String, String> map = new HashMap<>();
//	map.put("action", "delete");
//
//	map.put("sourceArray", input);
//	RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//	this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//					MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//}
//}
