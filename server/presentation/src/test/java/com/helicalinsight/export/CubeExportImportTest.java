/*
package com.helicalinsight.export;

import java.io.File;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CubeExportImportTest {
	
	MockMvc efwMock;
	MockMvc mockMvc;
	@Autowired
	FilterChainProxy filterChainProxy;
	
	@Autowired
	IntegrationTestUtility testUtility;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
	
	@Autowired
	private HIResourceServiceDB resourceServiceDb;

	@Autowired
	private HIMetadataResourceServiceDB mdServiceDb;

	
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
		ServletContext servletContext = context.getServletContext();
		servletContext.setAttribute("filterStatus", "ok");
		this.efwMock = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
	}

	@Autowired
	private FileSystemOperationsController fileSystemOperationsController;

	private static String TESTURL = "";
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			TESTURL = String.join(File.separator, "/home", "helical", "Performance", "HITest");
			
		} else if (os.toLowerCase().contains("windows")) {
			TESTURL = String.join(File.separator, "C:", "home", "helical", "Performance", "HITest");
		}
	}
	
	
	@Test
	public void exp_a1_init() throws Exception {
		testUtility.createFolder("CubeExportImportTest");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}");
		testUtility.expand(
				"{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}");

	}
	
    static String dimdateId = "";
    static String created_date_id = "";
    static String date_key_id = "";
	
	@Test
	public void exp_a2_createMetadata() throws Exception {
		String response = testUtility.createMetadata("{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"schema\":\"HIUSER\",\"catSchemaPredicted\":false,\"connId\":\"HywgRinHc\",\"dbId\":\"HywgRinHc\",\"baseType\":\"global.jdbc\",\"changed\":false,\"databaseType\":\"Derby\",\"catalog\":\"\",\"type\":\"dynamicDataSource\",\"id\":\"1\",\"database\":\"HIUSER\",\"sync\":false},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"4ac5d9f68b58bd7c0d179146e46795be\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"metadataReload\":true,\"location\":\"CubeExportImportTest\",\"fileName\":\"MetadataForCube\",\"uniqueId\":true}");
		JSONObject dimdate = JSONObject.fromObject(response).getJSONObject("response")
				.getJSONObject("metadata")
				.getJSONObject("tables")
				.getJSONObject("dimdate");
		dimdateId = dimdate.getString("id");
		created_date_id = dimdate.getJSONObject("columns").getJSONObject("created_date").getString("id");
		date_key_id = dimdate.getJSONObject("columns").getJSONObject("date_key").getString("id");
	}
	
	@Test
	public void exp_a3_create_cube() throws Exception {
		String formData = "{\"dataSource\":{\"sync\":false,\"id\":\"1\",\"catSchemaPredicted\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"13308\"},\"fileName\":\"cube-1\",\"location\":\"CubeExportImportTest\",\"cubes\":[{\"setCache\":true,\"caption\":\"Cube Creation\",\"defaultMeasure\":\"defaultMeasureName\",\"description\":\"First Cube Creation\",\"enabled\":true,\"cubeName\":\"cube-1\",\"annotation\":\"To be discussed\",\"dimensions\":[{\"dimensionName\":\"created_date\",\"dimensionType\":{\"java.lang.String\":\"text\"},\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"tableId\":\""+dimdateId+"\",\"columnName\":\"created_date\",\"columnId\":\""+created_date_id+"\",\"hierarchies\":[{\"hierarchyName\":\"created_date\",\"visible\":true,\"primaryColumnId\":\""+created_date_id+"\",\"tableId\":\""+dimdateId+"\",\"hierarchyType\":{\"java.lang.String\":\"text\"},\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_date\",\"levels\":[{\"levelName\":\"created_date\",\"columnId\":\""+created_date_id+"\",\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"created_date\",\"levelType\":{\"java.lang.String\":\"text\"},\"tableId\":\""+dimdateId+"\"}]}]},{\"dimensionName\":\"date_key\",\"dimensionType\":{\"java.lang.String\":\"text\"},\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"tableId\":\""+dimdateId+"\",\"columnName\":\"date_key\",\"columnId\":\""+date_key_id+"\",\"hierarchies\":[{\"hierarchyName\":\"date_key\",\"visible\":true,\"primaryColumnId\":\""+date_key_id+"\",\"tableId\":\""+dimdateId+"\",\"hierarchyType\":{\"java.lang.String\":\"text\"},\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"date_key\",\"levels\":[{\"levelName\":\"date_key\",\"columnId\":\""+date_key_id+"\",\"visible\":true,\"table\":{\"name\":\"dimdate\",\"alias\":\"dimdate\",\"databaseName\":\"HIUSER\",\"catalog\":\"\",\"schema\":\"HIUSER\"},\"column\":{\"defaultFunction\":\"db.generic.groupBy.group\"},\"columnName\":\"date_key\",\"levelType\":{\"java.lang.String\":\"text\"},\"tableId\":\""+dimdateId+"\"}]}]}],\"measures\":[]}],\"metadata\":{\"location\":\"CubeExportImportTest\",\"metadataFileName\":\"MetadataForCube.metadata\"},\"uniqueId\":true}";
		testUtility.createCube(formData);
//		testUtility.createCube(formData);
	}
	
	@Test
	public void exp_a4_exportAFolderHavingCube() throws Exception {
		String request = "{\"dir\": \"CubeExportImportTest\",\"file\" : \"\",\"options\": {\"share\": true,\"dataSource\": true,\"schedules\": true}}";
		fileName = testUtility.exportResource(request, TESTURL);
	}
	
	@Test
	public void exp_a5_cleanup() throws Exception {
		testUtility.deleteResource("CubeExportImportTest");
		testUtility.clearRecycleBin();
	}
	
	@Test
	public void exp_a6_importFolderHavingCube() throws Exception {
		String request = "{\"onConflict\" : \"update\",\"upload\":true,\"options\" :{\"share\" : true,\"dataSource\": true,\"schedules\" : true}}";
		String resp = testUtility.importResource(request, TESTURL, fileName);
		JSONObject response = JSONObject.fromObject(resp).getJSONObject("response");
		Assert.assertEquals(3, response.getInt("insertCount"));
		Assert.assertEquals(0, response.getInt("updateCount"));
		Assert.assertEquals(0, response.getInt("skipCount"));
	}

}*/
