package com.helicalinsight.metadata;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.FileSystemOperationsController;
import com.helicalinsight.admin.utils.JacksonUtility;
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

public class MultiConnectionSecurityTest {


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
	private static String secondJdbcId = "";
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
    public void md_a1_createFolder() throws Exception {
    	testUtility.createFolder("MultiConSecurityTest");
    }
    
    @Test
    public void md_a2_create_second_ds() throws Exception {
    	String derby = "{\"classifier\":\"global\",\"dataSourceProvider\":\"tomcat\",\"driverName\":\"org.apache.derby.jdbc.AutoloadedDriver\",\"name\":\"SampleTravelData\",\"userName\":\"\",\"password\":\"\",\"database\":\""+dbName+"\",\"jdbcUrl\":\""+jdbcUrl+"\"}";
    	String dsResponse = testUtility.createDatasource(derby);
    	JSONObject responseObject = JSONObject.fromObject(dsResponse).getJSONObject("response");
    	secondJdbcId = responseObject.getString("dataSourceId");
    }
    
    @Test
    public void md_a3_createCache() throws Exception {
    	String defaultCatalog = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(defaultCatalog);
    	String defaultTable = "{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(defaultTable);
    	String secondDbCatalog = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchCatalogs\":true,\"fetchSchemas\":true,\"view\":\"tree\",\"skipped\":true}}";
    	testUtility.expand(secondDbCatalog);
    	String derbyTable = "{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"parameters\":{\"fetchTables\":true,\"fetchData\":[{\"schemas\":[{\"name\":\"HIUSER\"}]}]}}";
    	testUtility.expand(derbyTable);
    }
    
    static String firstExpressionId = "";
    
    @Test
    public void md_a4_saveSecurity_first_con() throws Exception {
    	
    	String validate = "{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_dimdate_1\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4ac5d9f68b58bd7c0d179146e46795be_z0fr5\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
    	testUtility.validateSecurity(validate);
    	String response = testUtility.saveSecurity(saveSecurity);
    	ObjectNode responseObj = JacksonUtility.fromObject(response);
    	firstExpressionId = responseObj.with("response").get("expressionId").asText();
    	
    }
    
    static String secondExpressionId = "";
    @Test
    public void md_a5_saveSecurity_second_con() throws Exception {
    	
    	String validate = "{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_employee_details\",\"expressionType\":\"global\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\"4e1fd245f4d13b77be423a43f01d80b2_bc78f\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
    	testUtility.validateSecurity(validate);
    	String response = testUtility.saveSecurity(saveSecurity);
    	ObjectNode responseObj = JacksonUtility.fromObject(response);
    	secondExpressionId = responseObj.with("response").get("expressionId").asText();
    }
    
    static String thirdExpressionId = "";
    static String columnId = "";
    @Test
    public void md_a6_save_column_security() throws Exception {
    	
		String fetchColumns = "{\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catSchemaPredicted\":false,\"sync\":false,\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"classifier\":\"db.workflow\",\"datasourceName\":\"SampleTravelDataDerby\",\"dsKeyPath\":\"4zmw-fiy8-gcnp-1n61-sy/7w83-wx45-z373-byeu-se/8ebt-lw3r-54qm-i5g5-sc\",\"driverType\":\"Derby\",\"database\":\"HIUSER\"},\"classifier\":\"db.workflow\",\"metadata\":{\"catalog\":\"\",\"schema\":\"HIUSER\",\"table\":\"travel_details\"},\"refresh\":true}";
		String columns = testUtility.fetchColumns(fetchColumns);
		ObjectNode columnsNode = JacksonUtility.fromObject(columns).with("response").with("metadata").with("table").with("travel_details")
				.with("columns");
		columnId = columnsNode.with("destination_id").get("id").asText();
		String expression = columnId+"_z0fr5";
		String validate = "{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
		String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_traveldetails_destination_id\",\"expressionType\":\"column\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+expression+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
		testUtility.validateSecurity(validate);
		String response = testUtility.saveSecurity(saveSecurity);
		ObjectNode responseObj = JacksonUtility.fromObject(response);
		thirdExpressionId = responseObj.with("response").get("expressionId").asText();
    }
    
