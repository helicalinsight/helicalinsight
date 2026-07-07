package com.helicalinsight.core.datasource;

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
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.util.NestedServletException;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.externalauth.jwt.TokenProvider;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManagedGroovyConnectionOwnerChangeTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;


	@Autowired
	private AdminController adminController;
	
	@Autowired
	private HIResourceServiceDB serviceDb;


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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}


	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	private static String firstJdbcId = "";

	
	@Test
	public void ds_a1_create_folder() throws Exception {
		testUtility.createFolder("ManagedGroovyConnectionOwnerChangeTest");
	}
	

	@Test
	public void ds_a2_createDataSourceConnection() throws Exception {

		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\"}";
		String response = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject data = responseObject.getJSONObject("data");
		firstJdbcId = data.getString("id");
	}
	static String userName = "user_" + System.currentTimeMillis();
	static Integer roleId;
	static String  userId;
	
	@Test
	public void ds_a3_createUser() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\""+userName+"@temp.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		String response = testUtility.createUser(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		userId = responseObj.getJSONObject("response").getString("id");
		String attachRole = "{\"id\":"+userId+",\"name\":\""+userName+"\",\"email\":\""+userName+"@helical.com\",\"enabled\":true,\"roleIds\":[2,1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void ds_a4_changeOwnerOfFolder() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("ManagedGroovyConnectionOwnerChangeTest");
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\""+userId+"\"}";
		testUtility.changeOwner(formData);
		HIResource resource_1 =  serviceDb.getResourceByUrl("ManagedGroovyConnectionOwnerChangeTest");
		Assert.assertEquals(userId,""+resource_1.getCreatedBy());
	}
	
	
	@Test
	public void ds_a5_1_testConnection_shouldThrowException() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\",\"name\":\"GroovyManagedDatasource\",\"id\":"+firstJdbcId+"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
	}
	
	@Test
	public void ds_a5_updateConnection_shouldThrowException() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\",\"name\":\"GroovyManagedDatasource\",\"id\":"+firstJdbcId+"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
	}
	
	@Test
	public void ds_a6_read_ds_shouldThrowException() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"id\":"+firstJdbcId+",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"dir\":\"ManagedGroovyConnectionOwnerChangeTest\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
	}
	
	
	@Test
	public void ds_a7_delete_ds_shouldThrowException() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":"+firstJdbcId+",\"type\":\"simple\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\",\"driver\":null}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("Error: EfwServiceException: Access Denied. You don't have sufficient privileges to access  the requested resource", message);
	}
	
	
	
	
	@Test
	public void ds_a8_shareFolder() throws Exception {
		String bearer = testUtility.generateAuthToken(userName, "password","");
		String formData = "{\"username\":\""+userName+"\",\"password\":\"password\",\"Bearer\":\""+bearer+"\",\"share\":{\"user\":[{\"id\":\"1\",\"permission\":\"5\"}]},\"type\":\"folder\",\"dir\":\"ManagedGroovyConnectionOwnerChangeTest\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void ds_a9_update() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\",\"name\":\"GroovyManagedDatasource\",\"id\":"+firstJdbcId+"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("The efwd connection is updated with the new details successfully.", message);
	}
	
	
	@Test
	public void ds_b1_read_ds() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"id\":"+firstJdbcId+",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"dir\":\"ManagedGroovyConnectionOwnerChangeTest\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
	}
	
	@Test
	public void ds_b2_delete_ds() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":"+firstJdbcId+",\"type\":\"simple\",\"directory\":\"ManagedGroovyConnectionOwnerChangeTest\",\"driver\":null}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		String message  = JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response")
				.getString("message");
		Assert.assertEquals("The data source has been deleted successfully.",message);
	}
	
	
}
