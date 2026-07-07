package com.helicalinsight.metadata;

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
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
		"classpath:spring-security.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataOriginalColumnDeleteTest {

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
	
	
	static String dimDateId = "";
	static String dimId = "";
	static String dbId = "";
	
	@Test
	public void md_a1_createMetadata() throws Exception {
    	
    	testUtility.createFolder("MetadataOriginalColumnDeleteTest");
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"1x2n6\",\"dbId\":\"1x2n6\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataOriginalColumnDeleteTest\",\"metadataReload\":true}";
    	String response = testUtility.createMetadata(metadata);
    	
    	JSONObject metadataObj = JSONObject.fromObject(response)
    			.getJSONObject("response")
				.getJSONObject("metadata");
    			
    	
    	JSONObject dimdate = metadataObj
    				.getJSONObject("tables")
    				.getJSONObject("dimdate");
    	dimDateId = dimdate.getString("id");
    	dimId = dimdate.getJSONObject("columns").getJSONObject("dim_id").getString("id");
    	dbId = metadataObj.getJSONObject("dataSource").getString("dbId");
	}
    
    @Test
    public void md_a2_duplicate_column_saveas_edit_mode() throws Exception {

    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"id\":\"7qwp-qibb-n4e0-kndt-f6\",\"originalId\":\""+dimId+"\",\"connId\":\"1\",\"tableId\":\""+dimDateId+"\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"connId\":\""+dbId+"\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[\""+dimId+"\"],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_1\",\"location\":\"MetadataOriginalColumnDeleteTest\",\"metadataReload\":false,\"uuid\":\"Metadata_1.metadata\",\"uniqueId\":true}";
    	String response = testUtility.createMetadata(metadata);
    	JSONObject jsonMd = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("metadata");
    	JSONObject dimdate = jsonMd.getJSONObject("tables").getJSONObject("dimdate");
    	JSONObject columns = dimdate.getJSONObject("columns");
    	Assert.assertFalse(columns.containsKey("dim_id"));
    	Assert.assertTrue(columns.containsKey("dim_id_1"));
    }
    
    
	
	
}