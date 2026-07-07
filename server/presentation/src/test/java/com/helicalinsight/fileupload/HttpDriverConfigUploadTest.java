/*
package com.helicalinsight.fileupload;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.apache.commons.fileupload.disk.DiskFileItem;
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
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpDriverConfigUploadTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private IntegrationTestUtility testUtility;

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
			dbName = String.join(File.separator, "/home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
		}
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
	private FileSystemOperationsController fileSystemOperationsController;

	@Test
	public void driver_upload_a1_config_as_file() throws Exception {

		File file = new File("/src/test/resources/test/http_config.json");
		
		DiskFileItem fileItem = new DiskFileItem("file", "multipart/file", true, "http_config.json", 0, file);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/importFile")
				.param("destination", "")
				.param("driverType", "http")
				.requestAttr("file", fileItem);
		fileItem.getOutputStream();
		Map<String, String> map = new HashMap<>();
		map.put("type", "flatfile");
		map.put("destination", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	
	@Test
	public void driver_upload_a2_config_as_string() throws Exception {
		
		String config = "{\"name\":\"products\",\"url\":\"https://dummyjson.com/products\",\"strategy\":\"in-memory\",\"persistentLocation\":\"\",\"authType\":\"\",\"headers\":{\"Authorization\":\"\"},\"queryParams\":{},\"params\":[],\"postBody\":{},\"dataPath\":\"products\",\"method\":\"\",\"requireTail\":false,\"username\":\"\",\"password\":\"\",\"timeout\":1000}";
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/importFile")
				.param("destination", "")
				.param("driverType", "http")
				.param("fileName", "string_properties.json")
				.param("config", config);
		Map<String, String> map = new HashMap<>();
		map.put("type", "flatfile");
		map.put("destination", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
	}

	//TODO  need to put the actual file in the path.
	//FIXME the hardcoded file has to be dynamically put in formdata
	//@Test
	public void driver_upload_a3_data_as_file() throws Exception {

		File file = new File("/src/test/resources/test/http_config.json");
		
		DiskFileItem fileItem = new DiskFileItem("file", "multipart/file", true, "http_config.json", 0, file);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.multipart("/importFile")
				.param("destination", "")
				.param("driverType", "flatfile")
				.param("dataFile", "true")
				.param("dataPath", "")
				.requestAttr("file", fileItem);
		fileItem.getOutputStream();
		Map<String, String> map = new HashMap<>();
		map.put("type", "flatfile");
		map.put("destination", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
//	@Test
	public void driver_upload_a4_data_as_zip() throws Exception {

		File file = new File("\\src\\test\\resources\\test\\data.zip");
		ZipFile zipFile = new ZipFile(file);
		DiskFileItem fileItem = new DiskFileItem("file", "multipart/file", true, "data.zip", 0, file);
		Object fileObject = null;
    	try(OutputStream outputStream =  fileItem.getOutputStream()) {
    		outputStream.write(Files.readAllBytes(zipFile.getFile().toPath()));
    	}
    	
    	fileObject = fileItem;
		
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/importFile")
				.requestAttr("destination", "")
				.requestAttr("driverType", "flatfile")
				.requestAttr("dataFile", "true")
				.requestAttr("dataPath", "")
				.requestAttr("file", fileObject);
		fileItem.getOutputStream();
		Map<String, String> map = new HashMap<>();
		map.put("type", "flatfile");
		map.put("destination", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1));
	}
	
	
	@Test
	public void driver_upload_data_as_file_create_connection() throws Exception {

		File file = new File("/src/test/resources/test/http_config.json");
		
		DiskFileItem fileItem = new DiskFileItem("file", "multipart/file", true, "http_config.json", 0, file);
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.multipart("/importFile")
				.param("destination", "")
				.param("driverType", "flatfile")
				.param("dataFile", "true")
				.param("dataPath", "")
				.requestAttr("file", fileItem);
		fileItem.getOutputStream();
		Map<String, String> map = new HashMap<>();
		map.put("type", "flatfile");
		map.put("destination", "");
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		MvcResult result =  this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andReturn();
		
		String responseString = result.getResponse().getContentAsString();
		JSONObject response = JSONObject.fromObject(responseString).getJSONObject("response");
		String fileName = response.getString("fileName");
		String config = "{\"tableName\":\"mydata\",\"strategy\":\"in-memory\",\"persistentLocation\":\"\",\"extensions\":[\"excel\",\"spatial\",\"httpfs\"],\"config\":{\"open_options\":[\"FIELD_TYPES=AUTO\"]}}";
		String formData = "{\"classifier\":\"global\",\"fileName\":\""+fileName+"\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"com.helical.FlatFileDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""
				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\",\"extraOptions\":{\"config\":" + config
				+ ",\"script\":\"\",\"dataFile\":\"http_config.json\"}}";
//		String testResponse = testUtility.testDataSource(formData);
		
//		System.out.println(testResponse);
		testUtility.createDatasource(formData);
	}
	
	
}
*/
