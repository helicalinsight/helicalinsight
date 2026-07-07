package com.helicalinsight.adhoc;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CubeTest {

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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void md_1_create_a_folder_to_save_metadata_and_cube() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "CubeTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void md_2_a_to_create_metadata_expand_catlog_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_2_b_expand_table() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_2_c_create_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"gun46\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_1\",\"location\":\"CubeTest\",\"metadataReload\":true}");
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
			System.out.println("Data : " + data);
			Assert.assertTrue(data.containsKey("lastModified"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("options"));
			Assert.assertTrue(data.containsKey("extension"));
			Assert.assertTrue(data.containsKey("path"));
			Assert.assertTrue(data.containsKey("permissionLevel"));
			Assert.assertTrue(data.containsKey("name"));
			Assert.assertTrue(data.containsKey("title"));
			Assert.assertEquals(1, status);
			Assert.assertEquals("Successfully saved metadata file", message);
		}
	}

	@Test
	public void md_3_create_cube_and_save() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "cube");
		map.put("service", "update");
		map.put("formData",
				"{\"dataSource\":{\"sync\":false,\"id\":\"1\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\"},\"fileName\":\"cube-1\",\"location\":\"CubeTest\",\"cubes\":[{\"setCache\":true,\"caption\":\"Cube Creation\",\"defaultMeasure\":\"defaultMeasureName\",\"description\":\"First Cube Creation\",\"enabled\":true,\"cubeName\":\"cube-1\",\"annotation\":\"To be discussed\",\"domainName\":[\"test\"],\"dimensions\":[{\"dimensionName\":\"created_date\",\"dimensionType\":{\"java.lang.String\":\"text\"},\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"columnName\":\"created_date\",\"columnId\":\"cf48b4c7-2da3-4598-8f61-f21cf57edb09\",\"hierarchies\":[{\"hierarchyName\":\"created_date\",\"visible\":true,\"primaryColumnId\":\"cf48b4c7-2da3-4598-8f61-f21cf57edb09\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"hierarchyType\":{\"java.lang.String\":\"text\"},\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_date\",\"levels\":[{\"levelName\":\"created_date\",\"columnId\":\"cf48b4c7-2da3-4598-8f61-f21cf57edb09\",\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_date\",\"levelType\":{\"java.lang.String\":\"text\"},\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"semanticType\":\"entity\",\"synonyms\":[\"sdf\"],\"topic\":[\"sdf\"],\"formula\":\"\",\"filter\":\"\",\"example\":\"\",\"description\":\"\"}]}]},{\"dimensionName\":\"created_time\",\"dimensionType\":{\"java.lang.String\":\"text\"},\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"columnName\":\"created_time\",\"columnId\":\"e90d0f75-1938-4f14-916a-cdea891fb679\",\"hierarchies\":[{\"hierarchyName\":\"created_time\",\"visible\":true,\"primaryColumnId\":\"e90d0f75-1938-4f14-916a-cdea891fb679\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"hierarchyType\":{\"java.lang.String\":\"text\"},\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_time\",\"levels\":[{\"levelName\":\"created_time\",\"columnId\":\"e90d0f75-1938-4f14-916a-cdea891fb679\",\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_time\",\"levelType\":{\"java.lang.String\":\"text\"},\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"semanticType\":\"organization\",\"synonyms\":[\"fee\"],\"topic\":[\"fees\"],\"formula\":\"\",\"filter\":\"\",\"example\":\"\",\"description\":\"\"}]}]}],\"measures\":[]}],\"metadata\":{\"location\":\"CubeTest\",\"metadataFileName\":\"Metadata_1.metadata\"},\"uniqueId\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		System.out.println("md_3 :" + responseObject);
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
			Assert.assertEquals(1, status);
			Assert.assertEquals("Cube saved successfully", message);
		}

	}

	static JSONObject responseObjectCube = null;

	@Test
	public void md_4_read_cube() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.get("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "cube");
		map.put("service", "getCube");
		map.put("formData", "{\"dir\":\"CubeTest\",\"file\":\"cube_1.cube\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		int status = jsonObject.getInt("status");
		responseObjectCube = jsonObject.getJSONObject("response");
	}

	@Test
	public void md_5_update_cube() throws Exception {

		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "cube");
		map.put("service", "update");

		map.put("formData",
				"{\"dataSource\":{\"sync\":false,\"id\":\"1\",\"connectionDatabaseId\":\"ef9f4c88-82f1-408a-8bd3-d78ecdc5d121\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\"},\"fileName\":\"cube_1.cube\",\"location\":\"CubeTest\",\"cubes\":"
						+ responseObjectCube.getString("cubes")
						+ ",\"metadata\":{\"location\":\"CubeTest\",\"metadataFileName\":\"Metadata_1.metadata\"}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));

	}

	@Test
	public void md_6_delete_cube() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"CubeTest/cube_1.cube\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}

	@Test
	public void md_7_delete_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"CubeTest\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
		
		testUtility.clearRecycleBin();
	}
	
	

}
