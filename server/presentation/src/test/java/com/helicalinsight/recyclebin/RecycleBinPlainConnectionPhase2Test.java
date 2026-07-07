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
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
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

public class RecycleBinPlainConnectionPhase2Test {

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
	private EFWDConnectionService connectionService;

	
	@Autowired
	private OrganizationService orgService;
	
	@Autowired
	@Qualifier("userDetailsService")
	private UserService userService;
	
	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	static String userId = null;
	
	static String orgName = "Org"+System.currentTimeMillis();
	static String userName = "User"+ System.currentTimeMillis();
	static String orgId = "";
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

	@Test
	public void rec_a0_createOrganisationUser() throws Exception {
		testUtility.clearRecycleBin();
		String formData = "{\"name\":\""+orgName+"\",\"description\":\""+orgName+"\"}";
		String response = testUtility.createOrg(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		orgId = responseObj.getJSONObject("response").getString("id");
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@test.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\""+orgId+"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
		Map<Integer,Map<String,Integer>> orgRoleMap = testUtility.getRoleMap();
		Map<String,Integer> roleNameIdMap = orgRoleMap.get(Integer.valueOf(orgId));
		int adminRoleId = roleNameIdMap.get("ROLE_ADMIN");
		String attachRole = "{\"id\":"+userId+",\"name\":\""+userName+"\",\"email\":\""+userName+"@test.com\",\"enabled\":true,\"roleIds\":["+adminRoleId+"],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void rec_a1_create_a_folder_to_save_datasoure() throws Exception {
		testUtility.createFolder("RecycleBinPlainConnectionPhase2Test",userName,"password",orgName);
	}

	static String firstDSId = "";
	
	@Test
	public void rec_a2_createPlainConnection() throws Exception {
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"RecycleBinPlainConnectionPhase2Test\",\"type\":\"sql.jdbc\"}";
		formData = testUtility.addTokenInFormData(formData);
		String response = testUtility.createPlainDatasource(formData);
		JSONObject responseObject = JSONObject.fromObject(response).getJSONObject("response");
		firstDSId = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void rec_a3_deletePlainConnection() throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"j_organization\":\""+orgName+"\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+firstDSId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"RecycleBinPlainConnectionPhase2Test\"}";
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
	public void rec_a4_deleteUser() throws Exception {
		testUtility.deleteUser(userId);
	}
	
	@Test
	public void rec_a5_deleteUserPermanently() throws Exception {
		JsonArray array =  testUtility.listRecycleBin();
		String binId = "";
		for( JsonElement obj : array ) {
			JsonObject json =  obj.getAsJsonObject();
			if("Users".equalsIgnoreCase(json.get("recycleBinType").getAsString())) {
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
	public void rec_a6_verifyOrganization() throws Exception {
		Assert.assertNotNull(orgService.getOrganization(orgName));
	}
	
	@Test(expected = EfwServiceException.class)
	public void rec_a7_verifyUser() throws Exception {
		userService.findUser(Integer.valueOf(userId));
	}
	
	@Test
	public void rec_a8_verifyDataSource() throws Exception {
		Assert.assertNull(connectionService.findConnectionByID(Integer.valueOf(firstDSId)));
	}
	
//	@Test
	public void rec_a9_clean() throws Exception {
		testUtility.clearRecycleBin();
	}
}
