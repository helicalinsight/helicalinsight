/*
package com.helicalinsight.core.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockServletContext;
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
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.efw.framework.BootTimeListener;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManagedGroovyConnectionTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;


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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
		MockServletContext mockServletContext = new MockServletContext();
		ServletContextEvent event = new ServletContextEvent(mockServletContext);
		BootTimeListener bootTimeListener=new BootTimeListener();
		bootTimeListener.contextInitialized(event);
	}

	@Autowired
	private EfwServicesController efwServicesController;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String firstJdbcId = "";

	@Test
	public void ds_a1_create_a_folder_to_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "GroovyManagedDatasource");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void ds_a2_testDataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}
	@Test
	public void ds_a3_createDataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =   this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The data source has been saved successfully."))
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject data = responseObject.getJSONObject("data");
		firstJdbcId = data.getString("id");
	}
	
	@Test
	public void ds_a4_groovy_managed_quick_test() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "quickTest");
		String formData = "{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"dir\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful."));
	}
	
	@Test
	public void ds_a5_groovy_managed_read() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"dir\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.permissionLevel").value("5"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.dir").value("GroovyManagedDatasource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy.managed"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.condition").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("GroovyManagedDatasource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.classifier").value("efwd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy.managed"));
	}
	
	@Test
	public void ds_a6_groovy_managed_update() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasourceUpdated\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"GroovyManagedDatasource\",\"id\":\""+firstJdbcId+"\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The efwd connection is updated with the new details successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.dir").value("GroovyManagedDatasource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy.managed"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(firstJdbcId));
				
	}
	
	@Test
	public void ds_a7_groovy_managed_read_updated() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"classifier\":\"efwd\",\"dir\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.permissionLevel").value("5"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.dir").value("GroovyManagedDatasource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy.managed"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.condition").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("GroovyManagedDatasourceUpdated"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.classifier").value("efwd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy.managed"));
	}
	
	@Test
	public void ds_a8_groovy_managed_share_with_role_user() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"id\":\""+firstJdbcId+"\",\"dir\":\"GroovyManagedDatasource\",\"type\":\"dataSource\",\"share\":{\"user\":[{\"id\":4,\"permission\":1},{\"id\":2,\"permission\":3},{\"id\":3,\"permission\":3}],\"role\":[{\"id\":4,\"permission\":2},{\"id\":1,\"permission\":2}]}}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		 this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully.")).andReturn();
		// BUG 6007
		 ds_a7_groovy_managed_read_updated();
	}
	
	@Test
	public void ds_a9_1_shareFolder() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"5\"}]},\"type\":\"folder\",\"dir\":\"GroovyManagedDatasource\"}";
		testUtility.shareResource(formData);
	}
	
	@Test
	public void ds_a9_groovy_managed_ds_count() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("username","hiuser");
		map.put("password", "hiuser");
		map.put("type", "monitor");
		map.put("serviceType", "system");
		map.put("service", "datasourceCount");
		String formData = "{}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());

		JSONObject responseObject = jsonObject.getJSONObject("response");
		int count = responseObject.getInt("dataSourceCount");
		Assert.assertTrue(count > 1);

	}
	
	
	@Test
	public void ds_b1_groovy_managed_retrieve_share_info() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "retrieveSharedInfo");
		String formData = "{\"id\":\""+firstJdbcId+"\",\"type\":\"dataSource\",\"classifier\":\"efwd\",\"dir\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.organization").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.user").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.role").exists());
	}
	static Integer orgId ;
	@Test
	public void ds_b2_create_orgnization() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "FakeOrg");
		formData.accumulate("description", "FakeOrg");
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/organisations");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		orgId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("Organization added successfully", message);

	}
	static Integer roleId;
	static Integer userId;

	@Test
	public void ds_b3_create_user() throws Exception {
		JSONObject formData = new JSONObject();
		formData.accumulate("name", "fakeuser");
		formData.accumulate("password", "password");
		formData.accumulate("email", "fakeuser@helical.com");
		formData.accumulate("enabled", "true");
		formData.accumulate("organisation", orgId.toString());
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/admin/users");
		Map<String, String> map = new HashMap<>();
		map.put("formData", formData.toString());
		map.put("action", "add");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		
		MvcResult result = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		JSONObject object = JSONObject.fromObject(content);
		JSONObject response = (JSONObject) object.get("response");
		userId = response.getInt("id");
		int status = object.getInt("status");
		Assert.assertEquals(1, status);
		String message = response.getString("message");
		Assert.assertEquals("User created successfully.", message);
	}
	
	@Test
	public void ds_b4_1_shareFolderWithOrgUser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":\"5\"}]},\"type\":\"folder\",\"dir\":\"GroovyManagedDatasource\"}";
		testUtility.shareResource(formData);
	}
	@Test
	public void ds_b4_groovy_managed_share_with_org() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"id\":\""+firstJdbcId+"\",\"type\":\"dataSource\",\"dir\":\"GroovyManagedDatasource\",\"share\":{\"organization\":[{\"id\":\""+orgId+"\",\"permission\":2}]}}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	@Test
	public void ds_b5_groovy_managed_ds_count() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("username","fakeuser");
		map.put("password", "password");
		map.put("j_organization", "FakeOrg");
		map.put("type", "monitor");
		map.put("serviceType", "system");
		map.put("service", "datasourceCount");
		String formData = "{}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());

		JSONObject responseObject = jsonObject.getJSONObject("response");
		int count = responseObject.getInt("dataSourceCount");
		Assert.assertTrue(count >  1);

	}
	
	@Test
	public void ds_b6_groovy_managed_delete() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":\""+firstJdbcId+"\",\"type\":\"cascade\",\"directory\":\"GroovyManagedDatasource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(firstJdbcId));
			
	}
	
	@Test
    public void ds_b7_deleteFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"GroovyManagedDatasource\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Delete operation is successful"));
    }
	
	
	
	
}
*/
