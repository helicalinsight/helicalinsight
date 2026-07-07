package com.helicalinsight.resourcedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
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
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
public class NewFolderHandlerDBTest {

    private static boolean initialized = false;

    MockMvc mockMvc;
    MockMvc authenticationMock;

    MockMvc efwMock;

    @Autowired
    private WebApplicationContext context;

    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }

    @Autowired
    private IntegrationTestUtility testUtility;
    

    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    HIResourceServiceDB hiResourceServiceDB;

    @Autowired
    private EfwServicesController efwServicesController;

    @InjectMocks
    private ServletContext servletContext;






    @Before
    @Transactional
    public void setup() {
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus","ok");
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
        this.authenticationMock = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();

    }



    @Test
    public void a1fileSystemOperationControllerTest() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "abstractFolder");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));

    }

    @Test
    public void a2fileSystemOperationControllerNestedTest() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "nestedFolderOne");
        map.put("sourceArray", "abstractFolder");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));

    }


    @Test
    public void a3fileSystemOperationControllerNestedTwo() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"abstractFolder/nestedFolderOne\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "nestedFolderLevel2");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));
    }


    @Test
    public void a4fileSystemOperationControllerUpdate() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"abstractFolder\",\"abstractRenamed\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }


    @Test
    public void a5fileSystemOperationControllerDeleteNested() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"abstractFolder/nestedFolderOne\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Delete operation is successful"));
    }

    @Test
    public void a6folderCreationWithSpeicalCharacters() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "@abstra=ct/Fol-de__r$$");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0));
        		/**
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.path").value("_abstra_ct_Fol_de_r_"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.name").value("@abstra=ct/Fol-de__r$$"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("folder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.lastModified").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.options").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.children").exists())
                .andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));
                **/

    }


    @Test
    public void a7folderCreationWithSpaces() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "Sample Test Folder");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.path").value("Sample_Test_Folder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.name").value("Sample Test Folder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.type").value("folder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.lastModified").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.options").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.children").exists())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));

    }


    @Test
    public void a9createFolderToShare() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "TestFolder");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));

    }

    @Test
    public void b1shareFolderWithPermission2() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("service", "update");
        map.put("serviceType", "share");
        map.put("type", "core");
        map.put("formData", "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]},\"type\":\"folder\",\"dir\":\"TestFolder\"}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
    }


    @Test
    public void b2shareFolderWithPermission3() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("service", "update");
        map.put("serviceType", "share");
        map.put("type", "core");
        map.put("formData", "{\"share\":{\"user\":[{\"id\":\"2\",\"permission\":\"3\"}]},\"revoke\":{\"user\":[{\"id\":\"2\",\"permission\":\"2\"}]},\"type\":\"folder\",\"dir\":\"TestFolder\"}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("The selected folder privileges are updated successfully."));
    }
	
	

 	   /* @Test
		public void b3sharedFolderDeletion() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        Map<String, String> map = new HashMap<>();
        String input = "[\"TestFolder\"]";
        map.put("action", "delete");
        map.put("sourceArray", input);
        map.put("username", "hiuser");
        map.put("password", "hiuser");
	
           RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
       	MvcResult mvcResult  = this.authenticationMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Access Denied")).andReturn();

				
    }*/
//	TODO : Configure the limits from config file instead of hardcoding them. 
//    @Test
    public void createFolderWith60Chars() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "Helical Workflow HWF Folder _Sample Helical Workflow Example333333333333");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Name must be between 3 and 60 characters"));

    }


