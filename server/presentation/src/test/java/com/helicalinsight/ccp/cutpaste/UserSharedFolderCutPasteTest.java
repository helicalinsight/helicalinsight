package com.helicalinsight.ccp.cutpaste;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import static org.mockito.Mockito.when;
import java.util.*;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserSharedFolderCutPasteTest {

	MockMvc efwMock;
	MockMvc mockMvc;

	@Autowired
	FilterChainProxy filterChainProxy;
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtility;

	@Autowired
	CCPTestUtility ccpTestUtility;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;


	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Autowired
	private HIResourceServiceDB serviceDb;

	private static String dbName = "";
	private static String jdbcUrl = "";
	private static String userId="";
	private static final String CUT="cut";

	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}

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
	public void cutpaste_a1_init() throws Exception {
		ccpTestUtility.init();
	}

	@Test
	public void cutpaste_a2_createdFoldersForAdmin() throws Exception {
		testUtility.createFolder("UserSharedFolderCutPasteTest2");
		testUtility.createFolder("UserSharedFolderCutPasteTest1", List.of("UserSharedFolderCutPasteTest2"));
	}

	@Test
	public void cutpaste_a3_createMetadata() throws Exception {
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"UserSharedFolderCutPasteTest2/UserSharedFolderCutPasteTest1\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
	}

	@Test
	public void cutpaste_a4_createUserAndUpdate() throws Exception {
		String formData = "{\"id\":\"\",\"email\":\"temp@temp.com\",\"name\":\"tempUser\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		userId=ccpTestUtility.createUser(formData);
	}

	@Test
	public void cutpaste_a5_shareAdminCreatedResourceWithUser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"UserSharedFolderCutPasteTest2\"}";
		testUtility.shareResource(formData);
	}

	@Test
	public void cutpaste_a6_createFolder() throws Exception {
		testUtility.createFolder("UserSharedFolderCutPasteTest","tempUser","password");
	}


	@Test
	public void cutpaste_a7_mergeUserCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission("UserSharedFolderCutPasteTest2");
		String formData="{\"sourceUrl\":\"UserSharedFolderCutPasteTest\",\"destinationUrl\":\"UserSharedFolderCutPasteTest2\",\"sourcePermission\":\"5\",\"destPermission\":\""+userSharedPermission+"\"}";
		ccpTestUtility.ccpOperation(CUT, formData, ccpTestUtility.getExceptionMessage2(), 0, "tempUser","password");
	}


	@Test
	public void cutpaste_a8_shareAdminCreatedResourceWithUser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":4}]},\"revoke\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":2}]},\"type\":\"folder\",\"dir\":\"UserSharedFolderCutPasteTest2\"}";
		testUtility.shareResource(formData);
	}


	@Test
	public void cutpaste_a9_mergeUserSharedFolderIntoCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission("UserSharedFolderCutPasteTest2");
		String formData="{\"sourceUrl\":\"UserSharedFolderCutPasteTest2/UserSharedFolderCutPasteTest1\",\"destinationUrl\":\"UserSharedFolderCutPasteTest\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT, formData, ccpTestUtility.getSuccessMessage(), 1, "tempUser","password");
		ccpTestUtility.verifyCcpOperation("UserSharedFolderCutPasteTest/UserSharedFolderCutPasteTest1");
	}

	@Test
	public void cutpaste_b1_mergeUserSharedFolderIntoCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission("UserSharedFolderCutPasteTest2");
		String formData="{\"sourceUrl\":\"UserSharedFolderCutPasteTest2\",\"destinationUrl\":\"UserSharedFolderCutPasteTest\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT, formData, ccpTestUtility.getSuccessMessage(), 1, "tempUser","password");
		ccpTestUtility.verifyCcpOperation("UserSharedFolderCutPasteTest/UserSharedFolderCutPasteTest2");
	}

	@Test
	public void cutpaste_b2_clearDB() throws Exception {
		testUtility.deleteResource("UserSharedFolderCutPasteTest");
		testUtility.deleteUser(userId);
		testUtility.clearRecycleBin();
	}

	public Integer getPermission(String folderName) {
		HIResource sharedResource=serviceDb.getResourceByUrl(folderName);
		HIResourceSecurityDB securityInfo=serviceDb.fetchPermissionBySharedUserIdAndResourceIdTest(Integer.parseInt(userId),sharedResource.getResourceId());
		return securityInfo.getPermission();
	}

}
