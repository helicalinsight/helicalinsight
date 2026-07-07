/*
package com.helicalinsight.core.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;


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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.controller.DataSourceController;
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
public class DataSourceCacheTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    MockMvc dsMock;
    @Autowired
    FilterChainProxy filterChainProxy;
    
    @Autowired
    private IntegrationTestUtility testUtility;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

    @Autowired
    HIResourceDBDAO serviceDao;
    @Autowired
	private DataSourceController dataSourceController;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
                .build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();
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
	
	
	static String plainId = "";
	static String dynamicId = "";
	
	@Test
	public void a1_create_plain_connection() throws Exception {
		testUtility.createFolder("EfwdConnections");
		String plain = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\"SampleTravelData\",\"directory\":\"EfwdConnections\",\"type\":\"sql.jdbc\"}";
		String plainResponse = testUtility.createPlainDatasource(plain);
		JSONObject responseObject = JSONObject.fromObject(plainResponse).getJSONObject("response");
		plainId = responseObject.getString("dataSourceId");
	}

	@Test
	public void a2_create_dynamicSwitch() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"EfwdConnections\"}";
		String response = testUtility.createPlainDatasource(formData);
		JSONObject responseObject = JSONObject.fromObject(response).getJSONObject("response");
		dynamicId = responseObject.getString("dataSourceId");
	}
    
	@Test
	public void a3_create_cache() throws Exception {
		String globalCache =  "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(globalCache);
		String pain = "{\"dir\":\"EfwdConnections\",\"type\":\"sql.jdbc\",\"id\":\""+plainId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(pain);
		
		String dynamic = "{\"dir\":\"EfwdConnections\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+dynamicId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(dynamic);
	}
	@Test
	public void a4_verify_cache() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "cachedDS");
		map.put("formData", "{}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
		JSONObject response = JSONObject.fromObject(result.getResponse().getContentAsString());
		JSONArray array = response.getJSONObject("response").getJSONArray("dataSources");
		List<String> ids = Arrays.asList("1",plainId,dynamicId);
		List<String> idsFromDb = new ArrayList<>();
		for(Object object : array) {
			JSONObject ob = (JSONObject) object;
			idsFromDb.add(ob.getString("id"));
		}
		for(String str : ids) {
			System.out.println("str---"+str);
			System.out.println("ids From Db---"+idsFromDb);
			//Assert.assertTrue(idsFromDb.contains(str));
		}
	}
	
	@Test
	public void a5_delete_cache() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "shutdown");
		map.put("formData", "{\"ids\":[{\"id\":\"1\",\"baseType\":\"global.jdbc\"},{\"id\":\""+plainId+"\",\"baseType\":\"sql.jdbc\"},{\"id\":\""+dynamicId+"\",\"baseType\":\"sql.jdbc.groovy.managed\"}]}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("The requested DataSource(s) is/are shutdown successfully. The database cache(if any) entries are also cleared. "));
	}

   

}
*/
