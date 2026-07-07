package com.helicalinsight.dashboard;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class DesignerSaveAsTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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

	@Test
	public void dd_a1_create_a_folder() throws Exception {
		testUtility.createFolder("DesignerSaveAsTest");
	}
	
	@Test
	public void dd_a2_save_dashboard_desiginer() throws Exception {
		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null,\\\"_store\\\":{}}\",\"state\":{\"variables\":{},\"components\":[],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\"DesignerSaveAsTest\",\"fileName\":\"test\"}";
		String response = testUtility.createDesign(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		String name = jsonObject.getJSONObject("response").getString("uuid");
		Assert.assertEquals("test",name);
	}
	
	@Test
	public void dd_a3_save_as_dashboard_desiginer() throws Exception {
		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null,\\\"_store\\\":{}}\",\"state\":{\"variables\":{},\"components\":[],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[],\"layout\":[],\"designerSettings\":[],\"parameterDrawerStatus\":false}},\"dir\":\"DesignerSaveAsTest\",\"fileName\":\"TEST\"}";
		String response = testUtility.createDesign(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		String name = jsonObject.getJSONObject("response").getString("uuid");
		Assert.assertEquals("TEST_01",name);
	}
	
	
}
