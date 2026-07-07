package com.helicalinsight.recyclebin;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.exception.OwnershipTransferException;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
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

public class MetadataOwnerChangeTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void rec_a1_create_a_folder() throws Exception {
		testUtility.clearRecycleBin();
		testUtility.createFolder("MetadataChangeOwnerTest");
	}

	static String userId = "";
	
	@Test
	public void rec_a2_0_create_user() throws Exception {
		String userFormData = "{\"id\":\"\",\"email\":\"userWithRoleUserAndAdmin67672@helical.com\",\"name\":\"userWithRoleUserAndAdmin67672\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		userId = JSONObject.fromObject(testUtility.createUser(userFormData)).getJSONObject("response").getString("id");
		String attachRole = "{\"id\":\""+userId+"\",\"name\":\"userWithRoleUserAndAdmin67672\",\"email\":\"userWithRoleUserAndAdmin67672@helical.com\",\"enabled\":true,\"roleIds\":[1],\"password\":\"\"}";
		testUtility.attachRole(attachRole, userId);
	}
	
	@Test
	public void rec_a2_1_create_cache() throws Exception {
		String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(defaultCatalog);
		String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(defaultTable);
	}
	
	@Test
	public void rec_a3_createMetadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataChangeOwnerTest\",\"fileName\":\"Metadata_1\",\"uniqueId\":true}";
		testUtility.createMetadata(formData);
	}
	
	@Test
	public void rec_a4_deleteMetadata() throws Exception {
		String url = "MetadataChangeOwnerTest/Metadata_1.metadata";
		testUtility.deleteResource(url);
	}
	
	@Test
	public void rec_a5_change_owner() throws Exception {
		
		HIResource resource =  serviceDb.getResourceByUrl("MetadataChangeOwnerTest/Metadata_1.metadata",false);
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\""+userId+"\"}";
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "owner");
		map.put("service", "change");
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andReturn();
		JSONObject response = JSONObject.fromObject(result.getResponse().getContentAsString());
		String message = JSONObject.fromObject(response).getJSONObject("response").getString("message");
		Assert.assertEquals("Successfully changed ownership of resource(s)", message);
		
		// restore 
		JsonArray data = testUtility.listRecycleBin();
		JsonObject item =  data.get(0).getAsJsonObject();
		String binId = item.get("recycleBinId").getAsString();
	    String formData2 = "{\"action\":\"restore\",\"recycleBinIds\":[" + binId + "]}";
	    testUtility.restore(formData2);
		
	}
	
	
	@Test
	public void rec_a6_update_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":false,\"location\":\"MetadataChangeOwnerTest\",\"fileName\":\"Metadata_1\",\"uniqueId\":true,\"uuid\":\"Metadata_1.metadata\"}";
		String response =  testUtility.createMetadata(formData);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void rec_a7_cleanUp() throws Exception {
		testUtility.clearRecycleBin();
	}
}