//    @Test
    public void renameWith60Chars() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"TestFolder\",\"Helical Workflow HWF Folder _Sample Helical Workflow Example333333333333\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Name must be between 3 and 60 characters"));
    }


    @Test
    public void hide0createHiddenFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "HiddenFolder");
        map.put("sourceArray", sourceList.toString());
        map.put("isVisible", "false");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }

    @Test
    public void hide1createOptionalHiddenFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "HiddenFolder");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }

    @Test
    public void hide2updateHiddenFolderToVisible() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"HiddenFolder\",\"HiddenFolder\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isVisible", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    @Test
    public void hide3updateVisibleFolderToHidden() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"HiddenFolder\",\"HiddenFolder\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isVisible", "false");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    @Test
    public void hide4updateFolderWithNoVisibleParam() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"HiddenFolder\",\"DefaultFolder\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    //Testcases related to public folder 4297
    @Test
    public void cc1createPublicFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "PublicFolder");
        map.put("sourceArray", sourceList.toString());
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("A new folder is created successfully"));

    }

    @Test
    public void cc2fileSystemOperationControllerNestedPublic() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"PublicFolder\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "publicFolderNested");
        map.put("sourceArray", input);
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(3))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));

    }

    @Test
    public void cc3renamePublicFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"PublicFolder\",\"newFolderName\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    //Bug id-4297
    @Test
    public void cc4createOptionalPublicFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "PublicFolderOptional");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(5))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));

    }

    //Bug id-4297
    @Test
    public void cc5createNonPublicFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "NonPublicFolder");
        map.put("sourceArray", sourceList.toString());
        map.put("isPublic", "false");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.data.permissionLevel").value(5))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("A new folder is created successfully"));

    }


    //Bug id-4297
    @Test
    public void cc6updatePublicFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"PublicFolder\",\"newFolderName\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isPublic", "false");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }


    //Bug id-4297
    @Test
    public void cc7updatePublicFolder2() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"PublicFolder\",\"newFolderName\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    //Bug id-4297
    @Test
    public void cc8updatePublicFolder3() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"PublicFolder\",\"newFolderName\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }

    @Test
    public void cc9createVisibleFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "VisibleFolder");
        map.put("sourceArray", sourceList.toString());
        map.put("isVisible", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }


    @Test
    public void rename1FolderWith2Characters() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "test_user_folder");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));
    }


    @Test
    public void rename2FolderWith2Characters() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"test_user_folder\",\"nf\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(0)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Please enter a valid name, minimum length 3."));
    }


    @Test
    public void rename3FolderWithSpecialChars() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"test_user_folder\",\"ABC$%#\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult mvcResult = this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                        .jsonPath("$.response.message").value("Rename is successful")).andReturn();
    }
    
    @Test
    public void z_clear() throws Exception  {
    	testUtility.clearRecycleBin();
    }
    
  //Bug id-4600
    /*@Test
    public void u1_uploadDataSourceDriver() throws Exception {
    	FileItem fileResource = new DiskFileItem("file", "multipart/form-data", true, "mysql-connector-java-8.0.27.jar",Integer.MAX_VALUE,  new File("D:\\MySqlJdbcDriver\\mysql-connector-java-8.0.27.jar")); 
    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/importFile").requestAttr("file", fileResource);

    	Map<String,String> map = new HashMap<>();
    	map.put("type", "datasource");
    	map.put("destination", "");
    	 RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
    	this.mockMvc.perform(builder)
    	.andExpect(MockMvcResultMatchers.status().isOk())
    	.andDo(MockMvcResultHandlers.print());
    	
    	
    }
    
  //Bug id-4600
    @Test(expected = OperationFailedException.class)
    public void u2_uploadDataSourceDriver() throws Exception {
    	FileItem fileResource = new DiskFileItem("file", "multipart/form-data", true, "mysql-connector-java-8.0.27.jar",Integer.MAX_VALUE,  new File("D:\\MySqlJdbcDriver\\mysql-connector-java-8.0.27.jar")); 
    	MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/importFile").requestAttr("file", fileResource);

    	Map<String,String> map = new HashMap<>();
    	map.put("type", "datasource");
    	map.put("destination", "");
    	 RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
    	this.mockMvc.perform(builder)
    	.andDo(MockMvcResultHandlers.print());
    }*/
}