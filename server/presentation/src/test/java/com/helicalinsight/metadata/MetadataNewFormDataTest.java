package com.helicalinsight.metadata;

import java.io.File;
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

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataNewFormDataTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;


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

    private static String dbName = "";
	private static String jdbcUrl = "";
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
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    

    @Test
    public void md_a1_create_a_folder_to_save_metadata() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
                .post("/fileSystemOperations");
        List<String> sourceList = new ArrayList<>();
        sourceList.add("");
        Map<String, String> map = new HashMap<>();
        map.put("action", "newFolder");
        map.put("folderName", "MetadataNewFormDataTest");
        map.put("sourceArray", sourceList.toString());
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
                .jsonPath("$.response.message").value("A new folder is created successfully"));
    }
    
    @Test
    public void md_a2_expand_catalog_schema() throws Exception {
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
    public void md_a3_expand_table() throws Exception {
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
	public void md_a4_retrieveViewLabels() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "retrieveViewLabels");

		map.put("formData","{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[]}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));

	}
    
    static String viewId = "";
    @Test
   	public void md_a5_saveView() throws Exception {
   		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
   		Map<String, String> map = new HashMap<>();
   		map.put("type", "adhoc");
   		map.put("serviceType", "metadata");
   		map.put("service", "saveView");

   		map.put("formData","{\"catSchemaPredicted\":false,\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"sync\":false,\"classifier\":\"db.workflow\",\"queryType\":\"conditionIf\",\"query\":\"select * from \\\"dimdate\\\"\",\"viewName\":\"View 1\",\"labels\":[{\"name\":\"dim_id\",\"type\":\"numeric\",\"checked\":true},{\"name\":\"fiscal_year\",\"type\":\"date\",\"checked\":true},{\"name\":\"modified_date\",\"type\":\"dateTime\",\"checked\":true},{\"name\":\"date_key\",\"type\":\"text\",\"checked\":true},{\"name\":\"day_number\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_name\",\"type\":\"text\",\"checked\":true},{\"name\":\"fiscal_month_label\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_date\",\"type\":\"text\",\"checked\":true},{\"name\":\"created_time\",\"type\":\"text\",\"checked\":true},{\"name\":\"rating\",\"type\":\"text\",\"checked\":true}]}");
   		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
   		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
   				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
   		JSONObject object = JSONObject.fromObject(result.getResponse().getContentAsString());
   		viewId = object.getJSONObject("response").getString("viewId");
   	}
    @Test
	public void md_a6_validate_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "evaluateSecurity");

		map.put("formData",
				"{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Condition Test Success"));
	}

	static String expressionId = "";

	@Test
	public void md_a7_create_security() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "access");

		map.put("formData",
				"{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dimdate\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_HywgRinHc\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}");

		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult mvcResult = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(mvcResult.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressionId"));
		expressionId = response.getString("expressionId");
	}
    
    static String dbId = "";
    public JSONObject createMetadata(String formData) throws Exception{
   	 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "adhoc");
        map.put("serviceType", "metadata");
        map.put("service", "update");
        map.put("formData", formData);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andReturn();
        JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertNotNull(responseObject.getJSONArray("data"));
        Assert.assertEquals(1, status);
        Assert.assertEquals("Successfully saved metadata file", message);
        return responseObject;
   }

    @Test
    public void md_a8_createMetadata() throws Exception {
        String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+expressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[\""+viewId+"\"]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"MetadataNewFormDataTest\",\"fileName\":\"SaveSelectAll\",\"uniqueId\":true}";
        createMetadata(formData);
        check_security_existence();
    }
    
    @Test
    public void md_a9_createMetadata_second_save() throws Exception {
        String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"c3fb0161f2b4f1695ebbe6ab08ee74da\"},{\"action\":\"noChange\",\"id\":\"c113fa06a79370db6feb443c0023f531\"},{\"action\":\"noChange\",\"id\":\"55a5cfab25247c48f9776bf9bd457a3c\"},{\"action\":\"noChange\",\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9\"},{\"action\":\"noChange\",\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22\"}],\"access\":{\"expression\":[]},\"dataSource\":{},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        JSONObject obj =  createMetadata(formData);
        Assert.assertTrue(!obj.getJSONObject("metadata").getJSONObject("dataSource").isEmpty());
        dbId = obj.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
        check_security_existence();
    }
    
    
	public void check_security_existence() throws Exception {
		MockHttpServletRequestBuilder httpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "getSecurity");
		map.put("formData","{\"metadataFileName\":\"SaveSelectAll.metadata\",\"location\":\"MetadataNewFormDataTest\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(httpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONObject response = jsonObject.getJSONObject("response");
		Assert.assertTrue(response.containsKey("expressions"));
		JSONArray array = response.getJSONArray("expressions");
		Assert.assertTrue(!array.isEmpty());
	}
    
    @Test
    public void md_b1_edit_metadata() throws Exception {
        String formData =  "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"c3fb0161f2b4f1695ebbe6ab08ee74da\"},{\"action\":\"noChange\",\"id\":\"c113fa06a79370db6feb443c0023f531\"},{\"action\":\"noChange\",\"id\":\"55a5cfab25247c48f9776bf9bd457a3c\"},{\"action\":\"noChange\",\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9\"},{\"action\":\"noChange\",\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22\"}],\"access\":{\"expression\":[]},\"dataSource\":{},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        JSONObject obj =  createMetadata(formData);
        Assert.assertTrue(!obj.getJSONObject("metadata").getJSONObject("dataSource").isEmpty());
        check_security_existence();
    }
    static String dataSourceId = "";
    @Test
    public void md_b2_createSecondDataSourceConnection() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
        Map<String, String> map = new HashMap<>();
        map.put("type", "core");
        map.put("serviceType", "dataSource");
        map.put("service", "write");
        String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
        map.put("formData", formData);
        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
        MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
                        .value("A new Tomcat data source is created successfully."))
                .andReturn();
        JSONObject resultJson = JSONObject.fromObject(result.getResponse().getContentAsString());
        JSONObject response = resultJson.getJSONObject("response");
        dataSourceId = response.getString("dataSourceId");
        check_security_existence();
    }
    @Test
    public void md_b3_edit_metadata_change_ds() throws Exception {
        String formData =  "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[{\"action\":\"noChange\",\"id\":\"c3fb0161f2b4f1695ebbe6ab08ee74da\"},{\"action\":\"noChange\",\"id\":\"c113fa06a79370db6feb443c0023f531\"},{\"action\":\"noChange\",\"id\":\"55a5cfab25247c48f9776bf9bd457a3c\"},{\"action\":\"noChange\",\"id\":\"aa85f3fbafd188679f5b9da8797d9ec9\"},{\"action\":\"noChange\",\"id\":\"1f3619f6ae1549d8a8d89c7b7466af22\"}],\"access\":{\"expression\":[]},\"dataSource\":{\"changed\":true,\"sync\":false,\"id\":\""+dataSourceId+"\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        JSONObject obj =  createMetadata(formData);
        Assert.assertTrue(!obj.getJSONObject("metadata").getJSONObject("dataSource").isEmpty());
        Assert.assertTrue(obj.getJSONObject("metadata").getJSONObject("dataSource").getString("id").equals(dataSourceId));
        dbId = obj.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
        Assert.assertTrue(obj.getJSONObject("metadata").getJSONObject("tables").containsKey("View 1"));
        check_security_existence();
    }
    static String tableIdToRemove = "";
    static JSONArray joins = null;
    @Test
    public void md_b4_edit_metadata_change_again() throws Exception {
        String formData =  "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"changed\":true,\"sync\":false,\"id\":\"1\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        JSONObject obj =  createMetadata(formData);
        
        JSONObject metadata = obj.getJSONObject("metadata");
        JSONObject dataSource = obj.getJSONObject("metadata").getJSONObject("dataSource");
        Assert.assertTrue(!dataSource.isEmpty());
        Assert.assertEquals(1,dataSource.getInt("id"));
        Assert.assertTrue(obj.getJSONObject("metadata").getJSONObject("tables").containsKey("View 1"));
        check_security_existence();
        dbId = obj.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
        tableIdToRemove = metadata.getJSONObject("tables").getJSONObject("geo_cordinates").getString("id");
        
        joins = obj.getJSONObject("metadata").getJSONArray("joins");
        for(Object object : joins) {
        	JSONObject join = (JSONObject) object;
        	join.discard("type");
    		join.discard("operator");
    		
        	if(join.getJSONObject("left").getString("table").equalsIgnoreCase("geo_cordinates") || join.getJSONObject("right").getString("table").equalsIgnoreCase("geo_cordinates")) {
        		join.put("action", "delete");
        		join.discard("left");
        		join.discard("right");
        	}
        	else {
        		join.put("action", "noChange");
        		join.discard("left");
        		join.discard("right");
        	}
        }
    }
    
    @Test
    public void md_b5_update_remove_table() throws Exception {
        String formData =  "{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"access\":{\"expression\":[]},\"dataSource\":{\"changed\":true,\"sync\":false,\"id\":\"1\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\"},\"removeItem\":{\"tables\":[\""+tableIdToRemove+"\"],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataNewFormDataTest\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
        JSONObject obj =  createMetadata(formData);
        Assert.assertTrue(!obj.getJSONObject("metadata").getJSONObject("dataSource").isEmpty());
        Assert.assertTrue(obj.getJSONObject("metadata").getJSONObject("dataSource").getString("id").equals("1"));
        Assert.assertEquals(null,obj.getJSONObject("metadata").getJSONObject("tables").optJSONObject("geo_cordinates"));
        Assert.assertTrue(obj.getJSONObject("metadata").getJSONObject("tables").containsKey("View 1"));
        check_security_existence();
        
    }
    @Test
	public void md_b6_deleteFolder() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
				.post("/fileSystemOperations");
		String input = "[\"MetadataNewFormDataTest\"]";
		Map<String, String> map = new HashMap<>();
		map.put("action", "delete");
		map.put("sourceArray", input);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
	}
}