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
import org.springframework.test.web.servlet.ResultActions;
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
public class MultipleTest {
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
		map.put("folderName", "MultipleTest");
		map.put("sourceArray", sourceList.toString());
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
						.jsonPath("$.response.message").value("A new folder is created successfully"));
	}
	
	
	@Test
	public void md_a2_to_create_metadata_expand_catlog_schema() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a3_expand_table() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "metadataWorkflow");
		map.put("formData","{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	@Test
	public void md_a4_fetch_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchColumns");
		map.put("formData","{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"cv6ve\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"hfqh-gvnd-azso-rn5e-6l/3q20-dnce-8mgn-kjcj-fo/r9fw-v6c4-2b1k-l35d-an\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(((MvcResult) result).getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		 boolean containsKey = responseObject.getJSONObject("metadata").getJSONObject("table").containsKey("dimdate");
		Assert.assertTrue(containsKey);
		
	}


	@Test
	public void md_a5_save_metadata_with_duplicate_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[{\"originalId\":\"9c968ccd-4679-4b6c-b22b-61fc8bd74f2b\",\"originalName\":\"created_date_1\",\"connId\":\"705rb\",\"alias\":\"created_date_1\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"},{\"originalId\":\"6dba895f-269a-43a8-ad34-7c5f5bdbf032\",\"originalName\":\"created_time_1\",\"connId\":\"705rb\",\"alias\":\"created_time_1\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"},{\"originalId\":\"278eb0b8-d626-46f7-b100-b451f83e0eba\",\"originalName\":\"date_key_1\",\"connId\":\"705rb\",\"alias\":\"date_key_1\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"}]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"705rb\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"816c49b7-dbe5-426a-b985-9f7577b90ef3\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_duplicateColumns\",\"location\":\"MultipleTest\",\"metadataReload\":true}");
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
	public void md_a6_fetch_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchColumns");
		map.put("formData","{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"tj3au\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"aoll-vf7y-oene-2t9k-qy/3yrt-5ii3-347w-wsc7-8l/qdgz-ii9x-z58n-cjm3-c6\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"geo_cordinates\"},\"refresh\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(((MvcResult) result).getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		 boolean containsKey = responseObject.getJSONObject("metadata").getJSONObject("table").containsKey("geo_cordinates");
		Assert.assertTrue(containsKey);
	}

	@Test
	public void md_a7_save_metadata_with_duplicate_table() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"geo_cordinates_1\",\"columns\":[{\"alias\":\"location_id\",\"connId\":\"qaxdz\",\"originalId\":\"aa21046e-1b90-4c5f-8d09-f19a4bf78841\",\"originalName\":\"location_id\"},{\"alias\":\"location\",\"connId\":\"qaxdz\",\"originalId\":\"6175685c-7d20-4945-b22d-52565a5524d8\",\"originalName\":\"location\"},{\"alias\":\"latitude\",\"connId\":\"qaxdz\",\"originalId\":\"0ddb568c-4108-4d45-8d0f-9c75e0175a9e\",\"originalName\":\"latitude\"},{\"alias\":\"longitude\",\"connId\":\"qaxdz\",\"originalId\":\"b59bb963-fc07-4119-a001-26838e6aa977\",\"originalName\":\"longitude\"}],\"connId\":\"qaxdz\",\"name\":\"geo_cordinates_1\",\"originalId\":\"be534112989b616b194bc59c2fb25a42\",\"originalName\":\"geo_cordinates\"}],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"qaxdz\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"d8afb8cb-fd6a-4e30-9d08-fa776730699a\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_duplicateTable\",\"location\":\"MultipleTest\",\"metadataReload\":true}");
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
	public void md_a8_fetch_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchColumns");
		map.put("formData","{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"korck\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"szhq-1vbd-2kl5-bv7m-0j/6zay-t59h-z1lx-tuhc-69/qad3-00yb-2563-qopx-q1\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(((MvcResult) result).getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		 boolean containsKey = responseObject.getJSONObject("metadata").getJSONObject("table").containsKey("dimdate");
		Assert.assertTrue(containsKey);
	}
	@Test
	public void md_a9_fetch_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "fetchColumns");
		map.put("formData","{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"korck\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"szhq-1vbd-2kl5-bv7m-0j/6zay-t59h-z1lx-tuhc-69/qad3-00yb-2563-qopx-q1\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"employee_details\"},\"refresh\":true}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(((MvcResult) result).getResponse().getContentAsString());
		JSONObject responseObject = jsonObject.getJSONObject("response");
		 boolean containsKey = responseObject.getJSONObject("metadata").getJSONObject("table").containsKey("employee_details");
		Assert.assertTrue(containsKey);
	}
