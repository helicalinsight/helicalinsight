package com.helicalinsight.adhoc;

import java.io.File;
import java.util.ArrayList;
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

import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DynamicSwitchTest {
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
		private static String dbName = "";
		private static String jdbcUrl = "";
		private static String sqliteDbName = "";
		private static String sqliteJdbcUrl = "";
		private static String sqliteJdbcId = "";
		private static String hieeUrl = "";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "/home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
			hieeUrl = "jdbc:derby:"+String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
			
			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			hieeUrl = "jdbc:derby:"+String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "C:", "home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
		}
	}

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void md_a1_create_a_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "DynamicSwitchTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}
	
	@Test
	public void md_a2_test_connection() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		map.put("formData",
				"{\"classifier\":\"efwd\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n      if(userName == \\\"hiuser\\\")\\n        responseJson.put(\\\"jdbcUrl\\\", \\\""+jdbcUrl+"\\\");\\n      if(userName == \\\"hiadmin\\\")\\n      responseJson.put(\\\"jdbcUrl\\\", \\\""+hieeUrl+"\\\");\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"DynamicSwitchingGroovy\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"DynamicSwitchTest\",\"type\":\"sql.jdbc.groovy\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
         .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful"));
	}
	static String id = "";
	@Test
	public void md_a3_create_groovy_plan_jdbc_datasource() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		map.put("formData",
				"{\"classifier\":\"efwd\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n      if(userName == \\\"hiuser\\\")\\n        responseJson.put(\\\"jdbcUrl\\\", \\\""+jdbcUrl+"\\\");\\n      if(userName == \\\"hiadmin\\\")\\n      responseJson.put(\\\"jdbcUrl\\\", \\\""+hieeUrl+"\\\");\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"DynamicSwitchingGroovy\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"dataSourceType\":\"Groovy Plain Jdbc DataSource\",\"directory\":\"DynamicSwitchTest\",\"type\":\"sql.jdbc.groovy\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		
		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("dataSourceId"));
		id = response.getString("dataSourceId");
	}
	@Test
	public void md_a4_to_create_metadata_expand_catlog_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":"+id+",\"type\":\"sql.jdbc.groovy\",\"dir\":\"DynamicSwitchTest\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a5_expand_table() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"dir\":\"DynamicSwitchTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"type\":\"sql.jdbc.groovy\",\"id\":"+id+",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n      if(userName == \\\"hiuser\\\")\\n        responseJson.put(\\\"jdbcUrl\\\", \\\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\\\");\\n      if(userName == \\\"hiadmin\\\")\\n      responseJson.put(\\\"jdbcUrl\\\", \\\"jdbc:derby:/home/helical/Performance/hi/db/hiee\\\");\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a6_create_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+id+"\",\"type\":\"sql.jdbc.groovy\",\"baseType\":\"sql.jdbc.groovy\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"DynamicSwitchTest\",\"connId\":\"jgj5q\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_5\",\"location\":\"DynamicSwitchTest\",\"metadataReload\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertTrue(responseObject.has("data"));
		JSONArray array = responseObject.getJSONArray("data");
		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			Assert.assertTrue(data.containsKey("lastModified"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("options"));
			Assert.assertTrue(data.containsKey("extension"));
			Assert.assertTrue(data.containsKey("path"));
			Assert.assertTrue(data.containsKey("permissionLevel"));
			Assert.assertTrue(data.containsKey("name"));
			Assert.assertTrue(data.containsKey("title"));

		}
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
	}
	

	@Test
	public void md_a8_delete_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", "[\"DynamicSwitchTest/Metadata_5.metadata\"]");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}

	@Test
	public void md_a9_share_datasource() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "share");
		map.put("service", "update");

		map.put("formData",
				"{\"classifier\":\"efwd\",\"id\":"+id+",\"type\":\"dataSource\",\"dir\":\"DynamicSwitchTest\",\"share\":{\"user\":[{\"id\":2,\"permission\":3}]}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
         .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The selected datasource privileges are updated successfully."));
	}
	
	@Test
	public void md_b1_to_create_metadata_expand_catlog_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":"+id+",\"type\":\"sql.jdbc.groovy\",\"dir\":\"DynamicSwitchTest\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_b2_expand_table() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"dir\":\"DynamicSwitchTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"type\":\"sql.jdbc.groovy\",\"id\":"+id+",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"condition\":\"import net.sf.json.JSONObject;\\n    import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n    public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        responseJson.put(\\\"driver\\\", \\\"org.apache.derby.jdbc.AutoloadedDriver\\\");\\n      if(userName == \\\"hiuser\\\")\\n        responseJson.put(\\\"jdbcUrl\\\", \\\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\\\");\\n      if(userName == \\\"hiadmin\\\")\\n      responseJson.put(\\\"jdbcUrl\\\", \\\"jdbc:derby:/home/helical/Performance/hi/db/hiee\\\");\\n        responseJson.put(\\\"user\\\", \\\"hiuser\\\");\\n        responseJson.put(\\\"pass\\\", \\\"hiuser\\\");\\n        return responseJson;\\n    }\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_b3_deleteFolder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"DynamicSwitchTest\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}

	
	
}
