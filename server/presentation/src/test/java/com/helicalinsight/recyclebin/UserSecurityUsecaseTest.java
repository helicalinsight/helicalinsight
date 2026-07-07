package com.helicalinsight.recyclebin;

import java.io.File;

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
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserSecurityUsecaseTest {
	
	
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

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	static String fileName = "";
	private static String TESTURL = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
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
	public void user_sec_a0_clear_bin() throws Exception {
		testUtility.clearRecycleBin();
	}
	
	static String userId = "";
	
	@Test
	public void user_sec_a1_create_an_user() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\"alien045@helical.com\",\"name\":\"alien045\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\"alien045\",\"email\":\"alien045@helical.com\",\"enabled\":true,\"roleIds\":[2,1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void user_sec_a3_create_folder() throws Exception {
		testUtility.createFolder("UserSecurityUseCaseTest", "alien045","password");
	}
	
	@Test
	public void user_sec_a4_share_folder() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"1\",\"permission\":\"3\"}]},\"type\":\"folder\",\"dir\":\"UserSecurityUseCaseTest\",\"username\":\"alien045\",\"password\":\"password\"}";
		formData  = testUtility.addTokenInFormData(formData);
		testUtility.shareResource(formData);
	}
	
	@Test
	public void user_sec_a5_exportResource() throws Exception {
		String formData = "{\"dir\": \"UserSecurityUseCaseTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true},\"username\":\"alien045\",\"password\":\"password\"}";
		formData  = testUtility.addTokenInFormData(formData);
		fileName=testUtility.exportResource(formData, TESTURL);
	}
	
	@Test
	public void user_sec_a6_delete_user() throws Exception {
		testUtility.deleteUser(userId);
		JsonArray array =  testUtility.listRecycleBin();
		Assert.assertEquals(1, array.size());
		User user =  userService.findUser(Integer.valueOf(userId));
		Assert.assertTrue(user.isDeleted());
	}
	
	@Test
	public void user_sec_a7_delete_resource() throws Exception {
		testUtility.deleteResource("UserSecurityUseCaseTest");
		JsonArray array =  testUtility.listRecycleBin();
		Assert.assertEquals(2, array.size());
	}
	
	@Test
	public void user_sec_a8_import_resource() throws Exception {
		String formData = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(formData, TESTURL, fileName);
		JsonArray array =  testUtility.listRecycleBin();
		Assert.assertEquals(1, array.size());
	}
	

}
