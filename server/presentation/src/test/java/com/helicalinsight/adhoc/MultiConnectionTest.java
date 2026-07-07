//package com.helicalinsight.adhoc;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class MultiConnectionTest {
//	MockMvc efwMock;
//	MockMvc mockMvc;
//	@Autowired
//	FilterChainProxy filterChainProxy;
//
//	@Autowired
//	private WebApplicationContext context;
//
//	@Autowired
//	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//
//	@Before
//	@Transactional
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
//				.build();
//		ServletContext servletContext = context.getServletContext();
//		servletContext.setAttribute("filterStatus", "ok");
//		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//	}
//
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//
//	@Test
//	public void md_a1_create_a_folder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "Amar");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//	@Test
//	public void md_a2_test_connection_for_SampleTravelData() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "test");
//
//		map.put("formData",
//				"{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelDataDerby\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"/home/helical/Performance/hi/db/SampleTravelData\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\",\"id\":\"1\",\"type\":\"dynamicDataSource\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//         .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful."));
//	}
//	
//	static String id = "";
//	@Test
//	public void md_a3_create_groovy_plan_jdbc_datasource() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//
//		map.put("formData",
//				"{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelDataDerby\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"/home/helical/Performance/hi/db/SampleTravelData\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/SampleTravelData\",\"id\":\"1\",\"type\":\"dynamicDataSource\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		
//		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
//		JSONObject response = jsonObject.getJSONObject("response");
//		Assert.assertTrue(response.containsKey("dataSourceId"));
//		id = response.getString("dataSourceId");
//	}
//	@Test
//	public void md_a4_to_create_metadata_expand_catlog_schema() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData","{\"id\":\""+id+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void md_a5_expand_table() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData","{\"id\":\""+id+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	@Test
//	public void md_a6_create_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+id+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3wbgx\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_sample\",\"location\":\"Amar\",\"metadataReload\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//
//		}
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//	
//	@Test
//	public void md_b1_test_connection_for_hieeDS() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "test");
//
//		map.put("formData",
//				"{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"hieeDS\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"/home/helical/Performance/hi/db/hiee\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/hiee\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//         .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The connection test is successful."));
//	}
//	
//	static String hiid = "";
//	@Test
//	public void md_b2_create_groovy_plan_jdbc_datasource() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "core");
//		map.put("serviceType", "dataSource");
//		map.put("service", "write");
//
//		map.put("formData",
//				"{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"hieeDS\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\"/home/helical/Performance/hi/db/hiee\",\"jdbcUrl\":\"jdbc:derby:/home/helical/Performance/hi/db/hiee\"}");
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		
//		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
//		JSONObject response = jsonObject.getJSONObject("response");
//		Assert.assertTrue(response.containsKey("dataSourceId"));
//		hiid = response.getString("dataSourceId");
//	}
//	@Test
//	public void md_b3_to_create_metadata_expand_catlog_schema() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData","{\"id\":\""+hiid+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void md_b4_expand_table() throws Exception {
//		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData","{\"id\":\""+hiid+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//	
//	@Test
//	public void md_b5_create_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+hiid+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3v16s\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"9645c648a1c0dbeec1287aaf1e996db3\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_hieeDS\",\"location\":\"Amar\",\"metadataReload\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//
//		}
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//	@Test
//	public void md_c1_merge_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+id+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"u1wgt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+hiid+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"m9vjl\",\"classifier\":\"db.workflow\",\"datasourceName\":\"hieeDS\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"f11bb8dbb44d200595a6b7ce2ef79d6a\",\"71ecafafb0670765849e49b7c165b047\",\"3e284d95730d70d64ba9c1726290d272\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"Metadata_AddConn\",\"location\":\"Amar\",\"metadataReload\":true}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//
//		}
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//	@Test
//	public void md_c2_edit_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+id+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"u1wgt\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"baf41c5a-779e-4290-ba26-a829272c1c58\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+hiid+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"m9vjl\",\"classifier\":\"db.workflow\",\"datasourceName\":\"hieeDS\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"d59579f2-43a5-402a-8676-548972f31605\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"fileName\":\"Metadata_AddConn\",\"location\":\"Amar\",\"metadataReload\":false,\"uuid\":\"Metadata_AddConn.metadata\",\"uniqueId\":true}");
//		
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		String message = responseObject.getString("message");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//
//		}
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//	@Test
//	public void md_c3_delete_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", "[\"Amar/Metadata_AddConn.metadata\"]");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//}
