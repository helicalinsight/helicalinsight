package com.helicalinsight.recyclebin.fetchdetails;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class FetchDetailsAPIForFolderTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;
	
	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	
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
	

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void fetch_det_a1_create_a_folder() throws Exception {
		testUtilitiy.clearRecycleBin();
		testUtilitiy.createFolder("TheMainResource");
		testUtilitiy.createFolder("Child1", List.of("TheMainResource"));
		testUtilitiy.createFolder("ChildOfChild1",List.of("TheMainResource/Child1"));
		testUtilitiy.createFolder("Child2", List.of("TheMainResource"));
		testUtilitiy.createFolder("ChildOfChild2",List.of("TheMainResource/Child2"));
	}
	
	@Test
	public void fetch_det_a2_1_create_efwd_connection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"name\":\"PlainJdbcTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""
				+ jdbcUrl + "\",\"database\":\"" + dbName
				+ "\",\"directory\":\"TheMainResource\",\"type\":\"sql.jdbc\"}";
		testUtilitiy.createPlainDatasource(formData);
		
		
		String formData2 = "{\"classifier\":\"efwd\",\"name\":\"PlainJdbcTest\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""
				+ jdbcUrl + "\",\"database\":\"" + dbName
				+ "\",\"directory\":\"TheMainResource/Child2\",\"type\":\"sql.jdbc\"}";
		testUtilitiy.createPlainDatasource(formData2);
	}

	@Test
	public void fetch_det_a2_delete_folder() throws Exception {
		testUtilitiy.deleteResource("TheMainResource");
	}
	
	@Test
	public void fetch_det_a3_fetchDetailsOfThMainResource() throws Exception {
		JsonArray binItems = testUtilitiy.listRecycleBin();
		String binId = "";
		for(JsonElement object : binItems ) {
			JsonObject json =  object.getAsJsonObject();
			if("TheMainResource".equalsIgnoreCase(json.getAsJsonObject("data").get("name").getAsString())) {
				binId = json.get("recycleBinId").getAsString();
				break;
			}
		}
		String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtilitiy.recycleBinAction(formData);
		JSONArray data =  JSONObject.fromObject(response).getJSONObject("response").getJSONObject(binId).getJSONObject("data").getJSONArray("resources");
		Assert.assertTrue(!data.isEmpty());
		Assert.assertEquals(4, data.size());
		for(Object json : data) {
			JSONObject jsonObject = (JSONObject) json;
			Assert.assertTrue(jsonObject.containsKey("name"));
			Assert.assertTrue(jsonObject.containsKey("path"));
			Assert.assertTrue(jsonObject.containsKey("deleted"));
			Assert.assertTrue(jsonObject.getBoolean("deleted"));
		}
	}
	
//	@Test
	public void fetch_det_a4_delete_another_folder() throws Exception {
		testUtilitiy.deleteResource("TheMainResource/Child1");
		
		JsonObject binItem =  testUtilitiy.listRecycleBin().get(1).getAsJsonObject();
		Assert.assertEquals("TheMainResource/Child1", binItem.getAsJsonObject("data").get("path").getAsString());
		String binId = binItem.get("recycleBinId").getAsString();
		String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":["+binId+"]}";
		String response = testUtilitiy.recycleBinAction(formData);
		JSONArray data =  JSONObject.fromObject(response).getJSONObject("response").getJSONObject(binId).getJSONObject("data").getJSONArray("resources");
		Assert.assertTrue(!data.isEmpty());
		Assert.assertEquals(1, data.size());
		for(Object json : data) {
			JSONObject jsonObject = (JSONObject) json;
			Assert.assertTrue(jsonObject.containsKey("name"));
			Assert.assertTrue(jsonObject.containsKey("path"));
			Assert.assertTrue(jsonObject.containsKey("deleted"));
		}
	}
	
	
	
	
//	@Test
	public void fetch_det_a6_fetchDetailsOfThMainResource() throws Exception {
		JsonArray binItems = testUtilitiy.listRecycleBin();
		String binId = "";
		for(Object object : binItems ) {
			JSONObject json = (JSONObject) object;
			if("TheMainResource".equalsIgnoreCase(json.getJSONObject("data").getString("name"))) {
				binId = json.getString("recycleBinId");
				break;
			}
		}
		String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":["+binId+"]}";
		JSONObject response = JSONObject.fromObject(testUtilitiy.recycleBinAction(formData)).getJSONObject("response").getJSONObject(binId).getJSONObject("data");
		JSONArray data =  response.getJSONArray("resources");
		JSONArray datasources =  response.getJSONArray("dataSources");
		Assert.assertTrue(!data.isEmpty());
		Assert.assertEquals(4, data.size());
		Assert.assertTrue(!datasources.isEmpty());
		Assert.assertEquals(2, datasources.size());
		for(Object json : data) {
			JSONObject jsonObject = (JSONObject) json;
			Assert.assertTrue(jsonObject.containsKey("name"));
			Assert.assertTrue(jsonObject.containsKey("path"));
			Assert.assertTrue(jsonObject.containsKey("deleted"));
			if(jsonObject.getString("path").equalsIgnoreCase("TheMainResource/Child1")) {
				Assert.assertTrue(jsonObject.getBoolean("deleted"));
			}
			else {
				Assert.assertFalse(jsonObject.getBoolean("deleted"));
			}
		}
	}
	
	@Test
	public void fetch_det_a7_1_fetchDetailsOfThMainResource_NegativeCase() throws Exception {
		String formData = "{\"action\":\"fetchDetails\",\"recycleBinIds\":[]}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "adhoc");
		map.put("serviceType", "recycleBin");
		map.put("service", "recycle");
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder)
		.andExpect(result -> {
			final JSONObject response =  JSONObject.fromObject(result.getResponse().getContentAsString());
			Assert.assertEquals("0", response.getString("status"));
			Assert.assertEquals("Error: EfwdServiceException: Please provide 'recycleBinIds'", response.getJSONObject("response").getString("message"));
		});
	}
	
	@Test
	public void fetch_det_a7_clean() throws Exception {
		String response = testUtilitiy.clearRecycleBin();
	}

}
