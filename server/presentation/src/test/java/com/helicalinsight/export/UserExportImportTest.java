package com.helicalinsight.export;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserExportImportTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceServiceDB serviceDb;
	
	@Qualifier("userDetailsService")
	@Autowired
	UserService userService;
	
	@Autowired
	IntegrationTestUtility testUtility;
	
	static String fileName = "";

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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String TESTURL = "";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	@Test
	public void exp_a1_create_a_folder() throws Exception {
		testUtility.createFolder("UserImportExportTest");
	}
	
	static String userId = "";
	
	@Test
	public void exp_a2_create_user() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\"theghost@helical.com\",\"name\":\"theghost\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
	}
	
	@Test
	public void exp_a3_share_with_user()  throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":\"5\"}]},\"type\":\"folder\",\"dir\":\"UserImportExportTest\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void exp_a4_export_shared_folder() throws Exception {
		String request = "{\"dir\": \"UserImportExportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public  void exp_a5_import_folder_shared_with_org_user() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(request, TESTURL, fileName);
	}
	
	@Test
	public void exp_a6_update_user() throws Exception {
		String formData = "{\"id\":"+userId+",\"name\":\"TheGhost\",\"email\":\"theghost21212@ghost.com\",\"enabled\":true,\"roleIds\":[2],\"password\":\"newpassword\"}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData);
		map.put("action", "update");
		map.put("id",userId);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("User updated successfully. "));
		exp_a5_import_folder_shared_with_org_user();
	}
	
	@Test
	public void exp_a7_verify() throws Exception {
		User user = userService.findUser(Integer.valueOf(userId));
		Assert.assertNotNull(user);
		Assert.assertNotNull(user.getRoles());
		Assert.assertEquals(1,user.getRoles().size());
		Assert.assertEquals("theghost@helical.com",user.getEmailAddress());
		Assert.assertTrue(user.isEnabled());
	}
	
	
}
	
	