    static JsonArray noChangeJoins;
    @Test
	public void md_a7_createMetadata() throws Exception {
    	
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+secondExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+thirdExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[],\"connections\":[\"bc78f\"]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"access\":{\"expression\":[{\"expressionId\":\"dab8c53a-8a83-431a-a98a-59a51ef2fe72\",\"action\":\"add\"},{\"expressionId\":\"a6b580cc-c9e5-4268-a95b-acecb5ae2671\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bc78f\",\"dbId\":\"bc78f\",\"datasourceName\":\"Derby2\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[\"4ac5d9f68b58bd7c0d179146e46795be\",\"4e1fd245f4d13b77be423a43f01d80b2\",\"be534112989b616b194bc59c2fb25a42\",\"9645c648a1c0dbeec1287aaf1e996db3\",\"8a28627d07d04ef096d9935f12e0c7e9\"],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true}],\"fileName\":\"SecurityTest\",\"location\":\"MultiConSecurityTest\",\"metadataReload\":true}";
    	String mdResponse = testUtility.createMetadata(metadata);
    	ObjectNode mdResponseNode = JacksonUtility.fromObject(mdResponse).with("response");
    	JsonNode dimdateNode =  mdResponseNode.with("metadata").with("tables").get("dimdate");
    	Assert.assertNotNull(dimdateNode);
    	ObjectNode employeeDetails = (ObjectNode) mdResponseNode.with("metadata").withArray("connections").get(0).with("tables").get("employee_details");
    	Assert.assertNotNull(employeeDetails);
    	ObjectNode destination_id =  mdResponseNode.with("metadata").with("tables").with("travel_details").with("columns");
    	Assert.assertTrue(destination_id.has("destination_id"));
    	testUtility.getSecurity("{\"metadataFileName\":\"SecurityTest.metadata\",\"location\":\"MultiConSecurityTest\"}");
    }
    
    static ObjectNode outerTables;
    static ObjectNode innerTables;
    static JSONArray tableIds;
    
    @Test
   	public void md_a8_secondSave() throws Exception {
       	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+secondExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+thirdExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bc78f\",\"dbId\":\"bc78f\",\"datasourceName\":\"Derby2\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true}],\"fileName\":\"SecurityTest\",\"location\":\"MultiConSecurityTest\",\"uuid\":\"SecurityTest.metadata\",\"metadataReload\":false,\"uniqueId\":true}";
       	String mdResponse = testUtility.createMetadata(metadata);
       	ObjectNode mdResponseNode = JacksonUtility.fromObject(mdResponse).with("response");
       	JsonNode dimdateNode =  mdResponseNode.with("metadata").with("tables").get("dimdate");
       	outerTables = mdResponseNode.with("metadata").with("tables");
       	ArrayNode arrNode = mdResponseNode.with("metadata").withArray("joins");
       	noChangeJoins = JsonParser.parseString(arrNode.toString()).getAsJsonArray();
       	Assert.assertNotNull(dimdateNode);
       	ObjectNode employeeDetails = (ObjectNode) mdResponseNode.with("metadata").withArray("connections").get(0).with("tables").get("employee_details");
       	innerTables = mdResponseNode.with("metadata").withArray("connections").get(0).with("tables");
       	Assert.assertNotNull(employeeDetails);
       	ObjectNode destination_id =  mdResponseNode.with("metadata").with("tables").with("travel_details").with("columns");
       	Assert.assertTrue(destination_id.has("destination_id"));
       	testUtility.getSecurity("{\"metadataFileName\":\"SecurityTest.metadata\",\"location\":\"MultiConSecurityTest\"}");
       }
    
    
    
