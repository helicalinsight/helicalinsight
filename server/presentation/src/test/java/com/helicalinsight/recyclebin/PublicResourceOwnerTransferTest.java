package com.helicalinsight.recyclebin;

import java.util.HashMap;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class PublicResourceOwnerTransferTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

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
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	
	@Test
	public void rec_a1_createPublicFolder() throws Exception {
		testUtilitiy.createFolder("PublicFolderChangeOwner");
		testUtilitiy.makeResourcePublic("PublicFolderChangeOwner", "PublicFolderChangeOwner",true);
		HIResource resource =  serviceDb.getResourceByUrl("PublicFolderChangeOwner");
		Assert.assertNull(resource.getCreatedBy());
	}
	
	@Test
	public void rec_a2_change_owner() throws Exception {
		HIResource resource =  serviceDb.getResourceByUrl("PublicFolderChangeOwner");
		String formData ="{\"type\":\"Folders\",\"resources\":["+resource.getResourceId()+"],\"newOwnerId\":\"1\"}";
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
	}
	
	@Test
	public void rec_a3_verify() throws Exception {
		HIResource resource = serviceDb.getResourceByUrl("PublicFolderChangeOwner");
		Assert.assertNotNull(resource.getCreatedBy());
		Assert.assertEquals(Integer.valueOf(1), resource.getCreatedBy());
	}
	
	
}
