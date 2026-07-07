//package com.helicalinsight.adhoc;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletContext;
//import javax.transaction.Transactional;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
//import com.helicalinsight.test.utility.TestUtility;
//
//import net.sf.json.JSONObject;
//
//@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
//		"classpath:spring-security.xml" })
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class XmlFilePwdFieldsEncryptTest {
//	
//
//	MockMvc efwMock;
//	MockMvc mockMvc;
//	
//	@Autowired
//	FilterChainProxy filterChainProxy;
//	@Autowired
//	private WebApplicationContext context;
//	
//	@Autowired
//	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
//	
//	@Autowired
//	private FileSystemOperationsController fileSystemOperationsController;
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
//	
//	private static String metaStorePasswordErlier;
//	
//	@Test
//	public void xmlFile_a1_startDICE() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "monitor");
//        map.put("serviceType", "system");
//        map.put("service", "management");
//        map.put("formData", "{\"command\":\"START_COMPUTATION\"}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                .jsonPath("$.response.message").value("Computation Started successfully")).andReturn();
//	}
//	
//	
//	@Test
//	public void xmlFile_a2_loadNoSQL() throws Exception {
//		metaStorePasswordErlier=loadNoSQL();
//	}
//
//	@Test
//	public void xmlFile_a3_updateNoSQLXml() throws Exception {
//		String formData="{\"noSql\":{\"enableWarehouse\":\"true\",\"master\":\"local[*]\",\"appName\":\"HI-Spark-Application\",\"executeAtStart\":\"false\",\"mode\":\"inMemory\",\"url\":\"jdbc:hive2://\",\"host\":\"localhost\",\"inMemoryPort\":\"10001\",\"restApiPort\":\"4040\",\"templatePath\":\"NoSql\",\"driverClass\":\"org.apache.hive.jdbc.HiveDriver\",\"sparkHome\":\"${INSTALL_PATH}/hi-repository/System/SparkHome\",\"hadoopHome\":\"${INSTALL_PATH}/hi-repository/System/HadoopHome\",\"executorInstances\":\"1\",\"executorCore\":\"1\",\"maxCore\":\"1\",\"masterUrl\":\"auto\",\"masterPort\":\"8899\",\"masterWebUiPort\":\"8082\",\"workerWebUiport\":\"8088\",\"applicationUiPort\":\"8089\",\"executorMemory\":\"1024M\",\"portRangeStart\":\"8000\",\"portRangeEnd\":\"9000\",\"userName\":\" \",\"password\":\" \",\"metaStoreJdbcUrl\":\"jdbc:derby:${INSTALL_PATH}/db/hi_metastore;create=true\",\"metaStoreDriver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"metaStoreUserName\":\"hiuser\",\"metaStorePassword\":\"hiuser123\",\"wareHousePath\":\"${INSTALL_PATH}/hi-repository/System/hidw\",\"splitCharacter\":\";\",\"otherUrlPart\":\" \"}}";
//        updateNoSqlXml(formData);
//	}
//
//	@Test
//	public void xmlFile_a4_loadNoSQL() throws Exception {
//		String updatedMetastorePassword=loadNoSQL();
//		Assert.assertNotNull(updatedMetastorePassword);
//		Assert.assertNotEquals(metaStorePasswordErlier, updatedMetastorePassword);
//	}
//	
//	@Test
//	public void xmlFile_a5_restorePwdAndStopDICE() throws Exception {
//		String formData="{\"noSql\":{\"enableWarehouse\":\"true\",\"master\":\"local[*]\",\"appName\":\"HI-Spark-Application\",\"executeAtStart\":\"false\",\"mode\":\"inMemory\",\"url\":\"jdbc:hive2://\",\"host\":\"localhost\",\"inMemoryPort\":\"10001\",\"restApiPort\":\"4040\",\"templatePath\":\"NoSql\",\"driverClass\":\"org.apache.hive.jdbc.HiveDriver\",\"sparkHome\":\"${INSTALL_PATH}/hi-repository/System/SparkHome\",\"hadoopHome\":\"${INSTALL_PATH}/hi-repository/System/HadoopHome\",\"executorInstances\":\"1\",\"executorCore\":\"1\",\"maxCore\":\"1\",\"masterUrl\":\"auto\",\"masterPort\":\"8899\",\"masterWebUiPort\":\"8082\",\"workerWebUiport\":\"8088\",\"applicationUiPort\":\"8089\",\"executorMemory\":\"1024M\",\"portRangeStart\":\"8000\",\"portRangeEnd\":\"9000\",\"userName\":\" \",\"password\":\" \",\"metaStoreJdbcUrl\":\"jdbc:derby:${INSTALL_PATH}/db/hi_metastore;create=true\",\"metaStoreDriver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"metaStoreUserName\":\"hiuser\",\"metaStorePassword\":\"hiuser\",\"wareHousePath\":\"${INSTALL_PATH}/hi-repository/System/hidw\",\"splitCharacter\":\";\",\"otherUrlPart\":\" \"}}";
//        updateNoSqlXml(formData);
//        stopDICE();
//	}
//
//	private String loadNoSQL() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "monitor");
//        map.put("serviceType", "system");
//        map.put("service", "noSqlConfig");
//        map.put("formData", "{}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        MvcResult result= this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andReturn();
//        JSONObject jsonObject=JSONObject.fromObject(result.getResponse().getContentAsString());
//        return jsonObject.getJSONObject("response").getJSONObject("noSqldata").getJSONObject("noSql").getString("metaStorePassword");
//	}
//	
//	private void updateNoSqlXml(String formDate) throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "monitor");
//        map.put("serviceType", "system");
//        map.put("service", "updateNoSql");
//        map.put("formData", formDate);
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                        .jsonPath("$.response.message").value("The noSql.xml file updated successfully")).andReturn();
//	}
//	
//	private void stopDICE() throws Exception {
//		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
//        Map<String, String> map = new HashMap<>();
//        map.put("type", "monitor");
//        map.put("serviceType", "system");
//        map.put("service", "management");
//        map.put("formData", "{\"command\":\"STOP_COMPUTATION\"}");
//        RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
//        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1)).andExpect(MockMvcResultMatchers
//                .jsonPath("$.response.message").value("Computation Stopped successfully")).andReturn();
//	}
//}
