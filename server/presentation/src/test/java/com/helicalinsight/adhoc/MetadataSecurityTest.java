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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.JacksonUtility;
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
public class MetadataSecurityTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	IntegrationTestUtility testUtility;

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
	public void md_a1_create_a_folder_to_save_metadata_for_security() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("action", "newFolder");
		map.put("folderName", "MetadataSecurityTest");
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
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");

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
		map.put("formData",
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}

	@Test
	public void md_a4_validate_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "evaluateSecurity");

		map.put("formData",
				"{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiuser'\"}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Condition Test Success"));
	}

	static String expressionId = "";

	@Test
	public void md_a5_create_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData",
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_hiuser\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"8a28627d07d04ef096d9935f12e0c7e9_askmv\"],\"condition\":\"${user}.name eq 'hiuser'\",\"action\":\"add\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressionId"));
		expressionId = response.getString("expressionId");
	}
	
	@Test
	public void md_a6_create_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""
						+ expressionId
						+ "\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"askmv\",\"dbId\":\"askmv\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"7884a765-12ee-45d3-a1a8-a4b37bb9d093\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_2\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":true}");
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
	public void md_a7_get_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");

		map.put("formData","{\"metadataFileName\":\"Metadata_2.metadata\",\"location\":\"MetadataSecurityTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(!array.isEmpty());

	}

	@Test
	public void md_a8_edit_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData",
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_hiuser\",\"expressionType\":\"global\",\"accessType\":\"grant\",\"executionType\":\"conditionIf\",\"on\":[\"travel_details\"],\"condition\":\"${user}.name eq 'hiuser'\",\"filter\":\"TableName.ColumnName = Filter Condition\",\"action\":\"edit\",\"expressionId\":\""
						+ expressionId + "\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Expression edited successfully "));
	}

	@Test
	public void md_a9_edit_secrity_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"}],\"access\":{\"expression\":[{\"expressionId\":\""
						+ expressionId
						+ "\",\"action\":\"edit\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"askmv\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"7884a765-12ee-45d3-a1a8-a4b37bb9d093\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_2\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":false,\"uuid\":\"Metadata_2.metadata\"}");
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
	public void md_b1_delete_secrity_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData",
				"{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"}],\"access\":{\"expression\":[{\"expressionId\":\""
						+ expressionId
						+ "\",\"action\":\"delete\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"askmv\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"7884a765-12ee-45d3-a1a8-a4b37bb9d093\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_2\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":false,\"uuid\":\"Metadata_2.metadata\"}");
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
	public void md_b2_get_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");

		map.put("formData","{\"metadataFileName\":\"Metadata_2.metadata\",\"location\":\"MetadataSecurityTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(array.isEmpty());

	}
	@Test
	public void md_b3_validate_security_groovy() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "evaluateSecurity");

		map.put("formData",
				"{\"executionType\":\"groovy\",\"data\":{\"condition\":\"import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n                def evalCondition() {\\n                        String userName = GroovyUsersSession.getValue('${user}.name');\\n                        if (userName.equalsIgnoreCase(\\\"'hiuser'\\\")) {\\n                            return true\\n                        } else {\\n                            return false\\n                        }        \\n                }\"}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Condition Test Success"));
	}
	
	@Test
	public void md_b4_save_security_for_groovy() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData","{\"uuid\":true,\"expression\":[{\"expressionName\":\"groovy\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"groovy\",\"on\":[\"8a28627d07d04ef096d9935f12e0c7e9_0fene\"],\"condition\":\"import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n                def evalCondition() {\\n                        String userName = GroovyUsersSession.getValue('${user}.name');\\n                        if (userName.equalsIgnoreCase(\\\"'hiuser'\\\")) {\\n                            return true\\n                        } else {\\n                            return false\\n                        }        \\n                }\",\"action\":\"add\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressionId"));
		expressionId = response.getString("expressionId");
	}
	@Test
	public void md_b5_groovy_save_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"0fene\",\"dbId\":\"0fene\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"d3770a8a-19f2-4aa4-a9f4-c5a40230f652\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_4\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":true}");
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
	public void md_b6_get_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");

		map.put("formData","{\"metadataFileName\":\"Metadata_4.metadata\",\"location\":\"MetadataSecurityTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(!array.isEmpty());

	}
	@Test
	public void md_b7_edit_security_for_groovy() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData",
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"groovy\",\"expressionType\":\"global\",\"accessType\":\"grant\",\"executionType\":\"groovy\",\"on\":[\"travel_details\"],\"condition\":\"import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n                def evalCondition() {\\n                        String userName = GroovyUsersSession.getValue('${user}.name');\\n                        if (userName.equalsIgnoreCase(\\\"'hiuser'\\\")) {\\n                            return true\\n                        } else {\\n                            return false\\n                        }        \\n                }\",\"filter\":\"\\n\\t\\t        def evalFilter() {\\n\\t\\t\\t        return 'TableName.ColumnName = Filter Condition';\\n\\t\\t        }\\n\\t\\t\",\"action\":\"edit\",\"expressionId\":\""+expressionId+"\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Expression edited successfully "));
	}
	
	@Test
	public void md_b8_groovy_edit_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"}],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"edit\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"0fene\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"d3770a8a-19f2-4aa4-a9f4-c5a40230f652\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_4\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":false,\"uuid\":\"Metadata_4.metadata\",\"uniqueId\":true}");
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
	public void md_b9_groovy_delete_security_metadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"}],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"delete\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"0fene\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"d3770a8a-19f2-4aa4-a9f4-c5a40230f652\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"Metadata_4\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":false,\"uuid\":\"Metadata_4.metadata\",\"uniqueId\":true}");
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
	public void md_c1_get_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");

		map.put("formData","{\"metadataFileName\":\"Metadata_4.metadata\",\"location\":\"MetadataSecurityTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(array.isEmpty());

	}
	
	@Test
	public void md_c2_validate_security_for_role() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "evaluateSecurity");

		map.put("formData","{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"check(\\\"${role}.name\\\" , \\\"ROLE_USER\\\")\"}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Condition Test Success"));

	}
	@Test
	public void md_c3_add_security_for_role() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData","{\"uuid\":true,\"expression\":[{\"expressionName\":\"Role\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"8a28627d07d04ef096d9935f12e0c7e9_pffii\"],\"condition\":\"check(\\\"${role}.name\\\" , \\\"ROLE_USER\\\")\",\"action\":\"add\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressionId"));
		expressionId = response.getString("expressionId");
	}
	@Test
	public void md_c4_save_metadata_for_role() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "update");
		map.put("formData","{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"},{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"}],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"pffii\",\"dbId\":\"pffii\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\",\"connectionDatabaseId\":\"4dcc2f70-9c46-4056-82fc-531a7c2ca072\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true,\"fileName\":\"Metadata_1\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":true}");
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
	public void md_c5_get_security_for_role() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");

		map.put("formData","{\"metadataFileName\":\"Metadata_1.metadata\",\"location\":\"MetadataSecurityTest\"}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(!array.isEmpty());

	}
	
	
	
	@Test
	public void md_c6_add_security_on_multiple_tables() throws Exception {
		String accessFormData = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_all_tables\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_u2tfb\",\"4e1fd245f4d13b77be423a43f01d80b2_u2tfb\",\"be534112989b616b194bc59c2fb25a42_u2tfb\",\"9645c648a1c0dbeec1287aaf1e996db3_u2tfb\",\"8a28627d07d04ef096d9935f12e0c7e9_u2tfb\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
		String accessResponse = testUtility.saveSecurity(accessFormData);
		ObjectNode responseObj = JacksonUtility.fromObject(accessResponse);
    	expressionId = responseObj.with("response").get("expressionId").asText();
		
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"u2tfb\",\"dbId\":\"u2tfb\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"fileName\":\"HiddenAllTables\",\"location\":\"MetadataSecurityTest\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
		
		String getMetadata = "{\"location\":\"MetadataSecurityTest\",\"uniqueId\":true,\"metadataFileName\":\"HiddenAllTables.metadata\",\"provideJoins\":true}";
		String getResponse = testUtility.getMetadata(getMetadata);
		ObjectNode mdResponse = JacksonUtility.fromObject(getResponse);
		ObjectNode tables =  mdResponse.with("response").with("metadata").with("tables");
		Assert.assertTrue(tables.isEmpty());
		
		String securityResp = testUtility.getSecurity("{\"metadataFileName\":\"HiddenAllTables.metadata\",\"location\":\"MetadataSecurityTest\"}");
		ObjectNode secNode = JacksonUtility.fromObject(securityResp);
		ArrayNode arrNode = secNode.with("response").withArray("expressions");
		Assert.assertFalse(arrNode.isEmpty());
		ObjectNode firstExpression = (ObjectNode) arrNode.get(0);
		ArrayNode onArr = firstExpression.withArray("on");
		Assert.assertFalse(onArr.isEmpty());
		Assert.assertEquals(5, onArr.size());
	}
	
	
	
	@Test
	public void md_c7_delete_folder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/fileSystemOperations");
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", "[\"MetadataSecurityTest\"]");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}
}
