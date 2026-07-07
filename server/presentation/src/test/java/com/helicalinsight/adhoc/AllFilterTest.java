package com.helicalinsight.adhoc;

import java.util.ArrayList;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.filter.CacheFilter;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AllFilterTest {
	
	
	    MockMvc efwMock;
	    MockMvc mockMvc;
	    @Autowired
	    FilterChainProxy filterChainProxy;

	    @Autowired
	    private WebApplicationContext context;


	    @Autowired
	    private ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	    
	    @Autowired
	    private CacheFilter cacheFilter;
	    
	    @Autowired
	    private IntegrationTestUtility testUtility;

	    @Bean
	    public FileSystemOperationsController fileSystemOperationsController() {
	        return new FileSystemOperationsController();
	    }
	    

	    @Before
	    @Transactional
	    public void setup() {
	        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController)
	        		.addFilters(filterChainProxy, cacheFilter)
	        		.build();
	        ServletContext servletContext = context.getServletContext();
	        servletContext.setAttribute("filterStatus", "ok");
	        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, cacheFilter,  authenticationAndAuthorizationFilter).build();
	    }
	    

	    @Autowired
	    private FileSystemOperationsController fileSystemOperationsController;
	    
	    
	    
	    @Test
	    public void md_1_create_a_folder_to_save_metadata() throws Exception {
	        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
	                .post("/fileSystemOperations")
	                .servletPath("/fileSystemOperations");;
	        List<String> sourceList = new ArrayList<>();
	        sourceList.add("");
	        Map<String, String> map = new HashMap<>();
	        map.put("action", "newFolder");
	        map.put("folderName", "AllFilter");
	        map.put("sourceArray", sourceList.toString());
	        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
			//JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
	        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
	                .jsonPath("$.response.message").value("A new folder is created successfully"));
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
        map.put("formData", "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"classifier\":\"db.workflow\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"datasourceName\":\"SampleTravelDataDerby\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"fileName\":\"SaveSelectAll\",\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"AllFilter\",\"uniqueId\":true}");
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertTrue(responseObject.has("data"));
        JSONArray array = responseObject.getJSONArray("data");
		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			Assert.assertTrue(data.containsKey("lastModified"));
			Assert.assertTrue(data.containsKey("type"));
			Assert.assertTrue(data.containsKey("options"));
			Assert.assertTrue(data.containsKey("extension"));
			Assert.assertTrue(data.containsKey("path"));
			Assert.assertTrue(data.containsKey("permissionLevel"));
			Assert.assertTrue(data.containsKey("name"));
			Assert.assertTrue(data.containsKey("title"));
			Assert.assertEquals(1, status);
			Assert.assertEquals("Successfully saved metadata file", message);
		}
    }
	
	
	    @Test
	    public void md_8_createReport_equals() throws Exception {
	        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
	        Map<String, String> map = new HashMap<>();
	        map.put("type", "adhoc");
	        map.put("serviceType", "report");
	        map.put("service", "generateQuery");
	        String  formData = "{\"location\":\"AllFilter\",\"metadataFileName\":\"SaveSelectAll.metadata\",\"columns\":[{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"address\",\"custom\":true}]},\"filters\":[{\"values\":[\"'_all_')\"],\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"isCustomValue\":true,\"column\":\"HIUSER.employee_details.address\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
	        map.put("formData",formData);
	        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
	        MvcResult result = efwMock.perform(builder).andReturn();
	        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
	        int status = jsonObject.getInt("status");
	        JSONObject responseObject = jsonObject.getJSONObject("response");
	        Assert.assertTrue(responseObject.has("query"));
	        String query = responseObject.getString("query");
	        Assert.assertTrue(query.contains("'_all_' = '_all_'"));
	        
	        Assert.assertEquals(1, status);                          
	    }
	    
	    @Test
	    public void md_9_fetchDataAgainToEnsureCache() throws Exception {
	        String  formData = "{\"location\":\"AllFilter\",\"metadataFileName\":\"SaveSelectAll.metadata\",\"columns\":[{\"column\":\"HIUSER.employee_details.address\",\"alias\":\"address\",\"floatingType\":\"discrete\"}],\"functions\":{\"groupBy\":[{\"column\":\"address\",\"custom\":true}]},\"filters\":[{\"values\":[\"'_all_')\"],\"operator\":\"AND\",\"dataType\":\"java.lang.String\",\"customCondition\":\" IN (\",\"encloseInQuotes\":false,\"isCustomValue\":true,\"column\":\"HIUSER.employee_details.address\",\"id\":0,\"condition\":\"CUSTOM\"}],\"customFilterExpression\":\" ${0} \",\"limitBy\":10,\"prependTableNameToAlias\":false}";
	        String response = testUtility.fetchData(formData);  
	    	JsonObject  responseJson = GsonUtility.parseString(response, JsonObject.class);
	    	Assert.assertEquals(1, GsonUtility.optInt(responseJson, "status"));
	    	Assert.assertTrue(GsonUtility.getByPath(responseJson, "response.data") != null);
	    }
	
     
     @Test
     public void md_ten_deleteFolder() throws Exception {
         MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                 .post("/fileSystemOperations");
         String input = "[\"AllFilter\"]";
         Map<String, String> map = new HashMap<>();
         map.put("action", "delete");
         map.put("sourceArray", input);
         RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
         this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                 .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                 .jsonPath("$.response.message").value("Delete operation is successful"));
     }


}