    @Test
   	public void md_a9_1_saveAsMetadata() throws Exception {
       	JsonArray innerTableIds = testUtility.prepareTableIds(innerTables.toString());
       	JsonArray outerTableIds = testUtility.prepareTableIds(outerTables.toString());
       	JsonArray joins = testUtility.prepareNoChangeJoins(noChangeJoins);
       	String metadata = "{\"database\":\"\",\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":"+joins+",\"dataSource\":{\"id\":\"1\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"8\",\"datasourceName\":\"SampleTravelData\",\"connId\":\"8\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":"+outerTableIds+",\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"connections\":[{\"classifier\":\"db.generic\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"dbId\":\"9\",\"datasourceName\":\"SampleTravelDataDerby\",\"connId\":\"9\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":"+innerTableIds+",\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]}}],\"access\":{\"expression\":[]},\"fileName\":\"SecurityTest_saveas\",\"location\":\"MultiConSecurityTest\",\"metadataReload\":false,\"uuid\":\"SecurityTest.metadata\",\"newLocation\":\"MultiConSecurityTest\"}";
        testUtility.createMetadata(metadata);
       	testUtility.getSecurity("{\"metadataFileName\":\"SecurityTest_saveas.metadata\",\"location\":\"MultiConSecurityTest\"}");
    }    
    static String employee_details_id = "";
    
    @Test
   	public void md_a9_update_security() throws Exception {
    	
    	String getSecurity = "{\"metadataFileName\":\"SecurityTest.metadata\",\"location\":\"MultiConSecurityTest\"}";
    	String getSecurityResponse = testUtility.getSecurity(getSecurity);
    	ObjectNode securityResponseNode = JacksonUtility.fromObject(getSecurityResponse);
    	String onId = securityResponseNode.with("response").withArray("expressions").get(0).withArray("on").get(0).asText();
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"show_dimdate\",\"expressionType\":\"global\",\"accessType\":\"grant\",\"executionType\":\"conditionIf\",\"on\":[\""+onId+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"filter\":\"TableName.ColumnName = Filter Condition\",\"action\":\"edit\",\"expressionId\":\""+firstExpressionId+"\"}]}";
    	testUtility.saveSecurity(saveSecurity);
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"edit\"},{\"expressionId\":\""+secondExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+thirdExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bc78f\",\"dbId\":\"bc78f\",\"datasourceName\":\"Derby2\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true}],\"fileName\":\"SecurityTest\",\"location\":\"MultiConSecurityTest\",\"uuid\":\"SecurityTest.metadata\",\"metadataReload\":false,\"uniqueId\":true}";
       	String mdResponse = testUtility.createMetadata(metadata);
       	ObjectNode mdResponseNode = JacksonUtility.fromObject(mdResponse).with("response");
       	JsonNode dimdateNode =  mdResponseNode.with("metadata").with("tables").get("dimdate");
       	employee_details_id =  mdResponseNode.with("metadata").with("tables").get("employee_details").get("id").asText();
       	Assert.assertNotNull(dimdateNode);
       	ObjectNode employeeDetails = (ObjectNode) mdResponseNode.with("metadata").withArray("connections").get(0).with("tables").get("employee_details");
       	Assert.assertNotNull(employeeDetails);
       	ObjectNode destination_id =  mdResponseNode.with("metadata").with("tables").with("travel_details").with("columns");
       	Assert.assertTrue(destination_id.has("destination_id"));
       	testUtility.getSecurity("{\"metadataFileName\":\"SecurityTest.metadata\",\"location\":\"MultiConSecurityTest\"}");
    	
      }
    
    static String fourthExpressionId = "";
    @Test
    public void md_b1_add_security_in_edit_mode() throws Exception {
    	
    	String validate = "{\"executionType\":\"conditionIf\",\"data\":{\"condition\":\"${user}.name eq 'hiadmin'\"}}";
    	String saveSecurity = "{\"uuid\":true,\"expression\":[{\"expressionName\":\"hide_employee_details_1\",\"expressionType\":\"table\",\"accessType\":\"deny\",\"executionType\":\"conditionIf\",\"on\":[\""+employee_details_id+"\"],\"condition\":\"${user}.name eq 'hiadmin'\",\"action\":\"add\"}]}";
    	testUtility.validateSecurity(validate);
    	String response = testUtility.saveSecurity(saveSecurity);
    	ObjectNode responseObj = JacksonUtility.fromObject(response);
    	fourthExpressionId = responseObj.with("response").get("expressionId").asText();
    	
    }
    
    
    @Test
   	public void md_b2_add_new_security_saveMd() throws Exception {
    	
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+secondExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+thirdExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+fourthExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bc78f\",\"dbId\":\"bc78f\",\"datasourceName\":\"Derby2\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true}],\"fileName\":\"SecurityTest\",\"location\":\"MultiConSecurityTest\",\"uuid\":\"SecurityTest.metadata\",\"metadataReload\":false,\"uniqueId\":true}";
       	String mdResponse = testUtility.createMetadata(metadata);
       	ObjectNode mdResponseNode = JacksonUtility.fromObject(mdResponse).with("response");
       	JsonNode employee_details =  mdResponseNode.with("metadata").with("tables").get("employee_details");
       	Assert.assertNotNull(employee_details);
       	JsonNode dimdateNode =  mdResponseNode.with("metadata").with("tables").get("dimdate");
       	Assert.assertNotNull(dimdateNode);
       	ObjectNode employeeDetails = (ObjectNode) mdResponseNode.with("metadata").withArray("connections").get(0).with("tables").get("employee_details");
       	Assert.assertNotNull(employeeDetails);
       	ObjectNode destination_id =  mdResponseNode.with("metadata").with("tables").with("travel_details").with("columns");
       	Assert.assertTrue(destination_id.has("destination_id"));
 
    	
      }
    
    @Test
   	public void md_b3_delete_security() throws Exception {
    	String metadata = "{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"joins\":[],\"access\":{\"expression\":[{\"expressionId\":\""+firstExpressionId+"\",\"action\":\"delete\"},{\"expressionId\":\""+secondExpressionId+"\",\"action\":\"add\"},{\"expressionId\":\""+thirdExpressionId+"\",\"action\":\"add\"}]},\"dataSource\":{\"id\":\"1\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"z0fr5\",\"dbId\":\"z0fr5\",\"datasourceName\":\"SampleTravelDataDerby\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[],\"connections\":[]},\"changeItem\":{\"tables\":[],\"columns\":[],\"connections\":[]},\"uniqueId\":true,\"connections\":[{\"database\":\"HIUSER\",\"classifier\":\"db.workflow\",\"duplicate\":{\"table\":[],\"column\":[]},\"dataSource\":{\"id\":\""+secondJdbcId+"\",\"type\":\"dynamicDataSource\",\"baseType\":\"global.jdbc\",\"catalog\":\"\",\"schema\":\"HIUSER\",\"connId\":\"bc78f\",\"dbId\":\"bc78f\",\"datasourceName\":\"Derby2\",\"database\":\"HIUSER\",\"databaseType\":\"Derby\"},\"removeItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"addItem\":{\"tables\":[],\"columns\":[],\"views\":[]},\"changeItem\":{\"tables\":[],\"columns\":[]},\"uniqueId\":true}],\"fileName\":\"SecurityTest\",\"location\":\"MultiConSecurityTest\",\"uuid\":\"SecurityTest.metadata\",\"metadataReload\":false,\"uniqueId\":true}";
       	String mdResponse = testUtility.createMetadata(metadata);
       	ObjectNode mdResponseNode = JacksonUtility.fromObject(mdResponse).with("response");
       	JsonNode dimdateNode =  mdResponseNode.with("metadata").with("tables").get("dimdate");
       	Assert.assertNotNull(dimdateNode);
       	ObjectNode employeeDetails = (ObjectNode) mdResponseNode.with("metadata").withArray("connections").get(0).with("tables").get("employee_details");
       	Assert.assertNotNull(employeeDetails);
       	ObjectNode destination_id =  mdResponseNode.with("metadata").with("tables").with("travel_details").with("columns");
       	Assert.assertTrue(destination_id.has("destination_id"));
       	
       	String getSecurity = "{\"metadataFileName\":\"SecurityTest.metadata\",\"location\":\"MultiConSecurityTest\"}";
    	String getSecurityResponse = testUtility.getSecurity(getSecurity);
    	ObjectNode securityResponseNode = JacksonUtility.fromObject(getSecurityResponse);
    	Assert.assertEquals(3,securityResponseNode.with("response").withArray("expressions").size());
      }
    

    
}
    

