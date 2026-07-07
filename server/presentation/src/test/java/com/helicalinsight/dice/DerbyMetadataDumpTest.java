//package com.helicalinsight.dice;
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
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.cache.filter.CacheFilter;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.spark.SparkExecutionCommands;
//import com.helicalinsight.spark.SparkServiceComponent;
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
//
//public class DerbyMetadataDumpTest {
//	
//	/**
//	 * 
//	 * 
//	 * Works only if
//	 * 			Dice configured
//	 * 			SampleTravelData - derby network exists.
//	 */
//
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
//	@Autowired
//	CacheFilter cacheFilter;
//	
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//
//	@Before
//	@Transactional
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy,cacheFilter)
//				.build();
//		ServletContext servletContext = context.getServletContext();
//		servletContext.setAttribute("filterStatus", "ok");
//		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter,cacheFilter).build();
//	}
//
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//	
//	private static String derbyNetworkId = "";
//	
//	 @Test
//	    public void dice_a0_create_derby_network_ds() throws Exception {
//
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "core");
//			map.put("serviceType", "dataSource");
//			map.put("service", "write");
//			String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.ClientDriver\",\"name\":\"SampleTravelDataNetwork\",\"userName\":\"root\",\"password\":\"root\",\"database\":\"SampleTravelData\",\"jdbcUrl\":\"jdbc:derby://localhost:1527/SampleTravelData\"}";
//			map.put("formData", formData);
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//					.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//					.jsonPath("$.response.message").value("A new Tomcat data source is created successfully."))
//					.andReturn();
//			JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//			JSONObject responseObject = jsonObject.getJSONObject("response");
//			JSONObject data = responseObject.getJSONObject("data");
//			derbyNetworkId = data.getString("id");
//		}
//
//	@Test
//	public void dice_a1_expand_catalog_schema() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData",
//				"{\"id\":\""+derbyNetworkId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void dice_a2_expand_table() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "metadataWorkflow");
//		map.put("formData",
//				"{\"id\":\""+derbyNetworkId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
//	}
//
//	@Test
//	public void dice_a3_create_a_folder_to_save_metadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "DerbyMetadataDumpTest");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//
//	@Test
//	public void dice_a4_createMetadata() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "update");
//		map.put("formData",
//				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDNetwork\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\""+derbyNetworkId+"\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"DerbyMetadataDumpTest\",\"uniqueId\":true}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		System.out.println(responseObject);
//		String message = responseObject.getString("message");
//		if(responseObject.has("data"))
//			Assert.assertNotNull(responseObject.getJSONArray("data"));
//		Assert.assertEquals(1, status);
//		Assert.assertEquals("Successfully saved metadata file", message);
//	}
//
//	@Test
//	public void dice_a5_startDice() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "monitor");
//		map.put("serviceType", "system");
//		map.put("service", "management");
//		map.put("formData", "{\"command\":\"START_COMPUTATION\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Computation Started successfully"));
//	}
//
//	@Test
//	public void dice_a7_dumpMetadata() throws Exception {
//			MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//			Map<String, String> map = new HashMap<>();
//			map.put("type", "adhoc");
//			map.put("serviceType", "metadata");
//			map.put("service", "dumpCube");
//			map.put("formData",
//					"{\"location\":\"DerbyMetadataDumpTest\",\"metadataFileName\":\"SaveSelectAll.metadata\",\"dumpType\":\"sample\"}");
//			map.put("requestId","sk37fg");
//			RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//			MvcResult result = this.efwMock.perform(builder).andReturn();
//			JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//			int status = jsonObject.getInt("status");
//			Assert.assertEquals(1, status);
//			if (jsonObject.has("message"))
//				Assert.assertTrue("Dump Operation Reverted Back".equals(jsonObject.getString("message"))
//						|| "Cube Dump Successful".equals(jsonObject.getString("message")));
//	}
//	
//	@Test
//	public void dice_a6_dump_save_abort() throws Exception{
//		new LocalThread().start();
//		Thread.sleep(1000);
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "adhoc");
//		map.put("serviceType", "metadata");
//		map.put("service", "cancelDump");
//		map.put("requestId","sk37fg");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = this.efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		Assert.assertEquals(1, status);
//		if(jsonObject.has("message"))
//			Assert.assertEquals("Dump Operation cancelled successfully", jsonObject.getString("message"));
//	}
//
//	@Test
//	public void dice_a8_diceStop() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "monitor");
//		map.put("serviceType", "system");
//		map.put("service", "management");
//		map.put("formData", "{\"command\":\"STOP_COMPUTATION\"}");
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
//				MockMvcResultMatchers.jsonPath("$.response.message").value("Computation Stopped successfully"));
//	}
//
//	// @Test
//	public void dice_b4_deleteFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"DerbyMetadataDumpTest\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//
//	public class LocalThread extends Thread{
//		@Override
//		public void run() {
//			try {
//				dice_a7_dumpMetadata();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}
