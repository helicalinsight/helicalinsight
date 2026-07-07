package com.helicalinsight.metadata;

import java.io.File;
import java.util.HashMap;

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
import com.helicalinsight.resourcesecurity.filter.ResourceAuthenticationAndAuthorizationFilter;
import com.helicalinsight.test.utility.IntegrationTestUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml", "classpath:dispatcher-servlet.xml",
        "classpath:spring-security.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataEditDuplicateTableDuplicateColumnTest {

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
	
	static {
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			dbName = String.join(File.separator, "/home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/","/home", "helical", "Performance", "hi","db","SampleTravelData");
			
			
			} else if (os.toLowerCase().contains("windows")) {
			dbName = String.join(File.separator, "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			
		}
	}
	

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
 	/**
 	 * bugId : 7321
 	 */
    @Test
   	public void md_a1_metadataEditMode_save_duplicatetable_duplicateColumn() throws Exception {
       	
       	// create folder 
       	testUtility.createFolder("EditDuplicateTableColumnTest");
       	// create cache
       	// expand catalog schema
       	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
       	testUtility.expand(defaultCatalog);
       	// expand table
       	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
       	testUtility.expand(defaultTable);
       //create/save metadata
       	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"de9i3\",\"dbId\":\"de9i3\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"EditMetadataColumn\",\"location\":\"EditDuplicateTableColumnTest\",\"metadataReload\":true}";
       	String saveResponse = testUtility.createMetadata(metadata);
       	JSONObject dimdate = JSONObject.fromObject(saveResponse).getJSONObject("response")
    			.getJSONObject("metadata")
    			.getJSONObject("tables")
    			.getJSONObject("dimdate");  
       	
       	JSONObject dimdate_columns = dimdate.getJSONObject("columns");
    	String tableId = dimdate.getString("id");
    	String colOriginalId = dimdate.getJSONObject("columns").getJSONObject("dim_id").getString("id");
    	
    	//Edit Metadata: saving duplicate column in duplicate table
    	String metadata_2 = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"id\":\"aveq-dcys-y4h3-rlvc-49\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"8629\",\"originalId\":\"29522\",\"id\":\"x3gg-d0yh-twmk-eiio-qc\",\"name\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"8629\",\"originalId\":\"29523\",\"id\":\"emf0-pzaw-76iv-23yf-1u\",\"name\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"8629\",\"originalId\":\"29524\",\"id\":\"2vgm-x1t0-opvz-11c2-qb\",\"name\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"8629\",\"originalId\":\"29525\",\"id\":\"5o6z-gwg1-2s7g-rncg-4c\",\"name\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"8629\",\"originalId\":\"29526\",\"id\":\"jzjl-zfe6-fslb-ce6f-gg\",\"name\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"8629\",\"originalId\":\"29527\",\"id\":\"bilg-b793-dtxm-nag0-zk\",\"name\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"8629\",\"originalId\":\"29528\",\"id\":\"xzti-6ty0-kmmf-gc01-1f\",\"name\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"8629\",\"originalId\":\"29529\",\"id\":\"jmyx-jkv5-qh05-zuwf-wz\",\"name\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"8629\",\"originalId\":\"29530\",\"id\":\"bxb7-a7ne-29yf-ybk3-p8\",\"name\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"8629\",\"originalId\":\"29531\",\"id\":\"9gvt-c1ux-z9au-0yq3-e3\",\"name\":\"rating\"},{\"alias\":\"dim_id_1\",\"connId\":\"8629\",\"originalId\":\""+colOriginalId+"\",\"id\":\"i6he-fu3v-ysp8-i4be-yb\",\"name\":\"dim_id_1\"}],\"connId\":\"8629\",\"originalName\":\"dimdate\",\"originalId\":\""+tableId+"\",\"name\":\"dimdate_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"8629\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"connId\":\"8629\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"EditMetadataColumn\",\"location\":\"EditDuplicateTableColumnTest\",\"metadataReload\":false,\"uuid\":\"EditMetadataColumn.metadata\",\"uniqueId\":true}";
    	JSONObject metadata_2_object = JSONObject.fromObject(metadata_2);
    	JSONArray mdColumnsArray = ((JSONObject)metadata_2_object.getJSONObject("duplicate").getJSONArray("table").get(0)).getJSONArray("columns");
    	
    	HashMap<String, JSONObject> columnMap =  new ObjectMapper().readValue(dimdate_columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});

    	for(Object it : mdColumnsArray) {
    		JSONObject eachColumn = (JSONObject) it;
    		JSONObject mapObj =  columnMap.get(eachColumn.getString("alias"));
    		if( mapObj != null) {
    			eachColumn.put("originalId", mapObj.getString("id"));
    		}
    	}
    	((JSONObject)metadata_2_object.getJSONObject("duplicate").getJSONArray("table").get(0)).put("columns", mdColumnsArray);
    	metadata_2 = metadata_2_object.toString();
    	String response = testUtility.createMetadata(metadata_2);
       	JSONObject mdResponseObject = JSONObject.fromObject(response).getJSONObject("response");
    	Assert.assertTrue(mdResponseObject.getJSONObject("metadata").getJSONObject("tables").containsKey("dimdate_1"));
    	Assert.assertTrue(mdResponseObject.getJSONObject("metadata").getJSONObject("tables").containsKey("dimdate"));
    	Assert.assertTrue(mdResponseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate_1").getJSONObject("columns").containsKey("dim_id_1"));
    	String orginalName = mdResponseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate_1").getJSONObject("columns").getJSONObject("dim_id_1").getString("originalName"); 
    	Assert.assertEquals("dim_id", orginalName);;
    }
    @Test
 	public void md_a2_delete_folder() throws Exception {
 		testUtility.deleteResource("EditDuplicateTableColumnTest");
 	}
 	
}
