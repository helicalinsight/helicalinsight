package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
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
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
public class PublicFolderTest {


    private MockMvc efwMock;

    @Autowired
    private WebApplicationContext context;

    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }

    @Autowired
    private IntegrationTestUtility testUtility;
    

    @Autowired
    FilterChainProxy filterChainProxy;


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    HIResourceServiceDB hiResourceServiceDB;


    @InjectMocks
    private ServletContext servletContext;


    @Before
    @Transactional
    public void setup() {
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus","ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();

    }

    @Test
    public void a1_createPublicFolder() throws Exception {
    	testUtility.createFolder("PublicFolderTest");
    	testUtility.makeResourcePublic("PublicFolderTest", "PublicFolderTest", true);
    	testUtility.createFolder("PrivateRootFolder");
    }
    
    @Test
    public void a2_createPrivateNestedFolder() throws Exception {
    	testUtility.createFolder("SubFolder", List.of("PublicFolderTest"));
    	testUtility.createFolder("SubFolder2", List.of("PublicFolderTest"));
    	testUtility.createFolder("PublicSubFolder", List.of("PublicFolderTest"));
    	testUtility.makeResourcePublic("PublicFolderTest/PublicSubFolder", "PublicSubFolder", true);
    }
    

	@Test
	public void a3_listFileBrowser() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.get("/getSolutionResourcesTest").param("resource", "test").param("recursive", "true")
				.param("extensions", "json").contentType(MediaType.APPLICATION_JSON);

		Map<String, String> map = new HashMap<>();
		map.put("username", "hiuser");
		map.put("password", "hiuser");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		JsonArray resources = GsonUtility.parseString(result.getResponse().getContentAsString(), JsonArray.class);
		
		Map<String,JsonObject> resourceMap = new HashMap<>();
		
		for(JsonElement element : resources ) {
			JsonObject resource = element.getAsJsonObject();
			resourceMap.put(GsonUtility.optString(resource, "path"), resource);
		}
		
		JsonObject parentResource = resourceMap.get("PublicFolderTest");
		assertEquals("PublicFolderTest", GsonUtility.optString(parentResource, "path"));
		JsonArray children =  GsonUtility.optJsonArray(parentResource, "children");
		assertEquals(3, children.size());
		Map<String,String> childPathSet = new HashMap<>();
		for(JsonElement child : children ) {
			JsonObject childJson = child.getAsJsonObject();
			childPathSet.put(GsonUtility.optString(childJson, "path"), GsonUtility.optString(childJson, "permissionLevel"));
		}
		
		assertEquals(3, childPathSet.size());
		assertTrue(childPathSet.containsKey("PublicFolderTest/SubFolder"));
		assertTrue(childPathSet.containsKey("PublicFolderTest/SubFolder2"));
		assertTrue(childPathSet.containsKey("PublicFolderTest/PublicSubFolder"));
		
		assertEquals("3", childPathSet.get("PublicFolderTest/SubFolder"));
		assertEquals("3", childPathSet.get("PublicFolderTest/SubFolder2"));
		assertEquals("3", childPathSet.get("PublicFolderTest/PublicSubFolder"));
	}

}