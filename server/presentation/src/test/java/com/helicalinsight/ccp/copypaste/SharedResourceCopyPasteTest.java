package com.helicalinsight.ccp.copypaste;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

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
import com.helicalinsight.admin.model.HIResourceSecurityDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import static org.mockito.Mockito.when;
import java.util.*;
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SharedResourceCopyPasteTest {

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

	private static String userId="";
	private static final String FOLDER1="SharedResourceCopyPasteTest1";
	private static final String FOLDER2="SharedResourceCopyPasteTest2";
	private static final String FOLDER3="SharedResourceCopyPasteTest3";
	private static final String COPY="copy";

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
	}

	@Test
	public void copypaste_a2_createdFoldersForAdmin() throws Exception {
		testUtility.createFolder(FOLDER2);
		testUtility.createFolder(FOLDER1, List.of(FOLDER2));
		testUtility.createFolder(FOLDER3);
	}

	@Test
	public void copypaste_a3_createMetadata() throws Exception {
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"SharedResourceCopyPasteTest2/SharedResourceCopyPasteTest1\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
	}

	@Test
	public void copypaste_a4_createUserAndUpdate() throws Exception {
		createUser("tempUser");
	}


	@Test
	public void copypaste_a5_shareAdminCreatedResourceWithUser() throws Exception {
		share(1);
	}

	@Test
	public void copypaste_a6_createFolder() throws Exception {
		testUtility.createFolder("SharedResourceCopyPasteTest","tempUser","password");
	}


	@Test
	public void copypaste_a7_copyUserCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission(FOLDER2);
		String formData="{\"sourceUrl\":\"SharedResourceCopyPasteTest\",\"destinationUrl\":\"SharedResourceCopyPasteTest2\",\"sourcePermission\":\"5\",\"destPermission\":\""+userSharedPermission+"\",\"onConflictSkip\":true}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getExceptionMessage2(),0,"tempUser","password");
	}


	@Test
	public void copypaste_a8_shareAdminCreatedResourceWithUser() throws Exception {
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":2}]},\"revoke\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":1}]},\"type\":\"folder\",\"dir\":\"SharedResourceCopyPasteTest2\"}";
		testUtility.shareResource(formData);
	}


	@Test
	public void copypaste_a9_copyUserSharedFolderIntoCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission(FOLDER2);
		String formData="{\"sourceUrl\":\"SharedResourceCopyPasteTest2/SharedResourceCopyPasteTest1\",\"destinationUrl\":\"SharedResourceCopyPasteTest\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\",\"onConflictSkip\":true}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1,"tempUser","password");
		ccpTestUtility.verifyCcpOperation("SharedResourceCopyPasteTest/SharedResourceCopyPasteTest1");
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation("SharedResourceCopyPasteTest2/SharedResourceCopyPasteTest1");
	}

	@Test
	public void copypaste_b1_copyUserSharedFolderIntoCreatedFolder() throws Exception {
		Integer userSharedPermission=this.getPermission(FOLDER2);
		String formData="{\"sourceUrl\":\"SharedResourceCopyPasteTest2\",\"destinationUrl\":\"SharedResourceCopyPasteTest\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1,"tempUser","password");
		ccpTestUtility.verifyCcpOperation("SharedResourceCopyPasteTest/SharedResourceCopyPasteTest2");
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation(FOLDER2);
		testUtility.deleteUser(userId);
		ccpTestUtility.clearDB("SharedResourceCopyPasteTest",FOLDER2);
	}


	@Test
	public void copypaste_b2_copyUserSharedFolderIntoCreatedFolderForUser2() throws Exception {
		createUser("tempUser2");
		testUtility.createFolder(FOLDER2);
		share(2);
		testUtility.createFolder("SharedResourceCopyPasteTest","tempUser2","password");
		Integer userSharedPermission=this.getPermission(FOLDER2);
		String formData="{\"sourceUrl\":\"SharedResourceCopyPasteTest2\",\"destinationUrl\":\"SharedResourceCopyPasteTest\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\",\"onConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1,"tempUser2","password");
		ccpTestUtility.verifyCcpOperation("SharedResourceCopyPasteTest/SharedResourceCopyPasteTest2");
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation(FOLDER2);
	}

	@Test
	public void copypaste_b3_copyPasteResourceIntoAValidDestinationWhichIsNotAvailableToSharedUsser() throws Exception {
		Integer userSharedPermission=this.getPermission(FOLDER2);
		String formData="{\"sourceUrl\":\"SharedResourceCopyPasteTest2\",\"destinationUrl\":\""+FOLDER3+"\",\"sourcePermission\":\""+userSharedPermission+"\",\"destPermission\":\"5\",\"onConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getExceptionMessage1(),0,"tempUser2","password");
	}

	@Test
	public void copypaste_b4_clearSharedUserData() throws Exception {
		testUtility.deleteUser(userId);
		ccpTestUtility.clearDB("SharedResourceCopyPasteTest",FOLDER2,FOLDER3);
	}


	public Integer getPermission(String folderName) {
		HIResource sharedResource=serviceDb.getResourceByUrl(folderName);
		HIResourceSecurityDB securityInfo=serviceDb.fetchPermissionBySharedUserIdAndResourceIdTest(Integer.parseInt(userId),sharedResource.getResourceId());
		return securityInfo.getPermission();
	}

	private void createUser(String userName) throws Exception{
		String formData = "{\"id\":\"\",\"email\":\"temp@temp.com\",\"name\":\""+userName+"\",\"enabled\":true,\"password\":\"password\",\"organisation\":\"\"}";
		userId=ccpTestUtility.createUser(formData);
	}

	private void share(Integer permission) throws Exception{
		String formData = "{\"share\":{\"user\":[{\"id\":\""+userId+"\",\"permission\":"+permission+"}]},\"type\":\"folder\",\"dir\":\"SharedResourceCopyPasteTest2\"}";
		testUtility.shareResource(formData);
	}
}
