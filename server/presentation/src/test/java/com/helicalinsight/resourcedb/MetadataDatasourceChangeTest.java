package com.helicalinsight.resourcedb;

import static org.junit.Assert.assertNull;

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
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDatasourceChangeTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	HIResourceServiceDB serviceDb;
	@Autowired
	HIMetadataResourceServiceDB mdServiceDb;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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

	@Autowired
	private EfwServicesController efwServicesController;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String jdbcUrl = "";
	private static String firstJdbcId = "";
	private static String dbName = "";
	private static String globalConId = "";

	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
		} else if (os.toLowerCase().contains("windows")) {
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
			dbName = String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
		}
	}

	@Test
	public void md_a1_create_a_folder_to_save_metadata() throws Exception {
		testUtility.createFolder("MetadataChangeDS");
	}
	
	@Test
	public void md_a2_createDataSourceConnection() throws Exception {

		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""
				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\"}";
		String resp = testUtility.createDatasource(formData);
		JSONObject response = JSONObject.fromObject(resp).getJSONObject("response");
		globalConId = response.getString("dataSourceId");
	}
	
	@Test
    public void md_a3_create_cache() throws Exception {
        String schema =  "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
        testUtility.expand(schema);
        String  table = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
        testUtility.expand(table);
        
        String schemaGlobal =  "{\"id\":\""+globalConId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
        testUtility.expand(schemaGlobal);
        String  tableGlobal = "{\"id\":\""+globalConId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
        testUtility.expand(tableGlobal);
    }


    static String firstdbIdToChange = "";
    @Test
    public void md_a4_createMetadata() throws Exception {
        String formData =  "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"b406j\",\"dbId\":\"b406j\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataChangeDS\",\"metadataReload\":true}";
        String resp = testUtility.createMetadata(formData);
        JSONObject jsonObject = JSONObject.fromObject(resp);
        int status = jsonObject.getInt("status");
        JSONObject responseObject = jsonObject.getJSONObject("response");
        String message = responseObject.getString("message");
        Assert.assertNotNull(responseObject.getJSONArray("data"));
        Assert.assertEquals(1, status);
        Assert.assertEquals("Successfully saved metadata file", message);
        firstdbIdToChange = responseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
    }	
	
	@Test
	public void md_a5_createEFWDDataSourceConnection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", \\\""+globalConId+"\\\");\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"MetadataChangeDS\"}";
		String resp = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject data = responseObject.getJSONObject("data");
		firstJdbcId = data.getString("id");
	}
	
	@Test // use case : Global To Global ( Homogeneous ) 
	public void md_a6_global_to_global_ds_change() throws Exception  {
		
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+globalConId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"43kqg\",\"database\":\"HIUSER\",\"changed\":true,\"dbId\":\""+firstdbIdToChange+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataChangeDS\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject  response = JSONObject.fromObject(resp);
		JSONObject datasource = response.getJSONObject("response").getJSONObject("metadata").getJSONObject("dataSource");
		Assert.assertEquals(globalConId, datasource.getString("id"));
	}
	
	@Test // use case : Global to Efwd ( Heterogeneous ) 
	public void md_a7_global_to_efwd_ds_change() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"sql.jdbc.groovy.managed\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"43kqg\",\"classifier\":\"efwd\",\"database\":\"\",\"dir\":\"MetadataChangeDS\",\"driverName\":null,\"userName\":null,\"password\":null,\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", \\\""+globalConId+"\\\");\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"jdbcUrl\":null,\"changed\":true,\"driver\":null,\"dbId\":\""+firstdbIdToChange+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataChangeDS\",\"metadataReload\":false,\"uuid\":\"SaveSelectAll.metadata\",\"uniqueId\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertNotNull(responseObject.getJSONArray("data"));
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
        firstdbIdToChange = responseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
        JSONArray array = responseObject.getJSONObject("metadata").optJSONArray("connections");
        Assert.assertNull(array);
	}
	
	
	@Test
	public void md_a8_readMetadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "get");
		String formData = "{\"location\":\"MetadataChangeDS\",\"metadataFileName\":\"SaveSelectAll.metadata\"}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.id").value(firstJdbcId))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.type").value("sql.jdbc.groovy.managed"));
	}
	
	/*@Test
	public void md_b2_createMetadata() throws Exception {
		
		String schema = "{\"dir\":\"MetadataChangeDS\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(schema);
		String table = "{\"dir\":\"MetadataChangeDS\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(table);
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"MetadataChangeDS\",\"connId\":\"74vc2\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SecondMetadata\",\"location\":\"MetadataChangeDS\",\"metadataReload\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(resp).getJSONObject("response");
		firstdbIdToChange = responseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");
	}
	
	@Test   //  Efwd to Global ( Hetero ) 
	public void md_b3_editMetadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+globalConId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"izygh\",\"classifier\":\"global\",\"database\":\"HIUSER\",\"changed\":true,\"driver\":\"org.apache.derby.jdbc.ClientDriver\",\"dbId\":\""+firstdbIdToChange+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SecondMetadata\",\"location\":\"MetadataChangeDS\",\"metadataReload\":false,\"uuid\":\"SecondMetadata.metadata\",\"uniqueId\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertNotNull(responseObject.getJSONArray("data"));
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
		JSONArray array = responseObject.getJSONObject("metadata").optJSONArray("connections");
        Assert.assertNull(array);
	}
	*/
	
	
	/*@Test
	public void md_b4_readMetadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "get");
		String formData = "{\"location\":\"MetadataChangeDS\",\"metadataFileName\":\"SecondMetadata.metadata\"}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.id").value(globalConId))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.type").value("dynamicDataSource"));
	}
	*/
	static String secondJdbcId = "";
	@Test
	public void md_b5_createEFWDDataSourceConnection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", \\\"1\\\");\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"MetadataChangeDS\"}";
		String resp = testUtility.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		JSONObject data = responseObject.getJSONObject("data");
		secondJdbcId = data.getString("id");
	}


	@Test
	public void md_b9_deleteFolder() throws Exception {
		testUtility.deleteResource("MetadataChangeDS");
	}


	/*@Test
	public void md_b6_create_plain_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"MetadataChangeDS\",\"connId\":\"74vc2\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"ThirdMetadata\",\"location\":\"MetadataChangeDS\",\"metadataReload\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject responseObject = JSONObject.fromObject(resp).getJSONObject("response");
		firstdbIdToChange = responseObject.getJSONObject("metadata").getJSONObject("dataSource").getString("dbId");

	}
	*/
	/*@Test  //  Efwd to Efwd ( Homo )
	public void md_b7_efwd_to_efwd() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"sql.jdbc.groovy.managed\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"\",\"connId\":\"43kqg\",\"classifier\":\"efwd\",\"database\":\"\",\"dir\":\"MetadataChangeDS\",\"driverName\":null,\"userName\":null,\"password\":null,\"jdbcUrl\":null,\"changed\":true,\"driver\":null,\"dbId\":\""+firstdbIdToChange+"\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"MetadataChangeDS\",\"metadataReload\":false,\"uuid\":\"ThirdMetadata.metadata\",\"uniqueId\":true}";
		String resp = testUtility.createMetadata(formData);
		JSONObject jsonObject = JSONObject.fromObject(resp);
		int status = jsonObject.getInt("status");
		JSONObject responseObject = jsonObject.getJSONObject("response");
		String message = responseObject.getString("message");
		Assert.assertNotNull(responseObject.getJSONArray("data"));
		Assert.assertEquals(1, status);
		Assert.assertEquals("Successfully saved metadata file", message);
        JSONArray array = responseObject.getJSONObject("metadata").optJSONArray("connections");
        Assert.assertNull(array);
	}

*//*	@Test
	public void md_b8_readMetadata() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "metadata");
		map.put("service", "get");
		String formData = "{\"location\":\"MetadataChangeDS\",\"metadataFileName\":\"ThirdMetadata.metadata\"}";
		map.put("formData",formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.id").value(secondJdbcId))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.dataSource.type").value("sql.jdbc.groovy.managed"));
	}*/
}
