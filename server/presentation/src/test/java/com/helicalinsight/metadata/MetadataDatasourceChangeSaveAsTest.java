package com.helicalinsight.metadata;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDatasourceChangeSaveAsTest {


    MockMvc efwMock;
    MockMvc mockMvc;
    
    @Autowired
    private FilterChainProxy filterChainProxy;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ResourceAuthenticationAndAuthorizationFilter authenticationAndAuthorizationFilter;
    
    @Autowired
    private IntegrationTestUtility testUtility;


    @Bean
    public FileSystemOperationsController fileSystemOperationsController() {
        return new FileSystemOperationsController();
    }
    
    @Before
    @Transactional
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.fileSystemOperationsController).addFilters(filterChainProxy).build();
        ServletContext servletContext = context.getServletContext();
        servletContext.setAttribute("filterStatus", "ok");
        this.efwMock = MockMvcBuilders.webAppContextSetup(this.context).addFilters(filterChainProxy, authenticationAndAuthorizationFilter).build();
    }
    private static String dbName = "";
	private static String jdbcUrl = "";
	private static String sqliteDbName = "";
	private static String sqliteJdbcUrl = "";
	private static String sqliteJdbcId = "";
	private static String dbIdToChange = "";
	private static String secondJdbcId = "";
	private static String thirdJdbcId = "";
	
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "/home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
			
			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			sqliteDbName = String.join("/", "C:", "home", "helical", "Sqlite", "SampleTravelData.db");
			sqliteJdbcUrl = "jdbc:sqlite:" + sqliteDbName;
		}
	}
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
    @Autowired
    private HIResourceServiceDB serviceDb;
    
    @Autowired
    private HIMetadataResourceServiceDB mdServiceDb;
    
    
    
    @Test
    public void md_a0_create_cache() throws Exception {
    	// create folder 
    	testUtility.createFolder("DatasourceChangeSaveAsTest");
    	// create cache
    	// expand catalog schema
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	// expand table
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    	
    	// second ds : postgres 
    	String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"hiuser\",\"password\":\"hiuser\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
    	String dsResponse = testUtility.createDatasource(derby);
    	String dbResponse2 = testUtility.createDatasource(derby);
    	JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
    	secondJdbcId = responseObject.getString("dataSourceId");
    	JSONObject responseObject2 = JSONObject.fromObject(dbResponse2).getJSONObject("response");
    	thirdJdbcId = responseObject2.getString("dataSourceId");
    	// create cache
    	// expand catalog schema
    	String secondDbCatalog = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(secondDbCatalog);
    	// expand table
    	String derbyTable = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(derbyTable);
    	
    	// expand catalog schema
    	String thirdDb = "{\"id\":\""+thirdJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(thirdDb);
    	// expand table
    	String thirdTables = "{\"id\":\""+thirdJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(thirdTables);
    }
    
    
    static String dbId1 ="";
    static String dbId2 = "";
    
    @Test
	public void md_a1_createMetadata_multi_con() throws Exception {
    	
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ntxrj\",\"dbId\":\"ntxrj\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[\"h06ca\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"h06ca\",\"dbId\":\"h06ca\",\"datasourceName\":\"AnotherDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"DatasourceChangeSaveAsTest\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	dbIdToChange = ((JSONObject)mdResponseObject.getJSONObject("metadata").getJSONArray("connections").get(0)).getJSONObject("dataSource").getString("dbId");
    	dbId1 = testUtility.getOuterDbId(mdResponse);
    	dbId2 = dbIdToChange;
    }
    
    
    @Test
    public void md_a2_change_datasource_multi_con()  throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId1+"\",\"datasourceName\":\"DerbyNet\",\"connId\":\""+dbId1+"\",\"oldDbId\":\"222\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[\""+dbId2+"\"]},\"connections\":[{\"database\":\"\",\"classifier\":\"global\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+thirdJdbcId+"\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId2+"\",\"datasourceName\":\"Derby2\",\"connId\":\""+dbId2+"\",\"oldDbId\":\"223\",\"changed\":true,\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"Metadata_2\",\"location\":\"DatasourceChangeSaveAsTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"newLocation\":\"DatasourceChangeSaveAsTest\"}";
		testUtility.createMetadata(formData);
		HIResource firstMdResource =  serviceDb.getResourceByUrl("DatasourceChangeSaveAsTest/Metadata_1.metadata");
		HIResource secondMdResource = serviceDb.getResourceByUrl("DatasourceChangeSaveAsTest/Metadata_2.metadata");
		HIResourceMetadata md1 =  mdServiceDb.giveHIResourceMetadataByResourceId(firstMdResource.getResourceId());
		HIResourceMetadata md2 =  mdServiceDb.giveHIResourceMetadataByResourceId(secondMdResource.getResourceId());
		Assert.assertEquals(2,md2.getHiMetadataConnections().size());
		Assert.assertEquals(2,md1.getHiMetadataConnections().size());
		
    }
    @Test
    public void md_a3_create_metadata_single() throws Exception {
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"ktkvi\",\"dbId\":\"ktkvi\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_3\",\"location\":\"DatasourceChangeSaveAsTest\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject.fromObject(mdResponse).getJSONObject("response");
    	dbId1 = testUtility.getOuterDbId(mdResponse);
    
    }
    
    @Test
    public void md_a4_change_ds_single() throws Exception {
    	String formData = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId1+"\",\"datasourceName\":\"DerbyNet\",\"connId\":\"236\",\"oldDbId\":\"236\",\"database\":\"HIUSER\",\"changed\":true,\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_4\",\"location\":\"DatasourceChangeSaveAsTest\",\"metadataReload\":false,\"uuid\":\"Metadata_3.metadata\",\"newLocation\":\"DatasourceChangeSaveAsTest\"}";
		testUtility.createMetadata(formData);
		HIResource firstMdResource =  serviceDb.getResourceByUrl("DatasourceChangeSaveAsTest/Metadata_3.metadata");
		HIResource secondMdResource = serviceDb.getResourceByUrl("DatasourceChangeSaveAsTest/Metadata_4.metadata");
		HIResourceMetadata md1 =  mdServiceDb.giveHIResourceMetadataByResourceId(firstMdResource.getResourceId());
		HIResourceMetadata md2 =  mdServiceDb.giveHIResourceMetadataByResourceId(secondMdResource.getResourceId());
		Assert.assertEquals(1,md2.getHiMetadataConnections().size());
		Assert.assertEquals(1,md1.getHiMetadataConnections().size());
    }
    
    
    
}