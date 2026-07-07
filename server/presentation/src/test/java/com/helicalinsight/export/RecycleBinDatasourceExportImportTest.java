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
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
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

public class RecycleBinDatasourceExportImportTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IntegrationTestUtility testUtilitiy;
	
	@Autowired
	private GlobalConnectionService globalConnectionService;
	
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
			dbName = String.join(File.separator, "/home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "HITest", "hiee");
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "HITest", "hiee");
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
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
	public void ds_rec_a0_clear_bin() throws Exception {
		testUtilitiy.clearRecycleBin();
	}
	
	
	@Test
	public void ds_rec_a1_create_a_folder() throws Exception {
		testUtilitiy.createFolder("RecycleBinDatasourceExportImportTest");
	}

	static String dataSourceId = "";
	
	@Test
	public void ds_rec_a2_create_global_connection() throws Exception {
        String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
		System.out.println(formData);
        String response = testUtilitiy.createDatasource(formData);
		JSONObject responseObj = JSONObject.fromObject(response).getJSONObject("response");
		dataSourceId = responseObj.getString("dataSourceId");
	}
	
	@Test
	public void ds_rec_a3_create_single_con_metadata() throws Exception {
		
		String expandSchema =  "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		String expandTables = "{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtilitiy.expand(expandSchema);
		testUtilitiy.expand(expandTables);
		String metadataFormData =  "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"" + dataSourceId + "\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"cv7i3\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelData\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"ed031d3decbf73c096c71a81e8e828b1\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"RecycleBinDatasourceExportImportTest\",\"metadataReload\":true}";
		testUtilitiy.createMetadata(metadataFormData);
	}
	
	@Test
	public void ds_rec_a4_exportFolder() throws Exception {
		String request = "{\"dir\": \"RecycleBinDatasourceExportImportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName=testUtilitiy.exportResource(request, TESTURL);
	}
	
	static String id = "";
	
	@Test
	public void ds_rec_a5_deleteDatasource() throws Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = MockMvcRequestBuilders.post("/services");
		Map<String, String> map = new HashMap<>();
		map.put("type", "core");
		map.put("serviceType", "dataSource");
		map.put("service", "delete");
		String formData = "{\"classifier\":\"global\",\"id\":\"" + dataSourceId
				+ "\",\"type\":\"cascade\",\"dataSourceProvider\":\"tomcat\"}";
		map.put("formData", formData);
		RequestBuilder builder = TestUtility.getMockHttpServletRequestBuilder(mockHttpServletRequestBuilder, map);
		this.efwMock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.response.message")
						.value("The datasource " + dataSourceId + " have been deleted successfully."));
		GlobalConnections connection =  globalConnectionService.getDeletedGlobalConnectionById(Integer.valueOf(dataSourceId));
		Assert.assertNotNull(connection);
		Assert.assertTrue(connection.isDeleted());
		HIRecycleBin bin =  recycleBinService.findHIRecycleBinByGlobalId(Integer.valueOf(dataSourceId));
		Assert.assertNotNull(bin);
		Assert.assertNotNull(bin.getHiRecycleBinDsGlobalConnections());
	}
	
	@Test
	public void ds_rec_a6_importResource() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		testUtilitiy.importResource(request, TESTURL, fileName);
		GlobalConnections connection =  globalConnectionService
				.getDeletedGlobalConnectionById(Integer.valueOf(dataSourceId));
		Assert.assertNotNull(connection);
//		Assert.assertFalse(connection.isDeleted());
	}
//	@Test(expected = EfwServiceException.class)
	public void ds_rec_a7_verifyRecycleBinItem() throws Exception {
		recycleBinService.findHIRecycleBinByGlobalId(Integer.valueOf(dataSourceId));
	}
}
