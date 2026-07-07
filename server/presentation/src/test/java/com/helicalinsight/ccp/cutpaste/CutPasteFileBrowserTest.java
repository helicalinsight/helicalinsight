package com.helicalinsight.ccp.cutpaste;

import java.io.File;
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
public class CutPasteFileBrowserTest {
	
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
	
    private static String dbName = "";
	private static String jdbcUrl = "";
	private static final String PREFIX="CutPasteFileBrowserTest2/CutPasteFileBrowserTest";
	private static final String DESTINATION="CutPasteFileBrowserTest2";
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
	public void cutpaste_a2_emptyFolderToEmptyFolder() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.clearDB(DESTINATION);
	}

	@Test
	public void cutpaste_a3_sourceWithDataToEmptyFolder() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest");
		ccpTestUtility.createReport("CutPasteFileBrowserTest","CutPasteFileBrowserTest");
		ccpTestUtility.createDesignerReport("CutPasteFileBrowserTest/report1.hr","CutPasteFileBrowserTest");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.clearDB(DESTINATION);
	}
	
	@Test
	public void cutpaste_a4_nestedFoldersSourceToEmptyFolder() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest2", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest/CutPasteFileBrowserTest2"));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of("CutPasteFileBrowserTest/CutPasteFileBrowserTest2/CutPasteFileBrowserTest3"));
		testUtility.createFolder("CutPasteFileBrowserTest5", List.of("CutPasteFileBrowserTest/CutPasteFileBrowserTest2/CutPasteFileBrowserTest3/CutPasteFileBrowserTest4"));
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.clearDB(DESTINATION);
	}
	
	@Test
	public void cutpaste_a5_sourceWithAllChildFolderResourceToDestWithResourceNameConflict() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest2", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest", List.of("CutPasteFileBrowserTest2"));
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest/CutPasteFileBrowserTest2");
		ccpTestUtility.createReport("CutPasteFileBrowserTest/CutPasteFileBrowserTest2","CutPasteFileBrowserTest/CutPasteFileBrowserTest3");
		ccpTestUtility.createDesignerReport("CutPasteFileBrowserTest/CutPasteFileBrowserTest3/report.hr","CutPasteFileBrowserTest/CutPasteFileBrowserTest4");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		//ccpTestUtility.clearDB("CutPasteFileBrowserTest",DESTINATION);
	}
	
	@Test
	public void cutpaste_a6_sourceAndDestHasSameFolderStructure() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		
		testUtility.createFolder("CutPasteFileBrowserTest2", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of("CutPasteFileBrowserTest"));

		testUtility.createFolder("CutPasteFileBrowserTest2", List.of("CutPasteFileBrowserTest2"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest2"));
		testUtility.createFolder("CutPasteFileBrowserTest4", List.of("CutPasteFileBrowserTest2"));
		
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
		ccpTestUtility.clearDB(DESTINATION);
	}
	
	@Test
	public void cutpaste_a7_createPlainConAndDoCutPaste() throws Exception {
		
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		String formData="{\"classifier\":\"efwd\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"sample-plain\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\",\"directory\":\"CutPasteFileBrowserTest\",\"type\":\"sql.jdbc\"}";
    	testUtility.createPlainDatasource(formData);
    	String formData2="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData2,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation(PREFIX);
    	ccpTestUtility.clearDB(DESTINATION);
	}
	
	@Test
	public void cutpaste_a8_cutResourceFileAndPsteItAtNestedFolder() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		ccpTestUtility.init();
		testUtility.createFolder("CutPasteFileBrowserTest2");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest", List.of("CutPasteFileBrowserTest2"));
    	String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CutPasteFileBrowserTest2/CutPasteFileBrowserTest\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation("CutPasteFileBrowserTest2/CutPasteFileBrowserTest");
    	testUtility.deleteResource("CutPasteFileBrowserTest");
    	testUtility.deleteResource("CutPasteFileBrowserTest2");
    	testUtility.clearRecycleBin();
	}
	
	@Test
	public void cutpaste_a9_cutNestedResourceAndPasteItAtNestedResource() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest2", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest2"));
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest/CutPasteFileBrowserTest2");
    	String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest/CutPasteFileBrowserTest2\",\"destinationUrl\":\"CutPasteFileBrowserTest2/CutPasteFileBrowserTest3\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		ccpTestUtility.verifyCcpOperation("CutPasteFileBrowserTest2/CutPasteFileBrowserTest3");
    	testUtility.deleteResource("CutPasteFileBrowserTest");
    	testUtility.deleteResource("CutPasteFileBrowserTest2");
    	testUtility.clearRecycleBin();
	}
	
	@Test
	public void cutpaste_b1_cutResourceFileAndPasteWhereDestinationHasConflict() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest2");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		JSONObject cutPasteJsonObject=JSONObject.fromObject(ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1));
		String modifiedMetadataPath=cutPasteJsonObject.getJSONObject("response").getJSONObject("data").getString("path");
		HIResource resource=serviceDb.getResourceByUrl(modifiedMetadataPath);
		Assert.assertNotNull(resource);
		testUtility.deleteResource("CutPasteFileBrowserTest");
		testUtility.deleteResource("CutPasteFileBrowserTest2");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void cutpaste_b2_copyPasteAPIvalidation() throws Exception {
		ccpTestUtility.ccpApiValidation(CUT);
	}
	
	@Test
	public void cutpaste_b3_lastUpdatedTimeTest() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest2"));
		Long beforeFolderDate_1=getDate("CutPasteFileBrowserTest");
		Long afterFolderDate_2=getDate("CutPasteFileBrowserTest2");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest2/CutPasteFileBrowserTest3\",\"destinationUrl\":\"CutPasteFileBrowserTest\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		Assert.assertNotEquals(getDate("CutPasteFileBrowserTest"), beforeFolderDate_1);
		Assert.assertNotEquals(getDate("CutPasteFileBrowserTest2"), afterFolderDate_2);
		ccpTestUtility.clearDB("CutPasteFileBrowserTest","CutPasteFileBrowserTest2");
	}

	private Long getDate(String url) {
		HIResource hiResource=serviceDb.getResourceByUrl(url);
		return hiResource.getLastUpdatedTime().getTime();
	}
	
	@Test
	public void cutpaste_b4_defaultCutPasteTest() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest");
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"g7cx1\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"CutPasteFileBrowserTest2\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest/SaveSelectAll.metadata\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\"}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		HIResource metadata1=serviceDb.getResourceByUrl("CutPasteFileBrowserTest/SaveSelectAll.metadata");
		HIResource metadata2=serviceDb.getResourceByUrl("CutPasteFileBrowserTest2/SaveSelectAll.metadata");
		Assert.assertNotNull(metadata1);
		Assert.assertNotNull(metadata2);
		ccpTestUtility.clearDB("CutPasteFileBrowserTest","CutPasteFileBrowserTest2");
	}
	
	@Test
	public void cutpaste_b5_cutPasteTestSourceDeletion() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest", List.of("CutPasteFileBrowserTest2"));
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest");
		ccpTestUtility.createMetadata("CutPasteFileBrowserTest2/CutPasteFileBrowserTest");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		HIResource metdataOnelocation=serviceDb.getResourceByUrl("CutPasteFileBrowserTest");
		Assert.assertNull(metdataOnelocation);
		ccpTestUtility.clearDB("CutPasteFileBrowserTest2");
	}
	
	@Test
	public void cutpaste_b6_lastUpdatedTimeTest() throws Exception {
		testUtility.createFolder("CutPasteFileBrowserTest");
		testUtility.createFolder("CutPasteFileBrowserTest2");
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest"));
		testUtility.createFolder("CutPasteFileBrowserTest3", List.of("CutPasteFileBrowserTest2"));
		Long beforeFolderDate_1=getDate("CutPasteFileBrowserTest");
		Long beforeFolderDate_2=getDate("CutPasteFileBrowserTest2");
		String formData="{\"sourceUrl\":\"CutPasteFileBrowserTest/CutPasteFileBrowserTest3\",\"destinationUrl\":\"CutPasteFileBrowserTest2\",\"sourcePermission\":\"5\",\"destPermission\":\"5\",\"isConflictSkip\":false}";
		ccpTestUtility.ccpOperation(CUT,formData,ccpTestUtility.getSuccessMessage(),1);
		Assert.assertNotEquals(getDate("CutPasteFileBrowserTest"), beforeFolderDate_1);
		Assert.assertNotEquals(getDate("CutPasteFileBrowserTest2"), beforeFolderDate_2);
		ccpTestUtility.clearDB("CutPasteFileBrowserTest","CutPasteFileBrowserTest2");
	}

}
