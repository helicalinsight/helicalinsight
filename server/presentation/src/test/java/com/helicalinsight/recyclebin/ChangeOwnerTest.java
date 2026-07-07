package com.helicalinsight.recyclebin;

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
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ChangeOwnerTest {

	MockMvc efwMock;
	MockMvc mockMvc;

	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
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

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = (ServletContext) context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void rec_a1_create_a_folder() throws Exception {
		testUtilitiy.createFolder("ChangeOwnerTest");
		testUtilitiy.createFolder("ChangeOwnerTest2");
	}

	static String secondResourceId = "";
	static String userId = "";
	static String secondUserId = "";

	@Test
	public void rec_a2_0_create_user() throws Exception {
		String userFormData = "{\"id\":\"\",\"email\":\"userWithRoleUserAndAdmin@helical.com\",\"name\":\"userWithRoleUserAndAdmin\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		userId = JSONObject.fromObject(testUtilitiy.createUser(userFormData)).getJSONObject("response").getString("id");

		String userFormData2 = "{\"id\":\"\",\"email\":\"userWithRoleUserAndAdmin2@helical.com\",\"name\":\"userWithRoleUserAndAdmin2\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		secondUserId = JSONObject.fromObject(testUtilitiy.createUser(userFormData2)).getJSONObject("response").getString("id");


		String attachRole = "{\"id\":\""+userId+"\",\"name\":\"userWithRoleUserAndAdmin\",\"email\":\"userWithRoleUserAndAdmin@helical.com\",\"enabled\":true,\"roleIds\":[1],\"password\":\"\"}";
		testUtilitiy.attachRole(attachRole, userId);
	}

	@Test
	public void rec_a2_1_change_owner() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("ChangeOwnerTest");
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\""+secondUserId+"\"}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject response = JSONObject.fromObject(result.getResponse().getContentAsString());
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("Successfully changed ownership of resource(s)", message);
	}

	@Test
	public void rec_a2_change_owner() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("ChangeOwnerTest");
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\""+userId+"\"}";
		testUtilitiy.changeOwner(formData);
		resource = serviceDb.getResourceByUrl("ChangeOwnerTest");
		Assert.assertEquals(userId,""+resource.getCreatedBy());

		// should throw exception
		changeOwner_to_same_user(formData);
	}

	public void changeOwner_to_same_user(String formData) throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0))
				.andReturn();
		JSONObject response =  JSONObject.fromObject(result.getResponse().getContentAsString()).getJSONObject("response");
		Assert.assertEquals("Error: OwnershipTransferException: The ownership of the resource(s) cannot be changed to the same user, as it is already assigned. The ownership will remain unchanged.", response.getString("message"));
	}

	static String childId = "";

	@Test
	public void rec_a3_create_childFolder() throws Exception {
		testUtilitiy.createFolder("Child1", List.of("ChangeOwnerTest2"));
		HIResource child = serviceDb.getResourceByUrl("ChangeOwnerTest2/Child1");
		childId = ""+child.getResourceId();
	}

	@Test
	public void rec_a4_delete_folder() throws Exception {
		HIResource resource0 =  serviceDb.getResourceByUrl("ChangeOwnerTest2");
		secondResourceId =  ""+ resource0.getResourceId();
		testUtilitiy.deleteResource("ChangeOwnerTest2");
		HIResource resource =  serviceDb.getResourceByUrl("ChangeOwnerTest2");
		Assert.assertNull(resource);
	}

	/*@Test
	public void rec_a5_change_owner_of_deleted_folder() throws Exception {
		String formData ="{\"type\":\"Folders\",\"resources\":["+secondResourceId+"],\"newOwnerId\":\""+userId+"\"}";
		String response = testUtilitiy.changeOwner(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		Assert.assertEquals("0", responseObj.getString("status"));
		*///Assert.assertEquals("Successfully changed ownership of resource(s)", responseObj.getJSONObject("response").getString("message"));
	//}

	@Test
	public void rec_a6_change_owner_of_child() throws Exception {
		String formData ="{\"type\":\"Folders\",\"resources\":["+childId+"],\"newOwnerId\":\""+userId+"\"}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Error: EfwServiceException: Can not change ownership , since the parent of the resource has been deleted."));
	}

	static String secondJdbcId = "";

	@Test
	public void rec_a7_create_datasource() throws Exception {
		String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
		String dsResponse = testUtilitiy.createDatasource(derby);
		JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
		secondJdbcId = responseObject.getString("dataSourceId");
	}

	@Test
	public void rec_a8_changeOwner() throws Exception {
		String formData ="{\"type\":\"Datasources[Managed]\",\"resources\":["+secondJdbcId+"],\"newOwnerId\":\""+userId+"\"}";
		testUtilitiy.changeOwner(formData);

		// should throw exception
		changeOwner_to_same_user(formData);
	}


	static String thirdJdbcid = "";

	@Test
	public void rec_a9_create_datasource() throws Exception {
		String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
		String dsResponse = testUtilitiy.createDatasource(derby);
		JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
		thirdJdbcid = responseObject.getString("dataSourceId");
	}

	@Test
	public void rec_b1_deleteDatasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"global\",\"id\":\"" + thirdJdbcid + "\",\"type\":\"simple\",\"dataSourceProvider\":\"tomcat\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The datasource " + thirdJdbcid + " have been deleted successfully"));
	}

	@Test
	public void rec_b2_changeOwner() throws Exception {
		String formData ="{\"type\":\"Datasources[Managed]\",\"resources\":["+thirdJdbcid+"],\"newOwnerId\":\""+userId+"\"}";
		String response = testUtilitiy.changeOwner(formData);
		JSONObject responseObj = JSONObject.fromObject(response);
		Assert.assertEquals("1", responseObj.getString("status"));
		Assert.assertEquals("Successfully changed ownership of resource(s)", responseObj.getJSONObject("response").getString("message"));
	}

	@Test
	public void rec_b3_create_folder_to_store_plain_connection() throws Exception {
		testUtilitiy.createFolder("EfwdDatasourceOwnershipChange");
	}

	static String plainId = "";

	@Test
	public void rec_b4_create_plain_connection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"name\":\"PlainJdbcTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""
				+ jdbcUrl + "\",\"database\":\"" + dbName
				+ "\",\"directory\":\"EfwdDatasourceOwnershipChange\",\"type\":\"sql.jdbc\"}";
		String plainResponse = testUtilitiy.createPlainDatasource(formData);
		JSONObject responseObject = JSONObject.fromObject(plainResponse).getJSONObject("response");
		plainId = responseObject.getString("dataSourceId");
	}

	/*@Test
	public void rec_b5_change_plain_con_ownership() throws Exception {
		String formdata = "{\"type\":\"datasources[efwd]\",\"resources\":["+plainId+"],\"newOwnerId\":\""+userId+"\"}";
		testUtilitiy.changeOwner(formdata);
		EFWDConnectionService service = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
		HIEfwdConnection connection =  service.findConnectionById(plainId, false);
		Assert.assertEquals(Integer.valueOf(userId), (Integer) connection.getHiResourceEFWD().getCreatedBy());

		String formdata2 = "{\"type\":\"datasources[efwd]\",\"resources\":["+plainId+"],\"newOwnerId\":\"1\"}";
		testUtilitiy.changeOwner(formdata2);
		HIEfwdConnection connection2 =  service.findConnectionById(plainId, false);
		Assert.assertEquals(1, (int) connection2.getHiResourceEFWD().getCreatedBy());

		// should throw exception
		changeOwner_to_same_user(formdata2);

	}*/

	@Test
	public void rec_b6_plain_jdbc_delete() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":\""+plainId+"\",\"type\":\"cascade\",\"directory\":\"EfwdDatasourceOwnershipChange\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(plainId));
	}

	/*@Test
	public void rec_b7_change_plain_con_ownership_for_deleted_con() throws Exception {
		String formdata = "{\"type\":\"datasources[efwd]\",\"resources\":["+plainId+"],\"newOwnerId\":\""+userId+"\"}";
		testUtilitiy.changeOwner(formdata);
		EFWDConnectionService service = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
		HIEfwdConnection connection =  service.findConnectionById(plainId, false);
		Assert.assertEquals(Integer.valueOf(userId), (Integer) connection.getHiResourceEFWD().getCreatedBy());
	}*/

	@Test
	public void rec_b8_change_ownership_invalid_id() throws Exception {
		String formdata = "{\"type\":\"Folders\",\"resources\":[-1],\"newOwnerId\":\""+userId+"\"}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formdata);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(result -> {
			JSONObject json = JSONObject.fromObject(result.getResponse().getContentAsString());
			Assert.assertEquals("Error: OwnershipTransferException: Resource not found.", json.getJSONObject("response").getString("message"));
		});
	}

	@Test
	public void rec_b9_change_ownership_Datasource_invalid_id() throws Exception {
		String formdata = "{\"type\":\"Datasources[Managed]\",\"resources\":[-2],\"newOwnerId\":\""+userId+"\"}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formdata);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(result -> {
			JSONObject json = JSONObject.fromObject(result.getResponse().getContentAsString());
			Assert.assertEquals("Error: OwnershipTransferException: Datasource not found.", json.getJSONObject("response").getString("message"));
		});
	}
}
