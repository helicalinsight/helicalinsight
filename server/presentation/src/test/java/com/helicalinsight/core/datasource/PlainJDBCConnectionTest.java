package com.helicalinsight.core.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.controller.DataSourceController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlainJDBCConnectionTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	MockMvc dsMock;

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
	
	static String firstDSId = "";
	static String secondDsId = "";

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}
	
	
	private static String TESTURL = "";
	private static String jdbcUrl = "";
	private static String firstJdbcId = "";
	private static String firstGroovy = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home","helical","Performance","hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home","helical","Performance","hi","db","SampleTravelData");
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
		map.put("folderName", "DatasourceTest");
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
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"DatasourceTest\",\"type\":\"sql.jdbc\"}";
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
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"DatasourceTest\",\"type\":\"sql.jdbc\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.isPublic").exists())
				.andReturn();
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
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC2\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"DatasourceTest\",\"type\":\"sql.jdbc\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been saved successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.isPublic").exists())
				.andReturn();
		
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		
		JSONObject responseObject = jsonObject.getJSONObject("response");
		secondDsId = responseObject.getString("dataSourceId");
	}

	@Test
	public void a5_listDataSources() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/listDataSources");
		Map<String, String> map = new HashMap<>();
		map.put("type", "sql.jdbc");
		map.put("name", "Plain Jdbc DataSource");
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
			Assert.assertTrue(object.containsKey("baseType"));
			JSONObject data = object.getJSONObject("data");
			Assert.assertTrue(data.containsKey("id"));
			Assert.assertTrue(data.containsKey("dir"));
			Assert.assertTrue(data.containsKey("driverName"));
			Assert.assertTrue(data.containsKey("password"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("jdbcUrl"));
			Assert.assertTrue(data.containsKey("userName"));
			Assert.assertTrue(data.containsKey("isPublic"));
		}
	}

	@Test
	public void a6_readDataSource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "read");
		String formData = "{\"dir\":\"DatasourceTest\",\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+firstDSId+"\",\"type\":\"sql.jdbc\",\"classifier\":\"efwd\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response").exists()).andReturn();
		
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		Assert.assertTrue(responseObject.containsKey("@id"));
		Assert.assertTrue(responseObject.containsKey("@name"));
		Assert.assertTrue(responseObject.containsKey("@type"));
		Assert.assertTrue(responseObject.containsKey("@baseType"));
		Assert.assertTrue(responseObject.containsKey("jdbcUrl"));
		Assert.assertTrue(responseObject.containsKey("driverName"));
		Assert.assertTrue(responseObject.containsKey("userName"));
		Assert.assertTrue(responseObject.containsKey("password"));
	}

	@Test
	public void a7_updateDataSource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "update");
		String formData = "{\"classifier\":\"efwd\",\"name\":\"UpdatedPlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\"jdbc:derby:\\//home//helical//Performance//HITest//hiee\",\"database\":\"\\//home//helical//Performance//HITest//hiee\",\"directory\":\"DatasourceTest\",\"type\":\"sql.jdbc\",\"id\":\""+secondDsId+"\"}";
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
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.driver").exists())
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.isPublic").exists());
	}

	@Test
	public void a8_deleteDataSource() throws Exception{
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+secondDsId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"DatasourceTest\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data").exists());
	}
	
	@Test
	public void a9_1_getSharedInfo() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "retrieveSharedInfo");
		map.put("formData","{\"classifier\":\"efwd\",\"id\":"+firstDSId+",\"type\":\"dataSource\",\"dir\":\"Datasources\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource is not shared with other users/roles/organizations."));

	}

	@Test
	public void a9_shareDatasourceWithRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"share\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}

	@Test
	public void b1_verifyDataSourceShareWithRoleUser() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNotNull(security);
		Assert.assertEquals(Integer.valueOf(firstDSId), security.getHiEfwdConnection().getId());
	}
	
	@Test
	public void b1_zretriveShareInfo() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "retrieveSharedInfo");
		map.put("formData","{\"classifier\":\"efwd\",\"id\":"+firstDSId+",\"type\":\"dataSource\",\"dir\":\"DatasourceTest\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.organization").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.user").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.role").exists());
	}

	@Test
	public void b2_revokeDatasourceOfRoleUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"revoke\":{\"role\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}

	@Test
	public void b3_verifyDataSourcerevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}

	@Test
	public void b4_shareDatasourceWithUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}

	@Test
	public void b5_verifyDataSourceShareWithUser() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNotNull(security);
		Assert.assertEquals(Integer.valueOf(firstDSId), security.getHiEfwdConnection().getId());
	}

	@Test
	public void b6_revokeDatasourceOfUser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		map.put("formData",
				"{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"revoke\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}

	@Test
	public void b7_verifyUserDataSourcerevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}
	
	@Test
	public void b8_sharewithComboOfUsersAndRoles() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"share\":{\"user\":[{\"id\":\"4\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"1\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"1\",\"permission\":\"2\"}],\"role\":[{\"id\":\"1\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"2\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"4\",\"permission\":\"2\"}]}}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void b9_revokeCombo() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");
		String formData = "{\"type\":\"dataSource\",\"id\":\""+firstDSId+"\",\"dir\":\"DatasourceTest\",\"classifier\":\"efwd\",\"revoke\":{\"user\":[{\"id\":\"4\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"1\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"1\",\"permission\":\"2\"}],\"role\":[{\"id\":\"1\",\"permission\":\"2\"},{\"id\":\"2\",\"permission\":\"2\"},{\"id\":\"3\",\"permission\":\"2\"},{\"id\":\"4\",\"permission\":\"2\"}]}}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void c1_verifyComboRevoke() throws Exception {
		HIEfwdConnSecurity security = dsService.findEfConnectionSecurityByConnectionId(Integer.valueOf(firstDSId));
		Assert.assertNull(security);
	}
}
