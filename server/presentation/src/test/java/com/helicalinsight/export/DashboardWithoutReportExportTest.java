//package com.helicalinsight.export;
//
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import jakarta.servlet.ServletContext;
//import jakarta.transaction.Transactional;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.adhoc.FileSystemOperationsController;
//import com.helicalinsight.efw.controller.EfwServicesController;
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.lingala.zip4j.core.ZipFile;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//
//public class DashboardWithoutReportExportTest {
//	
//	
//
//	MockMvc efwMock;
//	MockMvc mockMvc;
//	@Autowired
//	FilterChainProxy filterChainProxy;
//
//	@Autowired
//	private WebApplicationContext context;
//	
//	static String fileName="";
//
//	@Autowired
//	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//
//	@Bean
//	public FileSystemOperationsController fileSystemOperationsController() {
//		return new FileSystemOperationsController();
//	}
//
//	@Before
//	@Transactional
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
//				.build();
//		ServletContext servletContext = context.getServletContext();
//		servletContext.setAttribute("filterStatus", "ok");
//		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
//				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
//	}
//	
//	private static String TESTURL = "";
//	static String dataSourceId = "";
//	static {
//		String os = System.getProperty("os.name");
//		if (os.toLowerCase().contains("linux")) {
//			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
//			
//		} else if (os.toLowerCase().contains("windows")) {
//			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
//		}
//	}
//
//	@Autowired
//	private EfwServicesController efwServicesController;
//
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
//
//	@Test
//	public void exp_a1_create_a_folder_to_save_dashboard() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		List<String> sourceList = new ArrayList<>();
//		sourceList.add("");
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "newFolder");
//		map.put("folderName", "JustDashboard");
//		map.put("sourceArray", sourceList.toString());
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//						.jsonPath("$.response.message").value("A new folder is created successfully"));
//	}
//	@Test
//	public void exp_a2_createDesign() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "dashboard");
//		map.put("serviceType", "efwdd");
//		map.put("service", "designer");
//		String formData = "{\"htmlString\":\"{\\\"type\\\":\\\"div\\\",\\\"key\\\":null,\\\"ref\\\":null,\\\"props\\\":{\\\"children\\\":\\\"hi\\\"},\\\"_owner\\\":null}\",\"state\":{\"variables\":{},\"components\":[{\"id\":\"item-zCS60\",\"compType\":\"text\",\"isGrouped\":false,\"isSaved\":true,\"initialPosition\":{\"x\":0,\"y\":0,\"w\":2,\"h\":2,\"i\":\"item-zCS60\",\"static\":false},\"gridItemConfig\":[{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":1,\"yOffset\":1,\"blur\":1,\"spread\":1,\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":24,\"g\":144,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"edit\",\"values\":{\"enable\":true,\"text\":\"\",\"link\":\"\",\"placeholder\":\"Edit/Add your text content here\"}}]}],\"dashboardConfig\":{\"id\":\"dashboard\"},\"css\":\"\",\"script\":\"\",\"printOptions\":{\"templateId\":\"Dashboard\",\"title\":\"Dashboard\"},\"componentSettings\":{\"gridSettingsData\":[{\"key\":\"breakpoints\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":1200,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":996,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":768,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":480,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":240,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"columns\",\"values\":[{\"key\":\"lg\",\"name\":\"Large Screens\",\"value\":12,\"tooltip\":\"Large Screens(lg)\"},{\"key\":\"md\",\"name\":\"Medium Screens\",\"value\":12,\"tooltip\":\"Medium Screens(md)\"},{\"key\":\"sm\",\"name\":\"Small Screens\",\"value\":6,\"tooltip\":\"Small Screens(sm)\"},{\"key\":\"xs\",\"name\":\"Extra Small Screens\",\"value\":4,\"tooltip\":\"Extra Small Screens(xs)\"},{\"key\":\"xxs\",\"name\":\"Extra Extra Small Screens\",\"value\":2,\"tooltip\":\"Extra Extra Small Screens(xxs)\"}]},{\"key\":\"grid\",\"values\":{\"autoSize\":true,\"compactType\":null,\"rowHeight\":100,\"isDroppable\":false,\"preventCollision\":true,\"measureBeforeMount\":false,\"isDraggable\":true,\"isResizable\":true}},{\"key\":\"header\",\"values\":{\"enable\":false,\"title\":\"\",\"placeholder\":\"Edit/Add your header content here\",\"link\":\"\",\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"position\":\"left\"}},{\"key\":\"shadow\",\"values\":{\"enable\":false,\"xOffset\":0,\"yOffset\":0,\"blur\":0,\"spread\":0,\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"background\",\"values\":{\"enable\":false,\"backgroundColor\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100},\"image\":\"\"}},{\"key\":\"border\",\"values\":{\"enable\":false,\"borderWidth\":1,\"borderStyle\":\"solid\",\"color\":{\"r\":255,\"g\":255,\"b\":255,\"a\":100}}},{\"key\":\"html\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"css\",\"values\":{\"enable\":false,\"value\":\"\"}},{\"key\":\"javascript\",\"values\":{\"enable\":false,\"value\":\"\"}}],\"layout\":[{\"w\":2,\"h\":2,\"x\":0,\"y\":0,\"i\":\"item-zCS60\",\"moved\":false,\"static\":false}],\"designerSettings\":[{\"key\":\"parameters\",\"values\":{\"enable\":true,\"orientation\":\"right\",\"enableApplyButton\":true}}],\"parameterDrawerStatus\":false}},\"dir\":\"JustDashboard\",\"fileName\":\"dashboard\"}";
//		map.put("formData", formData);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = efwMock.perform(builder).andReturn();
//		JSONObject jsonObject = JSONObject.fromObject(result.getResponse().getContentAsString());
//		int status = jsonObject.getInt("status");
//		JSONObject responseObject = jsonObject.getJSONObject("response");
//		Assert.assertTrue(responseObject.has("data"));
//		JSONArray array = responseObject.getJSONArray("data");
//		for (int i = 0; i < array.size(); i++) {
//			JSONObject data = array.getJSONObject(i);
//			Assert.assertTrue(data.containsKey("lastModified"));
//			Assert.assertTrue(data.containsKey("type"));
//			Assert.assertTrue(data.containsKey("options"));
//			Assert.assertTrue(data.containsKey("extension"));
//			Assert.assertTrue(data.containsKey("path"));
//			Assert.assertTrue(data.containsKey("permissionLevel"));
//			Assert.assertTrue(data.containsKey("name"));
//			Assert.assertTrue(data.containsKey("title"));
//			String message = responseObject.getString("message");
//			Assert.assertEquals(1, status);
//			Assert.assertEquals("Design is saved successfully", message);
//		}
//
//	}
//	@Test
//	public void exp_a3_exportAFolder() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/exportResource");
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"dir\": \"JustDashboard\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
//		map.put("formData", request);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		MvcResult result = efwMock.perform(builder).andReturn();
//		String header = result.getResponse().getHeader("Content-Disposition");
//		fileName = header.substring(header.indexOf("=")+1);
//		fileName = fileName.substring(1,fileName.length()-1);
//		
//		byte[] bytes = result.getResponse().getContentAsByteArray();
//		try (FileOutputStream outputStream = new FileOutputStream(String.join(File.separator, TESTURL, fileName))) {
//			outputStream.write(bytes);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		ZipFile zipFile = new ZipFile(String.join("/", TESTURL,fileName));
//		Assert.assertTrue(zipFile.isValidZipFile());
//		Assert.assertNotNull(bytes);
//		Assert.assertTrue(bytes.length > 0);
//
//	}
//	@Test
//	public void exp_a4_cleanup() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.post("/fileSystemOperations");
//		String input = "[\"JustDashboard\"]";
//		Map<String, String> map = new HashMap<>();
//		map.put("action", "delete");
//		map.put("sourceArray", input);
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(
//						MockMvcResultMatchers.jsonPath("$.response.message").value("Delete operation is successful"));
//	}
//	@Test
//	public void exp_a5_importAFolderHavingOnlyDashboards() throws Exception {
//		FileInputStream fileStream = new FileInputStream(String.join(File.separator, TESTURL, fileName));
//		MockMultipartFile file = new MockMultipartFile("file", fileName, "multipart/form-data",
//				fileStream);
//		fileStream.close();
//		Map<String, String> map = new HashMap<>();
//		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : false}}";
//		map.put("formData", request);
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders
//				.fileUpload("/importResource").file(file);
//
//		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//		efwMock.perform(builder)
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
//		.andExpect(MockMvcResultMatchers.jsonPath("$.response.message").value("Imported Successfully"));
//	}
//	
//}