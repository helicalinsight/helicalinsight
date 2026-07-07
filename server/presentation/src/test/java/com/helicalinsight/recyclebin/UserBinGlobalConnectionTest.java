/*
package com.helicalinsight.recyclebin;

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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.DataSourceConnectionNotFoundException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserBinGlobalConnectionTest {
	
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
	private GlobalConnectionService globalConnectionService;

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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}
	
	static String jdbcUrl = "";
    static String dbName = "";
    static String userName = "User"+System.currentTimeMillis();
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

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	
	static String datasourceId = "";
	static String userId = "";
	@Test
	public void rec_a1_init() throws Exception {
		testUtility.clearRecycleBin();
		String userFormData = "{\"id\":\"\",\"email\":\""+userName+"@helical.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String userResponse = testUtility.createUser(userFormData);
		JSONObject userResponseObj = JSONObject.fromObject(userResponse);
		userId = userResponseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\""+userName+"\",\"email\":\""+userName+"@helical.com\",\"enabled\":true,\"roleIds\":[1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"username\":\"hiuser\",\"password\":\"hiuser\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
       // formData = testUtility.addTokenInFormData(formData);
		String response = testUtility.createDatasource(formData);
        JSONObject responseObject = JSONObject.fromObject(response).getJSONObject("response");
    	datasourceId = responseObject.getString("dataSourceId");
	}
	
	@Test
	public void rec_a2_delete_and_restore_connection() throws Exception {
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"global\",\"id\":\"" + datasourceId
				+ "\",\"type\":\"simple\",\"dataSourceProvider\":\"tomcat\"}";
		map.put("formData", formData);
		map.put("username", userName);
		map.put("password", "password");
		map.put("Bearer", testUtility.generateAuthToken(userName, "password", ""));
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The datasource " + datasourceId + " have been deleted successfully"));
	}
	
	@Test
	public void rec_a3_deleteUser() throws Exception {
		testUtility.deleteUser(userId);
	}
	@Test
	public void rec_a4_delete_userPermanently() throws Exception {
		String binId = testUtility.getRecycleBinIdByResourceName(userName);
		String formData="{\"action\":\"delete\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtility.recycleBinAction(formData);
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", JSONObject.fromObject(response).getJSONObject("response").getString("message"));
	}
	
	@Test
	public void rec_a5_verify() throws Exception {
		Assert.assertNull(globalConnectionService.findGlobalConnectionById(Integer.valueOf(datasourceId)));
	}
	
}
*/
