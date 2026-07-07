package com.helicalinsight.recyclebin;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecycleBinResourceRestoreTest {
	
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
	@Qualifier("userDetailsService")
	private UserService userService;

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
	public void rec_a0_create_a_folder() throws Exception {
		testUtility.createFolder("RecycleBinResourceRestoreTest");
		testUtility.createFolder("TestFolder_1",List.of("RecycleBinResourceRestoreTest"));
		testUtility.createFolder("TestFolder_2",List.of("RecycleBinResourceRestoreTest"));
		testUtility.deleteResource("RecycleBinResourceRestoreTest");
	}
	
	@Test
	public void rec_a1_restore_folder() throws Exception {
		String mainFolderBinId = testUtility.getRecycleBinIdByResourceName("TestFolder_1");
		String formData = "{\"action\":\"restore\",\"recycleBinIds\":["+mainFolderBinId+"]}";
		String response = testUtility.recycleBinAction(formData);
		ObjectNode responseObj = JacksonUtility.fromObject(response).with("response");
		Assert.assertEquals("You can't restore this resource because its parent is in the recycle bin. To restore it, you need to restore the parent first.", responseObj.get("message").asText());
	}
	
	@Test
	public void rec_a2_restore_folders() throws Exception {
		String mainFolderBinId = testUtility.getRecycleBinIdByResourceName("TestFolder_1");
		String mainFolderBinId2 = testUtility.getRecycleBinIdByResourceName("TestFolder_2");
		String formData = "{\"action\":\"restore\",\"recycleBinIds\":["+mainFolderBinId+","+mainFolderBinId2+"]}";
		String response = testUtility.recycleBinAction(formData);
		ObjectNode responseObj = JacksonUtility.fromObject(response).with("response");
		Assert.assertEquals("You can't restore this resource because its parent is in the recycle bin. To restore it, you need to restore the parent first.", responseObj.get("message").asText());
	}
}
