package com.helicalinsight.admin.controller;

import java.io.File;
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
import org.springframework.test.web.servlet.MvcResult;
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
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImageReadAPItest {

    MockMvc efwMock;
    MockMvc mockMvc;
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    

    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }

    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
    }

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
    @Test
    public void imageApi_a1_FolderCreate() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "ImageReadAPITest");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));
    }

    @Test
    public void imageApi_a2_upload() throws Exception {
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
    	map.put("destination", "ImageReadAPITest");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The import operation is successful"));
        
    }
    
    // ImageReadAPITest/echo_sport.image
    
    @Test
    public void imageApi_a3_readImageData() throws Exception {
    	
    	   MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                   .post("/services");
       
    	    Map<String, String> map = new HashMap<>();
	        map.put("type", "util");
	        map.put("serviceType", "io");
	        map.put("service", "imageService");
	        map.put("formData", "{\"dir\":\"ImageReadAPITest\",\"file\":\"echo_sport.image\"}");

           RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
          MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                   .andReturn();
          
        String responseString =   result.getResponse().getContentAsString();
        JsonObject rawResponse =  GsonUtility.parseString(responseString, JsonObject.class);
        JsonObject response = GsonUtility.optJsonObject(rawResponse, "response");
        Assert.assertNotNull(response);
        
        System.out.println(response);
        
        JsonObject data = GsonUtility.optJsonObject(response, "data");
        
        Assert.assertNotNull(data);
        
        Assert.assertTrue(data.has("title"));
        Assert.assertTrue(data.has("fileName"));
        Assert.assertTrue(data.has("location"));
        Assert.assertTrue(data.has("imageData"));
        Assert.assertTrue(data.has("resourceId"));
        Assert.assertFalse(data.has("imagePath"));
    }
    
    @Test
    public void imageApi_a4_readImagePath() throws Exception {
    	
    	   MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                   .post("/services");
       
    	    Map<String, String> map = new HashMap<>();
	        map.put("type", "util");
	        map.put("serviceType", "io");
	        map.put("service", "imageService");
	        map.put("formData", "{\"dir\":\"ImageReadAPITest\",\"file\":\"echo_sport.image\",\"fetchPath\":\"true\"}");

           RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
          MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                   .andReturn();
          
        String responseString =   result.getResponse().getContentAsString();
        JsonObject rawResponse =  GsonUtility.parseString(responseString, JsonObject.class);
        JsonObject response = GsonUtility.optJsonObject(rawResponse, "response");
        
        System.out.println(response);
        
        Assert.assertNotNull(response);
        
        JsonObject data = GsonUtility.optJsonObject(response, "data");
        
        Assert.assertNotNull(data);
        
        Assert.assertTrue(data.has("title"));
        Assert.assertTrue(data.has("fileName"));
        Assert.assertTrue(data.has("location"));
        Assert.assertFalse(data.has("imageData"));
        Assert.assertTrue(data.has("resourceId"));
        Assert.assertTrue(data.has("imagePath"));
    }
    
    @Test
    public void imageApi_a5_readImageNegative() throws Exception {
    	
    	   MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                   .post("/services");
       
    	    Map<String, String> map = new HashMap<>();
	        map.put("type", "util");
	        map.put("serviceType", "io");
	        map.put("service", "imageService");
	        map.put("formData", "{\"dir\":\"ImageReadAPITest\",\"file\":\"echo-sport.image\",\"fetchPath\":\"true\"}");

           RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
           this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0));
    }
    
    @Test
    public void imageApi_a6_readImageNegative() throws Exception {
    	
    	   MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                   .post("/services");
       
    	    Map<String, String> map = new HashMap<>();
	        map.put("type", "util");
	        map.put("serviceType", "io");
	        map.put("service", "imageService");
	        map.put("formData", "{\"dir\":\"ImageReadAPITest\",\"file\":\"\",\"fetchPath\":\"true\"}");

           RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
           this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                   .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0));
    }
}
