package com.helicalinsight.metadata;

import java.io.File;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;

import org.bson.json.JsonMode;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.adhoc.FileSystemOperationsController;
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

public class MetadataDuplicateTest {


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
			dbName = String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			jdbcUrl = "jdbc:derby:"+ String.join("/", "C:","home", "helical", "Performance", "hi","db","SampleTravelData");
			
		}
	}
	
    @Autowired
    private EfwServicesController efwServicesController;

    @Autowired
    private FileSystemOperationsController fileSystemOperationsController;
    
 	
    
    @Test
	public void md_a1_createMetadata() throws Exception {
    	
    	// create folder 
    	testUtility.createFolder("DuplicateTableColumnTest");
    	// create cache
    	// expand catalog schema
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	// expand table
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    	
    	String columns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"1x2n6\",\"dbId\":\"1x2n6\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"7iu1-yaee-9nek-sorh-3w/st0y-fdsf-n5m3-mwfc-23/sddz-il3i-2epn-mxbu-pe\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"dimdate\"},\"refresh\":true}";
    	String fetchColumns = testUtility.fetchColumns(columns);
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[{\"alias\":\"dimdate_1\",\"columns\":[{\"alias\":\"dim_id\",\"connId\":\"1x2n6\",\"originalId\":\"1a7cfebc-7879-4ccf-b1de-e52bc0b804bb\",\"originalName\":\"dim_id\"},{\"alias\":\"fiscal_year\",\"connId\":\"1x2n6\",\"originalId\":\"d48b469e-88c6-4ec0-8db5-166ba58a55f1\",\"originalName\":\"fiscal_year\"},{\"alias\":\"modified_date\",\"connId\":\"1x2n6\",\"originalId\":\"6ccfaeb4-e0f2-4cf1-a72e-831ad3eca272\",\"originalName\":\"modified_date\"},{\"alias\":\"date_key\",\"connId\":\"1x2n6\",\"originalId\":\"5309c2ac-149e-4472-82a8-91c3b59145a3\",\"originalName\":\"date_key\"},{\"alias\":\"day_number\",\"connId\":\"1x2n6\",\"originalId\":\"21ddc15e-d16a-4727-8fcd-8a7348a15c22\",\"originalName\":\"day_number\"},{\"alias\":\"fiscal_month_name\",\"connId\":\"1x2n6\",\"originalId\":\"ec870ee0-e2e5-415b-8c67-c3d27f5ee400\",\"originalName\":\"fiscal_month_name\"},{\"alias\":\"fiscal_month_label\",\"connId\":\"1x2n6\",\"originalId\":\"037dfe08-faee-471d-ba2a-a8a0be1436d6\",\"originalName\":\"fiscal_month_label\"},{\"alias\":\"created_date\",\"connId\":\"1x2n6\",\"originalId\":\"930b3068-b238-40d6-b5df-bb8998fe309c\",\"originalName\":\"created_date\"},{\"alias\":\"created_time\",\"connId\":\"1x2n6\",\"originalId\":\"d3f27317-7be4-436f-b055-86b83daec3fa\",\"originalName\":\"created_time\"},{\"alias\":\"rating\",\"connId\":\"1x2n6\",\"originalId\":\"eb88d0b1-933c-4d83-8061-ce1d1675f92e\",\"originalName\":\"rating\"},{\"alias\":\"rating_1\",\"connId\":\"1x2n6\",\"originalId\":\"eb88d0b1-933c-4d83-8061-ce1d1675f92e\",\"originalName\":\"rating\",\"name\":\"rating_1\"}],\"connId\":\"1x2n6\",\"name\":\"dimdate_1\",\"originalId\":\"4ac5d9f68b58bd7c0d179146e46795be\",\"originalName\":\"dimdate\"}],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"1x2n6\",\"dbId\":\"1x2n6\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"fileName\":\"Duplicatetablecolumn\",\"location\":\"DuplicateTableColumnTest\",\"metadataReload\":true}";
    	metadata = testUtility.addDuplicateColumnsInFormData(fetchColumns,metadata,"dimdate");
    	String mdResponse = testUtility.createMetadata(metadata);
    	JSONObject mdResponseObject = JSONObject.fromObject(mdResponse).getJSONObject("response");
    	Assert.assertTrue(mdResponseObject.getJSONObject("metadata").getJSONObject("tables").containsKey("dimdate_1"));
    	Assert.assertTrue(mdResponseObject.getJSONObject("metadata").getJSONObject("tables").getJSONObject("dimdate_1").getJSONObject("columns").containsKey("rating_1"));
	}
    
    @Test
    public void md_a2_duplicate_column_saveas_edit_mode() throws Exception {

    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"jsrji\",\"dbId\":\"jsrji\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_2\",\"location\":\"DuplicateTableColumnTest\",\"metadataReload\":true}";
    	String response = testUtility.createMetadata(metadata);
    	JSONObject jsonMd = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("metadata");
    	JSONObject dimdate = jsonMd.getJSONObject("tables").getJSONObject("dimdate");
    	String tableId = dimdate.getString("id");
    	String colOriginalId = dimdate.getJSONObject("columns").getJSONObject("dim_id").getString("id");
    	String dbId = testUtility.getOuterDbId(response);
    	String duplicateColumn = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[{\"alias\":\"dim_id_1\",\"name\":\"dim_id_1\",\"originalName\": \"dim_id_1\",\"id\":\"yak3-4vkt-e2ii-iwqd-cd\",\"originalId\":\""+colOriginalId+"\",\"connId\":\""+dbId+"\",\"tableId\":\""+tableId+"\",\"duplicate\":true}]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\""+dbId+"\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\""+dbId+"\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\""+tableId+"\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_3\",\"location\":\"DuplicateTableColumnTest\",\"metadataReload\":false,\"uuid\":\"Metadata_2.metadata\",\"newLocation\":\"DuplicateTableColumnTest\"}";
    	response = testUtility.createMetadata(duplicateColumn);
    	jsonMd = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("metadata");
    	JSONObject columns = jsonMd.getJSONObject("tables").getJSONObject("dimdate").getJSONObject("columns");
    	Assert.assertTrue(columns.containsKey("dim_id_1"));
    }
    
    @Test
    public void md_a3_dupplicate_table_save_edit_mode() throws Exception {
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"fn0kh\",\"dbId\":\"fn0kh\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_4\",\"location\":\"DuplicateTableColumnTest\",\"metadataReload\":true}";
    	String saveResponse = testUtility.createMetadata(metadata);
    	
    	JSONObject travel_details = JSONObject.fromObject(saveResponse).getJSONObject("response")
    			.getJSONObject("metadata")
    			.getJSONObject("tables")
    			.getJSONObject("travel_details");
    	
    	JSONObject travel_details_columns = travel_details.getJSONObject("columns");
    	String tableId = travel_details.getString("id");
    	
    	String metadata_2 = "{\"database\":\"HIUSER\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[{\"alias\":\"travel_details_1\",\"id\":\"jsd9-nx3m-5ij9-1n7k-no\",\"columns\":[{\"alias\":\"travel_id\",\"connId\":\"2573\",\"originalId\":\"edd48756-98e5-4418-9c20-8acaac1edd0b\",\"name\":\"travel_id\"},{\"alias\":\"travel_date\",\"connId\":\"2573\",\"originalId\":\"2dd41d5b-df0f-4b14-b715-ae5a943d80f0\",\"name\":\"travel_date\"},{\"alias\":\"travel_type\",\"connId\":\"2573\",\"originalId\":\"a42746e2-ec92-4cea-82eb-e0133db9fa34\",\"name\":\"travel_type\"},{\"alias\":\"travel_medium\",\"connId\":\"2573\",\"originalId\":\"bdce37bd-a7c5-48b1-b1b5-a52ce8ee1881\",\"name\":\"travel_medium\"},{\"alias\":\"source_id\",\"connId\":\"2573\",\"originalId\":\"8ba92af7-05a5-49a0-9998-ed7cddbaa943\",\"name\":\"source_id\"},{\"alias\":\"source\",\"connId\":\"2573\",\"originalId\":\"ff8d3433-a3a0-4fbd-b051-0b4c22abfc56\",\"name\":\"source\"},{\"alias\":\"destination_id\",\"connId\":\"2573\",\"originalId\":\"96a10965-7b31-40db-a3c7-86e7a2520e1a\",\"name\":\"destination_id\"},{\"alias\":\"destination\",\"connId\":\"2573\",\"originalId\":\"7899dad4-60c8-4d12-86de-ca38054734e0\",\"name\":\"destination\"},{\"alias\":\"travel_cost\",\"connId\":\"2573\",\"originalId\":\"85af10a7-d710-4383-bf9d-54a9fa7f973a\",\"name\":\"travel_cost\"},{\"alias\":\"mode_of_payment\",\"connId\":\"2573\",\"originalId\":\"936e3598-ad4c-4d9b-93d2-50368be2e135\",\"name\":\"mode_of_payment\"},{\"alias\":\"booking_platform\",\"connId\":\"2573\",\"originalId\":\"8b3a12b8-e0be-4c4d-9415-733884193a99\",\"name\":\"booking_platform\"},{\"alias\":\"travelled_by\",\"connId\":\"2573\",\"originalId\":\"97d2dc57-8dfd-4715-895a-de682559c85d\",\"name\":\"travelled_by\"}],\"connId\":\"2573\",\"originalName\":\"travel_details\",\"originalId\":\""+tableId+"\",\"name\":\"travel_details_1\"}],\"column\":[]},\"joins\":[],\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"2573\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"2573\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"access\":{\"expression\":[]},\"fileName\":\"Metadata_4\",\"location\":\"DuplicateTableColumnTest\",\"metadataReload\":false,\"uuid\":\"Metadata_4.metadata\",\"uniqueId\":true}";
    	JSONObject metadata_2_object = JSONObject.fromObject(metadata_2);
    	JSONArray mdColumnsArray = ((JSONObject)metadata_2_object.getJSONObject("duplicate").getJSONArray("table").get(0)).getJSONArray("columns");
    	
    	HashMap<String, JSONObject> columnMap =  new ObjectMapper().readValue(travel_details_columns.toString(), new TypeReference<HashMap<String, JSONObject>>(){});

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
    	JSONObject tables = JSONObject.fromObject(response).getJSONObject("response").getJSONObject("metadata").getJSONObject("tables");
    	
    	Assert.assertTrue(tables.containsKey("travel_details"));
    	Assert.assertTrue(tables.containsKey("travel_details_1"));
    	
    	JSONObject travel_details_1 =  tables.getJSONObject("travel_details_1");
    	JSONObject columns = travel_details_1.getJSONObject("columns");
    	JSONObject destination_id = columns.getJSONObject("destination_id");
    	Assert.assertFalse(destination_id.containsKey("duplicate"));
    	
    	
    }
    
    
	@Test
 	public void md_a4_delete_folder() throws Exception {
 		testUtility.deleteResource("DuplicateTableColumnTest");
 	}
 	

    
}
    

