package com.helicalinsight.recyclebin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserWithPublicFolderDeleteTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;
	
	@Qualifier("userDetailsService")
	@Autowired
	private UserService userService;
	
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
	
	
	/**
	 * Test Class Summary
	 * 
	 * 1. Create Public Folder
	 * 2. Create User
	 * 3. Delete User
	 * 4. Delete User from Recycle Bin
	 * 5. Verify  if user deleted successfully from Database
	 * 6. Cleanup the Recycle Bin
	 * 
	 */
	
	@Test
	public void rec_a1_createPublicFolder() throws Exception {
		
		testUtility.clearRecycleBin();
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "UserPublicFolder");
        map.put("sourceArray", sourceList.toString());
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("A new folder is created successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.permissionLevel").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.public").value(true));
	}
	
	static String userName = "User" + System.currentTimeMillis();
	static String userId = "";
	@Test
	public void rec_a2_createUser() throws Exception {
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@test.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(response);
		userId = userResponseObj.getJSONObject("response").getString("id");
		testUtility.deleteUser(userId);
	}
	
	@Test
	public void rec_a3_deleteUserFromRB() throws Exception {
		JsonObject binItem =  testUtility.listRecycleBin().get(0).getAsJsonObject();
		String binId = binItem.get("recycleBinId").getAsString();
		String deleteBin = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"],\"force\":true}";
		String response = testUtility.recycleBinAction(deleteBin);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", message);
	}
	
	@Test(expected = EfwServiceException.class)
	public void rec_a4_verify() throws Exception {
		userService.findUser(Integer.valueOf(userId));
	}
	
	@Test
	public void rec_a5_clean() throws Exception {
		testUtility.clearRecycleBin();
	}
	
}
