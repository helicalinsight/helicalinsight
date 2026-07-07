package com.helicalinsight.recyclebin.fetchdetails;

import java.util.List;

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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FetchDetailsMultipleBinTest {
	
	
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

	static String userId = "";

	@Test
	public void fetch_det_a0_create_an_user() throws Exception {
		testUtility.clearRecycleBin();
		String formData = "{\"id\":\"\",\"email\":\"alien129919208282@helical.com\",\"name\":\"alien129919208282\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\"alien129919208282\",\"email\":\"alien129919208282@helical.com\",\"enabled\":true,\"roleIds\":[2,1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void fetch_det_a1_create_folder() throws Exception {
		testUtility.createFolder("FolderToTestMultipleBinFetchDetails");
		testUtility.createFolder("Children", List.of("FolderToTestMultipleBinFetchDetails"));
	}

	@Test
	public void fetch_det_a2_delete_user_and_folder() throws Exception {
		testUtility.deleteUser(userId);
		testUtility.deleteResource("FolderToTestMultipleBinFetchDetails");
	}
	
	@Test
	public void fetch_det_a3_fetchDetails() throws Exception {
		 String  userBinId = testUtility.getRecycleBinIdByResourceName("alien129919208282");
		 String  folderBinId =  testUtility.getRecycleBinIdByResourceName("FolderToTestMultipleBinFetchDetails");
		 String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":["+userBinId+","+folderBinId+"]}";
		 String response = testUtility.recycleBinAction(formData);
		 JSONObject userData = JSONObject.fromObject(response).getJSONObject("response").getJSONObject(userBinId).getJSONObject("data");
		 JSONObject folderData = JSONObject.fromObject(response).getJSONObject("response").getJSONObject(folderBinId).getJSONObject("data");
		 Assert.assertTrue(userData.isEmpty());
		 Assert.assertFalse(folderData.isEmpty());
	}
}
