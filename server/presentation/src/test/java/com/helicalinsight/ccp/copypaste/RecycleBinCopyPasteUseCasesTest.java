package com.helicalinsight.ccp.copypaste;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecycleBinCopyPasteUseCasesTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	@Autowired
	private CCPTestUtility ccpTestUtility;
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	
	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;
	
	@Autowired
	private HIResourceServiceDB serviceDb;
	
	private static final String FOLDER1="RecycleBinCopyPasteUseCasesTest";
	private static final String FOLDER2="RecycleBinCopyPasteUseCasesTest2";
	private static final String PREFIX1=FOLDER1+"/"+FOLDER2;
	private static final String PREFIX2=FOLDER2+"/"+PREFIX1;
	private static String url1,url2;
	
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
	
	
	@Test
	public void copypaste_a1_init() throws Exception {
		ccpTestUtility.init();
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		ccpTestUtility.createMetadata(FOLDER1);
		ccpTestUtility.createReport(FOLDER1, PREFIX1);
	}
	
	
	@Test
	public void copypaste_a2_deleteReportFolder() throws Exception {
		testUtility.deleteResource(PREFIX1);
	}
	
	@Test
	public void copypaste_a3_copyFolder1AndPasteItAtFolder2() throws Exception {
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"onConflictSkip\":true}";
		ccpTestUtility.ccpOperation("copy",formData,ccpTestUtility.getSuccessMessage(),1);
	}
	
	@Test
	public void copypaste_a4_fetchRBItemsAndVerifyDoWeHaveReplica() throws Exception {
		JsonArray array=testUtility.listRecycleBin();
		JSONArray rbIds=new JSONArray();
		int count1=0;
		int count2=0;
		for(JsonElement object : array) {
			JsonObject  json =  object.getAsJsonObject();
			String path=json.getAsJsonObject("data").get("path").getAsString();
			if(path.equals(PREFIX1) || path.equals(PREFIX1+"/"+"report1.hr"))
				count1++;
			if(path.equals(PREFIX2) || path.equals(PREFIX2+"/"+"report1.hr"))
				count2++;
			rbIds.add(json.get("recycleBinId").getAsInt());
		}
		Assert.assertEquals(count1,count2);
		String formData="{\"action\":\"restore\",\"recycleBinIds\":["+rbIds.getInt(0)+","+rbIds.getInt(1)+","+rbIds.getInt(2)+","+rbIds.getInt(3)+"]}";
		testUtility.restore(formData);
	}
	
	@Test
	public void copypaste_a5_verifyRestore() throws Exception {
		ccpTestUtility.verifyCcpOperation(PREFIX1);
		ccpTestUtility.verifyCcpOperation(PREFIX2);
	}
	
	@Test
	public void copypaste_a6_clear() throws Exception {
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_a7_a1_createdFolders() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
	}
	
	@Test
	public void copypaste_a8_copyResource() throws Exception {
		url1=doCopyPaste();
	}

	@Test
	public void copypaste_a9_deleteCopiedResource() throws Exception {
		testUtility.deleteResource(FOLDER2+"/"+FOLDER1);
	}
	
	@Test
	public void copypaste_b1_copySameResourceAgain() throws Exception {
		url2=doCopyPaste();
		Assert.assertNotEquals(url1, url2);
		HIResource resourceInRb=serviceDb.getResourceByUrl(url1, false);
		Assert.assertNotNull(resourceInRb);
		HIResource resourceNewlyCreated=serviceDb.getResourceByUrl(url2);
		Assert.assertNotNull(resourceNewlyCreated);
		Assert.assertNotEquals(resourceInRb.getResourceId(), resourceNewlyCreated.getResourceURL());
	}
	
	@Test
	public void copypaste_b2_clearRB() throws Exception {
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	private String doCopyPaste() throws Exception{
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"onConflictSkip\":false}";
		JSONObject jsonObject=JSONObject.fromObject(ccpTestUtility.ccpOperation("copy",formData,ccpTestUtility.getSuccessMessage(),1));
		return jsonObject.getJSONObject("response").getJSONObject("data").getString("path");
	}
}
