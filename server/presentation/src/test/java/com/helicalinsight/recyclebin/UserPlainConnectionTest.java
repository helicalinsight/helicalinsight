package com.helicalinsight.recyclebin;

import java.io.File;
import java.util.HashMap;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
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

public class UserPlainConnectionTest {

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
	
	@Qualifier("userDetailsService")
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}


	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	/**
	 * 1. Create new User 
	 * 2. Login as new user and  create folder and plain connection
	 * 3. Delete Plain Connection and Folder
	 * 4. Delete User
	 * 5. Permanently Delete User.
	 * 6. Verify
	 * 7. Clear bin
	 * 
	 */
	

	static String firstDSId = "";
	static String secondDsId="";
	static String userName = "User"+System.currentTimeMillis();
	static String userId = "";
	
	@Test
	public void rec_a1_createUser() throws Exception {
		testUtility.clearRecycleBin();
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@test.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(response);
		userId = userResponseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\""+userName+"\",\"email\":\""+userName+"@test.com\",\"enabled\":true,\"roleIds\":[\"1\"],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void rec_a2_createFolder() throws Exception {
		testUtility.createFolder("UserPlainConnectionTest", userName,"password");
	}
	
	static String plainId = "";
	
	@Test
	public void rec_a3_createPlainConnection() throws Exception {
		String plain = "{\"username\":\""+userName+"\",\"password\":\"password\",\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"UserPlainConnectionTest\",\"type\":\"sql.jdbc\"}";
		plain = testUtility.addTokenInFormData(plain);
		String plainResponse = testUtility.createPlainDatasource(plain);
		JSONObject responseObject = JSONObject.fromObject(plainResponse).getJSONObject("response");
		plainId = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void rec_a4_deletePlainConnection() throws Exception {
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+plainId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"UserPlainConnectionTest\"}";
		formData = testUtility.addTokenInFormData(formData);
		formData = testUtility.replaceCredentials(formData, map);
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists());
	}
	
	
	@Test
	public void rec_a5_deleteFolderAndUser() throws Exception {
		testUtility.deleteResource("UserPlainConnectionTest");
		testUtility.deleteUser(userId);
	}
	
	@Test
	public void rec_a5_deleteUserFromRB() throws Exception {
		
		JsonArray array =  testUtility.listRecycleBin();
		String binId = "";
		for( JsonElement obj : array ) {
			JsonObject json =  obj.getAsJsonObject();
			if("Users".equalsIgnoreCase(json.get("recycleBinType").getAsString())) {
				binId = json.get("recycleBinId").getAsString();
				break;
			}
		}
		String deleteBin = "{\"action\":\"delete\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtility.recycleBinAction(deleteBin);
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", message);
	}
	
	@Test(expected = EfwServiceException.class)
	public void rec_a6_verify() throws Exception {
		userService.findUser(Integer.valueOf(userId));
	}
	
	@Test
	public void rec_a7_clear_recycle_bin() throws Exception {
		testUtility.clearRecycleBin();
	}
}
