package com.helicalinsight.recyclebin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RecycleBinDeletePhase2Test {

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
	
	@Autowired
	private EFWDConnectionService connectionService;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi", "db", "SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:"
					+ String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi", "db", "SampleTravelData");
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
	public void rec_a1_createResources() throws Exception {
		
		testUtility.clearRecycleBin();
		
		testUtility.createFolder("RecycleBinNestedFolderDeleteTest");
		testUtility.createFolder("RecycleBinNestedFolderDeleteTest1", List.of("RecycleBinNestedFolderDeleteTest"));
		testUtility.createFolder("RecycleBinNestedFolderDeleteTest2", List.of("RecycleBinNestedFolderDeleteTest"));
		testUtility.createFolder("RecycleBinNestedFolderDeleteTest1_1", List.of("RecycleBinNestedFolderDeleteTest/RecycleBinNestedFolderDeleteTest1"));
		testUtility.createFolder("RecycleBinNestedFolderDeleteTest2_1", List.of("RecycleBinNestedFolderDeleteTest/RecycleBinNestedFolderDeleteTest2"));
		testUtility.deleteResource("RecycleBinNestedFolderDeleteTest");
	}
	
	@Test
	public void rec_a2_delete() throws Exception {
		JsonArray array =  testUtility.listRecycleBin();
		List<String> list = new ArrayList<>();
		for(JsonElement object : array ) {
			JsonObject json = object.getAsJsonObject();
			list.add(json.get("recycleBinId").getAsString());
		}
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":"+list+"}";
		String response = testUtility.deletePermanently(formData);
		JSONObject responseObj = JSONObject.fromObject(response).getJSONObject("response");
		JSONObject recyclebinObj = responseObj.getJSONObject("recycleBin");
		JSONArray completed = recyclebinObj.getJSONArray("completed");
		Assert.assertNotNull(completed);
		Assert.assertEquals(5, completed.size());
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", responseObj.getString("message"));
	}
	
	@Test
	public void rec_a3_delete_rev_order() throws Exception {
		
		rec_a1_createResources();
		
		JsonArray array=  testUtility.listRecycleBin();
		List<String> list = new ArrayList<>();
		for(JsonElement object : array ) {
			JsonObject json =  object.getAsJsonObject();
			list.add(json.get("recycleBinId").getAsString());
		}
		Collections.reverse(list);
		String formData = "{\"action\":\"delete\",\"recycleBinIds\":"+list+"}";
		String response = testUtility.deletePermanently(formData);
		JSONObject responseObj = JSONObject.fromObject(response).getJSONObject("response");
		JSONObject recyclebinObj = responseObj.getJSONObject("recycleBin");
		JSONArray completed = recyclebinObj.getJSONArray("completed");
		Assert.assertNotNull(completed);
		Assert.assertEquals(5, completed.size());
		Assert.assertEquals("The selected resource have been deleted and any related content(s).", responseObj.getString("message"));
	}
	
	
}
