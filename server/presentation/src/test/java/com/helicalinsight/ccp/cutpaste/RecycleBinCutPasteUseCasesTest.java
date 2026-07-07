package com.helicalinsight.ccp.cutpaste;

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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecycleBinCutPasteUseCasesTest {
	
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
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private HIResourceServiceDB serviceDb;
	
	@Autowired
	private CCPTestUtility ccpTestUtility;
	
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
	
	private static final String Folder1="RecycleBinCutPasteUseCasesTest1";
	private static final String Folder2="RecycleBinCutPasteUseCasesTest2";
	private static String url1=Folder2+"/"+Folder1,url2;
	
	@Test
	public void cutpaste_rb_a1_init() throws Exception {
		testUtility.createFolder(Folder1);
		testUtility.createFolder(Folder2);
		testUtility.createFolder(Folder1, List.of(Folder2));
		testUtility.deleteResource(Folder2+"/"+Folder1);
	}
	
	@Test
	public void cutpaste_rb_a2_doCutPaste() throws Exception {
		url2=doCutPaste();
		Assert.assertNotEquals(url1, url2);
		HIResource resourceInRb=serviceDb.getResourceByUrl(Folder1, false);
		Assert.assertNull(resourceInRb);
	}
	
	@Test
	public void cutpaste_rb_a3_clearRB() throws Exception {
		ccpTestUtility.clearDB(Folder2);
	}
	
	private String doCutPaste() throws Exception{
		String formData="{\"sourceUrl\":\""+Folder1+"\",\"destinationUrl\":\""+Folder2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		JSONObject jsonObject=JSONObject.fromObject(ccpTestUtility.ccpOperation("cut",formData,ccpTestUtility.getSuccessMessage(),1));
		return jsonObject.getJSONObject("response").getJSONObject("data").getString("path");
	}
}
