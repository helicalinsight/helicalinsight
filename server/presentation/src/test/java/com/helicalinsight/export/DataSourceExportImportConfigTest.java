package com.helicalinsight.export;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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
import com.helicalinsight.datasource.model.DSExtraOption;
import com.helicalinsight.datasource.service.GlobalConnectionService;
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
public class DataSourceExportImportConfigTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	MockMvc dsMock;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private GlobalConnectionService service;

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
	private static String TESTURL = "";
	static String fileName = "";
	
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
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");

		} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("\\\\", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "hi","db","SampleTravelData");
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");

		}
	}

	
	@Test
	public void a0_createDataSourceConnection() throws Exception {
		String config = "{\"name\":\"users\",\"url\":\"https://dummyjson.com/users\",\"strategy\":\"in-memory\",\"persistentLocation\":\"\",\"authType\":\"\",\"headers\":{\"Authorization\":\"\"},\"queryParams\":{},\"params\":[],\"postBody\":{},\"dataPath\":\"users\",\"method\":\"\",\"requireTail\":false,\"username\":\"\",\"password\":\"\",\"timeout\":1000}";
		String formData = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""
				+ dbName + "\",\"jdbcUrl\":\"" + jdbcUrl + "\",\"extraOptions\":{\"config\":" + config
				+ ",\"script\":\"\"}}";
		String responseString = testUtility.createDatasource(formData);
		JSONObject resultJson = JSONObject.fromObject(responseString);
		JSONObject response = resultJson.getJSONObject("response");
		dataSourceId = response.getString("dataSourceId");
	}

	@Test
	public void a1_createFolder() throws Exception {
		testUtility.createFolder("DataSourceExportConfigTest");
	

	}
	
	@Test
	public void a2_createMetadata() throws Exception{
        String formData = "{\"id\":\""+ dataSourceId +"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
        testUtility.expand(formData);
        String formData1 = "{\"id\":\""+ dataSourceId +"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
        testUtility.expand(formData1);
        String formData2 ="{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+dataSourceId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"cv7i3\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelData\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"ed031d3decbf73c096c71a81e8e828b1\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"SaveSelectAll\",\"location\":\"DataSourceImportExportConfigTest\",\"metadataReload\":true}";
        //testUtility.createMetadata(formData2);
	}
	
	@Test
	public void a3_exportFolder() throws Exception{
		String request = "{\"dir\": \"DataSourceExportConfigTest\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": false,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL );
	}
	
	@Test
	public void a4_delete_folder() throws Exception {
		testUtility.deleteResource("DataSourceExportConfigTest");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void a5_import_folder() throws Exception {
		String formData = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"security\":true,\"dataSource\": true,\"schedules\" : true}}";
		testUtility.importResource(formData, TESTURL, fileName);
		List<DSExtraOption> list = service.getExtraOptions(Integer.valueOf(dataSourceId));
		assertTrue(!(list.isEmpty()));
	}
	
	
}