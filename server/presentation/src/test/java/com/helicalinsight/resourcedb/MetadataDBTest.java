package com.helicalinsight.resourcedb;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;
import net.sf.json.JSONObject;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDBTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    private IntegrationTestUtility testUtility;

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
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    @Test
    public void md_1_create_a_folder_to_save_metadata() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "MetadataTest");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));
    }
    
    
    
//    @Test
    public void md_2_quick_test() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "core");
        map.put("serviceType", "dataSource");
        map.put("service", "quickTest");
        map.put("formData", "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"classifier\":\"global\"}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }
    @Test
    public void md_3_expand_catalog_schema() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "metadataWorkflow");
        map.put("formData", "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }


    @Test
    public void md_4_expand_table() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "metadataWorkflow");
        map.put("formData", "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
    }

    @Test
    public void md_5_createMetadata() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertNotNull(responseObject.getJSONArray("data"));
        Assert.assertEquals(1, status);
        Assert.assertEquals("Successfully saved metadata file", message);
    }
    
    @Test
    public void md_9_9_saveasMetadata() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        System.out.println(jsonObject);
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String uuid = responseObject.getString("uuid");
        uuid = uuid.replace(".metadata", "");
        String[] strArr = uuid.split("_");
        
        String message = responseObject.getString("message");
        Assert.assertEquals(1, status);
        Assert.assertEquals(2, strArr[1].length());
        Assert.assertTrue(Integer.valueOf(strArr[1]) < 100);
        Assert.assertEquals("Successfully saved metadata file", message);
    }


    @Test
    public void md_6_fetchMetadata() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "get");
        map.put("formData", "{\"location\":\"MetadataTest\",\"uniqueId\":true,\"metadataFileName\":\"SaveSelectAll.metadata\",\"provideJoins\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        Assert.assertEquals(1, status);
    }

    @Test
    public void md_7_editMetadata_change_table_column_alias() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"noChange\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"noChange\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"noChange\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.generic\",\"catSchemaPredicted\":false,\"connId\":\"rkPecn3S5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[{\"id\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"alias\":\"travel_details_alias\",\"connId\":\"rkPecn3S5\"}],\"columns\":[{\"alias\":\"destination_alias\",\"columnId\":\"1c816105-70b2-42cc-9459-ed52f52974bc\",\"tableId\":\"8a28627d07d04ef096d9935f12e0c7e9\",\"connId\":\"rkPecn3S5\"}]},\"metadataReload\":false,\"location\":\"MetadataTest\",\"uuid\":\"SaveSelectAll.metadata\"}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertEquals(1, status);
        Assert.assertEquals("Successfully saved metadata file", message);
    }

    @Test
    public void md_8_editMetadata_remove_table_column() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"af8f3186af3703a70a3d6e219faafb4e\"},{\"action\":\"noChange\",\"id\":\"ca21d00c8c87263dedd812f8f74c05b5\"},{\"action\":\"delete\",\"id\":\"aab02b68e2c7febf125c50c8c5175037\"},{\"action\":\"delete\",\"id\":\"daa3221b04c18670d4af25ac99f3ae76\"},{\"action\":\"delete\",\"id\":\"cdeb5b19799c89335f23ed9b50cc5a22\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.generic\",\"catSchemaPredicted\":false,\"connId\":\"rkPecn3S5\",\"baseType\":\"global.jdbc\",\"changed\":false,\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[\"ee6b543e-4d09-48c0-9748-5505813f41b0\"],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":false,\"location\":\"MetadataTest\",\"uuid\":\"SaveSelectAll.metadata\"}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertEquals(1, status);
        Assert.assertEquals("Successfully saved metadata file", message);
    }


    @Test
    public void md_9_renameMetadataFile() throws Exception {
		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"MetadataTest/SaveSelectAll.metadata\",\"RenameMd\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Rename is successful"));
    }
    
    //Bug Id-4394
    @Test
    public void md_ten_createMetadata_with_lessthan_three_chars() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"ab\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.jsonPath("status").value(0));
    }
    //BugId - 4397
    @Test
    public void md_tena_createMetadata_with_blank_name() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.jsonPath("status").value(0));
    }
    
  //BugId - 4396
    @Test
    public void md_tenb_createMetadata_with_more_than_60_chars() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"arandomfilenamewithmorethansixtycharacterstotestmetadatalocalfile\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataTest\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.jsonPath("status").value(0));
    }
    
    //BugId - 4565
    @Test
    public void md_tenc_renameMetadataFileAndHide() throws Exception {
		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"MetadataTest/SaveSelectAll.metadata\",\"RenameMd\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isVisible", "false");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder)
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.isVisible").value("false"));
    }
    
    
  //BugId - 4566
    @Test
    public void md_tenc_renameMetadataFileAndMakePublic() throws Exception {
		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[[\"MetadataTest/SaveSelectAll.metadata\",\"RenameMd\"]]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "rename");
        map.put("sourceArray", input);
        map.put("isPublic", "true");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder)
        		.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Rename is successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.permissionLevel").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.public").value("true"));
    }
    
    @Test
    public void md_tend_deleteFolder() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        String input = "[\"MetadataTest\"]";
        Map<String, String> map = new HashMap<>();
        map.put("action", "delete");
        map.put("sourceArray", input);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("Delete operation is successful"));
        testUtility.clearRecycleBin();
    }
    
    
    

   
}
