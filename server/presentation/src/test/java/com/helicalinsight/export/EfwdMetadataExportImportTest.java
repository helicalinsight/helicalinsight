package com.helicalinsight.export;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwdMetadataExportImportTest {

	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;

	@Autowired
	HIResourceServiceDB serviceDb;
	@Autowired
	EFWDConnectionService connectionService;
	
	@Autowired
	private IntegrationTestUtility testUtility;
	
	static String fileName = "";

	@Bean
	public FileSystemOperationsController fileSystemOperationsController() {
		return new FileSystemOperationsController();
	}

	@Before
	@Transactional
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy)
				.build();
		ServletContext servletContext =  context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String TESTURL = "";
	private static String jdbcUrl = "";
	private static String dbName = "";
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:" + String.join("/", "/home", "helical", "Performance", "hi","db","SampleTravelData");

		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join("\\\\", "C:", "home", "helical", "Performance", "HITest");
			dbName = String.join("\\\\", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:" + String.join("/", "C:", "home", "helical", "Performance", "hi","db", "SampleTravelData");
		}
	}

	@Test
	public void exp_a1_create_a_folder() throws Exception {
		testUtility.createFolder("EfwdMetadataExportImportTest");
	}
	
	static String plainJdbcId = "";
	static String groovyJdbcId = "";
	
	@Test
	public void exp_a2_createPlainJdbcConnection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"dataSourceType\": \"Plain Jdbc DataSource\",\"name\":\"PlainJDBC\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"userName\":\"root\",\"password\":\"root\",\"jdbcUrl\":\""+jdbcUrl+"\",\"database\":\""+dbName+"\",\"directory\":\"EfwdMetadataExportImportTest\",\"type\":\"sql.jdbc\"}";
		String response = testUtility.createPlainDatasource(formData);
		JsonObject responseNode = GsonUtility.parseString(response, JsonObject.class);
		plainJdbcId = GsonUtility.getByPath(responseNode, "response.dataSourceId").getAsString();
	}
	
	@Test
	public void exp_a3_createGroovyManagedConnection() throws Exception {
		String formData = "{\"classifier\":\"efwd\",\"condition\":\"import groovy.sql.Sql;\\n      import net.sf.json.JSONObject;\\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\\n      public JSONObject evalCondition() {\\n        JSONObject responseJson = new JSONObject();\\n        String userName = GroovyUsersSession.getValue('${user}.name');\\n        userName = userName.replaceAll(\\\"'\\\", \\\"\\\");\\n        if (userName.equals(\\\"hiadmin\\\")) {\\n          responseJson.put(\\\"globalId\\\", 1);\\n        }\\n      \\n        if (userName.equals(\\\"hiuser\\\")) {\\n          responseJson.put(\\\"globalId\\\", 3);\\n        }\\n      \\n        if (userName.equals(\\\"test\\\")) {\\n          responseJson.put(\\\"globalId\\\", 4);\\n        }\\n      \\n        responseJson.put(\\\"type\\\", \\\"global.jdbc\\\");\\n      \\n        //throw new RuntimeException(\\\"This is a test exception\\\" +responseJson);\\n        return responseJson;\\n      }\",\"dataSourceType\":\"Groovy Managed Jdbc DataSource\",\"name\":\"GroovyManagedDatasource\",\"type\":\"sql.jdbc.groovy.managed\",\"directory\":\"EfwdMetadataExportImportTest\"}";
		String response = testUtility.createPlainDatasource(formData);
		JsonObject responseNode = GsonUtility.parseString(response, JsonObject.class);
		groovyJdbcId = GsonUtility.getByPath(responseNode, "response.dataSourceId").getAsString();
	}
	
	@Test
	public void exp_a4_plain_metadata() throws Exception {
		String catalogSchema =  "{\"id\":\"" + plainJdbcId + "\",\"type\":\"sql.jdbc\",\"dir\":\"EfwdMetadataExportImportTest\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(catalogSchema);
		String table = "{\"id\":\"" + plainJdbcId + "\",\"type\":\"sql.jdbc\",\"dir\":\"EfwdMetadataExportImportTest\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(table);
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+plainJdbcId+"\",\"type\":\"sql.jdbc\",\"baseType\":\"sql.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"EfwdMetadataExportImportTest\",\"connId\":\"yfj7o\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"PlainJdbcMetadata\",\"location\":\"EfwdMetadataExportImportTest\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
	}
	
	@Test
	public void exp_a5_groovy_metadata() throws Exception {
		String catalogSchema =  "{\"id\":\"" + groovyJdbcId + "\",\"type\":\"sql.jdbc.groovy.managed\",\"dir\":\"EfwdMetadataExportImportTest\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
		testUtility.expand(catalogSchema);
		String table = "{\"id\":\"" + groovyJdbcId + "\",\"type\":\"sql.jdbc.groovy.managed\",\"dir\":\"EfwdMetadataExportImportTest\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
		testUtility.expand(table);
		String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\""+groovyJdbcId+"\",\"type\":\"sql.jdbc.groovy.managed\",\"baseType\":\"sql.jdbc.groovy.managed\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"dir\":\"EfwdMetadataExportImportTest\",\"connId\":\"yfj7o\",\"classifier\":\"db.workflow\",\"database\":\"HIUSER\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"fileName\":\"GroovyJdbcMetadata\",\"location\":\"EfwdMetadataExportImportTest\",\"metadataReload\":true}";
		testUtility.createMetadata(metadata);
	}
	
	@Test
	public void exp_a6_exportFolder() throws Exception {
		String request = "{\"dir\": \"EfwdMetadataExportImportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"users\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL );
		assertTrue(StringUtils.isNotBlank(fileName));
	}
	
	@Test
	public void exp_a7_cleanResources() throws Exception {
		String plainDelete = "{\"driver\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"id\":\""+plainJdbcId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"EfwdMetadataExportImportTest\"}";
		String groovyDelete = "{\"id\":\""+plainJdbcId+"\",\"type\":\"simple\",\"classifier\":\"efwd\",\"directory\":\"EfwdMetadataExportImportTest\"}";
		testUtility.deleteDatasource(plainDelete);
		testUtility.deleteDatasource(groovyDelete);
		testUtility.deleteResource("EfwdMetadataExportImportTest");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_a8_import() throws Exception {
		String formData = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"security\":true,\"dataSource\": true,\"schedules\" : true}}";
		String response = testUtility.importResource(formData, TESTURL, fileName);
		JsonObject responseNode = GsonUtility.parseString(response, JsonObject.class);
		int insertCount = GsonUtility.getByPath(responseNode, "response.insertCount").getAsInt();
		assertEquals(3, insertCount);
		JsonArray inserts =  GsonUtility.getByPath(responseNode, "response.inserts").getAsJsonArray();
		Set<String> expectedResources = Set.of("EfwdMetadataExportImportTest","EfwdMetadataExportImportTest/PlainJdbcMetadata.metadata","EfwdMetadataExportImportTest/GroovyJdbcMetadata.metadata");
		for(JsonElement element : inserts) {
			String resource = element.getAsString();
			assertTrue(expectedResources.contains(resource));
		}
	}
	
}
