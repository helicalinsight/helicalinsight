package com.helicalinsight.core.datasource;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.controller.AdminController;
import com.helicalinsight.efw.controller.DataSourceController;
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
public class ManagedGroovyShareTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	 MockMvc dsMock;
	 
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;


	@Autowired
	private AdminController adminController;
	
    @Autowired
	private DataSourceController dataSourceController;


	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController,this.adminController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
        this.dsMock = MockMvcBuilders.standaloneSetup(this.dataSourceController).addFilter(filterChainProxy).build();

	}

	@Autowired
	private EfwServicesController efwServicesController;
	
	@Autowired
	private IntegrationTestUtility testUtility;

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String firstJdbcId = "";

	@Test
	public void ds_a1_init() throws Exception {
		testUtility.createFolder("GroovyManagedShareTest");
		testUtility.createFolder("EfwdDataSource",Arrays.asList("GroovyManagedShareTest"));
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"GroovyManagedShareTest/EfwdDataSource\"}";
		testUtility.createPlainDatasource(formData);
	}

	@Test
	public void ds_a2_share_root_folder_with_user() throws Exception  {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("");
		Map<String, String> map = new HashMap<>();
		map.put("service", "update");
		map.put("serviceType", "share");
		map.put("type", "core");
		map.put("formData","{\"share\":{\"user\":[{\"id\":2,\"permission\":5}]},\"type\":\"folder\",\"dir\":\"GroovyManagedShareTest\"}");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The selected folder privileges are updated successfully."));

	}
	@Test
	public void ds_a3_check_if_shared() throws Exception {
		
		 MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/listDataSources");
	        Map<String, String> map = new HashMap<>();
	        map.put("type", "sql.jdbc.groovy.managed");
	        map.put("name", "Groovy Managed Jdbc DataSource");
	        map.put("classifier", "efwd");
	        map.put("categoryName", "advanced");
	        map.put("categoryType", "advanced");
	        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
	        MvcResult result = this.dsMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	        JSONObject responseNode = JSONObject.fromObject(result.getResponse().getContentAsString());
	        JSONArray array = responseNode.getJSONArray("dataSources");
	        Assert.assertNotNull(array);
	        Assert.assertTrue(array.size() >  0);
	        for(Object object : array) {
	        	JSONObject connection = (JSONObject) object;
	        	Assert.assertTrue(connection.containsKey("baseType"));
	        	if(connection.getJSONObject("data").getString("id").equals("1")) {
	        		Assert.assertEquals("5", connection.getString("permissionLevel"));
	        		break;
	        	}
	        }  
	}
	
	@Test
	public void ds_a4_delete_folder() throws Exception {
		testUtility.deleteResource("GroovyManagedShareTest");
	}
	
	
}
