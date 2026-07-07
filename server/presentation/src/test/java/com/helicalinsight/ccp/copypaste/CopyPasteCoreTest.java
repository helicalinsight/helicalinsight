package com.helicalinsight.ccp.copypaste;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.ccp.CCPTestUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CopyPasteCoreTest {
	
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
	
    @Autowired
    ResourcePermissionLevelsHolder accessLevelService;
	
    private static String dbName = "";
	private static String jdbcUrl = "";
	private static final String COPY="copy";
	private static final String FOLDER1="CopyPasteCoreTest";
	private static final String FOLDER2="CopyPasteCoreTest2";
	private static final String PREFIX="CopyPasteCoreTest2/CopyPasteCoreTest";

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
	public void copypaste_a1_init() throws Exception {
		ccpTestUtility.init();
	}
	
	@Test
	public void copypaste_a2_emptyFolderToEmptyFolder() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}

	@Test
	public void copypaste_a3_sourceWithDataToEmptyFolder() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		ccpTestUtility.createMetadata(FOLDER1);
		ccpTestUtility.createReport(FOLDER1,FOLDER1);
		ccpTestUtility.createDesignerReport("CopyPasteCoreTest/report1.hr",FOLDER1);
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_a4_copyALongNestedFolder() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CopyPasteCoreTest/CopyPasteCoreTest2"));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of("CopyPasteCoreTest/CopyPasteCoreTest2/CutPasteFileBrowserTest3"));
		testUtility.createFolder("CutPasteFileBrowserTest5", List.of("CopyPasteCoreTest/CopyPasteCoreTest2/CutPasteFileBrowserTest3/CutPasteFileBrowserTest4"));
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation(FOLDER1);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_a5_sourceWithAllChildFolderResourceToDestWithResourceNameConflict() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of(FOLDER1));
		testUtility.createFolder(FOLDER1, List.of(FOLDER2));
		ccpTestUtility.createMetadata("CopyPasteCoreTest/CopyPasteCoreTest2");
		ccpTestUtility.createReport("CopyPasteCoreTest/CopyPasteCoreTest2","CopyPasteCoreTest/CutPasteFileBrowserTest3");
		ccpTestUtility.createDesignerReport("CopyPasteCoreTest/CutPasteFileBrowserTest3/report.hr","CopyPasteCoreTest/CutPasteFileBrowserTest4");
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		HIResource folder1=serviceDb.getResourceByUrl(FOLDER1);
		HIResource folder2=serviceDb.getResourceByUrl(FOLDER2);
		List<HIResource> contents = serviceDb.getResourceByParentId(folder2.getResourceId());
		List<HIResource> contents2 = serviceDb.getResourceByParentId(folder1.getResourceId());
		Assert.assertNotNull(contents);
		Assert.assertEquals(contents.size(), 1);
		Assert.assertNotEquals(contents.get(0).getResourceId(), folder1.getResourceId());
		List<HIResource> contents3 = serviceDb.getResourceByParentId(contents.get(0).getResourceId());
		Assert.assertNotNull(contents2);
		Assert.assertEquals(contents2.size(), contents3.size());
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation(FOLDER1);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	@Test
	public void copypaste_a6_sourceAndDestHasSameFolderStructure() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of(FOLDER1));

		testUtility.createFolder(FOLDER2, List.of(FOLDER2));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of(FOLDER2));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of(FOLDER2));
		
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation(FOLDER1);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	@Test
	public void copypaste_a7_createPlainConAndDoCopyPaste() throws Exception {
		
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"directory\":\"CopyPasteCoreTest\",\"type\":\"sql.jdbc\"}";
    	testUtility.createPlainDatasource(formData);
    	String formData2="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData2,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		HIResource hiResource1=serviceDb.getResourceByUrl(FOLDER1);
		HIResource hiResource2=serviceDb.getResourceByUrl(PREFIX);
		Assert.assertNotNull(hiResource1);
		Assert.assertNotNull(hiResource2);
		List<HIEFWD> plainCons1=serviceDb.getHIResourceEFWDByParentResourceId(hiResource1.getResourceId());
		List<HIEFWD> plainCons2=serviceDb.getHIResourceEFWDByParentResourceId(hiResource2.getResourceId());
		Assert.assertNotNull(plainCons1);
		Assert.assertNotNull(plainCons2);
		Assert.assertEquals(plainCons1.size(), plainCons2.size());
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_a8_copyResourceFileAndPsteItAtNestedFolder() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		ccpTestUtility.init();
		ccpTestUtility.createMetadata(FOLDER1);
		testUtility.createFolder(FOLDER1, List.of(FOLDER2));
    	String formData="{\"sourceUrl\":\"CopyPasteCoreTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CopyPasteCoreTest2/CopyPasteCoreTest\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation("CopyPasteCoreTest2/CopyPasteCoreTest");
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation("CopyPasteCoreTest/SaveSelectAll.metadata");
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_a9_copyNestedResourceAndPasteItAtNestedResource() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of(FOLDER2));
		ccpTestUtility.createMetadata("CopyPasteCoreTest/CopyPasteCoreTest2");
    	String formData="{\"sourceUrl\":\"CopyPasteCoreTest/CopyPasteCoreTest2\",\"destinationUrl\":\"CopyPasteCoreTest2/CutPasteFileBrowserTest3\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation("CopyPasteCoreTest2/CutPasteFileBrowserTest3");
		ccpTestUtility.checkForSourceExistanceAfterCopyOperation("CopyPasteCoreTest2/CutPasteFileBrowserTest3");
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_b1_copyResourceFileAndPasteWhereDestinationHasConflict() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		ccpTestUtility.createMetadata(FOLDER1);
		ccpTestUtility.createMetadata(FOLDER2);
		HIResource metadataFromSource=serviceDb.getResourceByUrl("CopyPasteCoreTest2/SaveSelectAll.metadata");
		String formData="{\"sourceUrl\":\"CopyPasteCoreTest/SaveSelectAll.metadata\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		JSONObject cutPasteJsonObject=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		Long modifiedDateAfterOnConflict=cutPasteJsonObject.getJSONObject("response").getJSONObject("data").getLong("lastModified");
		HIResource metadataAfterConflictUpdate=serviceDb.getResourceByUrl("CopyPasteCoreTest2/SaveSelectAll.metadata");
		Assert.assertTrue(metadataFromSource.getLastUpdatedTime().getTime()<modifiedDateAfterOnConflict);
		Assert.assertNotEquals(metadataFromSource.getResourceId(), metadataAfterConflictUpdate.getResourceId());
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_b2_copyPasteAPIvalidation() throws Exception {
		ccpTestUtility.ccpApiValidation(COPY);
	}
	
	@Test
	public void copypaste_b3_copyPasteLastUpdateTimeTest() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		ccpTestUtility.createMetadata(FOLDER1);
		Long folder2LastUpdateTimeBeforeCopyOperation=serviceDb.getResourceByUrl(FOLDER2).getLastUpdatedTime().getTime();
		String formData="{\"sourceUrl\":\"CopyPasteCoreTest/SaveSelectAll.metadata\",\"destinationUrl\":\""+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		Long folder2LastUpdateTimeAfterCopyOperation=serviceDb.getResourceByUrl(FOLDER2).getLastUpdatedTime().getTime();
		Assert.assertNotEquals(folder2LastUpdateTimeBeforeCopyOperation, folder2LastUpdateTimeAfterCopyOperation);
		ccpTestUtility.clearDB(FOLDER1,FOLDER2);
	}
	
	@Test
	public void copypaste_b4_copyPasteAtSamePath() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		String PATH=FOLDER1+"/"+FOLDER2;
		String formData="{\"sourceUrl\":\""+PATH+"\",\"destinationUrl\":\""+FOLDER1+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		JSONObject res1=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		JSONObject res2=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		JSONObject res3=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		JSONObject res4=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		formData="{\"sourceUrl\":\""+res1.getJSONObject("response").getJSONObject("data").getString("path")+"\",\"destinationUrl\":\""+FOLDER1+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		String path1=PATH+"_Copy",path2=PATH+"_Copy_1_",path3=PATH+"_Copy_2_",path4=PATH+"_Copy_3_",path5=PATH+"_Copy_Copy";
		Assert.assertEquals(res1.getJSONObject("response").getJSONObject("data").getString("path"),path1);
		Assert.assertEquals(res2.getJSONObject("response").getJSONObject("data").getString("path"),path2);
		Assert.assertEquals(res3.getJSONObject("response").getJSONObject("data").getString("path"),path3);
		Assert.assertEquals(res4.getJSONObject("response").getJSONObject("data").getString("path"),path4);
		JSONObject res5=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
		Assert.assertEquals(res5.getJSONObject("response").getJSONObject("data").getString("path"),path5);
		ccpTestUtility.clearDB(FOLDER1);
	}
	
	@Test
	public void copypaste_b4_copyPasteAsADefault() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2);
		ccpTestUtility.createMetadata(FOLDER1);
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\""+FOLDER2+"\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
		String formData="{\"sourceUrl\":\"CopyPasteCoreTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CopyPasteCoreTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		HIResource metadata1=serviceDb.getResourceByUrl("CopyPasteCoreTest/SaveSelectAll.metadata");
		HIResource metadata2=serviceDb.getResourceByUrl("CopyPasteCoreTest2/SaveSelectAll.metadata");
		Assert.assertNotNull(metadata1);
		Assert.assertNotNull(metadata2);
		ccpTestUtility.clearDB("CopyPasteCoreTest","CopyPasteCoreTest2");
	}
	
	@Test
	public void copypaste_b5_copyPasteOfResourceIntoIt() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		String formData="{\"sourceUrl\":\"CopyPasteCoreTest/CopyPasteCoreTest2\",\"destinationUrl\":\"CopyPasteCoreTest/CopyPasteCoreTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getExceptionMessage3(),0);
		ccpTestUtility.clearDB(FOLDER1);
	}
	
	@Test
	public void copypaste_b6_copyPasteResourceAndCheckTitle() throws Exception {
		testUtility.createFolder(FOLDER1);
		ccpTestUtility.createMetadata(FOLDER1);
		String formData="{\"sourceUrl\":\"CopyPasteCoreTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CopyPasteCoreTest\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		
        String edit =  "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"RenamedMetadata\",\"location\":\"CopyPasteCoreTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        String response = testUtility.createMetadata(edit);
        JSONObject responseObj =  JSONObject.fromObject(response).getJSONObject("response");
        Assert.assertEquals("RenamedMetadata", responseObj.getJSONObject("metadata").getString("metadataName"));
        
        JSONObject res=JSONObject.fromObject(ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1));
        Assert.assertEquals(res.getJSONObject("response").getJSONObject("data").getString("title"),"RenamedMetadata Copy");
		ccpTestUtility.clearDB(FOLDER1);
	}
	
	@Test
	public void copypaste_b7_parentToChildCopy() throws Exception {
		testUtility.createFolder(FOLDER1);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\""+FOLDER1+"/"+FOLDER2+"\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getExceptionMessage3(),0);
		ccpTestUtility.clearDB(FOLDER1);
	}
	
	@Test
	public void copypaste_b8_publicFolderCopyWithModifiedPermission() throws Exception {
		JSONObject settingJson=JsonUtils.getSettingsJson();
		String existingPermission = settingJson.getJSONObject("defaultPermissions").getString("publicResourceAccessLevel");
		settingJson.getJSONObject("defaultPermissions").replace("publicResourceAccessLevel", accessLevelService.readWriteDeleteAccessLevel());
		createPublicFolder(FOLDER1);
		testUtility.createFolder(FOLDER2, List.of(FOLDER1));
		testUtility.createFolder("CopyPasteCoreTest3");
		String formData="{\"sourceUrl\":\""+FOLDER1+"\",\"destinationUrl\":\"CopyPasteCoreTest3\",\"sourcePermission\":\""+accessLevelService.publicResourceAccessLevel()+"\",\"destPermission\":\""+accessLevelService.ownerAccessLevel()+"\"}";
		ccpTestUtility.ccpOperation(COPY,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.clearDB(FOLDER1,"CopyPasteCoreTest3");
		settingJson.getJSONObject("defaultPermissions").replace("publicResourceAccessLevel", existingPermission);
	}

	private void createPublicFolder(String folder) throws Exception {
		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
	                .post("/fileSystemOperations");
	        Map<String, String> map = new HashMap<>();
	        map.put("action", "newFolder");
	        map.put("folderName", folder);
	        map.put("sourceArray","[\"\"]");
	        map.put("isPublic", "true");
	        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
	        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
	                .jsonPath("$.response.message").value("A new folder is created successfully")).andReturn();
	}
	
}
