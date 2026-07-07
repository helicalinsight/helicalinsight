package com.helicalinsight.resourcedb.hcr.adv;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.helicalinsight.efw.jasperintegration.bandcontents.HCRImage;
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
public class HCRImageTest {
	
	
	
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
	
	
	@Test
    public void hcrImage_a1_FolderCreate() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "HCRImageTest");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));
    }

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
    	map.put("destination", "HCRImageTest");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The import operation is successful"));
    }
	
	@Test
	public void hcrImage_a3_generateImageComponent() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"title\":{\"bandHeight\":440,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[{\"dir\":\"HCRImageTest\",\"file\":\"echo_sport.image\",\"link\":\"\",\"X\":103.18639999999903,\"Y\":0,\"imageHeight\":440,\"imageWidth\":370,\"imageResourceId\":17}],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"isPreview\":true,\"isExport\":\"true\",\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
		Assert.assertEquals("File successfully exported", jsonObject.getAsJsonObject("response").get("response").getAsString());
	}
	
	@Test
	public void hcrImage_a4_generateImageComponent_negative() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"title\":{\"bandHeight\":440,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[{\"dir\":\"HCRImageTest\",\"onErrorType\": \"Error\",\"file\":\"echo-sport.image\",\"link\":\"\",\"X\":103.18639999999903,\"Y\":0,\"imageHeight\":440,\"imageWidth\":370,\"imageResourceId\":17}],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"isPreview\":true,\"isExport\":\"true\",\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void hcrImage_a5_generateImageComponent_withLink() throws Exception {
		String formData = "{\"format\":\"pdf\",\"page\":0,\"connectionDetails\":{},\"designerProperties\":{\"reportName\":\"Untitled 1\",\"groups\":[],\"fields\":[],\"designerStyles\":[],\"parameters\":[],\"variables\":[],\"designerStyle\":[],\"pageWidth\":595,\"pageHeight\":842,\"orientation\":\"Portrait\",\"columnCount\":1,\"title\":{\"bandHeight\":440,\"isImageAttached\":false,\"staticText\":[],\"textField\":[],\"image\":[{\"dir\":\"\",\"onErrorType\": \"Error\",\"file\":\"\",\"link\":\"https://randomwordgenerator.com/img/picture-generator/53e3d7454351ac14f1dc8460962e33791c3ad6e04e507440762e7ad3954fcd_640.jpg\",\"X\":103.18639999999903,\"Y\":0,\"imageHeight\":440,\"imageWidth\":370,\"imageResourceId\":17}],\"lines\":[],\"break\":[],\"table\":[],\"crosstab\":[],\"chart\":[]}},\"type\":\"pdf\",\"isPreview\":true,\"isExport\":\"true\",\"reportName\":\"Untitled 1\",\"designerChange\":{\"isChanged\":true,\"printUUID\":\"printUUID\"}}";
		String resp = integrationTestUtility.generateHCRReport(formData);
		JsonObject jsonObject = GsonUtility.parseString(resp,JsonObject.class);
		int status = jsonObject.get("status").getAsInt();
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void hcrImage_a5_generateImageComponent_withExpression() throws Exception {
		
		HCRImage hcrImage = mock(HCRImage.class);
		
		Method method =  HCRImage.class.getDeclaredMethod("getImage", JsonObject.class);
		method.setAccessible(true);
		JsonObject imageJson = new JsonObject();
		
		GsonUtility.accumulate(imageJson, "expression", "$F{expression}");
		GsonUtility.accumulate(imageJson, "dir","");
		GsonUtility.accumulate(imageJson, "file","");
		GsonUtility.accumulate(imageJson, "imageHeight","100");
		GsonUtility.accumulate(imageJson, "imageWidth","100");
		String result = (String) method.invoke(hcrImage, imageJson);
		assertEquals("$F{expression}", result);
	}
	
}
