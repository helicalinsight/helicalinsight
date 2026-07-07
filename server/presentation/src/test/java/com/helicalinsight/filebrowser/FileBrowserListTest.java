package com.helicalinsight.filebrowser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileBrowserListTest {

	
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
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private IntegrationTestUtility testUtility;	
	

	@Test
	public void fb1_create_folder_structure() throws Exception {
		
		testUtility.createFolder("FileBrowserListApiTest");
		testUtility.createFolder("FileBrowserListApiTestPublicFolder");
		testUtility.makeResourcePublic("FileBrowserListApiTestPublicFolder", "FileBrowserListApiTestPublicFolder", true);
		testUtility.createFolder("Child1", List.of("FileBrowserListApiTest"));
		testUtility.createFolder("Child2", List.of("FileBrowserListApiTest"));
		testUtility.createFolder("grandchild1", List.of("FileBrowserListApiTest/Child1"));
		testUtility.createFolder("grandchild2", List.of("FileBrowserListApiTest/Child2"));
	}
	
	@Test
	public void fb2_list_folders() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.get("/getSolutionResourcesTest")
		        .param("resource", "test")
		        .param("recursive", "true")
		        .param("extensions", "json")
		        .contentType(MediaType.APPLICATION_JSON);
		
		Map<String, String> map = new HashMap<>();
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		JsonArray resources = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonArray.class);
		for(JsonElement resourceElement: resources) {
			JsonObject resource = resourceElement.getAsJsonObject();
			String path = GsonUtility.optString(resource, "path");
			if ( path.equalsIgnoreCase("FileBrowserListApiTest")) {
				JsonArray children = GsonUtility.optJsonArray(resource, "children");
				Assert.assertEquals(2, children.size());
			}
		}
	}
}
