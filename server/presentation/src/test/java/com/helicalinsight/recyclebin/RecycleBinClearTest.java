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
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RecycleBinClearTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;

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
	public void rec_a1_create_a_folder() throws Exception {
		testUtilitiy.clearRecycleBin();
		testUtilitiy.createFolder("RecycleBinClearTest");
		testUtilitiy.createFolder("Level1Folder",List.of("RecycleBinClearTest"));
	}

	@Test
	public void rec_a2_deleteFolder() throws Exception {
		testUtilitiy.deleteResource("RecycleBinClearTest");
	}
	
	@Test
	public void rec_a3_restore_folder() throws Exception {
		String mainFolderBinId = testUtilitiy.getRecycleBinIdByResourceName("Level1Folder");
		String formData = "{\"action\":\"restore\",\"recycleBinIds\":["+mainFolderBinId+"]}";
		String response = testUtilitiy.recycleBinAction(formData);
		ObjectNode responseObj = JacksonUtility.fromObject(response).with("response");
		Assert.assertEquals("You can't restore this resource because its parent is in the recycle bin. To restore it, you need to restore the parent first.", responseObj.get("message").asText());
	}
	
	@Test
	public void rec_a4_restore_main_folder() throws Exception {
		String mainFolderBinId = testUtilitiy.getRecycleBinIdByResourceName("RecycleBinClearTest");
		String formData = "{\"action\":\"restore\",\"recycleBinIds\":["+mainFolderBinId+"]}";
		String response = testUtilitiy.recycleBinAction(formData);
		ObjectNode responseObj = JacksonUtility.fromObject(response).with("response");
		Assert.assertEquals("Resource(s) restored successfully.", responseObj.get("message").asText());
	}
	
//	@Test / NOTE : With the fix of BUG 7004 - this testcase becomes invalid.
	public void rec_a4_clear() throws Exception {
		String response = testUtilitiy.clearRecycleBinNoForce();
		JSONObject responseObj = JSONObject.fromObject(response).getJSONObject("response");
		String message = responseObj.getString("message");
		JSONObject recycleBin = responseObj.getJSONObject("recycleBin");
		Assert.assertEquals("The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually.",message);
		Assert.assertEquals(1, recycleBin.getJSONArray("incomplete").size());
	}
}
