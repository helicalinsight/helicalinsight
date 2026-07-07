package com.helicalinsight.export;

import java.io.File;
import java.util.HashMap;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;
import com.helicalinsight.test.utility.TestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RecycleBinEFWDExportImportTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;
	
	@Autowired
	private EFWDConnectionService efwdConnectionService;
	
	@Autowired
	private HIRecycleBinService recycleBinService;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	private static String dbName = "";
	private static String jdbcUrl = "";
	private static String fileName = "";
	private static String TESTURL = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home","helical","Performance","HITest","hiee");
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");

			} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home","helical","Performance","HITest","hiee");
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
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
	public void exp_1_create_a_folder() throws Exception {
		testUtilitiy.clearRecycleBin();		
		testUtilitiy.createFolder("RecycleBinEFWDExportImportTest");
	}

	static String firstJdbcId = "";
	
	
	@Test
	public void exp_a2_create_plain_jdbc_Datasource() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"RecycleBinEFWDExportImportTest\"}";
		String response = testUtilitiy.createPlainDatasource(formData);
		JSONObject jsonObject = JSONObject.fromObject(response);
		JSONObject responseObject = jsonObject.getJSONObject("response");
		firstJdbcId = responseObject.getString("dataSourceId");
	}

	/*@Test
	public void exp_a3_create_cache() throws Exception {
		String catalog = "{\"dir\":\"RecycleBinEFWDExportImportTest\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtilitiy.expand(catalog);
		String table =  "{\"dir\":\"RecycleBinEFWDExportImportTest\",\"type\":\"sql.jdbc.groovy.managed\",\"id\":\""+firstJdbcId+"\",\"username\":null,\"password\":null,\"url\":null,\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtilitiy.expand(table);
	}
	
	@Test
	public void exp_a4_create_metadata() throws Exception {
		String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+firstJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"RecycleBinEFWDExportImportTest\",\"connId\":\"74vc2\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"RecycleBinEFWDExportImportTest\",\"metadataReload\":true}";
		testUtilitiy.createMetadata(formData);
	}*/
	
	
	@Test
	public void exp_a5_exportFolder() throws Exception {
		String request = "{\"dir\": \"RecycleBinEFWDExportImportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName=testUtilitiy.exportResource(request, TESTURL);
	}
	
	
	@Test
	public void exp_a6_deleteConnection() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"efwd\",\"id\":\"" + firstJdbcId
				+ "\",\"type\":\"cascade\",\"directory\":\"RecycleBinEFWDExportImportTest\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The data source has been deleted successfully."))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.data.id").value(firstJdbcId));
		HIEfwdConnection connection =  efwdConnectionService
				.findConnectionById(firstJdbcId,false);
		Assert.assertNotNull(connection);
		Assert.assertTrue(connection.isDeleted());
	}
	
	@Test
	public void exp_a7_importResource() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtilitiy.importResource(request, TESTURL, fileName);
//		HIEfwdConnection connection =  efwdConnectionService
//				.findConnectionById(firstJdbcId);
//		Assert.assertNotNull(connection);
//		Assert.assertFalse(connection.isDeleted());
	}
	
//	@Test(expected = EfwServiceException.class)
	public void exp_a8_verifyRecycleBinItem() throws Exception {
		recycleBinService.findHIRecycleBinByEFWDId((Integer.valueOf(firstJdbcId)));
	}
	
//	@Test
	public void exp_a9_verify_connection() throws Exception {
		HIEfwdConnection connection =  efwdConnectionService.findConnectionById(firstJdbcId);
		Assert.assertNotNull(connection);
		testUtilitiy.clearRecycleBin();
	}
}
