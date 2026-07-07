package com.helicalinsight.core.datasource;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.controller.DataSourceController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroovyConnectionTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	MockMvc dsMock;
	
	static String firstDSId = "";
	static String secondDSId = "";

	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Autowired
	private DataSourceController dataSourceController;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceDBDAO serviceDao;

	@Autowired
	RoleService roleService;

	@Autowired
	EFWDConnectionService dsService;

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
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
		this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
	}

	
	
	@Test
	public void a1_create_a_folder_to_save_datasoure() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "GroovyDataSource");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void a2_testDataSourceConnection() throws Exception {

		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"GroovyDataSource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}

	@Test
	public void a3_createDatasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"GroovyDataSource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstDSId = responseObject.getString("dataSourceId");
	}

	// to confirm the one to many relation ship
	@Test
	public void a4_createSecondDatasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"    import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n        responseJson.put(\\\"url\\\", \\\""+jdbcUrl+"\\\" );\\n        responseJson.put(\\\"user\\\", \\\"root\\\");\\n        responseJson.put(\\\"pass\\\", \\\"root\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"GroovyDataSource2\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"GroovyDataSource\",\"type\":\"sql.jdbc.groovy\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists()).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		secondDSId = responseObject.getString("dataSourceId");
	}

	@Test
	public void a5_listDataSources() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/listDataSources");
		Map<String, String> map = new HashMap<>();
		map.put("type", "sql.jdbc.groovy");
		map.put("name", "Groovy Plain Jdbc DataSource");
		map.put("classifier", "efwd");
		map.put("categoryName", "advanced");
		map.put("categoryType", "advanced");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.dsMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONArray array = jsonObject.getJSONArray("dataSources");
		for(int i=0; i<array.size();i++) {
			JSONObject object = array.getJSONObject(i);
			Assert.assertTrue(object.containsKey("permissionLevel"));
			Assert.assertTrue(object.containsKey("driver"));
			JSONObject data = object.getJSONObject("data");
			Assert.assertTrue(data.containsKey("condition"));
			Assert.assertTrue(data.containsKey("id"));
			Assert.assertTrue(data.containsKey("dir"));
			Assert.assertTrue(data.containsKey("driverName"));
			Assert.assertTrue(data.containsKey("password"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("jdbcUrl"));
			Assert.assertTrue(data.containsKey("userName"));
		}
	}
	@Test
	public void a6_readDataSource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"dir\":\"GroovyDataSource\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+firstDSId+"\",\"type\":\"sql.jdbc.groovy\",\"classifier\":\"efwd\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.driver").value("org.apache.derby.jdbc.AutoloadedDriver"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.classifier").value("efwd"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("GroovyDataSource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.dir").value("GroovyDataSource"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.driverName").value("org.apache.derby.jdbc.AutoloadedDriver"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("sql.jdbc.groovy"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(firstDSId))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.condition").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.jdbcUrl").value(jdbcUrl));

	}
	@Test
	public void a7_updateDataSource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"name\":\"UpdatedGroovyConnection\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\"jdbc:derby:\\//home//helical//Performance//HITest//hiee\",\"database\":\"\\//home//helical//Performance//HITest//hiee\",\"directory\":\"GroovyDataSource\",\"type\":\"sql.jdbc.groovy\",\"id\":\""+firstDSId+"\",\"condition\" : \"import net.sf.json.JSONObject;\\r\\n\\t\\t\\t\\t\\timport com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\r\\n\\t\\t\\t\\t\\tpublic JSONObject evalCondition() {\\r\\n\\t\\t\\t\\t\\tJSONObject responseJson = new JSONObject();\\r\\n\\t\\t\\t\\t\\tString userName = GroovyUsersSession.getValue('${user}.name');\\r\\n\\t\\t\\t\\t\\tuserName = userName .replaceAll(\\\"'\\\",\\\"\\\");\\r\\n\\t\\t\\t\\t\\tresponseJson.put(\\\"driver\\\",\\\"com.mysql.jdbc.Driver\\\");\\r\\n\\t\\t\\t\\t\\tresponseJson.put(\\\"url\\\",\\\"jdbc:mysql://127.0.0.1:3306/\\\"+userName );\\r\\n\\t\\t\\t\\t\\tresponseJson.put(\\\"user\\\",\\\"root\\\");\\r\\n\\t\\t\\t\\t\\tresponseJson.put(\\\"pass\\\",\\\"root\\\");\\r\\n\\t\\t\\t\\t\\treturn responseJson;\\r\\n\\t\\t\\t\\t\\t}\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The efwd connection is updated with the new details successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.dir").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.driver").exists());
	}
	@Test
	public void a8_shareDatasourceWithRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData","{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"share\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void a9_verifyDataSourceShareWithRoleUser() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNotNull(security);
		Assert.assertEquals(Integer.valueOf(firstDSId), security.getHiEfwdConnection().getId());
	}
	
	@Test
	public void b1_revokeDatasourceOfRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"revoke\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	@Test
	public void b1_zretriveShareInfo() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "retrieveSharedInfo");
		map.put("formData","{\"classifier\":\"efwd\",\"id\":"+firstDSId+",\"type\":\"dataSource\",\"dir\":\"GroovyDataSource\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource is not shared with other users/roles/organizations."));
	}

	
	@Test
	public void b2_verifyDataSourcerevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}
	
	@Test
	public void b3_shareDatasourceWithUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void b4_verifyDataSourceShareWithUser() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNotNull(security);
		Assert.assertEquals(Integer.valueOf(firstDSId), security.getHiEfwdConnection().getId());
	}

	
	@Test
	public void b5_revokeDatasourceOfUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"revoke\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	@Test
	public void b6_verifyUserDataSourcerevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}
	
	@Test
	public void b7_sharewithComboOfUsersAndRoles() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"share\":{\"user\":[{\"id\":\"4\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"1\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"1\",\"permission\":\"2\"}],\"role\":[{\"id\":\"1\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"2\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"4\",\"permission\":\"2\"}]}}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void b8_revokeCombo() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"GroovyDataSource\",\"classifier\":\"efwd\",\"revoke\":{\"user\":[{\"id\":\"4\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"1\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"1\",\"permission\":\"2\"}],\"role\":[{\"id\":\"1\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"2\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"4\",\"permission\":\"2\"}]}}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	@Test
	public void b9_verifyComboRevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}
	@Test
	public void c1_deleteDataSource() throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+secondDSId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"GroovyDataSource\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").exists());
	}

}