//	@Test
	public void md_b1_save_metadata_with_add_joins_with_duplicate_table_and_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"s7u4s\",\"originalId\":\"a7dec882-3bd1-4224-b51b-09f1ce2fb474\",\"originalName\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"s7u4s\",\"originalId\":\"6b4c07cc-97f1-4d06-9cc8-f7ad47a81c72\",\"originalName\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"s7u4s\",\"originalId\":\"cbd89eec-f110-495f-b414-cbe4f37387e8\",\"originalName\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"s7u4s\",\"originalId\":\"e50b878f-fadd-496d-948d-c867ade312af\",\"originalName\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"s7u4s\",\"originalId\":\"509a7855-bc4f-465c-950d-8045394652bc\",\"originalName\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"s7u4s\",\"originalId\":\"e4907d15-30af-4a8c-acdb-069610770f1a\",\"originalName\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"s7u4s\",\"originalId\":\"a289c335-8247-4464-9c9a-9a8b34d9a189\",\"originalName\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"s7u4s\",\"originalId\":\"ebb1e849-033c-4047-b5ec-7d4239427178\",\"originalName\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"s7u4s\",\"originalId\":\"135bdbb7-95a8-4247-a28d-2498c49a3635\",\"originalName\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"s7u4s\",\"originalId\":\"c8b55ccf-5789-4c1d-953b-3900f47679a4\",\"originalName\":\"rating\"}],\"connId\":\"s7u4s\",\"name\":\"dimdate_1\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"originalName\":\"dimdate\"},{\"alias\":\"employee_details_1\",\"columns\":[{\"alias\":\"employee_id\",\"connId\":\"s7u4s\",\"originalId\":\"db88a1cf-c609-41c1-9a1f-404bf74bb448\",\"originalName\":\"employee_id\"},{\"alias\":\"employee_name\",\"connId\":\"s7u4s\",\"originalId\":\"222ec8e1-8c49-4413-8efb-912508fe578f\",\"originalName\":\"employee_name\"},{\"alias\":\"age\",\"connId\":\"s7u4s\",\"originalId\":\"68d069e3-5ece-440c-a474-16df67bf7f22\",\"originalName\":\"age\"},{\"alias\":\"address\",\"connId\":\"s7u4s\",\"originalId\":\"0f90a7aa-cc8e-4e11-bb8c-fe845c25a0cd\",\"originalName\":\"address\"},{\"alias\":\"address_1\",\"connId\":\"s7u4s\",\"originalId\":\"0f90a7aa-cc8e-4e11-bb8c-fe845c25a0cd\",\"originalName\":\"address\",\"name\":\"address_1\"}],\"connId\":\"s7u4s\",\"name\":\"employee_details_1\",\"originalId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"originalName\":\"employee_details\"}],\"column\":[{\"originalId\":\"ebb1e849-033c-4047-b5ec-7d4239427178\",\"originalName\":\"created_date\",\"connId\":\"s7u4s\",\"alias\":\"created_date_1\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"}]},\"joins\":[{\"type\":\"inner\",\"operator\":\"=\",\"left\":{\"table\":\"dimdate\",\"column\":\"created_date_1\",\"alias\":\"dimdate.created_date_1\",\"dbId\":\"s7u4s\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"},\"right\":{\"table\":\"employee_details\",\"column\":\"employee_id\",\"alias\":\"employee_details.employee_id\",\"dbId\":\"s7u4s\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\"},\"key\":\"zi0q-x2cl-u67a-151w-fh\",\"uuid\":\"zi0q-x2cl-u67a-151w-fh\",\"action\":\"add\",\"index\":1},{\"type\":\"inner\",\"operator\":\"=\",\"left\":{\"table\":\"dimdate_1\",\"column\":\"created_date\",\"alias\":\"dimdate_1.created_date\",\"dbId\":\"s7u4s\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\"},\"right\":{\"table\":\"employee_details_1\",\"column\":\"address_1\",\"alias\":\"employee_details_1.address_1\",\"dbId\":\"s7u4s\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\"},\"key\":\"6z38-f116-gqug-ywd9-vz\",\"uuid\":\"6z38-f116-gqug-ywd9-vz\",\"action\":\"add\",\"index\":2}],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"s7u4s\",\"dbId\":\"s7u4s\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_999\",\"location\":\"MultipleTest\",\"metadataReload\":true}");
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
		boolean table1 = responseObject.getJSONObject("metadata").getJSONObject("tables").containsKey("dimdate_1");
		boolean table2 = responseObject.getJSONObject("metadata").getJSONObject("tables").containsKey("employee_details_1");
		Assert.assertTrue(table1);
		Assert.assertTrue(table2);
	}


	@Test
	public void md_b2_save_metadata_with_alias_name_table_and_column() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"3kmjn\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"ccdb2e93-72dc-4e1a-a03b-21ee3c88892f\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[{\"id\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"alias\":\"dimdate_dim\",\"connId\":\"3kmjn\"},{\"id\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"alias\":\"employeinfo\",\"connId\":\"3kmjn\"},{\"id\":\"be534112989b616b194bc59c2fb25a42\",\"alias\":\"geo_co-rdinates\",\"connId\":\"3kmjn\"}],\"columns\":[{\"alias\":\"date\",\"columnId\":\"648ea291-83ef-4e2b-86a4-f8e459a9f493\",\"connId\":\"3kmjn\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"aliasChanged\":true},{\"alias\":\"dim_idid\",\"columnId\":\"eeda9cc8-8cdb-4cca-86e1-a417288e03aa\",\"connId\":\"3kmjn\",\"tableId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"aliasChanged\":true},{\"alias\":\"location\",\"columnId\":\"bde6f796-4eda-48b0-98ba-639e840e3f4a\",\"connId\":\"3kmjn\",\"tableId\":\"4e1fd245f4d13b77be423a43f01d80b2\",\"aliasChanged\":true},{\"alias\":\"address\",\"columnId\":\"68a6202a-b2ed-4c26-9b4f-5d6ba2ddef08\",\"connId\":\"3kmjn\",\"tableId\":\"be534112989b616b194bc59c2fb25a42\",\"aliasChanged\":true}]},\"fileName\":\"Metadata_alias\",\"location\":\"MultipleTest\",\"metadataReload\":true}");
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
		String str = responseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate").getString("alias");
		Assert.assertTrue(str.equalsIgnoreCase("dimdate_dim"));
		str = responseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("employee_details").getString("alias");
		Assert.assertTrue(str.equalsIgnoreCase("employeinfo"));
		str = responseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("geo_cordinates").getString("alias");
		Assert.assertTrue(str.equalsIgnoreCase("geo_co-rdinates"));
	    
	}
	@Test
	public void md_b3_delete_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"MultipleTest\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}

	   
}
