package com.helicalinsight.filebrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataJoinsFetchTest {
	
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
	
	private static String jdbcUrl = "";
	private static String dbName = "";

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
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	private static String dataSourceGlobalId;
	private static Set<String> tableIds=new HashSet<>();

	@Test
	public void md_1_create_a_folder_to_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "MetadataTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}

	@Test
	public void md_list_ds_no_source_forge() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "content");
		map.put("serviceType", "static");
		map.put("service", "getContents");
		map.put("formData","{\"contentId\":\"Static/DataSourcesList\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		String res=result.getResponse().getContentAsString();
		Assert.assertTrue(!res.contains("forge"));
		JSONObject jsonObject = JSONObject.fromObject(res);
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}


	@Test
	public void md_2_create_data_source() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "write");
		map.put("formData",
				"{\"classifier\":\"global\",\"name\":\"SampleTravelData\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"hiadmin\",\"password\":\"hiadmin\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\""+dbName+"\",\"dataSourceProvider\":\"tomcat\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		dataSourceGlobalId = jsonObject.getJSONObject("response").getString("dataSourceId");
		int status = jsonObject.getInt("status");
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void md_3_expand_catalog_schema() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\""+dataSourceGlobalId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	
	@Test
	public void md_4_expand_database_tables() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData",
				"{\"id\":\""+dataSourceGlobalId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	
	@Test
	public void md_5_fetch_joins_before_metadata_save() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchJoins");
		map.put("formData",
				"{\"dataSource\":{\"id\":\""+dataSourceGlobalId+"\",\"type\":\"dynamicDataSource\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"metadata\":{\"metdataDir\":\"\",\"filename\":\"\",\"table\":[\"dimdate\",\"employee_details\",\"geo_cordinates\",\"meeting_details\",\"travel_details\"]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		jsonObject = jsonObject.getJSONObject("response");
		JSONArray joins=jsonObject.getJSONArray("joins");
		String leftId="",rightId="";
		for (int i = 0; i < joins.size(); i++) {
			JSONObject data = joins.getJSONObject(i);
			if(data.getJSONObject("left").containsKey("tableId"))
				leftId=data.getJSONObject("left").getString("tableId");
			if(data.getJSONObject("right").containsKey("tableId"))
				rightId=data.getJSONObject("right").getString("tableId");
			tableIds.add(leftId);
			tableIds.add(rightId);
			Assert.assertTrue(data.getJSONObject("left").containsKey("dbId"));
			Assert.assertTrue(data.getJSONObject("right").containsKey("dbId"));
			Assert.assertTrue(!leftId.equals("")&&!rightId.equals(""));
		}
	}
	
	@Test
	public void md_6_fetch_joins_after_metadata_save() throws Exception {
		List<String> list=new ArrayList<>(tableIds);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+dataSourceGlobalId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"7wset\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"dbId\":\"7wset\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\""+list.get(0)+"\",\""+list.get(1)+"\",\""+list.get(2)+"\",\""+list.get(3)+"\",\""+list.get(4)+"\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"testMetaDataSave\",\"location\":\"MetadataTest\",\"metadataReload\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		jsonObject = jsonObject.getJSONObject("response").getJSONObject("metadata");	
		JSONArray joins=jsonObject.getJSONArray("joins");
		String leftId="",rightId="";
		for (int i = 0; i < joins.size(); i++) {
			JSONObject data = joins.getJSONObject(i);
			if(data.getJSONObject("left").containsKey("tableId"))
				leftId=data.getJSONObject("left").getString("tableId");
			if(data.getJSONObject("right").containsKey("tableId"))
				rightId=data.getJSONObject("left").getString("tableId");
			Assert.assertTrue(!leftId.equals("")&&!rightId.equals(""));
		}
	}
	
}
