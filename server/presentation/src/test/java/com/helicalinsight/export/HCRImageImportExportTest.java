package com.helicalinsight.export;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HCRImageImportExportTest {
	
	
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	private FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility integrationTestUtility;
	

	
	@Autowired
	private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Before
	@Transactional
	public void setup() {
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}
	
	private static String TESTURL = "";

	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");

		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	@Test
    public void hcrImage_a1_FolderCreate() throws Exception {
        integrationTestUtility.createFolder("HCRImageImportExportTest");
    }

	static int imageResourceId = 0;
    @Test
    public void hcrImage_a2_upload() throws Exception {
    	File file=new File("src/test/resources/test/echo-sport.jpg");
    	byte[] fileContent = Files.readAllBytes(file.toPath());

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",                         
            file.getName(),                 
            "image/jpeg",                    
            fileContent
        );

    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = 
    			MockMvcRequestBuilders.multipart("/importFile")
                .file(mockFile);
    	Map<String,String> map = new HashMap<>();
    	map.put("type", "image");
    	map.put("destination", "HCRImageImportExportTest");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        String response = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The import operation is successful"))
        .andReturn().getResponse().getContentAsString();
        JsonObject responseNode = GsonUtility.parseString(response,JsonObject.class);
        JsonObject dataNode =  GsonUtility.optJsonArray(responseNode, "data").get(0).getAsJsonObject();
        imageResourceId = GsonUtility.getByPath(dataNode, "resourceId").getAsInt();
    }
	
	@Test
	public void hcrImage_a3_generateImageComponent() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"title\":{\"bandHeight\":440,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[{\"dir\":\"HCRImageImportExportTest\",\"file\":\"echo_sport.image\",\"link\":\"\",\"X\":103.18639999999903,\"Y\":0,\"imageHeight\":440,\"imageWidth\":370,\"imageResourceId\":%d}],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"isPreview\":true,\"isExport\":\"true\",\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}".formatted(imageResourceId);
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
		String saveFormData = "{\"state\":{\"dsPanes\":[{\"key\":\"query\",\"dataSourcePane\":\"Query\",\"menu\":[{\"id\":1,\"name\":\"Query1\",\"config\":\"select  * from \\\"dimdate\\\"\",\"connectionDetails\":{},\"executeQueryData\":{\"data\":[],\"field\":[]},\"parameterList\":[],\"temp_uuid\":\"\"}]},{\"key\":\"parameter\",\"dataSourcePane\":\"Parameter\",\"menu\":[]}],\"canvasProperties\":{\"margin\":{},\"layout\":{\"name\":\"A4\",\"orientation\":\"Portrait\",\"size\":{\"width\":595,\"height\":842}},\"pageProperties\":{\"columnCount\":1},\"calculations\":{\"selectCalculation\":\"\",\"options\":[],\"keyValuePairs\":{\"id\":\"39a13b5e-4ca5-45ca-b559-5730ddc3fbef\"}},\"previewParameters\":{\"showParameters\":true},\"groupProperties\":{\"selectGroup\":\"\",\"options\":[]},\"pageStyles\":{\"selectStyles\":\"\",\"options\":[],\"keyValuePairs\":{\"borders\":{},\"padding\":{},\"lineStyles\":{}}}},\"groupCount\":0,\"groupsOrder\":[],\"selectedQueryId\":1},\"diagram\":{\"nodes\":[{\"name\":\"dim_id\",\"width\":100,\"height\":30,\"label\":\"$F{dim_id}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.lang.Integer\",\"id\":\"node-97bbfcab-aa9f-4be9-b060-315883567f1b\",\"x\":60,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null},{\"name\":\"fiscal_year\",\"width\":100,\"height\":30,\"label\":\"$F{fiscal_year}\",\"renderKey\":\"text\",\"isLeaf\":true,\"zIndex\":10,\"type\":\"queryField\",\"category\":\"text\",\"repeat\":\"rt\",\"borders\":{},\"padding\":{},\"backendDataType\":\"java.sql.Date\",\"id\":\"node-7c808e29-c783-43b3-9c63-46581f7eddca\",\"x\":201.6864,\"y\":94,\"printRepeatedValues\":true,\"fontSize\":10,\"fontFamily\":\"Serif\",\"rt\":\"footer\",\"pg\":null,\"cl\":null,\"rd\":null,\"lpf\":null,\"nd\":null}]},\"name\":\"CannedReport_formdata\",\"dir\":\"HCRImageImportExportTest\",\"previewFormData\":{\"format\":\"html\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"title\":{\"bandHeight\":440,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[{\"dir\":\"HCRImageImportExportTest\",\"file\":\"echo_sport.image\",\"link\":\"\",\"X\":103.18639999999903,\"Y\":0,\"imageHeight\":440,\"imageWidth\":370,\"imageResourceId\":%d}],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[],\"chart\":[]}},\"type\":\"html\",\"isPreview\":true,\"isExport\":\"false\",\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}},\"saveUUID\":\"6e4d7e24-8b80-4c29-a89a-4f3a821b177a\"}".formatted(imageResourceId);
		integrationTestUtility.saveHCRReportState(saveFormData);
	}
	
	static String fileName = "";
	
	@Test
	public void hcrImage_a4_exportFolder() throws Exception {
		String request = "{\"dir\": \"HCRImageImportExportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": true,\"schedules\": true}}";
		fileName = integrationTestUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void hcrImage_a5_deleteFolder() throws Exception {
		integrationTestUtility.deleteResource("HCRImageImportExportTest");
		integrationTestUtility.clearRecycleBin();
	}
	
	@Test
	public void hcrImage_a6_importFolder() throws Exception {
		String formData = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"security\":true,\"dataSource\": true,\"schedules\" : true}}";
		String response = integrationTestUtility.importResource(formData, TESTURL, fileName);
		int insertCount = GsonUtility.getByPath(GsonUtility.parseString(response, JsonObject.class), "response.insertCount").getAsInt();
		assertEquals(3, insertCount);
	}
	
	
	
}
