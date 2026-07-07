package com.helicalinsight.core.datasource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

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
import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.efw.controller.DataSourceController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataSourceExtraOptionTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	MockMvc dsMock;
	@Autowired
	FilterChainProxy filterChainProxy;

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

	@Autowired
	private IntegrationTestUtility testUtility;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	static String dataSourceId = "";

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
			dbName = String.join(File.separator, "/home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
		}
	}

	@Test
	public void a0_testDataSourceConnection() throws Exception {
		String config = "{\"name\":\"users\",\"url\":\"https://dummyjson.com/users\",\"strategy\":\"in-memory\",\"persistentLocation\":\"\",\"authType\":\"\",\"headers\":{\"Authorization\":\"\"},\"queryParams\":{},\"params\":[],\"postBody\":{},\"dataPath\":\"users\",\"method\":\"\",\"requireTail\":false,\"username\":\"\",\"password\":\"\",\"timeout\":1000}";
		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""
				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\",\"extraOptions\":{\"config\":"+config+",\"script\":\"\"}}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "test");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();

	}

	@Test
	public void a1_createDataSourceConnection() throws Exception {
		String config = "{\"name\":\"users\",\"url\":\"https://dummyjson.com/users\",\"strategy\":\"in-memory\",\"persistentLocation\":\"\",\"authType\":\"\",\"headers\":{\"Authorization\":\"\"},\"queryParams\":{},\"params\":[],\"postBody\":{},\"dataPath\":\"users\",\"method\":\"\",\"requireTail\":false,\"username\":\"\",\"password\":\"\",\"timeout\":1000}";
		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""
				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\",\"extraOptions\":{\"config\":" + config
				+ ",\"script\":\"\"}}";
		
		System.out.println(formData);
		String responseString = testUtility.createDatasource(formData);
		JSONObject resultJson = JSONObject.fromObject(responseString);
		JSONObject response = resultJson.getJSONObject("response");
		dataSourceId = response.getString("dataSourceId");
	}

//	@Test
	public void a2_quickTestDataSourceConnection() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		String formData = "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"classifier\":\"global\"}";
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "quickTest");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result = this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
	}

	@Test
	public void a3_deleteDatasource() throws Exception {
		String formData = "{\"classifier\":\"global\",\"id\":\"" + dataSourceId
				+ "\",\"type\":\"cascade\",\"dataSourceProvider\":\"tomcat\"}";
		testUtility.deleteDatasource(formData);
		testUtility.clearRecycleBin();
	}

}
